import { motion } from 'framer-motion'
import { Link } from 'react-router-dom'
import {
  Shield, Cpu, Database, Lock, ArrowRight, Eye, BookOpen,
  Code, Heart, GitBranch, Zap, Layers, Fingerprint,
} from 'lucide-react'

const fadeUp = {
  hidden: { opacity: 0, y: 30 },
  show: (i: number) => ({
    opacity: 1, y: 0,
    transition: { delay: i * 0.08, duration: 0.5, ease: [0.22, 1, 0.36, 1] },
  }),
}

const techStack = [
  { icon: Eye, name: 'React + Vite', desc: 'Fast, modern frontend with TypeScript', color: 'text-cyan-400' },
  { icon: Layers, name: 'Tailwind CSS', desc: 'Utility-first styling with custom design system', color: 'text-sky-400' },
  { icon: Cpu, name: 'Hugging Face Models', desc: 'Pretrained deepfake detection transformers', color: 'text-yellow-400' },
  { icon: Zap, name: 'FastAPI', desc: 'Python backend for async ML inference', color: 'text-emerald-400' },
  { icon: Fingerprint, name: 'C2PA SDK', desc: 'Content provenance & authenticity checking', color: 'text-indigo-400' },
  { icon: Database, name: 'FaceForensics++', desc: 'Training reference dataset for deepfake detection', color: 'text-purple-400' },
]

const principles = [
  {
    icon: Shield,
    title: 'Privacy First',
    desc: 'Files are analysed in-memory and never stored on our servers. Your data stays yours.',
  },
  {
    icon: BookOpen,
    title: 'Education Over Detection',
    desc: 'We don\'t just flag fakes — we teach you how to spot them yourself for lasting digital literacy.',
  },
  {
    icon: Lock,
    title: 'Transparency',
    desc: 'Every result shows confidence scores, the model used, and a detailed breakdown of findings.',
  },
  {
    icon: Heart,
    title: 'Built for Young People',
    desc: 'Kid-friendly language, clear guidance, and a focus on what to do — not just what\'s wrong.',
  },
]

const problemStats = [
  { value: '500K+', label: 'Deepfake videos online (and growing rapidly)', color: 'text-danger-400' },
  { value: '10M', label: 'AI-generated images shared daily', color: 'text-warn-400' },
  { value: '60%', label: 'Of teens can\'t reliably spot deepfakes', color: 'text-brand-400' },
  { value: '0', label: 'Cost to create a deepfake with free tools', color: 'text-accent-400' },
]

export default function AboutPage() {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0, transition: { duration: 0.5 } }}
      exit={{ opacity: 0, y: -20, transition: { duration: 0.3 } }}
      className="min-h-screen pt-20 hero-bg"
    >
      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        {/* Header */}
        <div className="text-center mb-16">
          <div className="inline-flex items-center gap-2 glass px-4 py-2 rounded-full text-sm text-brand-300 mb-4 border-brand-500/30">
            <Shield size={14} />
            How Deepfake A.I Detector Works
          </div>
          <h1 className="font-display font-black text-4xl md:text-5xl text-white mb-4">
            Built for a <span className="gradient-text">safer digital world</span>
          </h1>
          <p className="text-slate-400 text-lg max-w-2xl mx-auto">
          Deepfake A.I Detector combines AI detection with digital literacy education to help young people
            navigate manipulated media, deepfakes, and AI-generated content.
          </p>
        </div>

        {/* The Problem */}
        <motion.div
          variants={fadeUp}
          custom={0}
          initial="hidden"
          whileInView="show"
          viewport={{ once: true }}
          className="mb-16"
        >
          <h2 className="section-title mb-6">The Problem</h2>
          <p className="text-slate-400 leading-relaxed mb-8 max-w-3xl">
            Deepfakes and AI-generated content are becoming indistinguishable from reality.
            Young people — who spend the most time on social media — are most vulnerable to
            manipulation, impersonation, and misinformation. Existing tools detect fakes but
            don't teach people how to think critically about what they see.
          </p>
          <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4">
            {problemStats.map(({ value, label, color }, i) => (
              <motion.div
                key={i}
                custom={i + 1}
                variants={fadeUp}
                initial="hidden"
                whileInView="show"
                viewport={{ once: true }}
                className="card-hover text-center"
              >
                <div className={`font-display font-black text-3xl ${color} mb-2`}>{value}</div>
                <div className="text-slate-500 text-xs leading-relaxed">{label}</div>
              </motion.div>
            ))}
          </div>
        </motion.div>

        {/* The Solution */}
        <motion.div
          variants={fadeUp}
          custom={0}
          initial="hidden"
          whileInView="show"
          viewport={{ once: true }}
          className="mb-16"
        >
          <h2 className="section-title mb-6">Our Approach</h2>
          <p className="text-slate-400 leading-relaxed mb-8 max-w-3xl">
          Deepfake A.I Detector takes a two-pronged approach: AI-powered detection that analyses media for
            manipulation, paired with interactive education that builds lasting digital literacy
            skills. The goal isn't just to flag what's fake — it's to teach you how to think
            critically about everything you see online.
          </p>
          <div className="grid sm:grid-cols-2 gap-5">
            {principles.map(({ icon: Icon, title, desc }, i) => (
              <motion.div
                key={title}
                custom={i + 1}
                variants={fadeUp}
                initial="hidden"
                whileInView="show"
                viewport={{ once: true }}
                className="card-hover flex items-start gap-4"
              >
                <div className="w-10 h-10 rounded-xl bg-brand-600/15 flex items-center justify-center shrink-0">
                  <Icon size={18} className="text-brand-400" />
                </div>
                <div>
                  <h3 className="text-white font-semibold mb-1">{title}</h3>
                  <p className="text-slate-400 text-sm leading-relaxed">{desc}</p>
                </div>
              </motion.div>
            ))}
          </div>
        </motion.div>

        {/* Tech Stack */}
        <motion.div
          variants={fadeUp}
          custom={0}
          initial="hidden"
          whileInView="show"
          viewport={{ once: true }}
          className="mb-16"
        >
          <h2 className="section-title mb-6 flex items-center gap-2">
            <Code size={24} className="text-brand-400" />
            Technology Stack
          </h2>
          <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {techStack.map(({ icon: Icon, name, desc, color }, i) => (
              <motion.div
                key={name}
                custom={i + 1}
                variants={fadeUp}
                initial="hidden"
                whileInView="show"
                viewport={{ once: true }}
                className="card-hover"
              >
                <Icon size={22} className={color + ' mb-3'} />
                <h3 className="text-white font-semibold text-sm mb-1">{name}</h3>
                <p className="text-slate-500 text-xs leading-relaxed">{desc}</p>
              </motion.div>
            ))}
          </div>
        </motion.div>

        {/* How detection works */}
        <motion.div
          variants={fadeUp}
          custom={0}
          initial="hidden"
          whileInView="show"
          viewport={{ once: true }}
          className="mb-16"
        >
          <h2 className="section-title mb-6 flex items-center gap-2">
            <GitBranch size={24} className="text-purple-400" />
            Detection Pipeline
          </h2>
          <div className="glass-dark rounded-2xl p-6 md:p-8">
            <div className="space-y-1">
              {[
                { step: '1', title: 'File Upload', desc: 'User uploads an image or video via the secure web interface.' },
                { step: '2', title: 'Preprocessing', desc: 'Backend extracts frames (for video) and normalises input for the model.' },
                { step: '3', title: 'Model Inference', desc: 'Hugging Face deepfake detection models analyse the input for manipulation artifacts.' },
                { step: '4', title: 'Confidence Scoring', desc: 'Models output fake/real probabilities with per-component breakdowns.' },
                { step: '5', title: 'C2PA Check', desc: 'If available, content provenance metadata is verified for authenticity.' },
                { step: '6', title: 'Result Delivery', desc: 'User receives a clear verdict with confidence score, detailed breakdown, and safety guidance.' },
              ].map(({ step, title, desc }, i) => (
                <motion.div
                  key={step}
                  custom={i}
                  variants={fadeUp}
                  initial="hidden"
                  whileInView="show"
                  viewport={{ once: true }}
                  className="flex items-start gap-4 py-3 border-b border-white/5 last:border-0"
                >
                  <div className="w-8 h-8 rounded-lg bg-brand-600/20 text-brand-400 font-bold text-sm flex items-center justify-center shrink-0">
                    {step}
                  </div>
                  <div>
                    <h3 className="text-white font-medium text-sm">{title}</h3>
                    <p className="text-slate-500 text-xs mt-0.5">{desc}</p>
                  </div>
                </motion.div>
              ))}
            </div>
          </div>
        </motion.div>

        {/* CTA */}
        <motion.div
          variants={fadeUp}
          custom={0}
          initial="hidden"
          whileInView="show"
          viewport={{ once: true }}
          className="text-center glass rounded-3xl p-10 border-brand-500/20"
        >
          <Eye size={32} className="text-brand-400 mx-auto mb-4" />
          <h2 className="font-display font-bold text-3xl text-white mb-3">
            Start protecting yourself today
          </h2>
          <p className="text-slate-400 mb-8 max-w-md mx-auto">
            Try the detector, explore the lessons, or test your knowledge — all free, no account needed.
          </p>
          <div className="flex flex-col sm:flex-row items-center justify-center gap-3">
            <Link to="/detect" className="btn-primary flex items-center gap-2">
              <Shield size={16} /> Try Detector
            </Link>
            <Link to="/learn" className="btn-secondary flex items-center gap-2">
              <BookOpen size={16} /> Explore Lessons
            </Link>
            <Link to="/quiz" className="btn-secondary flex items-center gap-2">
              Take Quiz <ArrowRight size={14} />
            </Link>
          </div>
        </motion.div>
      </div>
    </motion.div>
  )
}
