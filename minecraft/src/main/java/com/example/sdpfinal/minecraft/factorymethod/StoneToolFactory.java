package com.example.sdpfinal.minecraft.factorymethod;

import com.example.sdpfinal.minecraft.model.EquipmentItem;

public class StoneToolFactory implements ToolFactory {
    @Override
    public EquipmentItem createTool() {
        return new EquipmentItem("Stone Pickaxe", 131, "TOOL", 3);
    }
}