package com.icelake.fishinglog.presentation.game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.icelake.fishinglog.R
import com.icelake.fishinglog.core.ui.ResourceMapper
import com.icelake.fishinglog.databinding.ItemSpeciesChipBinding
import com.icelake.fishinglog.domain.model.FishSpecies

class SpeciesAdapter(
    private val onPicked: (FishSpecies) -> Unit
) : RecyclerView.Adapter<SpeciesAdapter.SpeciesViewHolder>() {

    private val items = FishSpecies.values().toList()
    private var selected: FishSpecies = FishSpecies.PIKE

    fun select(species: FishSpecies) {
        if (selected == species) {
            return
        }
        selected = species
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeciesViewHolder {
        val binding = ItemSpeciesChipBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SpeciesViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: SpeciesViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class SpeciesViewHolder(
        private val binding: ItemSpeciesChipBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(species: FishSpecies) {
            val context = binding.root.context
            val isSelected = species == selected
            binding.imgSpecies.setImageResource(ResourceMapper.speciesSprite(species))
            binding.imgSpecies.contentDescription =
                context.getString(ResourceMapper.speciesName(species))
            binding.speciesChipRoot.setBackgroundResource(
                if (isSelected) R.drawable.shape_chip_selected else R.drawable.shape_chip_idle
            )
            val state = if (isSelected) R.string.chip_selected else R.string.chip_not_selected
            ViewCompat.setStateDescription(binding.speciesChipRoot, context.getString(state))
            binding.speciesChipRoot.setOnClickListener {
                select(species)
                onPicked(species)
            }
        }
    }
}
