package com.example.sdpfinal.minecraft.abstractfactory;

import com.example.sdpfinal.minecraft.model.*;

public class IronArmorFactory implements ArmorFactory {

    public ArmorPiece.Helmet createHelmet() {
        return new IronHelmet();
    }

    public record IronHelmet() implements ArmorPiece.Helmet {
        public Item item() {
            return Item.equipment("helmet", Material.IRON, 165);
        }
    }

    public ArmorPiece.Chestplate createChestplate() {
        return new IronChestplate();
    }

    public record IronChestplate() implements ArmorPiece.Chestplate {
        public Item item() {
            return Item.equipment("chestplate", Material.IRON, 240);
        }
    }

    public ArmorPiece.Leggings createLeggings() {
        return new IronLeggings();
    }

    public record IronLeggings() implements ArmorPiece.Leggings {
        public Item item() {
            return Item.equipment("leggings", Material.IRON, 225);
        }
    }

    public ArmorPiece.Boots createBoots() {
        return new IronBoots();
    }

    public record IronBoots() implements ArmorPiece.Boots {
        public Item item() {
            return Item.equipment("boots", Material.IRON, 195);
        }
    }
}
