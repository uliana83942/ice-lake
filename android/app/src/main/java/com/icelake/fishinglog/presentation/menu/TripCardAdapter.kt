package com.icelake.fishinglog.presentation.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.icelake.fishinglog.R
import com.icelake.fishinglog.core.ui.ResourceMapper
import com.icelake.fishinglog.core.util.DateFormatter
import com.icelake.fishinglog.core.util.MeasureFormatter
import com.icelake.fishinglog.databinding.ItemTripCardBinding
import com.icelake.fishinglog.domain.model.Trip

class TripCardAdapter(
    private val formatter: MeasureFormatter,
    private val showTemperature: Boolean
) : RecyclerView.Adapter<TripCardAdapter.TripViewHolder>() {

    private val items = mutableListOf<Trip>()

    fun submit(trips: List<Trip>) {
        items.clear()
        items.addAll(trips)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = ItemTripCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TripViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    inner class TripViewHolder(
        private val binding: ItemTripCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: Trip, position: Int) {
            val context = binding.root.context
            binding.txtTripTitle.text = trip.spotName
            val date = DateFormatter.shortDate(trip.dateEpochDay)
            val temperature = if (showTemperature) {
                formatter.temperature(trip.temperatureC)
            } else {
                context.getString(ResourceMapper.speciesName(trip.species))
            }
            binding.txtTripMeta.text = context.getString(
                R.string.trip_meta_format,
                date,
                temperature,
                trip.catchCount
            )
            binding.imgTripSpecies.setImageResource(ResourceMapper.speciesSprite(trip.species))
            binding.imgTripSpecies.contentDescription =
                context.getString(ResourceMapper.speciesName(trip.species))
            binding.root.contentDescription = context.getString(
                R.string.trip_card_desc,
                trip.spotName,
                trip.catchCount
            )
            binding.root.alpha = 0f
            binding.root.translationY = ENTRY_OFFSET
            binding.root.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(position * STAGGER_MS)
                .setDuration(ENTRY_MS)
                .start()
        }
    }

    private companion object {
        const val ENTRY_OFFSET = 16f
        const val ENTRY_MS = 260L
        const val STAGGER_MS = 60L
    }
}
