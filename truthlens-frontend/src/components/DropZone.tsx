import { useCallback, useState } from 'react'
import { useDropzone } from 'react-dropzone'
import { motion, AnimatePresence } from 'framer-motion'
import { Upload, Image, Video, X, FileWarning } from 'lucide-react'
import { cn, formatFileSize, isValidMedia } from '../utils/helpers'

interface DropZoneProps {
  onFileSelected: (file: File) => void
  disabled?: boolean
}

export default function DropZone({ onFileSelected, disabled }: DropZoneProps) {
  const [preview, setPreview] = useState<string | null>(null)
  const [previewFile, setPreviewFile] = useState<File | null>(null)
  const [error, setError] = useState<string | null>(null)

  const onDrop = useCallback((accepted: File[]) => {
    if (!accepted.length) return
    const file = accepted[0]
    const validation = isValidMedia(file)

    if (!validation.valid) {
      setError(validation.error || 'Invalid file')
      return
    }

    setError(null)
    setPreviewFile(file)

    if (file.type.startsWith('image/')) {
      const url = URL.createObjectURL(file)
      setPreview(url)
    } else {
      setPreview(null)
    }
  }, [])

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: {
      'image/*': ['.jpg', '.jpeg', '.png', '.webp', '.gif'],
      'video/*': ['.mp4', '.webm', '.avi', '.mov'],
    },
    maxFiles: 1,
    disabled,
  })

  const clearFile = (e: React.MouseEvent) => {
    e.stopPropagation()
    setPreviewFile(null)
    setPreview(null)
    setError(null)
  }

  const handleAnalyze = () => {
    if (previewFile) onFileSelected(previewFile)
  }

  return (
    <div className="space-y-4">
      <div
        {...getRootProps()}
        className={cn(
          'relative rounded-2xl border-2 border-dashed transition-all duration-300 cursor-pointer',
          'flex flex-col items-center justify-center text-center p-8 min-h-[280px]',
          isDragActive
            ? 'border-brand-400 bg-brand-600/10 scale-[1.01]'
            : previewFile
              ? 'border-accent-500/40 bg-accent-500/5'
              : 'border-slate-700 bg-slate-800/30 hover:border-brand-500/60 hover:bg-slate-800/50',
          disabled && 'opacity-50 cursor-not-allowed',
        )}
      >
        <input {...getInputProps()} />

        <AnimatePresence mode="wait">
          {previewFile ? (
            <motion.div
              key="preview"
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.95 }}
              className="w-full"
            >
              {preview ? (
                <img
                  src={preview}
                  alt="Preview"
                  className="max-h-48 mx-auto rounded-xl object-contain mb-4"
                />
              ) : (
                <div className="w-16 h-16 rounded-2xl bg-purple-600/20 flex items-center justify-center mx-auto mb-4">
                  <Video size={32} className="text-purple-400" />
                </div>
              )}
              <p className="text-white font-medium">{previewFile.name}</p>
              <p className="text-slate-500 text-sm mt-1">{formatFileSize(previewFile.size)}</p>
              <button
                onClick={clearFile}
                className="mt-3 flex items-center gap-1.5 text-slate-500 hover:text-danger-400 text-xs transition-colors mx-auto"
              >
                <X size={12} /> Remove file
              </button>
            </motion.div>
          ) : (
            <motion.div
              key="idle"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              className="flex flex-col items-center"
            >
              <div className={cn(
                'w-16 h-16 rounded-2xl flex items-center justify-center mb-5 transition-all duration-300',
                isDragActive
                  ? 'bg-brand-600/30 scale-110'
                  : 'bg-slate-700/60'
              )}>
                {isDragActive
                  ? <Upload size={28} className="text-brand-400" />
                  : <Image size={28} className="text-slate-400" />
                }
              </div>
              <p className="text-white font-semibold text-lg mb-2">
                {isDragActive ? 'Drop it here!' : 'Drag & drop a file'}
              </p>
              <p className="text-slate-500 text-sm mb-4">
                or <span className="text-brand-400 underline underline-offset-2">click to browse</span>
              </p>
              <div className="flex items-center gap-4 text-xs text-slate-600">
                <span className="flex items-center gap-1"><Image size={11} /> JPG, PNG, WebP, GIF</span>
                <span className="w-px h-3 bg-slate-700" />
                <span className="flex items-center gap-1"><Video size={11} /> MP4, WebM, MOV</span>
                <span className="w-px h-3 bg-slate-700" />
                <span>Max 50 MB</span>
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </div>

      {/* Error */}
      <AnimatePresence>
        {error && (
          <motion.div
            initial={{ opacity: 0, y: -8 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -8 }}
            className="flex items-center gap-2 text-danger-400 text-sm bg-danger-500/10 border border-danger-500/20 rounded-xl px-4 py-3"
          >
            <FileWarning size={16} className="shrink-0" />
            {error}
          </motion.div>
        )}
      </AnimatePresence>

      {/* Analyze Button */}
      <AnimatePresence>
        {previewFile && !error && (
          <motion.button
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: 10 }}
            onClick={handleAnalyze}
            disabled={disabled}
            className="btn-primary w-full text-base py-4 flex items-center justify-center gap-2"
          >
            <Upload size={18} />
            Analyse This {previewFile.type.startsWith('video/') ? 'Video' : 'Image'}
          </motion.button>
        )}
      </AnimatePresence>
    </div>
  )
}
