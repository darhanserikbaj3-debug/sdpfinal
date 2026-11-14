package com.example.sdpfinal.minecraft.facade;

import static org.junit.jupiter.api.Assertions.*;

import com.example.sdpfinal.minecraft.adapter.*;
import com.example.sdpfinal.minecraft.builder.*;
import com.example.sdpfinal.minecraft.model.*;
import com.example.sdpfinal.minecraft.service.*;
import com.example.sdpfinal.minecraft.strategy.*;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Facade: coordinated workshop operations")
class FacadeTests {

  private InventoryService inventory;
  private WorkshopFacade facade;

  @BeforeEach
  void setup() {
    inventory = new InventoryService();
    facade = new WorkshopFacade(
      inventory,
      new AnvilService(),
      new BrewingAdapter()
    );
  }

  @Test
  @DisplayName(
    "Crafting coordinates both factories and exact resource consumption"
  )
  void coordinatesArmorAndToolCrafting() {
    assertEquals(100, facade.state().state().experience());
    facade.craftArmor(Material.IRON, "set");
    facade.craftTool(Material.DIAMOND, "axe");
    var state = facade.state().state();
    assertEquals(40, state.resources().get("iron"));
    assertEquals(45, state.resources().get("diamond"));
    assertEquals(62, state.resources().get("stick"));
    assertEquals(4, state.items().stream().filter(Item::armor).count());
    assertEquals(
      1,
      state
        .items()
        .stream()
        .filter(i -> i.kind().equals("axe"))
        .count()
    );
    assertEquals(100, state.experience());
  }

  @Test
  @DisplayName(
    "Enchanting and brewing previews are read-only; apply charges once"
  )
  void coordinatesPreviewAndApply() {
    Item target = inventory
      .snapshot()
      .items()
      .stream()
      .filter(Item::tool)
      .findFirst()
      .orElseThrow();
    var before = inventory.snapshot();
    facade.enchant(target.id(), "unbreaking", 3, true);
    facade.brew("healing", "strong", true);
    assertEquals(before, inventory.snapshot());
    facade.enchant(target.id(), "unbreaking", 3, false);
    facade.brew("healing", "strong", false);
    var state = inventory.snapshot();
    assertEquals(91, state.experience());
    assertEquals(61, state.resources().get("lapis"));
    assertEquals(11, state.resources().get("glass_bottle"));
    assertEquals(15, state.resources().get("glowstone_dust"));
    assertEquals(
      3,
      inventory.item(target.id()).enchantments().get("unbreaking")
    );
    assertEquals(
      1,
      state
        .items()
        .stream()
        .filter(i -> i.potion() != null)
        .count()
    );
  }

  @Test
  @DisplayName(
    "Anvil workflow previews then atomically consumes and replaces items"
  )
  void coordinatesAnvilCombination() {
    List<Item> picks = inventory
      .snapshot()
      .items()
      .stream()
      .filter(Item::tool)
      .toList();
    Item target = picks.get(0),
      donor = picks.get(1);
    var order = new AnvilOrder.Builder()
      .target(target.id())
      .sacrifice(donor.id())
      .rename("Combined Pickaxe")
      .build();
    var before = inventory.snapshot();
    var operation = facade.anvil(order, true);
    var quote = (AnvilService.Quote) operation.result().get("preview");
    assertEquals(before, inventory.snapshot());
    var applied = facade.anvil(order, false);
    Item result = inventory.item(target.id());
    assertEquals(170, result.durability());
    assertEquals(4, result.enchantments().get("efficiency"));
    assertEquals(2, result.enchantments().get("unbreaking"));
    assertEquals("Combined Pickaxe", result.name());
    assertThrows(WorkshopException.class, () -> inventory.item(donor.id()));
    assertEquals(
      before.experience() - quote.experience(),
      applied.state().experience()
    );
    assertEquals(before.items().size() - 1, applied.state().items().size());
  }

  @Test
  @DisplayName("Insufficient XP never partially consumes an anvil order")
  void failedOperationLeavesAllStateUnchanged() {
    Item target = inventory
      .snapshot()
      .items()
      .stream()
      .filter(Item::tool)
      .findFirst()
      .orElseThrow();
    inventory.commit(Map.of(), 100, List.of(), List.of());
    var before = inventory.snapshot();
    var order = new AnvilOrder.Builder()
      .target(target.id())
      .material(2)
      .rename("Repaired Pickaxe")
      .build();
    assertThrows(WorkshopException.class, () -> facade.anvil(order, false));
    assertEquals(before, inventory.snapshot());
    assertEquals(target, inventory.item(target.id()));
  }
}
