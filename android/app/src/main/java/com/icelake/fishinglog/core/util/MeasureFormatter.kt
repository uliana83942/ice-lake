package com.icelake.fishinglog.core.util

import android.content.Context
import com.icelake.fishinglog.R
import java.util.Locale
import kotlin.math.roundToInt

class MeasureFormatter(private val context: Context) {

    fun weight(kilograms: Float, metric: Boolean): String {
        val value = if (metric) kilograms else kilograms * POUNDS_PER_KILOGRAM
        val formatted = String.format(Locale.US, "%.1f", value)
        val template = if (metric) R.string.format_weight_metric else R.string.format_weight_imperial
        return context.getString(template, formatted)
    }

    fun length(centimetres: Int, metric: Boolean): String {
        val value = if (metric) centimetres else (centimetres / CM_PER_INCH).roundToInt()
        val template = if (metric) R.string.format_length_metric else R.string.format_length_imperial
        return context.getString(template, value)
    }

    fun ice(centimetres: Int, metric: Boolean): String {
        val value = if (metric) centimetres else (centimetres / CM_PER_INCH).roundToInt()
        val template = if (metric) R.string.format_ice_metric else R.string.format_ice_imperial
        return context.getString(template, value)
    }

    fun temperature(celsius: Int): String = context.getString(R.string.format_temperature, celsius)

    private companion object {
        const val POUNDS_PER_KILOGRAM = 2.20462f
        const val CM_PER_INCH = 2.54f
    }
}
