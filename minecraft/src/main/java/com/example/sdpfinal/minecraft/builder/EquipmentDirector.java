package com.example.sdpfinal.minecraft.builder;

public class EquipmentDirector {

    public void makeFullIronSet(EquipmentBuilder b) {
        b.reset();
        b.setHelmet("Iron Helmet")
         .setChestplate("Iron Chestplate")
         .setLeggings("Iron Leggings")
         .setBoots("Iron Boots")
         .setSword("Iron Sword")
         .setPickaxe("Iron Pickaxe")
         .setShovel("Iron Shovel");
    }

    public void makeIronToolsOnly(EquipmentBuilder b) {
        b.reset();
        b.setSword("Iron Sword")
         .setPickaxe("Iron Pickaxe")
         .setShovel("Iron Shovel");
    }
}
