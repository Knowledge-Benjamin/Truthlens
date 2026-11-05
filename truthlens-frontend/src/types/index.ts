// ─── Core Detection ────────────────────────────────────────────────────────
export interface DetectionResult {
  id: string
  filename: string
  fileType: 'image' | 'video' | 'audio' | 'url'
  prediction: 'REAL' | 'FAKE' | 'UNCERTAIN'
  confidence: number
  fakeScore: number
  realScore: number
  analysisDetails: AnalysisDetail[]
  timestamp: string
  modelUsed: string
  processingTime: number
  heatmapUrl?: string        // base64 PNG for visual explainability
  explanation?: string       // plain-language "why"
}

export interface AnalysisDetail {
  label: string
  score: number
  description: string
  icon: string
}

// ─── Quiz ──────────────────────────────────────────────────────────────────
export interface QuizQuestion {
  id: number
  question: string
  options: string[]
  correctIndex: number
  explanation: string
  difficulty: 'easy' | 'medium' | 'hard'
  category: string
}

// ─── Learn ─────────────────────────────────────────────────────────────────
export interface LessonModule {
  id: string
  title: string
  description: string
  icon: string
  color: string
  duration: string
  level: 'Beginner' | 'Intermediate' | 'Advanced'
  topics: string[]
}

export interface TipCard {
  id: number
  title: string
  description: string
  icon: string
  category: string
  color: string
}

// ─── Upload / UI State ─────────────────────────────────────────────────────
export interface ApiError {
  message: string
  code?: string
}

export interface UploadState {
  status: 'idle' | 'uploading' | 'analyzing' | 'done' | 'error'
  progress: number
  error?: string
}

// ─── Feature 3: Impersonation ──────────────────────────────────────────────
export interface ImpersonationResult {
  riskLevel: 'LOW' | 'MEDIUM' | 'HIGH'
  riskScore: number
  flags: ImpersonationFlag[]
  advice: string
}

export interface ImpersonationFlag {
  label: string
  description: string
  severity: 'info' | 'warning' | 'danger'
  icon: string
}

// ─── Feature 4: Progress / Badges ─────────────────────────────────────────
export type BadgeId =
  | 'first_scan' | 'five_scans' | 'twenty_scans'
  | 'first_quiz' | 'quiz_pass' | 'quiz_perfect'
  | 'all_lessons' | 'first_lesson'
  | 'fake_hunter' | 'voice_check' | 'url_check'
  | 'spot_trainer' | 'text_checker' | 'weekly_streak'

export interface Badge {
  id: BadgeId
  title: string
  description: string
  icon: string
  color: string
  unlocked: boolean
  unlockedAt?: string
}

export interface UserProgress {
  scansCompleted: number
  fakesFound: number
  quizzesTaken: number
  bestQuizScore: number
  lessonsRead: string[]
  badgesUnlocked: BadgeId[]
  lastActive: string
  streakDays: number
  level: number
  xp: number
}

// ─── Feature 7: Text fact-check ───────────────────────────────────────────
export interface TextCheckResult {
  prediction: 'AI_GENERATED' | 'HUMAN_WRITTEN' | 'UNCERTAIN'
  aiScore: number
  humanScore: number
  confidence: number
  signals: TextSignal[]
  modelUsed: string
  processingTime: number
}

export interface TextSignal {
  label: string
  score: number
  description: string
  icon: string
}

// ─── Feature 9: Spot the Fake ─────────────────────────────────────────────
export interface SpotChallenge {
  id: number
  realImage: string    // URL or base64
  fakeImage: string
  explanation: string
  difficulty: 'easy' | 'medium' | 'hard'
  category: string
  hints: string[]
}
