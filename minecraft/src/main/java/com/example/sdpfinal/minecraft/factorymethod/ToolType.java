package com.example.sdpfinal.minecraft.factorymethod;

public enum ToolType {
    SWORD("Sword", 2),
    AXE("Axe", 3),
    PICKAXE("Pickaxe", 3);

    private final String displayName;
    private final int resourceCost;

    ToolType(String displayName, int resourceCost) {
        this.displayName = displayName;
        this.resourceCost = resourceCost;
    }

    public String getDisplayName() { return displayName; }
    public int getResourceCost() { return resourceCost; }
}
