package com.example.sdpfinal.minecraft.factorymethod;

import com.example.sdpfinal.minecraft.model.EquipmentItem;

public class CopperToolFactory implements ToolFactory {
    @Override
    public EquipmentItem createTool() {
        return new EquipmentItem("Copper Pickaxe", 250, "TOOL", 3);
    }
}