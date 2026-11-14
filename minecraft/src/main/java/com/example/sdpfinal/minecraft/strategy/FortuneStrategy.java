package com.example.sdpfinal.minecraft.strategy;

import com.example.sdpfinal.minecraft.model.*;
import java.util.*;

public class FortuneStrategy implements EnchantmentStrategy {

    public String key() {
        return "fortune";
    }

    public int maxLevel() {
        return 3;
    }

    public int price() {
        return 4;
    }

    public boolean supports(Item item) {
        return item.kind().equals("book") || Set.of("pickaxe", "shovel").contains(item.kind());
    }
}
