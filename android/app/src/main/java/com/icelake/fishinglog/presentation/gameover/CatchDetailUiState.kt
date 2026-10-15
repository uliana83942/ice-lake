package com.icelake.fishinglog.presentation.gameover

import com.icelake.fishinglog.domain.model.CatchEntry

data class CatchDetailUiState(
    val loaded: Boolean = false,
    val entry: CatchEntry? = null,
    val personalBest: Boolean = false,
    val metricUnits: Boolean = true,
    val showTemperature: Boolean = true
)
