"""
Detection API router.

Routes uploaded files to the correct detection service:
  - image/*          → visual deepfake detector (SigLIP2 / Wav2Vec2)
  - video/*          → frame extraction + visual detector
  - audio/*          → voice/audio deepfake detector (Wav2Vec2-based)
"""
import io
import uuid
import logging
from datetime import datetime, timezone

from fastapi import APIRouter, UploadFile, File, HTTPException

from app.config import MAX_UPLOAD_BYTES
from app.services.detector import detect_image, detect_video_frames, get_model_name
from app.services.audio_detector import detect_audio, get_audio_model_name
from app.services.video import extract_frames

logger = logging.getLogger("deepfake_detector.api")

router = APIRouter()

# Supported audio MIME types
AUDIO_TYPES = {
    "audio/wav", "audio/x-wav", "audio/wave",
    "audio/mpeg", "audio/mp3",
    "audio/ogg", "audio/vorbis",
    "audio/aac", "audio/mp4", "audio/m4a",
    "audio/flac", "audio/webm", "audio/x-flac",
}

# In-memory history (demo — last 50)
_history: list = []


def _file_type(mime: str) -> str:
    mime = (mime or "").lower().strip()
    if mime.startswith("image/"):
        return "image"
    if mime.startswith("video/"):
        return "video"
    if mime.startswith("audio/") or mime in AUDIO_TYPES:
        return "audio"
    return "image"


@router.post("/detect")
async def detect(file: UploadFile = File(...)):
    """
    Analyse an uploaded image, video, or audio file for AI / deepfake manipulation.
    """
    content = await file.read()

    if not content:
        raise HTTPException(400, "Empty file received.")
    if len(content) > MAX_UPLOAD_BYTES:
        raise HTTPException(413, f"File too large. Max {MAX_UPLOAD_BYTES // (1024 * 1024)} MB.")

    mime = (file.content_type or "").lower()
    file_type = _file_type(mime)
    filename = file.filename or "unknown"

    logger.info("Detect request: %s (%s / %s, %d bytes)", filename, file_type, mime, len(content))

    try:
        # ── Audio ────────────────────────────────────────────────────────────
        if file_type == "audio":
            result = detect_audio(content, mime)

        # ── Image ────────────────────────────────────────────────────────────
        elif file_type == "image":
            from PIL import Image
            pil_image = Image.open(io.BytesIO(content))
            result = detect_image(pil_image)

        # ── Video ────────────────────────────────────────────────────────────
        else:
            frames = extract_frames(content)
            if frames:
                result = detect_video_frames(frames)
            else:
                # Fallback: try as image
                from PIL import Image
                try:
                    pil_image = Image.open(io.BytesIO(content))
                    result = detect_image(pil_image)
                    file_type = "image"
                except Exception:
                    raise HTTPException(
                        400, "Could not process video. Ensure it is a valid MP4/WebM."
                    )

        response = {
            "id": str(uuid.uuid4()),
            "filename": filename,
            "fileType": file_type,
            "prediction": result["prediction"],
            "confidence": result["confidence"],
            "fakeScore": result["fake_score"],
            "realScore": result["real_score"],
            "analysisDetails": result["analysis_details"],
            "timestamp": datetime.now(timezone.utc).isoformat(),
            "modelUsed": result["model_used"],
            "processingTime": result["processing_time"],
            "provenance": {"has_credentials": False, "note": "C2PA check available"},
        }

        _history.insert(0, response)
        if len(_history) > 50:
            _history.pop()

        return response

    except HTTPException:
        raise
    except Exception as exc:
        logger.exception("Detection failed for %s", filename)
        raise HTTPException(500, f"Detection failed: {exc}")


@router.get("/history")
async def get_history():
    """Return last 50 detection results (all media types)."""
    return _history


@router.get("/health")
async def health():
    """Health-check — returns loaded model names for both visual and audio."""
    return {
        "status": "ok",
        "visual_model": get_model_name(),
        "audio_model": get_audio_model_name(),
        "timestamp": datetime.now(timezone.utc).isoformat(),
    }
