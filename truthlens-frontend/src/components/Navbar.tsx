import { useState, useEffect, useRef } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import {
  Eye, Menu, X, Shield, BookOpen, Brain, Info,
  Link2, Camera, UserX, Trophy, Flag, FileText,
  ChevronDown, Sparkles,
} from 'lucide-react'
import { cn } from '../utils/helpers'
import { useLanguage } from '../i18n/LanguageContext'
import LanguageSelector from './LanguageSelector'

const TOOLS = [
  { to: '/detect',        icon: Shield,   label: 'Deepfake Detector',      desc: 'Image, video & audio' },
  { to: '/url-scan',      icon: Link2,    label: 'URL Scanner',             desc: 'Scan any link' },
  { to: '/camera',        icon: Camera,   label: 'Live Camera',             desc: 'Real-time detection' },
  { to: '/impersonation', icon: UserX,    label: 'Impersonation Checker',   desc: 'Spot fake profiles' },
  { to: '/text-check',    icon: FileText, label: 'Text Fact-Checker',       desc: 'Detect AI-written text' },
  { to: '/spot',          icon: Eye,      label: 'Spot the Fake',           desc: 'Interactive training' },
]

export default function Navbar() {
  const [scrolled, setScrolled]       = useState(false)
  const [mobileOpen, setMobileOpen]   = useState(false)
  const [toolsOpen, setToolsOpen]     = useState(false)
  const location                      = useLocation()
  const { t }                         = useLanguage()
  const toolsRef                      = useRef<HTMLDivElement>(null)

  const mainLinks = [
    { to: '/learn',    label: t.nav_learn,  icon: BookOpen },
    { to: '/quiz',     label: t.nav_quiz,   icon: Brain },
    { to: '/progress', label: 'Progress',   icon: Trophy },
    { to: '/report',   label: 'Report',     icon: Flag },
    { to: '/about',    label: t.nav_about,  icon: Info },
  ]

  useEffect(() => {
    const fn = () => setScrolled(window.scrollY > 20)
    window.addEventListener('scroll', fn)
    return () => window.removeEventListener('scroll', fn)
  }, [])

  useEffect(() => { setMobileOpen(false); setToolsOpen(false) }, [location.pathname])

  useEffect(() => {
    const fn = (e: MouseEvent) => {
      if (toolsRef.current && !toolsRef.current.contains(e.target as Node)) setToolsOpen(false)
    }
    document.addEventListener('mousedown', fn)
    return () => document.removeEventListener('mousedown', fn)
  }, [])

  const isToolActive = TOOLS.some(t => t.to === location.pathname)

  return (
    <nav className={cn(
      'fixed top-0 left-0 right-0 z-50 transition-all duration-300',
      scrolled ? 'bg-slate-950/90 backdrop-blur-xl border-b border-white/10 shadow-xl shadow-black/20' : 'bg-transparent'
    )}>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16 gap-2">

          {/* Logo */}
          <Link to="/" className="flex items-center gap-2 group shrink-0">
            <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-brand-500 to-purple-600 flex items-center justify-center group-hover:shadow-lg group-hover:shadow-brand-500/40 transition-all">
              <Eye size={16} className="text-white" />
            </div>
            <span className="font-display font-bold text-base sm:text-xl text-white whitespace-nowrap">
              Deepfake<span className="gradient-text">A.I</span>
              <span className="hidden sm:inline"> Detector</span>
            </span>
          </Link>

          {/* Desktop nav */}
          <div className="hidden md:flex items-center gap-1 flex-1 justify-center">

            {/* Tools dropdown */}
            <div ref={toolsRef} className="relative">
              <button onClick={() => setToolsOpen(!toolsOpen)}
                className={cn(
                  'flex items-center gap-1.5 px-3 py-2 rounded-lg text-sm font-medium transition-all duration-200',
                  (isToolActive || toolsOpen)
                    ? 'bg-brand-600/20 text-brand-400 border border-brand-500/30'
                    : 'text-slate-400 hover:text-white hover:bg-white/8'
                )}>
                <Shield size={15} />
                {t.nav_detect}
                <ChevronDown size={13} className={cn('transition-transform', toolsOpen && 'rotate-180')} />
              </button>

              <AnimatePresence>
                {toolsOpen && (
                  <motion.div
                    initial={{ opacity: 0, y: 6, scale: 0.97 }}
                    animate={{ opacity: 1, y: 0, scale: 1 }}
                    exit={{ opacity: 0, y: 6, scale: 0.97 }}
                    transition={{ duration: 0.15 }}
                    className="absolute left-0 top-full mt-2 w-72 bg-slate-900 border border-white/10 rounded-2xl shadow-2xl shadow-black/40 overflow-hidden z-50"
                  >
                    <div className="px-4 py-2.5 border-b border-white/8 flex items-center gap-2">
                      <Sparkles size={12} className="text-brand-400" />
                      <span className="text-xs text-slate-400 font-medium">Detection Tools</span>
                    </div>
                    <div className="p-2">
                      {TOOLS.map(tool => {
                        const Icon = tool.icon
                        const active = location.pathname === tool.to
                        return (
                          <Link key={tool.to} to={tool.to}
                            className={cn(
                              'flex items-center gap-3 px-3 py-2.5 rounded-xl transition-colors',
                              active ? 'bg-brand-600/15 text-white' : 'text-slate-400 hover:bg-white/5 hover:text-white'
                            )}>
                            <div className={cn('w-8 h-8 rounded-lg flex items-center justify-center shrink-0', active ? 'bg-brand-600/20' : 'bg-white/5')}>
                              <Icon size={15} className={active ? 'text-brand-400' : ''} />
                            </div>
                            <div>
                              <p className="text-sm font-medium leading-none">{tool.label}</p>
                              <p className="text-xs text-slate-600 mt-0.5">{tool.desc}</p>
                            </div>
                          </Link>
                        )
                      })}
                    </div>
                  </motion.div>
                )}
              </AnimatePresence>
            </div>

            {/* Other links */}
            {mainLinks.map(({ to, label, icon: Icon }) => {
              const active = location.pathname === to
              return (
                <Link key={to} to={to}
                  className={cn(
                    'flex items-center gap-2 px-3 py-2 rounded-lg text-sm font-medium transition-all duration-200',
                    active ? 'bg-brand-600/20 text-brand-400 border border-brand-500/30' : 'text-slate-400 hover:text-white hover:bg-white/8'
                  )}>
                  <Icon size={15} /> {label}
                </Link>
              )
            })}
          </div>

          {/* Right */}
          <div className="flex items-center gap-2 shrink-0">
            <LanguageSelector />
            <Link to="/detect" className="hidden md:inline-flex btn-primary text-sm py-2 px-4 whitespace-nowrap">
              {t.nav_try}
            </Link>
            <button onClick={() => setMobileOpen(!mobileOpen)}
              className="md:hidden p-2 rounded-lg text-slate-400 hover:text-white hover:bg-white/10 transition-colors">
              {mobileOpen ? <X size={20}/> : <Menu size={20}/>}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile menu */}
      <AnimatePresence>
        {mobileOpen && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            exit={{ opacity: 0, height: 0 }}
            transition={{ duration: 0.25 }}
            className="md:hidden bg-slate-950/95 backdrop-blur-xl border-b border-white/10"
          >
            <div className="px-4 py-4 flex flex-col gap-1 max-h-[80vh] overflow-y-auto">
              {/* Tools section */}
              <p className="text-xs text-slate-600 uppercase tracking-wider px-2 mb-1 mt-1">Detection Tools</p>
              {TOOLS.map(({ to, icon: Icon, label }) => {
                const active = location.pathname === to
                return (
                  <Link key={to} to={to}
                    className={cn('flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-all',
                      active ? 'bg-brand-600/20 text-brand-400 border border-brand-500/30' : 'text-slate-400 hover:text-white hover:bg-white/8')}>
                    <Icon size={16}/> {label}
                  </Link>
                )
              })}

              <div className="border-t border-white/8 my-2" />
              <p className="text-xs text-slate-600 uppercase tracking-wider px-2 mb-1">More</p>

              {mainLinks.map(({ to, icon: Icon, label }) => {
                const active = location.pathname === to
                return (
                  <Link key={to} to={to}
                    className={cn('flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-all',
                      active ? 'bg-brand-600/20 text-brand-400 border border-brand-500/30' : 'text-slate-400 hover:text-white hover:bg-white/8')}>
                    <Icon size={16}/> {label}
                  </Link>
                )
              })}
              <Link to="/detect" className="btn-primary text-sm mt-2 text-center">{t.nav_try}</Link>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </nav>
  )
}
