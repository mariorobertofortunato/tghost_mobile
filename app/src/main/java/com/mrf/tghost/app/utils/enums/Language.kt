package com.mrf.tghost.app.utils.enums

import java.util.Locale

// Enum to represent supported languages
enum class Language(val locale: Locale?, val displayName: String) {
    SYSTEM_DEFAULT(null, "System Default"),
    ENGLISH(Locale.ENGLISH, "English"),
    SPANISH(Locale("es", "ES"), "Español"),
    FRENCH(Locale.FRENCH, "Français"),
    GERMAN(Locale.GERMAN, "Deutsch"),
    ITALIAN(Locale.ITALIAN, "Italiano"),
    PORTUGUESE(Locale("pt", "PT"), "Português"),
    CHINESE_SIMPLIFIED(Locale.SIMPLIFIED_CHINESE, "简体中文"),
    JAPANESE(Locale.JAPANESE, "日本語"),
}