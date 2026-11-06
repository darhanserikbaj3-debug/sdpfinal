package com.example.sdpfinal.minecraft.model;

public class EquipmentItem {
    private String name;
    private int durability;
    private String type;
    private int resourceCost;

    public EquipmentItem(String name, int durability, String type, int resourceCost) {
        this.name = name;
        this.durability = durability;
        this.type = type;
        this.resourceCost = resourceCost;
    }

    public String getName() { return name; }
    public int getDurability() { return durability; }
    public String getType() { return type; }
    public int getResourceCost() { return resourceCost; }
}