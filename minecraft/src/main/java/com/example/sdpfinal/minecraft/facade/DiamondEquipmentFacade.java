package com.example.sdpfinal.minecraft.facade;

import com.example.sdpfinal.minecraft.model.EquipmentItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DiamondEquipmentFacade {

    public List<EquipmentItem> craftFullDiamondSet() {
        List<EquipmentItem> items = new ArrayList<>();

        // Armor
        items.add(new EquipmentItem("Diamond Helmet", 1561, "ARMOR", 5));
        items.add(new EquipmentItem("Diamond Chestplate", 1561, "ARMOR", 8));
        items.add(new EquipmentItem("Diamond Leggings", 1561, "ARMOR", 7));
        items.add(new EquipmentItem("Diamond Boots", 1561, "ARMOR", 4));

        // Tools 
        items.add(new EquipmentItem("Diamond Sword", 1561, "TOOL", 2));
        items.add(new EquipmentItem("Diamond Pickaxe", 1561, "TOOL", 3));
        items.add(new EquipmentItem("Diamond Axe", 1561, "TOOL", 3));
        items.add(new EquipmentItem("Diamond Shovel", 1561, "TOOL", 1));
        items.add(new EquipmentItem("Diamond Hoe", 1561, "TOOL", 2));

        return items;
    }
}