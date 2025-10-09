package com.microsoft.xal.androidjava;

import android.content.Context;
import org.jetbrains.annotations.NotNull;



public class Storage {
    @NotNull
    public static String getStoragePath(@NotNull Context context) {
        return context.getFilesDir().getPath();
    }
}
