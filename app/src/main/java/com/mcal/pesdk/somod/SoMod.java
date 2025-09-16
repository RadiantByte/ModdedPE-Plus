package com.mcal.pesdk.somod;

public class SoMod {
    private final String fileName;
    private boolean enabled;
    private int order;

    public SoMod(String fileName, boolean enabled, int order) {
        this.fileName = fileName;
        this.enabled = enabled;
        this.order = order;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isEnabled() {
        return enabled;
    }

}