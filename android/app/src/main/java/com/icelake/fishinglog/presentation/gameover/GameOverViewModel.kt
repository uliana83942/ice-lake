package com.icelake.fishinglog.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icelake.fishinglog.domain.repository.SettingsRepository
import com.icelake.fishinglog.domain.repository.TripRepository
import com.icelake.fishinglog.domain.usecase.SaveCatchUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameOverViewModel(
    private val tripRepository: TripRepository,
    private val saveCatchUseCase: SaveCatchUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val state = MutableStateFlow(CatchDetailUiState())
    val uiState: StateFlow<CatchDetailUiState> = state.asStateFlow()

    fun load(catchId: String) {
        viewModelScope.launch {
            val snapshot = withContext(Dispatchers.IO) {
                val settings = settingsRepository.settings()
                val entry = tripRepository.findCatch(catchId)
                    ?: tripRepository.allCatches().firstOrNull()
                CatchDetailUiState(
                    loaded = true,
                    entry = entry,
                    personalBest = entry != null && saveCatchUseCase.isPersonalBest(entry),
                    metricUnits = settings.metricUnits,
                    showTemperature = settings.showTemperature
                )
            }
            state.value = snapshot
        }
    }
}
