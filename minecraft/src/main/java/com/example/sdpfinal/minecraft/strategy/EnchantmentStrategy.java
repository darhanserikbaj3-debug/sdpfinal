package com.example.sdpfinal.minecraft.strategy;

import com.example.sdpfinal.minecraft.model.*;
import java.util.*;

public interface EnchantmentStrategy {
    String key();
    int maxLevel();
    int price();
    boolean supports(Item item);

    default Quote apply(Item item, int level) {
        if (level < 1 || level > maxLevel()) {
            throw new WorkshopException("Invalid enchantment level");
        }
        if (!supports(item)) {
            throw new WorkshopException("This enchantment is incompatible with the item");
        }
        if (item.durability() == 0 && item.maxDurability() > 0) {
            throw new WorkshopException("Repair the item first");
        }
        if (item.enchantments().getOrDefault(key(), 0) >= level) {
            throw new WorkshopException(
                "The item already has this enchantment at the same or a higher level"
            );
        }
        checkConflicts(item.enchantments(), key());
        Map<String, Integer> enchants = new TreeMap<>(item.enchantments());
        enchants.put(key(), level);
        return new Quote(
            item.update(item.durability(), enchants, item.name(), item.anvilUses()),
            level * price(),
            level
        );
    }

    record Quote(Item item, int experience, int lapis) {}

    static void checkConflicts(Map<String, Integer> existing, String added) {
        if (
            (added.equals("fortune") && existing.containsKey("silk_touch")) ||
            (added.equals("silk_touch") && existing.containsKey("fortune"))
        ) {
            throw new WorkshopException("Fortune and Silk Touch are incompatible");
        }
    }
}
