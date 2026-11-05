"""
Translation API router — powered by Sunbird AI NLLB endpoint.
"""
from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from app.services.translator import translate_text, SUPPORTED_LANGUAGES

router = APIRouter()


class TranslateRequest(BaseModel):
    text: str
    source_language: str = "eng"
    target_language: str = "lug"


class TranslateResponse(BaseModel):
    translated_text: str
    source_language: str
    target_language: str
    error: str | None = None


@router.post("/translate", response_model=TranslateResponse)
async def translate(req: TranslateRequest):
    """Translate text between English and Ugandan/East African languages."""
    if not req.text.strip():
        raise HTTPException(400, "Text cannot be empty")
    if len(req.text) > 2000:
        raise HTTPException(400, "Text too long (max 2000 characters)")
    result = await translate_text(req.text, req.source_language, req.target_language)
    return TranslateResponse(**result)


@router.get("/languages")
async def get_languages():
    """Return supported language codes and names."""
    return {
        "languages": [
            {"code": code, "name": name}
            for code, name in SUPPORTED_LANGUAGES.items()
        ]
    }
