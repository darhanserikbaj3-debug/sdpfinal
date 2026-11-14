package com.example.sdpfinal.minecraft.model;

import java.time.Instant;
import java.util.*;

public record Operation(
    String requestId,
    Instant serverTime,
    String message,
    Map<String, Object> result,
    State state,
    List<Step> steps
) {
    public record Step(String pattern, String implementation) {}

    public record State(
        Map<String, Integer> resources,
        List<Item> items,
        int experience,
        int health,
        Map<String, Integer> effects,
        long revision
    ) {}

    public static Operation of(String message, Map<String, Object> result, State state, List<Step> steps) {
        return new Operation(
            UUID.randomUUID().toString(),
            Instant.now(),
            message,
            result,
            state,
            List.copyOf(steps)
        );
    }
}
