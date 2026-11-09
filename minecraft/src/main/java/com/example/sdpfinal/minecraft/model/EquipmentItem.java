package com.example.sdpfinal.minecraft.model;

public class EquipmentItem {
    private String name;
    private int durability;
    private String type;
    private int resourceCost;
    private String enchantment; 

    public EquipmentItem(String name, int durability, String type, int resourceCost, String enchantment) {
        this.name = name;
        this.durability = durability;
        this.type = type;
        this.resourceCost = resourceCost;
        this.enchantment = enchantment;
    }

    public EquipmentItem(String name, int durability, String type, int resourceCost) {
        this(name, durability, type, resourceCost, getRandomEnchantment(type));
    }

    private static String getRandomEnchantment(String type) {
        String[] armorEnchants = {"Protection IV", "Unbreaking III", "Respiration III", "Thorns II"};
        String[] toolEnchants = {"Efficiency V", "Unbreaking III", "Sharpness V", "Fortune III", "Silk Touch"};
        
        String[] pool = "ARMOR".equals(type) ? armorEnchants : toolEnchants;
        return pool[(int) (Math.random() * pool.length)];
    }

    public String getName() { return name; }
    public int getDurability() { return durability; }
    public String getType() { return type; }
    public int getResourceCost() { return resourceCost; }
    public String getEnchantment() { return enchantment; }
}