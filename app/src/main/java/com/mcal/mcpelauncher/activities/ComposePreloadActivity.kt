package com.mcal.mcpelauncher.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.mcal.mcpelauncher.ModdedPEApplication
import com.mcal.mcpelauncher.R
import com.mcal.mcpelauncher.data.Preferences
import com.mcal.mcpelauncher.ui.pages.PreloadingPage
import com.mcal.mcpelauncher.ui.theme.ModdedTheme
import com.mcal.pesdk.PreloadException
import com.mcal.pesdk.Preloader
import com.mcal.pesdk.nmod.NMod
import com.mcal.pesdk.somod.SoModManager
import com.mcal.pesdk.somod.SoModNativeLoader
import java.util.ArrayList
import java.util.Random

/**
 * @author <a href="https://github.com/RadiantByte">RadiantByte</a>
 */

class ComposePreloadActivity : ComponentActivity() {
    private val messages = mutableStateListOf<String>()
    private val tipText = mutableStateOf("")

    private companion object {
        const val MSG_START_MINECRAFT = 1
        const val MSG_WRITE_TEXT = 2
        const val MSG_ERROR = 3
        const val MSG_START_NMOD_LOADING_FAILED = 4
    }

    @SuppressLint("HandlerLeak")
    private val preloadUIHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_WRITE_TEXT -> {
                    messages.add(msg.obj as String)
                }
                MSG_START_MINECRAFT -> {
                    val intent = Intent(this@ComposePreloadActivity, MinecraftActivity::class.java)
                    intent.putExtras(msg.data)
                    startActivity(intent)
                    finish()
                }
                MSG_ERROR -> {
                    val preloadException = msg.obj as PreloadException
                    Preferences.openGameFailed = preloadException.toString()
                    val intent = Intent(this@ComposePreloadActivity, ComposeMainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                MSG_START_NMOD_LOADING_FAILED -> {
                    @Suppress("UNCHECKED_CAST")
                    val failedNMods = msg.obj as ArrayList<NMod>
                    ComposeNModLoadFailActivity.startThisActivity(this@ComposePreloadActivity, failedNMods, msg.data)
                    finish()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.hide(android.view.WindowInsets.Type.systemBars())
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        val tipsArray = resources.getStringArray(R.array.preloading_tips_text)
        tipText.value = tipsArray[Random().nextInt(tipsArray.size)]

        setContent {
            ModdedTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    PreloadingPage(
                        messages = messages,
                        tipText = tipText.value
                    )
                }
            }
        }

        PreloadThread().start()
    }

    private fun writeNewText(text: String) {
        val message = Message()
        message.obj = text
        message.what = MSG_WRITE_TEXT
        preloadUIHandler.sendMessage(message)
    }

    private inner class PreloadThread : Thread() {
        private val failedNMods = ArrayList<NMod>()

        override fun run() {
            super.run()
            try {
                val preloader = Preloader(ModdedPEApplication.mPESdk, null, object : Preloader.PreloadListener() {
                    override fun onStart() {
                        writeNewText(getString(R.string.preloading_initing))
                        if (Preferences.isSafeMode) {
                            writeNewText(getString(R.string.preloading_initing_info_safe_mode, ModdedPEApplication.mPESdk.minecraftInfo.minecraftVersionName))
                        } else {
                            writeNewText(getString(R.string.preloading_initing_info, ModdedPEApplication.mPESdk.nModAPI.versionName, ModdedPEApplication.mPESdk.minecraftInfo.minecraftVersionName))
                        }
                        try {
                            sleep(1500)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onLoadNativeLibs() {
                        writeNewText(getString(R.string.preloading_initing_loading_libs))
                    }

                    override fun onLoadCppSharedLib() {
                        writeNewText("--Loading libc++_shared.so")
                    }

                    override fun onLoadSubstrateLib() {
                        writeNewText(getString(R.string.preloading_loading_lib_substrate))
                    }

                    override fun onLoadFModLib() {
                        writeNewText(getString(R.string.preloading_loading_lib_fmod))
                    }

                    override fun onLoadMediaDecoders() {
                        writeNewText(getString(R.string.preloading_loading_lib_media_decoders))
                    }

                    override fun onLoadXHookLib() {
                        writeNewText(getString(R.string.preloading_loading_lib_xhook))
                    }

                    override fun onLoadMinecraftPELib() {
                        writeNewText(getString(R.string.preloading_loading_lib_minecraftpe))
                    }

                    override fun onLoadPESdkLib() {
                        writeNewText(getString(R.string.preloading_loading_lib_game_launcher))
                    }

                    override fun onLoadGameLauncherLib() {
                        writeNewText("--Loading launcher core")
                    }

                    override fun onFinishedLoadingNativeLibs() {
                        writeNewText(getString(R.string.preloading_initing_loading_libs_done))
                    }

                    override fun onStartLoadingAllNMods() {
                        writeNewText(getString(R.string.preloading_nmod_start_loading))
                    }

                    override fun onFinishedLoadingAllNMods() {
                        writeNewText(getString(R.string.preloading_nmod_finish_loading))

                        if (!Preferences.isSafeMode) {
                            writeNewText("Loading SoMods...")
                            try {
                                val soModManager = SoModManager(this@ComposePreloadActivity)
                                val cacheDir = this@ComposePreloadActivity.cacheDir
                                SoModNativeLoader.loadEnabledSoMods(soModManager, cacheDir)
                                writeNewText("SoMods loaded successfully")
                            } catch (e: Exception) {
                                writeNewText("SoMod loading failed: ${e.message}")
                            }
                        } else {
                            writeNewText("SoMods skipped (Safe Mode)")
                        }
                    }

                    override fun onNModLoaded(nmod: NMod) {
                        writeNewText(getString(R.string.preloading_nmod_loaded, nmod.packageName))
                    }

                    override fun onFailedLoadingNMod(nmod: NMod) {
                        writeNewText(getString(R.string.preloading_nmod_loaded_failed, nmod.packageName))
                        failedNMods.add(nmod)
                    }

                    override fun onFinish(bundle: Bundle) {
                        if (failedNMods.isEmpty()) {
                            writeNewText(getString(R.string.preloading_finished))
                            try {
                                sleep(1500)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                            val message = Message()
                            message.what = MSG_START_MINECRAFT
                            message.data = bundle
                            preloadUIHandler.sendMessage(message)
                        } else {
                            val message = Message()
                            message.what = MSG_START_NMOD_LOADING_FAILED
                            message.obj = failedNMods
                            message.data = bundle
                            preloadUIHandler.sendMessage(message)
                        }
                    }
                })
                preloader.preload(this@ComposePreloadActivity)
            } catch (e: PreloadException) {
                val message = Message()
                message.what = MSG_ERROR
                message.obj = e
                preloadUIHandler.sendMessage(message)
            }
        }
    }
}