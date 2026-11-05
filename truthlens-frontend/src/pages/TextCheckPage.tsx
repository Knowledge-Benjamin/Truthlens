import { useState, useContext } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { FileText, Search, AlertTriangle, CheckCircle, ExternalLink, Info, RotateCcw } from 'lucide-react'
import axios from 'axios'
import { TextCheckResult } from '../types'
import { ProgressContext } from '../context/ProgressContext'
import { cn } from '../utils/helpers'

const PAGE = {
  initial: { opacity: 0, y: 20 },
  animate: { opacity: 1, y: 0, transition: { duration: 0.5 } },
  exit:    { opacity: 0, y: -20, transition: { duration: 0.3 } },
}

const FACT_CHECK_SITES = [
  { name: 'Africa Check',    url: 'https://africacheck.org',                   icon: '🌍', desc: 'Leading fact-checker for Africa' },
  { name: 'Snopes',          url: 'https://snopes.com',                        icon: '🔍', desc: 'Global myth-busting site' },
  { name: 'PolitiFact',      url: 'https://politifact.com',                    icon: '📊', desc: 'Political fact checking' },
  { name: 'FactCheck.org',   url: 'https://factcheck.org',                     icon: '✅', desc: 'Non-partisan fact checking' },
  { name: 'Full Fact (UK)',  url: 'https://fullfact.org',                      icon: '🇬🇧', desc: 'UK independent fact checker' },
  { name: 'Reuters Fact Check', url: 'https://reuters.com/fact-check',         icon: '📰', desc: 'News agency fact checks' },
]

const EXAMPLE_TEXTS = [
  "Scientists have discovered that drinking hot water with lemon cures all diseases and boosts immunity by 500%.",
  "The government announced today that all schools will be closed permanently starting next month due to budget cuts.",
]

export default function TextCheckPage() {
  const [text, setText]     = useState('')
  const [status, setStatus] = useState<'idle' | 'checking' | 'done' | 'error'>('idle')
  const [result, setResult] = useState<TextCheckResult | null>(null)
  const [error, setError]   = useState('')
  const ctx = useContext(ProgressContext)

  const checkText = async () => {
    if (text.trim().length < 20) { setError('Please enter at least 20 characters of text.'); return }
    setError('')
    setStatus('checking')
    setResult(null)
    try {
      const res = await axios.post('/api/check-text', { text: text.trim() }, { timeout: 30000 })
      setResult(res.data)
      setStatus('done')
      ctx?.recordSpecialBadge?.('text_checker')
    } catch (e) {
      setStatus('error')
      setError('Text analysis failed. Please try again.')
    }
  }

  const reset = () => { setStatus('idle'); setResult(null); setError(''); setText('') }

  const ICON_MAP: Record<string, string> = {
    book: '📚', ruler: '📏', link: '🔗', chat: '💬',
    mic: '🎤', wave: '🎵', silence: '🔇', voice: '🗣️',
  }
  const getIcon = (icon: string) => ICON_MAP[icon] ?? icon
  const predColor = (p?: string) => p === 'AI_GENERATED' ? 'text-danger-400' : p === 'HUMAN_WRITTEN' ? 'text-accent-400' : 'text-warn-400'
  const predBg    = (p?: string) => p === 'AI_GENERATED' ? 'bg-danger-500/10 border-danger-500/30' : p === 'HUMAN_WRITTEN' ? 'bg-accent-500/10 border-accent-500/30' : 'bg-warn-500/10 border-warn-500/30'

  return (
    <motion.div {...PAGE} className="min-h-screen pt-20 hero-bg">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-10">

        {/* Header */}
        <div className="mb-8">
          <div className="flex items-center gap-3 mb-2">
            <div className="w-10 h-10 rounded-xl bg-amber-600/20 flex items-center justify-center">
              <FileText size={20} className="text-amber-400" />
            </div>
            <h1 className="font-display font-black text-2xl sm:text-4xl text-white">Text Fact-Checker</h1>
          </div>
          <p className="text-slate-400 ml-14">Paste an article, post, or message to check if it was written by AI.</p>
        </div>

        <div className="grid lg:grid-cols-3 gap-6">
          {/* Input / Result */}
          <div className="lg:col-span-2 space-y-4">
            <div className="glass-dark rounded-3xl p-6">
              <AnimatePresence mode="wait">
                {status === 'done' && result ? (
                  <motion.div key="result" initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="space-y-4">
                    {/* Verdict */}
                    <div className={cn('rounded-2xl p-5 border', predBg(result.prediction))}>
                      <div className="flex items-center gap-3 mb-3">
                        <span className="text-3xl">
                          {result.prediction === 'AI_GENERATED' ? '🤖' : result.prediction === 'HUMAN_WRITTEN' ? '✍️' : '🤔'}
                        </span>
                        <div>
                          <h3 className={cn('font-display font-bold text-xl', predColor(result.prediction))}>
                            {result.prediction === 'AI_GENERATED' ? 'Likely AI-Generated'
                             : result.prediction === 'HUMAN_WRITTEN' ? 'Likely Human-Written'
                             : 'Uncertain'}
                          </h3>
                          <p className="text-slate-400 text-sm">{(result.confidence * 100).toFixed(1)}% confident · {result.modelUsed}</p>
                        </div>
                      </div>
                      {/* Score bars */}
                      <div className="space-y-2">
                        <div>
                          <div className="flex justify-between text-xs text-slate-400 mb-1">
                            <span>🤖 AI probability</span><span className="text-danger-400">{(result.aiScore * 100).toFixed(1)}%</span>
                          </div>
                          <div className="progress-bar">
                            <motion.div className="progress-fill bg-danger-500" initial={{ width: 0 }} animate={{ width: `${result.aiScore * 100}%` }} transition={{ duration: 1 }} />
                          </div>
                        </div>
                        <div>
                          <div className="flex justify-between text-xs text-slate-400 mb-1">
                            <span>✍️ Human probability</span><span className="text-accent-400">{(result.humanScore * 100).toFixed(1)}%</span>
                          </div>
                          <div className="progress-bar">
                            <motion.div className="progress-fill bg-accent-500" initial={{ width: 0 }} animate={{ width: `${result.humanScore * 100}%` }} transition={{ duration: 1, delay: 0.2 }} />
                          </div>
                        </div>
                      </div>
                    </div>

                    {/* Signal breakdown */}
                    {result.signals.length > 0 && (
                      <div className="glass rounded-xl p-4 space-y-3">
                        <h4 className="text-white font-semibold text-sm">Analysis Signals</h4>
                        {result.signals.map((sig, i) => (
                          <div key={i}>
                            <div className="flex items-center justify-between text-xs text-slate-400 mb-1">
                              <span className="flex items-center gap-1.5"><span>{getIcon(sig.icon)}</span>{sig.label}</span>
                              <span className={sig.score > 0.6 ? 'text-danger-400' : sig.score > 0.4 ? 'text-warn-400' : 'text-accent-400'}>
                                {(sig.score * 100).toFixed(0)}%
                              </span>
                            </div>
                            <div className="progress-bar">
                              <motion.div
                                className={cn('progress-fill', sig.score > 0.6 ? 'bg-danger-500' : sig.score > 0.4 ? 'bg-warn-500' : 'bg-accent-500')}
                                initial={{ width: 0 }}
                                animate={{ width: `${sig.score * 100}%` }}
                                transition={{ delay: i * 0.1, duration: 0.8 }}
                              />
                            </div>
                            <p className="text-slate-600 text-xs mt-0.5">{sig.description}</p>
                          </div>
                        ))}
                      </div>
                    )}

                    {/* AI-generated warning */}
                    {result.prediction === 'AI_GENERATED' && (
                      <div className="rounded-xl p-4 bg-warn-500/10 border border-warn-500/20 flex items-start gap-3">
                        <AlertTriangle size={14} className="text-warn-400 mt-0.5 shrink-0" />
                        <div className="text-sm">
                          <p className="text-warn-300 font-medium mb-0.5">This text may be AI-generated</p>
                          <p className="text-slate-400 text-xs">AI-written content is not always wrong, but be extra careful about its claims. Verify facts using the fact-check sites below before sharing.</p>
                        </div>
                      </div>
                    )}

                    <button onClick={reset} className="btn-secondary w-full flex items-center justify-center gap-2">
                      <RotateCcw size={14} /> Check Another Text
                    </button>
                  </motion.div>
                ) : (
                  <motion.div key="input" initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
                    <label className="block text-sm text-slate-400 mb-2 font-medium">
                      Paste text to check <span className="text-slate-600">(min 20 characters)</span>
                    </label>
                    <textarea
                      value={text}
                      onChange={e => setText(e.target.value)}
                      rows={7}
                      placeholder="Paste an article, social media post, WhatsApp message, or any text here..."
                      className="input resize-none mb-3"
                    />
                    <div className="flex items-center justify-between mb-4">
                      <span className="text-xs text-slate-600">{text.length} characters</span>
                      <span className={cn('text-xs', text.length >= 20 ? 'text-accent-400' : 'text-slate-600')}>
                        {text.length >= 20 ? '✓ Ready to check' : `${20 - text.length} more needed`}
                      </span>
                    </div>
                    <AnimatePresence>
                      {error && (
                        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
                          className="flex items-center gap-2 text-danger-400 text-sm bg-danger-500/10 border border-danger-500/20 rounded-xl px-4 py-3 mb-3">
                          <AlertTriangle size={14} className="shrink-0" />{error}
                        </motion.div>
                      )}
                    </AnimatePresence>
                    <button onClick={checkText} disabled={status === 'checking' || text.length < 20}
                      className="btn-primary w-full flex items-center justify-center gap-2 disabled:opacity-50">
                      {status === 'checking'
                        ? <><div className="w-4 h-4 rounded-full border-2 border-transparent border-t-white animate-spin" /> Analysing…</>
                        : <><Search size={16} /> Check This Text</>}
                    </button>

                    {/* Examples */}
                    <div className="mt-4">
                      <p className="text-xs text-slate-600 mb-2">Try an example:</p>
                      <div className="space-y-2">
                        {EXAMPLE_TEXTS.map((t, i) => (
                          <button key={i} onClick={() => setText(t)}
                            className="w-full text-left text-xs text-slate-400 bg-white/5 rounded-xl px-3 py-2.5 hover:bg-white/10 transition-colors line-clamp-2">
                            {t}
                          </button>
                        ))}
                      </div>
                    </div>
                  </motion.div>
                )}
              </AnimatePresence>
            </div>
          </div>

          {/* Fact-check sites sidebar */}
          <div className="space-y-4">
            <div className="glass-dark rounded-2xl p-5">
              <h3 className="text-white font-semibold mb-4 flex items-center gap-2 text-sm">
                <CheckCircle size={14} className="text-accent-400" /> Verify With These Sites
              </h3>
              <div className="space-y-2">
                {FACT_CHECK_SITES.map(site => (
                  <a key={site.name} href={site.url} target="_blank" rel="noopener noreferrer"
                    className="flex items-center gap-3 px-3 py-2.5 rounded-xl bg-white/5 hover:bg-white/10 transition-colors group">
                    <span className="text-xl">{site.icon}</span>
                    <div className="flex-1 min-w-0">
                      <p className="text-white text-xs font-medium">{site.name}</p>
                      <p className="text-slate-500 text-[10px]">{site.desc}</p>
                    </div>
                    <ExternalLink size={11} className="text-slate-600 group-hover:text-slate-400 shrink-0" />
                  </a>
                ))}
              </div>
            </div>
            <div className="flex items-start gap-2 text-xs text-slate-600">
              <Info size={12} className="shrink-0 mt-0.5" />
              <p>AI detection is not perfect. Always cross-check important claims with trusted sources.</p>
            </div>
          </div>
        </div>
      </div>
    </motion.div>
  )
}
