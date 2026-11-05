import { useState, useContext } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { UserX, Search, Info, ExternalLink, ShieldAlert } from 'lucide-react'
import { ImpersonationResult } from '../types'
import { cn } from '../utils/helpers'
import { ProgressContext } from '../context/ProgressContext'

const PAGE = {
  initial: { opacity: 0, y: 20 },
  animate: { opacity: 1, y: 0, transition: { duration: 0.5 } },
  exit:    { opacity: 0, y: -20, transition: { duration: 0.3 } },
}

// Client-side heuristic impersonation analyser
function analyseProfile(username: string, bio: string, url: string): ImpersonationResult {
  const flags = []
  let riskScore = 0

  // Check for celebrity/official impersonation patterns
  const impersonationPatterns = [
    /^(the)?real/i, /official/i, /verified/i, /not_?fake/i, /legit/i,
    /authentic/i, /genuine/i, /actual/i, /true/i, /original/i
  ]
  if (impersonationPatterns.some(p => p.test(username))) {
    riskScore += 35
    flags.push({ label: 'Suspicious Username', description: 'Username contains words like "real", "official", or "verified" — a common impersonation tactic.', severity: 'danger' as const, icon: '⚠️' })
  }

  // Lots of underscores / numbers (e.g. john__doe123)
  const underscoreCount = (username.match(/_/g) || []).length
  if (underscoreCount >= 2) {
    riskScore += 20
    flags.push({ label: 'Unusual Username Format', description: 'Multiple underscores are often used to create lookalike usernames (e.g. john__doe instead of johndoe).', severity: 'warning' as const, icon: '🔡' })
  }

  // Bio contains suspicious phrases
  const suspiciousBio = [/dm for promo/i, /giveaway/i, /click.*link/i, /earn.*money/i, /free.*gift/i, /investment/i, /crypto/i, /admin/i]
  if (suspiciousBio.some(p => p.test(bio))) {
    riskScore += 25
    flags.push({ label: 'Suspicious Bio Content', description: 'Bio contains common scam phrases like "DM for promo", "free gift", or investment offers.', severity: 'danger' as const, icon: '📝' })
  }

  // URL shorteners or suspicious domains
  const suspiciousUrls = [/bit\.ly/i, /tinyurl/i, /goo\.gl/i, /t\.co/i, /ow\.ly/i]
  if (url && suspiciousUrls.some(p => p.test(url))) {
    riskScore += 20
    flags.push({ label: 'Shortened Link in Bio', description: 'Short URLs hide where they actually lead — a common phishing and scam technique.', severity: 'warning' as const, icon: '🔗' })
  }

  // Mixed scripts (homoglyph attacks: using Cyrillic chars that look like Latin)
  if (/[а-яА-Я]/.test(username)) {
    riskScore += 40
    flags.push({ label: 'Mixed Script Characters', description: 'Username contains non-Latin characters that look identical to English letters — a sophisticated impersonation trick.', severity: 'danger' as const, icon: '🔤' })
  }

  // Very short username with numbers (e.g. elon1, nike2)
  if (/^[a-z]{3,8}\d+$/i.test(username)) {
    riskScore += 15
    flags.push({ label: 'Name + Number Pattern', description: 'Adding numbers to a real name is a classic way to impersonate when the original account already exists.', severity: 'warning' as const, icon: '🔢' })
  }

  if (flags.length === 0) {
    flags.push({ label: 'No Obvious Red Flags', description: 'We did not detect common impersonation patterns — but always verify through official channels.', severity: 'info' as const, icon: '✅' })
  }

  riskScore = Math.min(riskScore, 100)
  const riskLevel: ImpersonationResult['riskLevel'] = riskScore >= 50 ? 'HIGH' : riskScore >= 25 ? 'MEDIUM' : 'LOW'
  const advice = riskLevel === 'HIGH'
    ? "This profile shows multiple red flags. Do not share personal information, click links, or send money. Report to the platform immediately."
    : riskLevel === 'MEDIUM'
    ? "This profile has some warning signs. Verify through official websites or a second contact method before trusting it."
    : "No major red flags found. Still verify important accounts through official channels."

  return { riskLevel, riskScore, flags, advice }
}

export default function ImpersonationPage() {
  const [username, setUsername] = useState('')
  const [bio, setBio]           = useState('')
  const [url, setUrl]           = useState('')
  const [result, setResult]     = useState<ImpersonationResult | null>(null)
  const ctx = useContext(ProgressContext)

  const check = () => {
    if (!username.trim()) return
    const r = analyseProfile(username.trim(), bio.trim(), url.trim())
    setResult(r)
    ctx?.recordSpecialBadge?.('first_scan')
  }

  const riskColor = (level: ImpersonationResult['riskLevel']) =>
    level === 'HIGH' ? 'text-danger-400' : level === 'MEDIUM' ? 'text-warn-400' : 'text-accent-400'

  const riskBg = (level: ImpersonationResult['riskLevel']) =>
    level === 'HIGH' ? 'bg-danger-500/10 border-danger-500/30' :
    level === 'MEDIUM' ? 'bg-warn-500/10 border-warn-500/30' : 'bg-accent-500/10 border-accent-500/30'

  const severityColor = (s: string) =>
    s === 'danger' ? 'text-danger-400 bg-danger-500/10' :
    s === 'warning' ? 'text-warn-400 bg-warn-500/10' : 'text-accent-400 bg-accent-500/10'

  return (
    <motion.div {...PAGE} className="min-h-screen pt-20 hero-bg">
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-10">

        <div className="mb-8">
          <div className="flex items-center gap-3 mb-2">
            <div className="w-10 h-10 rounded-xl bg-pink-600/20 flex items-center justify-center">
              <UserX size={20} className="text-pink-400" />
            </div>
            <h1 className="font-display font-black text-2xl sm:text-4xl text-white">Impersonation Checker</h1>
          </div>
          <p className="text-slate-400 ml-14">Check if a social media profile shows signs of digital impersonation.</p>
        </div>

        <div className="glass-dark rounded-3xl p-6 md:p-8 mb-6 space-y-4">
          <div>
            <label className="block text-sm text-slate-400 mb-1.5 font-medium">Username / Handle *</label>
            <input value={username} onChange={e => setUsername(e.target.value)}
              placeholder="e.g. elonmusk_real or johnDoe123" className="input" />
          </div>
          <div>
            <label className="block text-sm text-slate-400 mb-1.5 font-medium">Profile Bio <span className="text-slate-600">(optional)</span></label>
            <textarea value={bio} onChange={e => setBio(e.target.value)}
              placeholder="Paste the profile bio here..."
              rows={2} className="input resize-none" />
          </div>
          <div>
            <label className="block text-sm text-slate-400 mb-1.5 font-medium">Profile Link / URL <span className="text-slate-600">(optional)</span></label>
            <input value={url} onChange={e => setUrl(e.target.value)}
              placeholder="https://bit.ly/xyz or https://example.com" className="input" />
          </div>
          <button onClick={check} disabled={!username.trim()}
            className="btn-primary w-full flex items-center justify-center gap-2 disabled:opacity-50">
            <Search size={16} /> Check for Impersonation
          </button>
        </div>

        {/* Result */}
        <AnimatePresence>
          {result && (
            <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="space-y-4">
              {/* Risk level card */}
              <div className={cn('rounded-2xl p-6 border', riskBg(result.riskLevel))}>
                <div className="flex items-center gap-4">
                  <div className={cn('w-14 h-14 rounded-2xl flex items-center justify-center text-3xl border', riskBg(result.riskLevel))}>
                    {result.riskLevel === 'HIGH' ? '🚨' : result.riskLevel === 'MEDIUM' ? '⚠️' : '✅'}
                  </div>
                  <div className="flex-1">
                    <div className="flex items-center gap-3 flex-wrap">
                      <h3 className={cn('font-display font-bold text-2xl', riskColor(result.riskLevel))}>
                        {result.riskLevel === 'HIGH' ? 'High Risk' : result.riskLevel === 'MEDIUM' ? 'Medium Risk' : 'Low Risk'}
                      </h3>
                      <span className={cn('text-xs font-bold px-2.5 py-1 rounded-full border', riskBg(result.riskLevel), riskColor(result.riskLevel))}>
                        Score: {result.riskScore}/100
                      </span>
                    </div>
                    <p className="text-slate-400 text-sm mt-1">{result.advice}</p>
                  </div>
                </div>
                {/* Score bar */}
                <div className="mt-4 progress-bar">
                  <motion.div
                    className={cn('progress-fill', result.riskLevel === 'HIGH' ? 'bg-danger-500' : result.riskLevel === 'MEDIUM' ? 'bg-warn-500' : 'bg-accent-500')}
                    initial={{ width: 0 }}
                    animate={{ width: `${result.riskScore}%` }}
                    transition={{ duration: 1, ease: 'easeOut', delay: 0.3 }}
                  />
                </div>
              </div>

              {/* Flags */}
              <div className="glass-dark rounded-2xl p-5">
                <h3 className="text-white font-semibold mb-4 flex items-center gap-2">
                  <ShieldAlert size={15} className="text-brand-400" /> Detected Signals
                </h3>
                <div className="space-y-3">
                  {result.flags.map((flag, i) => (
                    <motion.div key={i} initial={{ opacity: 0, x: -10 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: i * 0.08 }}
                      className={cn('flex items-start gap-3 rounded-xl px-4 py-3', severityColor(flag.severity))}>
                      <span className="text-lg shrink-0">{flag.icon}</span>
                      <div>
                        <p className="font-semibold text-sm">{flag.label}</p>
                        <p className="text-xs opacity-80 mt-0.5 leading-relaxed">{flag.description}</p>
                      </div>
                    </motion.div>
                  ))}
                </div>
              </div>

              {/* Report links */}
              <div className="glass-dark rounded-2xl p-5">
                <h3 className="text-white font-semibold mb-3 flex items-center gap-2">
                  <ExternalLink size={14} className="text-warn-400" /> Report This Profile
                </h3>
                <div className="grid grid-cols-2 sm:grid-cols-3 gap-2">
                  {[
                    { name: 'Facebook',  url: 'https://www.facebook.com/help/report/1216349518398524', color: 'text-blue-400' },
                    { name: 'Instagram', url: 'https://help.instagram.com/contact/636276399721841',    color: 'text-pink-400' },
                    { name: 'TikTok',    url: 'https://support.tiktok.com/en/safety-hc/report-a-problem/report-a-user', color: 'text-cyan-400' },
                    { name: 'Twitter/X', url: 'https://help.twitter.com/en/safety-and-security/report-twitter-impersonation', color: 'text-sky-400' },
                    { name: 'YouTube',   url: 'https://support.google.com/youtube/answer/2802268',     color: 'text-red-400' },
                    { name: 'WhatsApp',  url: 'https://faq.whatsapp.com/663208052372782',              color: 'text-green-400' },
                  ].map(p => (
                    <a key={p.name} href={p.url} target="_blank" rel="noopener noreferrer"
                      className={cn('flex items-center gap-1.5 text-xs font-medium px-3 py-2 rounded-xl bg-white/5 hover:bg-white/10 transition-colors', p.color)}>
                      <ExternalLink size={11} /> {p.name}
                    </a>
                  ))}
                </div>
              </div>

              <button onClick={() => { setResult(null); setUsername(''); setBio(''); setUrl('') }}
                className="btn-secondary w-full">← Check Another Profile</button>
            </motion.div>
          )}
        </AnimatePresence>

        {/* Info box */}
        {!result && (
          <div className="flex items-start gap-3 text-xs text-slate-600">
            <Info size={13} className="shrink-0 mt-0.5" />
            <p>This tool analyses username patterns and bio content for common impersonation red flags. It does not access real social media accounts. Always verify accounts through official verification badges or websites.</p>
          </div>
        )}
      </div>
    </motion.div>
  )
}
