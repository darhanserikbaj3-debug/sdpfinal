package com.example.sdpfinal.minecraft.factorymethod;

import com.example.sdpfinal.minecraft.model.*;

public class PickaxeWorkshop extends ToolWorkshop {

    protected Tool createTool(Material material) {
        return new Tool.Pickaxe(material);
    }
}
