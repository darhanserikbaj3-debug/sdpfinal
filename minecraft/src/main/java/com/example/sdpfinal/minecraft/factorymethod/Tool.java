package com.example.sdpfinal.minecraft.factorymethod;

import com.example.sdpfinal.minecraft.model.*;
import java.util.*;

public interface Tool {
    Item item();
    String action();
    Map<String, Integer> harvest(Item item);

    record Pickaxe(Material material) implements Tool {
        public Item item() {
            return Item.equipment("pickaxe", material, material.toolDurability);
        }

        public String action() {
            return "Mine ore";
        }

        public Map<String, Integer> harvest(Item item) {
            if (item.enchantments().containsKey("silk_touch")) {
                return Map.of("diamond_ore", 1);
            }
            return Map.of("diamond", 1 + item.enchantments().getOrDefault("fortune", 0), "iron", 2);
        }
    }

    record Axe(Material material) implements Tool {
        public Item item() {
            return Item.equipment("axe", material, material.toolDurability);
        }

        public String action() {
            return "Chop wood";
        }

        public Map<String, Integer> harvest(Item item) {
            return Map.of("log", 4, "stick", 2);
        }
    }

    record Shovel(Material material) implements Tool {
        public Item item() {
            return Item.equipment("shovel", material, material.toolDurability);
        }

        public String action() {
            return "Dig dirt";
        }

        public Map<String, Integer> harvest(Item item) {
            return Map.of("dirt", 4, "flint", 1 + item.enchantments().getOrDefault("fortune", 0));
        }
    }
}
