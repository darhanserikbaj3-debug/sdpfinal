package com.example.sdpfinal.minecraft.factorymethod;

import com.example.sdpfinal.minecraft.model.*;

public class ShovelWorkshop extends ToolWorkshop {

    protected Tool createTool(Material material) {
        return new Tool.Shovel(material);
    }
}
