package com.truthlens.ui.model

/**
 * Representation of a Ugandan Language with Region, Native Name, and Sample Greeting.
 * Covers Central, Eastern, Northern, Western regions as well as National languages.
 */
data class UgandanLanguage(
    val code: String,
    val name: String,
    val nativeName: String,
    val region: String,
    val sampleGreeting: String
)

object UgandanLanguageDirectory {
    val languages = listOf(
        // National & Cross-Regional
        UgandanLanguage("en", "English", "English", "National / Official", "Hello, how can I help you today?"),
        UgandanLanguage("sw", "Kiswahili", "Kiswahili", "National / East Africa", "Hujambo, ninawezaje kukusaidia leo?"),

        // Central Region
        UgandanLanguage("lg", "Luganda", "Oluganda", "Central (Buganda)", "Oli otya? Nnyinza ntya okukuyamba leero?"),
        UgandanLanguage("xog", "Lusoga", "Olusoga", "Central-East (Busoga)", "Kodheyo! Nsobola ntya okukuyamba leero?"),

        // Eastern Region
        UgandanLanguage("myx", "Lumasaba (Lugisu)", "Lumasaba", "Eastern (Elgon/Bugisu)", "Mulembe! Inyanga kukwasa soti leero?"),
        UgandanLanguage("teo", "Ateso", "Ateso", "Eastern (Teso)", "Yoga! Epone bo ani apedoria ayangakin ijo lolo?"),
        UgandanLanguage("lsm", "Samia", "Olusamia", "Eastern (Samia-Bugwe)", "Mirembe! Manyala khukwasa khuriena leero?"),
        UgandanLanguage("adh", "Dhopadhola", "Dhopadhola", "Eastern (Tororo/Padhola)", "Kopango! Anyaalo konyi nade kawono?"),

        // Northern Region
        UgandanLanguage("ach", "Acholi", "Leb Acoli", "Northern (Acholiland)", "Kop angoo? Aromo konyi ningning tin?"),
        UgandanLanguage("laj", "Leb Lango", "Leb Lango", "Northern (Lango)", "Itye ningning? Aromo konyi i nyo tin?"),
        UgandanLanguage("alz", "Alur", "Dhu Alur", "Northern (West Nile/Nebbi)", "Kopa ngo? Aromo konyi nenedi tin?"),
        UgandanLanguage("lgg", "Lugbara", "Lugbarati", "Northern (West Nile/Arua)", "Mi ifa! Ma eko ngoni mi aza lele dusi?"),
        UgandanLanguage("mhi", "Madi", "Madi", "Northern (West Nile/Moyo)", "Odu adri! Ma eco nyi aza ko ngoni andu?"),

        // Western Region
        UgandanLanguage("nyn", "Runyankole", "Runyankole", "Western (Ankole)", "Agandi! Nimbasa kukuhwera nta eriizooba?"),
        UgandanLanguage("cgg", "Rukiga", "Rukiga", "Western (Kigezi)", "Oraire ota? Nimbaasa kukuhwera nta eriizooba?"),
        UgandanLanguage("nyo", "Runyoro", "Runyoro", "Western (Bunyoro)", "Oraire ota? Nsobora nkaha kukukonyera kiro kinu?"),
        UgandanLanguage("ttj", "Rutooro", "Rutooro", "Western (Tooro)", "Oraire ota? Nsobora nkaha kukukonyera leero?")
    )

    fun findByCode(code: String): UgandanLanguage {
        return languages.find { it.code.equals(code, ignoreCase = true) } ?: languages.first()
    }

    fun findByName(name: String): UgandanLanguage {
        return languages.find { it.name.equals(name, ignoreCase = true) } ?: languages.first()
    }
}
