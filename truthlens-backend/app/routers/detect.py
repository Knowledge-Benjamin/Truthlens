"""
Detection API router.

Handles file uploads, routes to image/video detection services, and returns
structured results to the frontend.
"""
import io
import uuid
import time
import logging
from datetime import datetime, timezone

from fastapi import APIRouter, UploadFile, File, HTTPException

from app.config import MAX_UPLOAD_BYTES
from app.services.detector import detect_image, detect_video_frames, get_model_name
from app.services.video import extract_frames
from app.services.provenance import check_provenance

logger = logging.getLogger("deepfake_detector.api")

router = APIRouter()

# In-memory history store (demo only)
_history: list = []


def _file_type(mime: str) -> str:
    if mime.startswith("image/"):
        return "image"
    if mime.startswith("video/"):
        return "video"
    return "image"  # default


@router.post("/detect")
async def detect(file: UploadFile = File(...)):
    """Analyse an uploaded image or video for deepfake manipulation."""
    # Read file content
    content = await file.read()
    if len(content) > MAX_UPLOAD_BYTES:
        raise HTTPException(413, f"File too large. Max {MAX_UPLOAD_BYTES // (1024*1024)} MB.")

    if not content:
        raise HTTPException(400, "Empty file received.")

    file_type = _file_type(file.content_type or "")
    filename = file.filename or "unknown"

    logger.info("Detect request: %s (%s, %d bytes)", filename, file_type, len(content))

    try:
        if file_type == "image":
            from PIL import Image
            pil_image = Image.open(io.BytesIO(content))
            result = detect_image(pil_image)
        else:
            # Video — extract frames then detect
            frames = extract_frames(content)
            if frames:
                result = detect_video_frames(frames)
            else:
                # If frame extraction fails, try as image
                from PIL import Image
                try:
                    pil_image = Image.open(io.BytesIO(content))
                    result = detect_image(pil_image)
                    file_type = "image"
                except Exception:
                    raise HTTPException(400, "Could not process video file. Ensure it's a valid MP4/WebM.")

        # Check provenance (non-blocking, best-effort)
        provenance = None
        # We skip file-based provenance for bytes; in production we'd write temp file.
        # For now just note it's available
        provenance = {"has_credentials": False, "note": "Provenance check available with C2PA SDK"}

        # Build response
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
            "provenance": provenance,
        }

        # Store in history
        _history.insert(0, response)
        if len(_history) > 50:
            _history.pop()

        return response

    except HTTPException:
        raise
    except Exception as exc:
        logger.exception("Detection failed")
        raise HTTPException(500, f"Detection failed: {str(exc)}")


@router.get("/history")
async def get_history():
    """Return recent detection results."""
    return _history


@router.get("/health")
async def health():
    """Health check endpoint."""
    return {
        "status": "ok",
        "model": get_model_name(),
        "timestamp": datetime.now(timezone.utc).isoformat(),
    }
