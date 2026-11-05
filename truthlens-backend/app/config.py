"""Application configuration loaded from environment variables."""
import os
from dotenv import load_dotenv

load_dotenv()

# Hugging Face
HF_TOKEN = os.getenv("HF_TOKEN", "")

# Kaggle
KAGGLE_API_TOKEN = os.getenv("KAGGLE_API_TOKEN", "")

# Server
HOST = os.getenv("HOST", "0.0.0.0")
PORT = int(os.getenv("PORT", "8080"))

# Model — a community deepfake detection model on Hugging Face
DEEPFAKE_MODEL_NAME = os.getenv(
    "DEEPFAKE_MODEL_NAME",
    "prithivMLmods/Deepfake-Detect-Siglip2",
)
FALLBACK_MODEL_NAME = "HrutikAdsare/deepfake-detector-faceforensics"

# Upload limits
MAX_UPLOAD_MB = 50
MAX_UPLOAD_BYTES = MAX_UPLOAD_MB * 1024 * 1024

# In-memory history (for demo purposes)
HISTORY_LIMIT = 50
