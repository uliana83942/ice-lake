package com.icelake.fishinglog.domain.model

data class Trip(
    val id: String,
    val spotName: String,
    val species: FishSpecies,
    val dateEpochDay: Long,
    val temperatureC: Int,
    val catchCount: Int
)
