package com.icelake.fishinglog.domain.model

data class JournalStats(
    val totalCatches: Int,
    val heaviestKg: Float,
    val heaviestSpecies: FishSpecies?,
    val tripCount: Int,
    val favouriteSpot: String
)
