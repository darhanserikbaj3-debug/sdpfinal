package com.example.sdpfinal.minecraft.adapter;

public class GoldEquipment {
    private final String helmet;
    private final String chestplate;
    private final String leggings;
    private final String boots;
    private final String axe;
    private final String sword;
    private final String pickaxe;

    public GoldEquipment(String helmet, String chestplate, String leggings, String boots,
                         String axe, String sword, String pickaxe) {
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.axe = axe;
        this.sword = sword;
        this.pickaxe = pickaxe;
    }

    public String getHelmet() { return helmet; }
    public String getChestplate() { return chestplate; }
    public String getLeggings() { return leggings; }
    public String getBoots() { return boots; }
    public String getAxe() { return axe; }
    public String getSword() { return sword; }
    public String getPickaxe() { return pickaxe; }

    @Override
    public String toString() {
        return "GoldEquipment {\n" +
                "  Armor: [" + helmet + ", " + chestplate + ", " + leggings + ", " + boots + "]\n" +
                "  Tools: [" + axe + ", " + sword + ", " + pickaxe + "]\n" +
                "}";
    }
}
