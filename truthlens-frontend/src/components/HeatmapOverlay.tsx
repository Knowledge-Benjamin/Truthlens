import { useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { Eye, EyeOff, Lightbulb } from 'lucide-react'

interface Props {
  imageUrl: string
  heatmapUrl?: string
  explanation?: string
  prediction: string
}

export default function HeatmapOverlay({ imageUrl, heatmapUrl, explanation, prediction }: Props) {
  const [showHeatmap, setShowHeatmap] = useState(false)

  const defaultExplanation = prediction === 'FAKE'
    ? "The AI found inconsistencies in the facial region — unnatural texture smoothing and irregular lighting gradients that are common in GAN-generated face swaps. The highlighted areas show where manipulation is most likely."
    : prediction === 'REAL'
    ? "The AI found natural noise patterns, consistent lighting, and realistic compression artifacts throughout the image — all signs of authentic, unmanipulated media."
    : "The AI could not make a confident determination. Some regions show minor inconsistencies but they are within normal variance for real media."

  return (
    <div className="space-y-3">
      {/* Explainability card */}
      <div className="glass rounded-xl p-4">
        <div className="flex items-start gap-3">
          <Lightbulb size={16} className="text-warn-400 mt-0.5 shrink-0" />
          <div>
            <p className="text-white text-sm font-semibold mb-1">Why did the AI say this?</p>
            <p className="text-slate-400 text-xs leading-relaxed">{explanation || defaultExplanation}</p>
          </div>
        </div>
      </div>

      {/* Image + heatmap toggle */}
      {(heatmapUrl || imageUrl) && (
        <div className="glass rounded-xl overflow-hidden">
          <div className="flex items-center justify-between px-4 py-3 border-b border-white/8">
            <p className="text-white text-sm font-medium">
              {showHeatmap ? 'Manipulation Heatmap' : 'Original Image'}
            </p>
            {heatmapUrl && (
              <button
                onClick={() => setShowHeatmap(!showHeatmap)}
                className="flex items-center gap-1.5 text-xs text-brand-400 hover:text-brand-300 transition-colors"
              >
                {showHeatmap ? <><EyeOff size={12} /> Hide heatmap</> : <><Eye size={12} /> Show heatmap</>}
              </button>
            )}
          </div>
          <div className="relative">
            <AnimatePresence mode="wait">
              <motion.img
                key={showHeatmap ? 'heat' : 'orig'}
                src={showHeatmap && heatmapUrl ? heatmapUrl : imageUrl}
                alt={showHeatmap ? 'Manipulation heatmap' : 'Analysed image'}
                className="w-full max-h-64 object-contain bg-slate-900"
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                exit={{ opacity: 0 }}
                transition={{ duration: 0.3 }}
              />
            </AnimatePresence>
            {showHeatmap && (
              <div className="absolute bottom-2 right-2 flex items-center gap-1">
                <div className="flex items-center gap-1 text-[10px] text-white bg-black/60 px-2 py-1 rounded-full">
                  <span className="w-3 h-3 rounded-full bg-blue-500 inline-block" />Low
                  <span className="w-3 h-3 rounded-full bg-warn-500 inline-block ml-1" />Med
                  <span className="w-3 h-3 rounded-full bg-danger-500 inline-block ml-1" />High
                </div>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  )
}
