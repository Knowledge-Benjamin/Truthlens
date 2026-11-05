import { clsx, type ClassValue } from 'clsx'
import { twMerge } from 'tailwind-merge'

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export function formatConfidence(score: number): string {
  return `${(score * 100).toFixed(1)}%`
}

export function getVerdictColor(prediction: string): string {
  switch (prediction) {
    case 'REAL': return 'text-accent-400'
    case 'FAKE': return 'text-danger-400'
    default: return 'text-warn-400'
  }
}

export function getVerdictBg(prediction: string): string {
  switch (prediction) {
    case 'REAL': return 'bg-accent-500/10 border-accent-500/30'
    case 'FAKE': return 'bg-danger-500/10 border-danger-500/30'
    default: return 'bg-warn-500/10 border-warn-500/30'
  }
}

export function getVerdictGlow(prediction: string): string {
  switch (prediction) {
    case 'REAL': return 'glow-green'
    case 'FAKE': return 'glow-red'
    default: return ''
  }
}

export function formatFileSize(bytes: number): string {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

export function formatDuration(ms: number): string {
  if (ms < 1000) return ms + 'ms'
  return (ms / 1000).toFixed(2) + 's'
}

export function isValidMedia(file: File): { valid: boolean; error?: string } {
  const validTypes = [
    'image/jpeg', 'image/jpg', 'image/png', 'image/webp', 'image/gif',
    'video/mp4', 'video/webm', 'video/avi', 'video/mov', 'video/quicktime',
    'audio/mpeg', 'audio/mp3', 'audio/wav', 'audio/x-wav', 'audio/wave',
    'audio/ogg', 'audio/vorbis', 'audio/aac', 'audio/mp4', 'audio/m4a',
    'audio/flac', 'audio/x-flac', 'audio/webm',
  ]
  const maxSize = 50 * 1024 * 1024 // 50MB

  if (!validTypes.includes(file.type)) {
    return { valid: false, error: 'Unsupported file type. Use JPG, PNG, MP4, WebM, MP3, WAV, or OGG.' }
  }
  if (file.size > maxSize) {
    return { valid: false, error: 'File too large. Maximum size is 50MB.' }
  }
  return { valid: true }
}
