package com.icelake.fishinglog.core.di

import android.content.Context
import com.icelake.fishinglog.data.local.SettingsPreferences
import com.icelake.fishinglog.data.local.TripPreferences
import com.icelake.fishinglog.data.repository.SettingsRepositoryImpl
import com.icelake.fishinglog.data.repository.SpotRepositoryImpl
import com.icelake.fishinglog.data.repository.TripRepositoryImpl
import com.icelake.fishinglog.domain.repository.SettingsRepository
import com.icelake.fishinglog.domain.repository.SpotRepository
import com.icelake.fishinglog.domain.repository.TripRepository
import com.icelake.fishinglog.domain.usecase.GetJournalStatsUseCase
import com.icelake.fishinglog.domain.usecase.GetRecentTripsUseCase
import com.icelake.fishinglog.domain.usecase.GetSpotsUseCase
import com.icelake.fishinglog.domain.usecase.SaveCatchUseCase
import com.icelake.fishinglog.domain.usecase.ValidateCatchUseCase

object ServiceLocator {

    private var tripRepositoryRef: TripRepository? = null
    private var spotRepositoryRef: SpotRepository? = null
    private var settingsRepositoryRef: SettingsRepository? = null

    fun init(context: Context) {
        if (tripRepositoryRef != null) {
            return
        }
        val appContext = context.applicationContext
        val settingsPreferences = SettingsPreferences(appContext)
        tripRepositoryRef = TripRepositoryImpl(TripPreferences(appContext))
        spotRepositoryRef = SpotRepositoryImpl(settingsPreferences)
        settingsRepositoryRef = SettingsRepositoryImpl(settingsPreferences)
    }

    fun tripRepository(): TripRepository =
        tripRepositoryRef ?: throw IllegalStateException(NOT_READY)

    fun spotRepository(): SpotRepository =
        spotRepositoryRef ?: throw IllegalStateException(NOT_READY)

    fun settingsRepository(): SettingsRepository =
        settingsRepositoryRef ?: throw IllegalStateException(NOT_READY)

    fun journalStatsUseCase(): GetJournalStatsUseCase = GetJournalStatsUseCase(tripRepository())

    fun recentTripsUseCase(): GetRecentTripsUseCase = GetRecentTripsUseCase(tripRepository())

    fun saveCatchUseCase(): SaveCatchUseCase = SaveCatchUseCase(tripRepository())

    fun validateCatchUseCase(): ValidateCatchUseCase = ValidateCatchUseCase()

    fun spotsUseCase(): GetSpotsUseCase = GetSpotsUseCase(spotRepository())

    private const val NOT_READY = "ServiceLocator.init must run before the first screen"
}
