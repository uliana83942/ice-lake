package com.icelake.fishinglog.data.repository

import com.icelake.fishinglog.data.local.SettingsPreferences
import com.icelake.fishinglog.domain.model.AppSettings
import com.icelake.fishinglog.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val preferences: SettingsPreferences) : SettingsRepository {

    override fun settings(): AppSettings = preferences.read()

    override fun update(settings: AppSettings) {
        preferences.write(settings)
    }
}
