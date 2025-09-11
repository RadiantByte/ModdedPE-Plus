/*
 * Copyright (C) 2018-2021 Тимашков Иван
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.mcal.pesdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.mcal.mcpelauncher.data.Constants;
import com.mcal.mcpelauncher.data.Preferences;
import com.mcal.pesdk.nativeapi.LibraryLoader;
import com.mcal.pesdk.nmod.LoadFailedException;
import com.mcal.pesdk.nmod.NMod;
import com.mcal.pesdk.nmod.NModJSONEditor;
import com.mcal.pesdk.nmod.NModLib;
import com.mcal.pesdk.nmod.NModTextEditor;
import com.mcal.pesdk.utils.MinecraftInfo;
import com.mcal.pesdk.utils.SplitParser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;

import org.conscrypt.Conscrypt;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class Preloader {
    private final PESdk mPESdk;
    private Bundle mBundle;
    private PreloadListener mPreloadListener;
    private NModPreloadData mPreloadData = new NModPreloadData();
    private ArrayList<String> mAssetsArrayList = new ArrayList<>();
    private ArrayList<String> mLoadedNativeLibs = new ArrayList<>();
    private ArrayList<NMod> mLoadedEnabledNMods = new ArrayList<>();

    public Preloader(PESdk pesdk, Bundle bundle, PreloadListener listener) {
        mBundle = bundle;
        mPreloadListener = listener;
        mPESdk = pesdk;
        if (mPreloadListener == null)
            mPreloadListener = new PreloadListener();
    }

    public Preloader(PESdk pesdk, Bundle bundle) {
        this(pesdk, bundle, null);
    }

    public void preload(Context context) throws PreloadException {
        mPreloadListener.onStart();

        if (mBundle == null)
            mBundle = new Bundle();
        Gson gson = new Gson();
        boolean safeMode = Preferences.isSafeMode();

        try {
            Log.i("Preloader", "Starting native library preloading process");

            Log.d("Preloader", "Extracting Minecraft libraries...");
            new SplitParser(context).parseMinecraft();

            mPreloadListener.onLoadNativeLibs();

            String nativeLibDir = MinecraftInfo.getMinecraftPackageNativeLibraryDir();
            Log.d("Preloader", "Native library directory: " + nativeLibDir);

            mPreloadListener.onLoadCppSharedLib();
            Log.d("Preloader", "Loading libc++_shared.so...");
            LibraryLoader.loadCppShared(nativeLibDir);

            mPreloadListener.onLoadFModLib();
            Log.d("Preloader", "Loading libfmod.so...");
            LibraryLoader.loadFMod(nativeLibDir);

            mPreloadListener.onLoadMediaDecoders();
            Log.d("Preloader", "Loading libMediaDecoders_Android.so...");
            LibraryLoader.loadMediaDecoders(nativeLibDir);

            Log.d("Preloader", "Loading libpairipcore.so...");
            LibraryLoader.loadPairipCore(nativeLibDir);

            Log.d("Preloader", "Initializing Conscrypt security provider...");
            try {
                Security.insertProviderAt(Conscrypt.newProvider(), 1);
                Log.d("Preloader", "Conscrypt provider initialized successfully");
            } catch (Exception e) {
                Log.w("Preloader", "Failed to initialize Conscrypt provider: " + e.getMessage());
            }

            Log.d("Preloader", "Loading libmaesdk.so...");
            LibraryLoader.loadMaeSdk(nativeLibDir);

            mPreloadListener.onLoadMinecraftPELib();
            Log.d("Preloader", "Loading libminecraftpe.so...");
            LibraryLoader.loadMinecraftPE(nativeLibDir);

            mPreloadListener.onLoadGameLauncherLib();
            Log.d("Preloader", "Loading launcher core...");
            LibraryLoader.loadLauncher(nativeLibDir);

            if (!safeMode) {
                mPreloadListener.onLoadSubstrateLib();
                Log.d("Preloader", "Loading substrate...");
                LibraryLoader.loadSubstrate();

                mPreloadListener.onLoadXHookLib();
                Log.d("Preloader", "Loading xhook...");
                LibraryLoader.loadXHook();

                mPreloadListener.onLoadPESdkLib();
                Log.d("Preloader", "Loading NMod API...");
                LibraryLoader.loadNModAPI(nativeLibDir);
            } else {
                Log.i("Preloader", "Safe mode enabled - skipping advanced libraries");
            }

            mPreloadListener.onFinishedLoadingNativeLibs();
            Log.i("Preloader", "Native library preloading completed successfully");
        } catch (Throwable throwable) {
            Log.e("Preloader", "Failed to load native libraries", throwable);

            String errorDetails = getDetailedErrorInfo(context, throwable);
            Log.e("Preloader", "Error details: " + errorDetails);

            throw new PreloadException(PreloadException.TYPE_LOAD_LIBS_FAILED, throwable);
        }

        if (!safeMode) {
            mPreloadListener.onStartLoadingAllNMods();
            //init data
            mPreloadData = new NModPreloadData();
            mAssetsArrayList = new ArrayList<>();
            mLoadedNativeLibs = new ArrayList<>();
            mLoadedEnabledNMods = new ArrayList<>();

            mAssetsArrayList.add(MinecraftInfo.getMinecraftPackageContext().getPackageResourcePath());

            //init index
            ArrayList<NMod> unIndexedNModArrayList = mPESdk.getNModAPI().getImportedEnabledNMods();
            for (int index = unIndexedNModArrayList.size() - 1; index >= 0; --index) {
                mLoadedEnabledNMods.add(unIndexedNModArrayList.get(index));
            }

            //start init nmods
            for (NMod nmod : mLoadedEnabledNMods) {
                if (nmod.isBugPack()) {
                    mPreloadListener.onFailedLoadingNMod(nmod);
                    continue;
                }

                NMod.NModPreloadBean preloadDataItem;
                try {
                    preloadDataItem = nmod.copyNModFiles();
                } catch (IOException ioe) {
                    nmod.setBugPack(new LoadFailedException(LoadFailedException.TYPE_IO_FAILED, ioe));
                    mPreloadListener.onFailedLoadingNMod(nmod);
                    continue;
                }

                if (loadNMod(context, nmod, preloadDataItem))
                    mPreloadListener.onNModLoaded(nmod);
                else
                    mPreloadListener.onFailedLoadingNMod(nmod);
            }

            mPreloadData.assets_packs_path = mAssetsArrayList.toArray(new String[0]);
            mPreloadData.loaded_libs = mLoadedNativeLibs.toArray(new String[0]);
            mBundle.putString(Constants.NMOD_DATA_TAG, gson.toJson(mPreloadData));
            mPreloadListener.onFinishedLoadingAllNMods();
        } else
            mBundle.putString(Constants.NMOD_DATA_TAG, gson.toJson(new Preloader.NModPreloadData()));

        mPreloadListener.onFinish(mBundle);
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    private boolean loadNMod(Context context, @NotNull NMod nmod, NMod.NModPreloadBean preloadDataItem) {
        MinecraftInfo minecraftInfo = mPESdk.getMinecraftInfo();

        String jsonEditFile = null;
        String textEditFile = null;

        //edit json files
        if (nmod.getInfo().json_edit != null && nmod.getInfo().json_edit.length > 0) {
            ArrayList<File> assetFiles = new ArrayList<>();
            for (String filePath : mAssetsArrayList)
                assetFiles.add(new File(filePath));
            NModJSONEditor jsonEditor = new NModJSONEditor(context, nmod, assetFiles.toArray(new File[0]));
            try {
                File outResourceFile = jsonEditor.edit();
                jsonEditFile = outResourceFile.getAbsolutePath();
            } catch (IOException e) {
                if (e instanceof FileNotFoundException)
                    nmod.setBugPack(new LoadFailedException(LoadFailedException.TYPE_FILE_NOT_FOUND, e));
                else
                    nmod.setBugPack(new LoadFailedException(LoadFailedException.TYPE_IO_FAILED, e));
                return false;
            } catch (JSONException jsonE) {
                nmod.setBugPack(new LoadFailedException(LoadFailedException.TYPE_JSON_SYNTAX, jsonE));
                return false;
            }
        }
        //edit text files
        if (nmod.getInfo().text_edit != null && nmod.getInfo().text_edit.length > 0) {
            ArrayList<File> assetFiles = new ArrayList<>();
            for (String filePath : mAssetsArrayList)
                assetFiles.add(new File(filePath));
            NModTextEditor textEditor = new NModTextEditor(context, nmod, assetFiles.toArray(new File[0]));
            try {
                File outResourceFile = textEditor.edit();
                textEditFile = outResourceFile.getAbsolutePath();
            } catch (IOException e) {
                if (e instanceof FileNotFoundException)
                    nmod.setBugPack(new LoadFailedException(LoadFailedException.TYPE_FILE_NOT_FOUND, e));
                else
                    nmod.setBugPack(new LoadFailedException(LoadFailedException.TYPE_IO_FAILED, e));
                return false;
            }
        }

        if (preloadDataItem.assets_path != null)
            mAssetsArrayList.add(preloadDataItem.assets_path);

        if (jsonEditFile != null)
            mAssetsArrayList.add(jsonEditFile);
        if (textEditFile != null)
            mAssetsArrayList.add(textEditFile);

        //load elf files
        if (preloadDataItem.native_libs != null && preloadDataItem.native_libs.length > 0) {
            for (NMod.NModLibInfo nameItem : preloadDataItem.native_libs) {
                try {
                    System.load(nameItem.name);
                } catch (Throwable t) {
                    nmod.setBugPack(new LoadFailedException(LoadFailedException.TYPE_LOAD_LIB_FAILED, t));
                    return false;
                }
            }

            for (NMod.NModLibInfo nameItem : preloadDataItem.native_libs) {
                if (nameItem.use_api) {
                    NModLib lib = new NModLib(nameItem.name);
                    lib.callOnLoad(minecraftInfo.getMinecraftVersionName(), mPESdk.getNModAPI().getVersionName());
                    mLoadedNativeLibs.add(nameItem.name);
                }
            }
        }
        return true;
    }

    private String getDetailedErrorInfo(Context context, Throwable throwable) {
        StringBuilder info = new StringBuilder();
        info.append("Error: ").append(throwable.getMessage()).append("\n");
        info.append("Device ABI: ").append(com.mcal.pesdk.utils.ABIInfo.getABI()).append("\n\n");

        info.append("Minecraft Installation Info:\n");
        info.append(com.mcal.pesdk.utils.MinecraftPackageHelper.INSTANCE.getMinecraftInstallationInfo(context));
        info.append("\n");

        try {
            String nativeDir = MinecraftInfo.getMinecraftPackageNativeLibraryDir();
            File nativeDirFile = new File(nativeDir);
            info.append("Native Library Directory:\n");
            info.append("- Path: ").append(nativeDir).append("\n");
            info.append("- Exists: ").append(nativeDirFile.exists()).append("\n");
            if (nativeDirFile.exists()) {
                File[] files = nativeDirFile.listFiles();
                info.append("- File count: ").append(files != null ? files.length : 0).append("\n");
                if (files != null && files.length > 0) {
                    info.append("- Files: ");
                    for (File file : files) {
                        info.append(file.getName()).append(" (").append(file.length()).append(" bytes), ");
                    }
                    info.append("\n");
                }
            }
        } catch (Exception e) {
            info.append("- Error checking native dir: ").append(e.getMessage()).append("\n");
        }

        info.append("\nSuggested fixes:\n");
        for (String suggestion : com.mcal.pesdk.utils.MinecraftPackageHelper.INSTANCE.suggestFixes(context)) {
            info.append("- ").append(suggestion).append("\n");
        }

        return info.toString();
    }

    static class NModPreloadData {
        String[] assets_packs_path;
        String[] loaded_libs;
    }

    public static class PreloadListener {
        public static String TAG = "PreloadListener";

        public void onStart() {
            Log.e(TAG, "onStart()");
        }

        public void onLoadNativeLibs() {
            Log.e(TAG, "onLoadNativeLibs()");
        }

        public void onLoadSubstrateLib() {
            Log.e(TAG, "onLoadSubstrateLib()");
        }

        public void onLoadXHookLib() {
            Log.e(TAG, "onLoadXHookLib()");
        }

        public void onLoadGameLauncherLib() {
            Log.e(TAG, "onLoadGameLauncherLib()");
        }

        public void onLoadFModLib() {
            Log.e(TAG, "onLoadFModLib()");
        }

        public void onLoadMediaDecoders() {
            Log.e(TAG, "onLoadMediaDecoders()");
        }

        public void onLoadMinecraftPELib() {
            Log.e(TAG, "onLoadMinecraftPELib()");
        }

        public void onLoadCppSharedLib() {
            Log.e(TAG, "onLoadCppSharedLib()");
        }

        public void onLoadPESdkLib() {
            Log.e(TAG, "onLoadPESdkLib()");
        }

        public void onFinishedLoadingNativeLibs() {
            Log.e(TAG, "onFinishedLoadingNativeLibs()");
        }

        public void onStartLoadingAllNMods() {
            Log.e(TAG, "onStartLoadingAllNMods()");
        }

        public void onNModLoaded(NMod nmod) {
            Log.e(TAG, "onNModLoaded()");
        }

        public void onFailedLoadingNMod(NMod nmod) {
            Log.e(TAG, "onFailedLoadingNMod()");
        }

        public void onFinishedLoadingAllNMods() {
            Log.e(TAG, "onFinishedLoadingAllNMods()");
        }

        public void onFinish(Bundle bundle) {
            Log.e(TAG, "onFinish()");
        }
    }
}
