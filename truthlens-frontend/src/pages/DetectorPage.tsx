import { useState, useEffect, useRef } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { Shield, Info, History, Trash2, AlertCircle } from 'lucide-react'
import DropZone from '../components/DropZone'
import AnalyzingLoader from '../components/AnalyzingLoader'
import AnalysisResult from '../components/AnalysisResult'
import { analyzeMedia } from '../utils/api'
import { DetectionResult, UploadState } from '../types'

const PAGE_TRANSITION = {
  initial: { opacity: 0, y: 20 },
  animate: { opacity: 1, y: 0, transition: { duration: 0.5, ease: [0.22, 1, 0.36, 1] } },
  exit: { opacity: 0, y: -20, transition: { duration: 0.3 } },
}

export default function DetectorPage() {
  const [uploadState, setUploadState] = useState<UploadState>({ status: 'idle', progress: 0 })
  const [result, setResult] = useState<DetectionResult | null>(null)
  const [history, setHistory] = useState<DetectionResult[]>([])
  const [showHistory, setShowHistory] = useState(false)
  const progressTimer = useRef<ReturnType<typeof setInterval> | null>(null)

  // Simulate progress ticking during analysis
  const startProgressSimulation = () => {
    let p = 0
    progressTimer.current = setInterval(() => {
      p += Math.random() * 8 + 2
      if (p >= 90) {
        clearInterval(progressTimer.current!)
        p = 90
      }
      setUploadState(prev => ({ ...prev, progress: Math.min(Math.floor(p), 90) }))
    }, 400)
  }

  const stopProgress = () => {
    if (progressTimer.current) clearInterval(progressTimer.current)
  }

  const handleFileSelected = async (file: File) => {
    setUploadState({ status: 'uploading', progress: 5 })
    setResult(null)

    try {
      // Short upload phase
      await new Promise(r => setTimeout(r, 300))
      setUploadState({ status: 'analyzing', progress: 10 })
      startProgressSimulation()

      const data = await analyzeMedia(file)

      stopProgress()
      setUploadState({ status: 'done', progress: 100 })
      setResult(data)
      setHistory(prev => [data, ...prev].slice(0, 10))
    } catch (err: unknown) {
      stopProgress()
      const msg = err instanceof Error ? err.message : 'Analysis failed. Please try again.'
      setUploadState({ status: 'error', progress: 0, error: msg })
    }
  }

  const handleReset = () => {
    setUploadState({ status: 'idle', progress: 0 })
    setResult(null)
  }

  useEffect(() => () => stopProgress(), [])

  const isAnalyzing = uploadState.status === 'analyzing' || uploadState.status === 'uploading'

  return (
    <motion.div {...PAGE_TRANSITION} className="min-h-screen pt-20 hero-bg">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        {/* Header */}
        <div className="flex items-center justify-between mb-10">
          <div>
            <div className="flex items-center gap-3 mb-2">
              <div className="w-10 h-10 rounded-xl bg-brand-600/20 flex items-center justify-center">
                <Shield size={20} className="text-brand-400" />
              </div>
              <h1 className="font-display font-black text-3xl md:text-4xl text-white">
                Deepfake Detector
              </h1>
            </div>
            <p className="text-slate-400 ml-14 -mt-1">
              Upload any image or video to check for AI manipulation.
            </p>
          </div>

          {history.length > 0 && (
            <button
              onClick={() => setShowHistory(!showHistory)}
              className="hidden sm:flex items-center gap-2 btn-secondary text-sm py-2"
            >
              <History size={15} />
              History ({history.length})
            </button>
          )}
        </div>

        <div className="grid lg:grid-cols-5 gap-8">
          {/* Main Panel */}
          <div className="lg:col-span-3 space-y-6">
            <div className="glass-dark rounded-3xl p-6 md:p-8">
              <AnimatePresence mode="wait">
                {uploadState.status === 'error' ? (
                  <motion.div
                    key="error"
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    exit={{ opacity: 0 }}
                    className="flex flex-col items-center gap-4 py-8 text-center"
                  >
                    <div className="w-14 h-14 rounded-2xl bg-danger-500/20 flex items-center justify-center">
                      <AlertCircle size={28} className="text-danger-400" />
                    </div>
                    <div>
                      <p className="text-white font-semibold mb-1">Something went wrong</p>
                      <p className="text-slate-400 text-sm max-w-sm">{uploadState.error}</p>
                    </div>
                    <button onClick={handleReset} className="btn-primary">Try Again</button>
                  </motion.div>
                ) : isAnalyzing ? (
                  <AnalyzingLoader key="analyzing" progress={uploadState.progress} />
                ) : result ? (
                  <AnalysisResult key="result" result={result} onReset={handleReset} />
                ) : (
                  <DropZone key="dropzone" onFileSelected={handleFileSelected} disabled={isAnalyzing} />
                )}
              </AnimatePresence>
            </div>

            {/* Disclaimer */}
            <div className="flex items-start gap-3 text-xs text-slate-600 px-1">
              <Info size={13} className="shrink-0 mt-0.5 text-slate-700" />
              <p>
                Deepfake A.I Detector uses AI models that are not 100% accurate. Results are for educational guidance only.
                Always verify important content through multiple trusted sources. Files are not stored after analysis.
              </p>
            </div>
          </div>

          {/* Sidebar */}
          <div className="lg:col-span-2 space-y-5">
            {/* What we detect */}
            <div className="glass-dark rounded-2xl p-5">
              <h3 className="font-semibold text-white mb-4 flex items-center gap-2">
                <Shield size={15} className="text-brand-400" />
                What we analyse
              </h3>
              <ul className="space-y-3">
                {[
                  { icon: '🎭', title: 'Face swaps', desc: 'Detect GAN-generated face replacements' },
                  { icon: '🗣️', title: 'Lip sync manipulation', desc: 'Audio-video sync inconsistencies' },
                  { icon: '🖼️', title: 'Image synthesis', desc: 'Fully AI-generated images' },
                  { icon: '✂️', title: 'Splicing & editing', desc: 'Cut-and-paste manipulations' },
                  { icon: '🤖', title: 'AI generation', desc: 'Stable Diffusion, DALL·E, Midjourney' },
                ].map(({ icon, title, desc }) => (
                  <li key={title} className="flex items-start gap-3">
                    <span className="text-base leading-none mt-0.5">{icon}</span>
                    <div>
                      <p className="text-slate-300 text-sm font-medium">{title}</p>
                      <p className="text-slate-600 text-xs">{desc}</p>
                    </div>
                  </li>
                ))}
              </ul>
            </div>

            {/* Tips */}
            <div className="glass-dark rounded-2xl p-5">
              <h3 className="font-semibold text-white mb-4">📚 Tip of the day</h3>
              <p className="text-slate-400 text-sm leading-relaxed">
                Always check the <strong className="text-slate-300">source</strong> of content before sharing. Even if an image passes our detector, it may still be misleading when taken out of context.
              </p>
            </div>

            {/* History (mobile) */}
            {history.length > 0 && (
              <div className="lg:hidden">
                <button
                  onClick={() => setShowHistory(!showHistory)}
                  className="flex items-center gap-2 btn-secondary text-sm py-2 w-full justify-center"
                >
                  <History size={15} />
                  Recent scans ({history.length})
                </button>
              </div>
            )}
          </div>
        </div>

        {/* History Panel */}
        <AnimatePresence>
          {showHistory && (
            <motion.div
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: 'auto' }}
              exit={{ opacity: 0, height: 0 }}
              className="mt-8 glass-dark rounded-2xl overflow-hidden"
            >
              <div className="p-5 flex items-center justify-between border-b border-white/8">
                <h3 className="font-semibold text-white flex items-center gap-2">
                  <History size={15} className="text-brand-400" />
                  Recent Scans
                </h3>
                <button
                  onClick={() => setHistory([])}
                  className="text-slate-500 hover:text-danger-400 text-xs flex items-center gap-1 transition-colors"
                >
                  <Trash2 size={12} /> Clear
                </button>
              </div>
              <div className="divide-y divide-white/5">
                {history.map((item) => (
                  <div key={item.id} className="p-4 flex items-center justify-between gap-4 hover:bg-white/3 transition-colors">
                    <div className="min-w-0">
                      <p className="text-slate-300 text-sm truncate">{item.filename}</p>
                      <p className="text-slate-600 text-xs">{new Date(item.timestamp).toLocaleTimeString()}</p>
                    </div>
                    <div className={`shrink-0 text-xs font-semibold px-2.5 py-1 rounded-full
                      ${item.prediction === 'FAKE' ? 'bg-danger-500/20 text-danger-400' :
                        item.prediction === 'REAL' ? 'bg-accent-500/20 text-accent-400' :
                        'bg-warn-500/20 text-warn-400'}`}>
                      {item.prediction} {(item.confidence * 100).toFixed(0)}%
                    </div>
                  </div>
                ))}
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </div>
    </motion.div>
  )
}
