import { useState, useContext } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { Link } from 'react-router-dom'
import {
  Brain, CheckCircle, XCircle, Trophy, RotateCcw,
  ArrowRight, Sparkles, Star, Award, Target,
} from 'lucide-react'
import { QuizQuestion } from '../types'
import { cn } from '../utils/helpers'
import { useLanguage } from '../i18n/LanguageContext'
import { ProgressContext } from '../context/ProgressContext'

const questions: QuizQuestion[] = [
  {
    id: 1,
    question: 'You see a video of a famous person saying something shocking. What should you do first?',
    options: ['Share it immediately to warn others', 'Verify the source and check if reputable news is reporting it', 'Comment your opinion on the post', 'Believe it because it looks real'],
    correctIndex: 1,
    explanation: 'Always verify the source before sharing or believing. Real news will be reported by multiple reputable outlets.',
    difficulty: 'easy',
    category: 'Verification',
  },
  {
    id: 2,
    question: 'Which of these is a common sign of a deepfake?',
    options: ['Perfect lighting', 'Unnatural or no blinking', 'Clear, crisp audio', 'Smooth camera movement'],
    correctIndex: 1,
    explanation: 'AI-generated faces often blink too regularly or not at all. Real people blink randomly and naturally.',
    difficulty: 'easy',
    category: 'Visual Clues',
  },
  {
    id: 3,
    question: 'What does "content provenance" mean?',
    options: ['The country where content was filmed', 'A record of where content came from and if it was edited', 'The file size of an image', 'The number of times content was shared'],
    correctIndex: 1,
    explanation: 'Provenance tracks the origin and edit history of media. The C2PA standard embeds this as signed metadata.',
    difficulty: 'medium',
    category: 'Provenance',
  },
  {
    id: 4,
    question: 'A friend messages you urgently asking for money via a new number. What\'s the safest action?',
    options: ['Send it right away — they\'re your friend', 'Ask them a question only the real person would know', 'Ignore the message completely', 'Send half the amount to be safe'],
    correctIndex: 1,
    explanation: 'Verify identity through a second channel or by asking something only the real person would know. Voice cloning can mimic voices.',
    difficulty: 'medium',
    category: 'Impersonation',
  },
  {
    id: 5,
    question: 'AI image generators commonly struggle with which of the following?',
    options: ['Landscapes', 'Text, hands, and fine details', 'Colors', 'Simple shapes'],
    correctIndex: 1,
    explanation: 'AI models frequently produce garbled text, extra fingers, and distorted hands. These are the easiest artifacts to spot.',
    difficulty: 'easy',
    category: 'AI-Generated',
  },
  {
    id: 6,
    question: 'You spot fake content about someone you know. What should you NOT do?',
    options: ['Report it to the platform', 'Tell a trusted adult', 'Share it to warn others', 'Use Deepfake A.I Detector to confirm your suspicion'],
    correctIndex: 2,
    explanation: 'Sharing fake content — even to "warn" people — spreads it further. Report and flag instead of forwarding.',
    difficulty: 'easy',
    category: 'Safety',
  },
  {
    id: 7,
    question: 'What is a GAN?',
    options: ['A type of social media post', 'A Generative Adversarial Network used to create deepfakes', 'A video editing app', 'A type of computer virus'],
    correctIndex: 1,
    explanation: 'GANs (Generative Adversarial Networks) are AI models that pit a generator against a discriminator to create realistic fake content.',
    difficulty: 'hard',
    category: 'Technical',
  },
  {
    id: 8,
    question: 'Why might a "real" result from a deepfake detector still be misleading?',
    options: ['The detector is always wrong', 'Content can be real but presented out of context', 'Real images never fool detectors', 'Detectors only work on videos'],
    correctIndex: 1,
    explanation: 'An authentic image can still be misleading if captioned incorrectly or shown out of context. Always verify the full story.',
    difficulty: 'medium',
    category: 'Critical Thinking',
  },
  {
    id: 9,
    question: 'What does the C2PA standard do?',
    options: ['Deletes fake content automatically', 'Embeds signed metadata proving content origin and edits', 'Blocks deepfake websites', 'Rates content trustworthiness 1-10'],
    correctIndex: 1,
    explanation: 'C2PA cryptographically signs metadata into media files so viewers can verify origin and modification history.',
    difficulty: 'hard',
    category: 'Provenance',
  },
  {
    id: 10,
    question: 'Which is the best first step when unsure about online content?',
    options: ['React emotionally', 'Pause and verify before sharing or believing', 'Delete your account', 'Forward to all contacts'],
    correctIndex: 1,
    explanation: 'Pausing gives your brain time to engage critical thinking. Verify, then decide. This is the #1 digital literacy habit.',
    difficulty: 'easy',
    category: 'Mindset',
  },
]

export default function QuizPage() {
  const [started, setStarted] = useState(false)
  const [currentQ, setCurrentQ] = useState(0)
  const [selected, setSelected] = useState<number | null>(null)
  const [answered, setAnswered] = useState(false)
  const [score, setScore] = useState(0)
  const [answers, setAnswers] = useState<boolean[]>([])
  const [finished, setFinished] = useState(false)
  const { t } = useLanguage()
  const progressCtx = useContext(ProgressContext)

  const question = questions[currentQ]
  const total = questions.length

  const handleStart = () => {
    setStarted(true)
    setCurrentQ(0)
    setSelected(null)
    setAnswered(false)
    setScore(0)
    setAnswers([])
    setFinished(false)
  }

  const handleSelect = (index: number) => {
    if (answered) return
    setSelected(index)
    setAnswered(true)

    const isCorrect = index === question.correctIndex
    if (isCorrect) setScore((s) => s + 1)
    setAnswers((a) => [...a, isCorrect])
  }

  const handleNext = () => {
    if (currentQ + 1 < total) {
      setCurrentQ((q) => q + 1)
      setSelected(null)
      setAnswered(false)
    } else {
      setFinished(true)
      progressCtx?.recordQuiz(score, total)
    }
  }

  const getScoreMessage = () => {
    const pct = (score / total) * 100
    if (pct === 100) return { title: 'Perfect Score! 🎉', msg: 'You\'re a digital literacy champion. You can spot fakes like a pro.' }
    if (pct >= 80) return { title: 'Excellent! ⭐', msg: 'You have strong digital literacy skills. Keep it up!' }
    if (pct >= 60) return { title: 'Good Job! 👍', msg: 'You\'re on the right track. Review the lessons to sharpen your skills.' }
    if (pct >= 40) return { title: 'Keep Learning! 📚', msg: 'You\'ve got the basics. Check out the Learning Hub to improve.' }
    return { title: 'Let\'s Learn Together! 💪', msg: 'Everyone starts somewhere. Visit the Learning Hub and try again!' }
  }

  // Intro screen
  if (!started) {
    return (
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        exit={{ opacity: 0, y: -20 }}
        className="min-h-screen pt-20 hero-bg flex items-center"
      >
        <div className="max-w-2xl mx-auto px-4 py-12 text-center w-full">
            <div className="inline-flex items-center gap-2 glass px-4 py-2 rounded-full text-sm text-pink-300 mb-6 border-pink-500/30">
            <Brain size={14} />
            {t.quiz_badge}
          </div>

          <motion.div
            initial={{ scale: 0.8, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            transition={{ delay: 0.2, type: 'spring' }}
            className="w-20 h-20 rounded-3xl bg-gradient-to-br from-pink-500 to-rose-600 flex items-center justify-center mx-auto mb-6 glow-brand"
          >
            <Brain size={36} className="text-white" />
          </motion.div>

          <h1 className="font-display font-black text-3xl sm:text-4xl md:text-5xl text-white mb-4">
            {t.quiz_title1} <span className="gradient-text">{t.quiz_title2}</span>
          </h1>
          <p className="text-slate-400 text-lg mb-8 max-w-lg mx-auto">
            {total} {t.quiz_sub}
          </p>

          <div className="flex items-center justify-center gap-6 mb-10 text-sm text-slate-500">
            <span className="flex items-center gap-1.5"><Target size={14} className="text-brand-400" /> {total} Questions</span>
            <span className="w-px h-4 bg-slate-700" />
            <span className="flex items-center gap-1.5"><Star size={14} className="text-warn-400" /> Instant Feedback</span>
            <span className="w-px h-4 bg-slate-700" />
            <span className="flex items-center gap-1.5"><Award size={14} className="text-accent-400" /> Score Tracking</span>
          </div>

          <button onClick={handleStart} className="btn-primary text-base sm:text-lg px-8 sm:px-10 py-3 sm:py-4 inline-flex items-center gap-2 group w-full sm:w-auto justify-center">
            {t.quiz_start}
            <ArrowRight size={18} className="group-hover:translate-x-1 transition-transform" />
          </button>
        </div>
      </motion.div>
    )
  }

  // Results screen
  if (finished) {
    const pct = Math.round((score / total) * 100)
    const { title, msg } = getScoreMessage()

    return (
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="min-h-screen pt-20 hero-bg flex items-center"
      >
        <div className="max-w-2xl mx-auto px-4 py-12 w-full">
          <motion.div
            initial={{ scale: 0.9, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            transition={{ type: 'spring', delay: 0.2 }}
            className="text-center"
          >
            {/* Trophy / Score Ring */}
            <div className="relative w-36 h-36 mx-auto mb-6">
              <svg className="w-full h-full -rotate-90" viewBox="0 0 120 120">
                <circle cx="60" cy="60" r="52" fill="none" stroke="#1e293b" strokeWidth="8" />
                <motion.circle
                  cx="60" cy="60" r="52" fill="none" stroke="url(#scoreGrad)" strokeWidth="8" strokeLinecap="round"
                  initial={{ pathLength: 0 }}
                  animate={{ pathLength: pct / 100 }}
                  transition={{ duration: 1.2, ease: 'easeOut', delay: 0.3 }}
                  pathLength={1}
                  strokeDasharray="326.7"
                  strokeDashoffset={326.7 - (326.7 * pct / 100)}
                />
                <defs>
                  <linearGradient id="scoreGrad" x1="0%" y1="0%" x2="100%" y2="0%">
                    <stop offset="0%" stopColor={pct >= 60 ? '#10b981' : pct >= 40 ? '#f59e0b' : '#ef4444'} />
                    <stop offset="100%" stopColor={pct >= 60 ? '#34d399' : pct >= 40 ? '#fbbf24' : '#f87171'} />
                  </linearGradient>
                </defs>
              </svg>
              <div className="absolute inset-0 flex flex-col items-center justify-center">
                <Trophy size={28} className={cn(pct >= 60 ? 'text-accent-400' : pct >= 40 ? 'text-warn-400' : 'text-danger-400')} />
                <span className="font-display font-black text-3xl text-white mt-1">{pct}%</span>
              </div>
            </div>

            <h1 className="font-display font-bold text-2xl sm:text-3xl text-white mb-2">{title}</h1>
            <p className="text-slate-400 mb-2">
              {t.quiz_results_scored} <span className="text-white font-bold">{score}</span> {t.quiz_results_of} <span className="text-white font-bold">{total}</span>
            </p>
            <p className="text-slate-500 text-sm max-w-md mx-auto mb-8">{msg}</p>

            {/* Answer Review */}
            <div className="glass-dark rounded-2xl p-5 mb-8 text-left">
              <h3 className="text-white font-semibold text-sm mb-3 flex items-center gap-2">
                <Sparkles size={14} className="text-brand-400" />
                Your Answers
              </h3>
              <div className="grid grid-cols-5 sm:grid-cols-10 gap-2">
                {answers.map((correct, i) => (
                  <div
                    key={i}
                    className={cn(
                      'aspect-square rounded-lg flex items-center justify-center text-xs font-bold',
                      correct ? 'bg-accent-500/20 text-accent-400 border border-accent-500/30' : 'bg-danger-500/20 text-danger-400 border border-danger-500/30'
                    )}
                  >
                    {correct ? <CheckCircle size={16} /> : <XCircle size={16} />}
                  </div>
                ))}
              </div>
            </div>

            {/* Actions */}
            <div className="flex flex-col sm:flex-row items-center justify-center gap-3">
              <button onClick={handleStart} className="btn-primary flex items-center gap-2">
                <RotateCcw size={16} /> {t.quiz_retry}
              </button>
              <Link to="/learn" className="btn-secondary flex items-center gap-2">
                {t.quiz_review}
              </Link>
            </div>
          </motion.div>
        </div>
      </motion.div>
    )
  }

  // Quiz question screen
  const progress = ((currentQ + (answered ? 1 : 0)) / total) * 100

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -20 }}
      className="min-h-screen pt-20 hero-bg"
    >
      <div className="max-w-2xl mx-auto px-4 py-12">
        {/* Progress */}
        <div className="mb-8">
          <div className="flex items-center justify-between text-sm text-slate-400 mb-2">
            <span>{t.quiz_question_of} {currentQ + 1} of {total}</span>
            <span className="flex items-center gap-1.5"><Star size={13} className="text-warn-400" /> {t.quiz_score}: {score}</span>
          </div>
          <div className="progress-bar">
            <motion.div
              className="progress-fill bg-gradient-to-r from-pink-500 to-rose-500"
              animate={{ width: `${progress}%` }}
              transition={{ duration: 0.4 }}
            />
          </div>
        </div>

        <AnimatePresence mode="wait">
          <motion.div
            key={currentQ}
            initial={{ opacity: 0, x: 30 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -30 }}
            transition={{ duration: 0.3 }}
          >
            {/* Category badge */}
            <div className="flex items-center gap-2 mb-4">
              <span className="text-xs text-slate-400 bg-white/5 px-2.5 py-1 rounded-full">{question.category}</span>
              <span className={cn(
                'text-xs px-2.5 py-1 rounded-full',
                question.difficulty === 'easy' && 'text-accent-400 bg-accent-500/10',
                question.difficulty === 'medium' && 'text-warn-400 bg-warn-500/10',
                question.difficulty === 'hard' && 'text-danger-400 bg-danger-500/10',
              )}>
                {question.difficulty}
              </span>
            </div>

            {/* Question */}
            <h2 className="font-display font-bold text-lg sm:text-2xl text-white mb-6 sm:mb-8 leading-snug">
              {question.question}
            </h2>

            {/* Options */}
            <div className="space-y-3 mb-6">
              {question.options.map((option, index) => {
                const isSelected = selected === index
                const isCorrect = index === question.correctIndex
                const showCorrect = answered && isCorrect
                const showWrong = answered && isSelected && !isCorrect

                return (
                  <button
                    key={index}
                    onClick={() => handleSelect(index)}
                    disabled={answered}
                    className={cn(
                      'w-full text-left p-3 sm:p-4 rounded-xl border transition-all duration-200 flex items-center gap-3',
                      !answered && 'border-slate-700 bg-slate-800/40 hover:border-brand-500/50 hover:bg-slate-800/70 cursor-pointer',
                      showCorrect && 'border-accent-500/50 bg-accent-500/10',
                      showWrong && 'border-danger-500/50 bg-danger-500/10',
                      answered && !showCorrect && !showWrong && 'border-slate-700 bg-slate-800/20 opacity-50',
                    )}
                  >
                    <div className={cn(
                      'w-8 h-8 rounded-lg flex items-center justify-center text-sm font-bold shrink-0 transition-colors',
                      !answered && 'bg-slate-700 text-slate-400',
                      showCorrect && 'bg-accent-500/20 text-accent-400',
                      showWrong && 'bg-danger-500/20 text-danger-400',
                      answered && !showCorrect && !showWrong && 'bg-slate-800 text-slate-600',
                    )}>
                      {showCorrect ? <CheckCircle size={16} /> : showWrong ? <XCircle size={16} /> : String.fromCharCode(65 + index)}
                    </div>
                    <span className={cn(
                      'text-sm flex-1',
                      showCorrect ? 'text-accent-300 font-medium' : showWrong ? 'text-danger-300' : 'text-slate-300'
                    )}>
                      {option}
                    </span>
                  </button>
                )
              })}
            </div>

            {/* Explanation */}
            <AnimatePresence>
              {answered && (
                <motion.div
                  initial={{ opacity: 0, y: 10, height: 0 }}
                  animate={{ opacity: 1, y: 0, height: 'auto' }}
                  exit={{ opacity: 0, y: 10, height: 0 }}
                  className={cn(
                    'rounded-xl p-4 border mb-6',
                    selected === question.correctIndex
                      ? 'bg-accent-500/10 border-accent-500/20'
                      : 'bg-warn-500/10 border-warn-500/20'
                  )}
                >
                  <p className={cn('font-medium text-sm mb-1 flex items-center gap-1.5',
                    selected === question.correctIndex ? 'text-accent-300' : 'text-warn-300')}>
                    {selected === question.correctIndex ? <><CheckCircle size={14} /> {t.quiz_correct}</> : <><XCircle size={14} /> {t.quiz_wrong}</>}
                  </p>
                  <p className="text-slate-400 text-sm leading-relaxed">{question.explanation}</p>
                </motion.div>
              )}
            </AnimatePresence>

            {/* Next button */}
            <AnimatePresence>
              {answered && (
                <motion.button
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  onClick={handleNext}
                  className="btn-primary w-full flex items-center justify-center gap-2"
                >
                  {currentQ + 1 < total ? t.quiz_next : t.quiz_see_results}
                  <ArrowRight size={16} />
                </motion.button>
              )}
            </AnimatePresence>
          </motion.div>
        </AnimatePresence>
      </div>
    </motion.div>
  )
}
