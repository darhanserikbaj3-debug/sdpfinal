package com.example.sdpfinal.minecraft.builder;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EquipmentBuilderTests {
    @Test
    void rejectsEmptyButAllowsPickaxeOnly() {
        EquipmentObjectBuilder builder = new EquipmentObjectBuilder();
        assertThrows(IllegalStateException.class, builder::getResult);
        assertEquals("Custom Pickaxe", builder.setPickaxe("Custom Pickaxe").getResult().getPickaxe());
    }

    @Test
    void toolsOnlyDoesNotRetainArmorFromPreviousBuild() {
        EquipmentDirector director = new EquipmentDirector();
        EquipmentObjectBuilder builder = new EquipmentObjectBuilder();
        director.makeFullIronSet(builder);
        IronEquipment full = builder.getResult();
        director.makeIronToolsOnly(builder);
        IronEquipment tools = builder.getResult();
        assertNull(tools.getHelmet());
        assertNull(tools.getChestplate());
        assertNull(tools.getLeggings());
        assertNull(tools.getBoots());
        assertEquals("Iron Sword", tools.getSword());
        assertEquals("Iron Pickaxe", tools.getPickaxe());
        assertEquals("Iron Shovel", tools.getShovel());
        assertEquals("Iron Helmet", full.getHelmet());
    }

    @Test
    void textBuilderReplacesValuesAndDirectorStartsFresh() {
        EquipmentTextBuilder builder = new EquipmentTextBuilder();
        builder.setHelmet("Old Helmet").setHelmet("New Helmet");
        assertFalse(builder.getResult().contains("Old Helmet"));
        assertTrue(builder.getResult().contains("New Helmet"));
        EquipmentDirector director = new EquipmentDirector();
        director.makeFullIronSet(builder);
        String full = builder.getResult();
        director.makeFullIronSet(builder);
        assertEquals(full, builder.getResult());
        director.makeIronToolsOnly(builder);
        assertFalse(builder.getResult().contains("Helmet"));
        assertTrue(builder.getResult().contains("Iron Pickaxe"));
    }
}
