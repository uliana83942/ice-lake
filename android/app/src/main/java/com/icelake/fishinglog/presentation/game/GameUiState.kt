package com.icelake.fishinglog.presentation.game

import com.icelake.fishinglog.core.config.GameConfig
import com.icelake.fishinglog.domain.model.BaitType
import com.icelake.fishinglog.domain.model.FishSpecies
import com.icelake.fishinglog.domain.model.Spot
import com.icelake.fishinglog.domain.model.WeatherCondition

data class GameUiState(
    val loaded: Boolean = false,
    val species: FishSpecies = FishSpecies.PIKE,
    val weightKg: Float = GameConfig.DEFAULT_WEIGHT_KG,
    val lengthCm: Int = GameConfig.DEFAULT_LENGTH_CM,
    val bait: BaitType = BaitType.JIG,
    val weather: WeatherCondition = WeatherCondition.SNOW,
    val spot: Spot? = null,
    val notes: String = "",
    val loggedCount: Int = 0,
    val valid: Boolean = true,
    val saving: Boolean = false,
    val metricUnits: Boolean = true,
    val showTemperature: Boolean = true
)
