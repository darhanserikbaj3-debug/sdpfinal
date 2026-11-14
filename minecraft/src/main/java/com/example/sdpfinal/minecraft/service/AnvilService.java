package com.example.sdpfinal.minecraft.service;

import com.example.sdpfinal.minecraft.builder.AnvilOrder;
import com.example.sdpfinal.minecraft.model.*;
import com.example.sdpfinal.minecraft.strategy.*;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class AnvilService {

    public record Quote(Item result, Map<String, Integer> cost, int experience, List<String> consumed) {}

    public Quote preview(AnvilOrder order, InventoryService inventory) {
        Item target = inventory.item(order.itemId());
        if (!target.tool() && !target.armor() && !target.kind().equals("book")) {
            throw new WorkshopException("This item cannot be used at the anvil");
        }

        int durability = target.durability();
        int price = 0;
        int uses = target.anvilUses();
        String name = target.name();
        Map<String, Integer> cost = new TreeMap<>();
        Map<String, Integer> enchantments = new TreeMap<>(target.enchantments());
        List<String> consumed = new ArrayList<>();
        consumed.add(target.id());

        if (order.materialUnits() > 0) {
            if (target.material() == null || target.maxDurability() == 0) {
                throw new WorkshopException("This item cannot be repaired with material");
            }
            if (durability == target.maxDurability()) {
                throw new WorkshopException("The item is already fully repaired");
            }
            int repairPerUnit = Math.max(1, target.maxDurability() / 4);
            int usedMaterials = 0;
            while (usedMaterials < order.materialUnits() && durability < target.maxDurability()) {
                durability = Math.min(target.maxDurability(), durability + repairPerUnit);
                usedMaterials++;
            }
            cost.put(target.material().key(), usedMaterials);
            price = price + usedMaterials;
        }

        Item second = null;
        if (order.sacrificeId() != null) {
            second = inventory.item(order.sacrificeId());
            boolean isBook = second.kind().equals("book");
            boolean sameKind = target.kind().equals(second.kind());
            boolean sameMaterial = target.material() == second.material();
            if (!isBook && (!sameKind || !sameMaterial)) {
                throw new WorkshopException("Use a book or a matching item of the same material");
            }

            if (!isBook && target.maxDurability() > 0) {
                int bonus = (target.maxDurability() * 12) / 100;
                durability = Math.min(target.maxDurability(), durability + second.durability() + bonus);
                if (durability > target.durability()) {
                    price = price + 2;
                }
            }

            for (String enchantment : second.enchantments().keySet()) {
                EnchantmentStrategy strategy = EnchantingTable.strategy(enchantment);
                if (!strategy.supports(target)) {
                    throw new WorkshopException("The book enchantment is incompatible with the item");
                }
                EnchantmentStrategy.checkConflicts(enchantments, enchantment);
                int currentLevel = enchantments.getOrDefault(enchantment, 0);
                int incomingLevel = second.enchantments().get(enchantment);
                int newLevel;
                if (currentLevel == incomingLevel) {
                    newLevel = currentLevel + 1;
                } else {
                    newLevel = Math.max(currentLevel, incomingLevel);
                }
                newLevel = Math.min(newLevel, strategy.maxLevel());
                if (newLevel > currentLevel) {
                    enchantments.put(enchantment, newLevel);
                    price = price + newLevel;
                }
            }
            consumed.add(second.id());
            uses = Math.max(uses, second.anvilUses());
        }

        if (order.name() != null && !order.name().equals(name)) {
            name = order.name();
            price++;
        }
        if (
            durability == target.durability() &&
            enchantments.equals(target.enchantments()) &&
            name.equals(target.name())
        ) {
            throw new WorkshopException("Combining these items would change nothing");
        }

        price = price + previousWorkCost(target.anvilUses());
        if (second != null) {
            price = price + previousWorkCost(second.anvilUses());
        }
        if (price >= 40) {
            throw new WorkshopException("Too expensive: anvil cost is 40 or more");
        }

        Item result = target.update(durability, enchantments, name, Math.min(6, uses + 1));
        return new Quote(result, Map.copyOf(cost), Math.max(1, price), List.copyOf(consumed));
    }

    private int previousWorkCost(int uses) {
        int multiplier = 1;
        int count = Math.min(uses, 6);
        for (int i = 0; i < count; i++) {
            multiplier = multiplier * 2;
        }
        return multiplier - 1;
    }
}
