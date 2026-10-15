package com.icelake.fishinglog.core.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateFormatter {

    private val shortDateFormat = DateTimeFormatter.ofPattern("d MMM", Locale.US)

    fun shortDate(epochDay: Long): String =
        LocalDate.ofEpochDay(epochDay).format(shortDateFormat).uppercase(Locale.US)

    fun timeOfDay(minuteOfDay: Int): String {
        val safe = ((minuteOfDay % 1440) + 1440) % 1440
        return String.format(Locale.US, "%02d:%02d", safe / 60, safe % 60)
    }

    fun todayEpochDay(): Long = LocalDate.now().toEpochDay()

    fun nowMinuteOfDay(): Int {
        val now = LocalTime.now()
        return now.hour * 60 + now.minute
    }
}
