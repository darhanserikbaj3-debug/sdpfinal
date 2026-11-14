package com.example.sdpfinal.minecraft.factorymethod;

import static org.junit.jupiter.api.Assertions.*;

import com.example.sdpfinal.minecraft.model.*;
import java.util.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Factory Method: tool creation and behavior")
class FactoryMethodTests {

  @Test
  @DisplayName("Pickaxe creator produces a pickaxe that mines ore")
  void createsMiningPickaxe() {
    ToolWorkshop creator = new PickaxeWorkshop();
    Item item = creator.craft(Material.IRON);
    assertEquals("pickaxe", item.kind());
    assertEquals(Material.IRON, item.material());
    assertEquals(250, item.maxDurability());
    assertEquals("Mine ore", creator.action(Material.IRON));
    assertEquals(Map.of("diamond", 1, "iron", 2), creator.harvest(item));
  }

  @Test
  @DisplayName("Axe creator produces an axe that harvests wood")
  void createsWoodcuttingAxe() {
    ToolWorkshop creator = new AxeWorkshop();
    Item item = creator.craft(Material.DIAMOND);
    assertEquals("axe", item.kind());
    assertEquals(Material.DIAMOND, item.material());
    assertEquals(1561, item.durability());
    assertEquals("Chop wood", creator.action(Material.DIAMOND));
    assertEquals(Map.of("log", 4, "stick", 2), creator.harvest(item));
  }

  @Test
  @DisplayName("Shovel creator produces a shovel that digs dirt")
  void createsDiggingShovel() {
    ToolWorkshop creator = new ShovelWorkshop();
    Item item = creator.craft(Material.IRON);
    assertEquals("shovel", item.kind());
    assertEquals("Dig dirt", creator.action(Material.IRON));
    assertEquals(Map.of("dirt", 4, "flint", 1), creator.harvest(item));
    assertNotEquals(item.id(), creator.craft(Material.IRON).id());
  }

  @Test
  @DisplayName("Creator selection rejects unknown tools and missing materials")
  void validatesCreatorSelection() {
    assertInstanceOf(PickaxeWorkshop.class, ToolWorkshop.forKind("pickaxe"));
    assertInstanceOf(AxeWorkshop.class, ToolWorkshop.forKind("axe"));
    assertInstanceOf(ShovelWorkshop.class, ToolWorkshop.forKind("shovel"));
    assertThrows(WorkshopException.class, () -> ToolWorkshop.forKind("sword"));
    assertThrows(WorkshopException.class, () -> ToolWorkshop.forKind(null));
    assertThrows(WorkshopException.class, () ->
      new PickaxeWorkshop().craft(null)
    );
  }
}
