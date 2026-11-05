import { useCallback, useState } from 'react'
import { useDropzone } from 'react-dropzone'
import { motion, AnimatePresence } from 'framer-motion'
import { Upload, Image, Video, Music, X, FileWarning } from 'lucide-react'
import { cn, formatFileSize, isValidMedia } from '../utils/helpers'

interface DropZoneProps {
  onFileSelected: (file: File) => void
  disabled?: boolean
}

function FileTypeIcon({ type }: { type: string }) {
  if (type.startsWith('video/')) return <Video size={32} className="text-purple-400" />
  if (type.startsWith('audio/')) return <Music size={32} className="text-pink-400" />
  return <Image size={32} className="text-brand-400" />
}

function FileTypeBadge({ type }: { type: string }) {
  if (type.startsWith('video/')) return <span className="text-xs text-purple-400 bg-purple-500/10 px-2 py-0.5 rounded-full">Video</span>
  if (type.startsWith('audio/')) return <span className="text-xs text-pink-400 bg-pink-500/10 px-2 py-0.5 rounded-full">Audio</span>
  return <span className="text-xs text-brand-400 bg-brand-500/10 px-2 py-0.5 rounded-full">Image</span>
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
      'image/*':  ['.jpg', '.jpeg', '.png', '.webp', '.gif'],
      'video/*':  ['.mp4', '.webm', '.avi', '.mov'],
      'audio/*':  ['.mp3', '.wav', '.ogg', '.m4a', '.flac', '.aac'],
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
              className="w-full flex flex-col items-center"
            >
              {/* Image preview */}
              {preview ? (
                <img src={preview} alt="Preview" className="max-h-44 mx-auto rounded-xl object-contain mb-4" />
              ) : (
                /* Audio/video icon */
                <div className={cn(
                  'w-16 h-16 rounded-2xl flex items-center justify-center mx-auto mb-4',
                  previewFile.type.startsWith('audio/') ? 'bg-pink-600/20' : 'bg-purple-600/20'
                )}>
                  <FileTypeIcon type={previewFile.type} />
                </div>
              )}

              {/* Audio waveform bars (visual only) */}
              {previewFile.type.startsWith('audio/') && (
                <div className="flex items-center gap-1 mb-3 h-8">
                  {Array.from({ length: 20 }).map((_, i) => (
                    <div
                      key={i}
                      className="w-1 rounded-full bg-pink-500/60 wave-bar"
                      style={{ height: `${8 + Math.sin(i * 0.9) * 10 + Math.cos(i * 0.4) * 6}px` }}
                    />
                  ))}
                </div>
              )}

              <div className="flex items-center gap-2 mb-1">
                <p className="text-white font-medium text-sm truncate max-w-[200px]">{previewFile.name}</p>
                <FileTypeBadge type={previewFile.type} />
              </div>
              <p className="text-slate-500 text-xs mt-0.5">{formatFileSize(previewFile.size)}</p>
              <button
                onClick={clearFile}
                className="mt-3 flex items-center gap-1.5 text-slate-500 hover:text-danger-400 text-xs transition-colors"
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
                isDragActive ? 'bg-brand-600/30 scale-110' : 'bg-slate-700/60'
              )}>
                {isDragActive
                  ? <Upload size={28} className="text-brand-400" />
                  : <Image size={28} className="text-slate-400" />
                }
              </div>
              <p className="text-white font-semibold text-lg mb-2">
                {isDragActive ? 'Drop it here!' : 'Drag & drop a file'}
              </p>
              <p className="text-slate-500 text-sm mb-5">
                or <span className="text-brand-400 underline underline-offset-2">click to browse</span>
              </p>
              {/* Accepted file types */}
              <div className="flex flex-wrap items-center justify-center gap-2 text-xs text-slate-600">
                <span className="flex items-center gap-1 bg-white/5 px-2 py-1 rounded-lg">
                  <Image size={11} /> Image
                </span>
                <span className="flex items-center gap-1 bg-white/5 px-2 py-1 rounded-lg">
                  <Video size={11} /> Video
                </span>
                <span className="flex items-center gap-1 bg-white/5 px-2 py-1 rounded-lg">
                  <Music size={11} /> Audio
                </span>
                <span className="text-slate-700">· Max 50 MB</span>
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

      {/* Analyse button */}
      <AnimatePresence>
        {previewFile && !error && (
          <motion.button
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: 10 }}
            onClick={() => onFileSelected(previewFile)}
            disabled={disabled}
            className={cn(
              'w-full text-base py-4 flex items-center justify-center gap-2 rounded-xl font-semibold transition-all duration-200',
              previewFile.type.startsWith('audio/')
                ? 'bg-gradient-to-r from-pink-600 to-rose-600 hover:from-pink-500 hover:to-rose-500 text-white hover:shadow-lg hover:shadow-pink-900/40 hover:-translate-y-0.5'
                : 'btn-primary',
              disabled && 'opacity-50 cursor-not-allowed'
            )}
          >
            {previewFile.type.startsWith('audio/')
              ? <><Music size={18} /> Analyse This Audio</>
              : previewFile.type.startsWith('video/')
                ? <><Video size={18} /> Analyse This Video</>
                : <><Upload size={18} /> Analyse This Image</>
            }
          </motion.button>
        )}
      </AnimatePresence>
    </div>
  )
}
