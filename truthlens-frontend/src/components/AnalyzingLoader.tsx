import { motion } from 'framer-motion'
import { Cpu, Eye, ScanLine, Layers } from 'lucide-react'
import { useLanguage } from '../i18n/LanguageContext'

export default function AnalyzingLoader({ progress }: { progress: number }) {
  const { t } = useLanguage()
  const steps = [
    { icon: Eye,      label: t.analyze_step1 },
    { icon: Layers,   label: t.analyze_step2 },
    { icon: Cpu,      label: t.analyze_step3 },
    { icon: ScanLine, label: t.analyze_step4 },
  ]
  const stepIndex = Math.min(Math.floor(progress / 25), steps.length - 1)
  const { icon: StepIcon, label } = steps[stepIndex]

  return (
    <motion.div
      initial={{ opacity: 0, scale: 0.95 }}
      animate={{ opacity: 1, scale: 1 }}
      exit={{ opacity: 0, scale: 0.95 }}
      className="flex flex-col items-center justify-center py-12 text-center"
    >
      {/* Animated ring */}
      <div className="relative mb-8">
        <div className="w-24 h-24 rounded-full border-2 border-slate-700 flex items-center justify-center">
          <motion.div
            animate={{ rotate: 360 }}
            transition={{ duration: 2, repeat: Infinity, ease: 'linear' }}
            className="absolute inset-0 rounded-full border-2 border-transparent border-t-brand-500"
          />
          <StepIcon size={28} className="text-brand-400" />
        </div>
        {/* Ping rings */}
        <div className="absolute inset-0 rounded-full border border-brand-500/20 ping-slow" />
      </div>

      <h3 className="font-display font-bold text-xl text-white mb-2">{t.analyzing_title}</h3>
      <p className="text-slate-400 text-sm mb-8">{label}</p>

      {/* Progress bar */}
      <div className="w-full max-w-xs">
        <div className="flex justify-between text-xs text-slate-500 mb-2">
          <span>Progress</span>
          <span>{progress}%</span>
        </div>
        <div className="progress-bar">
          <motion.div
            className="progress-fill bg-gradient-to-r from-brand-600 to-purple-500"
            animate={{ width: `${progress}%` }}
            transition={{ duration: 0.5, ease: 'easeOut' }}
          />
        </div>
      </div>

      {/* Steps */}
      <div className="mt-8 space-y-2 w-full max-w-xs">
        {steps.map(({ label: stepLabel, icon: Icon }, i) => (
          <div
            key={i}
            className={`flex items-center gap-2.5 text-xs transition-all duration-300 ${
              i < stepIndex
                ? 'text-accent-400'
                : i === stepIndex
                  ? 'text-brand-400'
                  : 'text-slate-700'
            }`}
          >
            <Icon size={12} className="shrink-0" />
            {stepLabel}
            {i < stepIndex && (
              <motion.span
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                className="ml-auto text-accent-500"
              >
                ✓
              </motion.span>
            )}
          </div>
        ))}
      </div>
    </motion.div>
  )
}
