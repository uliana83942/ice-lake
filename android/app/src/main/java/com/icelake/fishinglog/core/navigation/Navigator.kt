package com.icelake.fishinglog.core.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.icelake.fishinglog.R
import com.icelake.fishinglog.presentation.game.GameFragment
import com.icelake.fishinglog.presentation.gameover.GameOverFragment
import com.icelake.fishinglog.presentation.menu.MenuFragment
import com.icelake.fishinglog.presentation.splash.SplashFragment

class Navigator(
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) {

    fun showSplash(): Boolean {
        if (fragmentManager.isStateSaved) {
            return false
        }
        fragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(containerId, SplashFragment())
            .commit()
        return true
    }

    fun showMenu(): Boolean {
        if (fragmentManager.isStateSaved) {
            return false
        }
        fragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
            .replace(containerId, MenuFragment())
            .commit()
        return true
    }

    fun showGame(): Boolean = push(GameFragment(), TAG_GAME)

    fun showResult(catchId: String): Boolean =
        push(GameOverFragment.newInstance(catchId), TAG_RESULT)

    fun backToMenu(): Boolean {
        if (fragmentManager.isStateSaved) {
            return false
        }
        fragmentManager.popBackStack(TAG_GAME, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        return true
    }

    fun back(): Boolean {
        if (fragmentManager.isStateSaved || fragmentManager.backStackEntryCount == 0) {
            return false
        }
        fragmentManager.popBackStack()
        return true
    }

    private fun push(fragment: Fragment, tag: String): Boolean {
        if (fragmentManager.isStateSaved) {
            return false
        }
        fragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out_right
            )
            .replace(containerId, fragment, tag)
            .addToBackStack(tag)
            .commit()
        return true
    }

    private companion object {
        const val TAG_GAME = "screen_game"
        const val TAG_RESULT = "screen_result"
    }
}
