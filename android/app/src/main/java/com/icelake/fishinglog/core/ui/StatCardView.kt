package com.icelake.fishinglog.core.ui

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.google.android.material.card.MaterialCardView
import com.icelake.fishinglog.R
import com.icelake.fishinglog.databinding.ViewStatCardBinding

class StatCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : MaterialCardView(context, attrs) {

    private val binding: ViewStatCardBinding =
        ViewStatCardBinding.inflate(LayoutInflater.from(context), this)

    init {
        radius = resources.getDimension(R.dimen.radius_m)
        cardElevation = resources.getDimension(R.dimen.elevation_s)
        setStrokeWidth(resources.getDimensionPixelSize(R.dimen.hairline))
        setStrokeColor(ContextCompat.getColor(context, R.color.ice_stroke))
        setCardBackgroundColor(ContextCompat.getColor(context, R.color.ice_surface))
        isClickable = false
        isFocusable = false
    }

    fun bind(labelRes: Int, value: String, accentColorRes: Int) {
        val accent = ContextCompat.getColor(context, accentColorRes)
        binding.txtStatLabel.setText(labelRes)
        binding.txtStatValue.text = value
        binding.txtStatValue.setTextColor(accent)
        binding.statAccentDot.backgroundTintList = ColorStateList.valueOf(accent)
        val description = context.getString(labelRes) + " " + value
        contentDescription = description
        ViewCompat.setStateDescription(this, value)
    }
}
