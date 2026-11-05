import { useContext } from 'react'
import { motion } from 'framer-motion'
import { Trophy, Star, Zap, Target, RefreshCw, BookOpen, Shield, Brain } from 'lucide-react'
import { ProgressContext } from '../context/ProgressContext'
import { Badge } from '../types'
import { cn } from '../utils/helpers'

const PAGE = {
  initial: { opacity: 0, y: 20 },
  animate: { opacity: 1, y: 0, transition: { duration: 0.5 } },
  exit:    { opacity: 0, y: -20, transition: { duration: 0.3 } },
}

const fadeUp = {
  hidden: { opacity: 0, y: 20 },
  show: (i: number) => ({ opacity: 1, y: 0, transition: { delay: i * 0.06, duration: 0.4 } }),
}

export default function ProgressPage() {
  const ctx = useContext(ProgressContext)
  if (!ctx) return null
  const { progress, getBadges, resetProgress } = ctx
  const badges = getBadges() as Badge[]
  const unlockedBadges = badges.filter(b => b.unlocked)
  const lockedBadges   = badges.filter(b => !b.unlocked)
  const xpInLevel  = progress.xp % 100
  const xpNeeded   = 100
  const levelPct   = (xpInLevel / xpNeeded) * 100

  const stats = [
    { icon: Shield, label: 'Total Scans',      value: progress.scansCompleted,         color: 'text-brand-400' },
    { icon: Target, label: 'Fakes Found',       value: progress.fakesFound,             color: 'text-danger-400' },
    { icon: Brain,  label: 'Quizzes Taken',     value: progress.quizzesTaken,           color: 'text-pink-400' },
    { icon: BookOpen, label: 'Lessons Read',    value: progress.lessonsRead.length,     color: 'text-accent-400' },
    { icon: Star,   label: 'Best Quiz Score',   value: `${progress.bestQuizScore}%`,    color: 'text-warn-400' },
    { icon: Trophy, label: 'Badges Unlocked',   value: `${unlockedBadges.length}/${badges.length}`, color: 'text-purple-400' },
  ]

  return (
    <motion.div {...PAGE} className="min-h-screen pt-20 hero-bg">
      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-10">

        {/* Header */}
        <div className="flex items-start justify-between mb-8">
          <div>
            <div className="flex items-center gap-3 mb-2">
              <div className="w-10 h-10 rounded-xl bg-warn-600/20 flex items-center justify-center">
                <Trophy size={20} className="text-warn-400" />
              </div>
              <h1 className="font-display font-black text-2xl sm:text-4xl text-white">My Progress</h1>
            </div>
            <p className="text-slate-400 ml-14">Your digital literacy journey so far.</p>
          </div>
          <button onClick={resetProgress}
            className="flex items-center gap-1.5 text-xs text-slate-500 hover:text-danger-400 transition-colors mt-2">
            <RefreshCw size={12} /> Reset
          </button>
        </div>

        {/* Level card */}
        <div className="glass-dark rounded-3xl p-6 md:p-8 mb-8">
          <div className="flex flex-col sm:flex-row items-start sm:items-center gap-6">
            {/* Level badge */}
            <div className="relative shrink-0">
              <div className="w-20 h-20 rounded-2xl bg-gradient-to-br from-brand-500 to-purple-600 flex flex-col items-center justify-center shadow-lg">
                <span className="text-white text-xs font-medium opacity-70">LEVEL</span>
                <span className="text-white font-display font-black text-3xl leading-none">{progress.level}</span>
              </div>
            </div>
            <div className="flex-1 w-full">
              <div className="flex items-center justify-between mb-2">
                <span className="text-white font-semibold">
                  {progress.level < 3 ? 'Rookie Detective' :
                   progress.level < 6 ? 'Media Investigator' :
                   progress.level < 10 ? 'Deepfake Hunter' : 'Digital Guardian'}
                </span>
                <span className="text-slate-400 text-sm">{xpInLevel} / {xpNeeded} XP</span>
              </div>
              <div className="progress-bar h-3">
                <motion.div
                  className="h-full rounded-full bg-gradient-to-r from-brand-500 to-purple-500"
                  initial={{ width: 0 }}
                  animate={{ width: `${levelPct}%` }}
                  transition={{ duration: 1, ease: 'easeOut' }}
                />
              </div>
              <div className="flex items-center gap-4 mt-3 text-xs text-slate-500">
                <span className="flex items-center gap-1"><Zap size={11} className="text-warn-400" /> {progress.xp} total XP</span>
                <span className="flex items-center gap-1"><Star size={11} className="text-brand-400" /> {progress.streakDays} day streak</span>
              </div>
            </div>
          </div>
        </div>

        {/* Stats grid */}
        <div className="grid grid-cols-2 sm:grid-cols-3 gap-4 mb-8">
          {stats.map(({ icon: Icon, label, value, color }, i) => (
            <motion.div key={label} custom={i} variants={fadeUp} initial="hidden" animate="show"
              className="card-hover text-center">
              <Icon size={22} className={cn(color, 'mx-auto mb-2')} />
              <div className={cn('font-display font-black text-2xl', color)}>{value}</div>
              <div className="text-slate-500 text-xs mt-1">{label}</div>
            </motion.div>
          ))}
        </div>

        {/* Badges */}
        <div className="mb-6">
          <h2 className="text-white font-bold text-lg mb-4 flex items-center gap-2">
            <Trophy size={18} className="text-warn-400" />
            Badges ({unlockedBadges.length}/{badges.length})
          </h2>

          {/* Unlocked */}
          {unlockedBadges.length > 0 && (
            <div className="mb-6">
              <p className="text-xs text-slate-500 mb-3 uppercase tracking-wider">Unlocked</p>
              <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-3">
                {unlockedBadges.map((badge, i) => (
                  <motion.div key={badge.id} custom={i} variants={fadeUp} initial="hidden" animate="show"
                    className="card text-center relative overflow-hidden group">
                    <div className={`absolute inset-0 bg-gradient-to-br ${badge.color} opacity-5 group-hover:opacity-10 transition-opacity`} />
                    <div className={`w-14 h-14 rounded-2xl bg-gradient-to-br ${badge.color} flex items-center justify-center text-3xl mx-auto mb-3 shadow-lg`}>
                      {badge.icon}
                    </div>
                    <p className="text-white font-semibold text-sm">{badge.title}</p>
                    <p className="text-slate-500 text-xs mt-1 leading-tight">{badge.description}</p>
                    <div className="mt-2">
                      <span className="badge-safe text-[10px]">Earned</span>
                    </div>
                  </motion.div>
                ))}
              </div>
            </div>
          )}

          {/* Locked */}
          {lockedBadges.length > 0 && (
            <div>
              <p className="text-xs text-slate-500 mb-3 uppercase tracking-wider">Locked</p>
              <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-3">
                {lockedBadges.map((badge, i) => (
                  <motion.div key={badge.id} custom={i} variants={fadeUp} initial="hidden" animate="show"
                    className="card text-center opacity-40 grayscale">
                    <div className="w-14 h-14 rounded-2xl bg-slate-700 flex items-center justify-center text-3xl mx-auto mb-3">
                      {badge.icon}
                    </div>
                    <p className="text-slate-300 font-semibold text-sm">{badge.title}</p>
                    <p className="text-slate-600 text-xs mt-1 leading-tight">{badge.description}</p>
                    <div className="mt-2">
                      <span className="text-[10px] text-slate-600 bg-slate-800 px-2 py-0.5 rounded-full">Locked</span>
                    </div>
                  </motion.div>
                ))}
              </div>
            </div>
          )}

          {unlockedBadges.length === 0 && (
            <div className="text-center py-10 text-slate-600">
              <Trophy size={36} className="mx-auto mb-3 opacity-30" />
              <p className="text-sm">Start scanning, learning, and quizzing to earn badges!</p>
            </div>
          )}
        </div>
      </div>
    </motion.div>
  )
}
