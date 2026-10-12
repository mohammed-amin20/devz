package com.mohamed.devz.feature.settings.presentation

/**
 * User-selectable app language. Persisted in DataStore under [storageKey].
 * [localeTag] is the BCP-47 tag applied via AppCompatDelegate; null means
 * "follow the system locale".
 */
enum class AppLanguage(val storageKey: String, val localeTag: String?) {
    SYSTEM("system", null),
    ENGLISH("en", "en"),
    ARABIC("ar", "ar"),
    ;

    companion object {
        val default: AppLanguage = SYSTEM

        fun fromStorage(value: String?): AppLanguage =
            entries.firstOrNull { it.storageKey == value } ?: default
    }
}
