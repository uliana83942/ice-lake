package com.icelake.fishinglog.data.local

import android.content.Context
import android.content.SharedPreferences
import com.icelake.fishinglog.domain.model.AppSettings

class SettingsPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    fun read(): AppSettings = AppSettings(
        soundEnabled = prefs.getBoolean(KEY_SOUND, true),
        hapticEnabled = prefs.getBoolean(KEY_HAPTIC, true),
        metricUnits = prefs.getBoolean(KEY_METRIC, true),
        showTemperature = prefs.getBoolean(KEY_TEMPERATURE, true)
    )

    fun write(settings: AppSettings) {
        prefs.edit()
            .putBoolean(KEY_SOUND, settings.soundEnabled)
            .putBoolean(KEY_HAPTIC, settings.hapticEnabled)
            .putBoolean(KEY_METRIC, settings.metricUnits)
            .putBoolean(KEY_TEMPERATURE, settings.showTemperature)
            .apply()
    }

    fun selectedSpotId(): String = prefs.getString(KEY_SPOT, "").orEmpty()

    fun selectSpot(spotId: String) {
        prefs.edit().putString(KEY_SPOT, spotId).apply()
    }

    private companion object {
        const val STORE_NAME = "ice_lake_settings"
        const val KEY_SOUND = "sound"
        const val KEY_HAPTIC = "haptic"
        const val KEY_METRIC = "metric"
        const val KEY_TEMPERATURE = "temperature"
        const val KEY_SPOT = "spot"
    }
}
