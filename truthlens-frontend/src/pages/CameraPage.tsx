import { useRef, useState, useEffect, useContext } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { Camera, CameraOff, Aperture, RefreshCw, AlertTriangle, Info } from 'lucide-react'
import axios from 'axios'
import { DetectionResult } from '../types'
import AnalysisResult from '../components/AnalysisResult'
import { ProgressContext } from '../context/ProgressContext'
import { cn } from '../utils/helpers'

const PAGE = {
  initial: { opacity: 0, y: 20 },
  animate: { opacity: 1, y: 0, transition: { duration: 0.5 } },
  exit:    { opacity: 0, y: -20, transition: { duration: 0.3 } },
}

export default function CameraPage() {
  const videoRef  = useRef<HTMLVideoElement>(null)
  const canvasRef = useRef<HTMLCanvasElement>(null)
  const streamRef = useRef<MediaStream | null>(null)
  const ctx       = useContext(ProgressContext)

  const [camActive, setCamActive] = useState(false)
  const [scanning,  setScanning]  = useState(false)
  const [result,    setResult]    = useState<DetectionResult | null>(null)
  const [error,     setError]     = useState('')
  const [snapUrl,   setSnapUrl]   = useState('')

  const startCamera = async () => {
    setError('')
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: { width: { ideal: 1280 }, height: { ideal: 720 }, facingMode: 'user' },
        audio: false,
      })
      streamRef.current = stream
      if (videoRef.current) {
        videoRef.current.srcObject = stream
        videoRef.current.play()
      }
      setCamActive(true)
    } catch (err) {
      setError('Camera access denied. Please allow camera permissions in your browser.')
    }
  }

  const stopCamera = () => {
    streamRef.current?.getTracks().forEach(t => t.stop())
    streamRef.current = null
    setCamActive(false)
    setResult(null)
    setSnapUrl('')
  }

  const captureAndAnalyse = async () => {
    if (!videoRef.current || !canvasRef.current) return
    const video  = videoRef.current
    const canvas = canvasRef.current
    canvas.width  = video.videoWidth
    canvas.height = video.videoHeight
    canvas.getContext('2d')?.drawImage(video, 0, 0)

    canvas.toBlob(async blob => {
      if (!blob) return
      const previewUrl = canvas.toDataURL('image/jpeg', 0.85)
      setSnapUrl(previewUrl)
      setScanning(true)
      setResult(null)

      const form = new FormData()
      form.append('file', blob, 'camera_frame.jpg')
      try {
        const res = await axios.post('/api/detect', form, {
          headers: { 'Content-Type': 'multipart/form-data' },
          timeout: 60000,
        })
        setResult(res.data)
        ctx?.recordScan(res.data.prediction, 'image')
      } catch (e) {
        setError('Analysis failed. Try again.')
      } finally {
        setScanning(false)
      }
    }, 'image/jpeg', 0.85)
  }

  // cleanup on unmount
  useEffect(() => () => stopCamera(), [])

  return (
    <motion.div {...PAGE} className="min-h-screen pt-20 hero-bg">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-10">

        {/* Header */}
        <div className="mb-8">
          <div className="flex items-center gap-3 mb-2">
            <div className="w-10 h-10 rounded-xl bg-emerald-600/20 flex items-center justify-center">
              <Camera size={20} className="text-emerald-400" />
            </div>
            <h1 className="font-display font-black text-2xl sm:text-4xl text-white">Live Camera Scan</h1>
          </div>
          <p className="text-slate-400 ml-14">Point your camera at suspicious content and capture a frame to analyse.</p>
        </div>

        <div className="grid lg:grid-cols-5 gap-6">
          {/* Camera panel */}
          <div className="lg:col-span-3 space-y-4">
            <div className="glass-dark rounded-3xl overflow-hidden">
              {/* Video feed */}
              <div className="relative bg-slate-900 aspect-video flex items-center justify-center">
                <video
                  ref={videoRef}
                  autoPlay
                  playsInline
                  muted
                  className={cn('w-full h-full object-cover', !camActive && 'hidden')}
                />
                <canvas ref={canvasRef} className="hidden" />

                {!camActive && (
                  <div className="flex flex-col items-center gap-3 p-8">
                    <CameraOff size={40} className="text-slate-600" />
                    <p className="text-slate-500 text-sm text-center">Camera is off. Start it to scan content live.</p>
                  </div>
                )}

                {/* Scanning overlay */}
                {scanning && (
                  <div className="absolute inset-0 bg-black/60 flex items-center justify-center">
                    <div className="text-center">
                      <motion.div
                        animate={{ rotate: 360 }}
                        transition={{ duration: 1.5, repeat: Infinity, ease: 'linear' }}
                        className="w-14 h-14 rounded-full border-2 border-transparent border-t-emerald-500 mx-auto mb-3"
                      />
                      <p className="text-white text-sm font-medium">Analysing frame…</p>
                    </div>
                  </div>
                )}

                {/* Capture button overlay */}
                {camActive && !scanning && (
                  <div className="absolute bottom-4 left-0 right-0 flex justify-center">
                    <button
                      onClick={captureAndAnalyse}
                      className="w-14 h-14 rounded-full bg-white flex items-center justify-center shadow-xl hover:scale-110 transition-transform"
                    >
                      <Aperture size={26} className="text-slate-900" />
                    </button>
                  </div>
                )}
              </div>

              {/* Controls */}
              <div className="p-4 flex items-center gap-3">
                {!camActive ? (
                  <button onClick={startCamera} className="btn-primary flex-1 flex items-center justify-center gap-2">
                    <Camera size={16} /> Start Camera
                  </button>
                ) : (
                  <>
                    <button onClick={captureAndAnalyse} disabled={scanning}
                      className="btn-primary flex-1 flex items-center justify-center gap-2 disabled:opacity-50">
                      <Aperture size={16} /> {scanning ? 'Analysing…' : 'Capture & Analyse'}
                    </button>
                    <button onClick={stopCamera} className="btn-secondary px-4 py-3">
                      <CameraOff size={16} />
                    </button>
                  </>
                )}
              </div>
            </div>

            {/* Error */}
            <AnimatePresence>
              {error && (
                <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} exit={{ opacity: 0 }}
                  className="flex items-center gap-2 text-danger-400 text-sm bg-danger-500/10 border border-danger-500/20 rounded-xl px-4 py-3">
                  <AlertTriangle size={14} className="shrink-0" /> {error}
                </motion.div>
              )}
            </AnimatePresence>
          </div>

          {/* Result / Snapshot panel */}
          <div className="lg:col-span-2 space-y-4">
            {snapUrl && (
              <div className="glass-dark rounded-2xl overflow-hidden">
                <div className="px-4 py-3 border-b border-white/8 flex items-center justify-between">
                  <p className="text-white text-sm font-medium flex items-center gap-2">
                    <RefreshCw size={13} className="text-emerald-400" /> Last Capture
                  </p>
                  <button onClick={() => { setSnapUrl(''); setResult(null) }}
                    className="text-slate-600 hover:text-slate-400 text-xs">Clear</button>
                </div>
                <img src={snapUrl} alt="Captured frame" className="w-full object-contain max-h-48" />
              </div>
            )}

            {result && (
              <div className="glass-dark rounded-2xl p-4">
                <AnalysisResult result={result} onReset={() => { setResult(null); setSnapUrl('') }} />
              </div>
            )}

            {/* Tips */}
            <div className="glass-dark rounded-2xl p-4">
              <h3 className="text-white font-semibold text-sm mb-3 flex items-center gap-2">
                <Info size={14} className="text-brand-400" /> Tips
              </h3>
              <ul className="space-y-2 text-xs text-slate-400">
                {[
                  'Hold your camera steady when capturing',
                  'Ensure good lighting for better results',
                  'Point at a screen showing suspicious video',
                  'Works best with clear face shots',
                ].map(t => (
                  <li key={t} className="flex items-start gap-2">
                    <span className="text-accent-500 mt-0.5">•</span> {t}
                  </li>
                ))}
              </ul>
            </div>
          </div>
        </div>
      </div>
    </motion.div>
  )
}
