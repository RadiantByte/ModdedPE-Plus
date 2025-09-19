package com.mcal.mcpelauncher.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.mcal.mcpelauncher.ui.theme.ModdedTheme
import com.mcal.mcpelauncher.ui.MainScreen

/**
 * @author <a href="https://github.com/RadiantByte">RadiantByte</a>
 */

class ComposeMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ModdedTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MainScreen()
                }
            }
        }
    }
}