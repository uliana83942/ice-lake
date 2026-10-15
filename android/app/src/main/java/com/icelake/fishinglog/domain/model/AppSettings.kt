package com.icelake.fishinglog.domain.model

data class AppSettings(
    val soundEnabled: Boolean,
    val hapticEnabled: Boolean,
    val metricUnits: Boolean,
    val showTemperature: Boolean
)
