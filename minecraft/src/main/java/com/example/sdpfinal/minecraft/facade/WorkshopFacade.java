package com.example.sdpfinal.minecraft.facade;

import com.example.sdpfinal.minecraft.abstractfactory.*;
import com.example.sdpfinal.minecraft.adapter.*;
import com.example.sdpfinal.minecraft.builder.*;
import com.example.sdpfinal.minecraft.factorymethod.*;
import com.example.sdpfinal.minecraft.model.*;
import com.example.sdpfinal.minecraft.service.*;
import com.example.sdpfinal.minecraft.strategy.*;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class WorkshopFacade {

    private final InventoryService inventory;
    private final AnvilService anvil;
    private final BrewingSystem brewing;

    public WorkshopFacade(InventoryService inventory, AnvilService anvil, BrewingSystem brewing) {
        this.inventory = inventory;
        this.anvil = anvil;
        this.brewing = brewing;
    }

    private Operation done(
        String message,
        Map<String, Object> result,
        String pattern,
        String implementation
    ) {
        List<Operation.Step> steps = new ArrayList<>();
        steps.add(new Operation.Step("Facade", "WorkshopFacade"));
        steps.add(new Operation.Step(pattern, implementation));
        return Operation.of(message, result, inventory.snapshot(), steps);
    }

    public Operation state() {
        synchronized (inventory) {
            return Operation.of("Workshop ready", Map.of(), inventory.snapshot(), List.of());
        }
    }

    public Operation reset() {
        synchronized (inventory) {
            inventory.reset();
            return state();
        }
    }

    public Operation craftArmor(Material material, String kind) {
        synchronized (inventory) {
            ArmorFactory factory = ArmorFactory.forMaterial(material);
            List<String> kinds = new ArrayList<>();
            if ("set".equals(kind)) {
                kinds.add("helmet");
                kinds.add("chestplate");
                kinds.add("leggings");
                kinds.add("boots");
            } else {
                if (kind == null) {
                    kind = "";
                }
                kinds.add(kind);
            }

            List<Item> output = new ArrayList<>();
            int amount = 0;
            for (String slot : kinds) {
                output.add(factory.create(slot));
                amount = amount + ArmorFactory.cost(slot);
            }
            Map<String, Integer> cost = Map.of(material.key(), amount);
            inventory.commit(cost, 0, List.of(), output);
            return done(
                "Armor crafted",
                Map.of("created", output, "cost", cost),
                "Abstract Factory",
                factory.getClass().getSimpleName()
            );
        }
    }

    public Operation craftTool(Material material, String kind) {
        synchronized (inventory) {
            ToolWorkshop workshop = ToolWorkshop.forKind(kind);
            Item item = workshop.craft(material);
            Map<String, Integer> cost = Map.of(material.key(), ToolWorkshop.cost(kind), "stick", 2);
            inventory.commit(cost, 0, List.of(), List.of(item));
            return done(
                "Tool crafted",
                Map.of("created", item, "cost", cost),
                "Factory Method",
                workshop.getClass().getSimpleName()
            );
        }
    }

    public Operation enchant(String id, String key, int level, boolean preview) {
        synchronized (inventory) {
            Item item = inventory.item(id);
            EnchantingTable table = new EnchantingTable(key);
            EnchantmentStrategy.Quote quote = table.enchant(item, level);
            String message = "Enchantment cost";
            if (!preview) {
                inventory.commit(
                    Map.of("lapis", quote.lapis()),
                    quote.experience(),
                    List.of(id),
                    List.of(quote.item())
                );
                message = "Item enchanted";
            }
            return done(
                message,
                Map.of("preview", quote, "applied", !preview),
                "Strategy",
                table.implementation()
            );
        }
    }

    public Operation brew(String recipe, String modifier, boolean preview) {
        synchronized (inventory) {
            BrewingSystem.Brew quote = brewing.brew(recipe, modifier);
            String message = "Potion recipe";
            if (!preview) {
                inventory.commit(quote.cost(), 0, List.of(), List.of(quote.potion()));
                message = "Potion brewed";
            }
            return done(
                message,
                Map.of("preview", quote, "applied", !preview),
                "Adapter",
                "BrewingAdapter → LegacyBrewingSystem"
            );
        }
    }

    public Operation anvil(AnvilOrder order, boolean preview) {
        synchronized (inventory) {
            AnvilService.Quote quote = anvil.preview(order, inventory);
            String message = "Anvil preview";
            if (!preview) {
                inventory.commit(quote.cost(), quote.experience(), quote.consumed(), List.of(quote.result()));
                message = "Anvil work complete";
            }
            return done(
                message,
                Map.of("preview", quote, "applied", !preview),
                "Builder",
                "AnvilOrder.Builder → AnvilService"
            );
        }
    }

    public Operation use(String id) {
        synchronized (inventory) {
            Item item = inventory.item(id);
            if (!item.tool() && !item.armor()) {
                throw new WorkshopException("Choose a tool or armor piece");
            }
            Operation.State state = inventory.snapshot();
            if (state.health() == 0) {
                throw new WorkshopException("Restore health with a healing potion");
            }
            if (item.durability() == 0) {
                throw new WorkshopException("This item is broken. Repair it at the anvil.");
            }

            Map<String, Integer> output = Map.of();
            String message = "Armor tested";
            if (item.tool()) {
                ToolWorkshop workshop = ToolWorkshop.forKind(item.kind());
                output = workshop.harvest(item);
                message = "Resources collected";
            }

            int wear = calculateWear(item);
            int duration = calculateDuration(item, state.effects());
            int damage = calculateDamage(item, state.effects());
            Map<String, Integer> remainingEffects = new TreeMap<>();
            for (String effect : state.effects().keySet()) {
                int turns = state.effects().get(effect);
                if (turns > 1) {
                    remainingEffects.put(effect, turns - 1);
                }
            }

            int durability = Math.max(0, item.durability() - wear);
            Item updated = item.update(durability, item.enchantments(), item.name(), item.anvilUses());
            inventory.harvest(output, updated, damage, remainingEffects);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("produced", output);
            result.put("wear", Math.min(wear, item.durability()));
            result.put("duration", duration);
            result.put("damage", damage);
            return Operation.of(
                message,
                result,
                inventory.snapshot(),
                List.of(new Operation.Step("Facade", "WorkshopFacade → InventoryService"))
            );
        }
    }

    private int calculateWear(Item item) {
        int unbreaking = item.enchantments().getOrDefault("unbreaking", 0);
        return Math.max(1, (int) Math.ceil(24.0 / (unbreaking + 1)));
    }

    private int calculateDuration(Item item, Map<String, Integer> effects) {
        int efficiency = item.enchantments().getOrDefault("efficiency", 0);
        int duration = Math.max(1, 12 - efficiency * 2);
        if (effects.containsKey("swiftness")) {
            duration = Math.max(1, duration / 2);
        }
        if (effects.containsKey("swiftness_ii")) {
            duration = Math.max(1, duration / 3);
        }
        return duration;
    }

    private int calculateDamage(Item item, Map<String, Integer> effects) {
        if (effects.containsKey("fire_resistance")) {
            return 0;
        }
        if (item.armor() && item.enchantments().containsKey("protection")) {
            return 1;
        }
        return 2;
    }

    public Operation drink(String id) {
        synchronized (inventory) {
            Item item = inventory.item(id);
            Item.Potion potion = item.potion();
            if (potion == null) {
                throw new WorkshopException("This is not a potion");
            }

            Operation.State state = inventory.snapshot();
            Map<String, Integer> effects = new TreeMap<>(state.effects());
            int healing = 0;
            if (potion.effect().equals("healing")) {
                healing = 6 * potion.power();
            } else {
                String effect = potion.effect();
                if (potion.power() > 1) {
                    effect = effect + "_ii";
                }
                effects.put(effect, potion.turns());
            }
            inventory.commit(Map.of(), 0, List.of(id), List.of(), healing, effects);
            return Operation.of(
                "Potion consumed",
                Map.of("effect", potion.effect()),
                inventory.snapshot(),
                List.of(new Operation.Step("Facade", "WorkshopFacade → InventoryService"))
            );
        }
    }
}
