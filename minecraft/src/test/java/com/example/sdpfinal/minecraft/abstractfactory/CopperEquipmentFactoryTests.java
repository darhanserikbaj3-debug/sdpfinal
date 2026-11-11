package com.example.sdpfinal.minecraft.abstractfactory;

import com.example.sdpfinal.minecraft.controller.FactoryController;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class CopperEquipmentFactoryTests {
    @Test
    void createsMatchingArmorAndToolFamily() {
        EquipmentFactory factory = new CopperEquipmentFactory();
        assertEquals(4, factory.createArmorSet().size());
        assertEquals(5, factory.createTools().size());
        assertTrue(factory.createArmorSet().stream().allMatch(item ->
                item.getName().startsWith("Copper ") && item.getType().equals("ARMOR")));
        assertTrue(factory.createTools().stream().allMatch(item ->
                item.getName().startsWith("Copper ") && item.getType().equals("TOOL")));
    }

    @Test
    void controllerReturnsFullCopperSetAndPreservesOtherMaterials() {
        FactoryController controller = new FactoryController();
        assertEquals(9, controller.getAbstractFactorySet("copper").size());
        assertEquals("Wooden Pickaxe", controller.getAbstractFactorySet("wood").get(0).getName());
        assertEquals("Stone Pickaxe", controller.getAbstractFactorySet("stone").get(0).getName());
        assertThrows(ResponseStatusException.class, () -> controller.getAbstractFactorySet("unknown"));
    }
}
