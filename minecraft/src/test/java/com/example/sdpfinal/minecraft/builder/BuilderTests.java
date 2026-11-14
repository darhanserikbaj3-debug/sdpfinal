package com.example.sdpfinal.minecraft.builder;

import static org.junit.jupiter.api.Assertions.*;

import com.example.sdpfinal.minecraft.model.*;
import java.util.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Builder: step-by-step anvil orders")
class BuilderTests {

  @Test
  @DisplayName("Builds a repair order with material and optional name")
  void buildsMaterialRepairOrder() {
    AnvilOrder order = new AnvilOrder.Builder()
      .target("pickaxe-id")
      .material(3)
      .rename("  Trusty Pickaxe  ")
      .build();
    assertEquals("pickaxe-id", order.itemId());
    assertEquals(3, order.materialUnits());
    assertNull(order.sacrificeId());
    assertEquals("Trusty Pickaxe", order.name());
  }

  @Test
  @DisplayName("Builds an order for combining an item or enchanted book")
  void buildsCombinationOrder() {
    AnvilOrder order = new AnvilOrder.Builder()
      .target("pickaxe-id")
      .sacrifice("book-id")
      .build();
    assertEquals("pickaxe-id", order.itemId());
    assertEquals("book-id", order.sacrificeId());
    assertEquals(0, order.materialUnits());
    assertNull(order.name());
  }

  @Test
  @DisplayName("Rename-only orders are immutable when the builder is reused")
  void builtOrdersDoNotChangeWithBuilder() {
    var builder = new AnvilOrder.Builder()
      .target("pickaxe-id")
      .sacrifice(" ")
      .rename("First name");
    AnvilOrder first = builder.build();
    AnvilOrder second = builder.rename("Second name").build();
    assertEquals("First name", first.name());
    assertEquals("Second name", second.name());
    assertNull(first.sacrificeId());
    assertEquals(0, first.materialUnits());
    assertEquals(first.itemId(), second.itemId());
  }

  @Test
  @DisplayName("Rejects incomplete, conflicting and malformed orders")
  void rejectsInvalidOrders() {
    assertThrows(WorkshopException.class, () ->
      new AnvilOrder.Builder().build()
    );
    assertThrows(WorkshopException.class, () ->
      new AnvilOrder.Builder().target(" ")
    );
    assertThrows(WorkshopException.class, () ->
      new AnvilOrder.Builder().target("x").build()
    );
    assertThrows(WorkshopException.class, () ->
      new AnvilOrder.Builder().target("x").material(1).sacrifice("y").build()
    );
    assertThrows(WorkshopException.class, () ->
      new AnvilOrder.Builder().target("x").sacrifice("x").build()
    );
    assertThrows(WorkshopException.class, () ->
      new AnvilOrder.Builder().material(-1)
    );
    assertThrows(WorkshopException.class, () ->
      new AnvilOrder.Builder().material(65)
    );
    assertThrows(WorkshopException.class, () ->
      new AnvilOrder.Builder().rename(" ")
    );
    assertThrows(WorkshopException.class, () ->
      new AnvilOrder.Builder().rename("x".repeat(51))
    );
    assertThrows(WorkshopException.class, () ->
      new AnvilOrder.Builder().rename("bad\nname")
    );
  }
}
