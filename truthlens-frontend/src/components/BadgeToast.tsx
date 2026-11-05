import { motion, AnimatePresence } from 'framer-motion'
import { Badge } from '../types'

interface Props {
  badges: Badge[]
}

export default function BadgeToast({ badges }: Props) {
  return (
    <div className="fixed bottom-6 right-4 sm:right-6 z-50 space-y-3 pointer-events-none">
      <AnimatePresence>
        {badges.map(b => (
          <motion.div
            key={b.id}
            initial={{ opacity: 0, x: 80, scale: 0.8 }}
            animate={{ opacity: 1, x: 0, scale: 1 }}
            exit={{ opacity: 0, x: 80, scale: 0.8 }}
            transition={{ type: 'spring', stiffness: 300, damping: 25 }}
            className="glass border border-white/20 rounded-2xl px-4 py-3 flex items-center gap-3 shadow-2xl shadow-black/40 max-w-xs"
          >
            <div className={`w-11 h-11 rounded-xl bg-gradient-to-br ${b.color} flex items-center justify-center text-2xl shrink-0`}>
              {b.icon}
            </div>
            <div>
              <p className="text-xs text-warn-400 font-semibold uppercase tracking-wider">Badge Unlocked!</p>
              <p className="text-white font-bold text-sm">{b.title}</p>
              <p className="text-slate-400 text-xs">{b.description}</p>
            </div>
          </motion.div>
        ))}
      </AnimatePresence>
    </div>
  )
}
