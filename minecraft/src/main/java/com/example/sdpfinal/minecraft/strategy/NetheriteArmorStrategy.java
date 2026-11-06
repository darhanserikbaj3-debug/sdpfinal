package com.example.sdpfinal.minecraft.strategy;

import com.example.sdpfinal.minecraft.model.EquipmentItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("netheriteArmorStrategy")
public class NetheriteArmorStrategy implements NetheriteStrategy {

    @Override
    public List<EquipmentItem> execute() {
        List<EquipmentItem> armor = new ArrayList<>();
        armor.add(new EquipmentItem("Netherite Helmet", 2031, "ARMOR", 5));
        armor.add(new EquipmentItem("Netherite Chestplate", 2031, "ARMOR", 8));
        armor.add(new EquipmentItem("Netherite Leggings", 2031, "ARMOR", 7));
        armor.add(new EquipmentItem("Netherite Boots", 2031, "ARMOR", 4));
        return armor;
    }

    @Override
    public String getSpecialBonus() {
        return "Knockback Resistance + Fire Resistance";
    }
}