package com.mohamed.devz.ui.theme

/**
 * User-selectable theme preference. Persisted in DataStore under [storageKey].
 *
 * Default is [DARK] until every screen is migrated to the new token system
 * (see requirement: default to the existing dark look for now).
 */
enum class ThemeMode(val storageKey: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark"),
    ;

    companion object {
        val default: ThemeMode = DARK

        fun fromStorage(value: String?): ThemeMode =
            entries.firstOrNull { it.storageKey == value } ?: default
    }
}
