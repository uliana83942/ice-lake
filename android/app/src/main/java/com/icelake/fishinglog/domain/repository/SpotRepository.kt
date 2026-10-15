package com.icelake.fishinglog.domain.repository

import com.icelake.fishinglog.domain.model.Spot

interface SpotRepository {
    fun spots(): List<Spot>
    fun selectedSpot(): Spot
    fun selectSpot(spotId: String)
}
