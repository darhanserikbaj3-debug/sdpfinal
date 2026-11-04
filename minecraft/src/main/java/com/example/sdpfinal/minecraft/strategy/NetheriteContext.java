package com.example.sdpfinal.minecraft.strategy;

import com.example.sdpfinal.minecraft.model.EquipmentItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NetheriteContext {

    private NetheriteStrategy strategy;

    public void setStrategy(NetheriteStrategy strategy) {
        this.strategy = strategy;
    }

    public List<EquipmentItem> executeStrategy() {
        if (strategy == null) {
            throw new IllegalStateException("Strategy not set!");
        }
        System.out.println("[NetheriteStrategy] Applied bonus: " + strategy.getSpecialBonus());
        return strategy.execute();
    }
}