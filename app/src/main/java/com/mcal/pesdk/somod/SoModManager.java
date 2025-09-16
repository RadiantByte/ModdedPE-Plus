package com.mcal.pesdk.somod;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SoModManager {
    private final Context context;
    private final File modsDir;
    private final File configFile;
    private final Gson gson = new Gson();

    private Map<String, Boolean> enabledMap = new LinkedHashMap<>();
    private List<String> modOrder = new ArrayList<>();

    public SoModManager(Context context) {
        this.context = context.getApplicationContext();
        this.modsDir = new File(this.context.getFilesDir(), "mods");
        if (!modsDir.exists()) modsDir.mkdirs();
        this.configFile = new File(modsDir, "mods_config.json");
        loadConfig();
    }

    public File getModsDir() {
        return modsDir;
    }

    public synchronized List<SoMod> getMods() {
        File[] files = modsDir.listFiles((dir, name) -> name.endsWith(".so"));
        List<SoMod> mods = new ArrayList<>();
        boolean changed = false;

        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                if (!enabledMap.containsKey(fileName)) {
                    enabledMap.put(fileName, true);
                    modOrder.add(fileName);
                    changed = true;
                }
            }
        }

        List<String> toRemove = new ArrayList<>();
        for (String key : enabledMap.keySet()) {
            boolean found = false;
            if (files != null) {
                for (File file : files) {
                    if (file.getName().equals(key)) { found = true; break; }
                }
            }
            if (!found) toRemove.add(key);
        }
        for (String rm : toRemove) {
            enabledMap.remove(rm);
            modOrder.remove(rm);
            changed = true;
        }

        for (int i = 0; i < modOrder.size(); i++) {
            String fileName = modOrder.get(i);
            boolean enabled = Boolean.TRUE.equals(enabledMap.get(fileName));
            mods.add(new SoMod(fileName, enabled, i));
        }

        if (changed) saveConfig();
        return mods;
    }

    public synchronized void setEnabled(String fileName, boolean enabled) {
        if (!fileName.endsWith(".so")) fileName += ".so";
        if (enabledMap.containsKey(fileName)) {
            enabledMap.put(fileName, enabled);
            saveConfig();
        }
    }

    public synchronized void importSoFile(File source) throws IOException {
        if (source == null || !source.isFile()) throw new IOException("Invalid source file");
        if (!source.getName().endsWith(".so")) throw new IOException("Not a .so file");
        File target = new File(modsDir, source.getName());
        if (!modsDir.exists()) modsDir.mkdirs();
        if (!source.getAbsolutePath().equals(target.getAbsolutePath()))
            copyFile(source, target);
        if (!enabledMap.containsKey(target.getName())) {
            enabledMap.put(target.getName(), true);
            modOrder.add(target.getName());
        } else {
            enabledMap.put(target.getName(), true);
        }
        saveConfig();
    }

    private void copyFile(File src, File dst) throws IOException {
        java.io.FileInputStream in = null;
        java.io.FileOutputStream out = null;
        try {
            in = new java.io.FileInputStream(src);
            out = new java.io.FileOutputStream(dst);
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

    private void loadConfig() {
        enabledMap = new LinkedHashMap<>();
        modOrder = new ArrayList<>();
        if (!configFile.exists()) {
            File[] files = modsDir.listFiles((dir, name) -> name.endsWith(".so"));
            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    enabledMap.put(fileName, true);
                    modOrder.add(fileName);
                }
            }
            saveConfig();
            return;
        }
        try {
            FileReader reader = new FileReader(configFile);
            Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
            List<Map<String, Object>> configList = gson.fromJson(reader, listType);
            reader.close();
            if (configList != null) {
                for (Map<String, Object> item : configList) {
                    String name = (String) item.get("name");
                    Boolean enabled = (Boolean) item.get("enabled");
                    if (name != null && enabled != null) {
                        enabledMap.put(name, enabled);
                        modOrder.add(name);
                    }
                }
                return;
            }
        } catch (Throwable ignored) {
        }
        try {
            FileReader reader = new FileReader(configFile);
            Type mapType = new TypeToken<Map<String, Boolean>>(){}.getType();
            Map<String, Boolean> map = gson.fromJson(reader, mapType);
            reader.close();
            if (map != null) {
                enabledMap.putAll(map);
                modOrder.addAll(map.keySet());
            }
        } catch (Throwable ignored) {
        }
    }

    private void saveConfig() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (String name : modOrder) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", name);
            item.put("enabled", Boolean.TRUE.equals(enabledMap.get(name)));
            list.add(item);
        }
        try {
            FileWriter writer = new FileWriter(configFile);
            gson.toJson(list, writer);
            writer.flush();
            writer.close();
        } catch (IOException ignored) {
        }
    }
}