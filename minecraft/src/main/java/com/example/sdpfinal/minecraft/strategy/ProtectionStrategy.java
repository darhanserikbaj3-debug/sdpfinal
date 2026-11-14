package com.example.sdpfinal.minecraft.strategy;

import com.example.sdpfinal.minecraft.model.*;
import java.util.*;

public class ProtectionStrategy implements EnchantmentStrategy {

    public String key() {
        return "protection";
    }

    public int maxLevel() {
        return 4;
    }

    public int price() {
        return 2;
    }

    public boolean supports(Item item) {
        return item.kind().equals("book") || item.armor();
    }
}
