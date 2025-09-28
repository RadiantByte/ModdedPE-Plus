package com.radiantbyte.moddedpe.plus.mcpelauncher;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.google.android.material.color.DynamicColors;
import com.radiantbyte.moddedpe.plus.mcpelauncher.data.Preferences;
import com.radiantbyte.moddedpe.plus.pesdk.PESdk;

public class ModdedPEApplication extends Application {
    public static Context context;
    public static SharedPreferences preferences;
    public static PESdk mPESdk;

    public static PESdk getMPESdk() {
        return mPESdk;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        mPESdk = new PESdk(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (Preferences.isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        DynamicColors.applyToActivitiesIfAvailable(this);
    }

    public static Context getContext() {
        if (context == null) {
            context = new ModdedPEApplication();
        }
        return context;
    }

    public AssetManager getAssets() {
        return mPESdk.getMinecraftInfo().getAssets();
    }
}
