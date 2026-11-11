package com.example.sdpfinal.minecraft.abstractfactory;

import com.example.sdpfinal.minecraft.model.EquipmentItem;

import java.util.List;

public interface EquipmentFactory {
    EquipmentItem createTool();
    EquipmentItem createArmor();

    default List<EquipmentItem> createTools() {
        return List.of(createTool());
    }

    default List<EquipmentItem> createArmorSet() {
        return List.of(createArmor());
    }
}