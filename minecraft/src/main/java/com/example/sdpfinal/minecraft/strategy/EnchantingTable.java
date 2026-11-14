package com.example.sdpfinal.minecraft.strategy;

import com.example.sdpfinal.minecraft.model.*;
import java.util.*;

public class EnchantingTable {

    private final EnchantmentStrategy strategy;

    public EnchantingTable(String key) {
        strategy = strategy(key);
    }

    public EnchantmentStrategy.Quote enchant(Item item, int level) {
        return strategy.apply(item, level);
    }

    public String implementation() {
        return strategy.getClass().getSimpleName();
    }

    public static EnchantmentStrategy strategy(String key) {
        if (key == null) {
            throw new WorkshopException("Choose an enchantment");
        }
        if ("efficiency".equals(key)) {
            return new EfficiencyStrategy();
        }
        if ("unbreaking".equals(key)) {
            return new UnbreakingStrategy();
        }
        if ("fortune".equals(key)) {
            return new FortuneStrategy();
        }
        if ("silk_touch".equals(key)) {
            return new SilkTouchStrategy();
        }
        if ("protection".equals(key)) {
            return new ProtectionStrategy();
        }
        throw new WorkshopException("Unknown enchantment");
    }
}
