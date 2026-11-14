package com.example.sdpfinal.minecraft.model;

import java.util.*;

public record Item(
    String id,
    String kind,
    Material material,
    String name,
    int durability,
    int maxDurability,
    Map<String, Integer> enchantments,
    int anvilUses,
    Potion potion
) {
    public record Potion(String effect, int power, int turns) {}

    public Item {
        enchantments = Map.copyOf(enchantments);
    }

    public static Item equipment(String kind, Material material, int max) {
        String adjective = material.title;
        String noun;
        switch (kind) {
            case "helmet":
                noun = "Helmet";
                break;
            case "chestplate":
                noun = "Chestplate";
                break;
            case "leggings":
                noun = "Leggings";
                break;
            case "boots":
                noun = "Boots";
                break;
            case "pickaxe":
                noun = "Pickaxe";
                break;
            case "axe":
                noun = "Axe";
                break;
            case "shovel":
                noun = "Shovel";
                break;
            default:
                throw new WorkshopException("Unknown item");
        }
        return new Item(
            UUID.randomUUID().toString(),
            kind,
            material,
            adjective + " " + noun,
            max,
            max,
            Map.of(),
            0,
            null
        );
    }

    public static Item book(Map<String, Integer> enchants) {
        return new Item(
            UUID.randomUUID().toString(),
            "book",
            null,
            "Enchanted Book",
            0,
            0,
            enchants,
            0,
            null
        );
    }

    public static Item potion(String name, String effect, int power, int turns) {
        return new Item(
            UUID.randomUUID().toString(),
            "potion",
            null,
            name,
            0,
            0,
            Map.of(),
            0,
            new Potion(effect, power, turns)
        );
    }

    public Item update(int durability, Map<String, Integer> enchants, String name, int uses) {
        return new Item(id, kind, material, name, durability, maxDurability, enchants, uses, potion);
    }

    public boolean tool() {
        return kind.equals("pickaxe") || kind.equals("axe") || kind.equals("shovel");
    }

    public boolean armor() {
        return (
            kind.equals("helmet") ||
            kind.equals("chestplate") ||
            kind.equals("leggings") ||
            kind.equals("boots")
        );
    }
}
