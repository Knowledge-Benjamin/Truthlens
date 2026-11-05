"""
Sunbird AI Translation Service
Supports English ↔ Luganda, Acholi, Runyankole, Ateso, Swahili via
the Sunbird AI NLLB translate API at https://api.sunbird.ai
"""
import logging
import os
from typing import Optional

import httpx
from app.config import SUNBIRD_API_TOKEN, SUNBIRD_BASE_URL

logger = logging.getLogger("deepfake_detector.translator")

# Supported language codes (Sunbird API codes)
SUPPORTED_LANGUAGES = {
    "eng": "English",
    "lug": "Luganda",
    "ach": "Acholi",
    "nyn": "Runyankole",
    "teo": "Ateso",
    "swa": "Swahili",
}


async def translate_text(
    text: str,
    source_language: str,
    target_language: str,
    timeout: float = 15.0,
) -> dict:
    """
    Translate text using the Sunbird AI NLLB API.

    Returns dict:
        { "translated_text": str, "source_language": str, "target_language": str }

    Falls back gracefully if API is unavailable or token is missing.
    """
    if source_language == target_language:
        return {
            "translated_text": text,
            "source_language": source_language,
            "target_language": target_language,
        }

    if not SUNBIRD_API_TOKEN:
        logger.warning("SUNBIRD_API_TOKEN not set — returning original text")
        return {
            "translated_text": text,
            "source_language": source_language,
            "target_language": target_language,
            "error": "Translation API token not configured",
        }

    if source_language not in SUPPORTED_LANGUAGES:
        return {"translated_text": text, "error": f"Unsupported source language: {source_language}"}
    if target_language not in SUPPORTED_LANGUAGES:
        return {"translated_text": text, "error": f"Unsupported target language: {target_language}"}

    url = f"{SUNBIRD_BASE_URL}/tasks/nllb_translate"
    headers = {
        "Authorization": f"Bearer {SUNBIRD_API_TOKEN}",
        "Content-Type": "application/json",
    }
    payload = {
        "text": text,
        "source_language": source_language,
        "target_language": target_language,
    }

    try:
        async with httpx.AsyncClient(timeout=timeout) as client:
            response = await client.post(url, json=payload, headers=headers)
            response.raise_for_status()
            data = response.json()
            translated = data.get("translated_text") or data.get("output") or data.get("text") or text
            logger.info("Translated [%s→%s]: %s → %s", source_language, target_language, text[:40], translated[:40])
            return {
                "translated_text": translated,
                "source_language": source_language,
                "target_language": target_language,
            }
    except httpx.HTTPStatusError as exc:
        logger.error("Sunbird API HTTP error %s: %s", exc.response.status_code, exc.response.text)
        return {"translated_text": text, "error": f"API error {exc.response.status_code}"}
    except Exception as exc:
        logger.error("Sunbird translation failed: %s", exc)
        return {"translated_text": text, "error": str(exc)}
