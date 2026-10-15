package com.icelake.fishinglog.data.repository

import com.icelake.fishinglog.data.local.TripPreferences
import com.icelake.fishinglog.data.sample.SampleData
import com.icelake.fishinglog.domain.model.CatchEntry
import com.icelake.fishinglog.domain.repository.TripRepository

class TripRepositoryImpl(private val preferences: TripPreferences) : TripRepository {

    override fun allCatches(): List<CatchEntry> {
        ensureSeeded()
        return preferences.readCatches().sortedByDescending { it.recordedAtEpochDay }
    }

    override fun saveCatch(entry: CatchEntry): CatchEntry {
        ensureSeeded()
        val stored = entry.copy(id = preferences.nextId())
        val current = preferences.readCatches()
        current.add(stored)
        preferences.writeCatches(current)
        return stored
    }

    override fun findCatch(id: String): CatchEntry? =
        preferences.readCatches().firstOrNull { it.id == id }

    override fun clearJournal() {
        preferences.clear()
        preferences.writeCatches(emptyList())
    }

    private fun ensureSeeded() {
        if (preferences.isSeeded()) {
            return
        }
        preferences.writeCatches(SampleData.seedCatches())
        preferences.markSeeded()
    }
}
