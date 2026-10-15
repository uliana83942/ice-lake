package com.icelake.fishinglog

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.icelake.fishinglog.core.di.ServiceLocator
import com.icelake.fishinglog.core.navigation.Navigator
import com.icelake.fishinglog.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var navigatorRef: Navigator? = null

    val navigator: Navigator
        get() {
            val existing = navigatorRef
            if (existing != null) {
                return existing
            }
            val created = Navigator(supportFragmentManager, R.id.fragment_container)
            navigatorRef = created
            return created
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ServiceLocator.init(applicationContext)
        val inflated = ActivityMainBinding.inflate(layoutInflater)
        binding = inflated
        setContentView(inflated.root)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!navigator.back()) {
                    finish()
                }
            }
        })
        if (savedInstanceState == null) {
            navigator.showSplash()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        navigatorRef = null
        binding = null
    }
}
