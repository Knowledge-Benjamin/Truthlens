import { Routes, Route, useLocation } from 'react-router-dom'
import { AnimatePresence } from 'framer-motion'
import { LanguageProvider } from './i18n/LanguageContext'
import { ProgressProvider } from './context/ProgressContext'
import Navbar from './components/Navbar'
import Footer from './components/Footer'

// Core pages
import HomePage         from './pages/HomePage'
import DetectorPage     from './pages/DetectorPage'
import LearnPage        from './pages/LearnPage'
import QuizPage         from './pages/QuizPage'
import AboutPage        from './pages/AboutPage'

// New feature pages
import UrlScanPage       from './pages/UrlScanPage'
import CameraPage        from './pages/CameraPage'
import ImpersonationPage from './pages/ImpersonationPage'
import ProgressPage      from './pages/ProgressPage'
import ReportPage        from './pages/ReportPage'
import TextCheckPage     from './pages/TextCheckPage'
import SpotTheFakePage   from './pages/SpotTheFakePage'

function App() {
  const location = useLocation()

  return (
    <LanguageProvider>
      <ProgressProvider>
        <div className="min-h-screen flex flex-col">
          <Navbar />
          <main className="flex-1">
            <AnimatePresence mode="wait">
              <Routes location={location} key={location.pathname}>
                {/* Core */}
                <Route path="/"            element={<HomePage />} />
                <Route path="/detect"      element={<DetectorPage />} />
                <Route path="/learn"       element={<LearnPage />} />
                <Route path="/quiz"        element={<QuizPage />} />
                <Route path="/about"       element={<AboutPage />} />

                {/* Feature 1: URL Scanner */}
                <Route path="/url-scan"    element={<UrlScanPage />} />

                {/* Feature 2: Live Camera */}
                <Route path="/camera"      element={<CameraPage />} />

                {/* Feature 3: Impersonation Checker */}
                <Route path="/impersonation" element={<ImpersonationPage />} />

                {/* Feature 4: Progress & Badges */}
                <Route path="/progress"    element={<ProgressPage />} />

                {/* Feature 5+6: Report & Escalation */}
                <Route path="/report"      element={<ReportPage />} />

                {/* Feature 7: Text Fact-Checker */}
                <Route path="/text-check"  element={<TextCheckPage />} />

                {/* Feature 9: Spot the Fake */}
                <Route path="/spot"        element={<SpotTheFakePage />} />
              </Routes>
            </AnimatePresence>
          </main>
          <Footer />
        </div>
      </ProgressProvider>
    </LanguageProvider>
  )
}

export default App
