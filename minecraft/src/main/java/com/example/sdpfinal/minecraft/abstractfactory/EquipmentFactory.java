package com.example.sdpfinal.minecraft.abstractfactory;

import com.example.sdpfinal.minecraft.model.EquipmentItem;

public interface EquipmentFactory {
    EquipmentItem createTool();
    EquipmentItem createArmor();
}