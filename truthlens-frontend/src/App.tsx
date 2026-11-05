import { Routes, Route, useLocation } from 'react-router-dom'
import { AnimatePresence } from 'framer-motion'
import Navbar from './components/Navbar'
import Footer from './components/Footer'
import HomePage from './pages/HomePage'
import DetectorPage from './pages/DetectorPage'
import LearnPage from './pages/LearnPage'
import QuizPage from './pages/QuizPage'
import AboutPage from './pages/AboutPage'

function App() {
  const location = useLocation()

  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      <main className="flex-1">
        <AnimatePresence mode="wait">
          <Routes location={location} key={location.pathname}>
            <Route path="/"        element={<HomePage />} />
            <Route path="/detect"  element={<DetectorPage />} />
            <Route path="/learn"   element={<LearnPage />} />
            <Route path="/quiz"    element={<QuizPage />} />
            <Route path="/about"   element={<AboutPage />} />
          </Routes>
        </AnimatePresence>
      </main>
      <Footer />
    </div>
  )
}

export default App
