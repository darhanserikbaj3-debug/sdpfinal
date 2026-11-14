package com.example.sdpfinal.minecraft.adapter;

import static org.junit.jupiter.api.Assertions.*;

import com.example.sdpfinal.minecraft.model.*;
import java.util.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Adapter: legacy brewing integration")
class AdapterTests {

  private final BrewingSystem brewing = new BrewingAdapter();

  @Test
  @DisplayName(
    "Healing translates legacy reagents into a domain potion and recipe"
  )
  void translatesHealingRecipe() {
    var brew = brewing.brew("healing", "none");
    assertEquals("potion", brew.potion().kind());
    assertEquals("Potion of Healing", brew.potion().name());
    assertEquals(new Item.Potion("healing", 1, 0), brew.potion().potion());
    assertEquals(
      Map.of(
        "glass_bottle",
        1,
        "nether_wart",
        1,
        "blaze_powder",
        1,
        "glistering_melon",
        1
      ),
      brew.cost()
    );
  }

  @Test
  @DisplayName("Glowstone maps to a stronger healing effect")
  void translatesStrongModifier() {
    var brew = brewing.brew("healing", "strong");
    assertEquals(2, brew.potion().potion().power());
    assertEquals("Potion of Healing II", brew.potion().name());
    assertEquals(1, brew.cost().get("glowstone_dust"));
    assertFalse(brew.cost().containsKey("redstone"));
  }

  @Test
  @DisplayName("Legacy seconds convert into action turns for timed potions")
  void translatesTimedEffects() {
    var speed = brewing.brew("swiftness", "extended");
    assertEquals("swiftness", speed.potion().potion().effect());
    assertEquals(10, speed.potion().potion().turns());
    assertEquals(1, speed.cost().get("sugar"));
    assertEquals(1, speed.cost().get("redstone"));
    var fire = brewing.brew("fire_resistance", "none");
    assertEquals("fire_resistance", fire.potion().potion().effect());
    assertEquals(4, fire.potion().potion().turns());
    assertEquals(1, fire.cost().get("magma_cream"));
  }

  @Test
  @DisplayName("Unsupported legacy combinations become application exceptions")
  void convertsErrorsAtTheBoundary() {
    assertThrows(WorkshopException.class, () ->
      brewing.brew("healing", "extended")
    );
    assertThrows(WorkshopException.class, () ->
      brewing.brew("fire_resistance", "strong")
    );
    assertThrows(WorkshopException.class, () -> brewing.brew("poison", "none"));
    assertThrows(WorkshopException.class, () ->
      brewing.brew("healing", "unknown")
    );
    assertThrows(WorkshopException.class, () -> brewing.brew(null, "none"));
  }
}
