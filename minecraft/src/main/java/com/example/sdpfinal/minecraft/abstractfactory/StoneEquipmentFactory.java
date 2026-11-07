package com.example.sdpfinal.minecraft.abstractfactory;

import com.example.sdpfinal.minecraft.model.EquipmentItem;

public class StoneEquipmentFactory implements EquipmentFactory {
    @Override
    public EquipmentItem createTool() {
        return new EquipmentItem("Stone Pickaxe", 131, "TOOL", 3);
    }

    @Override
    public EquipmentItem createArmor() {
        throw new UnsupportedOperationException("В Minecraft не существует каменной брони!");
    }
}