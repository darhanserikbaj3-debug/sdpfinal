package com.example.sdpfinal.minecraft.abstractfactory;

import com.example.sdpfinal.minecraft.model.Item;

public interface ArmorPiece {
    Item item();

    interface Helmet extends ArmorPiece {}

    interface Chestplate extends ArmorPiece {}

    interface Leggings extends ArmorPiece {}

    interface Boots extends ArmorPiece {}
}
