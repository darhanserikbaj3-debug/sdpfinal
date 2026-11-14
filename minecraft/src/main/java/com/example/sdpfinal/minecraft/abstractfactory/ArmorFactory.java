package com.example.sdpfinal.minecraft.abstractfactory;

import com.example.sdpfinal.minecraft.model.*;
import java.util.*;

public interface ArmorFactory {
    ArmorPiece.Helmet createHelmet();
    ArmorPiece.Chestplate createChestplate();
    ArmorPiece.Leggings createLeggings();
    ArmorPiece.Boots createBoots();

    default Item create(String kind) {
        if ("helmet".equals(kind)) {
            return createHelmet().item();
        }
        if ("chestplate".equals(kind)) {
            return createChestplate().item();
        }
        if ("leggings".equals(kind)) {
            return createLeggings().item();
        }
        if ("boots".equals(kind)) {
            return createBoots().item();
        }
        throw new WorkshopException("Unknown armor piece");
    }

    static ArmorFactory forMaterial(Material material) {
        if (material == null) {
            throw new WorkshopException("Choose a material");
        }
        if (material == Material.IRON) {
            return new IronArmorFactory();
        }
        return new DiamondArmorFactory();
    }

    static int cost(String kind) {
        if ("helmet".equals(kind)) {
            return 5;
        }
        if ("chestplate".equals(kind)) {
            return 8;
        }
        if ("leggings".equals(kind)) {
            return 7;
        }
        if ("boots".equals(kind)) {
            return 4;
        }
        throw new WorkshopException("Unknown armor piece");
    }
}
