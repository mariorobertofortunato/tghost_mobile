package com.mrf.tghost.domain.model

enum class AppTheme {
    LIGHT, DARK, RED, BLUE, PURPLE, ORANGE, JUNGLE_DARK, JUNGLE_LIGHT, JUNGLE_GOLDEN, JUNGLE_OCEAN, JUNGLE_MONOTINT
}

enum class ThemeCategory {
    CLASSIC,
    JUNGLE,
    MONO,
}

data class ThemeItem(
    val theme: AppTheme,
    val backGroundColor: Long,
    val contentColor: Long,
    val category: ThemeCategory,
    val isProTheme: Boolean,
    val bgRes: Int? = null
)