package com.example.sdpfinal.minecraft.service;

import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    public boolean hasEnoughDiamonds(int required) {
        System.out.println("[InventoryService] Checking resources: Needs " + required + " diamonds.");
        return true;
    }

    public void deductDiamonds(int amount) {
        System.out.println("[InventoryService] Deducted " + amount + " diamonds from inventory.");
    }
}