package com.icelake.fishinglog.data.repository

import com.icelake.fishinglog.data.local.SettingsPreferences
import com.icelake.fishinglog.data.sample.SampleData
import com.icelake.fishinglog.domain.model.Spot
import com.icelake.fishinglog.domain.repository.SpotRepository

class SpotRepositoryImpl(private val preferences: SettingsPreferences) : SpotRepository {

    private val cached: MutableList<Spot> = SampleData.spots()

    override fun spots(): List<Spot> = cached.toList()

    override fun selectedSpot(): Spot {
        val storedId = preferences.selectedSpotId()
        val match = cached.firstOrNull { it.id == storedId }
        if (match != null) {
            return match
        }
        return cached.first()
    }

    override fun selectSpot(spotId: String) {
        if (cached.any { it.id == spotId }) {
            preferences.selectSpot(spotId)
        }
    }
}
