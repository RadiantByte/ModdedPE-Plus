package com.mcal.pesdk.somod;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class SoModNativeLoader {
    private static final String TAG = "SoModNativeLoader";

    private static native void nativeCallLeviEntry(String cacheDir);

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    public static void loadEnabledSoMods(SoModManager modManager, File cacheDir) {
        List<SoMod> mods = modManager.getMods();
        File dir = new File(cacheDir, "mods");
        if (!dir.exists()) dir.mkdirs();
        for (SoMod mod : mods) {
            if (!mod.isEnabled()) continue;
            File src = new File(modManager.getModsDir(), mod.getFileName());
            File dst = new File(dir, mod.getFileName());
            try {
                copyFile(src, dst);
                System.load(dst.getAbsolutePath());
                Log.i(TAG, "Loaded so: " + dst.getName());
            } catch (IOException | UnsatisfiedLinkError e) {
                Log.e(TAG, "Can't load " + src.getName() + ": " + e.getMessage());
            }
        }
        try {
            System.loadLibrary("so-mod-loader");
            nativeCallLeviEntry(cacheDir.getAbsolutePath());
        } catch (Throwable t) {
            Log.w(TAG, "native LeviMod_Load call skipped: " + t.getMessage());
        }
    }

    private static void copyFile(File src, File dst) throws IOException {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(dst);
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } finally {
            if (in != null) try { in.close(); } catch (IOException ignore) {}
            if (out != null) try { out.close(); } catch (IOException ignore) {}
        }
    }
}