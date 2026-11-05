import { motion } from 'framer-motion'
import { Link } from 'react-router-dom'
import {
  Shield, Eye, BookOpen, Brain, ArrowRight, Zap,
  Users, CheckCircle, AlertTriangle, TrendingUp,
} from 'lucide-react'

const fadeUp = {
  hidden: { opacity: 0, y: 30 },
  show: (i: number) => ({
    opacity: 1, y: 0,
    transition: { delay: i * 0.1, duration: 0.6, ease: [0.22, 1, 0.36, 1] },
  }),
}

const features = [
  {
    icon: Shield,
    color: 'from-brand-500 to-purple-600',
    glow: 'brand',
    title: 'Deepfake Detector',
    description: 'Upload any image or video. Our AI analyses it for signs of manipulation with detailed confidence scores.',
    link: '/detect',
    cta: 'Try Now',
  },
  {
    icon: BookOpen,
    color: 'from-accent-500 to-teal-600',
    glow: 'green',
    title: 'Learning Hub',
    description: 'Interactive lessons on spotting manipulated media, understanding AI-generated content, and staying safe.',
    link: '/learn',
    cta: 'Start Learning',
  },
  {
    icon: Brain,
    color: 'from-pink-500 to-rose-600',
    glow: 'red',
    title: 'Digital Literacy Quiz',
    description: 'Test your knowledge with real-world scenarios. Build skills to identify fake content in everyday life.',
    link: '/quiz',
    cta: 'Take Quiz',
  },
]

const stats = [
  { value: '96%', label: 'Detection Accuracy', icon: TrendingUp },
  { value: '2.1B+', label: 'Deepfakes Online', icon: AlertTriangle },
  { value: '3 Models', label: 'AI Models Used', icon: Zap },
  { value: '< 5s', label: 'Analysis Time', icon: Users },
]

const steps = [
  {
    number: '01',
    title: 'Upload Media',
    description: 'Drag and drop or select any image or video file from your device.',
    color: 'text-brand-400',
  },
  {
    number: '02',
    title: 'AI Analysis',
    description: 'Our deepfake detection models scan for inconsistencies, artifacts, and manipulation traces.',
    color: 'text-purple-400',
  },
  {
    number: '03',
    title: 'Get Results',
    description: 'Receive a clear verdict with confidence score and a detailed explanation of findings.',
    color: 'text-pink-400',
  },
  {
    number: '04',
    title: 'Learn & Share',
    description: 'Understand what to look for and share results with others to promote digital safety.',
    color: 'text-accent-400',
  },
]

const warningTips = [
  'Check if the face matches the rest of the video',
  'Look for unnatural blinking or eye movement',
  'Watch for blurry or inconsistent edges around faces',
  'Verify the source — is it from a trusted account?',
  'Use Deepfake A.I Detector to scan suspicious media',
]

export default function HomePage() {
  return (
    <div className="hero-bg">
      {/* Hero */}
      <section className="relative pt-32 pb-24 px-4 sm:px-6 lg:px-8 overflow-hidden">
        {/* Background blobs */}
        <div className="absolute inset-0 overflow-hidden pointer-events-none">
          <div className="absolute top-20 left-1/4 w-96 h-96 bg-brand-600/10 rounded-full blur-3xl" />
          <div className="absolute top-40 right-1/4 w-80 h-80 bg-purple-600/8 rounded-full blur-3xl" />
          <div className="absolute bottom-0 left-1/2 -translate-x-1/2 w-[600px] h-48 bg-brand-900/20 rounded-full blur-3xl" />
        </div>

        <div className="max-w-5xl mx-auto text-center relative">
          {/* Badge */}
          <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ duration: 0.5 }}
            className="inline-flex items-center gap-2 glass px-4 py-2 rounded-full text-sm text-brand-300 mb-8 border-brand-500/30"
          >
            <div className="w-1.5 h-1.5 rounded-full bg-accent-400 animate-pulse" />
            Track 6 — AI for Digital Trust &amp; Protection
            <div className="badge-safe text-xs py-0.5">UCC Hackathon 2026</div>
          </motion.div>

          {/* Headline */}
          <motion.h1
            custom={0}
            variants={fadeUp}
            initial="hidden"
            animate="show"
            className="font-display font-black text-5xl sm:text-6xl lg:text-7xl text-white leading-[1.05] tracking-tight"
          >
            See Through{' '}
            <span className="relative inline-block">
              <span className="gradient-text">the Fake.</span>
              <svg className="absolute -bottom-2 left-0 w-full" height="6" viewBox="0 0 200 6" preserveAspectRatio="none">
                <path d="M0 3 Q50 0 100 3 Q150 6 200 3" stroke="url(#grad)" strokeWidth="2" fill="none" />
                <defs>
                  <linearGradient id="grad" x1="0%" y1="0%" x2="100%" y2="0%">
                    <stop offset="0%" stopColor="#6272f5" />
                    <stop offset="100%" stopColor="#ec4899" />
                  </linearGradient>
                </defs>
              </svg>
            </span>
          </motion.h1>

          <motion.p
            custom={1}
            variants={fadeUp}
            initial="hidden"
            animate="show"
            className="mt-8 text-xl text-slate-400 leading-relaxed max-w-2xl mx-auto"
          >
            Deepfake A.I Detector helps young people detect deepfakes, spot manipulated media, and build the digital literacy skills
            to stay safe in an AI-powered world.
          </motion.p>

          {/* CTAs */}
          <motion.div
            custom={2}
            variants={fadeUp}
            initial="hidden"
            animate="show"
            className="mt-10 flex flex-col sm:flex-row items-center justify-center gap-4"
          >
            <Link to="/detect" className="btn-primary text-base px-8 py-4 flex items-center gap-2 group">
              <Eye size={18} />
              Scan a File Now
              <ArrowRight size={16} className="group-hover:translate-x-1 transition-transform" />
            </Link>
            <Link to="/learn" className="btn-secondary text-base px-8 py-4 flex items-center gap-2">
              <BookOpen size={18} />
              Learn Digital Safety
            </Link>
          </motion.div>

          {/* Trust line */}
          <motion.div
            custom={3}
            variants={fadeUp}
            initial="hidden"
            animate="show"
            className="mt-8 flex items-center justify-center gap-6 text-slate-600 text-sm"
          >
            {['No account needed', 'Free to use', 'Privacy-first'].map((t) => (
              <span key={t} className="flex items-center gap-1.5">
                <CheckCircle size={13} className="text-accent-500" />
                {t}
              </span>
            ))}
          </motion.div>
        </div>

        {/* Animated preview card */}
        <motion.div
          initial={{ opacity: 0, y: 60, scale: 0.95 }}
          animate={{ opacity: 1, y: 0, scale: 1 }}
          transition={{ delay: 0.5, duration: 0.8, ease: [0.22, 1, 0.36, 1] }}
          className="max-w-2xl mx-auto mt-16 px-4"
        >
          <div className="glass rounded-3xl p-6 border-brand-500/20 relative overflow-hidden">
            <div className="absolute inset-0 bg-gradient-to-br from-brand-600/5 to-purple-600/5" />
            <div className="relative">
              <div className="flex items-center gap-3 mb-4">
                <div className="w-10 h-10 rounded-xl bg-danger-500/20 flex items-center justify-center">
                  <AlertTriangle size={18} className="text-danger-400" />
                </div>
                <div>
                  <p className="text-white font-semibold">Deepfake Detected</p>
                  <p className="text-slate-400 text-xs">sample_video.mp4</p>
                </div>
                <div className="ml-auto badge-danger">94.2% Fake</div>
              </div>
              <div className="space-y-3">
                {[
                  { label: 'Face Manipulation', score: 0.94, color: 'bg-danger-500' },
                  { label: 'Audio Sync Issues', score: 0.71, color: 'bg-warn-500' },
                  { label: 'Compression Artifacts', score: 0.88, color: 'bg-danger-500' },
                ].map((item) => (
                  <div key={item.label}>
                    <div className="flex justify-between text-xs text-slate-400 mb-1">
                      <span>{item.label}</span>
                      <span>{(item.score * 100).toFixed(0)}%</span>
                    </div>
                    <div className="progress-bar">
                      <motion.div
                        className={`progress-fill ${item.color}`}
                        initial={{ width: 0 }}
                        animate={{ width: `${item.score * 100}%` }}
                        transition={{ delay: 0.8 + Math.random() * 0.3, duration: 1, ease: 'easeOut' }}
                      />
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </motion.div>
      </section>

      {/* Stats */}
      <section className="py-16 px-4 sm:px-6 lg:px-8 border-t border-white/5">
        <div className="max-w-6xl mx-auto grid grid-cols-2 md:grid-cols-4 gap-6">
          {stats.map(({ value, label, icon: Icon }, i) => (
            <motion.div
              key={label}
              custom={i}
              variants={fadeUp}
              initial="hidden"
              whileInView="show"
              viewport={{ once: true }}
              className="text-center"
            >
              <div className="inline-flex items-center justify-center w-10 h-10 rounded-xl bg-brand-600/15 mb-3">
                <Icon size={18} className="text-brand-400" />
              </div>
              <div className="font-display font-black text-3xl text-white">{value}</div>
              <div className="text-slate-500 text-sm mt-1">{label}</div>
            </motion.div>
          ))}
        </div>
      </section>

      {/* Features */}
      <section className="py-24 px-4 sm:px-6 lg:px-8">
        <div className="max-w-6xl mx-auto">
          <motion.div
            variants={fadeUp}
            custom={0}
            initial="hidden"
            whileInView="show"
            viewport={{ once: true }}
            className="text-center mb-16"
          >
            <h2 className="section-title">Everything you need to stay safe</h2>
            <p className="section-subtitle mx-auto">
              Three powerful tools that work together to protect you from manipulated content online.
            </p>
          </motion.div>

          <div className="grid md:grid-cols-3 gap-6">
            {features.map(({ icon: Icon, color, title, description, link, cta }, i) => (
              <motion.div
                key={title}
                custom={i}
                variants={fadeUp}
                initial="hidden"
                whileInView="show"
                viewport={{ once: true }}
                className="card-hover group cursor-pointer"
              >
                <div className={`w-12 h-12 rounded-xl bg-gradient-to-br ${color} flex items-center justify-center mb-5
                                 group-hover:scale-110 transition-transform duration-300`}>
                  <Icon size={22} className="text-white" />
                </div>
                <h3 className="font-display font-bold text-xl text-white mb-2">{title}</h3>
                <p className="text-slate-400 text-sm leading-relaxed mb-6">{description}</p>
                <Link
                  to={link}
                  className="flex items-center gap-2 text-brand-400 text-sm font-medium hover:gap-3 transition-all duration-200"
                >
                  {cta} <ArrowRight size={14} />
                </Link>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* How It Works */}
      <section className="py-24 px-4 sm:px-6 lg:px-8 bg-slate-900/30">
        <div className="max-w-6xl mx-auto">
          <motion.div
            variants={fadeUp}
            custom={0}
            initial="hidden"
            whileInView="show"
            viewport={{ once: true }}
            className="text-center mb-16"
          >
            <h2 className="section-title">How Deepfake A.I Detector works</h2>
            <p className="section-subtitle mx-auto">
              Four simple steps from suspicious content to confident understanding.
            </p>
          </motion.div>

          <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {steps.map(({ number, title, description, color }, i) => (
              <motion.div
                key={number}
                custom={i}
                variants={fadeUp}
                initial="hidden"
                whileInView="show"
                viewport={{ once: true }}
                className="relative"
              >
                <div className="card h-full">
                  <div className={`font-display font-black text-4xl ${color} mb-4 opacity-60`}>{number}</div>
                  <h3 className="font-semibold text-white mb-2">{title}</h3>
                  <p className="text-slate-500 text-sm leading-relaxed">{description}</p>
                </div>
                {i < steps.length - 1 && (
                  <div className="hidden lg:block absolute top-1/3 right-0 translate-x-1/2 text-slate-700 z-10">
                    <ArrowRight size={20} />
                  </div>
                )}
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* Warning Tips */}
      <section className="py-24 px-4 sm:px-6 lg:px-8">
        <div className="max-w-4xl mx-auto">
          <motion.div
            variants={fadeUp}
            custom={0}
            initial="hidden"
            whileInView="show"
            viewport={{ once: true }}
            className="glass rounded-3xl p-8 md:p-12 border-warn-500/20 relative overflow-hidden"
          >
            <div className="absolute inset-0 bg-gradient-to-br from-warn-600/5 to-transparent" />
            <div className="relative">
              <div className="flex items-center gap-3 mb-6">
                <div className="w-10 h-10 rounded-xl bg-warn-500/20 flex items-center justify-center">
                  <AlertTriangle size={20} className="text-warn-400" />
                </div>
                <h2 className="font-display font-bold text-2xl text-white">Quick Spotting Guide</h2>
              </div>
              <p className="text-slate-400 mb-6">
                Before using the detector, train your eye with these key warning signs:
              </p>
              <ul className="space-y-3">
                {warningTips.map((tip, i) => (
                  <motion.li
                    key={i}
                    custom={i}
                    variants={fadeUp}
                    initial="hidden"
                    whileInView="show"
                    viewport={{ once: true }}
                    className="flex items-start gap-3"
                  >
                    <CheckCircle size={16} className="text-accent-400 mt-0.5 shrink-0" />
                    <span className="text-slate-300 text-sm">{tip}</span>
                  </motion.li>
                ))}
              </ul>
              <Link to="/learn" className="btn-primary inline-flex items-center gap-2 mt-8">
                <BookOpen size={16} />
                Learn More Techniques
              </Link>
            </div>
          </motion.div>
        </div>
      </section>

      {/* CTA Banner */}
      <section className="py-24 px-4 sm:px-6 lg:px-8">
        <div className="max-w-4xl mx-auto text-center">
          <motion.div
            variants={fadeUp}
            custom={0}
            initial="hidden"
            whileInView="show"
            viewport={{ once: true }}
          >
            <h2 className="font-display font-black text-4xl md:text-5xl text-white mb-6">
              Ready to spot the{' '}
              <span className="gradient-text">fake?</span>
            </h2>
            <p className="text-slate-400 text-lg mb-10 max-w-xl mx-auto">
              Upload a suspicious image or video and let our AI tell you if it's real within seconds.
            </p>
            <Link to="/detect" className="btn-primary text-lg px-10 py-4 inline-flex items-center gap-2 group">
              <Shield size={20} />
              Start Detecting
              <ArrowRight size={16} className="group-hover:translate-x-1 transition-transform" />
            </Link>
          </motion.div>
        </div>
      </section>
    </div>
  )
}
