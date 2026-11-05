/**
 * TruthLens — UI Translations
 * Languages: English, Luganda, Swahili, Acholi, Runyankole, Ateso
 * Powered by Sunbird AI (https://sunbird.ai)
 */

export type LangCode = 'eng' | 'lug' | 'swa' | 'ach' | 'nyn' | 'teo'

export interface Language {
  code: LangCode
  name: string
  nativeName: string
  flag: string
  rtl?: boolean
}

export const LANGUAGES: Language[] = [
  { code: 'eng', name: 'English',    nativeName: 'English',       flag: '🇺🇬' },
  { code: 'lug', name: 'Luganda',    nativeName: 'Luganda',       flag: '🇺🇬' },
  { code: 'swa', name: 'Swahili',    nativeName: 'Kiswahili',     flag: '🇰🇪' },
  { code: 'ach', name: 'Acholi',     nativeName: 'Lwo',           flag: '🇺🇬' },
  { code: 'nyn', name: 'Runyankole', nativeName: 'Runyankole',    flag: '🇺🇬' },
  { code: 'teo', name: 'Ateso',      nativeName: 'Ateso',         flag: '🇺🇬' },
]

export interface Translations {
  // Navbar
  nav_detect: string
  nav_learn: string
  nav_quiz: string
  nav_about: string
  nav_try: string

  // Home Hero
  home_badge: string
  home_hero_title1: string
  home_hero_title2: string
  home_hero_desc: string
  home_cta_scan: string
  home_cta_learn: string
  home_trust_1: string
  home_trust_2: string
  home_trust_3: string

  // Home Stats
  stat_accuracy: string
  stat_deepfakes: string
  stat_models: string
  stat_time: string

  // Home Features section
  home_features_title: string
  home_features_sub: string
  feat_detector_title: string
  feat_detector_desc: string
  feat_detector_cta: string
  feat_learn_title: string
  feat_learn_desc: string
  feat_learn_cta: string
  feat_quiz_title: string
  feat_quiz_desc: string
  feat_quiz_cta: string

  // Home How It Works
  home_how_title: string
  home_how_sub: string
  step1_title: string
  step1_desc: string
  step2_title: string
  step2_desc: string
  step3_title: string
  step3_desc: string
  step4_title: string
  step4_desc: string

  // Home Tips
  home_tips_title: string
  home_tips_sub: string
  tip_1: string
  tip_2: string
  tip_3: string
  tip_4: string
  tip_5: string
  tips_learn_btn: string

  // Home CTA
  home_cta_title1: string
  home_cta_title2: string
  home_cta_sub: string
  home_cta_btn: string

  // Detector page
  detect_title: string
  detect_sub: string
  detect_history: string
  detect_what_title: string
  detect_tip_title: string
  detect_tip_body: string
  detect_disclaimer: string
  detect_drop_title: string
  detect_drop_or: string
  detect_drop_browse: string
  detect_drop_hint: string
  detect_analyse_btn: string
  detect_analysing: string
  detect_recent: string

  // Analyzing
  analyzing_title: string
  analyze_step1: string
  analyze_step2: string
  analyze_step3: string
  analyze_step4: string

  // Result
  result_fake: string
  result_real: string
  result_uncertain: string
  result_fake_sub: string
  result_real_sub: string
  result_uncertain_sub: string
  result_confident: string
  result_fake_prob: string
  result_real_prob: string
  result_breakdown: string
  result_model: string
  result_time: string
  result_warn_title: string
  result_warn_body: string
  result_good_title: string
  result_good_body: string
  result_reset: string

  // Learn page
  learn_badge: string
  learn_title1: string
  learn_title2: string
  learn_sub: string
  learn_tips_title: string
  learn_modules_title: string

  // Quiz page
  quiz_badge: string
  quiz_title1: string
  quiz_title2: string
  quiz_sub: string
  quiz_start: string
  quiz_question_of: string
  quiz_score: string
  quiz_next: string
  quiz_see_results: string
  quiz_correct: string
  quiz_wrong: string
  quiz_results_title: string
  quiz_results_scored: string
  quiz_results_of: string
  quiz_retry: string
  quiz_review: string

  // About page
  about_badge: string
  about_title1: string
  about_title2: string
  about_sub: string
  about_problem_title: string
  about_approach_title: string
  about_tech_title: string
  about_pipeline_title: string
  about_cta_title: string
  about_cta_sub: string

  // Footer
  footer_desc: string
  footer_built: string
  footer_features: string
  footer_resources: string
  footer_copy: string
  footer_love: string

  // Language selector
  lang_select: string
  lang_powered: string
}

const en: Translations = {
  nav_detect: 'Detector', nav_learn: 'Learn', nav_quiz: 'Quiz', nav_about: 'About', nav_try: 'Try Detector →',
  home_badge: 'Track 6 — AI for Digital Trust & Protection',
  home_hero_title1: 'See Through', home_hero_title2: 'the Fake.',
  home_hero_desc: 'Deepfake A.I Detector helps young people detect deepfakes, spot manipulated media, and build the digital literacy skills to stay safe in an AI-powered world.',
  home_cta_scan: 'Scan a File Now', home_cta_learn: 'Learn Digital Safety',
  home_trust_1: 'No account needed', home_trust_2: 'Free to use', home_trust_3: 'Privacy-first',
  stat_accuracy: 'Detection Accuracy', stat_deepfakes: 'Deepfakes Online', stat_models: 'AI Models Used', stat_time: 'Analysis Time',
  home_features_title: 'Everything you need to stay safe', home_features_sub: 'Three powerful tools that work together to protect you from manipulated content online.',
  feat_detector_title: 'Deepfake Detector', feat_detector_desc: 'Upload any image or video. Our AI analyses it for signs of manipulation with detailed confidence scores.', feat_detector_cta: 'Try Now',
  feat_learn_title: 'Learning Hub', feat_learn_desc: 'Interactive lessons on spotting manipulated media, understanding AI-generated content, and staying safe.', feat_learn_cta: 'Start Learning',
  feat_quiz_title: 'Digital Literacy Quiz', feat_quiz_desc: 'Test your knowledge with real-world scenarios. Build skills to identify fake content in everyday life.', feat_quiz_cta: 'Take Quiz',
  home_how_title: 'How Deepfake A.I Detector works', home_how_sub: 'Four simple steps from suspicious content to confident understanding.',
  step1_title: 'Upload Media', step1_desc: 'Drag and drop or select any image or video file from your device.',
  step2_title: 'AI Analysis', step2_desc: 'Our deepfake detection models scan for inconsistencies, artifacts, and manipulation traces.',
  step3_title: 'Get Results', step3_desc: 'Receive a clear verdict with confidence score and a detailed explanation of findings.',
  step4_title: 'Learn & Share', step4_desc: 'Understand what to look for and share results with others to promote digital safety.',
  home_tips_title: 'Quick Spotting Guide', home_tips_sub: 'Before using the detector, train your eye with these key warning signs:',
  tip_1: 'Check if the face matches the rest of the video', tip_2: 'Look for unnatural blinking or eye movement', tip_3: 'Watch for blurry or inconsistent edges around faces', tip_4: "Verify the source — is it from a trusted account?", tip_5: 'Use Deepfake A.I Detector to scan suspicious media',
  tips_learn_btn: 'Learn More Techniques',
  home_cta_title1: 'Ready to spot the', home_cta_title2: 'fake?', home_cta_sub: "Upload a suspicious image or video and let our AI tell you if it's real within seconds.", home_cta_btn: 'Start Detecting',
  detect_title: 'Deepfake Detector', detect_sub: 'Upload any image or video to check for AI manipulation.', detect_history: 'History', detect_what_title: 'What we analyse', detect_tip_title: '📚 Tip of the day', detect_tip_body: 'Always check the source of content before sharing. Even if an image passes our detector, it may still be misleading when taken out of context.', detect_disclaimer: 'Deepfake A.I Detector uses AI models that are not 100% accurate. Results are for educational guidance only. Always verify important content through multiple trusted sources. Files are not stored after analysis.', detect_drop_title: 'Drag & drop a file', detect_drop_or: 'or', detect_drop_browse: 'click to browse', detect_drop_hint: 'Max 50 MB', detect_analyse_btn: 'Analyse This', detect_analysing: 'Analysing content…', detect_recent: 'Recent scans',
  analyzing_title: 'Analysing content…', analyze_step1: 'Scanning visual patterns…', analyze_step2: 'Analysing facial features…', analyze_step3: 'Running deepfake models…', analyze_step4: 'Compiling results…',
  result_fake: 'Likely Fake / Manipulated', result_real: 'Likely Authentic', result_uncertain: 'Uncertain', result_fake_sub: 'This content shows strong signs of AI manipulation or deepfake generation.', result_real_sub: 'No significant signs of manipulation were detected.', result_uncertain_sub: 'The model could not make a confident determination. Use caution.', result_confident: 'confident', result_fake_prob: 'Fake probability', result_real_prob: 'Real probability', result_breakdown: 'Detailed Analysis Breakdown', result_model: 'Model used', result_time: 'Processing time', result_warn_title: 'What should you do?', result_warn_body: "Don't share this content. Report it on the platform where you found it. If it involves someone you know, tell a trusted adult.", result_good_title: 'Good news!', result_good_body: 'This media appears to be authentic. However, always verify information from multiple sources — no AI is 100% perfect.', result_reset: '← Analyse Another File',
  learn_badge: 'Learning Hub', learn_title1: 'Learn to spot the', learn_title2: 'fake', learn_sub: 'Interactive lessons that teach you how deepfakes work and how to protect yourself online. Pick a module to expand it.',
  learn_tips_title: 'Quick Spotting Tips', learn_modules_title: 'Lesson Modules',
  quiz_badge: 'Digital Literacy Quiz', quiz_title1: 'Test Your', quiz_title2: 'Fake-Spotting Skills', quiz_sub: 'questions covering deepfakes, impersonation, AI-generated content, and online safety.', quiz_start: 'Start Quiz', quiz_question_of: 'Question', quiz_score: 'Score', quiz_next: 'Next Question', quiz_see_results: 'See Results', quiz_correct: 'Correct!', quiz_wrong: 'Not quite.', quiz_results_title: 'Your Results', quiz_results_scored: 'You scored', quiz_results_of: 'out of', quiz_retry: 'Try Again', quiz_review: 'Review Lessons',
  about_badge: 'How It Works', about_title1: 'Built for a', about_title2: 'safer digital world', about_sub: 'Deepfake A.I Detector combines AI detection with digital literacy education to help young people navigate manipulated media, deepfakes, and AI-generated content.', about_problem_title: 'The Problem', about_approach_title: 'Our Approach', about_tech_title: 'Technology Stack', about_pipeline_title: 'Detection Pipeline', about_cta_title: 'Start protecting yourself today', about_cta_sub: 'Try the detector, explore the lessons, or test your knowledge — all free, no account needed.',
  footer_desc: 'Helping young people stay safe online by detecting deepfakes, manipulated media, and misleading AI-generated content.', footer_built: 'Built for UCC Hackathon 2026 — Track 6: AI for Digital Trust', footer_features: 'Features', footer_resources: 'Resources', footer_copy: '© 2026 Deepfake A.I Detector. Made for educational purposes.', footer_love: 'Built with ❤ for a safer digital world',
  lang_select: 'Language', lang_powered: 'Translation by Sunbird AI',
}

const lug: Translations = {
  nav_detect: 'Okukebera', nav_learn: 'Okuyiga', nav_quiz: 'Ebibuuzo', nav_about: 'Ebikuusa', nav_try: 'Gezaako →',
  home_badge: 'Oluuyi 6 — AI olw\'Obwesigwa bw\'Dijito',
  home_hero_title1: 'Laba Mu', home_hero_title2: 'Obulimba.',
  home_hero_desc: 'Deepfake A.I Detector enyamba abaana okuzuula deepfakes, okulaba media efuuse, n\'okuzimba eby\'okumanya eby\'dijito okutaabika mu nsi ey\'AI.',
  home_cta_scan: 'Kebera Fayiro Kati', home_cta_learn: 'Yiga Obukuumi bw\'Dijito',
  home_trust_1: 'Teweetaaga akawunti', home_trust_2: 'Buntu', home_trust_3: 'Obukuumi bw\'obwannamugenzi',
  stat_accuracy: 'Butuufu bw\'Okuteeka', stat_deepfakes: 'Deepfakes ku Intaneeti', stat_models: 'Emizannyo gy\'AI', stat_time: 'Obudde bw\'Okuteeka',
  home_features_title: 'Ebintu byonna eby\'okukuuma obutaabika', home_features_sub: 'Ebyuma bisatu ebimaanyi ebikolagana okukuuma ku kukyusibwa kw\'ebintu ku intaneeti.',
  feat_detector_title: 'Omukebezi wa Deepfake', feat_detector_desc: 'Yisa fayiro oba videwo. AI yaffe ikebera obubonero bw\'okukyusa ne bibaako amazima g\'okukkiriza.', feat_detector_cta: 'Gezaako Kati',
  feat_learn_title: 'Kifo ky\'Okuyiga', feat_learn_desc: 'Amasomo ag\'okukusanyira agokulangirira okulaba media ekyusibwa, okumanya ebintu eby\'AI, n\'okutaabika.', feat_learn_cta: 'Tandika Okuyiga',
  feat_quiz_title: 'Ebibuuzo eby\'Okumanya Dijito', feat_quiz_desc: 'Gezaako obwangu bwo ne mbeera z\'ensi ey\'amazima. Zimba eby\'okumanya okuzuula ebintu eby\'obulimba mu buzimbe bw\'olunaku.', feat_quiz_cta: 'Wagira Ebibuuzo',
  home_how_title: 'Engeri Deepfake A.I Detector gye ikola', home_how_sub: 'Ennambiro nnya enyangu okuva ku bintu eby\'okukeberera okutuuka ku kumanya obwesigwa.',
  step1_title: 'Yisa Media', step1_desc: 'Wenya wamma oyimba fayiro eyo y\'ekifaananyi oba videwo okuva ku kyangu kyo.',
  step2_title: 'Okusesengula kwa AI', step2_desc: 'Emizannyo gye y\'okuzuula deepfakes igisesengula okw\'okukyusibwa, ebintu eby\'enjawulo, n\'obubonero bw\'okukyusibwa.',
  step3_title: 'Funa Ebivvuunuzi', step3_desc: 'Weebwa obuvuunuzi obulongoofu n\'amazima g\'okukkiriza ne nsomekola y\'ebivvuunuzi.',
  step4_title: 'Yiga no Gabana', step4_desc: 'Tegeera ebyo by\'okulinnya n\'ogabana ebivvuunuzi n\'abalala okukulaakulanya obukuumi bw\'dijito.',
  home_tips_title: 'Ekkubo Enyangu ly\'Okutegeera', home_tips_sub: 'Nga tonnatandika okukozesa omukebezi, weekolere amaaso go n\'obutonde obu bw\'okulaagira:',
  tip_1: 'Kebera nga amaaso gakolagana ne videwo yonna', tip_2: 'Linnya okukozimba oba okuzinza okutagenda busto', tip_3: 'Linnya obupotoboto oba enkola ey\'entebe ku maaso', tip_4: 'Kakasa ensibuko — okuva ku akawunti ak\'obwesigwa?', tip_5: 'Kozesa Deepfake A.I Detector okukebera media ey\'okukeberera',
  tips_learn_btn: 'Yiga Engeri Ezisingawo',
  home_cta_title1: 'Olimu okuzuula', home_cta_title2: 'obulimba?', home_cta_sub: 'Yisa ekifaananyi oba videwo eby\'okukeberera n\'olowoozebwe AI yaffe okukubuulira obuvuunuzi mu bitikirivu.', home_cta_btn: 'Tandika Okukebera',
  detect_title: 'Omukebezi wa Deepfake', detect_sub: 'Yisa ekifaananyi oba videwo okukebera okukyusibwa kwa AI.', detect_history: 'Ebyafaayo', detect_what_title: 'Bye tukebera', detect_tip_title: '📚 Obugumiikiriza bw\'olunaku', detect_tip_body: 'Bulijjo kebera ensibuko y\'ebintu nga tonnagabana. Newaakubadde ekifaananyi kyakiisa omukebezi waffe, kisobola okubeerawa okutagoberwa nga kiggibwa mu nsitaani y\'enjawulo.', detect_disclaimer: 'Deepfake A.I Detector ikozesa emizannyo gya AI egitali ya 100% butuufu. Ebivvuunuzi biri olw\'okuyigiriza kwokka. Bulijjo kakasa ebintu ebikowaavu mu nsibuko ezizingiddwa mu mazima. Fayiro zitabulikwa oluvannyuma lw\'okusesengulwa.', detect_drop_title: 'Wenya wamma oyimba fayiro', detect_drop_or: 'oba', detect_drop_browse: 'nyiga okunoonya', detect_drop_hint: 'Bw\'entono 50 MB', detect_analyse_btn: 'Sesengula Eno', detect_analysing: 'Okusesengula ebintu…', detect_recent: 'Okukebera okw\'amangu',
  analyzing_title: 'Okusesengula ebintu…', analyze_step1: 'Okusesengula emizannyo egy\'okutegeera…', analyze_step2: 'Okusesengula ebirala eby\'amaaso…', analyze_step3: 'Okukozesa emizannyo gy\'okuzuula deepfakes…', analyze_step4: 'Okukuŋŋaanya ebivvuunuzi…',
  result_fake: 'Okusobola Kuba Bulimba / Okukyusibwa', result_real: 'Okusobola Kuba Mazima', result_uncertain: 'Tekikakasibwa', result_fake_sub: 'Ekibintu kino kireeta obubonero obunafu bw\'okukyusibwa kwa AI oba okuzaala deepfake.', result_real_sub: 'Tewali bubonero bunafu bw\'okukyusibwa obutegeeredwa.', result_uncertain_sub: 'Omuzannyo tegasobodde okukakasa obuvuunuzi. Kola n\'amagezi.', result_confident: 'wekkiriza', result_fake_prob: 'Okusobola kuba bulimba', result_real_prob: 'Okusobola kuba mazima', result_breakdown: 'Okwongereza Okusesengula', result_model: 'Omuzannyo ogukozesebwa', result_time: 'Obudde bw\'okukola', result_warn_title: 'Kola ki?', result_warn_body: "Togabana ekibintu kino. Kibuulire ku kibinja kye wakizuula. Bwoba kikolagana n\'omuntu gwe omanya, buulira omuntu mukulu ow\'obwesigwa.", result_good_title: 'Akamwa katuukira!', result_good_body: 'Media eno erirabika nga mazima. Naye, bulijjo kakasa amawulire mu nsibuko ezizingiddwa mu mazima — tewali AI ya 100% butuufu.', result_reset: '← Sesengula Fayiro Endala',
  learn_badge: 'Kifo ky\'Okuyiga', learn_title1: 'Yiga Okulaba', learn_title2: 'obulimba', learn_sub: 'Amasomo ag\'okukusanyira agokulangirira engeri deepfakes gy\'ikola n\'engeri gy\'okwekuuma ku intaneeti. Nyiga module okugiggula.',
  learn_tips_title: 'Amateeka Amangu g\'Okutegeera', learn_modules_title: 'Emizigo gy\'Amasomo',
  quiz_badge: 'Ebibuuzo eby\'Okumanya Dijito', quiz_title1: 'Gezaako', quiz_title2: 'Eby\'Okumanya Ebintu eby\'Obulimba', quiz_sub: 'Ebibuuzo ebikuusa deepfakes, okukyusibwa, ebintu eby\'AI, n\'obukuumi ku intaneeti.', quiz_start: 'Tandika Ebibuuzo', quiz_question_of: 'Okubuuza', quiz_score: 'Amazima', quiz_next: 'Okubuuza Okuliddako', quiz_see_results: 'Laba Ebivvuunuzi', quiz_correct: 'Kituufu!', quiz_wrong: 'Tekituufu.', quiz_results_title: 'Ebivvuunuzi Byo', quiz_results_scored: 'Waweza amazima', quiz_results_of: 'mu', quiz_retry: 'Gezaako Nate', quiz_review: 'Kebera Amasomo',
  about_badge: 'Engeri Gye Ikola', about_title1: 'Ezimbiddwa olw\'', about_title2: 'ensi y\'dijito ey\'obukuumi', about_sub: 'Deepfake A.I Detector ikuŋŋaanya okuzuula kwa AI n\'amasomero g\'okumanya dijito okuyamba abaana okuyita mu media ekyusibwa, deepfakes, n\'ebintu eby\'AI.', about_problem_title: 'Obuzibu', about_approach_title: 'Enkola Yaffe', about_tech_title: 'Ebikozesebwa mu Tekinologiya', about_pipeline_title: 'Enkola y\'Okuzuula', about_cta_title: 'Tandika okwekuuma leero', about_cta_sub: 'Gezaako omukebezi, kebera amasomo, oba gezaako obwangu bwo — byonna buntu, wetaaga akawunti.',
  footer_desc: 'Enyamba abaana okutuuka ku butaabika ku intaneeti okuzuula deepfakes, media ekyusibwa, n\'ebintu eby\'AI eby\'okutataniriza.', footer_built: 'Ezimbiddwa olw\'UCC Hackathon 2026 — Oluuyi 6: AI olw\'Obwesigwa bw\'Dijito', footer_features: 'Ebirala', footer_resources: 'Ebisaakiro', footer_copy: '© 2026 Deepfake A.I Detector. Ezimbiddwa olw\'okuyigiriza.', footer_love: 'Ezimbiddwa n\'❤ olw\'ensi y\'dijito ey\'obukuumi',
  lang_select: 'Olulimi', lang_powered: 'Okuvvuunula kwa Sunbird AI',
}

const swa: Translations = {
  nav_detect: 'Kugundua', nav_learn: 'Kujifunza', nav_quiz: 'Maswali', nav_about: 'Kuhusu', nav_try: 'Jaribu →',
  home_badge: 'Wimbo 6 — AI kwa Uaminifu wa Dijiti',
  home_hero_title1: 'Ona Kupitia', home_hero_title2: 'Uongo.',
  home_hero_desc: 'Deepfake A.I Detector husaidia vijana kugundua deepfakes, kutambua midia iliyobadilishwa, na kujenga ujuzi wa kusoma na kuandika dijiti kukaa salama katika ulimwengu wa AI.',
  home_cta_scan: 'Chunguza Faili Sasa', home_cta_learn: 'Jifunza Usalama wa Dijiti',
  home_trust_1: 'Hakuna akaunti inayohitajika', home_trust_2: 'Bure kutumia', home_trust_3: 'Faragha kwanza',
  stat_accuracy: 'Usahihi wa Kugundua', stat_deepfakes: 'Deepfakes Mtandaoni', stat_models: 'Mifano ya AI', stat_time: 'Muda wa Uchambuzi',
  home_features_title: 'Kila unachohitaji kukaa salama', home_features_sub: 'Zana tatu zenye nguvu zinazofanya kazi pamoja kukuwalinda kutoka kwa maudhui yaliyodanganywa mtandaoni.',
  feat_detector_title: 'Kigundua Deepfake', feat_detector_desc: 'Pakia picha au video yoyote. AI yetu inaichunguza kwa ishara za udanganyifu na alama za uhakika za kina.', feat_detector_cta: 'Jaribu Sasa',
  feat_learn_title: 'Kituo cha Kujifunza', feat_learn_desc: 'Masomo ya maingiliano ya kutambua midia iliyodanganywa, kuelewa maudhui yaliyozalishwa na AI, na kukaa salama.', feat_learn_cta: 'Anza Kujifunza',
  feat_quiz_title: 'Maswali ya Ujuzi wa Dijiti', feat_quiz_desc: 'Jaribu maarifa yako na hali za ulimwengu halisi. Jenga ujuzi wa kutambua maudhui bandia katika maisha ya kila siku.', feat_quiz_cta: 'Fanya Maswali',
  home_how_title: 'Jinsi Deepfake A.I Detector inavyofanya kazi', home_how_sub: 'Hatua nne rahisi kutoka kwa maudhui yanayoshukiwa hadi uelewa wa kujiamini.',
  step1_title: 'Pakia Media', step1_desc: 'Buruta na kudondosha au chagua faili yoyote ya picha au video kutoka kwa kifaa chako.',
  step2_title: 'Uchambuzi wa AI', step2_desc: 'Mifano yetu ya kugundua deepfakes inachunguza kutofautiana, vifaa, na athari za udanganyifu.',
  step3_title: 'Pata Matokeo', step3_desc: 'Pokea uamuzi wazi na alama ya uhakika na maelezo ya kina ya matokeo.',
  step4_title: 'Jifunza na Shiriki', step4_desc: 'Elewa unachopaswa kutafuta na ushiriki matokeo na wengine kukuza usalama wa dijiti.',
  home_tips_title: 'Mwongozo wa Haraka wa Kutambua', home_tips_sub: 'Kabla ya kutumia kigundua, fundisha jicho lako na ishara hizi muhimu za onyo:',
  tip_1: 'Angalia kama uso unafanana na sehemu nyingine ya video', tip_2: 'Tafuta kupepesa macho kwa njia isiyo ya kawaida', tip_3: 'Angalia kingo zisizo wazi au zisizo sawa karibu na nyuso', tip_4: 'Thibitisha chanzo — je, ni kutoka kwa akaunti inayoaminika?', tip_5: 'Tumia Deepfake A.I Detector kuchunguza midia inayoshukiwa',
  tips_learn_btn: 'Jifunza Mbinu Zaidi',
  home_cta_title1: 'Uko tayari kutambua', home_cta_title2: 'uongo?', home_cta_sub: 'Pakia picha au video inayoshukiwa na uache AI yetu ikuambie kama ni ya kweli ndani ya sekunde.', home_cta_btn: 'Anza Kugundua',
  detect_title: 'Kigundua Deepfake', detect_sub: 'Pakia picha au video yoyote kuangalia udanganyifu wa AI.', detect_history: 'Historia', detect_what_title: 'Tunachochunguza', detect_tip_title: '📚 Kidokezo cha leo', detect_tip_body: 'Daima angalia chanzo cha maudhui kabla ya kushiriki. Hata kama picha inapita kigundua chetu, bado inaweza kupotosha inapochukuliwa nje ya muktadha.', detect_disclaimer: 'Deepfake A.I Detector hutumia mifano ya AI ambayo si 100% sahihi. Matokeo ni kwa mwongozo wa elimu tu. Daima thibitisha maudhui muhimu kupitia vyanzo vingi vinavyoaminika. Faili hazihifadhiwa baada ya uchambuzi.', detect_drop_title: 'Buruta na kudondosha faili', detect_drop_or: 'au', detect_drop_browse: 'bonyeza kuvinjari', detect_drop_hint: 'Kiwango cha juu 50 MB', detect_analyse_btn: 'Chunguza Hii', detect_analysing: 'Inachunguza maudhui…', detect_recent: 'Uchunguzi wa hivi karibuni',
  analyzing_title: 'Inachunguza maudhui…', analyze_step1: 'Inachunguza mifumo ya kuona…', analyze_step2: 'Inachunguza vipengele vya uso…', analyze_step3: 'Inaendesha mifano ya deepfake…', analyze_step4: 'Inakusanya matokeo…',
  result_fake: 'Uwezekano Bandia / Kubadilishwa', result_real: 'Uwezekano Halisi', result_uncertain: 'Kutokuwa na Uhakika', result_fake_sub: 'Maudhui haya yanaonyesha ishara kali za udanganyifu wa AI au utengenezaji wa deepfake.', result_real_sub: 'Hakuna ishara kubwa za udanganyifu zilizogundulika.', result_uncertain_sub: 'Mfano haukuweza kufanya uamuzi wa uhakika. Kuwa makini.', result_confident: 'uhakika', result_fake_prob: 'Uwezekano wa kuwa bandia', result_real_prob: 'Uwezekano wa kuwa halisi', result_breakdown: 'Uchambuzi wa Kina', result_model: 'Mfano uliotumika', result_time: 'Muda wa usindikaji', result_warn_title: 'Unapaswa kufanya nini?', result_warn_body: "Usishiriki maudhui haya. Ripoti kwenye jukwaa uliloyapata. Ikiwa inahusisha mtu unayemjua, mwambie mtu mzima anayeaminika.", result_good_title: 'Habari njema!', result_good_body: 'Media hii inaonekana kuwa ya kweli. Hata hivyo, daima thibitisha taarifa kutoka vyanzo vingi — hakuna AI iliyo 100% kamili.', result_reset: '← Chunguza Faili Nyingine',
  learn_badge: 'Kituo cha Kujifunza', learn_title1: 'Jifunza kutambua', learn_title2: 'uongo', learn_sub: 'Masomo ya maingiliano yanayokufundisha jinsi deepfakes zinavyofanya kazi na jinsi ya kujilinda mtandaoni. Chagua moduli kupanua.',
  learn_tips_title: 'Vidokezo vya Haraka vya Kutambua', learn_modules_title: 'Moduli za Masomo',
  quiz_badge: 'Maswali ya Ujuzi wa Dijiti', quiz_title1: 'Jaribu', quiz_title2: 'Ujuzi Wako wa Kutambua Uongo', quiz_sub: 'maswali yanayohusiana na deepfakes, uigaji, maudhui ya AI, na usalama mtandaoni.', quiz_start: 'Anza Maswali', quiz_question_of: 'Swali', quiz_score: 'Alama', quiz_next: 'Swali Lijalo', quiz_see_results: 'Ona Matokeo', quiz_correct: 'Sahihi!', quiz_wrong: 'Si sahihi.', quiz_results_title: 'Matokeo Yako', quiz_results_scored: 'Ulipata alama', quiz_results_of: 'kati ya', quiz_retry: 'Jaribu Tena', quiz_review: 'Kagua Masomo',
  about_badge: 'Jinsi Inavyofanya Kazi', about_title1: 'Imejengwa kwa', about_title2: 'ulimwengu salama wa dijiti', about_sub: 'Deepfake A.I Detector inachanganya ugunduzi wa AI na elimu ya ujuzi wa dijiti kusaidia vijana kupita midia iliyodanganywa, deepfakes, na maudhui ya AI.', about_problem_title: 'Tatizo', about_approach_title: 'Mbinu Yetu', about_tech_title: 'Teknolojia Inayotumika', about_pipeline_title: 'Mchakato wa Kugundua', about_cta_title: 'Anza kujilinda leo', about_cta_sub: 'Jaribu kigundua, chunguza masomo, au jaribu maarifa yako — yote bure, hakuna akaunti inayohitajika.',
  footer_desc: 'Husaidia vijana kukaa salama mtandaoni kwa kugundua deepfakes, midia iliyobadilishwa, na maudhui ya AI yanayopotosha.', footer_built: 'Imejengwa kwa UCC Hackathon 2026 — Wimbo 6: AI kwa Uaminifu wa Dijiti', footer_features: 'Vipengele', footer_resources: 'Rasilimali', footer_copy: '© 2026 Deepfake A.I Detector. Imefanywa kwa madhumuni ya elimu.', footer_love: 'Imejengwa kwa ❤ kwa ulimwengu salama wa dijiti',
  lang_select: 'Lugha', lang_powered: 'Tafsiri na Sunbird AI',
}

const ach: Translations = {
  nav_detect: 'Neno', nav_learn: 'Pwony', nav_quiz: 'Penyo', nav_about: 'Kom', nav_try: 'Tem →',
  home_badge: 'Bat 6 — AI pi Gen me Dijito',
  home_hero_title1: 'Neno Woko', home_hero_title2: 'Mwongo.',
  home_hero_desc: 'Deepfake A.I Detector konyo awobe neno deepfakes, lemo media ma olokke, ka gero ngec me dijito me bedo arum i nyen me AI.',
  home_cta_scan: 'Neno Faili Kombedi', home_cta_learn: 'Pwony Bedo Arum me Dijito',
  home_trust_1: 'Pe miyo akaunti', home_trust_2: 'Nono', home_trust_3: 'Gwoko mung mukwoŋo',
  stat_accuracy: 'Pire me Neno', stat_deepfakes: 'Deepfakes i Intanet', stat_models: 'Leŋ pa AI', stat_time: 'Kare me Ryeko',
  home_features_title: 'Gin ducu ma imito pi bedo arum', home_features_sub: 'Ginaraka adek ma tek ma tiyo kacel pi gwoki ki gin ma olokke i intanet.',
  feat_detector_title: 'Janeno me Deepfake', feat_detector_desc: 'Cwal cal onyo vidio mo kono. AI waŋwa ryeko pi nyuto me loko ka pire me gen.', feat_detector_cta: 'Tem Kombedi',
  feat_learn_title: 'Kabedo me Pwony', feat_learn_desc: 'Cwalo me pwony pi neno media ma olokke, niang gin pa AI, ka bedo arum.', feat_learn_cta: 'Cak Pwony',
  feat_quiz_title: 'Penyo pa Ngec me Dijito', feat_quiz_desc: 'Tem ngec maŋi ki twero pa piny ma ŋeyo. Ger ngec me neno gin ma mwongo i kwo me nino ducu.', feat_quiz_cta: 'Tim Penyo',
  home_how_title: 'Ngo ma Deepfake A.I Detector tiyo kwede', home_how_sub: 'Ruk aŋwen ma nok ki cen gin ma ipenyo oo i niang me gen.',
  step1_title: 'Cwal Media', step1_desc: 'Yot wiŋ onyo yer faili mo kono ya cal onyo vidio ki ginaraka maŋi.',
  step2_title: 'Ryeko pa AI', step2_desc: 'Leŋ waŋwa pa neno deepfakes ryeko pi loc ma pe ŋene, jami, ka nyuto me loko.',
  step3_title: 'Mak Lokke', step3_desc: 'Nwe lokke me gin maber ki pire me gen ka lok ma pire atoo.',
  step4_title: 'Pwony ka Gabo', step4_desc: 'Niang gin ma imiyo nenone ka gabo lokke ki jo mukene pi cweyo bedo arum me dijito.',
  home_tips_title: 'Buk Nyangu me Neno', home_tips_sub: 'Okwor itiyo ki janeno, pwony waŋ maŋi ki nyuto ma tek man me moni:',
  tip_1: 'Neno ka wangkweny nen macalo vidio yee', tip_2: 'Yeny pur waŋ ma pe ŋene', tip_3: 'Nen okworo ma pe wir ikot wangkweny', tip_4: 'Kenyo waŋe — oa ki akaunti ma giŋeyo?', tip_5: 'Tiyo ki Deepfake A.I Detector neno media ma ipenyo',
  tips_learn_btn: 'Pwony Kit Mukene',
  home_cta_title1: 'Itwero neno', home_cta_title2: 'mwongo?', home_cta_sub: 'Cwal cal onyo vidio ma ipenyo ka AI waŋwa bimiyo gin lokke i kare manok.', home_cta_btn: 'Cak Neno',
  detect_title: 'Janeno me Deepfake', detect_sub: 'Cwal cal onyo vidio mo kono pi neno loko pa AI.', detect_history: 'Gin mukato', detect_what_title: 'Gin ma waneno', detect_tip_title: '📚 Cik pa nino', detect_tip_body: 'Jami kenyo waŋ gin okwor i cako gabo. Nen ka cal owoto janeno waŋwa, twero pud orwenyo ka gitero i kabedo mukene.', detect_disclaimer: 'Deepfake A.I Detector tiyo ki leŋ pa AI ma pe 100% pire. Lokke ni pi twero me pwony keken. Jami kenyo gin ma tek ki jo mukene ma giŋeyo. Faili pe girwakke ka ryeko otum.', detect_drop_title: 'Yot wiŋ faili woko', detect_drop_or: 'onyo', detect_drop_browse: 'dii pi yeny', detect_drop_hint: 'Dit ma dit 50 MB', detect_analyse_btn: 'Ryeko Man', detect_analysing: 'Ryeko gin…', detect_recent: 'Neno macon manok',
  analyzing_title: 'Ryeko gin…', analyze_step1: 'Ryeko kit me neno…', analyze_step2: 'Ryeko kit pa wangkweny…', analyze_step3: 'Tiyo ki leŋ pa deepfake…', analyze_step4: 'Cuŋo lokke…',
  result_fake: 'Twero Mwongo / Olokke', result_real: 'Twero Adaa', result_uncertain: 'Pe Ŋeyo', result_fake_sub: 'Gin man nyuto nyuto tek me loko pa AI onyo gero pa deepfake.', result_real_sub: 'Nyuto mo ma tek pa loko pe ŋeno.', result_uncertain_sub: 'Leŋ pe otwero miyo lokke me gen. Bed ki cen.', result_confident: 'gen', result_fake_prob: 'Twero kuba mwongo', result_real_prob: 'Twero kuba adaa', result_breakdown: 'Ryeko me Twero Atoo', result_model: 'Leŋ ma otiyo', result_time: 'Kare me tiyo', result_warn_title: 'Itiyo ngo?', result_warn_body: "Pe gab gin man. Mwony i kabedo ma inene iye. Ka orwenyo dano ma iŋeyo, wac dano madit ma giŋeyo.", result_good_title: 'Bwoŋ maber!', result_good_body: 'Media man nene macalo adaa. Ento, jami kenyo ngec ki kabedo mukene — pe leŋ mo ma 100% pire.', result_reset: '← Ryeko Faili Mukene',
  learn_badge: 'Kabedo me Pwony', learn_title1: 'Pwony Neno', learn_title2: 'mwongo', learn_sub: 'Cwalo me pwony ma nyuto kit deepfakes tiyo kwede ka kit me gwoko i intanet. Yer modyu pi yabo.',
  learn_tips_title: 'Cik Nyangu me Neno', learn_modules_title: 'Modyu me Cwalo',
  quiz_badge: 'Penyo pa Ngec me Dijito', quiz_title1: 'Tem', quiz_title2: 'Ngec Maŋi pa Neno Mwongo', quiz_sub: 'penyo ku kom deepfakes, kwer, gin pa AI, ka bedo arum i intanet.', quiz_start: 'Cak Penyo', quiz_question_of: 'Penyo', quiz_score: 'Pire', quiz_next: 'Penyo Mukene', quiz_see_results: 'Neno Lokke', quiz_correct: 'Tye atir!', quiz_wrong: 'Pe atir.', quiz_results_title: 'Lokke Maŋi', quiz_results_scored: 'I mako pire', quiz_results_of: 'ki', quiz_retry: 'Tem Doki', quiz_review: 'Kenn Cwalo',
  about_badge: 'Ngo ma Tiyo Kwede', about_title1: 'Gerre pi', about_title2: 'piny me dijito ma arum', about_sub: 'Deepfake A.I Detector cuŋo ryeko pa AI ki pwony pa ngec me dijito konyo awobe woto i media ma olokke, deepfakes, ka gin pa AI.', about_problem_title: 'Peko', about_approach_title: 'Kit Waŋwa', about_tech_title: 'Tekinoloji ma Tiyo', about_pipeline_title: 'Kit me Neno', about_cta_title: 'Cak gwoko komi kombedi', about_cta_sub: 'Tem janeno, yer cwalo, onyo tem ngec maŋi — gin ducu nono, pe miyo akaunti.',
  footer_desc: 'Konyo awobe bedo arum i intanet pi neno deepfakes, media ma olokke, ka gin pa AI ma rweryo.', footer_built: 'Gerre pi UCC Hackathon 2026 — Bat 6: AI pi Gen me Dijito', footer_features: 'Ginaraka', footer_resources: 'Gin me Konyo', footer_copy: '© 2026 Deepfake A.I Detector. Gerre pi pwony.', footer_love: 'Gerre ki ❤ pi piny me dijito ma arum',
  lang_select: 'Leb', lang_powered: 'Lokke pa Sunbird AI',
}

const nyn: Translations = {
  nav_detect: 'Kurebera', nav_learn: 'Kwiga', nav_quiz: 'Ibibazo', nav_about: 'Ebikuhusu', nav_try: 'Gezaho →',
  home_badge: 'Umurongo 6 — AI kw\'Okwesigwa kw\'Dijito',
  home_hero_title1: 'Reba Ahabw\'', home_hero_title2: 'Obukoogi.',
  home_hero_desc: 'Deepfake A.I Detector aganyisa abantu abaato kurebera deepfakes, okubona media eyakyusirwa, no kwaaha obushobozi bw\'okusoma dijito okuhara obwhereza mu nsi y\'AI.',
  home_cta_scan: 'Reberera Fairu Hati', home_cta_learn: 'Wiga Obukuumi bw\'Dijito',
  home_trust_1: 'Nta akaunti erakwa', home_trust_2: 'Birengyuka', home_trust_3: 'Obwhereza bw\'omuntu',
  stat_accuracy: 'Obushobozi bw\'Okurebera', stat_deepfakes: 'Deepfakes kw\'Intaneti', stat_models: 'Emizannyo y\'AI', stat_time: 'Obuhe bw\'Okureberera',
  home_features_title: 'Ebyona birakwetaaga okuhara obwhereza', home_features_sub: 'Ebyuma bisatu ebimaanyi ebikolagana kukugaraga ahabw\'ebintu ebikyusirwa kw\'intaneti.',
  feat_detector_title: 'Omureberero wa Deepfake', feat_detector_desc: 'Tuma ekifaananyi cyangwa videwo cyona. AI yaitu ireberera obubonero bw\'okukyusibwa n\'amazima g\'okwesiga.', feat_detector_cta: 'Gezaho Hati',
  feat_learn_title: 'Hamwe h\'Okwiga', feat_learn_desc: 'Amasomo ag\'okuganana agareba media eyakyusirwa, okumanya ebintu eby\'AI, no guma muhimu.', feat_learn_cta: 'Tandika Kwiga',
  feat_quiz_title: 'Ibibazo by\'Okumanya Dijito', feat_quiz_desc: 'Gezaho ubwenge bwawe n\'embeera z\'isi y\'amazima. Aaha ubushobozi bw\'okubona ebintu eby\'obukoogi mu buzimbe bw\'eizooba.', feat_quiz_cta: 'Kora Ibibazo',
  home_how_title: 'Engeri Deepfake A.I Detector ikoramu', home_how_sub: 'Intambwe ine nkorwa okwerinda ebintu eby\'okukeberera okutuuka ku kumanya kwesiga.',
  step1_title: 'Tuma Media', step1_desc: 'Yeronkeza cyangwa toora fairu yona y\'ekifaananyi cyangwa videwo kuruga ku nyandiiko yawe.',
  step2_title: 'Okureberera kw\'AI', step2_desc: 'Emizannyo yaitu y\'okuzuura deepfakes ireberera okutafaana, ebintu n\'obubonero bw\'okukyusibwa.',
  step3_title: 'Bonera Emigyezo', step3_desc: 'Bonesha emigyezo ehangire n\'amazima g\'okwesiga n\'ekirangiriizi ky\'emigyezo.',
  step4_title: 'Wiga no Gaba', step4_desc: 'Tegeerera ebigatuukire okurebera no gaba emigyezo n\'abandi okushimbura obukuumi bw\'dijito.',
  home_tips_title: 'Ebiragiro by\'Okubona', home_tips_sub: 'Eriyo otaandika kukozesa omureberero, meesho yawe n\'obutonde obu bw\'okulaagirira:',
  tip_1: 'Reberera nti amaaso gakolagana na videwo yona', tip_2: 'Reba okukozimba kwe kutakora busto', tip_3: 'Reba obupotoboto mu bantu nk\'enkera', tip_4: 'Kakasa ensibuko — okuva ku akaunti y\'okwesiga?', tip_5: 'Kozesa Deepfake A.I Detector okureberera media y\'okukeberera',
  tips_learn_btn: 'Wiga Engeri Ziziingira',
  home_cta_title1: 'Orinze kubona', home_cta_title2: 'obukoogi?', home_cta_sub: 'Tuma ekifaananyi cyangwa videwo eby\'okukeberera no AI yaitu ikubuulire emigyezo mu kanya.', home_cta_btn: 'Tandika Okurebera',
  detect_title: 'Omureberero wa Deepfake', detect_sub: 'Tuma ekifaananyi cyangwa videwo yona okureberera okukyusibwa kw\'AI.', detect_history: 'Ebyabaho', detect_what_title: 'Ebiratureberera', detect_tip_title: '📚 Ikiragiro ky\'eizooba', detect_tip_body: 'Buri gihe reberera ensibuko y\'ebintu eriyo otanaabiha. Aho ekifaananyi kyaatura omureberero waitu, kiringine kigarage nk\'ekitatunuuka iyo kitwa mu nsibuko y\'enjawulo.', detect_disclaimer: 'Deepfake A.I Detector ikozesa emizannyo y\'AI etali ya 100% butuufu. Emigyezo ni y\'ekiragiro ky\'okwiga ky\'okukwita. Buri gihe kakasa ebintu ebikowaavu mu nsibuko eziziingira. Fairu zitebesha okuva okureberera kwaamarira.', detect_drop_title: 'Yeronkeza fairu', detect_drop_or: 'cyangwa', detect_drop_browse: 'nyiga kunoonya', detect_drop_hint: 'Bigiire 50 MB', detect_analyse_btn: 'Reberera Eki', detect_analysing: 'Okureberera ebintu…', detect_recent: 'Okureberera kwahise mangu',
  analyzing_title: 'Okureberera ebintu…', analyze_step1: 'Okureberera emizannyo y\'okuteeka…', analyze_step2: 'Okureberera ebirala by\'amaaso…', analyze_step3: 'Okukozesa emizannyo y\'okuzuura deepfakes…', analyze_step4: 'Okukunngaanya emigyezo…',
  result_fake: 'Okusobora Kuba Obukoogi / Obukyusirwa', result_real: 'Okusobora Kuba Amazima', result_uncertain: 'Kutakakasibwa', result_fake_sub: 'Ekintu kino kiragira obubonero obumaanyi bw\'okukyusibwa kw\'AI cyangwa okworeka deepfake.', result_real_sub: 'Nta bubonero bumaanyi bw\'okukyusibwa bwabonetse.', result_uncertain_sub: 'Omuzannyo taashobora guha emigyezo y\'okwesiga. Kora n\'amagezi.', result_confident: 'kwesiga', result_fake_prob: 'Okusobora kuba obukoogi', result_real_prob: 'Okusobora kuba amazima', result_breakdown: 'Okureberera kw\'Ahabw\'Amaani', result_model: 'Omuzannyo ogukozesebwa', result_time: 'Obuhe bw\'okukora', result_warn_title: 'Okora ki?', result_warn_body: "Togaba ekintu kino. Kubikira ku kigoropa kye wakizuura. Nk\'ekireba omuntu ogunawe, buulira omuntu mukuru ow\'okwesiga.", result_good_title: 'Amakuru meza!', result_good_body: 'Media ino iragaruka nk\'amazima. Naho, buri gihe kakasa amakuru mu nsibuko eziziingira — nta AI ya 100% butuufu.', result_reset: '← Reberera Fairu Endala',
  learn_badge: 'Hamwe h\'Okwiga', learn_title1: 'Wiga Okubona', learn_title2: 'obukoogi', learn_sub: 'Amasomo ag\'okuganana agareba engeri deepfakes z\'ikoramu n\'engeri y\'okwigaraga kw\'intaneti. Toora moduru okugugula.',
  learn_tips_title: 'Ebiragiro by\'Okubona', learn_modules_title: 'Emizigo y\'Amasomo',
  quiz_badge: 'Ibibazo by\'Okumanya Dijito', quiz_title1: 'Gezaho', quiz_title2: 'Ubwenge bwawe bw\'Okubona Obukoogi', quiz_sub: 'ibibazo ku deepfakes, okukyusibwa, ebintu by\'AI, n\'obukuumi kw\'intaneti.', quiz_start: 'Tandika Ibibazo', quiz_question_of: 'Ikibazo', quiz_score: 'Amazima', quiz_next: 'Ikibazo Gikurikira', quiz_see_results: 'Reba Emigyezo', quiz_correct: 'N\'amazima!', quiz_wrong: 'Takiri mazima.', quiz_results_title: 'Emigyezo Yawe', quiz_results_scored: 'Wayera amazima', quiz_results_of: 'mu', quiz_retry: 'Gezaho Indi', quiz_review: 'Reberera Amasomo',
  about_badge: 'Engeri Ikoramu', about_title1: 'Yazimbwa kw\'', about_title2: 'isi y\'dijito y\'obukuumi', about_sub: 'Deepfake A.I Detector ikunngaanya okureberera kw\'AI n\'amasomero g\'okumanya dijito okuganyisa abaana bakwate mu media eyakyusirwa, deepfakes, n\'ebintu by\'AI.', about_problem_title: 'Obuzibu', about_approach_title: 'Engeri Yaitu', about_tech_title: 'Tekinoloji Ebikozesebwa', about_pipeline_title: 'Enkola y\'Okureberera', about_cta_title: 'Tandika okwigaraga leero', about_cta_sub: 'Gezaho omureberero, laba amasomo, cyangwa gezaho ubwenge bwawe — byona birengyuka, nta akaunti erakwa.',
  footer_desc: 'Aganyisa abantu abaato guma bwhereza kw\'intaneti okureberera deepfakes, media eyakyusirwa, n\'ebintu by\'AI ebiryangiraniza.', footer_built: 'Yazimbwa kw\'UCC Hackathon 2026 — Umurongo 6: AI kw\'Okwesigwa kw\'Dijito', footer_features: 'Ebirala', footer_resources: 'Ebisaakiro', footer_copy: '© 2026 Deepfake A.I Detector. Yazimbwa kw\'okwiga.', footer_love: 'Yazimbwa n\'❤ kw\'isi y\'dijito y\'obukuumi',
  lang_select: 'Orulimi', lang_powered: 'Kukyusibwa kw\'Sunbird AI',
}

const teo: Translations = {
  nav_detect: 'Etal', nav_learn: 'Ining', nav_quiz: 'Ibala', nav_about: 'Kom', nav_try: 'Tem →',
  home_badge: 'Apata 6 — AI pi Edeke me Dijito',
  home_hero_title1: 'Noo Amam', home_hero_title2: 'Apara.',
  home_hero_desc: 'Deepfake A.I Detector ebwotokin aitare etal deepfakes, noo media en apara, ka ikwas ngec me dijito pi bedo arum i nyen me AI.',
  home_cta_scan: 'Etal Failu Nisio', home_cta_learn: 'Ining Bedo Arum me Dijito',
  home_trust_1: 'Mun akaunti tuu', home_trust_2: 'Nono', home_trust_3: 'Gwak mung mukwon',
  stat_accuracy: 'Pire me Etal', stat_deepfakes: 'Deepfakes i Intaneti', stat_models: 'Leŋ pa AI', stat_time: 'Ikobo me Ryeko',
  home_features_title: 'Gin ducu itakon pi bedo arum', home_features_sub: 'Ginaraka adek ma tek en tiyo kacel pi gwokin ki gin ma apara i intaneti.',
  feat_detector_title: 'Jaetal me Deepfake', feat_detector_desc: 'Tum ediyo oba videwo mo kono. AI waŋwa iryeko pi nyuto me loko ka pire me gen.', feat_detector_cta: 'Tem Nisio',
  feat_learn_title: 'Kabedo me Ining', feat_learn_desc: 'Cwalo me ining pi noo media ma apara, niang gin pa AI, ka bedo arum.', feat_learn_cta: 'Icak Ining',
  feat_quiz_title: 'Ibala pa Ngec me Dijito', feat_quiz_desc: 'Tem ngec maŋi ki embeera pa isi ma ŋeyo. Ikwas ngec me noo gin ma mwongo i kwo me nino ducu.', feat_quiz_cta: 'Tim Ibala',
  home_how_title: 'Ngo ma Deepfake A.I Detector itiyo kwede', home_how_sub: 'Ikwacu ane ma nok ki gin en apara oo i niang me gen.',
  step1_title: 'Tum Media', step1_desc: 'Ikim onyo iyer failu mo kono ya ediyo onyo videwo ki ginaraka maŋi.',
  step2_title: 'Ryeko pa AI', step2_desc: 'Leŋ waŋwa pa etal deepfakes iryeko pi lok ma pe ŋene, jami, ka nyuto me loko.',
  step3_title: 'Mak Lokke', step3_desc: 'Nwe lokke me gin maber ki pire me gen ka lok ma pire atoo.',
  step4_title: 'Ining ka Gabo', step4_desc: 'Niang gin ma imiyo inenno ka gabo lokke ki jo mukene pi cwayo bedo arum me dijito.',
  home_tips_title: 'Buk Nyangu me Noo', home_tips_sub: 'Okwor itiyo ki jaetal, ining wangi maŋi ki nyuto man me moni:',
  tip_1: 'Noo ka wangkweny nen nen macalo videwo yee', tip_2: 'Iyer pur wangi ma pe ŋene', tip_3: 'Nen okworo ma pe wir ikot wangkweny', tip_4: 'Kenyo waŋe — oa ki akaunti ma giŋeyo?', tip_5: 'Tiyo ki Deepfake A.I Detector etal media ma ipenyo',
  tips_learn_btn: 'Ining Kit Mukene',
  home_cta_title1: 'Itweron noo', home_cta_title2: 'apara?', home_cta_sub: 'Tum ediyo onyo videwo ma ipenyo ka AI waŋwa bimiyo lokke i ikobo manok.', home_cta_btn: 'Icak Etal',
  detect_title: 'Jaetal me Deepfake', detect_sub: 'Tum ediyo onyo videwo mo kono pi etal loko pa AI.', detect_history: 'Gin mukato', detect_what_title: 'Gin ma wanoo', detect_tip_title: '📚 Cik pa nino', detect_tip_body: 'Jami kenyo waŋ gin okwor i cako gabo. Nen ka ediyo owoto jaetal waŋwa, twero pud orwenyo ka gitero i kabedo mukene.', detect_disclaimer: 'Deepfake A.I Detector itiyo ki leŋ pa AI ma pe 100% pire. Lokke ni pi twero me ining keken. Jami kenyo gin ma tek ki jo mukene ma giŋeyo. Failu pe girwakke ka ryeko otum.', detect_drop_title: 'Ikim failu woko', detect_drop_or: 'onyo', detect_drop_browse: 'dii pi iyer', detect_drop_hint: 'Dit ma dit 50 MB', detect_analyse_btn: 'Ryeko Man', detect_analysing: 'Iryeko gin…', detect_recent: 'Etal macon manok',
  analyzing_title: 'Iryeko gin…', analyze_step1: 'Iryeko kit me noo…', analyze_step2: 'Iryeko kit pa wangkweny…', analyze_step3: 'Tiyo ki leŋ pa deepfake…', analyze_step4: 'Cuŋo lokke…',
  result_fake: 'Twero Apara / Olokke', result_real: 'Twero Adaa', result_uncertain: 'Pe Ŋeyo', result_fake_sub: 'Gin man inyo nyuto tek me loko pa AI onyo gero pa deepfake.', result_real_sub: 'Nyuto mo ma tek pa loko pe ŋeno.', result_uncertain_sub: 'Leŋ pe otwero miyo lokke me gen. Ibed ki cen.', result_confident: 'gen', result_fake_prob: 'Twero kuba apara', result_real_prob: 'Twero kuba adaa', result_breakdown: 'Ryeko me Twero Atoo', result_model: 'Leŋ ma otiyo', result_time: 'Ikobo me tiyo', result_warn_title: 'Itiyo ngo?', result_warn_body: "Pe gab gin man. Mwony i kabedo ma inene iye. Ka orwenyo dano ma iŋeyo, wac dano madit ma giŋeyo.", result_good_title: 'Bwoŋ maber!', result_good_body: 'Media man inee macalo adaa. Ento, jami kenyo ngec ki kabedo mukene — pe leŋ mo ma 100% pire.', result_reset: '← Ryeko Failu Mukene',
  learn_badge: 'Kabedo me Ining', learn_title1: 'Ining Noo', learn_title2: 'apara', learn_sub: 'Cwalo me ining ma inyo kit deepfakes itiyo kwede ka kit me gwoko i intaneti. Iyer modyu pi yabo.',
  learn_tips_title: 'Cik Nyangu me Noo', learn_modules_title: 'Modyu me Cwalo',
  quiz_badge: 'Ibala pa Ngec me Dijito', quiz_title1: 'Tem', quiz_title2: 'Ngec Maŋi pa Noo Apara', quiz_sub: 'ibala ku kom deepfakes, kwer, gin pa AI, ka bedo arum i intaneti.', quiz_start: 'Icak Ibala', quiz_question_of: 'Ibala', quiz_score: 'Pire', quiz_next: 'Ibala Mukene', quiz_see_results: 'Noo Lokke', quiz_correct: 'Tye atir!', quiz_wrong: 'Pe atir.', quiz_results_title: 'Lokke Maŋi', quiz_results_scored: 'I mako pire', quiz_results_of: 'ki', quiz_retry: 'Tem Doki', quiz_review: 'Kenn Cwalo',
  about_badge: 'Ngo ma Itiyo Kwede', about_title1: 'Igerre pi', about_title2: 'isi me dijito ma arum', about_sub: 'Deepfake A.I Detector icuŋo ryeko pa AI ki ining pa ngec me dijito ikonyo aitare iwoto i media ma apara, deepfakes, ka gin pa AI.', about_problem_title: 'Peko', about_approach_title: 'Kit Waŋwa', about_tech_title: 'Tekinoloji ma Itiyo', about_pipeline_title: 'Kit me Etal', about_cta_title: 'Icak gwoko komi nisio', about_cta_sub: 'Tem jaetal, iyer cwalo, onyo tem ngec maŋi — gin ducu nono, pe miyo akaunti.',
  footer_desc: 'Ibwotokin aitare bedo arum i intaneti pi etal deepfakes, media ma apara, ka gin pa AI ma rwenyo.', footer_built: 'Igerre pi UCC Hackathon 2026 — Apata 6: AI pi Edeke me Dijito', footer_features: 'Ginaraka', footer_resources: 'Gin me Konyo', footer_copy: '© 2026 Deepfake A.I Detector. Igerre pi ining.', footer_love: 'Igerre ki ❤ pi isi me dijito ma arum',
  lang_select: 'Leb', lang_powered: 'Lokke pa Sunbird AI',
}

export const translations: Record<LangCode, Translations> = {
  eng: en,
  lug: lug,
  swa: swa,
  ach: ach,
  nyn: nyn,
  teo: teo,
}
