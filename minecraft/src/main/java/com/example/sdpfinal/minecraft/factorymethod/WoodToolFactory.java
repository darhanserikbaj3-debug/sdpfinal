package com.example.sdpfinal.minecraft.factorymethod;

import com.example.sdpfinal.minecraft.model.EquipmentItem;

public class WoodToolFactory implements ToolFactory {
    @Override
    public EquipmentItem createTool() {
        return new EquipmentItem("Wooden Pickaxe", 59, "TOOL", 3);
    }
}