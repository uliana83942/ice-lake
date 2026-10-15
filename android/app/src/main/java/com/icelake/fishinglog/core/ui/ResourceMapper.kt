package com.icelake.fishinglog.core.ui

import com.icelake.fishinglog.R
import com.icelake.fishinglog.domain.model.BaitType
import com.icelake.fishinglog.domain.model.FishSpecies
import com.icelake.fishinglog.domain.model.WeatherCondition

object ResourceMapper {

    fun speciesName(species: FishSpecies): Int = when (species) {
        FishSpecies.PIKE -> R.string.species_pike
        FishSpecies.PERCH -> R.string.species_perch
        FishSpecies.ZANDER -> R.string.species_zander
        FishSpecies.TROUT -> R.string.species_trout
        FishSpecies.BURBOT -> R.string.species_burbot
    }

    fun speciesSprite(species: FishSpecies): Int = when (species) {
        FishSpecies.PIKE -> R.drawable.sprite_fish_pike
        FishSpecies.PERCH -> R.drawable.sprite_fish_perch
        FishSpecies.ZANDER -> R.drawable.sprite_fish_zander
        FishSpecies.TROUT -> R.drawable.sprite_fish_trout
        FishSpecies.BURBOT -> R.drawable.sprite_fish_burbot
    }

    fun baitName(bait: BaitType): Int = when (bait) {
        BaitType.JIG -> R.string.bait_jig
        BaitType.SPOON -> R.string.bait_spoon
        BaitType.MORMYSHKA -> R.string.bait_mormyshka
        BaitType.LIVE_BAIT -> R.string.bait_live
        BaitType.BALANCE -> R.string.bait_balance
    }

    fun weatherName(weather: WeatherCondition): Int = when (weather) {
        WeatherCondition.CLEAR -> R.string.weather_clear
        WeatherCondition.SNOW -> R.string.weather_snow
        WeatherCondition.WIND -> R.string.weather_wind
        WeatherCondition.OVERCAST -> R.string.weather_overcast
        WeatherCondition.BLIZZARD -> R.string.weather_blizzard
    }

    fun spotSprite(sheltered: Boolean): Int =
        if (sheltered) R.drawable.sprite_tent else R.drawable.sprite_ice_hole
}
