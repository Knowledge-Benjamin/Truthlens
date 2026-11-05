import { motion } from 'framer-motion'
import {
  CheckCircle, AlertTriangle, HelpCircle, Clock, Cpu,
  BarChart2, ChevronDown, ChevronUp, ShieldCheck, ShieldAlert,
} from 'lucide-react'
import { useState } from 'react'
import { DetectionResult } from '../types'
import { cn, formatDuration } from '../utils/helpers'

interface Props {
  result: DetectionResult
  onReset: () => void
}

export default function AnalysisResult({ result, onReset }: Props) {
  const [expandDetails, setExpandDetails] = useState(false)

  const isFake = result.prediction === 'FAKE'
  const isReal = result.prediction === 'REAL'

  const verdictConfig = {
    FAKE: {
      icon: ShieldAlert,
      color: 'text-danger-400',
      bg: 'bg-danger-500/10',
      border: 'border-danger-500/30',
      glow: 'glow-red',
      label: 'Likely Fake / Manipulated',
      sub: 'This content shows strong signs of AI manipulation or deepfake generation.',
      badgeClass: 'badge-danger',
    },
    REAL: {
      icon: ShieldCheck,
      color: 'text-accent-400',
      bg: 'bg-accent-500/10',
      border: 'border-accent-500/30',
      glow: 'glow-green',
      label: 'Likely Authentic',
      sub: 'No significant signs of manipulation were detected.',
      badgeClass: 'badge-safe',
    },
    UNCERTAIN: {
      icon: HelpCircle,
      color: 'text-warn-400',
      bg: 'bg-warn-500/10',
      border: 'border-warn-500/30',
      glow: '',
      label: 'Uncertain',
      sub: 'The model could not make a confident determination. Use caution.',
      badgeClass: 'badge-warn',
    },
  }

  const cfg = verdictConfig[result.prediction]
  const VerdictIcon = cfg.icon

  const fakePercent = (result.fakeScore * 100).toFixed(1)
  const realPercent = (result.realScore * 100).toFixed(1)
  const confidencePercent = (result.confidence * 100).toFixed(1)

  return (
    <motion.div
      initial={{ opacity: 0, y: 30 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5, ease: [0.22, 1, 0.36, 1] }}
      className="space-y-5"
    >
      {/* Verdict Card */}
      <div className={cn('rounded-2xl p-6 border', cfg.bg, cfg.border, cfg.glow)}>
        <div className="flex items-start gap-4">
          <div className={cn('w-14 h-14 rounded-2xl flex items-center justify-center shrink-0', cfg.bg, 'border', cfg.border)}>
            <VerdictIcon size={28} className={cfg.color} />
          </div>
          <div className="flex-1 min-w-0">
            <div className="flex items-center gap-3 flex-wrap">
              <h3 className={cn('font-display font-bold text-2xl', cfg.color)}>{cfg.label}</h3>
              <span className={cfg.badgeClass}>{confidencePercent}% confident</span>
            </div>
            <p className="text-slate-400 text-sm mt-1">{cfg.sub}</p>
            <p className="text-slate-600 text-xs mt-2 truncate">📁 {result.filename}</p>
          </div>
        </div>

        {/* Score Bar */}
        <div className="mt-5 space-y-3">
          <div>
            <div className="flex justify-between text-xs text-slate-400 mb-1.5">
              <span className="flex items-center gap-1"><AlertTriangle size={11} className="text-danger-400" /> Fake probability</span>
              <span className="font-mono text-danger-400">{fakePercent}%</span>
            </div>
            <div className="progress-bar">
              <motion.div
                className="progress-fill bg-gradient-to-r from-danger-600 to-danger-400"
                initial={{ width: 0 }}
                animate={{ width: `${fakePercent}%` }}
                transition={{ delay: 0.3, duration: 1, ease: 'easeOut' }}
              />
            </div>
          </div>
          <div>
            <div className="flex justify-between text-xs text-slate-400 mb-1.5">
              <span className="flex items-center gap-1"><CheckCircle size={11} className="text-accent-400" /> Real probability</span>
              <span className="font-mono text-accent-400">{realPercent}%</span>
            </div>
            <div className="progress-bar">
              <motion.div
                className="progress-fill bg-gradient-to-r from-accent-600 to-accent-400"
                initial={{ width: 0 }}
                animate={{ width: `${realPercent}%` }}
                transition={{ delay: 0.4, duration: 1, ease: 'easeOut' }}
              />
            </div>
          </div>
        </div>
      </div>

      {/* Meta */}
      <div className="grid grid-cols-2 gap-3">
        {[
          { icon: Clock, label: 'Processing time', value: formatDuration(result.processingTime) },
          { icon: Cpu, label: 'Model used', value: result.modelUsed },
        ].map(({ icon: Icon, label, value }) => (
          <div key={label} className="glass rounded-xl p-4 flex items-center gap-3">
            <Icon size={16} className="text-slate-500 shrink-0" />
            <div className="min-w-0">
              <p className="text-slate-500 text-xs">{label}</p>
              <p className="text-white text-sm font-medium truncate">{value}</p>
            </div>
          </div>
        ))}
      </div>

      {/* Detailed breakdown toggle */}
      <div className="glass rounded-2xl overflow-hidden">
        <button
          onClick={() => setExpandDetails(!expandDetails)}
          className="w-full flex items-center justify-between px-5 py-4 hover:bg-white/5 transition-colors"
        >
          <div className="flex items-center gap-2 text-white font-medium">
            <BarChart2 size={16} className="text-brand-400" />
            Detailed Analysis Breakdown
          </div>
          {expandDetails ? <ChevronUp size={16} className="text-slate-400" /> : <ChevronDown size={16} className="text-slate-400" />}
        </button>

        {expandDetails && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            className="px-5 pb-5 space-y-4 border-t border-white/8"
          >
            {result.analysisDetails.map((detail, i) => (
              <motion.div
                key={detail.label}
                initial={{ opacity: 0, x: -10 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: i * 0.08 }}
                className="space-y-1.5"
              >
                <div className="flex items-center justify-between text-sm">
                  <span className="flex items-center gap-2 text-slate-300">
                    <span>{detail.icon}</span>
                    {detail.label}
                  </span>
                  <span className={cn(
                    'font-mono text-xs',
                    detail.score > 0.7 ? 'text-danger-400' : detail.score > 0.4 ? 'text-warn-400' : 'text-accent-400'
                  )}>
                    {(detail.score * 100).toFixed(0)}%
                  </span>
                </div>
                <div className="progress-bar">
                  <motion.div
                    className={cn(
                      'progress-fill',
                      detail.score > 0.7 ? 'bg-danger-500' : detail.score > 0.4 ? 'bg-warn-500' : 'bg-accent-500'
                    )}
                    initial={{ width: 0 }}
                    animate={{ width: `${detail.score * 100}%` }}
                    transition={{ delay: 0.5 + i * 0.1, duration: 0.8 }}
                  />
                </div>
                <p className="text-slate-600 text-xs">{detail.description}</p>
              </motion.div>
            ))}
          </motion.div>
        )}
      </div>

      {/* Safety message */}
      {isFake && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.6 }}
          className="rounded-xl p-4 bg-warn-500/10 border border-warn-500/20 flex items-start gap-3"
        >
          <AlertTriangle size={16} className="text-warn-400 mt-0.5 shrink-0" />
          <div className="text-sm">
            <p className="text-warn-300 font-medium mb-0.5">What should you do?</p>
            <p className="text-slate-400">
              Don't share this content. Report it on the platform where you found it. If it involves someone you know, tell a trusted adult.
            </p>
          </div>
        </motion.div>
      )}
      {isReal && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.6 }}
          className="rounded-xl p-4 bg-accent-500/10 border border-accent-500/20 flex items-start gap-3"
        >
          <CheckCircle size={16} className="text-accent-400 mt-0.5 shrink-0" />
          <div className="text-sm">
            <p className="text-accent-300 font-medium mb-0.5">Good news!</p>
            <p className="text-slate-400">
              This media appears to be authentic. However, always verify information from multiple sources — no AI is 100% perfect.
            </p>
          </div>
        </motion.div>
      )}

      {/* Reset */}
      <button onClick={onReset} className="btn-secondary w-full">
        ← Analyse Another File
      </button>
    </motion.div>
  )
}
