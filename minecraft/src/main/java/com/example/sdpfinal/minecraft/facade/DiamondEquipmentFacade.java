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

    private static final int DIAMONDS_FOR_ARMOR = 24;
    private static final int DIAMONDS_FOR_TOOLS = 11;
    private static final int DIAMOND_DURABILITY = 1561;

    public DiamondEquipmentFacade(InventoryService inventoryService,
                                 CraftingBenchService craftingBenchService,
                                 NotificationService notificationService) {
        this.inventoryService = inventoryService;
        this.craftingBenchService = craftingBenchService;
        this.notificationService = notificationService;
    }

    public List<EquipmentItem> craftFullDiamondSet() {
        int totalDiamondsNeeded = DIAMONDS_FOR_ARMOR + DIAMONDS_FOR_TOOLS;

        if (!inventoryService.hasEnoughDiamonds(totalDiamondsNeeded)) {
            notificationService.notifyPlayer("Not enough diamonds in inventory!");
            return new ArrayList<>();
        }

        inventoryService.deductDiamonds(totalDiamondsNeeded);
        List<EquipmentItem> fullSet = new ArrayList<>();

        System.out.println("\n=== Crafting Diamond Armor Set ===");
        fullSet.add(craftingBenchService.craft("Diamond Helmet", DIAMOND_DURABILITY, "ARMOR"));
        fullSet.add(craftingBenchService.craft("Diamond Chestplate", DIAMOND_DURABILITY, "ARMOR"));
        fullSet.add(craftingBenchService.craft("Diamond Leggings", DIAMOND_DURABILITY, "ARMOR"));
        fullSet.add(craftingBenchService.craft("Diamond Boots", DIAMOND_DURABILITY, "ARMOR"));

        System.out.println("\n=== Crafting Diamond Tool Set ===");
        fullSet.add(craftingBenchService.craft("Diamond Sword", DIAMOND_DURABILITY, "TOOL"));
        fullSet.add(craftingBenchService.craft("Diamond Pickaxe", DIAMOND_DURABILITY, "TOOL"));
        fullSet.add(craftingBenchService.craft("Diamond Axe", DIAMOND_DURABILITY, "TOOL"));
        fullSet.add(craftingBenchService.craft("Diamond Shovel", DIAMOND_DURABILITY, "TOOL"));
        fullSet.add(craftingBenchService.craft("Diamond Hoe", DIAMOND_DURABILITY, "TOOL"));

        notificationService.notifyPlayer("Full diamond set successfully crafted!");
        return fullSet;
    }
}