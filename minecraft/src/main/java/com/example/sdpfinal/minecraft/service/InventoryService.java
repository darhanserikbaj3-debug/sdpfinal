package com.example.sdpfinal.minecraft.service;

import com.example.sdpfinal.minecraft.model.*;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private Map<String, Integer> resources = new TreeMap<>();
    private Map<String, Item> items = new LinkedHashMap<>();
    private Map<String, Integer> effects = new TreeMap<>();
    private int experience;
    private int health;
    private long revision;

    public InventoryService() {
        reset();
    }

    public synchronized Operation.State snapshot() {
        // Return copies so other classes cannot change the inventory directly.
        return new Operation.State(
            Map.copyOf(resources),
            List.copyOf(items.values()),
            experience,
            health,
            Map.copyOf(effects),
            revision
        );
    }

    public synchronized Item item(String id) {
        Item item = items.get(id);
        if (item == null) {
            throw new WorkshopException("Item not found. Refresh the inventory.");
        }
        return item;
    }

    public synchronized void reset() {
        resources = new TreeMap<>();
        resources.put("iron", 64);
        resources.put("diamond", 48);
        resources.put("stick", 64);
        resources.put("lapis", 64);
        resources.put("glass_bottle", 12);
        resources.put("nether_wart", 12);
        resources.put("blaze_powder", 16);
        resources.put("sugar", 12);
        resources.put("glistering_melon", 12);
        resources.put("magma_cream", 12);
        resources.put("redstone", 16);
        resources.put("glowstone_dust", 16);
        items = new LinkedHashMap<>();
        effects = new TreeMap<>();
        experience = 100;
        health = 14;

        Item first = Item.equipment("pickaxe", Material.IRON, 250);
        first = first.update(80, Map.of("efficiency", 3), first.name(), 0);
        items.put(first.id(), first);

        Item second = Item.equipment("pickaxe", Material.IRON, 250);
        second = second.update(60, Map.of("efficiency", 3, "unbreaking", 2), second.name(), 0);
        items.put(second.id(), second);

        addStartingBook("efficiency", 3);
        addStartingBook("unbreaking", 3);
        addStartingBook("fortune", 2);
        addStartingBook("silk_touch", 1);
        addStartingBook("protection", 3);
        revision++;
    }

    private void addStartingBook(String enchantment, int level) {
        Item book = Item.book(Map.of(enchantment, level));
        items.put(book.id(), book);
    }

    public synchronized void commit(
        Map<String, Integer> cost,
        int xp,
        List<String> removed,
        List<Item> added
    ) {
        commit(cost, xp, removed, added, 0, effects);
    }

    public synchronized void commit(
        Map<String, Integer> cost,
        int xp,
        List<String> removed,
        List<Item> added,
        int healthChange,
        Map<String, Integer> nextEffects
    ) {
        if (xp < 0) {
            throw new WorkshopException("Invalid cost");
        }
        if (experience < xp) {
            throw new WorkshopException("Not enough XP: need " + xp + ", available " + experience);
        }

        // Prepare the changes first. If a check fails, the original state stays unchanged.
        Map<String, Integer> newResources = new TreeMap<>(resources);
        for (String key : cost.keySet()) {
            int amount = cost.get(key);
            if (amount <= 0) {
                throw new WorkshopException("Invalid cost");
            }
            int available = newResources.getOrDefault(key, 0);
            if (available < amount) {
                throw new WorkshopException(
                    "Not enough resource: " + resourceName(key) + " (need " + amount + ")"
                );
            }
            newResources.put(key, available - amount);
        }

        Map<String, Item> newItems = new LinkedHashMap<>(items);
        for (String id : removed) {
            if (!newItems.containsKey(id)) {
                throw new WorkshopException("Item is missing or used twice");
            }
            newItems.remove(id);
        }
        for (Item item : added) {
            if (newItems.containsKey(item.id())) {
                throw new WorkshopException("Duplicate item");
            }
            newItems.put(item.id(), item);
        }
        if (newItems.size() > 160) {
            throw new WorkshopException("Inventory full (160 items)");
        }

        resources = newResources;
        items = newItems;
        experience = experience - xp;
        health = Math.max(0, Math.min(20, health + healthChange));
        effects = new TreeMap<>(nextEffects);
        revision++;
    }

    private String resourceName(String key) {
        if (key.equals("diamond")) {
            return "diamonds";
        }
        if (key.equals("stick")) {
            return "sticks";
        }
        if (key.equals("lapis")) {
            return "lapis lazuli";
        }
        if (key.equals("glass_bottle")) {
            return "bottles";
        }
        return key.replace('_', ' ');
    }

    public synchronized void harvest(
        Map<String, Integer> output,
        Item updated,
        int damage,
        Map<String, Integer> nextEffects
    ) {
        Map<String, Integer> newResources = new TreeMap<>(resources);
        for (String key : output.keySet()) {
            long total = (long) newResources.getOrDefault(key, 0) + output.get(key);
            if (total > 1000000) {
                throw new WorkshopException("Resource storage full");
            }
            newResources.put(key, (int) total);
        }
        commit(Map.of(), 0, List.of(updated.id()), List.of(updated), -damage, nextEffects);
        resources = newResources;
    }
}
