package com.icelake.fishinglog.domain.usecase

import com.icelake.fishinglog.domain.model.Trip
import com.icelake.fishinglog.domain.repository.TripRepository

class GetRecentTripsUseCase(private val tripRepository: TripRepository) {

    operator fun invoke(limit: Int): List<Trip> {
        val grouped = tripRepository.allCatches()
            .groupBy { it.spotName + "#" + it.recordedAtEpochDay }
        val trips = mutableListOf<Trip>()
        for (entry in grouped) {
            val first = entry.value.firstOrNull() ?: continue
            val heaviest = entry.value.maxByOrNull { it.weightKg } ?: first
            trips.add(
                Trip(
                    id = entry.key,
                    spotName = first.spotName,
                    species = heaviest.species,
                    dateEpochDay = first.recordedAtEpochDay,
                    temperatureC = first.temperatureC,
                    catchCount = entry.value.size
                )
            )
        }
        trips.sortByDescending { it.dateEpochDay }
        return if (trips.size > limit) trips.subList(0, limit).toList() else trips.toList()
    }
}
