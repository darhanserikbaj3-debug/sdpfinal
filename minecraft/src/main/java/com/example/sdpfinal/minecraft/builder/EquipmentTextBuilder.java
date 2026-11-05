package com.example.sdpfinal.minecraft.builder;

public class EquipmentTextBuilder implements EquipmentBuilder {
    private final StringBuilder sb = new StringBuilder("=== IRON SET SPECIFICATION ===\n");

    @Override
    public EquipmentTextBuilder setHelmet(String helmet) { if(helmet != null) sb.append("Helmet:     ").append(helmet).append("\n"); return this; }
    @Override
    public EquipmentTextBuilder setChestplate(String chestplate) { if(chestplate != null) sb.append("Chestplate: ").append(chestplate).append("\n"); return this; }
    @Override
    public EquipmentTextBuilder setLeggings(String leggings) { if(leggings != null) sb.append("Leggings:   ").append(leggings).append("\n"); return this; }
    @Override
    public EquipmentTextBuilder setBoots(String boots) { if(boots != null) sb.append("Boots:      ").append(boots).append("\n"); return this; }
    @Override
    public EquipmentTextBuilder setSword(String sword) { if(sword != null) sb.append("Sword:      ").append(sword).append("\n"); return this; }
    @Override
    public EquipmentTextBuilder setPickaxe(String pickaxe) { if(pickaxe != null) sb.append("Pickaxe:    ").append(pickaxe).append("\n"); return this; }
    @Override
    public EquipmentTextBuilder setShovel(String shovel) { if(shovel != null) sb.append("Shovel:     ").append(shovel).append("\n"); return this; }

    public String getResult() {
        return sb.toString();
    }
}
