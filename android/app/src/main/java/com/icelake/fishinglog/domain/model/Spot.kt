package com.icelake.fishinglog.domain.model

data class Spot(
    val id: String,
    val name: String,
    val iceThicknessCm: Int,
    val catchCount: Int,
    val lastVisitEpochDay: Long,
    val favourite: Boolean,
    val sheltered: Boolean
)
