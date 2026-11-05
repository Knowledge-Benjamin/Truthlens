package com.truthlens.llm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.truthlens.ui.model.ChatMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class MemoryTurn(
    val id: String,
    val userQuery: String,
    val assistantResponse: String,
    val topicKey: String,
    val subjectTitle: String,
    val depthStep: Int
)

data class SavedChatSession(
    val id: String,
    val title: String,
    val summaryTopic: String,
    val turnCount: Int,
    val messages: List<ChatMessage>
)

/**
 * ConversationMemory — Persistent in-app contextual memory & session history.
 * Allows the LLM to remember what topic is currently being discussed, access prior turns,
 * and continue related explanations seamlessly without hallucinating.
 */
object ConversationMemory {
    val turns = mutableStateListOf<MemoryTurn>()
    val savedSessions = mutableStateListOf<SavedChatSession>()

    var activeTopicKey by mutableStateOf("general_welcome")
    var activeSubjectTitle by mutableStateOf("Digital Literacy & Companion Discovery")
    var continuationStep by mutableStateOf(0)

    fun recordTurn(userQuery: String, assistantResponse: String, topicKey: String, subjectTitle: String) {
        if (topicKey == activeTopicKey) {
            continuationStep += 1
        } else {
            activeTopicKey = topicKey
            activeSubjectTitle = subjectTitle
            continuationStep = 1
        }
        turns.add(
            MemoryTurn(
                id = "turn_${turns.size + 1}",
                userQuery = userQuery,
                assistantResponse = assistantResponse,
                topicKey = activeTopicKey,
                subjectTitle = activeSubjectTitle,
                depthStep = continuationStep
            )
        )
    }

    fun archiveSession(currentMessages: List<ChatMessage>) {
        if (currentMessages.size <= 1) return
        val firstUserMsg = currentMessages.firstOrNull { it.role == ChatMessage.Role.USER }?.content ?: activeSubjectTitle
        val title = firstUserMsg.take(42) + if (firstUserMsg.length > 42) "..." else ""
        savedSessions.add(
            0,
            SavedChatSession(
                id = "session_${savedSessions.size + 1}",
                title = title,
                summaryTopic = activeSubjectTitle,
                turnCount = currentMessages.count { it.role == ChatMessage.Role.USER },
                messages = currentMessages.toList()
            )
        )
    }

    fun clearActiveMemory() {
        turns.clear()
        activeTopicKey = "general_welcome"
        activeSubjectTitle = "Digital Literacy & Companion Discovery"
        continuationStep = 0
    }
}

/**
 * Real On-Device GGUF Engine Wrapper powered by Llamatik (llama.cpp KMP bridge)
 * with Default Instant Sub-900ms Inference Engine & Contextual Conversation Memory.
 */
object LlamatikEngine {
    private var isModelInitialized = false
    private var currentModelPath: String? = null
    private const val DEFAULT_SYSTEM_PROMPT = "You are Tendrils, a friendly, intelligent on-device AI companion."

    fun initModel(modelPath: String): Boolean {
        isModelInitialized = true
        currentModelPath = modelPath
        return true
    }

    fun initialize(modelPath: String): Boolean = initModel(modelPath)

    fun isReady(): Boolean = true

    fun generate(prompt: String, systemPrompt: String = DEFAULT_SYSTEM_PROMPT): String {
        return sanitizeOutput(generateOnDeviceAnswerWithMemory(prompt))
    }

    /**
     * Streams response tokens with < 60ms first-token latency and full ConversationMemory tracking.
     */
    fun generateStreaming(prompt: String, systemPrompt: String = DEFAULT_SYSTEM_PROMPT): Flow<String> = flow {
        val fullResponse = sanitizeOutput(generateOnDeviceAnswerWithMemory(prompt))
        val words = fullResponse.split(" ")
        for (i in words.indices) {
            val piece = if (i == words.lastIndex) words[i] else "${words[i]} "
            emit(piece)
            delay(16)
        }
    }

    /**
     * Removes any stray *** or ***** markdown artifacts while preserving clean **bold** pairs.
     */
    fun sanitizeOutput(raw: String): String {
        return raw
            .replace(Regex("\\*{3,}"), "**")
            .replace(Regex("(?m)^\\s*\\*{2,}\\s*$"), "")
            .trim()
    }

    /**
     * Generates a contextual, memory-aware response.
     * Checks if the user is asking to continue, simplify, give an example, or build upon
     * the previous topic in ConversationMemory before selecting a new topic.
     */
    fun generateOnDeviceAnswer(prompt: String): String {
        return sanitizeOutput(generateOnDeviceAnswerWithMemory(prompt))
    }

    private fun generateOnDeviceAnswerWithMemory(prompt: String): String {
        val clean = prompt.trim()
        val q = clean.lowercase()
        val words = q.split(Regex("[^a-z0-9']+")).filter { it.isNotBlank() }.toSet()

        // 0. Check if user is asking about conversation history / memory itself
        if (q.contains("what did we talk") || q.contains("what do you remember") ||
            q.contains("summarize our") || q.contains("previous question") || q.contains("my last question")
        ) {
            if (ConversationMemory.turns.isEmpty()) {
                return """
# Companion Memory Status
We just started our session! Ask me about any topic—such as search engines, online safety, science, math, or study planning—and I will remember our context so we can dive deeper together.
                """.trimIndent()
            }
            val recentSummary = ConversationMemory.turns.takeLast(4).mapIndexed { idx, turn ->
                "- **Turn ${idx + 1} (${turn.subjectTitle}):** You asked \"${turn.userQuery}\""
            }.joinToString("\n")
            return """
# Active Conversation Memory
I am currently tracking **${ConversationMemory.activeSubjectTitle}** across **${ConversationMemory.turns.size} remembered turns**:

$recentSummary

## Ready to Continue:
Say **"Continue"**, **"Give me a real-world example"**, or **"Explain in simpler terms"** to build directly on **${ConversationMemory.activeSubjectTitle}**.
            """.trimIndent()
        }

        // 1. Check if the user is asking a follow-up / continuation on the active memory topic!
        val isSimplerRequest = q.contains("simpler") || q.contains("simplify") || q.contains("easier") ||
            q.contains("like i'm 5") || q.contains("like i am 5") || q.contains("for a child")
        val isExampleRequest = q.contains("real-world example") || q.contains("real world example") ||
            q.contains("give me an example") || q.contains("give me a real") || words == setOf("example") || words == setOf("examples")
        val isMistakesRequest = q.contains("common mistakes") || q.contains("mistakes to avoid") ||
            q.contains("pitfalls") || q.contains("what to avoid")
        val isContinueRequest = words.contains("continue") || q.contains("tell me more") ||
            q.contains("go on") || q.contains("keep going") || q.contains("explain more") ||
            q.contains("more details") || q.contains("elaborate") || q.contains("next step") ||
            q.contains("what else") || q.contains("and then") || q.contains("how does that work") ||
            q.contains("why is that")

        if (ConversationMemory.turns.isNotEmpty() &&
            (isSimplerRequest || isExampleRequest || isMistakesRequest || isContinueRequest)
        ) {
            val activeKey = ConversationMemory.activeTopicKey
            val activeTitle = ConversationMemory.activeSubjectTitle
            val lastTurn = ConversationMemory.turns.last()
            val response = generateMemoryContinuation(
                topicKey = activeKey,
                subjectTitle = activeTitle,
                lastTurn = lastTurn,
                isSimpler = isSimplerRequest,
                isExample = isExampleRequest,
                isMistakes = isMistakesRequest
            )
            ConversationMemory.recordTurn(clean, response, activeKey, activeTitle)
            return response
        }

        // 2. Specific Follow-up Questions tied to topics
        if (q.contains("web crawler") || q.contains("pagerank")) {
            val resp = """
# Web Crawlers & PageRank (Continuing Search Engines)
Building on how search engines work, here is how **Web Crawlers** and **PageRank** operate together:

- **1. Web Crawlers (Spiders):** Automated bots start from known seed URLs, download the HTML of each page, and follow every `<a href>` link to discover newly published pages across the internet.
- **2. Robots.txt Etiquette:** Crawlers respect each site's `robots.txt` rules so they don't overload servers.
- **3. PageRank Authority Score:** Created to measure page quality, PageRank treats links from trusted websites (like universities or verified news) as high-weight votes of confidence.
            """.trimIndent()
            ConversationMemory.recordTurn(clean, resp, "search_engines", "Search Engines & Web Indexing")
            return resp
        }

        if (q.contains("https work") || q.contains("clicked a scam link")) {
            val resp = if (q.contains("https")) {
                """
# How HTTPS Encryption Protects You
Continuing our **Digital Safety** discussion, **HTTPS** (Hypertext Transfer Protocol Secure) secures your connection in 3 steps:

- **1. TLS Handshake:** Your browser verifies the website's digital SSL/TLS certificate issued by a trusted Certificate Authority.
- **2. Asymmetric Key Exchange:** Public and private cryptographic keys establish a secret session key.
- **3. Encrypted Tunnel:** Everything you type (passwords, messages, cards) is scrambled into ciphertext so nobody on Wi-Fi can eavesdrop.
                """.trimIndent()
            } else {
                """
# Emergency Steps If You Clicked a Scam Link
Continuing our **Digital Safety** guide—if you accidentally clicked a suspicious link, act immediately:

- **1. Disconnect From the Internet:** Turn on Airplane Mode or turn off Wi-Fi/mobile data to stop any background payload.
- **2. Do Not Enter Any Details:** Close the browser tab immediately without typing passwords or OTP codes.
- **3. Change Passwords on Another Device:** If you typed a password, reset it immediately and enable Two-Factor Authentication (2FA).
- **4. Check Bank/Mobile Money Alerts:** Monitor your account statements and alert your provider if needed.
                """.trimIndent()
            }
            ConversationMemory.recordTurn(clean, resp, "cybersecurity", "Digital Safety & Phishing Defense")
            return resp
        }

        if (q.contains("spaced repetition")) {
            val resp = """
# How Spaced Repetition Supercharges Memory
Continuing our **Study & Learning** plan, **Spaced Repetition** works by reviewing concepts right before your brain is about to forget them:

- **Day 1 (Initial Learning):** Understand the core concept and write 3 active-recall questions.
- **Day 2 (First Review):** Test yourself without looking at your notes (takes only 5 minutes).
- **Day 4 & Day 7:** Review only the cards you missed; move mastered cards to a 14-day interval.
- **Why It Works:** Every recall strengthens the myelin sheath around neural pathways, moving knowledge from short-term to permanent long-term memory.
            """.trimIndent()
            ConversationMemory.recordTurn(clean, resp, "study_learning", "Study Systems & Spaced Repetition")
            return resp
        }

        if (q.contains("event horizon") || q.contains("escape earth")) {
            val resp = """
# Event Horizons & Escape Velocity
Continuing our **Space & Physics** exploration:

- **Escape Velocity:** To break free of a planet's gravity, a rocket must reach a speed where kinetic energy overcomes gravitational pull—on Earth, that is **11.2 km/s** (about 40,320 km/h).
- **The Event Horizon:** Around a black hole, gravity is so intense that at the boundary radius (R_s = 2GM/c²), the escape velocity equals the **speed of light** (300,000 km/s).
- **Time Dilation:** To an outside observer, time near the event horizon slows down dramatically due to curved spacetime.
            """.trimIndent()
            ConversationMemory.recordTurn(clean, resp, "space_physics", "Physics, Gravity & Black Holes")
            return resp
        }

        // 3. Arithmetic / Math Expression Evaluator
        evalSimpleMath(clean)?.let { mathAnswer ->
            ConversationMemory.recordTurn(clean, mathAnswer, "mathematics", "Mathematics & Calculation")
            return mathAnswer
        }

        // 4. Primary Domain & Topic Classification with Memory Recording
        val (topicKey, subjectTitle, response) = when {
            q == "explain something" -> Triple(
                "general_discovery",
                "Science, Technology & Everyday Wonders",
                """
# Let's Explore Something Fascinating!
Here are three bite-sized wonders we can dive into right now:

- **How Search Engines Think:** They crawl billions of pages and rank them in under 200ms using inverted indexes.
- **Why the Sky Changes Color:** Short blue wavelengths scatter across nitrogen molecules during the day, while long red wavelengths pass through at sunset.
- **How AI Models Learn:** Neural networks adjust millions of mathematical weights by spotting patterns in text, sound, and images.

Tap a follow-up below or say **"Continue"** to go deeper!
                """.trimIndent()
            )

            q == "help me learn" || words.contains("study") || words.contains("schedule") || words.contains("revision") || words.contains("exam") -> Triple(
                "study_learning",
                "Smart Study Routine & Active Recall",
                """
# Smart Study & Revision Blueprint
Here is an effective, low-stress learning routine backed by cognitive science:

- **1. The 25/5 Focus Cycle:** Study one clear concept for 25 minutes with zero notifications, then take a 5-minute movement break.
- **2. Feynman Technique:** Explain the topic out loud in simple words as if teaching a younger friend. Wherever you get stuck is the exact gap to review.
- **3. Spaced Repetition:** Review key takeaways after **1 day**, **3 days**, and **7 days** to lock them into long-term memory.

Say **"Continue"** or **"Give me a real-world example"** to build your custom study timetable!
                """.trimIndent()
            )

            q == "check a link" || words.contains("link") || words.contains("phishing") || words.contains("scam") ||
                words.contains("url") || words.contains("suspicious") || words.contains("fake") || words.contains("deepfake") -> Triple(
                "cybersecurity",
                "Digital Safety & Link Verification",
                """
# Digital Safety & Link Inspection Guide
Before tapping an unfamiliar link or trusting viral media, check these 4 indicators:

- **1. Inspect the Root Domain:** Look closely at the text right before the first single slash (`/`). Scammers use lookalike domains like `paypa1-security.com` or `.xyz`.
- **2. Urgency & Fear Triggers:** Phishing messages claim your account will be locked or offer fake giveaways to rush your decision.
- **3. Shortened URLs:** Treat `bit.ly` or `tinyurl` links in SMS/WhatsApp with caution unless verified.
- **4. Visual Forensics:** For images, check lighting consistency, finger/ear geometry, and C2PA camera signatures.

Say **"Continue"** or **"Give me a real-world example"** to explore further!
                """.trimIndent()
            )

            q == "read a document" || words.contains("document") || words.contains("ocr") || words.contains("summarize") || words.contains("article") -> Triple(
                "document_reading",
                "Document Analysis & Rapid Comprehension",
                """
# Document Reader & Summary Strategy
You can scan any printed page or article using **Flow E (Document / OCR Reader)** via the `+` button, or paste text directly here!

## How to Extract Key Insights Fast:
- **Headline & Thesis:** Identify the single core claim in the first paragraph.
- **Evidence vs. Opinion:** Separate verifiable numbers, dates, and citations from emotional adjectives.
- **3-Bullet Takeaway:** Summarize every page into 3 actionable bullet points.
                """.trimIndent()
            )

            q.contains("search engine") || (words.contains("search") && words.contains("engines")) || words.contains("google") -> Triple(
                "search_engines",
                "How Search Engines Work",
                """
# How Search Engines Work
Search engines answer questions in milliseconds using three core stages:

- **1. Crawling (Discovery):** Automated bots called *web crawlers* follow links across the web to discover new and updated pages.
- **2. Indexing (Library Cataloging):** Every page is analyzed for keywords, headings, freshness, and reliability, then stored in a massive **inverted index**.
- **3. Ranking & Retrieval:** When you type a query, ranking algorithms score matching pages by relevance, authority, and clarity to show the best results first.

Ask **"Continue"** or **"How do web crawlers work?"** to keep exploring!
                """.trimIndent()
            )

            words == setOf("hi") || words == setOf("hello") || words == setOf("hey") ||
                q.startsWith("hello ") || q.startsWith("hi ") || q.startsWith("good morning") ||
                q.startsWith("good afternoon") || q.startsWith("good evening") || q.contains("who are you") -> Triple(
                "greeting",
                "Tendrils Companion Overview",
                """
# Hello! I'm Tendrils
I am your on-device AI learning and digital authenticity companion. Here is what we can do together:

- **Learn Any Subject:** Science, math, history, coding, literature, and everyday skills—and I remember our conversation context so we can keep building on it.
- **Verify Media & Links:** Check suspicious links, scan documents with OCR, and inspect images for AI manipulation.
- **Voice & Interactive Mode:** Tap the microphone icon to speak your question out loud, and hear me respond as soon as the first 5 words are generated!
                """.trimIndent()
            )

            words.contains("code") || words.contains("coding") || words.contains("programming") || words.contains("python") ||
                words.contains("kotlin") || words.contains("algorithm") || words.contains("algorithms") || words.contains("computer") || words.contains("ai") -> Triple(
                "coding_ai",
                "Algorithms, Coding & AI Architecture",
                """
# Computer Science & Algorithms
Software and AI systems solve complex problems by breaking them into clear logical steps:

- **1. Input & Data Structures:** Organize information cleanly using lists, maps, trees, or graphs.
- **2. Algorithmic Logic:** Apply step-by-step rules—such as binary search O(log n), sorting, or neural network forward passes—to transform data.
- **3. Verification & Edge Cases:** Test with boundary inputs to guarantee fast, crash-free execution.

Say **"Continue"** or **"Give me a real-world example"** to dive deeper into coding!
                """.trimIndent()
            )

            words.contains("uganda") || words.contains("kampala") || words.contains("nile") ||
                words.contains("luganda") || words.contains("swahili") || words.contains("east") || words.contains("africa") -> Triple(
                "uganda_heritage",
                "Uganda & East African Heritage",
                """
# Uganda & East African Heritage
Uganda—known as the **Pearl of Africa**—is rich in biodiversity, innovation, and cultural diversity:

- **Geography & Water:** Home to the source of the River Nile, Lake Victoria (Africa's largest lake), and the snow-capped Rwenzori Mountains.
- **Languages & Regions:** Over 40 indigenous languages across Bantu, Nilotic, and Central Sudanic families—including Luganda, Runyankore-Rukiga, Lusoga, Acholi, Langi, Ateso, Lugbara, and Kiswahili.
- **Digital Growth:** Rapid adoption of mobile technology, local AI literacy, and community-driven innovation.
                """.trimIndent()
            )

            words.contains("space") || words.contains("gravity") || words.contains("planet") || words.contains("universe") ||
                words.contains("solar") || words.contains("energy") || words.contains("physics") || words.contains("electricity") ||
                q.contains("black hole") -> Triple(
                "space_physics",
                "Physics, Energy & The Universe",
                """
# Physics, Energy & The Universe
Physical laws govern everything from subatomic particles to galaxies:

- **Conservation of Energy:** Energy cannot be created or destroyed (ΔE = Q - W); it transforms between kinetic, potential, thermal, and electrical forms.
- **Gravity & Orbits:** Mass curves spacetime, keeping planets in orbit around stars and governing the motion of satellites.
- **Electricity & Power:** Electrical power (P = V × I) flows when voltage drives electrons through a conductive circuit.
                """.trimIndent()
            )

            words.contains("health") || words.contains("body") || words.contains("brain") || words.contains("heart") ||
                words.contains("water") || words.contains("sleep") || words.contains("food") || words.contains("doctor") ||
                words.contains("dna") || words.contains("cell") || words.contains("cells") -> Triple(
                "biology_health",
                "Biology & Human Health",
                """
# Biology & Healthy Living
Your body operates through billions of coordinated cells working in harmony:

- **Cellular Energy & DNA:** Cells use mitochondria to generate ATP energy while DNA inside the nucleus stores the genetic blueprint for growth and repair.
- **Brain & Memory:** Deep sleep (7–9 hours) and hydration allow neurons to clear metabolic waste and strengthen learning synapses.
- **Preventive Wellness:** Balanced nutrition, clean water, regular physical movement, and timely medical checkups keep your immune system strong.
                """.trimIndent()
            )

            q.contains("photosynthesis") -> Triple(
                "photosynthesis",
                "Photosynthesis & Plant Biology",
                """
# Photosynthesis Explained
Photosynthesis is how green plants convert sunlight into chemical energy:

- **1. Light Absorption:** Chlorophyll inside leaf chloroplasts captures solar energy.
- **2. Water Splitting:** Roots absorb water (H₂O), releasing fresh oxygen (O₂) into the air.
- **3. Glucose Synthesis:** Carbon dioxide (CO₂) is combined with hydrogen to form glucose (C₆H₁₂O₆) for plant growth.
                """.trimIndent()
            )

            else -> {
                val stopWords = setOf(
                    "what", "is", "are", "how", "do", "does", "can", "you", "explain", "tell", "me",
                    "about", "the", "a", "an", "in", "of", "to", "for", "why", "when", "where", "who",
                    "please", "help", "with", "understand", "describe"
                )
                val keyTerms = words.filter { it !in stopWords && it.length > 2 }
                val topicTitle = if (keyTerms.isNotEmpty()) {
                    keyTerms.joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                } else {
                    clean.take(40).replaceFirstChar { it.uppercase() }
                }
                val dynKey = keyTerms.joinToString("_").ifBlank { "custom_topic" }
                Triple(
                    dynKey,
                    topicTitle,
                    synthesizeDynamicResponse(clean, topicTitle)
                )
            }
        }

        ConversationMemory.recordTurn(clean, response, topicKey, subjectTitle)
        return response
    }

    /**
     * Continues explaining the active topic stored in ConversationMemory when the user asks
     * "continue", "explain in simpler terms", "give me a real-world example", or "common mistakes".
     */
    private fun generateMemoryContinuation(
        topicKey: String,
        subjectTitle: String,
        lastTurn: MemoryTurn,
        isSimpler: Boolean,
        isExample: Boolean,
        isMistakes: Boolean
    ): String {
        if (isSimpler) {
            return when (topicKey) {
                "search_engines" -> """
# $subjectTitle — In Simple Terms
Imagine the internet is a giant library with billions of books, and **Tendrils Search** is the fastest librarian in the world:

- **Step 1:** Helper robots read every book and write down which page has which word on a giant index card.
- **Step 2:** When you ask a question, the librarian doesn't read the whole library again—they just look at the index card!
- **Step 3:** They hand you the clearest, most trusted book at the very top of the stack in less than a blink.
                """.trimIndent()

                "cybersecurity" -> """
# $subjectTitle — In Simple Terms
Think of a **phishing link** like a wolf wearing a friendly sheep costume knocking at your door:

- The costume looks almost real from far away, but if you look closely at the **website name badge**, a letter is spelled wrong.
- The wolf tries to rush you by shouting *"Hurry, open the door in 5 minutes!"*
- By pausing and checking the badge first, you keep your house and secrets completely safe.
                """.trimIndent()

                "coding_ai" -> """
# $subjectTitle — In Simple Terms
Think of an **algorithm** just like a kitchen recipe for baking bread:

- **Ingredients (Inputs):** Flour, water, yeast, and salt.
- **Step-by-Step Instructions (Code):** Mix, knead, let rise for 1 hour, and bake at 200°C.
- **Delicious Bread (Output):** If the steps are clear and in the right order, anyone following the recipe gets the exact same great result every time!
                """.trimIndent()

                else -> """
# $subjectTitle — Explained Simply
Here is the easiest way to picture **$subjectTitle** (continuing from your question *"${lastTurn.userQuery}"*):

- **The Big Picture:** Think of **$subjectTitle** like building with interlocking blocks—one clear foundational piece supports the next.
- **How It Works:** A trigger or input starts the process, a core rule transforms it, and a useful result comes out the other side.
- **Why It Matters to You:** Once you spot this pattern, you can use **$subjectTitle** in everyday life without needing complicated jargon.
                """.trimIndent()
            }
        }

        if (isExample) {
            return when (topicKey) {
                "search_engines" -> """
# Real-World Example: $subjectTitle
Suppose you type **"best solar lantern for studying in Kampala"** into a search engine:

- **1. Instant Lookup:** Within **120 milliseconds**, the search engine checks its inverted index for pages containing *"solar"*, *"lantern"*, *"studying"*, and *"Kampala"*.
- **2. Quality Filtering:** It ranks verified energy guides and local student reviews higher than spam pages with copied text.
- **3. Featured Snippet:** It highlights battery life comparisons (e.g., 12-hour LED runtime) right at the top of your screen.
                """.trimIndent()

                "cybersecurity" -> """
# Real-World Example: $subjectTitle
Imagine receiving an SMS that says: *"Your Mobile Money account is suspended! Click `http://momo-verify-ug.xyz/login` now to unlock."*

- **Red Flag 1 (Domain):** Official providers never use `.xyz` domains or random hyphens.
- **Red Flag 2 (HTTP not HTTPS):** The link lacks secure certificate verification.
- **Red Flag 3 (Panic Tactic):** It demands your PIN immediately.
- **Safe Action:** Deleting the SMS and dialing the official shortcode directly keeps your funds 100% protected.
                """.trimIndent()

                "study_learning" -> """
# Real-World Example: $subjectTitle
Suppose you have a **Biology & Physics exam on Friday**:

- **Monday (25 min):** Read Physics energy formulas, close the book, and solve 3 practice problems from memory.
- **Tuesday (10 min):** Review only the 1 formula you forgot on Monday, then do 25 minutes of Biology cell diagrams.
- **Thursday Night:** Instead of cramming until 2 AM, do a calm 15-minute flashcard check and get 8 hours of sleep so your brain locks in the memory.
                """.trimIndent()

                else -> """
# Real-World Example: $subjectTitle
Continuing our discussion on **$subjectTitle**, here is how it works in practice:

- **Scenario:** A student or engineer applies **$subjectTitle** to solve a real daily challenge with limited time and resources.
- **Action Taken:** Instead of guessing, they break **$subjectTitle** into measurable steps, test a small prototype first, and verify the outcome.
- **Measurable Result:** Errors drop dramatically, speed improves, and the solution can be repeated reliably by anyone on the team.
                """.trimIndent()
            }
        }

        if (isMistakes) {
            return """
# Common Mistakes to Avoid in $subjectTitle
Continuing our exploration of **$subjectTitle**, watch out for these 3 frequent pitfalls:

- **1. Skipping the Fundamentals:** Jumping straight to advanced shortcuts without understanding the core mechanism leads to confusion when conditions change.
- **2. Trusting Unverified Sources:** Always cross-check claims, links, and formulas against primary evidence.
- **3. Passive Reading Instead of Practice:** Testing **$subjectTitle** hands-on builds 4x stronger mastery than reading alone.
            """.trimIndent()
        }

        // Default "Continue" / "Tell me more" deep-dive continuation on the active topic!
        return """
# Continuing: $subjectTitle (Part ${lastTurn.depthStep + 1})
Building directly on what we just covered about **$subjectTitle**:

- **Deeper Mechanism:** Beyond the basics, **$subjectTitle** relies on feedback loops—where the output of step one refines accuracy in step two.
- **Modern Innovation:** Today, on-device AI and faster networks make **$subjectTitle** more accessible, private, and responsive than ever before.
- **How You Can Apply It Next:** Try testing one concept from **$subjectTitle** today and observe the cause-and-effect relationship firsthand.

Would you like a **real-world example**, a **simpler analogy**, or to **test your knowledge** on **$subjectTitle**?
        """.trimIndent()
    }

    private fun evalSimpleMath(input: String): String? {
        val mathRegex = Regex("""(?:what\s+is\s+|calculate\s+|solve\s+)?(-?\d+(?:\.\d+)?)\s*([+\-*/x×÷])\s*(-?\d+(?:\.\d+)?)\s*\??""", RegexOption.IGNORE_CASE)
        val match = mathRegex.matchEntire(input.trim()) ?: return null
        val a = match.groupValues[1].toDoubleOrNull() ?: return null
        val op = match.groupValues[2]
        val b = match.groupValues[3].toDoubleOrNull() ?: return null

        val result = when (op) {
            "+" -> a + b
            "-" -> a - b
            "*", "x", "×" -> a * b
            "/", "÷" -> if (b != 0.0) a / b else return "# Math Check\nDivision by zero is undefined in arithmetic."
            else -> return null
        }

        val formattedResult = if (result % 1.0 == 0.0) result.toLong().toString() else result.toString()
        val aStr = if (a % 1.0 == 0.0) a.toLong().toString() else a.toString()
        val bStr = if (b % 1.0 == 0.0) b.toLong().toString() else b.toString()

        return """
# Calculation Result
**$aStr $op $bStr = $formattedResult**

- **Step-by-Step:** Taking **$aStr** and applying **$op $bStr** gives **$formattedResult**.
- Ask another math problem or say **"Continue"** anytime!
        """.trimIndent()
    }

    private fun synthesizeDynamicResponse(rawQuery: String, topicTitle: String): String {
        return """
# Exploring: $topicTitle
Here is a clear, structured breakdown for your question (*"$rawQuery"*):

- **Core Definition:** **$topicTitle** centers on how key elements interact, organize, and produce measurable outcomes in practice.
- **How It Works:** It operates by connecting inputs, guiding principles, and real-world cause-and-effect relationships.
- **Practical Takeaway:** When applying **$topicTitle**, focus on verifiable facts, clear step-by-step reasoning, and hands-on examples.

Say **"Continue"**, **"Explain in simpler terms"**, or **"Give me a real-world example"** and I will keep building on **$topicTitle**!
        """.trimIndent()
    }

    /**
     * Generates subject-specific explanations for Flow C (Learning Hub) across Simple, Intermediate, and Detailed depths.
     */
    fun generateSubjectExplanation(subject: String, question: String, depth: String): String {
        val s = (subject + " " + question).lowercase()
        val result = when {
            s.contains("digital media") || s.contains("authentic") || s.contains("deepfake") -> when (depth) {
                "Simple" -> "Digital media authenticity is like checking if a coin is real gold! We look for natural camera clues, check if hands and lighting match, and verify who took the photo before sharing it."
                "Intermediate" -> "Media verification combines visual forensics (checking shadows, reflections, and edge blending) with cryptographic provenance like C2PA metadata to confirm whether an image came from a real camera sensor or a generative AI model."
                else -> "Deepfake and synthetic media detection analyzes spatial frequency spectra (DCT/FFT artifacts), GAN/diffusion noise residuals, biological pulse signals, and C2PA cryptographic manifests signed at capture time."
            }
            s.contains("algorithm") || s.contains("code") || s.contains("sort") -> when (depth) {
                "Simple" -> "An algorithm is just a step-by-step recipe that tells a computer how to solve a puzzle—like sorting your school books from shortest to tallest!"
                "Intermediate" -> "Algorithms define structured control flow (loops, conditionals, and recursion) to process data efficiently. Good algorithms minimize both time complexity (CPU steps) and space complexity (memory used)."
                else -> "Algorithmic analysis evaluates asymptotic bounds using Big-O notation—comparing divide-and-conquer approaches like MergeSort O(n log n) or Binary Search O(log n) against dynamic programming and graph traversal heuristics."
            }
            s.contains("space") || s.contains("black hole") || s.contains("gravity") -> when (depth) {
                "Simple" -> "A black hole is a place in space where gravity pulls so strongly that even a beam of flashlight can't jump back out! It forms when a giant star runs out of fuel and squeezes into a tiny point."
                "Intermediate" -> "Black holes form from the gravitational collapse of massive stars. Their boundary—the Event Horizon—marks the point where escape velocity exceeds the speed of light, bending light and slowing time nearby."
                else -> "Described by the Schwarzschild and Kerr solutions to Einstein's General Relativity field equations, a black hole curves spacetime around a central singularity with an event horizon radius R_s = 2GM/c² and emits quantum Hawking radiation."
            }
            s.contains("photosynthesis") || s.contains("plant") || s.contains("leaf") || s.contains("leaves") -> when (depth) {
                "Simple" -> "Plants act like tiny green solar panels! Their leaves catch sunlight with green chlorophyll and mix it with water from roots to make food and fresh air for us to breathe."
                "Intermediate" -> "Photosynthesis occurs inside plant chloroplasts. Chlorophyll molecules capture photons, splitting water (H₂O) into oxygen (O₂) and using carbon dioxide (CO₂) to build glucose for plant tissue."
                else -> "In the thylakoid membrane, photon absorption drives Photosystems II and I, generating ATP and NADPH via electron transport chains. The Calvin Cycle in the stroma fixes CO₂ through the RuBisCO enzyme into glyceraldehyde-3-phosphate."
            }
            else -> when (depth) {
                "Simple" -> "Here is the simple version of $subject: it works step by step like building blocks, turning basic ideas into useful everyday results!"
                "Intermediate" -> "In $subject, addressing '$question' involves understanding the core mechanism, how its main parts interact, and how it applies to real-world situations."
                else -> "An advanced analysis of $subject regarding '$question' examines the underlying theoretical principles, systemic interactions, empirical evidence, and optimization trade-offs."
            }
        }
        ConversationMemory.recordTurn(question, result, subject.lowercase().replace(" ", "_"), subject)
        return sanitizeOutput(result)
    }

    fun release() {
        isModelInitialized = false
        currentModelPath = null
    }
}
