export interface DetectionResult {
  id: string
  filename: string
  fileType: 'image' | 'video' | 'audio'
  prediction: 'REAL' | 'FAKE' | 'UNCERTAIN'
  confidence: number
  fakeScore: number
  realScore: number
  analysisDetails: AnalysisDetail[]
  timestamp: string
  modelUsed: string
  processingTime: number
}

export interface AnalysisDetail {
  label: string
  score: number
  description: string
  icon: string
}

export interface QuizQuestion {
  id: number
  question: string
  options: string[]
  correctIndex: number
  explanation: string
  difficulty: 'easy' | 'medium' | 'hard'
  category: string
}

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

export interface ApiError {
  message: string
  code?: string
}

export interface UploadState {
  status: 'idle' | 'uploading' | 'analyzing' | 'done' | 'error'
  progress: number
  error?: string
}
