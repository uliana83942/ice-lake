package com.icelake.fishinglog.presentation.gameover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.icelake.fishinglog.MainActivity
import com.icelake.fishinglog.R
import com.icelake.fishinglog.core.config.GameConfig
import com.icelake.fishinglog.core.di.AppViewModelFactory
import com.icelake.fishinglog.core.di.ServiceLocator
import com.icelake.fishinglog.core.ui.ResourceMapper
import com.icelake.fishinglog.core.util.MeasureFormatter
import com.icelake.fishinglog.databinding.FragmentGameOverBinding
import com.icelake.fishinglog.domain.model.CatchEntry
import kotlinx.coroutines.launch

class GameOverFragment : Fragment() {

    private var binding: FragmentGameOverBinding? = null

    private val viewModel: GameOverViewModel by viewModels {
        AppViewModelFactory {
            GameOverViewModel(
                ServiceLocator.tripRepository(),
                ServiceLocator.saveCatchUseCase(),
                ServiceLocator.settingsRepository()
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = FragmentGameOverBinding.inflate(inflater, container, false)
        binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val current = binding ?: return
        current.btnLogAgain.setOnClickListener { logAgain() }
        current.btnResultMenu.setOnClickListener { backToMenu() }
        playHeroAnimation()
        observeState()
        viewModel.load(arguments?.getString(ARG_CATCH_ID).orEmpty())
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: CatchDetailUiState) {
        val current = binding ?: return
        val entry = state.entry
        if (!state.loaded || entry == null) {
            return
        }
        val context = current.root.context
        val formatter = MeasureFormatter(context)
        val speciesName = context.getString(ResourceMapper.speciesName(entry.species))
        val weight = formatter.weight(entry.weightKg, state.metricUnits)
        val length = formatter.length(entry.lengthCm, state.metricUnits)

        current.imgHeroSpecies.setImageResource(ResourceMapper.speciesSprite(entry.species))
        current.imgHeroSpecies.contentDescription = speciesName
        current.txtHeroWeight.text = weight
        current.txtHeroSpecies.text = speciesName
        current.txtHeroBadge.visibility = if (state.personalBest) View.VISIBLE else View.GONE
        current.cardHero.contentDescription = context.getString(
            R.string.result_hero_desc,
            speciesName,
            weight,
            length,
            entry.spotName
        )
        if (state.personalBest) {
            playBadgeAnimation()
        }

        current.statResultLength.bind(R.string.stat_length, length, R.color.ice_primary)
        current.statResultBait.bind(
            R.string.stat_bait,
            context.getString(ResourceMapper.baitName(entry.bait)),
            R.color.ice_accent
        )
        current.statResultWeather.bind(
            R.string.stat_weather,
            context.getString(ResourceMapper.weatherName(entry.weather)),
            R.color.ice_primary_dark
        )
        current.statResultSpot.bind(R.string.stat_spot, entry.spotName, R.color.ice_primary)
        current.statResultIce.bind(
            R.string.stat_ice,
            formatter.ice(entry.iceThicknessCm, state.metricUnits),
            R.color.ice_accent
        )
        current.resultStatRow1.visibility = View.VISIBLE
        current.resultStatRow2.visibility =
            if (entry.spotName.isEmpty()) View.GONE else View.VISIBLE

        renderNotes(entry)
    }

    private fun renderNotes(entry: CatchEntry) {
        val current = binding ?: return
        if (entry.notes.isBlank()) {
            current.cardResultNotes.visibility = View.GONE
            return
        }
        current.cardResultNotes.visibility = View.VISIBLE
        current.txtResultNotes.text = entry.notes
    }

    private fun playHeroAnimation() {
        val current = binding ?: return
        current.cardHero.alpha = 0f
        current.cardHero.scaleX = HERO_SCALE
        current.cardHero.scaleY = HERO_SCALE
        current.cardHero.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setInterpolator(OvershootInterpolator(HERO_TENSION))
            .setDuration(GameConfig.HERO_ENTRY_MS)
            .start()
    }

    private fun playBadgeAnimation() {
        val current = binding ?: return
        current.txtHeroBadge.alpha = 0f
        current.txtHeroBadge.scaleX = BADGE_SCALE
        current.txtHeroBadge.scaleY = BADGE_SCALE
        current.txtHeroBadge.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setStartDelay(BADGE_DELAY)
            .setDuration(GameConfig.BADGE_ENTRY_MS)
            .start()
    }

    private fun logAgain() {
        if (!isAdded) {
            return
        }
        val host = activity as? MainActivity ?: return
        host.navigator.back()
    }

    private fun backToMenu() {
        if (!isAdded) {
            return
        }
        val host = activity as? MainActivity ?: return
        host.navigator.backToMenu()
    }

    override fun onDestroyView() {
        val current = binding
        if (current != null) {
            current.cardHero.animate().cancel()
            current.txtHeroBadge.animate().cancel()
        }
        binding = null
        super.onDestroyView()
    }

    companion object {
        const val ARG_CATCH_ID = "arg_catch_id"
        private const val HERO_SCALE = 0.88f
        private const val HERO_TENSION = 0.9f
        private const val BADGE_SCALE = 0.8f
        private const val BADGE_DELAY = 320L

        fun newInstance(catchId: String): GameOverFragment {
            val fragment = GameOverFragment()
            val args = Bundle()
            args.putString(ARG_CATCH_ID, catchId)
            fragment.arguments = args
            return fragment
        }
    }
}
