package com.icelake.fishinglog.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icelake.fishinglog.core.config.GameConfig
import com.icelake.fishinglog.core.util.DateFormatter
import com.icelake.fishinglog.domain.model.BaitType
import com.icelake.fishinglog.domain.model.CatchEntry
import com.icelake.fishinglog.domain.model.FishSpecies
import com.icelake.fishinglog.domain.model.WeatherCondition
import com.icelake.fishinglog.domain.repository.SettingsRepository
import com.icelake.fishinglog.domain.repository.SpotRepository
import com.icelake.fishinglog.domain.repository.TripRepository
import com.icelake.fishinglog.domain.usecase.SaveCatchUseCase
import com.icelake.fishinglog.domain.usecase.ValidateCatchUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameViewModel(
    private val saveCatchUseCase: SaveCatchUseCase,
    private val validateCatchUseCase: ValidateCatchUseCase,
    private val spotRepository: SpotRepository,
    private val settingsRepository: SettingsRepository,
    private val tripRepository: TripRepository
) : ViewModel() {

    private val state = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = state.asStateFlow()

    private val savedState = MutableStateFlow<String?>(null)
    val savedCatchId: StateFlow<String?> = savedState.asStateFlow()

    private var idleJob: Job? = null
    private var saveJob: Job? = null

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val snapshot = withContext(Dispatchers.IO) {
                val settings = settingsRepository.settings()
                val spot = spotRepository.selectedSpot()
                val logged = tripRepository.allCatches().size
                state.value.copy(
                    loaded = true,
                    spot = spot,
                    loggedCount = logged,
                    metricUnits = settings.metricUnits,
                    showTemperature = settings.showTemperature,
                    valid = true
                )
            }
            state.value = snapshot
            armIdleBackstop()
        }
    }

    fun setSpecies(species: FishSpecies) {
        state.value = state.value.copy(species = species)
        validate()
        armIdleBackstop()
    }

    fun setWeight(weightKg: Float) {
        state.value = state.value.copy(weightKg = weightKg)
        validate()
        armIdleBackstop()
    }

    fun setLength(lengthCm: Int) {
        state.value = state.value.copy(lengthCm = lengthCm)
        armIdleBackstop()
    }

    fun setBait(bait: BaitType) {
        state.value = state.value.copy(bait = bait)
        armIdleBackstop()
    }

    fun setWeather(weather: WeatherCondition) {
        state.value = state.value.copy(weather = weather)
        armIdleBackstop()
    }

    fun setNotes(notes: String) {
        state.value = state.value.copy(notes = notes)
        armIdleBackstop()
    }

    fun reloadSpot() {
        viewModelScope.launch {
            val spot = withContext(Dispatchers.IO) { spotRepository.selectedSpot() }
            state.value = state.value.copy(spot = spot)
            armIdleBackstop()
        }
    }

    fun save() {
        if (state.value.saving || !state.value.valid) {
            return
        }
        idleJob?.cancel()
        idleJob = null
        state.value = state.value.copy(saving = true)
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(GameConfig.SAVE_DELAY_MS)
            val stored = withContext(Dispatchers.IO) { saveCatchUseCase(buildEntry()) }
            state.value = state.value.copy(saving = false, loggedCount = state.value.loggedCount + 1)
            savedState.value = stored.id
        }
    }

    fun consumeSaved() {
        savedState.value = null
    }

    private fun buildEntry(): CatchEntry {
        val snapshot = state.value
        val spot = snapshot.spot
        val iceThickness = spot?.iceThicknessCm ?: DEFAULT_ICE_CM
        return CatchEntry(
            id = "",
            species = snapshot.species,
            weightKg = snapshot.weightKg,
            lengthCm = snapshot.lengthCm,
            bait = snapshot.bait,
            weather = snapshot.weather,
            spotName = spot?.name.orEmpty(),
            iceThicknessCm = iceThickness,
            temperatureC = temperatureFor(iceThickness),
            notes = snapshot.notes,
            recordedAtEpochDay = DateFormatter.todayEpochDay(),
            recordedAtMinuteOfDay = DateFormatter.nowMinuteOfDay()
        )
    }

    fun temperatureFor(iceThicknessCm: Int): Int = -(iceThicknessCm / 2)

    private fun validate() {
        val snapshot = state.value
        val valid = validateCatchUseCase(
            snapshot.species,
            snapshot.weightKg,
            GameConfig.MIN_WEIGHT_KG
        )
        state.value = snapshot.copy(valid = valid)
    }

    private fun armIdleBackstop() {
        idleJob?.cancel()
        idleJob = viewModelScope.launch {
            delay(GameConfig.IDLE_TIMEOUT_MS)
            if (!state.value.saving && savedState.value == null) {
                save()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        idleJob?.cancel()
        idleJob = null
        saveJob?.cancel()
        saveJob = null
    }

    private companion object {
        const val DEFAULT_ICE_CM = 24
    }
}
