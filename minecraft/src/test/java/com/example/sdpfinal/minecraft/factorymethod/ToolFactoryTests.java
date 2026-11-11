package com.example.sdpfinal.minecraft.factorymethod;

import com.example.sdpfinal.minecraft.controller.FactoryController;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ToolFactoryTests {
    @Test
    void createsCompleteWoodAndStoneSetsThroughFactoryMethod() {
        FactoryController controller = new FactoryController();
        for (String material : List.of("wood", "stone")) {
            String prefix = material.equals("wood") ? "Wooden" : "Stone";
            var items = controller.getFactoryMethodTool(material);
            assertEquals(List.of(prefix + " Sword", prefix + " Axe", prefix + " Pickaxe"),
                    items.stream().map(item -> item.getName()).toList());
            assertEquals(List.of(2, 3, 3), items.stream().map(item -> item.getResourceCost()).toList());
            assertTrue(items.stream().allMatch(item -> item.getType().equals("TOOL")
                    && item.getDurability() == (material.equals("wood") ? 59 : 131)));
        }
        assertEquals(400, assertThrows(ResponseStatusException.class,
                () -> controller.getFactoryMethodTool("copper")).getStatusCode().value());
    }
}
