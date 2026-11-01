package com.example.sdpfinal.minecraft.service;

import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    public boolean hasEnoughDiamonds(int required) {
        System.out.println("[InventoryService] Проверка ресурсов: требуется " + required + " алмазов.");
        return true; 
    }

    public void deductDiamonds(int amount) {
        System.out.println("[InventoryService] Успешно списано " + amount + " алмазов из инвентаря.");
    }
}