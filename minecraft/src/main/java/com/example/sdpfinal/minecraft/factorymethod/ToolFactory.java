package com.example.sdpfinal.minecraft.factorymethod;

import com.example.sdpfinal.minecraft.model.EquipmentItem;
import java.util.List;

public abstract class ToolFactory {
    public abstract EquipmentItem createTool(ToolType type);

    public List<EquipmentItem> createToolSet() {
        return List.of(
                createTool(ToolType.SWORD),
                createTool(ToolType.AXE),
                createTool(ToolType.PICKAXE)
        );
    }
}
