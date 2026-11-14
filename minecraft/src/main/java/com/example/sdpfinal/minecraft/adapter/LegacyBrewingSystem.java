package com.example.sdpfinal.minecraft.adapter;

import java.util.*;

// This old API uses numeric codes and seconds instead of our item names and turns.
public class LegacyBrewingSystem {

    public record LegacyElixir(int effectCode, int strength, int seconds) {}

    public LegacyElixir mix(int[] reagents, int enhancement) {
        Set<Integer> codes = new HashSet<>();
        for (int code : reagents) {
            codes.add(code);
        }
        if (!codes.contains(373) || !codes.contains(372) || !codes.contains(377)) {
            throw new IllegalArgumentException("Missing base reagents");
        }

        int effect = 0;
        if (codes.contains(382)) {
            effect = 1;
        } else if (codes.contains(353)) {
            effect = 2;
        } else if (codes.contains(378)) {
            effect = 3;
        }
        if (effect == 0) {
            throw new IllegalArgumentException("Unknown reagent");
        }
        if (enhancement != 0 && enhancement != 331 && enhancement != 348) {
            throw new IllegalArgumentException("Unknown enhancement");
        }
        if (effect == 1 && enhancement == 331) {
            throw new IllegalArgumentException("Unsupported enhancement");
        }
        if (effect == 3 && enhancement == 348) {
            throw new IllegalArgumentException("Unsupported enhancement");
        }

        int strength = 1;
        if (enhancement == 348) {
            strength = 2;
        }
        int seconds = 180;
        if (effect == 1) {
            seconds = 0;
        } else if (enhancement == 331) {
            seconds = 480;
        }
        return new LegacyElixir(effect, strength, seconds);
    }
}
