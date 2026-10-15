package com.icelake.fishinglog.core.config

object GameConfig {
    const val LOADER_DURATION_MS = 8000L
    const val IDLE_TIMEOUT_MS = 40000L
    const val SAVE_DELAY_MS = 260L
    const val RECENT_TRIPS_LIMIT = 8

    const val MIN_WEIGHT_KG = 0.1f
    const val MAX_WEIGHT_KG = 8.0f
    const val DEFAULT_WEIGHT_KG = 1.2f
    const val MIN_LENGTH_CM = 10
    const val MAX_LENGTH_CM = 120
    const val DEFAULT_LENGTH_CM = 42

    const val SPLASH_MARK_ENTRY_MS = 620L
    const val SPLASH_TEXT_ENTRY_MS = 480L
    const val SPLASH_TITLE_DELAY_MS = 220L
    const val SPLASH_SUBTITLE_DELAY_MS = 360L
    const val SPLASH_PULSE_MS = 1800L
    const val SPLASH_RING_MS = 9000L
    const val SPLASH_DOT_BASE_MS = 2400L
    const val SPLASH_DOT_STEP_MS = 150L

    const val MENU_ENTRY_MS = 320L
    const val HERO_ENTRY_MS = 420L
    const val BADGE_ENTRY_MS = 300L
    const val COUNTER_PULSE_MS = 280L
}
