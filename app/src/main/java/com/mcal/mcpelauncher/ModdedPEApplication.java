package com.mcal.mcpelauncher;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.google.android.material.color.DynamicColors;
import com.mcal.mcpelauncher.data.Preferences;
import com.mcal.pesdk.PESdk;

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

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        if (!preferences.contains("night_mode")) {
            preferences.edit().putBoolean("night_mode", true).apply();
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