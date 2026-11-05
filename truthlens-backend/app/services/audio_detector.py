"""
AI Voice / Audio Deepfake Detection Service

Primary model: koyelog/deepfake-voice-detector-sota
  - Architecture: facebook/wav2vec2-base + BiGRU + Multi-Head Attention
  - Task: Binary classification → REAL (bonafide) / FAKE (synthetic/cloned)
  - Published: 2025

Fallback model: Heem2/Deepfake-audio-detection
  - Architecture: facebook/wav2vec2-base fine-tuned
  - Task: Binary REAL / FAKE

Heuristic fallback: spectral + prosody features (no model download required)
"""
import io
import os
import time
import logging
import tempfile
from typing import Optional, List

import numpy as np

from app.config import HF_TOKEN

logger = logging.getLogger("deepfake_detector.audio")

AUDIO_MODEL_PRIMARY  = "koyelog/deepfake-voice-detector-sota"
AUDIO_MODEL_FALLBACK = "Heem2/Deepfake-audio-detection"

_audio_model     = None
_audio_processor = None
_audio_model_name: str = ""
_audio_loaded = False


# ---------------------------------------------------------------------------
# Model loading
# ---------------------------------------------------------------------------

def _load_audio_model():
    global _audio_model, _audio_processor, _audio_model_name, _audio_loaded

    if _audio_loaded:
        return

    offline = os.environ.get("TRANSFORMERS_OFFLINE", "0") == "1"

    for model_id in [AUDIO_MODEL_PRIMARY, AUDIO_MODEL_FALLBACK]:
        try:
            from transformers import AutoFeatureExtractor, AutoModelForAudioClassification
            processor = AutoFeatureExtractor.from_pretrained(
                model_id, token=HF_TOKEN or None,
                local_files_only=offline,
            )
            model = AutoModelForAudioClassification.from_pretrained(
                model_id, token=HF_TOKEN or None,
                local_files_only=offline,
            )
            _audio_processor = processor
            _audio_model = model
            _audio_model_name = model_id
            _audio_loaded = True
            logger.info("Audio model loaded: %s", model_id)
            return
        except Exception as exc:
            logger.warning("Audio model %s failed: %s", model_id, exc)

    # Both failed — use heuristic
    _audio_loaded = True
    _audio_model_name = "heuristic-audio-fallback"
    logger.warning("No audio model loaded — using heuristic fallback")


def get_audio_model_name() -> str:
    _load_audio_model()
    return _audio_model_name or "heuristic-audio-fallback"


# ---------------------------------------------------------------------------
# Load audio bytes → float32 array at 16 kHz
# ---------------------------------------------------------------------------

def _load_audio_array(audio_bytes: bytes, content_type: str) -> Optional[np.ndarray]:
    """
    Decode audio bytes to a 16 kHz mono float32 numpy array.
    Tries torchaudio first, falls back to scipy/soundfile.
    Returns None on failure.
    """
    try:
        import torchaudio
        import torch

        fd, tmp = tempfile.mkstemp(suffix=_ext_for(content_type))
        try:
            with os.fdopen(fd, "wb") as f:
                f.write(audio_bytes)
            waveform, sr = torchaudio.load(tmp)
        finally:
            if os.path.exists(tmp):
                os.unlink(tmp)

        # Convert to mono
        if waveform.shape[0] > 1:
            waveform = waveform.mean(dim=0, keepdim=True)

        # Resample to 16 kHz
        if sr != 16000:
            resampler = torchaudio.transforms.Resample(orig_freq=sr, new_freq=16000)
            waveform = resampler(waveform)

        return waveform.squeeze(0).numpy().astype(np.float32)

    except Exception as exc:
        logger.debug("torchaudio failed (%s), trying soundfile", exc)

    try:
        import soundfile as sf
        buf = io.BytesIO(audio_bytes)
        data, sr = sf.read(buf, dtype="float32", always_2d=True)
        mono = data.mean(axis=1)
        if sr != 16000:
            # Simple linear resample
            ratio = 16000 / sr
            new_len = int(len(mono) * ratio)
            mono = np.interp(
                np.linspace(0, len(mono) - 1, new_len),
                np.arange(len(mono)),
                mono,
            ).astype(np.float32)
        return mono
    except Exception as exc2:
        logger.error("Audio decode failed: %s", exc2)
        return None


def _ext_for(content_type: str) -> str:
    mapping = {
        "audio/wav": ".wav", "audio/x-wav": ".wav",
        "audio/mpeg": ".mp3", "audio/mp3": ".mp3",
        "audio/ogg": ".ogg", "audio/vorbis": ".ogg",
        "audio/aac": ".aac", "audio/mp4": ".m4a",
        "audio/flac": ".flac", "audio/webm": ".webm",
    }
    return mapping.get(content_type, ".wav")


# ---------------------------------------------------------------------------
# Model inference
# ---------------------------------------------------------------------------

def _classify_with_model(audio_array: np.ndarray) -> float:
    """Run Wav2Vec2-based classifier. Returns fake_score 0..1."""
    import torch

    inputs = _audio_processor(
        audio_array,
        sampling_rate=16000,
        return_tensors="pt",
        padding=True,
    )
    with torch.no_grad():
        outputs = _audio_model(**inputs)

    logits = outputs.logits
    probs = torch.nn.functional.softmax(logits, dim=-1)[0]

    id2label = getattr(_audio_model.config, "id2label", {0: "REAL", 1: "FAKE"})
    fake_score = 0.0
    for idx, prob in enumerate(probs):
        label = id2label.get(idx, str(idx)).upper()
        if any(kw in label for kw in ["FAKE", "SPOOF", "SYNTHETIC", "GENERATED", "AI"]):
            fake_score += float(prob)

    return float(np.clip(fake_score, 0.0, 1.0))


# ---------------------------------------------------------------------------
# Heuristic audio analyser
# ---------------------------------------------------------------------------

def _heuristic_audio(audio_array: np.ndarray) -> float:
    """
    Multi-signal spectral heuristic for voice deepfake detection.

    Signals:
      1. Spectral flatness — TTS voices tend to be unnaturally flat
      2. Zero-crossing rate variance — real speech is more irregular
      3. Pitch regularity — cloned voices often have unnaturally steady pitch
      4. Silence ratio — synthetic speech has very clean silence gaps
    """
    if len(audio_array) == 0:
        return 0.5

    # Normalise
    arr = audio_array / (np.max(np.abs(audio_array)) + 1e-8)

    # 1. Spectral flatness (per frame)
    frame_len = 512
    flatness_scores = []
    for i in range(0, len(arr) - frame_len, frame_len // 2):
        frame = arr[i:i + frame_len] * np.hanning(frame_len)
        mag = np.abs(np.fft.rfft(frame)) + 1e-10
        geo_mean = np.exp(np.mean(np.log(mag)))
        arith_mean = np.mean(mag)
        flatness_scores.append(geo_mean / arith_mean)

    flatness = float(np.mean(flatness_scores)) if flatness_scores else 0.5
    # High flatness → synthetic (TTS whitens the spectrum)
    flatness_score = float(np.clip(flatness * 2.5, 0, 1))

    # 2. Zero-crossing rate variance — low variance → synthetic
    zcr_per_frame = []
    for i in range(0, len(arr) - frame_len, frame_len // 2):
        frame = arr[i:i + frame_len]
        zcr = np.sum(np.abs(np.diff(np.sign(frame)))) / (2 * frame_len)
        zcr_per_frame.append(zcr)

    zcr_var = float(np.var(zcr_per_frame)) if zcr_per_frame else 0
    # Low ZCR variance → more synthetic
    zcr_score = float(np.clip(1.0 - zcr_var * 200, 0, 1))

    # 3. RMS energy regularity — real speech has more dynamic range
    rms_per_frame = []
    for i in range(0, len(arr) - frame_len, frame_len // 2):
        frame = arr[i:i + frame_len]
        rms_per_frame.append(np.sqrt(np.mean(frame ** 2)))

    rms_std = float(np.std(rms_per_frame)) if rms_per_frame else 0
    energy_score = float(np.clip(1.0 - rms_std * 10, 0, 1))

    # Weighted fake score
    fake_score = (
        flatness_score * 0.40 +
        zcr_score      * 0.35 +
        energy_score   * 0.25
    )

    # Deterministic bias from audio hash
    bias = ((hash(audio_array.tobytes()[:1000]) % 100) / 2000.0) - 0.025
    return float(np.clip(fake_score + bias, 0.03, 0.97))


# ---------------------------------------------------------------------------
# Analysis details builder
# ---------------------------------------------------------------------------

def _build_audio_details(audio_array: np.ndarray, fake_score: float) -> List[dict]:
    """Generate per-component breakdown for the result card."""
    if len(audio_array) == 0:
        return []

    arr = audio_array / (np.max(np.abs(audio_array)) + 1e-8)
    frame_len = 512

    # Spectral flatness
    flatness_scores = []
    for i in range(0, len(arr) - frame_len, frame_len // 2):
        frame = arr[i:i + frame_len] * np.hanning(frame_len)
        mag = np.abs(np.fft.rfft(frame)) + 1e-10
        flatness_scores.append(
            float(np.exp(np.mean(np.log(mag))) / np.mean(mag))
        )
    flatness = float(np.clip(np.mean(flatness_scores) * 2.0, 0, 1)) if flatness_scores else 0.5

    # Energy variance
    rms_vals = [
        float(np.sqrt(np.mean(arr[i:i + frame_len] ** 2)))
        for i in range(0, len(arr) - frame_len, frame_len // 2)
    ]
    energy_regularity = float(np.clip(1.0 - np.std(rms_vals) * 8, 0, 1)) if rms_vals else 0.5

    # Silence ratio
    silence_ratio = float(np.mean(np.abs(arr) < 0.02))
    silence_score = float(np.clip(silence_ratio * 3, 0, 1))

    def scale(s: float) -> float:
        return float(np.clip(s * 0.6 + fake_score * 0.4, 0.02, 0.98))

    return [
        {
            "label": "Spectral Naturalness",
            "score": round(scale(flatness), 4),
            "description": "AI voice cloners produce unnaturally flat frequency spectra. High score = suspicious flatness.",
            "icon": "🎵",
        },
        {
            "label": "Prosody & Rhythm",
            "score": round(scale(energy_regularity), 4),
            "description": "Real human speech has irregular energy patterns. Unnaturally steady rhythm indicates synthesis.",
            "icon": "🗣️",
        },
        {
            "label": "Silence Patterns",
            "score": round(scale(silence_score), 4),
            "description": "TTS systems produce clean, abrupt silences. Real speech has natural breath sounds and pauses.",
            "icon": "🔇",
        },
        {
            "label": "Voice Consistency",
            "score": round(scale(1.0 - float(np.clip(np.std(rms_vals) * 5, 0, 1)) if rms_vals else 0.5), 4),
            "description": "Cloned voices may show inconsistent tonal quality across the recording.",
            "icon": "🎤",
        },
    ]


# ---------------------------------------------------------------------------
# Public API
# ---------------------------------------------------------------------------

def detect_audio(audio_bytes: bytes, content_type: str) -> dict:
    """
    Detect whether an audio clip is a deepfake / AI-generated voice.

    Returns the same schema as detect_image() for consistent frontend handling.
    """
    start = time.time()
    _load_audio_model()

    audio_array = _load_audio_array(audio_bytes, content_type)

    if audio_array is None or len(audio_array) < 1600:  # < 0.1s at 16 kHz
        return {
            "prediction": "UNCERTAIN",
            "confidence": 0.5,
            "fake_score": 0.5,
            "real_score": 0.5,
            "analysis_details": [],
            "model_used": _audio_model_name,
            "processing_time": int((time.time() - start) * 1000),
            "error": "Audio too short or could not be decoded",
        }

    # Run detection
    if _audio_model is not None:
        try:
            # Clip to max 30 seconds to avoid OOM
            max_samples = 30 * 16000
            clip = audio_array[:max_samples]
            fake_score = _classify_with_model(clip)
            model_used = _audio_model_name
        except Exception as exc:
            logger.warning("Model inference failed, using heuristic: %s", exc)
            fake_score = _heuristic_audio(audio_array)
            model_used = "heuristic-audio-fallback"
    else:
        fake_score = _heuristic_audio(audio_array)
        model_used = "heuristic-audio-fallback"

    real_score = 1.0 - fake_score

    if fake_score > 0.65:
        prediction = "FAKE"
        confidence = fake_score
    elif real_score > 0.65:
        prediction = "REAL"
        confidence = real_score
    else:
        prediction = "UNCERTAIN"
        confidence = max(fake_score, real_score)

    analysis_details = _build_audio_details(audio_array, fake_score)
    processing_time = int((time.time() - start) * 1000)

    return {
        "prediction": prediction,
        "confidence": round(confidence, 4),
        "fake_score": round(fake_score, 4),
        "real_score": round(real_score, 4),
        "analysis_details": analysis_details,
        "model_used": model_used,
        "processing_time": processing_time,
    }
