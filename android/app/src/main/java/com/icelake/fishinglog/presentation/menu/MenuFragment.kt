package com.icelake.fishinglog.presentation.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.icelake.fishinglog.MainActivity
import com.icelake.fishinglog.R
import com.icelake.fishinglog.core.config.GameConfig
import com.icelake.fishinglog.core.di.AppViewModelFactory
import com.icelake.fishinglog.core.di.ServiceLocator
import com.icelake.fishinglog.core.util.MeasureFormatter
import com.icelake.fishinglog.databinding.FragmentMenuBinding
import com.icelake.fishinglog.presentation.dialog.ResetJournalDialog
import com.icelake.fishinglog.presentation.dialog.SettingsDialog
import com.icelake.fishinglog.presentation.dialog.SpotLibraryDialog
import kotlinx.coroutines.launch

class MenuFragment : Fragment() {

    private var binding: FragmentMenuBinding? = null
    private var adapter: TripCardAdapter? = null

    private val viewModel: MenuViewModel by viewModels {
        AppViewModelFactory {
            MenuViewModel(
                ServiceLocator.journalStatsUseCase(),
                ServiceLocator.recentTripsUseCase(),
                ServiceLocator.settingsRepository()
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = FragmentMenuBinding.inflate(inflater, container, false)
        binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val current = binding ?: return
        current.sectionRecent.bind(R.string.menu_recent_header)
        current.listTrips.layoutManager = LinearLayoutManager(requireContext())
        current.btnStartTrip.setOnClickListener { openGame() }
        current.btnSpotLibrary.setOnClickListener { openSpotLibrary() }
        current.btnSettings.setOnClickListener { openSettings() }
        registerDialogResults()
        playEntryAnimation()
        observeState()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun registerDialogResults() {
        val keys = listOf(
            SettingsDialog.RESULT_KEY,
            SpotLibraryDialog.RESULT_KEY,
            ResetJournalDialog.RESULT_KEY
        )
        for (key in keys) {
            parentFragmentManager.setFragmentResultListener(key, viewLifecycleOwner) { _, _ ->
                viewModel.refresh()
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: MenuUiState) {
        val current = binding ?: return
        if (!state.loaded) {
            return
        }
        val formatter = MeasureFormatter(current.root.context)
        val existing = adapter
        if (existing == null) {
            val created = TripCardAdapter(formatter, state.showTemperature)
            adapter = created
            current.listTrips.adapter = created
            created.submit(state.trips)
        } else {
            existing.submit(state.trips)
        }

        val hasCatches = state.stats.totalCatches > 0
        current.menuEmptyState.visibility = if (state.trips.isEmpty()) View.VISIBLE else View.GONE
        current.listTrips.visibility = if (state.trips.isEmpty()) View.GONE else View.VISIBLE
        current.menuStatColumn.visibility = if (hasCatches) View.VISIBLE else View.GONE
        current.txtMenuEmptyStat.visibility = if (hasCatches) View.GONE else View.VISIBLE

        if (hasCatches) {
            current.statCatches.bind(
                R.string.stat_catches,
                state.stats.totalCatches.toString(),
                R.color.ice_primary
            )
            current.statBest.bind(
                R.string.stat_best,
                formatter.weight(state.stats.heaviestKg, state.metricUnits),
                R.color.ice_accent
            )
            current.statCatches.alpha = 0f
            current.statCatches.animate()
                .alpha(1f)
                .setDuration(GameConfig.COUNTER_PULSE_MS)
                .start()
        }
    }

    private fun playEntryAnimation() {
        val current = binding ?: return
        current.menuBentoRow.alpha = 0f
        current.menuBentoRow.translationY = ENTRY_OFFSET
        current.menuBentoRow.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(GameConfig.MENU_ENTRY_MS)
            .start()
        current.txtCtaHint.alpha = 0f
        current.txtCtaHint.animate()
            .alpha(1f)
            .setStartDelay(HINT_DELAY)
            .setDuration(GameConfig.MENU_ENTRY_MS)
            .start()
    }

    private fun openGame() {
        if (!isAdded) {
            return
        }
        val host = activity as? MainActivity ?: return
        host.navigator.showGame()
    }

    private fun openSpotLibrary() {
        if (!isAdded || parentFragmentManager.isStateSaved) {
            return
        }
        SpotLibraryDialog().show(parentFragmentManager, SpotLibraryDialog.TAG)
    }

    private fun openSettings() {
        if (!isAdded || parentFragmentManager.isStateSaved) {
            return
        }
        SettingsDialog().show(parentFragmentManager, SettingsDialog.TAG)
    }

    override fun onDestroyView() {
        val current = binding
        if (current != null) {
            current.menuBentoRow.animate().cancel()
            current.txtCtaHint.animate().cancel()
            current.statCatches.animate().cancel()
            current.listTrips.adapter = null
        }
        adapter = null
        binding = null
        super.onDestroyView()
    }

    private companion object {
        const val ENTRY_OFFSET = 20f
        const val HINT_DELAY = 120L
    }
}
