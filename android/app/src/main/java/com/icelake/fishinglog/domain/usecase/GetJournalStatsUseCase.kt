package com.icelake.fishinglog.domain.usecase

import com.icelake.fishinglog.domain.model.JournalStats
import com.icelake.fishinglog.domain.repository.TripRepository

class GetJournalStatsUseCase(private val tripRepository: TripRepository) {

    operator fun invoke(): JournalStats {
        val catches = tripRepository.allCatches()
        if (catches.isEmpty()) {
            return JournalStats(0, 0f, null, 0, "")
        }
        val heaviest = catches.maxByOrNull { it.weightKg }
        val trips = catches.map { it.spotName + "#" + it.recordedAtEpochDay }.distinct().size
        val favourite = catches.groupBy { it.spotName }
            .maxByOrNull { it.value.size }
            ?.key
            .orEmpty()
        return JournalStats(
            totalCatches = catches.size,
            heaviestKg = heaviest?.weightKg ?: 0f,
            heaviestSpecies = heaviest?.species,
            tripCount = trips,
            favouriteSpot = favourite
        )
    }
}
