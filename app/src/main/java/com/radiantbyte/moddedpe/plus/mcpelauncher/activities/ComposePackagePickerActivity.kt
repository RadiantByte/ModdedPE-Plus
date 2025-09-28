package com.radiantbyte.moddedpe.plus.mcpelauncher.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.radiantbyte.moddedpe.plus.mcpelauncher.ui.pages.PackagePickerPage
import com.radiantbyte.moddedpe.plus.mcpelauncher.ui.theme.ModdedTheme

/**
 * @author <a href="https://github.com/RadiantByte">RadiantByte</a>
 */

class ComposePackagePickerActivity : ComponentActivity() {
    companion object {
        private const val EXTRA_TITLE = "title"
        const val RESULT_PACKAGE_NAME = "package_name"
        
        fun createIntent(context: Context, title: String = "Select Package"): Intent {
            val intent = Intent(context, ComposePackagePickerActivity::class.java)
            intent.putExtra(EXTRA_TITLE, title)
            return intent
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Select Package"
        
        setContent {
            ModdedTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    PackagePickerPage(
                        title = title,
                        onPackageSelected = { packageName ->
                            val resultIntent = Intent()
                            resultIntent.putExtra(RESULT_PACKAGE_NAME, packageName)
                            setResult(RESULT_OK, resultIntent)
                            finish()
                        },
                        onCancel = {
                            setResult(RESULT_CANCELED)
                            finish()
                        }
                    )
                }
            }
        }
    }
}