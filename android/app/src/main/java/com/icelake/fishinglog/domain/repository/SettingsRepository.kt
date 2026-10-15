package com.icelake.fishinglog.domain.repository

import com.icelake.fishinglog.domain.model.AppSettings

interface SettingsRepository {
    fun settings(): AppSettings
    fun update(settings: AppSettings)
}
