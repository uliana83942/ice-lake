package com.icelake.fishinglog.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icelake.fishinglog.core.config.GameConfig
import com.icelake.fishinglog.domain.repository.SettingsRepository
import com.icelake.fishinglog.domain.usecase.GetJournalStatsUseCase
import com.icelake.fishinglog.domain.usecase.GetRecentTripsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuViewModel(
    private val journalStatsUseCase: GetJournalStatsUseCase,
    private val recentTripsUseCase: GetRecentTripsUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val state = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = state.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            val snapshot = withContext(Dispatchers.IO) {
                val settings = settingsRepository.settings()
                MenuUiState(
                    loaded = true,
                    trips = recentTripsUseCase(GameConfig.RECENT_TRIPS_LIMIT),
                    stats = journalStatsUseCase(),
                    metricUnits = settings.metricUnits,
                    showTemperature = settings.showTemperature
                )
            }
            state.value = snapshot
        }
    }
}
