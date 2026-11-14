package com.example.sdpfinal.minecraft.abstractfactory;

import static org.junit.jupiter.api.Assertions.*;

import com.example.sdpfinal.minecraft.model.*;
import java.util.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Abstract Factory: armor families")
class AbstractFactoryTests {

  @Test
  @DisplayName("Iron factory creates a complete iron armor family")
  void createsIronFamily() {
    ArmorFactory factory = new IronArmorFactory();
    List<Item> items = List.of(
      factory.createHelmet().item(),
      factory.createChestplate().item(),
      factory.createLeggings().item(),
      factory.createBoots().item()
    );
    assertEquals(
      List.of("helmet", "chestplate", "leggings", "boots"),
      items.stream().map(Item::kind).toList()
    );
    assertTrue(
      items.stream().allMatch(i -> i.material() == Material.IRON && i.armor())
    );
    assertEquals(
      List.of(165, 240, 225, 195),
      items.stream().map(Item::maxDurability).toList()
    );
  }

  @Test
  @DisplayName("Diamond factory creates its own stronger product family")
  void createsDistinctDiamondFamily() {
    ArmorFactory factory = new DiamondArmorFactory();
    List<Item> items = List.of(
      factory.createHelmet().item(),
      factory.createChestplate().item(),
      factory.createLeggings().item(),
      factory.createBoots().item()
    );
    assertTrue(
      items
        .stream()
        .allMatch(i -> i.material() == Material.DIAMOND && i.armor())
    );
    assertEquals(
      List.of(363, 528, 495, 429),
      items.stream().map(Item::maxDurability).toList()
    );
    assertNotEquals(
      new IronArmorFactory().createHelmet().getClass(),
      factory.createHelmet().getClass()
    );
  }

  @Test
  @DisplayName("Client switches families through the same factory interface")
  void switchesFamiliesWithoutChangingClient() {
    for (Material material : Material.values()) {
      ArmorFactory factory = ArmorFactory.forMaterial(material);
      for (String slot : List.of("helmet", "chestplate", "leggings", "boots")) {
        Item first = factory.create(slot),
          second = factory.create(slot);
        assertEquals(slot, first.kind());
        assertEquals(material, first.material());
        assertEquals(first.maxDurability(), first.durability());
        assertNotEquals(first.id(), second.id());
      }
    }
  }

  @Test
  @DisplayName("Invalid material or product cannot produce armor")
  void rejectsInvalidFamilyRequests() {
    assertThrows(WorkshopException.class, () -> ArmorFactory.forMaterial(null));
    assertThrows(WorkshopException.class, () ->
      new IronArmorFactory().create("pickaxe")
    );
    assertThrows(WorkshopException.class, () ->
      new DiamondArmorFactory().create("shield")
    );
  }
}
