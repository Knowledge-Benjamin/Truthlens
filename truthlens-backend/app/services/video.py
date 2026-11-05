"""
Video frame extraction service.

Extracts evenly-spaced frames from an uploaded video for deepfake analysis.
Uses OpenCV (cv2) headless build.
"""
import logging
import tempfile
import os
from typing import List
from PIL import Image

logger = logging.getLogger("deepfake_detector.video")

MAX_FRAMES = 5  # number of frames to extract for analysis


def extract_frames(video_bytes: bytes, max_frames: int = MAX_FRAMES) -> List[Image.Image]:
    """
    Extract evenly-spaced frames from a video file (bytes).

    Returns a list of PIL Images.
    """
    try:
        import cv2
    except ImportError:
        logger.error("opencv-python-headless not installed")
        return []

    # Write bytes to temp file
    tmp_path = None
    try:
        fd, tmp_path = tempfile.mkstemp(suffix=".mp4")
        with os.fdopen(fd, "wb") as f:
            f.write(video_bytes)

        cap = cv2.VideoCapture(tmp_path)
        if not cap.isOpened():
            logger.error("Could not open video file")
            return []

        total_frames = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
        if total_frames <= 0:
            return []

        # Sample evenly
        step = max(total_frames // max_frames, 1)
        frames: List[Image.Image] = []

        for i in range(0, total_frames, step):
            if len(frames) >= max_frames:
                break
            cap.set(cv2.CAP_PROP_POS_FRAMES, i)
            ret, frame = cap.read()
            if not ret or frame is None:
                continue
            # Convert BGR → RGB → PIL
            rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
            pil = Image.fromarray(rgb)
            frames.append(pil)

        cap.release()
        logger.info("Extracted %d frames from video", len(frames))
        return frames

    except Exception as exc:
        logger.error("Frame extraction failed: %s", exc)
        return []
    finally:
        if tmp_path and os.path.exists(tmp_path):
            os.unlink(tmp_path)
