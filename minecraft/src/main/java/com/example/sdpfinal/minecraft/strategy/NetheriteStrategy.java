package com.example.sdpfinal.minecraft.strategy;

import com.example.sdpfinal.minecraft.model.EquipmentItem;
import java.util.List;

public interface NetheriteStrategy {
    List<EquipmentItem> execute();
    String getSpecialBonus();
}