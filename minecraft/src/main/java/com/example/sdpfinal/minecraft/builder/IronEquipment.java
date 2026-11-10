package com.example.sdpfinal.minecraft.builder;

public class IronEquipment {
    private final String helmet;
    private final String chestplate;
    private final String leggings;
    private final String boots;
    private final String sword;
    private final String pickaxe;
    private final String shovel;

    IronEquipment(String helmet, String chestplate, String leggings, String boots,
                  String sword, String pickaxe, String shovel) {
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.sword = sword;
        this.pickaxe = pickaxe;
        this.shovel = shovel;
    }

    public String getHelmet() { return helmet; }
    public String getChestplate() { return chestplate; }
    public String getLeggings() { return leggings; }
    public String getBoots() { return boots; }
    public String getSword() { return sword; }
    public String getPickaxe() { return pickaxe; }
    public String getShovel() { return shovel; }

    @Override
    public String toString() {
        return "Iron Equipment Set:\n" +
                "  Armor: [" + helmet + ", " + chestplate + ", " + leggings + ", " + boots + "]\n" +
                "  Tools: [" + sword + ", " + pickaxe + ", " + shovel + "]";
    }
}
