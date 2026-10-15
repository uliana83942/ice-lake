package com.icelake.fishinglog.domain.usecase

import com.icelake.fishinglog.domain.model.FishSpecies

class ValidateCatchUseCase {

    operator fun invoke(species: FishSpecies?, weightKg: Float, minimumWeightKg: Float): Boolean =
        species != null && weightKg >= minimumWeightKg
}
