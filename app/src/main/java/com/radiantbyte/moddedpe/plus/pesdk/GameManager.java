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
package com.radiantbyte.moddedpe.plus.pesdk;

import android.content.res.AssetManager;
import android.os.Bundle;

import com.google.gson.Gson;
import com.radiantbyte.moddedpe.plus.mcpelauncher.data.Constants;
import com.radiantbyte.moddedpe.plus.mcpelauncher.data.Preferences;
import com.radiantbyte.moddedpe.plus.pesdk.nmod.NModLib;
import com.radiantbyte.moddedpe.plus.pesdk.utils.AssetOverrideManager;
import com.radiantbyte.moddedpe.plus.pesdk.utils.MinecraftInfo;
import com.mojang.minecraftpe.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class GameManager {
    private final PESdk mPESdk;
    private final ArrayList<String> patchAssetPath = new ArrayList<>();

    GameManager(PESdk pesdk) {
        mPESdk = pesdk;
    }

    public AssetManager getAssets() {
        return mPESdk.getMinecraftInfo().getAssets();
    }

    public void onMinecraftActivityCreate(@NotNull MainActivity activity, Bundle savedInstanceState) {
        boolean safeMode = Preferences.isSafeMode();
        //AssetOverrideManager.addAssetOverride(activity.getAssets(), MinecraftInfo.getMinecraftPackageContext().getPackageResourcePath());

        String basePath = MinecraftInfo.getMinecraftPackageContext().getPackageResourcePath();
        patchAssetPath.add(basePath);
        /* In 1.17.30(beta version unknown), almost all assets files were moved to
         * split_install_pack.apk, including bootstrap.json, a file that is crucial to
         * launching the game.
         */
        String splitPath = basePath.replace("base.apk", "split_install_pack.apk");
        File splitFile = new File(splitPath);
        if (splitFile.exists()) {
            patchAssetPath.add(splitPath);
        }

        for (String assetPath : patchAssetPath) {
            AssetOverrideManager.addAssetOverride(activity.getAssets(), assetPath);
        }

        if (!safeMode) {
            Gson gson = new Gson();
            Bundle data = activity.getIntent().getExtras();

            Preloader.NModPreloadData preloadData = gson.fromJson(data.getString(Constants.NMOD_DATA_TAG), Preloader.NModPreloadData.class);

            for (String assetsPath : preloadData.assets_packs_path)
                AssetOverrideManager.addAssetOverride(activity.getAssets(), assetsPath);

            String[] loadedNModLibs = preloadData.loaded_libs;
            for (String nativeLibName : loadedNModLibs) {
                NModLib lib = new NModLib(nativeLibName);
                lib.callOnActivityCreate(activity, savedInstanceState);
            }
        }
    }

    public void onMinecraftActivityFinish(MainActivity activity) {
        if (Preferences.isSafeMode()) {
            return;
        }
        Gson gson = new Gson();
        Preloader.NModPreloadData preloadData = gson.fromJson(activity.getIntent().getExtras().getString(Constants.NMOD_DATA_TAG), Preloader.NModPreloadData.class);

        String[] loadedNModLibs = preloadData.loaded_libs;
        for (String nativeLibName : loadedNModLibs) {
            NModLib lib = new NModLib(nativeLibName);
            lib.callOnActivityFinish(activity);
        }
    }
}