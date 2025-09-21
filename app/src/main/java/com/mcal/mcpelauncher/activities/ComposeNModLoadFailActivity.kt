package com.mcal.mcpelauncher.activities

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.mcal.mcpelauncher.ui.pages.NModLoadFailPage
import com.mcal.mcpelauncher.ui.theme.ModdedTheme
import com.mcal.pesdk.nmod.NMod

/**
 * @author <a href="https://github.com/RadiantByte">RadiantByte</a>
 */

class ComposeNModLoadFailActivity : ComponentActivity() {
    companion object {
        private const val KEY_FAILED_NMODS = "failed_nmods"
        private const val KEY_MINECRAFT_BUNDLE = "minecraft_bundle"

        fun startThisActivity(context: Context, failedNMods: ArrayList<NMod>, minecraftBundle: Bundle) {
            val intent = Intent(context, ComposeNModLoadFailActivity::class.java)
            intent.putExtra(KEY_FAILED_NMODS, failedNMods)
            intent.putExtra(KEY_MINECRAFT_BUNDLE, minecraftBundle)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val failedNMods = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            @Suppress("UNCHECKED_CAST")
            intent.getSerializableExtra(KEY_FAILED_NMODS, ArrayList::class.java) as? ArrayList<NMod> ?: arrayListOf()
        } else {
            @Suppress("DEPRECATION", "UNCHECKED_CAST")
            intent.getSerializableExtra(KEY_FAILED_NMODS) as? ArrayList<NMod> ?: arrayListOf()
        }
        val minecraftBundle = intent.getBundleExtra(KEY_MINECRAFT_BUNDLE) ?: Bundle()

        setContent {
            ModdedTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    NModLoadFailPage(
                        failedNMods = failedNMods,
                        minecraftBundle = minecraftBundle,
                        onNavigateToMain = {
                            val intent = Intent(this@ComposeNModLoadFailActivity, ComposeMainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        },
                        onNavigateToMinecraft = { bundle ->
                            val intent = Intent(this@ComposeNModLoadFailActivity, MinecraftActivity::class.java)
                            intent.putExtras(bundle)
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }
}