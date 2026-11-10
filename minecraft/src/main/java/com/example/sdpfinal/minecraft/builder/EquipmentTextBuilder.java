package com.example.sdpfinal.minecraft.builder;

import java.util.LinkedHashMap;
import java.util.Map;

public class EquipmentTextBuilder implements EquipmentBuilder {
    private final Map<String, String> items = new LinkedHashMap<>();

    @Override
    public void reset() { items.clear(); }

    private void setItem(String slot, String value) {
        if (value == null) items.remove(slot);
        else items.put(slot, value);
    }

    @Override
    public EquipmentTextBuilder setHelmet(String value) { setItem("Helmet", value); return this; }
    @Override
    public EquipmentTextBuilder setChestplate(String value) { setItem("Chestplate", value); return this; }
    @Override
    public EquipmentTextBuilder setLeggings(String value) { setItem("Leggings", value); return this; }
    @Override
    public EquipmentTextBuilder setBoots(String value) { setItem("Boots", value); return this; }
    @Override
    public EquipmentTextBuilder setSword(String value) { setItem("Sword", value); return this; }
    @Override
    public EquipmentTextBuilder setPickaxe(String value) { setItem("Pickaxe", value); return this; }
    @Override
    public EquipmentTextBuilder setShovel(String value) { setItem("Shovel", value); return this; }

    public String getResult() {
        StringBuilder text = new StringBuilder("=== IRON SET SPECIFICATION ===\n");
        items.forEach((slot, value) -> text.append(slot).append(": ").append(value).append("\n"));
        return text.toString();
    }
}
