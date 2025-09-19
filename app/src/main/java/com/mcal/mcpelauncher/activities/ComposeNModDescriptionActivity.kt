package com.mcal.mcpelauncher.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.mcal.mcpelauncher.ModdedPEApplication
import com.mcal.mcpelauncher.ui.pages.NModDescriptionPage
import com.mcal.mcpelauncher.ui.theme.ModdedTheme

/**
 * @author <a href="https://github.com/RadiantByte">RadiantByte</a>
 */

class ComposeNModDescriptionActivity : ComponentActivity() {
    companion object {
        private const val TAG_PACKAGE_NAME = "package_name"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val packageName = intent.getStringExtra(TAG_PACKAGE_NAME)
        if (packageName == null) {
            finish()
            return
        }

        val allNMods = ModdedPEApplication.getMPESdk().nModAPI.importedEnabledNMods + 
                      ModdedPEApplication.getMPESdk().nModAPI.importedDisabledNMods
        val nmod = allNMods.find { it.packageName == packageName }
        if (nmod == null) {
            finish()
            return
        }
        
        setContent {
            ModdedTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    NModDescriptionPage(
                        nmod = nmod,
                        onClose = {
                            finish()
                        }
                    )
                }
            }
        }
    }
}