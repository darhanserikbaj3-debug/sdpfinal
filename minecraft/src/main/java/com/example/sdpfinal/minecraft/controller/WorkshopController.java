package com.example.sdpfinal.minecraft.controller;

import com.example.sdpfinal.minecraft.builder.*;
import com.example.sdpfinal.minecraft.facade.*;
import com.example.sdpfinal.minecraft.model.*;
import com.example.sdpfinal.minecraft.strategy.*;
import java.util.*;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class WorkshopController {

    private final WorkshopFacade facade;

    public WorkshopController(WorkshopFacade facade) {
        this.facade = facade;
    }

    public record CraftRequest(Material material, String kind) {}

    public record EnchantRequest(String itemId, String enchantment, int level) {}

    public record BrewRequest(String recipe, String modifier) {}

    public record ItemRequest(String itemId) {}

    public record AnvilRequest(String itemId, String sacrificeId, Integer materialUnits, String name) {
        AnvilOrder build() {
            int units = 0;
            if (materialUnits != null) {
                units = materialUnits;
            }
            AnvilOrder.Builder builder = new AnvilOrder.Builder();
            builder.target(itemId);
            builder.material(units);
            builder.sacrifice(sacrificeId);
            builder.rename(name);
            return builder.build();
        }
    }

    @GetMapping("/state")
    public Operation state() {
        return facade.state();
    }

    @GetMapping("/catalog")
    public Map<String, Object> catalog() {
        Map<String, Integer> armor = new LinkedHashMap<>();
        armor.put("helmet", 5);
        armor.put("chestplate", 8);
        armor.put("leggings", 7);
        armor.put("boots", 4);
        armor.put("set", 24);

        Map<String, Integer> tools = new LinkedHashMap<>();
        tools.put("pickaxe", 3);
        tools.put("axe", 3);
        tools.put("shovel", 1);

        List<Map<String, Object>> enchantments = new ArrayList<>();
        String[] keys = { "efficiency", "unbreaking", "fortune", "silk_touch", "protection" };
        for (String key : keys) {
            EnchantmentStrategy strategy = EnchantingTable.strategy(key);
            Map<String, Object> option = new LinkedHashMap<>();
            option.put("id", key);
            option.put("maxLevel", strategy.maxLevel());
            option.put("price", strategy.price());
            enchantments.add(option);
        }

        List<Map<String, Object>> potions = new ArrayList<>();
        potions.add(Map.of("id", "healing", "modifiers", List.of("none", "strong")));
        potions.add(Map.of("id", "swiftness", "modifiers", List.of("none", "strong", "extended")));
        potions.add(Map.of("id", "fire_resistance", "modifiers", List.of("none", "extended")));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("materials", Material.values());
        result.put("armor", armor);
        result.put("tools", tools);
        result.put("enchantments", enchantments);
        result.put("potions", potions);
        return result;
    }

    @PostMapping("/armor")
    public Operation armor(@RequestBody CraftRequest r) {
        return facade.craftArmor(r.material(), r.kind());
    }

    @PostMapping("/tools")
    public Operation tools(@RequestBody CraftRequest r) {
        return facade.craftTool(r.material(), r.kind());
    }

    @PostMapping("/enchant/preview")
    public Operation enchantPreview(@RequestBody EnchantRequest r) {
        return facade.enchant(r.itemId(), r.enchantment(), r.level(), true);
    }

    @PostMapping("/enchant")
    public Operation enchant(@RequestBody EnchantRequest r) {
        return facade.enchant(r.itemId(), r.enchantment(), r.level(), false);
    }

    @PostMapping("/brew/preview")
    public Operation brewPreview(@RequestBody BrewRequest r) {
        return facade.brew(r.recipe(), r.modifier(), true);
    }

    @PostMapping("/brew")
    public Operation brew(@RequestBody BrewRequest r) {
        return facade.brew(r.recipe(), r.modifier(), false);
    }

    @PostMapping("/anvil/preview")
    public Operation anvilPreview(@RequestBody AnvilRequest r) {
        return facade.anvil(r.build(), true);
    }

    @PostMapping("/anvil")
    public Operation anvil(@RequestBody AnvilRequest r) {
        return facade.anvil(r.build(), false);
    }

    @PostMapping("/use")
    public Operation use(@RequestBody ItemRequest r) {
        return facade.use(r.itemId());
    }

    @PostMapping("/drink")
    public Operation drink(@RequestBody ItemRequest r) {
        return facade.drink(r.itemId());
    }

    @PostMapping("/reset")
    public Operation reset() {
        return facade.reset();
    }

    @ExceptionHandler(WorkshopException.class)
    public ResponseEntity<Operation> invalid(WorkshopException ex) {
        return ResponseEntity.badRequest().body(
            Operation.of(
                ex.getMessage(),
                Map.of("error", "VALIDATION_ERROR"),
                facade.state().state(),
                List.of()
            )
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Operation> json(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(
            Operation.of(
                "Check the request fields and use whole numbers",
                Map.of("error", "INVALID_JSON"),
                facade.state().state(),
                List.of()
            )
        );
    }
}
