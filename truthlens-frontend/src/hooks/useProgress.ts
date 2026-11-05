import { useState, useCallback } from 'react'
import { UserProgress, Badge, BadgeId } from '../types'

const STORAGE_KEY = 'deepfake_ai_progress'

const BADGE_DEFINITIONS: Badge[] = [
  { id: 'first_scan',    title: 'First Scan',       description: 'Completed your first media scan', icon: '🔍', color: 'from-brand-500 to-purple-600', unlocked: false },
  { id: 'five_scans',    title: 'Scanner',          description: 'Completed 5 scans',               icon: '🛡️', color: 'from-purple-500 to-pink-600',  unlocked: false },
  { id: 'twenty_scans',  title: 'Deepfake Hunter',  description: 'Completed 20 scans',              icon: '🏆', color: 'from-warn-500 to-orange-500',  unlocked: false },
  { id: 'first_quiz',    title: 'Quiz Starter',     description: 'Took your first quiz',             icon: '🧠', color: 'from-pink-500 to-rose-600',    unlocked: false },
  { id: 'quiz_pass',     title: 'Quiz Passer',      description: 'Scored 60%+ on a quiz',           icon: '⭐', color: 'from-accent-500 to-teal-600',  unlocked: false },
  { id: 'quiz_perfect',  title: 'Perfect Score',    description: 'Got 100% on the quiz',            icon: '🎯', color: 'from-accent-400 to-cyan-500',  unlocked: false },
  { id: 'first_lesson',  title: 'Learner',          description: 'Read your first lesson',          icon: '📖', color: 'from-blue-500 to-indigo-600',  unlocked: false },
  { id: 'all_lessons',   title: 'Scholar',          description: 'Completed all 6 lessons',         icon: '🎓', color: 'from-indigo-500 to-purple-600', unlocked: false },
  { id: 'fake_hunter',   title: 'Fake Hunter',      description: 'Found 5 fake files',              icon: '🕵️', color: 'from-danger-500 to-rose-600',  unlocked: false },
  { id: 'voice_check',   title: 'Voice Guard',      description: 'Scanned your first audio file',   icon: '🎤', color: 'from-pink-500 to-fuchsia-600', unlocked: false },
  { id: 'url_check',     title: 'Link Checker',     description: 'Scanned your first URL',          icon: '🔗', color: 'from-cyan-500 to-blue-600',    unlocked: false },
  { id: 'spot_trainer',  title: 'Spot Trainer',     description: 'Completed Spot the Fake training', icon: '👁️', color: 'from-emerald-500 to-teal-600', unlocked: false },
  { id: 'text_checker',  title: 'Text Detective',   description: 'Checked your first text for AI',  icon: '📝', color: 'from-amber-500 to-orange-600', unlocked: false },
  { id: 'weekly_streak', title: 'Streak!',          description: 'Used the app 7 days in a row',    icon: '🔥', color: 'from-orange-500 to-red-600',   unlocked: false },
]

const DEFAULT_PROGRESS: UserProgress = {
  scansCompleted: 0,
  fakesFound: 0,
  quizzesTaken: 0,
  bestQuizScore: 0,
  lessonsRead: [],
  badgesUnlocked: [],
  lastActive: new Date().toISOString(),
  streakDays: 1,
  level: 1,
  xp: 0,
}

function loadProgress(): UserProgress {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (!raw) return DEFAULT_PROGRESS
    return { ...DEFAULT_PROGRESS, ...JSON.parse(raw) }
  } catch { return DEFAULT_PROGRESS }
}

function saveProgress(p: UserProgress) {
  try { localStorage.setItem(STORAGE_KEY, JSON.stringify(p)) } catch {}
}

function xpForLevel(level: number): number {
  return level * 100
}

export function useProgress() {
  const [progress, setProgress] = useState<UserProgress>(loadProgress)
  const [newBadges, setNewBadges] = useState<Badge[]>([])

  const awardXP = useCallback((amount: number) => {
    setProgress(prev => {
      const xp = prev.xp + amount
      const level = Math.floor(xp / 100) + 1
      const updated = { ...prev, xp, level }
      saveProgress(updated)
      return updated
    })
  }, [])

  const checkBadges = useCallback((updated: UserProgress): BadgeId[] => {
    const earned: BadgeId[] = []
    const p = updated

    const checks: [BadgeId, boolean][] = [
      ['first_scan',    p.scansCompleted >= 1],
      ['five_scans',    p.scansCompleted >= 5],
      ['twenty_scans',  p.scansCompleted >= 20],
      ['first_quiz',    p.quizzesTaken >= 1],
      ['quiz_pass',     p.bestQuizScore >= 60],
      ['quiz_perfect',  p.bestQuizScore >= 100],
      ['first_lesson',  p.lessonsRead.length >= 1],
      ['all_lessons',   p.lessonsRead.length >= 6],
      ['fake_hunter',   p.fakesFound >= 5],
      ['weekly_streak', p.streakDays >= 7],
    ]

    for (const [id, met] of checks) {
      if (met && !p.badgesUnlocked.includes(id)) earned.push(id)
    }
    return earned
  }, [])

  const recordScan = useCallback((prediction: string, fileType: string) => {
    setProgress(prev => {
      const updated: UserProgress = {
        ...prev,
        scansCompleted: prev.scansCompleted + 1,
        fakesFound: prev.fakesFound + (prediction === 'FAKE' ? 1 : 0),
        xp: prev.xp + 10,
        lastActive: new Date().toISOString(),
      }
      // Special badges
      if (fileType === 'audio' && !prev.badgesUnlocked.includes('voice_check')) {
        updated.badgesUnlocked = [...updated.badgesUnlocked, 'voice_check']
      }
      if (fileType === 'url' && !prev.badgesUnlocked.includes('url_check')) {
        updated.badgesUnlocked = [...updated.badgesUnlocked, 'url_check']
      }
      const newlyEarned = checkBadges(updated)
      if (newlyEarned.length) {
        updated.badgesUnlocked = [...updated.badgesUnlocked, ...newlyEarned]
        updated.xp += newlyEarned.length * 50
        const newBadgeObjects = BADGE_DEFINITIONS
          .filter(b => newlyEarned.includes(b.id))
          .map(b => ({ ...b, unlocked: true, unlockedAt: new Date().toISOString() }))
        setNewBadges(newBadgeObjects)
        setTimeout(() => setNewBadges([]), 4000)
      }
      updated.level = Math.floor(updated.xp / 100) + 1
      saveProgress(updated)
      return updated
    })
  }, [checkBadges])

  const recordQuiz = useCallback((score: number, total: number) => {
    const pct = Math.round((score / total) * 100)
    setProgress(prev => {
      const updated: UserProgress = {
        ...prev,
        quizzesTaken: prev.quizzesTaken + 1,
        bestQuizScore: Math.max(prev.bestQuizScore, pct),
        xp: prev.xp + score * 15,
        lastActive: new Date().toISOString(),
      }
      const newlyEarned = checkBadges(updated)
      if (newlyEarned.length) {
        updated.badgesUnlocked = [...updated.badgesUnlocked, ...newlyEarned]
        updated.xp += newlyEarned.length * 50
        const newBadgeObjects = BADGE_DEFINITIONS
          .filter(b => newlyEarned.includes(b.id))
          .map(b => ({ ...b, unlocked: true, unlockedAt: new Date().toISOString() }))
        setNewBadges(newBadgeObjects)
        setTimeout(() => setNewBadges([]), 4000)
      }
      updated.level = Math.floor(updated.xp / 100) + 1
      saveProgress(updated)
      return updated
    })
  }, [checkBadges])

  const recordLesson = useCallback((lessonId: string) => {
    setProgress(prev => {
      if (prev.lessonsRead.includes(lessonId)) return prev
      const updated: UserProgress = {
        ...prev,
        lessonsRead: [...prev.lessonsRead, lessonId],
        xp: prev.xp + 20,
        lastActive: new Date().toISOString(),
      }
      if (!prev.badgesUnlocked.includes('first_lesson')) {
        updated.badgesUnlocked = [...updated.badgesUnlocked, 'first_lesson']
      }
      const newlyEarned = checkBadges(updated)
      if (newlyEarned.length) {
        updated.badgesUnlocked = [...updated.badgesUnlocked, ...newlyEarned]
        updated.xp += newlyEarned.length * 50
      }
      updated.level = Math.floor(updated.xp / 100) + 1
      saveProgress(updated)
      return updated
    })
  }, [checkBadges])

  const recordSpecialBadge = useCallback((id: BadgeId) => {
    setProgress(prev => {
      if (prev.badgesUnlocked.includes(id)) return prev
      const updated: UserProgress = {
        ...prev,
        badgesUnlocked: [...prev.badgesUnlocked, id],
        xp: prev.xp + 50,
        lastActive: new Date().toISOString(),
      }
      updated.level = Math.floor(updated.xp / 100) + 1
      const badgeObj = BADGE_DEFINITIONS.find(b => b.id === id)
      if (badgeObj) {
        setNewBadges([{ ...badgeObj, unlocked: true, unlockedAt: new Date().toISOString() }])
        setTimeout(() => setNewBadges([]), 4000)
      }
      saveProgress(updated)
      return updated
    })
  }, [])

  const getBadges = useCallback((): Badge[] => {
    return BADGE_DEFINITIONS.map(b => ({
      ...b,
      unlocked: progress.badgesUnlocked.includes(b.id),
    }))
  }, [progress.badgesUnlocked])

  const resetProgress = useCallback(() => {
    saveProgress(DEFAULT_PROGRESS)
    setProgress(DEFAULT_PROGRESS)
  }, [])

  return {
    progress,
    newBadges,
    recordScan,
    recordQuiz,
    recordLesson,
    recordSpecialBadge,
    getBadges,
    resetProgress,
    awardXP,
    xpForLevel,
  }
}
