# Deepfake A.I Detector

### AI for Digital Trust & Protection Against Manipulated Content

> Helping young people see through the fake — detect deepfakes, learn digital literacy, and stay safe online.

Deepfake A.I Detector is a full-stack web application built for **Track 6** of the UCC Hackathon 2026. It combines AI-powered deepfake detection with interactive education to help children and young people recognise, assess, and respond safely to manipulated media, deepfakes, digital impersonation, and misleading AI-generated content.

---

## Features

### 1. Deepfake Detector
Upload any image or video and get an instant AI-powered analysis:
- Clear verdict: **REAL**, **FAKE**, or **UNCERTAIN**
- Confidence score with animated probability bars
- Detailed breakdown (face manipulation, compression artifacts, lighting consistency, blend/edge artifacts)
- Safety guidance on what to do next
- Scan history (in-memory, last 50 results)

### 2. Learning Hub
Six interactive lesson modules with expandable content:
- What is a Deepfake? (Beginner)
- Spotting Fake Content (Beginner)
- Digital Impersonation (Intermediate)
- AI-Generated Images (Intermediate)
- Content Provenance & C2PA (Advanced)
- Responding Safely (Beginner)

Plus 6 quick spotting tips covering eyes, shadows, lip sync, source verification, reverse image search, and edge artifacts.

### 3. Digital Literacy Quiz
A 10-question quiz with:
- Instant feedback and explanations after each answer
- Three difficulty tiers (easy, medium, hard)
- Animated SVG score ring on completion
- Answer review grid
- Score-based encouragement messages
- Retake option

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | React 18, Vite 5, TypeScript, Tailwind CSS 3 |
| Animation | Framer Motion 11 |
| Icons | Lucide React |
| File Upload | React Dropzone |
| HTTP Client | Axios |
| Backend | FastAPI, Uvicorn |
| ML Inference | Hugging Face Transformers, PyTorch |
| Image Processing | Pillow |
| Video Processing | OpenCV (headless) |
| Content Provenance | C2PA Rust SDK (optional) |

---

## Project Structure

```
ucc-hackathon/
├── truthlens-frontend/          # React + Vite + TypeScript
│   ├── src/
│   │   ├── components/           # Navbar, Footer, DropZone, AnalyzingLoader, AnalysisResult
│   │   ├── pages/                # HomePage, DetectorPage, LearnPage, QuizPage, AboutPage
│   │   ├── hooks/                # (future custom hooks)
│   │   ├── utils/                # api.ts, helpers.ts
│   │   ├── types/                # index.ts (shared types)
│   │   ├── App.tsx               # Router setup
│   │   ├── main.tsx              # Entry point
│   │   └── index.css             # Tailwind + custom styles
│   ├── index.html
│   ├── tailwind.config.js
│   ├── vite.config.ts
│   └── package.json
│
├── truthlens-backend/            # Python FastAPI
│   ├── app/
│   │   ├── routers/
│   │   │   └── detect.py          # /api/detect, /api/history, /api/health
│   │   ├── services/
│   │   │   ├── detector.py        # HF model + heuristic fallback
│   │   │   ├── video.py           # OpenCV frame extraction
│   │   │   └── provenance.py      # C2PA content credentials
│   │   ├── config.py              # Environment configuration
│   │   └── main.py                # FastAPI app entry point
│   ├── requirements.txt
│   └── .env.example
│
├── SPEC.md                       # Design specification document
└── README.md                     # This file
```

---

## Getting Started

### Prerequisites

- **Node.js** 18+ and npm
- **Python** 3.10+ (3.12+ recommended)
- Hugging Face read token (provided in `.env`)

### 1. Backend Setup

```bash
cd truthlens-backend

# Create virtual environment
python -m venv venv
source venv/bin/activate        # Linux/Mac
# or: venv\Scripts\activate     # Windows

# Install dependencies
pip install -r requirements.txt

# Copy environment file and add tokens
cp .env.example .env
# Edit .env and set HF_TOKEN=[REDACTED]

# Start the server
uvicorn app.main:app --reload --port 8000
```

The backend runs at `http://localhost:8000`. API docs available at `http://localhost:8000/docs`.

> **Note:** On first run, the backend will download the Hugging Face model (~500MB). If the model fails to load (e.g., no internet), the heuristic fallback kicks in automatically — the app still works.

### 2. Frontend Setup

```bash
cd truthlens-frontend

# Install dependencies
npm install

# Start dev server
npm run dev
```

The frontend runs at `http://localhost:5173` and proxies API calls to the backend.

### 3. Production Build

```bash
# Frontend
cd truthlens-frontend
npm run build
# Output: dist/ (deploy to any static host)

# Backend
cd truthlens-backend
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

---

## Environment Variables

### Backend (`.env`)

```bash
HF_TOKEN=[REDACTED]       # Hugging Face read token
KAGGLE_API_TOKEN=KGAT_9c100b947aa10328870da22d618dc737  # Kaggle API token
HOST=0.0.0.0
PORT=8000
```

### Frontend (`.env` — optional)

```bash
# VITE_API_URL=https://your-backend-url.com   # Override API base for production
```

---

## API Reference

### `POST /api/detect`
Upload a file for deepfake analysis.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| file | File | Yes | Image (JPG/PNG/WebP/GIF) or Video (MP4/WebM/MOV), max 50MB |

**Response:**
```json
{
  "id": "uuid",
  "filename": "example.jpg",
  "fileType": "image",
  "prediction": "FAKE",
  "confidence": 0.94,
  "fakeScore": 0.94,
  "realScore": 0.06,
  "analysisDetails": [...],
  "timestamp": "2026-11-03T14:00:00Z",
  "modelUsed": "deepfake-detection-v1",
  "processingTime": 1200,
  "provenance": {"hasCredentials": false}
}
```

### `GET /api/history`
Returns last 50 detection results.

### `GET /api/health`
Returns server health and loaded model name.

---

## How Detection Works

1. **Upload** — User drops a file via the drag-and-drop interface
2. **Preprocess** — Backend opens the image with Pillow (or extracts 5 frames from video with OpenCV)
3. **Model Inference** — Hugging Face transformer model classifies the input
4. **Fallback** — If the model fails to load, a heuristic analyzer (smoothness, saturation, channel correlation, lighting) provides scores
5. **Confidence Scoring** — Results are thresholded: >65% fake → FAKE, >65% real → REAL, else UNCERTAIN
6. **Detailed Breakdown** — Component scores (face manipulation, compression, lighting, blend artifacts) are computed
7. **Result Delivery** — Frontend renders an animated result card with verdict, bars, and safety guidance

---

## Datasets & Models

### Referenced Datasets
- **FaceForensics++** — Original and manipulated facial video dataset (access via application)
- **Kaggle Deepfake Detection Datasets** — Various community-published datasets

### Models
- **Hugging Face Deepfake Detection Models** — Community-published image classification transformers
- **Heuristic Fallback** — Custom multi-signal analyzer for offline use

### Open-Source Tools
- **C2PA Rust SDK** — Content provenance and authenticity verification

---

## Design Highlights

- **Dark mode** glassmorphism UI with gradient accents (blue → purple → pink)
- **Framer Motion** page transitions and element animations
- **Fully responsive** — mobile-first grid layout
- **Kid-friendly** language with emojis, encouragement, and safety-first guidance
- **Privacy-first** — files analysed in-memory, never stored on server
- **Graceful degradation** — works with or without GPU, with or without model download

---

## Built For

**UCC Hackathon 2026 — Track 6: AI for Digital Trust & Protection Against Manipulated Content**

> Explore AI-enabled solutions that help children recognise, assess or respond safely to manipulated media, deepfakes, digital impersonation and misleading AI-generated content. Solutions may promote content authenticity, digital literacy and safer online decision-making.

---

## License

Built for educational purposes as part of a hackathon project.
