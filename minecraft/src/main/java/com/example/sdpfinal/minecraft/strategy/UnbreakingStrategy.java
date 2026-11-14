package com.example.sdpfinal.minecraft.strategy;

import com.example.sdpfinal.minecraft.model.*;
import java.util.*;

public class UnbreakingStrategy implements EnchantmentStrategy {

    public String key() {
        return "unbreaking";
    }

    public int maxLevel() {
        return 3;
    }

    public int price() {
        return 3;
    }

    public boolean supports(Item item) {
        return item.kind().equals("book") || item.tool() || item.armor();
    }
}
