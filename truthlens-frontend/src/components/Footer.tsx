import { Link } from 'react-router-dom'
import { Eye, Shield, Heart } from 'lucide-react'
import { useLanguage } from '../i18n/LanguageContext'

export default function Footer() {
  const { t } = useLanguage()
  return (
    <footer className="bg-slate-950 border-t border-white/8 mt-auto">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
        <div className="grid grid-cols-2 md:grid-cols-4 gap-6 sm:gap-8">
          {/* Brand */}
          <div className="col-span-2 md:col-span-2">
            <div className="flex items-center gap-2 mb-3">
              <div className="w-7 h-7 rounded-lg bg-gradient-to-br from-brand-500 to-purple-600 flex items-center justify-center">
                <Eye size={14} className="text-white" />
              </div>
              <span className="font-display font-bold text-lg text-white">
                Deepfake<span className="gradient-text">A.I</span> Detector
              </span>
            </div>
            <p className="text-slate-500 text-sm leading-relaxed max-w-xs">
              {t.footer_desc}
            </p>
            <div className="flex items-center gap-1.5 mt-4 text-slate-600 text-xs">
              <Shield size={12} />
              <span>{t.footer_built}</span>
            </div>
          </div>

          {/* Links */}
          <div>
            <h4 className="text-white font-semibold text-sm mb-3">{t.footer_features}</h4>
            <ul className="space-y-2">
              {[
                { to: '/detect', label: 'Deepfake Detector' },
                { to: '/learn', label: 'Learning Hub' },
                { to: '/quiz', label: 'Digital Literacy Quiz' },
                { to: '/about', label: 'How It Works' },
              ].map(({ to, label }) => (
                <li key={to}>
                  <Link to={to} className="text-slate-500 hover:text-slate-300 text-sm transition-colors">
                    {label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          {/* Resources */}
          <div>
            <h4 className="text-white font-semibold text-sm mb-3">{t.footer_resources}</h4>
            <ul className="space-y-2">
              {[
                { href: 'https://huggingface.co', label: 'Hugging Face' },
                { href: 'https://contentauthenticity.org', label: 'C2PA Standard' },
                { href: 'https://github.com/ondyari/FaceForensics', label: 'FaceForensics++' },
                { href: 'https://www.kaggle.com', label: 'Kaggle Datasets' },
              ].map(({ href, label }) => (
                <li key={href}>
                  <a href={href} target="_blank" rel="noopener noreferrer"
                     className="text-slate-500 hover:text-slate-300 text-sm transition-colors">
                    {label} ↗
                  </a>
                </li>
              ))}
            </ul>
          </div>
        </div>

        <div className="border-t border-white/8 mt-8 pt-6 flex flex-col sm:flex-row items-center justify-between gap-3">
          <p className="text-slate-600 text-xs">{t.footer_copy}</p>
          <p className="text-slate-600 text-xs flex items-center gap-1">
            {t.footer_love} <Heart size={11} className="text-pink-500 fill-pink-500" />
          </p>
        </div>
      </div>
    </footer>
  )
}
