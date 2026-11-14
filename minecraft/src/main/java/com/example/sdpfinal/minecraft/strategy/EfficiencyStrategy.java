package com.example.sdpfinal.minecraft.strategy;

import com.example.sdpfinal.minecraft.model.*;
import java.util.*;

public class EfficiencyStrategy implements EnchantmentStrategy {

    public String key() {
        return "efficiency";
    }

    public int maxLevel() {
        return 5;
    }

    public int price() {
        return 2;
    }

    public boolean supports(Item item) {
        return item.kind().equals("book") || item.tool();
    }
}
