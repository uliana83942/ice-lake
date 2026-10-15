package com.icelake.fishinglog.presentation.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.icelake.fishinglog.MainActivity
import com.icelake.fishinglog.databinding.FragmentSplashBinding
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private var binding: FragmentSplashBinding? = null
    private var animator: SplashAnimator? = null

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflated = FragmentSplashBinding.inflate(inflater, container, false)
        binding = inflated
        return inflated.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val current = binding ?: return
        val created = SplashAnimator(current)
        animator = created
        created.prepare()
        created.start()
        observeHandOff()
        observeProgress()
    }

    private fun observeHandOff() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.handOffReady.collect { ready ->
                    if (ready) {
                        handOff()
                    }
                }
            }
        }
    }

    private fun observeProgress() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.progressPercent.collect { percent ->
                    val current = binding ?: return@collect
                    current.progressSplash.contentDescription =
                        getString(com.icelake.fishinglog.R.string.splash_progress_desc)
                    if (percent >= FULL_PERCENT) {
                        current.progressSplash.alpha = 1f
                    }
                }
            }
        }
    }

    private fun handOff() {
        if (!isAdded || binding == null) {
            return
        }
        val host = activity as? MainActivity ?: return
        if (!host.navigator.showMenu()) {
            return
        }
        viewModel.consumeHandOff()
    }

    override fun onDestroyView() {
        animator?.cancel()
        animator = null
        binding = null
        super.onDestroyView()
    }

    private companion object {
        const val FULL_PERCENT = 100
    }
}
