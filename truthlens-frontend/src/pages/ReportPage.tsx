import { useState, useRef } from 'react'
import { motion } from 'framer-motion'
import { Flag, Download, Share2, ExternalLink, CheckCircle, AlertTriangle, Copy } from 'lucide-react'
import { DetectionResult } from '../types'
import { cn } from '../utils/helpers'

const PAGE = {
  initial: { opacity: 0, y: 20 },
  animate: { opacity: 1, y: 0, transition: { duration: 0.5 } },
  exit:    { opacity: 0, y: -20, transition: { duration: 0.3 } },
}

const REPORT_PLATFORMS = [
  { name: 'Facebook',   icon: '📘', url: 'https://www.facebook.com/help/reportlinks',                          color: 'text-blue-400',   bg: 'bg-blue-500/10 border-blue-500/20' },
  { name: 'Instagram',  icon: '📸', url: 'https://help.instagram.com/contact/636276399721841',                 color: 'text-pink-400',   bg: 'bg-pink-500/10 border-pink-500/20' },
  { name: 'TikTok',     icon: '🎵', url: 'https://support.tiktok.com/en/safety-hc/report-a-problem',          color: 'text-cyan-400',   bg: 'bg-cyan-500/10 border-cyan-500/20' },
  { name: 'Twitter/X',  icon: '🐦', url: 'https://help.twitter.com/en/safety-and-security/report-abusive-behavior', color: 'text-sky-400', bg: 'bg-sky-500/10 border-sky-500/20' },
  { name: 'YouTube',    icon: '▶️', url: 'https://support.google.com/youtube/answer/2802268',                 color: 'text-red-400',    bg: 'bg-red-500/10 border-red-500/20' },
  { name: 'WhatsApp',   icon: '💬', url: 'https://faq.whatsapp.com/663208052372782',                           color: 'text-green-400',  bg: 'bg-green-500/10 border-green-500/20' },
  { name: 'Snapchat',   icon: '👻', url: 'https://support.snapchat.com/en-US/i-need-help',                    color: 'text-warn-400',   bg: 'bg-warn-500/10 border-warn-500/20' },
  { name: 'Africa Check',icon: '🌍',url: 'https://africacheck.org/fact-checks/submit-a-claim',                color: 'text-accent-400', bg: 'bg-accent-500/10 border-accent-500/20' },
]

const TRUSTED_ADULTS = [
  { name: 'Parent or Guardian',     icon: '👨‍👩‍👧', desc: 'Tell a parent or guardian what you found.' },
  { name: 'School Teacher',         icon: '👩‍🏫', desc: 'Show your teacher — they can help you respond safely.' },
  { name: 'School Counsellor',      icon: '🤝', desc: 'A counsellor can support you if the content is upsetting.' },
  { name: 'Uganda Police Cybercrime Division', icon: '🚔', desc: 'Report serious impersonation or threats to police.' },
]

export default function ReportPage() {
  const cardRef  = useRef<HTMLDivElement>(null)
  const [copied, setCopied] = useState(false)

  // Retrieve last result from localStorage if available
  const [result] = useState<DetectionResult | null>(() => {
    try { return JSON.parse(localStorage.getItem('last_scan_result') || 'null') } catch { return null }
  })

  const buildShareText = () => {
    if (!result) return 'I used Deepfake A.I Detector to check media for manipulation. Try it at http://localhost:3000'
    return `🔍 Deepfake A.I Detector Result\n\nFile: ${result.filename}\nVerdict: ${result.prediction} (${(result.confidence * 100).toFixed(1)}% confident)\nModel: ${result.modelUsed}\nDate: ${new Date(result.timestamp).toLocaleString()}\n\n${result.prediction === 'FAKE' ? '⚠️ This content appears to be manipulated. Do not share.' : '✅ This content appears authentic.'}\n\nCheck your own media at http://localhost:3000`
  }

  const copyReport = async () => {
    await navigator.clipboard.writeText(buildShareText())
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  const downloadReport = () => {
    const text = buildShareText()
    const blob = new Blob([text], { type: 'text/plain' })
    const a = document.createElement('a')
    a.href = URL.createObjectURL(blob)
    a.download = `deepfake-report-${Date.now()}.txt`
    a.click()
  }

  return (
    <motion.div {...PAGE} className="min-h-screen pt-20 hero-bg">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-10">

        {/* Header */}
        <div className="mb-8">
          <div className="flex items-center gap-3 mb-2">
            <div className="w-10 h-10 rounded-xl bg-red-600/20 flex items-center justify-center">
              <Flag size={20} className="text-red-400" />
            </div>
            <h1 className="font-display font-black text-2xl sm:text-4xl text-white">Report & Get Help</h1>
          </div>
          <p className="text-slate-400 ml-14">Found something suspicious? Here's how to report it and who to tell.</p>
        </div>

        <div className="grid lg:grid-cols-2 gap-6">
          {/* Left: Shareable result + report platforms */}
          <div className="space-y-5">

            {/* Shareable result card */}
            <div className="glass-dark rounded-2xl overflow-hidden">
              <div className="px-5 py-4 border-b border-white/8 flex items-center justify-between">
                <h3 className="text-white font-semibold flex items-center gap-2">
                  <Share2 size={15} className="text-brand-400" /> Your Last Scan Result
                </h3>
                <div className="flex items-center gap-2">
                  <button onClick={copyReport}
                    className={cn('flex items-center gap-1.5 text-xs px-3 py-1.5 rounded-lg transition-colors',
                      copied ? 'bg-accent-500/20 text-accent-400' : 'bg-white/5 text-slate-400 hover:bg-white/10')}>
                    {copied ? <><CheckCircle size={12} /> Copied!</> : <><Copy size={12} /> Copy</>}
                  </button>
                  <button onClick={downloadReport}
                    className="flex items-center gap-1.5 text-xs px-3 py-1.5 rounded-lg bg-white/5 text-slate-400 hover:bg-white/10 transition-colors">
                    <Download size={12} /> Save
                  </button>
                </div>
              </div>

              {/* Result summary card */}
              <div ref={cardRef} className="p-5">
                {result ? (
                  <div className={cn('rounded-xl p-4 border',
                    result.prediction === 'FAKE' ? 'bg-danger-500/10 border-danger-500/30' :
                    result.prediction === 'REAL' ? 'bg-accent-500/10 border-accent-500/30' :
                    'bg-warn-500/10 border-warn-500/30')}>
                    <div className="flex items-center gap-2 mb-3">
                      <span className="text-2xl">{result.prediction === 'FAKE' ? '🚨' : result.prediction === 'REAL' ? '✅' : '⚠️'}</span>
                      <div>
                        <p className={cn('font-bold', result.prediction === 'FAKE' ? 'text-danger-400' : result.prediction === 'REAL' ? 'text-accent-400' : 'text-warn-400')}>
                          {result.prediction === 'FAKE' ? 'Likely Fake / Manipulated' : result.prediction === 'REAL' ? 'Likely Authentic' : 'Uncertain'}
                        </p>
                        <p className="text-slate-500 text-xs">{(result.confidence * 100).toFixed(1)}% confident</p>
                      </div>
                    </div>
                    <div className="space-y-1 text-xs text-slate-400">
                      <p>📁 <span className="text-slate-300">{result.filename}</span></p>
                      <p>🤖 <span className="text-slate-300">{result.modelUsed}</span></p>
                      <p>🕐 <span className="text-slate-300">{new Date(result.timestamp).toLocaleString()}</span></p>
                    </div>
                  </div>
                ) : (
                  <div className="text-center py-6 text-slate-500 text-sm">
                    <Flag size={28} className="mx-auto mb-2 opacity-30" />
                    <p>No recent scan found. Scan a file first, then come back here to share the result.</p>
                  </div>
                )}
              </div>
            </div>

            {/* Platform report buttons */}
            <div className="glass-dark rounded-2xl p-5">
              <h3 className="text-white font-semibold mb-4 flex items-center gap-2">
                <Flag size={14} className="text-red-400" /> Report on a Platform
              </h3>
              <div className="grid grid-cols-2 gap-2">
                {REPORT_PLATFORMS.map(p => (
                  <a key={p.name} href={p.url} target="_blank" rel="noopener noreferrer"
                    className={cn('flex items-center gap-2 px-3 py-2.5 rounded-xl border text-sm font-medium transition-all hover:-translate-y-0.5', p.color, p.bg)}>
                    <span>{p.icon}</span> {p.name}
                    <ExternalLink size={11} className="ml-auto opacity-50" />
                  </a>
                ))}
              </div>
            </div>
          </div>

          {/* Right: Tell a trusted adult + emergency */}
          <div className="space-y-5">
            <div className="glass-dark rounded-2xl p-5">
              <h3 className="text-white font-semibold mb-4 flex items-center gap-2">
                <AlertTriangle size={14} className="text-warn-400" /> Tell a Trusted Adult
              </h3>
              <p className="text-slate-400 text-sm mb-4 leading-relaxed">
                If you've found content that involves you, a friend, or feels threatening — please don't handle it alone. Tell one of these people right away:
              </p>
              <div className="space-y-3">
                {TRUSTED_ADULTS.map((adult, i) => (
                  <div key={i} className="flex items-start gap-3 bg-white/5 rounded-xl px-4 py-3">
                    <span className="text-2xl">{adult.icon}</span>
                    <div>
                      <p className="text-white font-medium text-sm">{adult.name}</p>
                      <p className="text-slate-500 text-xs mt-0.5">{adult.desc}</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Safety rules */}
            <div className="glass-dark rounded-2xl p-5">
              <h3 className="text-white font-semibold mb-3 flex items-center gap-2">
                <CheckCircle size={14} className="text-accent-400" /> Golden Rules
              </h3>
              <ul className="space-y-3">
                {[
                  { icon: '🚫', rule: "Don't share it",        desc: "Even sharing to warn others spreads the fake further." },
                  { icon: '📢', rule: "Report it",             desc: "Use the platform's report button — every report helps." },
                  { icon: '🤐', rule: "Don't engage",          desc: "Commenting or reacting gives the post more reach." },
                  { icon: '🗣️', rule: "Tell someone",          desc: "You should never deal with harmful content alone." },
                  { icon: '📸', rule: "Take a screenshot first", desc: "Evidence helps when reporting to adults or authorities." },
                ].map(({ icon, rule, desc }) => (
                  <li key={rule} className="flex items-start gap-3">
                    <span className="text-xl shrink-0">{icon}</span>
                    <div>
                      <p className="text-white text-sm font-medium">{rule}</p>
                      <p className="text-slate-500 text-xs mt-0.5">{desc}</p>
                    </div>
                  </li>
                ))}
              </ul>
            </div>

            {/* Emergency */}
            <div className="rounded-2xl p-5 bg-danger-500/10 border border-danger-500/30">
              <p className="text-danger-400 font-semibold text-sm mb-2 flex items-center gap-2">
                <AlertTriangle size={14} /> If you feel unsafe right now
              </p>
              <p className="text-slate-400 text-sm leading-relaxed">
                Contact a trusted adult immediately, or call <strong className="text-white">Uganda Police: 999</strong> or the <strong className="text-white">Child Helpline: 116</strong> (free, 24/7).
              </p>
            </div>
          </div>
        </div>
      </div>
    </motion.div>
  )
}
