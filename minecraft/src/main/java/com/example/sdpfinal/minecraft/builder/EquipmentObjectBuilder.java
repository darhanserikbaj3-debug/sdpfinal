package com.example.sdpfinal.minecraft.builder;

public class EquipmentObjectBuilder implements EquipmentBuilder {
    private String helmet, chestplate, leggings, boots;
    private String sword, pickaxe, shovel;

    @Override
    public void reset() {
        helmet = chestplate = leggings = boots = sword = pickaxe = shovel = null;
    }

    @Override
    public EquipmentObjectBuilder setHelmet(String helmet) { this.helmet = helmet; return this; }
    @Override
    public EquipmentObjectBuilder setChestplate(String chestplate) { this.chestplate = chestplate; return this; }
    @Override
    public EquipmentObjectBuilder setLeggings(String leggings) { this.leggings = leggings; return this; }
    @Override
    public EquipmentObjectBuilder setBoots(String boots) { this.boots = boots; return this; }
    @Override
    public EquipmentObjectBuilder setSword(String sword) { this.sword = sword; return this; }
    @Override
    public EquipmentObjectBuilder setPickaxe(String pickaxe) { this.pickaxe = pickaxe; return this; }
    @Override
    public EquipmentObjectBuilder setShovel(String shovel) { this.shovel = shovel; return this; }

    public IronEquipment getResult() {
        if (helmet == null && chestplate == null && leggings == null && boots == null
                && sword == null && pickaxe == null && shovel == null) {
            throw new IllegalStateException("Equipment set cannot be completely empty!");
        }
        return new IronEquipment(helmet, chestplate, leggings, boots, sword, pickaxe, shovel);
    }
}
