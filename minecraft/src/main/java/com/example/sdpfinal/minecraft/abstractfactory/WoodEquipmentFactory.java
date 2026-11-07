package com.example.sdpfinal.minecraft.abstractfactory;

import com.example.sdpfinal.minecraft.model.EquipmentItem;

public class WoodEquipmentFactory implements EquipmentFactory {
    @Override
    public EquipmentItem createTool() {
        return new EquipmentItem("Wooden Pickaxe", 59, "TOOL", 3);
    }

    @Override
    public EquipmentItem createArmor() {
        throw new UnsupportedOperationException("В Minecraft не существует деревянной брони!");
    }
}