import axios from 'axios'
import { DetectionResult } from '../types'

const API_BASE = '/api'

const client = axios.create({
  baseURL: API_BASE,
  timeout: 120_000, // 2 minutes for video analysis
})

export async function analyzeMedia(file: File): Promise<DetectionResult> {
  const form = new FormData()
  form.append('file', file)

  const response = await client.post<DetectionResult>('/detect', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return response.data
}

export async function getHistory(): Promise<DetectionResult[]> {
  const response = await client.get<DetectionResult[]>('/history')
  return response.data
}

export async function checkHealth(): Promise<{ status: string; model: string }> {
  const response = await client.get('/health')
  return response.data
}
