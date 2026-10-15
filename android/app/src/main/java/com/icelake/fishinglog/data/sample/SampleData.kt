package com.icelake.fishinglog.data.sample

import com.icelake.fishinglog.domain.model.BaitType
import com.icelake.fishinglog.domain.model.CatchEntry
import com.icelake.fishinglog.domain.model.FishSpecies
import com.icelake.fishinglog.domain.model.Spot
import com.icelake.fishinglog.domain.model.WeatherCondition
import java.time.LocalDate

object SampleData {

    private fun day(year: Int, month: Int, dayOfMonth: Int): Long =
        LocalDate.of(year, month, dayOfMonth).toEpochDay()

    fun spots(): MutableList<Spot> {
        val list = mutableListOf<Spot>()
        list.add(Spot("spot_north_shelf", "NORTH SHELF", 24, 6, day(2026, 2, 12), true, false))
        list.add(Spot("spot_birch_bay", "BIRCH BAY", 19, 4, day(2026, 2, 3), false, true))
        list.add(Spot("spot_old_dam", "OLD DAM", 31, 5, day(2026, 1, 24), true, false))
        list.add(Spot("spot_reed_edge", "REED EDGE", 16, 3, day(2026, 1, 11), false, true))
        list.add(Spot("spot_deep_channel", "DEEP CHANNEL", 28, 7, day(2025, 12, 29), false, false))
        list.add(Spot("spot_stone_point", "STONE POINT", 22, 2, day(2025, 12, 14), false, true))
        return list
    }

    fun seedCatches(): MutableList<CatchEntry> {
        val list = mutableListOf<CatchEntry>()
        list.add(
            CatchEntry(
                id = "seed_01",
                species = FishSpecies.PIKE,
                weightKg = 2.4f,
                lengthCm = 61,
                bait = BaitType.JIG,
                weather = WeatherCondition.SNOW,
                spotName = "NORTH SHELF",
                iceThicknessCm = 24,
                temperatureC = -14,
                notes = "Bite window right after sunrise, 4.2 m under the hole",
                recordedAtEpochDay = day(2026, 2, 12),
                recordedAtMinuteOfDay = 580
            )
        )
        list.add(
            CatchEntry(
                id = "seed_02",
                species = FishSpecies.PERCH,
                weightKg = 0.4f,
                lengthCm = 24,
                bait = BaitType.MORMYSHKA,
                weather = WeatherCondition.CLEAR,
                spotName = "NORTH SHELF",
                iceThicknessCm = 24,
                temperatureC = -14,
                notes = "Shoal moved along the drop-off, five holes drilled in a line",
                recordedAtEpochDay = day(2026, 2, 12),
                recordedAtMinuteOfDay = 640
            )
        )
        list.add(
            CatchEntry(
                id = "seed_03",
                species = FishSpecies.ZANDER,
                weightKg = 1.8f,
                lengthCm = 52,
                bait = BaitType.BALANCE,
                weather = WeatherCondition.OVERCAST,
                spotName = "BIRCH BAY",
                iceThicknessCm = 19,
                temperatureC = -8,
                notes = "Slow lift, long pause, took it on the drop",
                recordedAtEpochDay = day(2026, 2, 3),
                recordedAtMinuteOfDay = 905
            )
        )
        list.add(
            CatchEntry(
                id = "seed_04",
                species = FishSpecies.PERCH,
                weightKg = 0.6f,
                lengthCm = 29,
                bait = BaitType.MORMYSHKA,
                weather = WeatherCondition.OVERCAST,
                spotName = "BIRCH BAY",
                iceThicknessCm = 19,
                temperatureC = -8,
                notes = "Warmer afternoon, bite picked up when the wind dropped",
                recordedAtEpochDay = day(2026, 2, 3),
                recordedAtMinuteOfDay = 960
            )
        )
        list.add(
            CatchEntry(
                id = "seed_05",
                species = FishSpecies.TROUT,
                weightKg = 1.3f,
                lengthCm = 44,
                bait = BaitType.SPOON,
                weather = WeatherCondition.WIND,
                spotName = "OLD DAM",
                iceThicknessCm = 31,
                temperatureC = -17,
                notes = "Thick ice over the old spillway, clean water below",
                recordedAtEpochDay = day(2026, 1, 24),
                recordedAtMinuteOfDay = 495
            )
        )
        list.add(
            CatchEntry(
                id = "seed_06",
                species = FishSpecies.BURBOT,
                weightKg = 1.1f,
                lengthCm = 47,
                bait = BaitType.LIVE_BAIT,
                weather = WeatherCondition.BLIZZARD,
                spotName = "DEEP CHANNEL",
                iceThicknessCm = 28,
                temperatureC = -21,
                notes = "Night session, set lines along the channel edge",
                recordedAtEpochDay = day(2025, 12, 29),
                recordedAtMinuteOfDay = 1230
            )
        )
        list.add(
            CatchEntry(
                id = "seed_07",
                species = FishSpecies.PIKE,
                weightKg = 3.1f,
                lengthCm = 72,
                bait = BaitType.LIVE_BAIT,
                weather = WeatherCondition.SNOW,
                spotName = "DEEP CHANNEL",
                iceThicknessCm = 28,
                temperatureC = -21,
                notes = "Season best so far, took twelve minutes to bring up",
                recordedAtEpochDay = day(2025, 12, 29),
                recordedAtMinuteOfDay = 1290
            )
        )
        list.add(
            CatchEntry(
                id = "seed_08",
                species = FishSpecies.PERCH,
                weightKg = 0.3f,
                lengthCm = 21,
                bait = BaitType.JIG,
                weather = WeatherCondition.CLEAR,
                spotName = "STONE POINT",
                iceThicknessCm = 22,
                temperatureC = -6,
                notes = "First ice of the season, shallow stones on the sunny side",
                recordedAtEpochDay = day(2025, 12, 14),
                recordedAtMinuteOfDay = 700
            )
        )
        return list
    }
}
