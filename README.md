# Deepfake A.I Detector

### Track 6 — AI for Digital Trust & Protection Against Manipulated Content
### UCC Hackathon 2026

> An AI-powered platform that helps young people detect deepfakes, AI-generated voices, manipulated media, and misleading content — while building lasting digital literacy skills.

---

## What It Does

**Deepfake A.I Detector** is a full-stack web application with three core tools:

| Tool | Description |
|------|-------------|
| 🛡️ **Deepfake Detector** | Upload an image, video, or audio file — AI analyses it for signs of manipulation and returns a verdict with confidence scores |
| 📚 **Learning Hub** | 6 interactive lesson modules covering deepfakes, AI-generated images, digital impersonation, content provenance, and safe responses |
| 🧠 **Digital Literacy Quiz** | 10 questions with instant feedback, difficulty tiers, animated score ring, and answer review |

---

## AI Models Used

### Visual Deepfake Detection (Images & Video)

| Layer | Model | Architecture | Notes |
|-------|-------|-------------|-------|
| Primary | [`prithivMLmods/Deepfake-Detect-Siglip2`](https://huggingface.co/prithivMLmods/Deepfake-Detect-Siglip2) | `SiglipForImageClassification` fine-tuned from `google/siglip2-base-patch16-224` | ~372 MB, binary REAL/FAKE |
| Fallback | [`HrutikAdsare/deepfake-detector-faceforensics`](https://huggingface.co/HrutikAdsare/deepfake-detector-faceforensics) | ViT/CNN fine-tuned on FaceForensics++ dataset | Loads if primary unavailable |
| Heuristic | Custom multi-signal analyser | NumPy pixel analysis | Offline fallback — no download |

**Heuristic visual signals:**
- High-frequency energy (Laplacian) — unnatural GAN skin smoothness
- Colour saturation skew — over-saturated AI-generated colours
- R/G channel correlation — channel decorrelation from face-swap blending
- Left/right lighting asymmetry — inconsistent compositing light direction

### Audio / Voice Deepfake Detection

| Layer | Model | Architecture | Notes |
|-------|-------|-------------|-------|
| Primary | [`koyelog/deepfake-voice-detector-sota`](https://huggingface.co/koyelog/deepfake-voice-detector-sota) | `facebook/wav2vec2-base` + BiGRU + Multi-Head Attention | ~378 MB, bonafide vs synthetic, 2025 SOTA |
| Fallback | [`Heem2/Deepfake-audio-detection`](https://huggingface.co/Heem2/Deepfake-audio-detection) | `facebook/wav2vec2-base` fine-tuned | Loads if primary unavailable |
| Heuristic | Custom spectral analyser | NumPy FFT analysis | Offline fallback — no download |

**Heuristic audio signals:**
- Spectral flatness — TTS voice synthesisers flatten the frequency spectrum
- Zero-crossing rate variance — real speech is more acoustically irregular
- RMS energy regularity — cloned voices have unnaturally steady loudness
- Silence pattern analysis — AI speech produces unnatural clean silences

### Translation (Multilingual Support)

| Service | Model | Languages |
|---------|-------|-----------|
| [Sunbird AI API](https://sunbird.ai) | NLLB (No Language Left Behind) | English, Luganda, Swahili, Acholi, Runyankole, Ateso |

---

## Features

### Deepfake Detector
- **Image detection** — JPG, PNG, WebP, GIF (max 50 MB)
- **Video detection** — MP4, WebM, MOV — extracts 5 evenly-spaced frames, analyses each, adds temporal consistency score
- **Audio / voice detection** — MP3, WAV, OGG, M4A, FLAC, AAC — Wav2Vec2-based neural model
- Animated waveform display for audio uploads
- Per-component analysis breakdown (face manipulation, compression, lighting, blend artifacts / spectral, prosody, silence)
- Voice-specific safety guidance ("never trust urgent requests from a voice alone")
- Scan history (last 10 results, in-memory)

### Learning Hub
Six expandable lesson modules:
1. What is a Deepfake? *(Beginner)*
2. Spotting Fake Content *(Beginner)*
3. Digital Impersonation *(Intermediate)*
4. AI-Generated Images *(Intermediate)*
5. Content Provenance & C2PA *(Advanced)*
6. Responding Safely *(Beginner)*

Plus 6 quick spotting tips.

### Digital Literacy Quiz
- 10 questions across 8 categories
- 3 difficulty tiers (easy, medium, hard)
- Instant feedback with explanations
- Animated SVG score ring at completion
- Retake and lesson review options

### Multilingual Support (Sunbird AI)
Full UI translation into 6 languages — every page, button, result label, and safety message:

| Code | Language | Native Name |
|------|----------|-------------|
| `eng` | English | English |
| `lug` | Luganda | Luganda |
| `swa` | Swahili | Kiswahili |
| `ach` | Acholi | Lwo |
| `nyn` | Runyankole | Runyankole |
| `teo` | Ateso | Ateso |

Language selection persists in `localStorage`. The navbar globe icon opens the language picker.

---

## Tech Stack

### Frontend
| Technology | Version | Purpose |
|-----------|---------|---------|
| React | 18 | UI framework |
| Vite | 5 | Build tool + dev server (HMR) |
| TypeScript | 5 | Type safety |
| Tailwind CSS | 3 | Styling + dark glassmorphism design |
| Framer Motion | 11 | Page transitions + animations |
| Lucide React | 0.344 | Icons |
| React Dropzone | 14 | Drag-and-drop file upload |
| Axios | 1.6 | HTTP client |
| React Router | 6 | Client-side routing |

### Backend
| Technology | Version | Purpose |
|-----------|---------|---------|
| FastAPI | 0.109+ | REST API framework |
| Uvicorn | 0.27+ | ASGI server with `--reload` |
| PyTorch | 2.9+ | ML inference runtime |
| Hugging Face Transformers | 5.x | Model loading + inference |
| torchaudio | 2.9+ | Audio loading + resampling |
| soundfile | 0.12+ | Audio decode fallback |
| OpenCV (headless) | 4.9+ | Video frame extraction |
| Pillow | 10+ | Image processing |
| httpx | 0.27+ | Async HTTP for Sunbird AI API |
| python-dotenv | 1.0+ | Environment configuration |

### Infrastructure
| Technology | Purpose |
|-----------|---------|
| Docker | Container runtime |
| Docker Compose | Multi-service orchestration |
| nginx (alpine) | Frontend static serve + API reverse proxy |
| Hugging Face Hub | Model hosting and download |
| Sunbird AI API | Translation service (`api.sunbird.ai`) |

---

## Project Structure

```
ucc-hackathon/
├── truthlens-frontend/           # React + Vite + TypeScript
│   ├── src/
│   │   ├── i18n/
│   │   │   ├── translations.ts    # All UI strings in 6 languages
│   │   │   └── LanguageContext.tsx # React language context + hook
│   │   ├── components/
│   │   │   ├── Navbar.tsx         # Navigation with language selector
│   │   │   ├── Footer.tsx
│   │   │   ├── DropZone.tsx       # Image / Video / Audio upload
│   │   │   ├── AnalyzingLoader.tsx
│   │   │   ├── AnalysisResult.tsx # Verdict card (visual + audio)
│   │   │   └── LanguageSelector.tsx # Globe dropdown, Sunbird attribution
│   │   ├── pages/
│   │   │   ├── HomePage.tsx
│   │   │   ├── DetectorPage.tsx
│   │   │   ├── LearnPage.tsx
│   │   │   ├── QuizPage.tsx
│   │   │   └── AboutPage.tsx
│   │   ├── utils/
│   │   │   ├── api.ts             # Backend API calls
│   │   │   └── helpers.ts         # Formatting + validation
│   │   └── types/index.ts         # Shared TypeScript types
│   ├── nginx.conf                 # nginx with API proxy + large header fix
│   ├── Dockerfile                 # Multi-stage: Node build → nginx
│   └── package.json
│
├── truthlens-backend/             # Python FastAPI
│   ├── app/
│   │   ├── services/
│   │   │   ├── detector.py        # Visual deepfake detection (SigLIP2)
│   │   │   ├── audio_detector.py  # Voice deepfake detection (Wav2Vec2)
│   │   │   ├── video.py           # OpenCV frame extraction
│   │   │   ├── translator.py      # Sunbird AI NLLB translation
│   │   │   └── provenance.py      # C2PA content credentials
│   │   ├── routers/
│   │   │   ├── detect.py          # /api/detect (image + video + audio)
│   │   │   └── translate.py       # /api/translate, /api/languages
│   │   ├── config.py              # Environment configuration
│   │   └── main.py                # FastAPI app + CORS + lifespan
│   ├── requirements.txt
│   ├── Dockerfile
│   └── .env.example
│
├── docker-compose.yml             # Orchestrates frontend + backend
├── SPEC.md                        # Full design specification
└── README.md                      # This file
```

---

## Getting Started

### Development (Hot Reload — Recommended)

No Docker rebuild needed during development. Changes reflect instantly.

**Backend** (auto-restarts on Python file changes):
```bash
cd truthlens-backend
python -m venv venv
venv\Scripts\activate          # Windows
# source venv/bin/activate     # Linux/Mac
pip install -r requirements.txt
uvicorn app.main:app --port 8088 --reload
```

**Frontend** (HMR — changes appear in < 500ms):
```bash
cd truthlens-frontend
npm install
npm run dev
# Open: http://localhost:5173
```

### Production (Docker)

```bash
# Copy and configure environment
cp truthlens-backend/.env.example truthlens-backend/.env
# Edit .env and add your tokens

# Build and start both containers
docker compose up -d --build

# Frontend: http://localhost:3000
# Backend:  http://localhost:8081
# API docs: http://localhost:8081/docs
```

> **Note:** First run downloads ~750 MB of HF models (visual ~372 MB + audio ~378 MB).
> Subsequent starts use the `hf_cache` Docker volume — startup in seconds.

### Stop

```bash
docker compose down
```

---

## Environment Variables

### Backend (`.env`)

```bash
# Hugging Face — read token for model downloads
HF_TOKEN=your_huggingface_read_token

# Kaggle API token (for dataset access)
KAGGLE_API_TOKEN=your_kaggle_api_token

# Sunbird AI translation API
# Register at https://api.sunbird.ai/auth/register
SUNBIRD_API_TOKEN=your_sunbird_token
SUNBIRD_BASE_URL=https://api.sunbird.ai

# Server
HOST=0.0.0.0
PORT=8088
```

---

## API Reference

### `POST /api/detect`
Analyse an image, video, or audio file.

| Parameter | Type | Description |
|-----------|------|-------------|
| `file` | File (multipart) | Image (JPG/PNG/WebP/GIF), Video (MP4/WebM/MOV), or Audio (MP3/WAV/OGG/M4A/FLAC) — max 50 MB |

**Response:**
```json
{
  "id": "uuid",
  "filename": "voice_message.mp3",
  "fileType": "audio",
  "prediction": "FAKE",
  "confidence": 0.91,
  "fakeScore": 0.91,
  "realScore": 0.09,
  "analysisDetails": [
    {"label": "Spectral Naturalness", "score": 0.88, "description": "...", "icon": "🎵"},
    {"label": "Prosody & Rhythm",     "score": 0.82, "description": "...", "icon": "🗣️"},
    {"label": "Silence Patterns",     "score": 0.79, "description": "...", "icon": "🔇"},
    {"label": "Voice Consistency",    "score": 0.74, "description": "...", "icon": "🎤"}
  ],
  "timestamp": "2026-11-04T00:00:00Z",
  "modelUsed": "koyelog/deepfake-voice-detector-sota",
  "processingTime": 850
}
```

### `GET /api/health`
```json
{
  "status": "ok",
  "visual_model": "prithivMLmods/Deepfake-Detect-Siglip2",
  "audio_model": "koyelog/deepfake-voice-detector-sota",
  "timestamp": "2026-11-04T00:00:00Z"
}
```

### `POST /api/translate`
```json
{ "text": "Hello", "source_language": "eng", "target_language": "lug" }
```

### `GET /api/languages`
Returns all 6 supported language codes and names.

### `GET /api/history`
Returns last 50 detection results (all file types).

---

## Detection Pipeline

```
User uploads file (image / video / audio)
    │
    ▼ POST /api/detect (multipart/form-data)
    │
    ├── audio/*  ──► audio_detector.py
    │                  ├── torchaudio decode → 16 kHz mono float32
    │                  ├── Clip to 30s max
    │                  ├── Wav2Vec2 feature extraction + inference
    │                  └── Returns fake_score + 4 spectral analysis details
    │
    ├── image/*  ──► detector.py
    │                  ├── PIL.Image.open
    │                  ├── AutoImageProcessor + SigLIP2 inference
    │                  └── Returns fake_score + 4 visual analysis details
    │
    └── video/*  ──► video.py → detector.py
                       ├── OpenCV: extract 5 evenly-spaced frames
                       ├── Run visual detector on each frame
                       ├── Average per-frame scores
                       └── Add temporal consistency metric
```

---

## Changes Log

### v1.2 — Voice & Audio Detection
- **Added** `audio_detector.py` — Wav2Vec2-based voice deepfake service
- **Added** audio routing in `detect.py` (routes `audio/*` MIME types to voice detector)
- **Updated** `DropZone.tsx` — now accepts MP3, WAV, OGG, M4A, FLAC, AAC
- **Added** animated waveform bars UI for audio file preview
- **Updated** `AnalysisResult.tsx` — "Voice Analysis" badge + voice-specific safety message
- **Updated** health endpoint to report both `visual_model` and `audio_model`
- **Added** `torchaudio` and `soundfile` to requirements

### v1.1 — Multilingual Support + Responsive UI
- **Added** full UI translations in 6 languages (English, Luganda, Swahili, Acholi, Runyankole, Ateso)
- **Added** `LanguageContext.tsx` + `useLanguage()` hook with localStorage persistence
- **Added** `LanguageSelector` navbar dropdown with Sunbird AI attribution
- **Added** Sunbird AI NLLB translation backend endpoint (`/api/translate`)
- **Added** `/api/languages` endpoint listing all supported codes
- **Fixed** responsive layout across all pages — mobile-first breakpoints
- **Fixed** nginx "Request Header Too Large" error (increased buffer sizes)
- **Fixed** TypeScript unused import errors blocking Docker build

### v1.0 — Initial Release
- Visual deepfake detection (images + video frames)
- 6 interactive learning modules
- 10-question digital literacy quiz
- Dark glassmorphism UI with Framer Motion animations
- Docker Compose deployment
- FastAPI backend with heuristic fallback detector
- C2PA content provenance integration (graceful fallback)

---

## Datasets Referenced

- **[FaceForensics++](https://github.com/ondyari/FaceForensics)** — Original and manipulated facial video dataset (FaceForensics++ model trained on this)
- **[Kaggle Deepfake Datasets](https://www.kaggle.com)** — Community-published deepfake detection datasets
- **[Sunbird SALT Dataset](https://huggingface.co/datasets/Sunbird/salt)** — Multi-way parallel text corpus for Ugandan languages (backs the Sunbird NLLB model)

---

## Built For

**UCC Hackathon 2026 — Track 6: AI for Digital Trust & Protection Against Manipulated Content**

> Explore AI-enabled solutions that help children recognise, assess or respond safely to manipulated media, deepfakes, digital impersonation and misleading AI-generated content. Solutions may promote content authenticity, digital literacy and safer online decision-making.

**Recommended tools used:**
- ✅ FaceForensics++ — training reference dataset
- ✅ Hugging Face Deepfake Detection Models — community-published models
- ✅ C2PA Rust SDK — content provenance checking
- ✅ Kaggle Datasets — referenced for deepfake benchmarks

---

## License

Built for educational purposes as part of a hackathon project.
