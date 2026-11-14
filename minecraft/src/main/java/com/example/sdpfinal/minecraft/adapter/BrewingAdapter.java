package com.example.sdpfinal.minecraft.adapter;

import com.example.sdpfinal.minecraft.model.*;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class BrewingAdapter implements BrewingSystem {

    private final LegacyBrewingSystem legacy = new LegacyBrewingSystem();

    public Brew brew(String recipe, String modifier) {
        if (recipe == null || modifier == null) {
            throw new WorkshopException("Choose a potion and modifier");
        }
        int reagentCode = getReagentCode(recipe);
        int modifierCode = getModifierCode(modifier);
        int[] reagents = { 373, 372, 377, reagentCode };

        LegacyBrewingSystem.LegacyElixir elixir;
        try {
            elixir = legacy.mix(reagents, modifierCode);
        } catch (IllegalArgumentException error) {
            throw new WorkshopException("This modifier cannot be used with the potion");
        }

        String effect;
        String name;
        if (elixir.effectCode() == 1) {
            effect = "healing";
            name = "Potion of Healing";
        } else if (elixir.effectCode() == 2) {
            effect = "swiftness";
            name = "Potion of Swiftness";
        } else if (elixir.effectCode() == 3) {
            effect = "fire_resistance";
            name = "Potion of Fire Resistance";
        } else {
            throw new WorkshopException("Unknown brewing effect");
        }
        if (elixir.strength() > 1) {
            name = name + " II";
        }
        if (modifier.equals("extended")) {
            name = name + " (Extended)";
        }

        Map<String, Integer> cost = new TreeMap<>();
        cost.put("glass_bottle", 1);
        cost.put("nether_wart", 1);
        cost.put("blaze_powder", 1);
        if (recipe.equals("healing")) cost.put("glistering_melon", 1);
        if (recipe.equals("swiftness")) cost.put("sugar", 1);
        if (recipe.equals("fire_resistance")) cost.put("magma_cream", 1);
        if (modifier.equals("extended")) cost.put("redstone", 1);
        if (modifier.equals("strong")) cost.put("glowstone_dust", 1);

        int turns = elixir.seconds() / 45;
        Item potion = Item.potion(name, effect, elixir.strength(), turns);
        return new Brew(potion, Map.copyOf(cost));
    }

    private int getReagentCode(String recipe) {
        if (recipe.equals("healing")) {
            return 382;
        }
        if (recipe.equals("swiftness")) {
            return 353;
        }
        if (recipe.equals("fire_resistance")) {
            return 378;
        }
        throw new WorkshopException("Unknown potion");
    }

    private int getModifierCode(String modifier) {
        if (modifier.equals("none")) {
            return 0;
        }
        if (modifier.equals("extended")) {
            return 331;
        }
        if (modifier.equals("strong")) {
            return 348;
        }
        throw new WorkshopException("Unknown modifier");
    }
}
