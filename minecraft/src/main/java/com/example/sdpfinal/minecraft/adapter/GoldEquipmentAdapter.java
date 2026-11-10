package com.example.sdpfinal.minecraft.adapter;

public class GoldEquipmentAdapter implements GoldEquipmentTarget {
    private final LegacyGoldForge legacyForge;

    public GoldEquipmentAdapter(LegacyGoldForge legacyForge) {
        this.legacyForge = legacyForge;
    }

    @Override
    public GoldEquipment craftFullGoldSet() {
        String helmet = legacyForge.forgeGoldHelmet(5);
        String chestplate = legacyForge.forgeGoldChestplate(8);
        String leggings = legacyForge.forgeGoldLeggings(7);
        String boots = legacyForge.forgeGoldBoots(4);

        String axe = legacyForge.forgeGoldAxe(3);
        String sword = legacyForge.forgeGoldSword(2);
        String pickaxe = legacyForge.forgeGoldPickaxe(3);

        return new GoldEquipment(helmet, chestplate, leggings, boots, axe, sword, pickaxe);
    }
}