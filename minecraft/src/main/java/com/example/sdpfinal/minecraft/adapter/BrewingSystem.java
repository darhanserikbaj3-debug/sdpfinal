package com.example.sdpfinal.minecraft.adapter;

import com.example.sdpfinal.minecraft.model.*;
import java.util.*;

public interface BrewingSystem {
    record Brew(Item potion, Map<String, Integer> cost) {}

    Brew brew(String recipe, String modifier);
}
