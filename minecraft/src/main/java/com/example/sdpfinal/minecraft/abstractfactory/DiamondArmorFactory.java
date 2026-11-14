package com.example.sdpfinal.minecraft.abstractfactory;

import com.example.sdpfinal.minecraft.model.*;

public class DiamondArmorFactory implements ArmorFactory {

    public ArmorPiece.Helmet createHelmet() {
        return new DiamondHelmet();
    }

    public record DiamondHelmet() implements ArmorPiece.Helmet {
        public Item item() {
            return Item.equipment("helmet", Material.DIAMOND, 363);
        }
    }

    public ArmorPiece.Chestplate createChestplate() {
        return new DiamondChestplate();
    }

    public record DiamondChestplate() implements ArmorPiece.Chestplate {
        public Item item() {
            return Item.equipment("chestplate", Material.DIAMOND, 528);
        }
    }

    public ArmorPiece.Leggings createLeggings() {
        return new DiamondLeggings();
    }

    public record DiamondLeggings() implements ArmorPiece.Leggings {
        public Item item() {
            return Item.equipment("leggings", Material.DIAMOND, 495);
        }
    }

    public ArmorPiece.Boots createBoots() {
        return new DiamondBoots();
    }

    public record DiamondBoots() implements ArmorPiece.Boots {
        public Item item() {
            return Item.equipment("boots", Material.DIAMOND, 429);
        }
    }
}
