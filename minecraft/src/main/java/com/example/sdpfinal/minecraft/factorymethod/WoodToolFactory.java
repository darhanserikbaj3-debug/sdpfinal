package com.example.sdpfinal.minecraft.factorymethod;

import com.example.sdpfinal.minecraft.model.EquipmentItem;

public class WoodToolFactory extends ToolFactory {
    @Override
    public EquipmentItem createTool(ToolType type) {
        return new EquipmentItem("Wooden " + type.getDisplayName(), 59, "TOOL", type.getResourceCost());
    }
}
