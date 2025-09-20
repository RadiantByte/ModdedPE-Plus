package com.mcal.mcpelauncher.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.mcal.mcpelauncher.ui.pages.FilePickerPage
import com.mcal.mcpelauncher.ui.theme.ModdedTheme
import java.io.File

/**
 * @author <a href="https://github.com/RadiantByte">RadiantByte</a>
 */

class ComposeFilePickerActivity : ComponentActivity() {
    companion object {
        private const val EXTRA_TITLE = "title"
        private const val EXTRA_INITIAL_PATH = "initial_path"
        private const val EXTRA_EXTENSIONS = "extensions"
        const val RESULT_FILE_PATH = "file_path"
        
        fun createIntent(context: Context, title: String = "Select File"): Intent {
            val intent = Intent(context, ComposeFilePickerActivity::class.java)
            intent.putExtra(EXTRA_TITLE, title)
            return intent
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Select File"
        val initialPath = intent.getStringExtra(EXTRA_INITIAL_PATH) ?: "/storage/emulated/0"
        val extensions = intent.getStringArrayExtra(EXTRA_EXTENSIONS)?.toList() 
            ?: listOf(".nmod", ".zip", ".apk", ".so")
        
        setContent {
            ModdedTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    FilePickerPage(
                        title = title,
                        initialPath = initialPath,
                        fileExtensions = extensions,
                        onFileSelected = { file ->
                            val resultIntent = Intent()
                            resultIntent.putExtra(RESULT_FILE_PATH, file.absolutePath)
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