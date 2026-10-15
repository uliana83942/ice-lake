package com.icelake.fishinglog.data.local

import android.content.Context
import android.content.SharedPreferences
import com.icelake.fishinglog.domain.model.BaitType
import com.icelake.fishinglog.domain.model.CatchEntry
import com.icelake.fishinglog.domain.model.FishSpecies
import com.icelake.fishinglog.domain.model.WeatherCondition

class TripPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    fun isSeeded(): Boolean = prefs.getBoolean(KEY_SEEDED, false)

    fun markSeeded() {
        prefs.edit().putBoolean(KEY_SEEDED, true).apply()
    }

    fun readCatches(): MutableList<CatchEntry> {
        val raw = prefs.getString(KEY_CATCHES, "").orEmpty()
        val result = mutableListOf<CatchEntry>()
        if (raw.isEmpty()) {
            return result
        }
        for (record in raw.split(RECORD_SEPARATOR)) {
            if (record.isBlank()) {
                continue
            }
            val parsed = decode(record)
            if (parsed != null) {
                result.add(parsed)
            }
        }
        return result
    }

    fun writeCatches(entries: List<CatchEntry>) {
        val encoded = entries.joinToString(RECORD_SEPARATOR) { encode(it) }
        prefs.edit().putString(KEY_CATCHES, encoded).apply()
    }

    fun clear() {
        prefs.edit().remove(KEY_CATCHES).apply()
    }

    fun nextId(): String {
        val counter = prefs.getInt(KEY_COUNTER, 0) + 1
        prefs.edit().putInt(KEY_COUNTER, counter).apply()
        return "catch_" + counter
    }

    private fun encode(entry: CatchEntry): String = listOf(
        entry.id,
        entry.species.name,
        entry.weightKg.toString(),
        entry.lengthCm.toString(),
        entry.bait.name,
        entry.weather.name,
        entry.spotName,
        entry.iceThicknessCm.toString(),
        entry.temperatureC.toString(),
        entry.notes.replace(FIELD_SEPARATOR, " ").replace(RECORD_SEPARATOR, " "),
        entry.recordedAtEpochDay.toString(),
        entry.recordedAtMinuteOfDay.toString()
    ).joinToString(FIELD_SEPARATOR)

    private fun decode(record: String): CatchEntry? {
        val parts = record.split(FIELD_SEPARATOR)
        if (parts.size < FIELD_COUNT) {
            return null
        }
        val species = FishSpecies.values().firstOrNull { it.name == parts[1] } ?: return null
        val bait = BaitType.values().firstOrNull { it.name == parts[4] } ?: return null
        val weather = WeatherCondition.values().firstOrNull { it.name == parts[5] } ?: return null
        val weight = parts[2].toFloatOrNull() ?: return null
        val length = parts[3].toIntOrNull() ?: return null
        val ice = parts[7].toIntOrNull() ?: return null
        val temperature = parts[8].toIntOrNull() ?: return null
        val epochDay = parts[10].toLongOrNull() ?: return null
        val minute = parts[11].toIntOrNull() ?: return null
        return CatchEntry(
            id = parts[0],
            species = species,
            weightKg = weight,
            lengthCm = length,
            bait = bait,
            weather = weather,
            spotName = parts[6],
            iceThicknessCm = ice,
            temperatureC = temperature,
            notes = parts[9],
            recordedAtEpochDay = epochDay,
            recordedAtMinuteOfDay = minute
        )
    }

    private companion object {
        const val STORE_NAME = "ice_lake_journal"
        const val KEY_CATCHES = "catches"
        const val KEY_SEEDED = "seeded"
        const val KEY_COUNTER = "counter"
        const val FIELD_SEPARATOR = "|"
        const val RECORD_SEPARATOR = "\n"
        const val FIELD_COUNT = 12
    }
}
