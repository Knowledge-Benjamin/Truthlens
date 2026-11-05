import { useState, useEffect } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'
import { Eye, Menu, X, Shield, BookOpen, Brain, Info } from 'lucide-react'
import { cn } from '../utils/helpers'
import { useLanguage } from '../i18n/LanguageContext'
import LanguageSelector from './LanguageSelector'

export default function Navbar() {
  const [scrolled, setScrolled] = useState(false)
  const [mobileOpen, setMobileOpen] = useState(false)
  const location = useLocation()
  const { t } = useLanguage()

  const navLinks = [
    { to: '/detect', label: t.nav_detect, icon: Shield },
    { to: '/learn',  label: t.nav_learn,  icon: BookOpen },
    { to: '/quiz',   label: t.nav_quiz,   icon: Brain },
    { to: '/about',  label: t.nav_about,  icon: Info },
  ]

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 20)
    window.addEventListener('scroll', onScroll)
    return () => window.removeEventListener('scroll', onScroll)
  }, [])

  useEffect(() => {
    setMobileOpen(false)
  }, [location.pathname])

  return (
    <nav className={cn(
      'fixed top-0 left-0 right-0 z-50 transition-all duration-300',
      scrolled ? 'bg-slate-950/90 backdrop-blur-xl border-b border-white/10 shadow-xl shadow-black/20' : 'bg-transparent'
    )}>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16 gap-2">

          {/* Logo */}
          <Link to="/" className="flex items-center gap-2 group shrink-0">
            <div className="relative">
              <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-brand-500 to-purple-600 flex items-center justify-center
                              group-hover:shadow-lg group-hover:shadow-brand-500/40 transition-all duration-300">
                <Eye size={16} className="text-white" />
              </div>
              <div className="absolute -inset-1 rounded-lg bg-brand-500/20 opacity-0 group-hover:opacity-100 transition-opacity duration-300 -z-10" />
            </div>
            <span className="font-display font-bold text-base sm:text-xl text-white whitespace-nowrap">
              Deepfake<span className="gradient-text">A.I</span>
              <span className="hidden sm:inline"> Detector</span>
            </span>
          </Link>

          {/* Desktop Nav */}
          <div className="hidden md:flex items-center gap-1 flex-1 justify-center">
            {navLinks.map(({ to, label, icon: Icon }) => {
              const active = location.pathname === to
              return (
                <Link
                  key={to}
                  to={to}
                  className={cn(
                    'flex items-center gap-2 px-3 py-2 rounded-lg text-sm font-medium transition-all duration-200',
                    active
                      ? 'bg-brand-600/20 text-brand-400 border border-brand-500/30'
                      : 'text-slate-400 hover:text-white hover:bg-white/8'
                  )}
                >
                  <Icon size={15} />
                  {label}
                </Link>
              )
            })}
          </div>

          {/* Right: Language selector + CTA + Mobile toggle */}
          <div className="flex items-center gap-2 shrink-0">
            <LanguageSelector />
            <Link to="/detect" className="hidden md:inline-flex btn-primary text-sm py-2 px-4 whitespace-nowrap">
              {t.nav_try}
            </Link>
            <button
              onClick={() => setMobileOpen(!mobileOpen)}
              className="md:hidden p-2 rounded-lg text-slate-400 hover:text-white hover:bg-white/10 transition-colors"
              aria-label="Toggle menu"
            >
              {mobileOpen ? <X size={20} /> : <Menu size={20} />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile Menu */}
      <AnimatePresence>
        {mobileOpen && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            exit={{ opacity: 0, height: 0 }}
            transition={{ duration: 0.25 }}
            className="md:hidden bg-slate-950/95 backdrop-blur-xl border-b border-white/10"
          >
            <div className="px-4 py-4 flex flex-col gap-1">
              {navLinks.map(({ to, label, icon: Icon }) => {
                const active = location.pathname === to
                return (
                  <Link
                    key={to}
                    to={to}
                    className={cn(
                      'flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-all duration-200',
                      active
                        ? 'bg-brand-600/20 text-brand-400 border border-brand-500/30'
                        : 'text-slate-400 hover:text-white hover:bg-white/8'
                    )}
                  >
                    <Icon size={16} />
                    {label}
                  </Link>
                )
              })}
              <Link to="/detect" className="btn-primary text-sm mt-2 text-center">
                {t.nav_try}
              </Link>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </nav>
  )
}
