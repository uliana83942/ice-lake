package com.icelake.fishinglog.domain.repository

import com.icelake.fishinglog.domain.model.CatchEntry

interface TripRepository {
    fun allCatches(): List<CatchEntry>
    fun saveCatch(entry: CatchEntry): CatchEntry
    fun findCatch(id: String): CatchEntry?
    fun clearJournal()
}
