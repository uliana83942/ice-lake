package com.icelake.fishinglog.domain.usecase

import com.icelake.fishinglog.domain.model.Spot
import com.icelake.fishinglog.domain.repository.SpotRepository

class GetSpotsUseCase(private val spotRepository: SpotRepository) {

    operator fun invoke(): List<Spot> = spotRepository.spots().sortedByDescending { it.favourite }
}
