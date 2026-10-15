package com.icelake.fishinglog.presentation.splash

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import com.icelake.fishinglog.core.config.GameConfig
import com.icelake.fishinglog.databinding.FragmentSplashBinding

class SplashAnimator(private val binding: FragmentSplashBinding) {

    private val running = mutableListOf<Animator>()

    fun prepare() {
        binding.splashMark.scaleX = ENTRY_SCALE
        binding.splashMark.scaleY = ENTRY_SCALE
        binding.splashMark.alpha = 0f
        binding.splashRing.alpha = 0f
        binding.txtSplashTitle.alpha = 0f
        binding.txtSplashTitle.translationY = TITLE_OFFSET
        binding.txtSplashSubtitle.alpha = 0f
        binding.txtSplashSubtitle.translationY = TITLE_OFFSET
        binding.txtSplashTagline.alpha = 0f
        binding.progressSplash.alpha = 0f
        for (dot in snowDots()) {
            dot.alpha = 0f
        }
    }

    fun start() {
        cancel()
        playMarkEntry()
        playTextEntry()
        playIdleLoops()
        playSnowDrift()
    }

    private fun playMarkEntry() {
        val scaleX = ObjectAnimator.ofFloat(binding.splashMark, View.SCALE_X, ENTRY_SCALE, 1f)
        val scaleY = ObjectAnimator.ofFloat(binding.splashMark, View.SCALE_Y, ENTRY_SCALE, 1f)
        val fade = ObjectAnimator.ofFloat(binding.splashMark, View.ALPHA, 0f, 1f)
        val ringFade = ObjectAnimator.ofFloat(binding.splashRing, View.ALPHA, 0f, 1f)
        val set = AnimatorSet()
        set.playTogether(scaleX, scaleY, fade, ringFade)
        set.duration = GameConfig.SPLASH_MARK_ENTRY_MS
        set.interpolator = OvershootInterpolator(OVERSHOOT_TENSION)
        set.start()
        running.add(set)
    }

    private fun playTextEntry() {
        val title = slideUp(binding.txtSplashTitle, GameConfig.SPLASH_TITLE_DELAY_MS)
        val subtitle = slideUp(binding.txtSplashSubtitle, GameConfig.SPLASH_SUBTITLE_DELAY_MS)
        val progress = ObjectAnimator.ofFloat(binding.progressSplash, View.ALPHA, 0f, 1f)
        progress.duration = GameConfig.SPLASH_TEXT_ENTRY_MS
        progress.startDelay = PROGRESS_DELAY
        progress.interpolator = DecelerateInterpolator()
        progress.start()
        val tagline = ObjectAnimator.ofFloat(binding.txtSplashTagline, View.ALPHA, 0f, 1f)
        tagline.duration = GameConfig.SPLASH_TEXT_ENTRY_MS
        tagline.startDelay = TAGLINE_DELAY
        tagline.interpolator = DecelerateInterpolator()
        tagline.start()
        running.add(title)
        running.add(subtitle)
        running.add(progress)
        running.add(tagline)
    }

    private fun slideUp(target: View, delay: Long): Animator {
        val fade = ObjectAnimator.ofFloat(target, View.ALPHA, 0f, 1f)
        val rise = ObjectAnimator.ofFloat(target, View.TRANSLATION_Y, TITLE_OFFSET, 0f)
        val set = AnimatorSet()
        set.playTogether(fade, rise)
        set.duration = GameConfig.SPLASH_TEXT_ENTRY_MS
        set.startDelay = delay
        set.interpolator = DecelerateInterpolator()
        set.start()
        return set
    }

    private fun playIdleLoops() {
        val pulseX = ObjectAnimator.ofFloat(binding.splashMark, View.SCALE_X, 1f, PULSE_SCALE)
        val pulseY = ObjectAnimator.ofFloat(binding.splashMark, View.SCALE_Y, 1f, PULSE_SCALE)
        for (pulse in listOf(pulseX, pulseY)) {
            pulse.duration = GameConfig.SPLASH_PULSE_MS
            pulse.startDelay = GameConfig.SPLASH_MARK_ENTRY_MS
            pulse.repeatCount = ValueAnimator.INFINITE
            pulse.repeatMode = ValueAnimator.REVERSE
            pulse.interpolator = AccelerateDecelerateInterpolator()
            pulse.start()
            running.add(pulse)
        }
        val spin = ObjectAnimator.ofFloat(binding.splashRing, View.ROTATION, 0f, FULL_TURN)
        spin.duration = GameConfig.SPLASH_RING_MS
        spin.repeatCount = ValueAnimator.INFINITE
        spin.repeatMode = ValueAnimator.RESTART
        spin.interpolator = LinearInterpolator()
        spin.start()
        running.add(spin)
    }

    private fun playSnowDrift() {
        val dots = snowDots()
        for (index in dots.indices) {
            val dot = dots[index]
            val appear = ObjectAnimator.ofFloat(dot, View.ALPHA, 0f, 1f)
            appear.duration = GameConfig.SPLASH_TEXT_ENTRY_MS
            appear.startDelay = index * STAGGER_MS
            appear.start()
            running.add(appear)
            val drift = ObjectAnimator.ofFloat(dot, View.TRANSLATION_Y, 0f, DRIFT_DISTANCE)
            drift.duration = GameConfig.SPLASH_DOT_BASE_MS + index * GameConfig.SPLASH_DOT_STEP_MS
            drift.startDelay = index * STAGGER_MS
            drift.repeatCount = ValueAnimator.INFINITE
            drift.repeatMode = ValueAnimator.REVERSE
            drift.interpolator = AccelerateDecelerateInterpolator()
            drift.start()
            running.add(drift)
        }
    }

    private fun snowDots(): List<View> = listOf(
        binding.dot1,
        binding.dot2,
        binding.dot3,
        binding.dot4,
        binding.dot5,
        binding.dot6,
        binding.dot7,
        binding.dot8
    )

    fun cancel() {
        for (animator in running) {
            animator.removeAllListeners()
            animator.cancel()
        }
        running.clear()
    }

    private companion object {
        const val ENTRY_SCALE = 0.72f
        const val PULSE_SCALE = 1.045f
        const val TITLE_OFFSET = 24f
        const val DRIFT_DISTANCE = 18f
        const val FULL_TURN = 360f
        const val OVERSHOOT_TENSION = 1.1f
        const val PROGRESS_DELAY = 520L
        const val TAGLINE_DELAY = 700L
        const val STAGGER_MS = 120L
    }
}
