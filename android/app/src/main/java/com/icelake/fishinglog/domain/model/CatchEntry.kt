package com.icelake.fishinglog.domain.model

data class CatchEntry(
    val id: String,
    val species: FishSpecies,
    val weightKg: Float,
    val lengthCm: Int,
    val bait: BaitType,
    val weather: WeatherCondition,
    val spotName: String,
    val iceThicknessCm: Int,
    val temperatureC: Int,
    val notes: String,
    val recordedAtEpochDay: Long,
    val recordedAtMinuteOfDay: Int
)
