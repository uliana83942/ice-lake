package com.icelake.fishinglog.domain.usecase

import com.icelake.fishinglog.domain.model.CatchEntry
import com.icelake.fishinglog.domain.repository.TripRepository

class SaveCatchUseCase(private val tripRepository: TripRepository) {

    operator fun invoke(entry: CatchEntry): CatchEntry = tripRepository.saveCatch(entry)

    fun isPersonalBest(entry: CatchEntry): Boolean {
        val previous = tripRepository.allCatches()
            .filter { it.species == entry.species && it.id != entry.id }
            .maxByOrNull { it.weightKg }
        return previous == null || entry.weightKg > previous.weightKg
    }
}
