package com.icelake.fishinglog.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icelake.fishinglog.core.config.GameConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val handOffState = MutableStateFlow(false)
    private val progressState = MutableStateFlow(0)

    val handOffReady: StateFlow<Boolean> = handOffState.asStateFlow()
    val progressPercent: StateFlow<Int> = progressState.asStateFlow()

    private var timerJob: Job? = null

    init {
        startTimer()
    }

    private fun startTimer() {
        if (timerJob != null) {
            return
        }
        timerJob = viewModelScope.launch {
            val step = GameConfig.LOADER_DURATION_MS / TICKS
            var elapsed = 0L
            while (elapsed < GameConfig.LOADER_DURATION_MS) {
                delay(step)
                elapsed += step
                val percent = (elapsed * 100 / GameConfig.LOADER_DURATION_MS).toInt()
                progressState.value = if (percent > 100) 100 else percent
            }
            progressState.value = 100
            handOffState.value = true
        }
    }

    fun consumeHandOff() {
        handOffState.value = false
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        timerJob = null
    }

    private companion object {
        const val TICKS = 20L
    }
}
