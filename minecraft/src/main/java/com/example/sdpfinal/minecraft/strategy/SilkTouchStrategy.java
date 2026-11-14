package com.example.sdpfinal.minecraft.strategy;

import com.example.sdpfinal.minecraft.model.*;
import java.util.*;

public class SilkTouchStrategy implements EnchantmentStrategy {

    public String key() {
        return "silk_touch";
    }

    public int maxLevel() {
        return 1;
    }

    public int price() {
        return 6;
    }

    public boolean supports(Item item) {
        return item.kind().equals("book") || item.kind().equals("pickaxe");
    }
}
