import { useState } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { Link } from 'react-router-dom'
import {
  BookOpen, AlertTriangle, Brain, CheckCircle, ArrowRight,
  Lightbulb, GraduationCap, Sparkles, ChevronRight,
} from 'lucide-react'
import { LessonModule, TipCard } from '../types'
import { useLanguage } from '../i18n/LanguageContext'
import { useContext } from 'react'
import { ProgressContext } from '../context/ProgressContext'

const fadeUp = {
  hidden: { opacity: 0, y: 30 },
  show: (i: number) => ({
    opacity: 1, y: 0,
    transition: { delay: i * 0.08, duration: 0.5, ease: [0.22, 1, 0.36, 1] },
  }),
}

const modules: LessonModule[] = [
  {
    id: 'intro',
    title: 'What is a Deepfake?',
    description: 'Learn how AI can create fake videos and images that look real, and why this matters for you.',
    icon: '🎬',
    color: 'from-brand-500 to-purple-600',
    duration: '5 min',
    level: 'Beginner',
    topics: ['How deepfakes are made', 'Why people create them', 'Real examples'],
  },
  {
    id: 'spotting',
    title: 'Spotting Fake Content',
    description: 'Train your eye to catch the visual clues that reveal manipulated media.',
    icon: '🔍',
    color: 'from-accent-500 to-teal-600',
    duration: '8 min',
    level: 'Beginner',
    topics: ['Facial clues', 'Lighting & shadows', 'Audio mismatch'],
  },
  {
    id: 'impersonation',
    title: 'Digital Impersonation',
    description: 'How bad actors pretend to be someone else online and what to watch for.',
    icon: '🎭',
    color: 'from-pink-500 to-rose-600',
    duration: '6 min',
    level: 'Intermediate',
    topics: ['Fake accounts', 'Voice cloning', 'Identity theft'],
  },
  {
    id: 'ai-generated',
    title: 'AI-Generated Images',
    description: 'Understand how AI creates fake images from scratch and how to tell them apart.',
    icon: '🤖',
    color: 'from-amber-500 to-orange-600',
    duration: '7 min',
    level: 'Intermediate',
    topics: ['Stable Diffusion', 'DALL·E outputs', 'Common artifacts'],
  },
  {
    id: 'provenance',
    title: 'Content Provenance & C2PA',
    description: 'How the C2PA standard helps verify where content came from and if it was modified.',
    icon: '🔐',
    color: 'from-indigo-500 to-blue-600',
    duration: '10 min',
    level: 'Advanced',
    topics: ['C2PA explained', 'Content credentials', 'Verification tools'],
  },
  {
    id: 'safety',
    title: 'Responding Safely',
    description: 'What to do when you spot manipulated content — reporting, not sharing, seeking help.',
    icon: '🛡️',
    color: 'from-emerald-500 to-green-600',
    duration: '5 min',
    level: 'Beginner',
    topics: ['Report & flag', 'Talk to an adult', 'Don\'t spread it'],
  },
]

const tips: TipCard[] = [
  { id: 1, title: 'Check the Eyes', description: 'Deepfakes often have unnatural or no blinking. Real people blink randomly.', icon: '👁️', category: 'Visual', color: 'bg-brand-500/15' },
  { id: 2, title: 'Look at Shadows', description: 'If the light direction on the face differs from the background, it may be manipulated.', icon: '🌑', category: 'Visual', color: 'bg-purple-500/15' },
  { id: 3, title: 'Listen Closely', description: 'Lip movements that don\'t match the audio is a classic deepfake tell.', icon: '🗣️', category: 'Audio', color: 'bg-pink-500/15' },
  { id: 4, title: 'Verify the Source', description: 'Was it posted by a trusted account? Check the original publisher.', icon: '✅', category: 'Source', color: 'bg-accent-500/15' },
  { id: 5, title: 'Reverse Image Search', description: 'Use Google Image Search to see if the image exists elsewhere online.', icon: '🔄', category: 'Tool', color: 'bg-teal-500/15' },
  { id: 6, title: 'Watch the Edges', description: 'Blurry or jagged edges around a face can indicate a face swap.', icon: '✂️', category: 'Visual', color: 'bg-amber-500/15' },
]

const lessonContent: Record<string, { heading: string; body: string[]; tip?: string }[]> = {
  intro: [
    {
      heading: 'What exactly is a deepfake?',
      body: [
        'A deepfake is a video, image, or audio clip created or altered using artificial intelligence. The name comes from "deep learning" (the AI technique) + "fake".',
        'AI models called GANs (Generative Adversarial Networks) can generate faces that never existed, swap one person\'s face onto another\'s body, or make someone appear to say things they never said.',
        'Not all deepfakes are bad — they\'re used in movies and entertainment. But when used to deceive, they can spread misinformation, harm reputations, or scam people.',
      ],
    },
    {
      heading: 'Why this matters for you',
      body: [
        'As deepfakes become more realistic, anyone can be fooled. Young people are especially targeted through social media.',
        'Being able to tell what\'s real helps you make better decisions about what to trust, share, and believe online.',
      ],
      tip: 'Remember: if something seems shocking or too good to be true, pause and verify before reacting.',
    },
  ],
  spotting: [
    {
      heading: 'Visual clues to watch for',
      body: [
        'Unnatural blinking — real people blink randomly; deepfakes may blink too regularly or not at all.',
        'Strange skin texture — look for patches that are too smooth or too blurry compared to surrounding areas.',
        'Inconsistent lighting — shadows on the face should match the light source in the scene.',
        'Warped backgrounds — check objects behind the person. Deepfakes sometimes distort them.',
      ],
    },
    {
      heading: 'Audio-visual mismatches',
      body: [
        'Lip sync errors — the mouth shape doesn\'t quite match the spoken words.',
        'Voice quality — cloned voices may sound flat, robotic, or have unnatural pauses.',
        'Emotional mismatch — the voice tone doesn\'t match the facial expression.',
      ],
      tip: 'Watch the video frame by frame if you can. Slowing it down often reveals artifacts hidden at normal speed.',
    },
  ],
  impersonation: [
    {
      heading: 'How digital impersonation works',
      body: [
        'Impersonators create fake accounts using someone else\'s photos, name, and details to trick others.',
        'Voice cloning AI can mimic someone\'s voice from just a few seconds of audio, enabling fake calls or messages.',
        'AI-generated video calls can make it look like a real person is speaking to you in real time.',
      ],
    },
    {
      heading: 'Protecting yourself',
      body: [
        'Be cautious of unexpected messages, even from people you know. Verify through a second channel.',
        'Set your social media profiles to private and limit what personal info you share publicly.',
        'Use unique profile photos that are hard to reverse-image-search.',
      ],
      tip: 'If a "friend" messages you urgently asking for money or codes, call them directly to confirm it\'s really them.',
    },
  ],
  'ai-generated': [
    {
      heading: 'How AI generates images',
      body: [
        'Tools like Stable Diffusion, DALL·E, and Midjourney create images from text prompts. They\'re trained on millions of real photos.',
        'The AI doesn\'t understand what it\'s drawing — it predicts pixel patterns. This leads to characteristic errors.',
        'Fully AI-generated faces are now nearly indistinguishable from real ones at a glance, but clues remain on closer inspection.',
      ],
    },
    {
      heading: 'Common AI image artifacts',
      body: [
        'Hands and fingers — AI famously struggles with hands. Look for extra fingers, merged digits, or odd positioning.',
        'Text and logos — rendered text is usually garbled or nonsensical.',
        'Symmetry — faces may be suspiciously symmetrical, or earrings/glasses mismatched between left and right.',
        'Background details — distant objects may dissolve into abstract shapes.',
      ],
      tip: 'Check hands, teeth, and text. These are the three things AI still gets wrong most often.',
    },
  ],
  provenance: [
    {
      heading: 'What is content provenance?',
      body: [
        'Provenance means knowing where content came from — who created it, when, and whether it was edited.',
        'The C2PA (Coalition for Content Provenance and Authenticity) standard embeds cryptographically signed metadata into media files.',
        'This "content credential" acts like a digital birth certificate for images and videos.',
      ],
    },
    {
      heading: 'How to verify provenance',
      body: [
        'Tools like ContentCredentials.org let you check if a file has valid C2PA metadata.',
        'Social platforms like Adobe, Microsoft, and BBC are adopting C2PA to flag verified content.',
        'If content has valid provenance, you can trust it more — if not, it doesn\'t mean it\'s fake, just unverified.',
      ],
      tip: 'Look for the "CR" (Content Credentials) icon on platforms that support C2PA.',
    },
  ],
  safety: [
    {
      heading: 'What to do when you spot fake content',
      body: [
        'Don\'t share it — even sharing to "warn others" spreads the fake further.',
        'Report it — use the platform\'s report/flag feature for misinformation or manipulated media.',
        'Tell someone — if it involves you, a friend, or feels threatening, tell a trusted adult immediately.',
      ],
    },
    {
      heading: 'Building good digital habits',
      body: [
        'Pause before sharing — give yourself 10 seconds to think "is this real?"',
        'Cross-check — see if reputable news sources are reporting the same thing.',
        'Use Deepfake A.I Detector — scan suspicious media with our detector tool.',
        'Help others — share what you learn with friends and family.',
      ],
      tip: 'Digital literacy is a skill. The more you practice spotting fakes, the better you get at it.',
    },
  ],
}

export default function LearnPage() {
  const [selectedModule, setSelectedModule] = useState<string | null>(null)
  const { t } = useLanguage()
  const progressCtx = useContext(ProgressContext)

  const handleModuleClick = (id: string) => {
    setSelectedModule(selectedModule === id ? null : id)
    if (selectedModule !== id) {
      progressCtx?.recordLesson(id)
    }
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0, transition: { duration: 0.5 } }}
      exit={{ opacity: 0, y: -20, transition: { duration: 0.3 } }}
      className="min-h-screen pt-20 hero-bg"
    >
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8 sm:py-12">
        {/* Header */}
        <div className="text-center mb-10 sm:mb-14">
          <div className="inline-flex items-center gap-2 glass px-4 py-2 rounded-full text-sm text-accent-300 mb-4 border-accent-500/30">
            <GraduationCap size={14} />
            {t.learn_badge}
          </div>
          <h1 className="font-display font-black text-3xl sm:text-4xl md:text-5xl text-white mb-4">
            {t.learn_title1} <span className="gradient-text">{t.learn_title2}</span>
          </h1>
          <p className="text-slate-400 text-lg max-w-2xl mx-auto">
            Interactive lessons that teach you how deepfakes work and how to protect yourself online.
            Pick a module to expand it.
          </p>
        </div>

        {/* Quick tips strip */}
        <div className="mb-10 sm:mb-14">
          <h2 className="flex items-center gap-2 text-white font-semibold mb-4 sm:mb-5">
            <Lightbulb size={18} className="text-warn-400" />
            {t.learn_tips_title}
          </h2>
          <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-3 sm:gap-4">
            {tips.map((tip, i) => (
              <motion.div
                key={tip.id}
                custom={i}
                variants={fadeUp}
                initial="hidden"
                whileInView="show"
                viewport={{ once: true }}
                className="card-hover group"
              >
                <div className="flex items-start gap-3">
                  <div className={`w-10 h-10 rounded-xl ${tip.color} flex items-center justify-center text-xl shrink-0`}>
                    {tip.icon}
                  </div>
                  <div>
                    <div className="flex items-center gap-2 mb-1">
                      <h3 className="text-white font-semibold text-sm">{tip.title}</h3>
                      <span className="text-[10px] text-slate-600 bg-white/5 px-1.5 py-0.5 rounded">{tip.category}</span>
                    </div>
                    <p className="text-slate-400 text-xs leading-relaxed">{tip.description}</p>
                  </div>
                </div>
              </motion.div>
            ))}
          </div>
        </div>

        {/* Modules */}
        <div>
          <h2 className="flex items-center gap-2 text-white font-semibold mb-5">
            <BookOpen size={18} className="text-brand-400" />
            {t.learn_modules_title}
          </h2>
          <div className="grid md:grid-cols-2 gap-3 sm:gap-4">
            {modules.map((mod, i) => (
              <motion.div
                key={mod.id}
                custom={i}
                variants={fadeUp}
                initial="hidden"
                whileInView="show"
                viewport={{ once: true }}
                className="card-hover cursor-pointer"
                onClick={() => handleModuleClick(mod.id)}
              >
                <div className="flex items-start gap-4">
                  <div className={`w-12 h-12 rounded-xl bg-gradient-to-br ${mod.color} flex items-center justify-center text-2xl shrink-0`}>
                    {mod.icon}
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 flex-wrap mb-1">
                      <h3 className="text-white font-semibold">{mod.title}</h3>
                      <span className="text-[10px] text-slate-500 bg-white/5 px-1.5 py-0.5 rounded">{mod.level}</span>
                      <span className="text-[10px] text-slate-600">⏱ {mod.duration}</span>
                    </div>
                    <p className="text-slate-400 text-sm leading-relaxed">{mod.description}</p>
                    <div className="flex flex-wrap gap-1.5 mt-3">
                      {mod.topics.map((topic) => (
                        <span key={topic} className="text-[11px] text-slate-500 bg-slate-800/60 px-2 py-0.5 rounded-md">
                          {topic}
                        </span>
                      ))}
                    </div>
                  </div>
                  <ChevronRight
                    size={18}
                    className={`text-slate-600 transition-transform duration-200 shrink-0 mt-1
                      ${selectedModule === mod.id ? 'rotate-90 text-brand-400' : ''}`}
                  />
                </div>

                {/* Expanded content */}
                <AnimatePresence>
                  {selectedModule === mod.id && (
                    <motion.div
                      initial={{ opacity: 0, height: 0 }}
                      animate={{ opacity: 1, height: 'auto' }}
                      exit={{ opacity: 0, height: 0 }}
                      transition={{ duration: 0.3 }}
                      className="mt-5 pt-5 border-t border-white/8 space-y-5"
                    >
                      {lessonContent[mod.id]?.map((section, si) => (
                        <div key={si}>
                          <h4 className="text-brand-300 font-medium text-sm mb-2 flex items-center gap-1.5">
                            <Sparkles size={13} />
                            {section.heading}
                          </h4>
                          <ul className="space-y-2">
                            {section.body.map((para, pi) => (
                              <li key={pi} className="flex items-start gap-2 text-slate-400 text-sm leading-relaxed">
                                <CheckCircle size={13} className="text-accent-500 mt-1 shrink-0" />
                                {para}
                              </li>
                            ))}
                          </ul>
                          {section.tip && (
                            <div className="mt-3 flex items-start gap-2 bg-warn-500/10 border border-warn-500/20 rounded-lg p-3">
                              <AlertTriangle size={13} className="text-warn-400 mt-0.5 shrink-0" />
                              <p className="text-warn-200 text-xs">{section.tip}</p>
                            </div>
                          )}
                        </div>
                      ))}
                    </motion.div>
                  )}
                </AnimatePresence>
              </motion.div>
            ))}
          </div>
        </div>

        {/* CTA */}
        <motion.div
          variants={fadeUp}
          custom={0}
          initial="hidden"
          whileInView="show"
          viewport={{ once: true }}
          className="mt-14 text-center glass rounded-2xl p-8 border-brand-500/20"
        >
          <Brain size={28} className="text-brand-400 mx-auto mb-4" />
          <h3 className="font-display font-bold text-2xl text-white mb-2">Ready to test your knowledge?</h3>
          <p className="text-slate-400 mb-6">Take our digital literacy quiz and see how much you've learned.</p>
          <Link to="/quiz" className="btn-primary inline-flex items-center gap-2">
            Start Quiz <ArrowRight size={16} />
          </Link>
        </motion.div>
      </div>
    </motion.div>
  )
}
