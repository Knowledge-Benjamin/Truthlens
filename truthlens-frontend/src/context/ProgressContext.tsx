import { createContext, ReactNode } from 'react'
import { useProgress } from '../hooks/useProgress'
import { Badge, BadgeId } from '../types'
import BadgeToast from '../components/BadgeToast'

interface ProgressContextValue {
  progress: ReturnType<typeof useProgress>['progress']
  newBadges: Badge[]
  recordScan: (prediction: string, fileType: string) => void
  recordQuiz: (score: number, total: number) => void
  recordLesson: (lessonId: string) => void
  recordSpecialBadge: (id: BadgeId) => void
  getBadges: () => Badge[]
  resetProgress: () => void
  xpForLevel: (level: number) => number
}

export const ProgressContext = createContext<ProgressContextValue | null>(null)

export function ProgressProvider({ children }: { children: ReactNode }) {
  const prog = useProgress()

  return (
    <ProgressContext.Provider value={prog}>
      {children}
      <BadgeToast badges={prog.newBadges} />
    </ProgressContext.Provider>
  )
}
