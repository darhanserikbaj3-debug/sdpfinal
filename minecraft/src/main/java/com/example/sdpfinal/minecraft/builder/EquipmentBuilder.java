package com.example.sdpfinal.minecraft.builder;

public interface EquipmentBuilder {
    void reset();
    EquipmentBuilder setHelmet(String helmet);
    EquipmentBuilder setChestplate(String chestplate);
    EquipmentBuilder setLeggings(String leggings);
    EquipmentBuilder setBoots(String boots);
    EquipmentBuilder setSword(String sword);
    EquipmentBuilder setPickaxe(String pickaxe);
    EquipmentBuilder setShovel(String shovel);
}
