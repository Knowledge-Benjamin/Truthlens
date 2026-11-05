import { useState, useContext } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { Link2, Search, AlertTriangle, Info, History, X } from 'lucide-react'
import axios from 'axios'
import { DetectionResult } from '../types'
import AnalysisResult from '../components/AnalysisResult'
import AnalyzingLoader from '../components/AnalyzingLoader'
import { ProgressContext } from '../context/ProgressContext'

const PAGE = {
  initial: { opacity: 0, y: 20 },
  animate: { opacity: 1, y: 0, transition: { duration: 0.5 } },
  exit:    { opacity: 0, y: -20, transition: { duration: 0.3 } },
}

const EXAMPLE_URLS = [
  'https://upload.wikimedia.org/wikipedia/commons/a/a7/Camponotus_flavomarginatus_ant.jpg',
  'https://www.w3schools.com/html/mov_bbb.mp4',
]

export default function UrlScanPage() {
  const [url, setUrl] = useState('')
  const [status, setStatus] = useState<'idle' | 'fetching' | 'analyzing' | 'done' | 'error'>('idle')
  const [progress, setProgress] = useState(0)
  const [result, setResult] = useState<DetectionResult | null>(null)
  const [error, setError] = useState('')
  const [history, setHistory] = useState<DetectionResult[]>([])
  const ctx = useContext(ProgressContext)

  const isValidUrl = (u: string) => {
    try { new URL(u); return true } catch { return false }
  }

  const handleScan = async () => {
    if (!url.trim() || !isValidUrl(url)) {
      setError('Please enter a valid URL (starting with https://)'); return
    }
    setError('')
    setResult(null)
    setStatus('fetching')
    setProgress(10)

    // Simulate progress
    const timer = setInterval(() => {
      setProgress(p => p >= 85 ? 85 : p + Math.random() * 8 + 3)
    }, 400)

    try {
      setStatus('analyzing')
      const res = await axios.post('/api/detect-url', { url: url.trim() }, { timeout: 60000 })
      clearInterval(timer)
      setProgress(100)
      setStatus('done')
      const data = res.data as DetectionResult
      setResult(data)
      setHistory(h => [data, ...h].slice(0, 10))
      ctx?.recordScan(data.prediction, 'url')
    } catch (err: unknown) {
      clearInterval(timer)
      setStatus('error')
      const msg = axios.isAxiosError(err)
        ? err.response?.data?.detail || err.message
        : 'Scan failed. Check the URL and try again.'
      setError(msg)
    }
  }

  const reset = () => {
    setStatus('idle'); setResult(null); setProgress(0); setError('')
  }

  return (
    <motion.div {...PAGE} className="min-h-screen pt-20 hero-bg">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-10">

        {/* Header */}
        <div className="mb-8">
          <div className="flex items-center gap-3 mb-2">
            <div className="w-10 h-10 rounded-xl bg-cyan-600/20 flex items-center justify-center">
              <Link2 size={20} className="text-cyan-400" />
            </div>
            <h1 className="font-display font-black text-2xl sm:text-4xl text-white">URL Scanner</h1>
          </div>
          <p className="text-slate-400 ml-14">Paste a link to a photo, video or audio — we'll fetch and analyse it.</p>
        </div>

        {/* Input card */}
        <div className="glass-dark rounded-3xl p-6 md:p-8 mb-6">
          <AnimatePresence mode="wait">
            {(status === 'fetching' || status === 'analyzing') ? (
              <AnalyzingLoader key="loader" progress={progress} />
            ) : result ? (
              <AnalysisResult key="result" result={result} onReset={reset} />
            ) : (
              <motion.div key="input" initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
                <label className="block text-sm text-slate-400 mb-2 font-medium">Media URL</label>
                <div className="flex gap-3 flex-col sm:flex-row">
                  <div className="relative flex-1">
                    <Link2 size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500" />
                    <input
                      type="url"
                      value={url}
                      onChange={e => setUrl(e.target.value)}
                      onKeyDown={e => e.key === 'Enter' && handleScan()}
                      placeholder="https://example.com/image.jpg"
                      className="input pl-9"
                    />
                  </div>
                  <button onClick={handleScan} className="btn-primary flex items-center gap-2 sm:w-auto">
                    <Search size={16} /> Scan URL
                  </button>
                </div>

                {/* Error */}
                <AnimatePresence>
                  {error && (
                    <motion.div initial={{ opacity: 0, y: -6 }} animate={{ opacity: 1, y: 0 }} exit={{ opacity: 0 }}
                      className="mt-3 flex items-center gap-2 text-danger-400 text-sm bg-danger-500/10 border border-danger-500/20 rounded-xl px-4 py-3">
                      <AlertTriangle size={14} className="shrink-0" /> {error}
                    </motion.div>
                  )}
                </AnimatePresence>

                {/* Example URLs */}
                <div className="mt-5">
                  <p className="text-xs text-slate-600 mb-2">Try an example:</p>
                  <div className="flex flex-wrap gap-2">
                    {EXAMPLE_URLS.map(u => (
                      <button key={u} onClick={() => setUrl(u)}
                        className="text-xs text-brand-400 bg-brand-600/10 border border-brand-500/20 px-3 py-1.5 rounded-lg hover:bg-brand-600/20 transition-colors truncate max-w-xs">
                        {u.split('/').pop()}
                      </button>
                    ))}
                  </div>
                </div>

                {/* Supported formats */}
                <div className="mt-5 flex flex-wrap gap-2">
                  {['JPG/PNG', 'MP4/WebM', 'MP3/WAV', 'GIF', 'WebP'].map(f => (
                    <span key={f} className="text-xs text-slate-500 bg-white/5 px-2 py-1 rounded-md">{f}</span>
                  ))}
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </div>

        {/* Privacy note */}
        <div className="flex items-start gap-2 text-xs text-slate-600 mb-8">
          <Info size={13} className="shrink-0 mt-0.5" />
          <p>We fetch the media from the URL, analyse it, and immediately discard it. No files are stored.</p>
        </div>

        {/* History */}
        {history.length > 0 && (
          <div className="glass-dark rounded-2xl overflow-hidden">
            <div className="px-5 py-4 flex items-center justify-between border-b border-white/8">
              <h3 className="text-white font-semibold text-sm flex items-center gap-2">
                <History size={14} className="text-brand-400" /> Recent URL Scans
              </h3>
              <button onClick={() => setHistory([])} className="text-slate-600 hover:text-danger-400 transition-colors">
                <X size={14} />
              </button>
            </div>
            {history.map(item => (
              <div key={item.id} className="px-5 py-3 flex items-center justify-between gap-4 border-b border-white/5 last:border-0 hover:bg-white/3">
                <p className="text-slate-400 text-xs truncate flex-1">{item.filename}</p>
                <span className={`text-xs font-bold px-2 py-0.5 rounded-full shrink-0 ${
                  item.prediction === 'FAKE' ? 'bg-danger-500/20 text-danger-400' :
                  item.prediction === 'REAL' ? 'bg-accent-500/20 text-accent-400' :
                  'bg-warn-500/20 text-warn-400'}`}>
                  {item.prediction}
                </span>
              </div>
            ))}
          </div>
        )}
      </div>
    </motion.div>
  )
}
