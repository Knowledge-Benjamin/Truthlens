# Deepfake A.I Detector Backend — Python + FastAPI + Hugging Face ML
# Models: prithivMLmods/Deepfake-Detect-Siglip2 (visual) + koyelog/deepfake-voice-detector-sota (audio)
FROM python:3.12-slim

# System deps: OpenCV, Pillow, torchaudio (libsndfile for soundfile)
RUN apt-get update && apt-get install -y --no-install-recommends \
    libgl1 \
    libglib2.0-0 \
    libgomp1 \
    libsndfile1 \
    ffmpeg \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Install CPU-only torch first (avoids pulling CUDA ~4GB)
COPY requirements.txt .
RUN pip install --no-cache-dir torch torchaudio \
    --index-url https://download.pytorch.org/whl/cpu
RUN pip install --no-cache-dir -r requirements.txt

# Copy application source
COPY app/ ./app/

ENV HF_TOKEN="" \
    KAGGLE_API_TOKEN="" \
    SUNBIRD_API_TOKEN="" \
    SUNBIRD_BASE_URL="https://api.sunbird.ai" \
    HOST=0.0.0.0 \
    PORT=8080

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=300s --retries=5 \
    CMD python -c \
    "import urllib.request; urllib.request.urlopen('http://localhost:8080/api/health')" \
    || exit 1

CMD ["python", "-m", "uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8080"]
