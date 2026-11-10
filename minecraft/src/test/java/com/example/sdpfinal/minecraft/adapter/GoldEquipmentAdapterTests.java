package com.example.sdpfinal.minecraft.adapter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GoldEquipmentAdapterTests {
    @Test
    void adaptsAllLegacyResultsAndRecipeCosts() {
        LegacyGoldForge forge = mock(LegacyGoldForge.class);
        when(forge.forgeGoldHelmet(5)).thenReturn("helmet");
        when(forge.forgeGoldChestplate(8)).thenReturn("chestplate");
        when(forge.forgeGoldLeggings(7)).thenReturn("leggings");
        when(forge.forgeGoldBoots(4)).thenReturn("boots");
        when(forge.forgeGoldAxe(3)).thenReturn("axe");
        when(forge.forgeGoldSword(2)).thenReturn("sword");
        when(forge.forgeGoldPickaxe(3)).thenReturn("pickaxe");
        GoldEquipmentTarget adapter = new GoldEquipmentAdapter(forge);
        GoldEquipment set = adapter.craftFullGoldSet();
        assertEquals("helmet", set.getHelmet());
        assertEquals("chestplate", set.getChestplate());
        assertEquals("leggings", set.getLeggings());
        assertEquals("boots", set.getBoots());
        assertEquals("axe", set.getAxe());
        assertEquals("sword", set.getSword());
        assertEquals("pickaxe", set.getPickaxe());
    }
}
