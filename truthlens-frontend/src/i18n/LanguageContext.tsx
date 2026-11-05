import { createContext, useContext, useState, useEffect, ReactNode } from 'react'
import { LangCode, Translations, LANGUAGES, Language, translations } from './translations'

interface LanguageContextValue {
  lang: LangCode
  t: Translations
  language: Language
  setLang: (code: LangCode) => void
  languages: Language[]
}

const LanguageContext = createContext<LanguageContextValue>({
  lang: 'eng',
  t: translations.eng,
  language: LANGUAGES[0],
  setLang: () => {},
  languages: LANGUAGES,
})

const STORAGE_KEY = 'deepfake_ai_lang'

export function LanguageProvider({ children }: { children: ReactNode }) {
  const [lang, setLangState] = useState<LangCode>(() => {
    const stored = localStorage.getItem(STORAGE_KEY) as LangCode | null
    return stored && translations[stored] ? stored : 'eng'
  })

  const setLang = (code: LangCode) => {
    setLangState(code)
    localStorage.setItem(STORAGE_KEY, code)
  }

  // Update <html lang=""> attribute
  useEffect(() => {
    document.documentElement.lang = lang
  }, [lang])

  const value: LanguageContextValue = {
    lang,
    t: translations[lang],
    language: LANGUAGES.find(l => l.code === lang) ?? LANGUAGES[0],
    setLang,
    languages: LANGUAGES,
  }

  return (
    <LanguageContext.Provider value={value}>
      {children}
    </LanguageContext.Provider>
  )
}

export function useLanguage() {
  return useContext(LanguageContext)
}
