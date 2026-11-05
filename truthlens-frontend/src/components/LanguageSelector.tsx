import { useState, useRef, useEffect } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { Globe, Check, ChevronDown } from 'lucide-react'
import { useLanguage } from '../i18n/LanguageContext'
import { cn } from '../utils/helpers'

export default function LanguageSelector() {
  const { lang, language, setLang, languages, t } = useLanguage()
  const [open, setOpen] = useState(false)
  const ref = useRef<HTMLDivElement>(null)

  // Close on outside click
  useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (ref.current && !ref.current.contains(e.target as Node)) setOpen(false)
    }
    document.addEventListener('mousedown', handler)
    return () => document.removeEventListener('mousedown', handler)
  }, [])

  return (
    <div ref={ref} className="relative">
      {/* Trigger */}
      <button
        onClick={() => setOpen(!open)}
        className={cn(
          'flex items-center gap-1.5 px-3 py-2 rounded-xl text-sm font-medium transition-all duration-200',
          open
            ? 'bg-brand-600/20 text-brand-300 border border-brand-500/40'
            : 'text-slate-400 hover:text-white hover:bg-white/8 border border-transparent'
        )}
        aria-label={t.lang_select}
        aria-expanded={open}
      >
        <Globe size={15} className="shrink-0" />
        <span className="hidden sm:inline text-xs">{language.flag} {language.nativeName}</span>
        <span className="sm:hidden">{language.flag}</span>
        <ChevronDown
          size={13}
          className={cn('transition-transform duration-200', open ? 'rotate-180' : '')}
        />
      </button>

      {/* Dropdown */}
      <AnimatePresence>
        {open && (
          <motion.div
            initial={{ opacity: 0, y: 6, scale: 0.97 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 6, scale: 0.97 }}
            transition={{ duration: 0.15 }}
            className="absolute right-0 mt-2 w-52 bg-slate-900 border border-white/10 rounded-2xl shadow-2xl shadow-black/40 overflow-hidden z-50"
          >
            {/* Header */}
            <div className="px-4 py-2.5 border-b border-white/8 flex items-center gap-2">
              <Globe size={13} className="text-brand-400" />
              <span className="text-xs text-slate-400 font-medium">{t.lang_select}</span>
            </div>

            {/* Language list */}
            <ul className="py-1.5">
              {languages.map((l) => {
                const active = l.code === lang
                return (
                  <li key={l.code}>
                    <button
                      onClick={() => { setLang(l.code); setOpen(false) }}
                      className={cn(
                        'w-full text-left px-4 py-2.5 flex items-center gap-3 text-sm transition-colors duration-150',
                        active
                          ? 'bg-brand-600/15 text-white'
                          : 'text-slate-400 hover:bg-white/5 hover:text-white'
                      )}
                    >
                      <span className="text-base leading-none">{l.flag}</span>
                      <div className="flex-1 min-w-0">
                        <p className="font-medium leading-none">{l.nativeName}</p>
                        {l.nativeName !== l.name && (
                          <p className="text-xs text-slate-600 mt-0.5">{l.name}</p>
                        )}
                      </div>
                      {active && <Check size={13} className="text-brand-400 shrink-0" />}
                    </button>
                  </li>
                )
              })}
            </ul>

            {/* Sunbird attribution */}
            <div className="px-4 py-2 border-t border-white/8">
              <p className="text-[10px] text-slate-600 text-center">{t.lang_powered}</p>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  )
}
