package com.example.sdpfinal.minecraft.model;

public class EquipmentItem {
    private String name;
    private int durability;
    private String type; 

    public EquipmentItem(String name, int durability, String type) {
        this.name = name;
        this.durability = durability;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getDurability() {
        return durability;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] (Durability: %d)", name, type, durability);
    }
}