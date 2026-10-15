package com.icelake.fishinglog.core.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import com.google.android.material.chip.Chip
import com.icelake.fishinglog.R
import com.icelake.fishinglog.databinding.ViewChipRowBinding

class ChipRowView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val binding: ViewChipRowBinding =
        ViewChipRowBinding.inflate(LayoutInflater.from(context), this)

    private var listener: ((Int) -> Unit)? = null
    private var suppress = false

    init {
        orientation = VERTICAL
    }

    fun bind(labelRes: Int, optionRes: List<Int>, selectedIndex: Int, onSelected: (Int) -> Unit) {
        listener = onSelected
        binding.txtChipRowLabel.setText(labelRes)
        binding.chipGroupRow.removeAllViews()
        val inflater = LayoutInflater.from(context)
        suppress = true
        for (index in optionRes.indices) {
            val chip = inflater.inflate(R.layout.item_chip, binding.chipGroupRow, false) as Chip
            chip.id = View.generateViewId()
            chip.setText(optionRes[index])
            chip.isChecked = index == selectedIndex
            applyState(chip)
            chip.setOnCheckedChangeListener { _, checked ->
                applyState(chip)
                if (checked && !suppress) {
                    listener?.invoke(index)
                }
            }
            binding.chipGroupRow.addView(chip)
        }
        suppress = false
    }

    fun select(index: Int) {
        suppress = true
        for (position in 0 until binding.chipGroupRow.childCount) {
            val chip = binding.chipGroupRow.getChildAt(position) as? Chip ?: continue
            chip.isChecked = position == index
            applyState(chip)
        }
        suppress = false
    }

    private fun applyState(chip: Chip) {
        val state = if (chip.isChecked) R.string.chip_selected else R.string.chip_not_selected
        ViewCompat.setStateDescription(chip, context.getString(state))
    }
}
