# -*- coding: utf-8 -*-
"""
Extra API endpoints:
  POST /api/detect-url   — fetch media from URL, run deepfake detection
  POST /api/check-text   — detect AI-generated text
"""
import io
import uuid
import logging
import re
from datetime import datetime, timezone
from urllib.parse import urlparse

import httpx
from fastapi import APIRouter, HTTPException
from pydantic import BaseModel

from app.config import MAX_UPLOAD_BYTES
from app.services.detector import detect_image, detect_video_frames, get_model_name
from app.services.audio_detector import detect_audio, get_audio_model_name
from app.services.video import extract_frames

logger = logging.getLogger("deepfake_detector.extras")
router = APIRouter()

ALLOWED_IMAGE_TYPES = {"image/jpeg","image/png","image/webp","image/gif"}
ALLOWED_VIDEO_TYPES = {"video/mp4","video/webm","video/quicktime","video/avi"}
ALLOWED_AUDIO_TYPES = {"audio/mpeg","audio/wav","audio/ogg","audio/mp4","audio/flac","audio/webm"}


# ─── URL Detection ───────────────────────────────────────────────────────────

class UrlRequest(BaseModel):
    url: str

@router.post("/detect-url")
async def detect_url(req: UrlRequest):
    """Fetch media from a URL and run deepfake detection."""
    url = req.url.strip()
    if not url.startswith(("http://","https://")):
        raise HTTPException(400, "URL must start with http:// or https://")

    parsed = urlparse(url)
    if not parsed.netloc:
        raise HTTPException(400, "Invalid URL")

    try:
        async with httpx.AsyncClient(timeout=30, follow_redirects=True,
                                     headers={"User-Agent": "DeepfakeAI-Scanner/1.0"}) as client:
            response = await client.get(url)
            response.raise_for_status()
    except httpx.TimeoutException:
        raise HTTPException(408, "URL timed out. The server took too long to respond.")
    except httpx.HTTPStatusError as e:
        raise HTTPException(400, f"Could not fetch URL: HTTP {e.response.status_code}")
    except Exception as e:
        raise HTTPException(400, f"Could not fetch URL: {e}")

    content      = response.content
    content_type = response.headers.get("content-type","").split(";")[0].strip().lower()
    filename     = parsed.path.split("/")[-1] or "url_media"

    if len(content) > MAX_UPLOAD_BYTES:
        raise HTTPException(413, "Remote file too large (max 50 MB)")

    # Route by content type
    try:
        if content_type in ALLOWED_IMAGE_TYPES:
            from PIL import Image
            pil = Image.open(io.BytesIO(content))
            result = detect_image(pil)
            file_type = "image"
        elif content_type in ALLOWED_VIDEO_TYPES:
            frames = extract_frames(content)
            if frames:
                result = detect_video_frames(frames)
            else:
                from PIL import Image
                pil = Image.open(io.BytesIO(content))
                result = detect_image(pil)
            file_type = "video"
        elif content_type in ALLOWED_AUDIO_TYPES:
            result = detect_audio(content, content_type)
            file_type = "audio"
        else:
            # Try as image by default
            from PIL import Image
            try:
                pil = Image.open(io.BytesIO(content))
                result = detect_image(pil)
                file_type = "image"
            except Exception:
                raise HTTPException(415, f"Unsupported media type: {content_type}")
    except HTTPException:
        raise
    except Exception as e:
        logger.exception("URL detection failed")
        raise HTTPException(500, f"Detection failed: {e}")

    return {
        "id": str(uuid.uuid4()),
        "filename": filename or url[:60],
        "fileType": "url",
        "prediction":      result["prediction"],
        "confidence":      result["confidence"],
        "fakeScore":       result["fake_score"],
        "realScore":       result["real_score"],
        "analysisDetails": result["analysis_details"],
        "timestamp":       datetime.now(timezone.utc).isoformat(),
        "modelUsed":       result["model_used"],
        "processingTime":  result["processing_time"],
        "sourceUrl":       url,
    }


# ─── Text AI-Detection ───────────────────────────────────────────────────────

class TextRequest(BaseModel):
    text: str

def _analyse_text_heuristic(text: str) -> dict:
    """
    Multi-signal heuristic for AI-generated text detection.
    Looks for: perplexity surrogates, repetition, lack of typos,
    overly formal connectives, and uniform sentence length.
    """
    import math

    sentences = [s.strip() for s in re.split(r'[.!?]+', text) if s.strip()]
    words     = text.lower().split()
    word_set  = set(words)

    if not sentences or not words:
        return {"ai_score": 0.5, "human_score": 0.5, "signals": []}

    # 1. Vocabulary diversity (type-token ratio) — AI tends to be lower
    ttr = len(word_set) / len(words) if words else 1.0
    ttr_score = max(0.0, 1.0 - ttr * 1.5)   # low diversity → AI

    # 2. Sentence length variance — AI writes very uniformly
    lengths = [len(s.split()) for s in sentences]
    if len(lengths) > 1:
        mean_len = sum(lengths) / len(lengths)
        variance = sum((l - mean_len)**2 for l in lengths) / len(lengths)
        uniformity_score = max(0.0, 1.0 - math.sqrt(variance) / 8.0)
    else:
        uniformity_score = 0.5

    # 3. Filler connectives common in AI writing
    ai_connectives = ["furthermore", "moreover", "additionally", "in conclusion",
                      "it is worth noting", "it is important to", "in summary",
                      "to elaborate", "it should be noted", "nevertheless",
                      "nonetheless", "in essence", "as a result", "consequently"]
    connective_count = sum(1 for c in ai_connectives if c in text.lower())
    connective_score = min(1.0, connective_count / 3.0)

    # 4. Absence of contractions (AI formal style avoids them)
    contractions = ["don't","can't","won't","isn't","it's","i'm","you're","we're","they're","couldn't","wouldn't"]
    has_contractions = any(c in text.lower() for c in contractions)
    contraction_score = 0.0 if has_contractions else 0.6

    # 5. Absence of informal markers (typos, slang, etc.)
    informal_markers = ["lol","haha","omg","tbh","ngl","smh","imo","btw","gonna","wanna","kinda"]
    has_informal = any(m in text.lower() for m in informal_markers)
    informal_score = 0.0 if has_informal else 0.3

    # Weighted combination
    ai_score = (
        ttr_score         * 0.20 +
        uniformity_score  * 0.25 +
        connective_score  * 0.30 +
        contraction_score * 0.15 +
        informal_score    * 0.10
    )
    ai_score = float(min(0.97, max(0.03, ai_score)))

    signals = [
        {
            "label": "Vocabulary Diversity",
            "score": round(ttr_score, 3),
            "description": "AI text tends to repeat similar words and phrases. Low diversity suggests AI authorship.",
            "icon": "book",
        },
        {
            "label": "Sentence Uniformity",
            "score": round(uniformity_score, 3),
            "description": "AI writes with very consistent sentence lengths. High uniformity is suspicious.",
            "icon": "ruler",
        },
        {
            "label": "Formal AI Connectives",
            "score": round(connective_score, 3),
            "description": "Words like 'furthermore', 'in conclusion' are overused by AI models.",
            "icon": "link",
        },
        {
            "label": "Lack of Natural Speech",
            "score": round((contraction_score + informal_score) / 2, 3),
            "description": "Real human writing contains contractions, informal words, or small typos.",
            "icon": "chat",
        },
    ]

    return {"ai_score": ai_score, "human_score": round(1.0 - ai_score, 3), "signals": signals}


@router.post("/check-text")
async def check_text(req: TextRequest):
    """Detect whether text was AI-generated."""
    text = req.text.strip()
    if len(text) < 20:
        raise HTTPException(400, "Text too short (min 20 characters)")
    if len(text) > 5000:
        raise HTTPException(400, "Text too long (max 5000 characters)")

    import time
    start = time.time()

    # Try HF text-detection model first (roberta-base-openai-detector)
    model_used = "heuristic-text-fallback"
    try:
        from transformers import pipeline
        import os
        classifier = pipeline(
            "text-classification",
            model="roberta-base-openai-detector",
            local_files_only=os.environ.get("TRANSFORMERS_OFFLINE","0") == "1",
        )
        # truncate to 512 tokens for roberta
        chunk = text[:2000]
        res   = classifier(chunk)[0]
        label = res["label"].upper()
        score = float(res["score"])
        ai_score    = score if "fake" in label or "ai" in label else (1.0 - score)
        human_score = 1.0 - ai_score
        model_used  = "roberta-base-openai-detector"
        analysis    = _analyse_text_heuristic(text)
        signals     = analysis["signals"]
    except Exception as e:
        logger.debug("HF text model failed (%s), using heuristic", e)
        analysis    = _analyse_text_heuristic(text)
        ai_score    = analysis["ai_score"]
        human_score = analysis["human_score"]
        signals     = analysis["signals"]

    if ai_score > 0.65:
        prediction = "AI_GENERATED"; confidence = ai_score
    elif human_score > 0.65:
        prediction = "HUMAN_WRITTEN"; confidence = human_score
    else:
        prediction = "UNCERTAIN"; confidence = max(ai_score, human_score)

    return {
        "prediction":    prediction,
        "aiScore":       round(ai_score, 4),
        "humanScore":    round(human_score, 4),
        "confidence":    round(confidence, 4),
        "signals":       signals,
        "modelUsed":     model_used,
        "processingTime": int((time.time() - start) * 1000),
    }
