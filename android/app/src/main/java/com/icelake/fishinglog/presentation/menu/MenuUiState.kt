package com.icelake.fishinglog.presentation.menu

import com.icelake.fishinglog.domain.model.JournalStats
import com.icelake.fishinglog.domain.model.Trip

data class MenuUiState(
    val loaded: Boolean = false,
    val trips: List<Trip> = emptyList(),
    val stats: JournalStats = JournalStats(0, 0f, null, 0, ""),
    val metricUnits: Boolean = true,
    val showTemperature: Boolean = true
)
