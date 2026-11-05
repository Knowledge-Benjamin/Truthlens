# Deepfake A.I Detector — Design Specification

## Track 6: AI for Digital Trust & Protection Against Manipulated Content

---

## 1. Problem Statement

Deepfakes and AI-generated content are becoming indistinguishable from reality. Young people — who are the most active on social media — are especially vulnerable to manipulated media, digital impersonation, and misleading AI-generated content. Existing detection tools flag fakes but don't teach critical thinking or build lasting digital literacy.

**The gap:** No tool combines AI detection *with* education tailored for young audiences.

---

## 2. Solution Overview

**Deepfake A.I Detector** is a web application that helps young people (ages 10-18) recognise, assess, and respond safely to manipulated media. It combines:

1. **AI Deepfake Detector** — Upload an image or video; get an instant verdict with confidence scores and a detailed breakdown
2. **Learning Hub** — Interactive, expandable lesson modules covering deepfakes, impersonation, AI-generated images, content provenance (C2PA), and safe responses
3. **Digital Literacy Quiz** — A 10-question quiz with instant feedback, explanations, and a visual score breakdown

The app uses kid-friendly language, clear safety guidance, and a privacy-first approach (files are analysed in-memory, never stored).

---

## 3. Goals & Success Criteria

| Goal | Success Criterion |
|------|-------------------|
| Detect manipulated images and videos | Backend runs HF model with heuristic fallback; returns fake/real probability |
| Teach spotting techniques | 6 lesson modules with detailed, expandable content |
| Test knowledge retention | 10-question quiz with explanations and scored results |
| Content authenticity | C2PA provenance checking integration (graceful fallback) |
| Excellent UI/UX | Polished, animated, responsive dark-mode interface |
| Privacy | Files analysed in-memory; no server-side storage |

---

## 4. Target Users

- **Primary:** Young people aged 10-18 who consume content on social media
- **Secondary:** Parents and educators looking for tools to teach digital literacy
- **Tertiary:** General users who want to verify suspicious media

---

## 5. Architecture

```
┌─────────────────────────────────────────────────────┐
│                    Deepfake A.I Detector              │
├──────────────────────────┬──────────────────────────┤
│    Frontend (React)       │    Backend (FastAPI)     │
│                          │                          │
│  ┌─────────────────────┐ │  ┌──────────────────────┐│
│  │ Home (landing page)  │ │  │ /api/detect (POST)   ││
│  │ Detector (upload)    │ │  │ /api/history (GET)   ││
│  │ Learn (lessons)      │ │  │ /api/health (GET)    ││
│  │ Quiz (assessment)    │ │  └──────────────────────┘│
│  │ About (info)         │ │                          │
│  └─────────────────────┘ │  ┌──────────────────────┐│
│                          │  │ Detection Pipeline    ││
│  Tech:                   │  │  1. Preprocess         ││
│  - React 18 + TypeScript │  │  2. HF Model Inference ││
│  - Vite 5                │  │  3. Heuristic Fallback  ││
│  - Tailwind CSS 3        │  │  4. Confidence Scoring  ││
│  - Framer Motion         │  │  5. C2PA Provenance     ││
│  - Lucide Icons          │  └──────────────────────┘│
│  - React Router 6        │                          │
│  - Axios                 │  Tech:                   │
│  - React Dropzone        │  - FastAPI 0.109         │
│                          │  - Hugging Face Transformers│
│                          │  - PyTorch               │
│                          │  - OpenCV (video frames)  │
│                          │  - Pillow (image)        │
│                          │  - C2PA Rust SDK (opt.)  │
└──────────────────────────┴──────────────────────────┘
```

### Data Flow

```
User uploads file
    │
    ▼
Frontend validates (type, size ≤ 50MB)
    │
    ▼ POST /api/detect (multipart/form-data)
    │
Backend receives bytes
    │
    ├── Image? → PIL.Image.open → detector.detect_image()
    │       └── HF model inference OR heuristic fallback
    │           └── Returns {prediction, confidence, fake_score, real_score, details}
    │
    ├── Video? → extract_frames() (OpenCV, 5 frames)
    │       └── detector.detect_video_frames() → averages per-frame scores
    │
    ▼
Build response object (uuid, timestamp, model_used, processing_time)
    │
    ▼
Store in in-memory history (last 50)
    │
    ▼
Return JSON to frontend
    │
    ▼
Frontend renders AnalysisResult component
(verdict card, score bars, expandable breakdown, safety guidance)
```

---

## 6. Feature Specifications

### 6.1 Deepfake Detector

- **Input:** Image (JPG, PNG, WebP, GIF) or Video (MP4, WebM, MOV), max 50MB
- **Output:** Verdict (REAL / FAKE / UNCERTAIN), confidence %, fake/real probability bars, 4-5 component breakdowns, safety guidance
- **Model:** Hugging Face image-classification transformer (deepfake detection); heuristic fallback for offline use
- **Video:** Extracts 5 evenly-spaced frames, analyses each, averages scores, adds temporal consistency metric
- **Thresholds:** fake_score > 0.65 → FAKE; real_score > 0.65 → REAL; else UNCERTAIN

### 6.2 Learning Hub

6 expandable modules:

| Module | Level | Duration |
|--------|-------|----------|
| What is a Deepfake? | Beginner | 5 min |
| Spotting Fake Content | Beginner | 8 min |
| Digital Impersonation | Intermediate | 6 min |
| AI-Generated Images | Intermediate | 7 min |
| Content Provenance & C2PA | Advanced | 10 min |
| Responding Safely | Beginner | 5 min |

Plus 6 quick spotting tips (eyes, shadows, lip sync, source, reverse search, edges).

### 6.3 Digital Literacy Quiz

- 10 questions across 8 categories (Verification, Visual Clues, Provenance, Impersonation, AI-Generated, Safety, Technical, Critical Thinking)
- 3 difficulty tiers (easy, medium, hard)
- Instant feedback with explanations after each answer
- Animated SVG score ring at completion
- Answer review grid (✓/✗ per question)
- Score-based messages and retake option

---

## 7. API Specification

### `POST /api/detect`

**Request:** `multipart/form-data` with `file` field

**Response (200):**
```json
{
  "id": "uuid-string",
  "filename": "example.jpg",
  "fileType": "image",
  "prediction": "FAKE",
  "confidence": 0.942,
  "fakeScore": 0.942,
  "realScore": 0.058,
  "analysisDetails": [
    {"label": "Face Manipulation", "score": 0.91, "description": "...", "icon": "🎭"},
    {"label": "Compression Artifacts", "score": 0.88, "description": "...", "icon": "🖼️"},
    {"label": "Lighting Consistency", "score": 0.72, "description": "...", "icon": "💡"},
    {"label": "Blend & Edge Artifacts", "score": 0.65, "description": "...", "icon": "✂️"}
  ],
  "timestamp": "2026-11-03T14:00:00Z",
  "modelUsed": "Organika/deepfake-detection-v1",
  "processingTime": 1200,
  "provenance": {"hasCredentials": false, "note": "..."}
}
```

**Errors:** 400 (invalid file), 413 (too large), 500 (detection failure)

### `GET /api/history`
Returns array of last 50 detection results.

### `GET /api/health`
Returns `{"status": "ok", "model": "model-name", "timestamp": "..."}`

---

## 8. UI/UX Design Principles

- **Dark mode** with gradient accents (brand blue → purple → pink)
- **Glassmorphism** cards (backdrop-blur, translucent borders)
- **Framer Motion** page transitions and element animations
- **Responsive** — mobile-first, grid breakpoints at sm/md/lg
- **Accessible** — semantic HTML, keyboard navigation, clear contrast
- **Kid-friendly** — emojis, simple language, encouraging tone, safety-first guidance
- **Micro-interactions** — hover lifts, animated progress bars, pulse rings during analysis

---

## 9. Security & Privacy

- Files analysed in-memory; no server-side file persistence
- CORS restricted to known frontend origins
- Hugging Face token used only server-side (never exposed to client)
- No user authentication required (no PII collected)
- File type and size validation on both client and server

---

## 10. Datasets & Models

### Datasets (referenced)
- **FaceForensics++** — Original and manipulated facial video dataset (access via application)
- **Kaggle Deepfake Datasets** — Various deepfake detection datasets

### Models
- **Hugging Face Deepfake Detection Models** — Community-published image classification transformers
- **Heuristic fallback** — Custom multi-signal analyzer (smoothness, saturation, channel correlation, lighting)

### Open-Source Tools
- **C2PA Rust SDK** — Content provenance verification (integrated with graceful fallback)

---

## 11. Tech Stack Summary

| Layer | Technology |
|-------|-----------|
| Frontend | React 18, Vite 5, TypeScript 5, Tailwind CSS 3 |
| Animation | Framer Motion 11 |
| Icons | Lucide React |
| HTTP | Axios |
| File Upload | React Dropzone |
| Backend | FastAPI 0.109, Uvicorn |
| ML | Hugging Face Transformers, PyTorch |
| Image | Pillow |
| Video | OpenCV (headless) |
| Provenance | C2PA (c2pa-rs, optional) |
