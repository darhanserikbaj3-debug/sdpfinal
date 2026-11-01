package com.example.sdpfinal.minecraft.service;

import com.example.sdpfinal.minecraft.model.EquipmentItem;
import org.springframework.stereotype.Service;

@Service
public class CraftingBenchService {

    public EquipmentItem craft(String name, int durability, String type) {
        System.out.println("[CraftingBenchService] Сковано: " + name);
        return new EquipmentItem(name, durability, type);
    }
}