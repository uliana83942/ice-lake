package com.icelake.fishinglog.presentation.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.icelake.fishinglog.R
import com.icelake.fishinglog.core.ui.ResourceMapper
import com.icelake.fishinglog.core.util.DateFormatter
import com.icelake.fishinglog.core.util.MeasureFormatter
import com.icelake.fishinglog.databinding.ItemSpotCardBinding
import com.icelake.fishinglog.domain.model.Spot

class SpotAdapter(
    private val formatter: MeasureFormatter,
    private val metricUnits: Boolean,
    private val onPicked: (Spot) -> Unit
) : RecyclerView.Adapter<SpotAdapter.SpotViewHolder>() {

    private val items = mutableListOf<Spot>()
    private var selectedId: String = ""

    fun submit(spots: List<Spot>, preselectedId: String) {
        items.clear()
        items.addAll(spots)
        selectedId = preselectedId
        notifyDataSetChanged()
    }

    fun selectedSpot(): Spot? = items.firstOrNull { it.id == selectedId }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpotViewHolder {
        val binding = ItemSpotCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SpotViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: SpotViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class SpotViewHolder(
        private val binding: ItemSpotCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(spot: Spot) {
            val context = binding.root.context
            binding.txtSpotName.text = spot.name
            binding.txtSpotMeta.text = context.getString(
                R.string.spot_meta_format,
                formatter.ice(spot.iceThicknessCm, metricUnits),
                spot.catchCount,
                DateFormatter.shortDate(spot.lastVisitEpochDay)
            )
            binding.imgSpot.setImageResource(ResourceMapper.spotSprite(spot.sheltered))
            binding.txtSpotFavourite.visibility = if (spot.favourite) View.VISIBLE else View.GONE
            val isSelected = spot.id == selectedId
            binding.spotCard.setStrokeColor(
                androidx.core.content.ContextCompat.getColor(
                    context,
                    if (isSelected) R.color.ice_accent else R.color.ice_stroke
                )
            )
            val state = if (isSelected) R.string.chip_selected else R.string.chip_not_selected
            ViewCompat.setStateDescription(binding.spotCard, context.getString(state))
            binding.spotCard.setOnClickListener {
                selectedId = spot.id
                notifyDataSetChanged()
                onPicked(spot)
            }
        }
    }
}
