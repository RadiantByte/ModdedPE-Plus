package com.mcal.mcpelauncher.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.mcal.mcpelauncher.ui.pages.SplashPage
import com.mcal.mcpelauncher.ui.theme.ModdedTheme
import com.mcal.mcpelauncher.utils.DataPreloader
import com.mcal.mcpelauncher.utils.PreloadingFinishedListener

/**
 * @author <a href="https://github.com/RadiantByte">RadiantByte</a>
 */

class ComposeSplashActivity : ComponentActivity(), PreloadingFinishedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            ModdedTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    SplashPage(
                        onPreloadingFinished = {
                            onPreloadingFinished()
                        }
                    )
                }
            }
        }
        
        initInstance()
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
                windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
            }
        }
    }

    private fun initInstance() {
        DataPreloader(this).preload(applicationContext)
    }

    override fun onPreloadingFinished() {
        val intent = Intent(this@ComposeSplashActivity, ComposeMainActivity::class.java)
        startActivity(intent)
        finish()
    }
}