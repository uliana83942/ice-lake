package com.icelake.fishinglog.presentation.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.icelake.fishinglog.MainActivity
import com.icelake.fishinglog.R
import com.icelake.fishinglog.core.di.AppViewModelFactory
import com.icelake.fishinglog.core.di.ServiceLocator
import com.icelake.fishinglog.core.ui.ResourceMapper
import com.icelake.fishinglog.core.util.DateFormatter
import com.icelake.fishinglog.core.util.MeasureFormatter
import com.icelake.fishinglog.databinding.FragmentGameBinding
import com.icelake.fishinglog.domain.model.BaitType
import com.icelake.fishinglog.domain.model.WeatherCondition
import com.icelake.fishinglog.presentation.dialog.SpotLibraryDialog
import kotlinx.coroutines.launch

class GameFragment : Fragment() {

    private var binding: FragmentGameBinding? = null
    private var speciesAdapter: SpeciesAdapter? = null
    private var rowsBound = false

    private val viewModel: GameViewModel by viewModels {
        AppViewModelFactory {
            GameViewModel(
                ServiceLocator.saveCatchUseCase(),
                ServiceLocator.validateCatchUseCase(),
                ServiceLocator.spotRepository(),
                ServiceLocator.settingsRepository(),
                ServiceLocator.tripRepository()
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = FragmentGameBinding.inflate(inflater, container, false)
        binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val current = binding ?: return
        current.sectionSpecies.bind(R.string.game_species_header)
        setUpSpecies(current)
        setUpChipRows(current)
        setUpSliders(current)
        setUpNotes(current)
        current.btnGameBack.setOnClickListener { goBack() }
        current.btnSaveCatch.setOnClickListener { viewModel.save() }
        current.txtGameStrip.setOnClickListener { openSpotLibrary() }
        registerDialogResults()
        observeState()
        observeSaved()
    }

    override fun onResume() {
        super.onResume()
        viewModel.reloadSpot()
    }

    private fun registerDialogResults() {
        parentFragmentManager.setFragmentResultListener(
            SpotLibraryDialog.RESULT_KEY,
            viewLifecycleOwner
        ) { _, _ ->
            viewModel.reloadSpot()
        }
    }

    private fun setUpSpecies(current: FragmentGameBinding) {
        val adapter = SpeciesAdapter { species -> viewModel.setSpecies(species) }
        speciesAdapter = adapter
        current.listSpecies.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        current.listSpecies.adapter = adapter
    }

    private fun setUpChipRows(current: FragmentGameBinding) {
        if (rowsBound) {
            return
        }
        rowsBound = true
        current.rowBait.bind(R.string.game_bait_label, BAIT_LABELS, 0) { index ->
            viewModel.setBait(BaitType.values()[index])
        }
        current.rowWeather.bind(R.string.game_weather_label, WEATHER_LABELS, 1) { index ->
            viewModel.setWeather(WeatherCondition.values()[index])
        }
    }

    private fun setUpSliders(current: FragmentGameBinding) {
        current.sliderWeight.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                viewModel.setWeight(value)
            }
        }
        current.sliderLength.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                viewModel.setLength(value.toInt())
            }
        }
    }

    private fun setUpNotes(current: FragmentGameBinding) {
        current.editNotes.doAfterTextChanged { editable ->
            viewModel.setNotes(editable?.toString().orEmpty())
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun observeSaved() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.savedCatchId.collect { catchId ->
                    if (catchId != null) {
                        openResult(catchId)
                    }
                }
            }
        }
    }

    private fun render(state: GameUiState) {
        val current = binding ?: return
        if (!state.loaded) {
            return
        }
        val context = current.root.context
        val formatter = MeasureFormatter(context)

        speciesAdapter?.select(state.species)
        current.imgPreviewSpecies.setImageResource(ResourceMapper.speciesSprite(state.species))
        current.txtPreviewSpecies.setText(ResourceMapper.speciesName(state.species))
        current.txtPreviewWeight.text = formatter.weight(state.weightKg, state.metricUnits)
        current.txtPreviewLength.text = formatter.length(state.lengthCm, state.metricUnits)

        if (current.sliderWeight.value != state.weightKg) {
            current.sliderWeight.value = state.weightKg
        }
        val lengthValue = state.lengthCm.toFloat()
        if (current.sliderLength.value != lengthValue) {
            current.sliderLength.value = lengthValue
        }

        current.txtGameCount.text = context.getString(R.string.game_logged_count, state.loggedCount)

        val spot = state.spot
        val iceCm = spot?.iceThicknessCm ?: 0
        val temperature = if (state.showTemperature) {
            formatter.temperature(viewModel.temperatureFor(iceCm))
        } else {
            context.getString(ResourceMapper.weatherName(state.weather))
        }
        current.txtGameStrip.text = context.getString(
            R.string.game_strip_format,
            spot?.name.orEmpty(),
            formatter.ice(iceCm, state.metricUnits),
            temperature,
            DateFormatter.timeOfDay(DateFormatter.nowMinuteOfDay())
        )

        val enabled = state.valid && !state.saving
        current.btnSaveCatch.isEnabled = enabled
        current.btnSaveCatch.alpha = if (enabled) 1f else DISABLED_ALPHA
        ViewCompat.setStateDescription(
            current.btnSaveCatch,
            context.getString(
                if (enabled) R.string.game_enabled_desc else R.string.game_disabled_desc
            )
        )
        current.txtGameWarning.visibility = if (state.valid) View.INVISIBLE else View.VISIBLE
    }

    private fun openResult(catchId: String) {
        if (!isAdded || binding == null) {
            return
        }
        val host = activity as? MainActivity ?: return
        if (!host.navigator.showResult(catchId)) {
            return
        }
        viewModel.consumeSaved()
    }

    private fun openSpotLibrary() {
        if (!isAdded || parentFragmentManager.isStateSaved) {
            return
        }
        SpotLibraryDialog().show(parentFragmentManager, SpotLibraryDialog.TAG)
    }

    private fun goBack() {
        if (!isAdded) {
            return
        }
        val host = activity as? MainActivity ?: return
        host.navigator.back()
    }

    override fun onDestroyView() {
        val current = binding
        if (current != null) {
            current.listSpecies.adapter = null
        }
        speciesAdapter = null
        rowsBound = false
        binding = null
        super.onDestroyView()
    }

    private companion object {
        const val DISABLED_ALPHA = 0.6f

        val BAIT_LABELS = listOf(
            R.string.bait_jig,
            R.string.bait_spoon,
            R.string.bait_mormyshka,
            R.string.bait_live,
            R.string.bait_balance
        )

        val WEATHER_LABELS = listOf(
            R.string.weather_clear,
            R.string.weather_snow,
            R.string.weather_wind,
            R.string.weather_overcast,
            R.string.weather_blizzard
        )
    }
}
