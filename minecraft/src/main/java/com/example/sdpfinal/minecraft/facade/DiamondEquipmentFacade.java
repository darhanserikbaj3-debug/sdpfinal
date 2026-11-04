package com.example.sdpfinal.minecraft.facade;

import com.example.sdpfinal.minecraft.model.EquipmentItem;
import com.example.sdpfinal.minecraft.service.CraftingBenchService;
import com.example.sdpfinal.minecraft.service.InventoryService;
import com.example.sdpfinal.minecraft.service.NotificationService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DiamondEquipmentFacade {

    private final InventoryService inventoryService;
    private final CraftingBenchService craftingBenchService;
    private final NotificationService notificationService;

    public DiamondEquipmentFacade(InventoryService inventoryService,
                                 CraftingBenchService craftingBenchService,
                                 NotificationService notificationService) {
        this.inventoryService = inventoryService;
        this.craftingBenchService = craftingBenchService;
        this.notificationService = notificationService;
    }

    public List<EquipmentItem> craftFullDiamondSet() {
        inventoryService.hasEnoughDiamonds(35);
        inventoryService.deductDiamonds(35);

        List<EquipmentItem> items = new ArrayList<>();

        // Armor
        items.add(craftingBenchService.craft("Diamond Helmet", 1561, "ARMOR"));
        items.add(craftingBenchService.craft("Diamond Chestplate", 1561, "ARMOR"));
        items.add(craftingBenchService.craft("Diamond Leggings", 1561, "ARMOR"));
        items.add(craftingBenchService.craft("Diamond Boots", 1561, "ARMOR"));

        // Tools
        items.add(craftingBenchService.craft("Diamond Sword", 1561, "TOOL"));
        items.add(craftingBenchService.craft("Diamond Pickaxe", 1561, "TOOL"));
        items.add(craftingBenchService.craft("Diamond Axe", 1561, "TOOL"));
        items.add(craftingBenchService.craft("Diamond Shovel", 1561, "TOOL"));
        items.add(craftingBenchService.craft("Diamond Hoe", 1561, "TOOL"));

        notificationService.notifyPlayer("Full Diamond equipment set successfully crafted!");
        return items;
    }
}