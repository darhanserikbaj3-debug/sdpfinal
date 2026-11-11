package com.example.sdpfinal.minecraft.factorymethod;

import com.example.sdpfinal.minecraft.model.EquipmentItem;

public class StoneToolFactory extends ToolFactory {
    @Override
    public EquipmentItem createTool(ToolType type) {
        return new EquipmentItem("Stone " + type.getDisplayName(), 131, "TOOL", type.getResourceCost());
    }
}
