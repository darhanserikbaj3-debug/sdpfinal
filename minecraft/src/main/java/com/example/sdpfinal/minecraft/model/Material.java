package com.example.sdpfinal.minecraft.model;

public enum Material {
    IRON("Iron", 250),
    DIAMOND("Diamond", 1561);

    public final String title;
    public final int toolDurability;

    Material(String title, int toolDurability) {
        this.title = title;
        this.toolDurability = toolDurability;
    }

    public String key() {
        return name().toLowerCase(java.util.Locale.ROOT);
    }
}
