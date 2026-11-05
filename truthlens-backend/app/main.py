"""
Deepfake A.I Detector FastAPI backend application.

Entry point for the API server. Configures CORS, mounts routers, and sets up
lifespan event handling for model preloading.
"""
import logging
from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.config import HOST, PORT
from app.routers import detect as detect_router
from app.routers import translate as translate_router
from app.routers import extras as extras_router

# ---------------------------------------------------------------------------
# Logging
# ---------------------------------------------------------------------------
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s: %(message)s",
)
logger = logging.getLogger("deepfake_detector")


# ---------------------------------------------------------------------------
# Lifespan — preload model at startup
# ---------------------------------------------------------------------------
@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("Deepfake A.I Detector backend starting up...")
    # Preload the detection model
    try:
        from app.services.detector import _load_model
        import os
        os.environ.setdefault("TRANSFORMERS_OFFLINE", "1")  # load from cache, skip Hub check
        _load_model()
        logger.info("Model preloaded: %s", get_model_name_safe())
    except Exception as exc:
        logger.warning("Model preload failed (will use heuristic fallback): %s", exc)
    logger.info("Server ready at http://%s:%d", HOST, PORT)
    yield
    logger.info("Deepfake A.I Detector backend shutting down.")


def get_model_name_safe() -> str:
    try:
        from app.services.detector import get_model_name
        return get_model_name()
    except Exception:
        return "unknown"


# ---------------------------------------------------------------------------
# App
# ---------------------------------------------------------------------------
app = FastAPI(
    title="Deepfake A.I Detector API",
    description="AI-powered deepfake and manipulated media detection for digital trust education.",
    version="1.0.0",
    lifespan=lifespan,
)

# CORS — allow the Vite dev server and any deployed frontend
app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:5173",
        "http://127.0.0.1:5173",
        "http://localhost:4173",
        "http://127.0.0.1:4173",
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Mount routers
app.include_router(detect_router.router, prefix="/api", tags=["detection"])
app.include_router(translate_router.router, prefix="/api", tags=["translation"])
app.include_router(extras_router.router, prefix="/api", tags=["extras"])


@app.get("/")
async def root():
    return {
        "name": "Deepfake A.I Detector API",
        "version": "1.0.0",
        "docs": "/docs",
        "health": "/api/health",
    }


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app.main:app", host=HOST, port=PORT, reload=True)
