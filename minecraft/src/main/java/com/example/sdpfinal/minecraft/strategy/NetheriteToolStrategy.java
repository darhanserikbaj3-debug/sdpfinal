package com.example.sdpfinal.minecraft.strategy;

import com.example.sdpfinal.minecraft.model.EquipmentItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("netheriteToolStrategy")
public class NetheriteToolStrategy implements NetheriteStrategy {

    @Override
    public List<EquipmentItem> execute() {
        List<EquipmentItem> tools = new ArrayList<>();
        tools.add(new EquipmentItem("Netherite Sword", 2031, "TOOL"));
        tools.add(new EquipmentItem("Netherite Pickaxe", 2031, "TOOL"));
        tools.add(new EquipmentItem("Netherite Axe", 2031, "TOOL"));
        tools.add(new EquipmentItem("Netherite Shovel", 2031, "TOOL"));
        tools.add(new EquipmentItem("Netherite Hoe", 2031, "TOOL"));
        return tools;
    }

    @Override
    public String getSpecialBonus() {
        return "Increased Attack Damage + Mining Speed Boost";
    }
}