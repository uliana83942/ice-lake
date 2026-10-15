package com.icelake.fishinglog.core.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.icelake.fishinglog.databinding.ViewSectionHeaderBinding

class SectionHeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val binding: ViewSectionHeaderBinding =
        ViewSectionHeaderBinding.inflate(LayoutInflater.from(context), this)

    init {
        orientation = VERTICAL
    }

    fun bind(titleRes: Int) {
        binding.txtSectionTitle.setText(titleRes)
    }

    fun showRule(visible: Boolean) {
        binding.sectionRule.visibility = if (visible) VISIBLE else GONE
    }
}
