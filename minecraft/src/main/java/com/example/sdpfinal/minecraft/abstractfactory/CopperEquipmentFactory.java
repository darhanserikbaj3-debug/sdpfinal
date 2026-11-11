package com.example.sdpfinal.minecraft.abstractfactory;

import com.example.sdpfinal.minecraft.model.EquipmentItem;
import java.util.List;

public class CopperEquipmentFactory implements EquipmentFactory {
    @Override
    public EquipmentItem createTool() {
        return new EquipmentItem("Copper Pickaxe", 250, "TOOL", 3);
    }

    @Override
    public EquipmentItem createArmor() {
        return new EquipmentItem("Copper Chestplate", 192, "ARMOR", 8);
    }

    @Override
    public List<EquipmentItem> createTools() {
        return List.of(
                new EquipmentItem("Copper Sword", 250, "TOOL", 2),
                createTool(),
                new EquipmentItem("Copper Axe", 250, "TOOL", 3),
                new EquipmentItem("Copper Shovel", 250, "TOOL", 1),
                new EquipmentItem("Copper Hoe", 250, "TOOL", 2)
        );
    }

    @Override
    public List<EquipmentItem> createArmorSet() {
        return List.of(
                new EquipmentItem("Copper Helmet", 132, "ARMOR", 5),
                createArmor(),
                new EquipmentItem("Copper Leggings", 180, "ARMOR", 7),
                new EquipmentItem("Copper Boots", 156, "ARMOR", 4)
        );
    }
}
