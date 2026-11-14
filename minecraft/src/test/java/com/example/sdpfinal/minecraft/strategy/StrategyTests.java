package com.example.sdpfinal.minecraft.strategy;

import static org.junit.jupiter.api.Assertions.*;

import com.example.sdpfinal.minecraft.abstractfactory.*;
import com.example.sdpfinal.minecraft.factorymethod.*;
import com.example.sdpfinal.minecraft.model.*;
import java.util.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Strategy: interchangeable enchantment rules")
class StrategyTests {

  @Test
  @DisplayName("Efficiency enchants a tool without mutating the original")
  void efficiencyPreservesOriginalAndQuotesCost() {
    Item tool = new PickaxeWorkshop().craft(Material.IRON);
    var quote = new EnchantingTable("efficiency").enchant(tool, 3);
    assertEquals(Map.of("efficiency", 3), quote.item().enchantments());
    assertTrue(tool.enchantments().isEmpty());
    assertEquals(tool.id(), quote.item().id());
    assertEquals(tool.durability(), quote.item().durability());
    assertEquals(6, quote.experience());
    assertEquals(3, quote.lapis());
  }

  @Test
  @DisplayName("Alternative strategies apply their own costs and compatibility")
  void alternativesHaveDifferentRules() {
    Item tool = new PickaxeWorkshop().craft(Material.DIAMOND);
    var fortune = new EnchantingTable("fortune").enchant(tool, 2);
    var unbreaking = new EnchantingTable("unbreaking").enchant(tool, 2);
    assertEquals(8, fortune.experience());
    assertEquals(6, unbreaking.experience());
    assertEquals(Map.of("fortune", 2), fortune.item().enchantments());
    assertEquals(Map.of("unbreaking", 2), unbreaking.item().enchantments());
    Item helmet = new IronArmorFactory().createHelmet().item();
    assertEquals(
      Map.of("protection", 4),
      new EnchantingTable("protection").enchant(helmet, 4).item().enchantments()
    );
    assertThrows(WorkshopException.class, () ->
      new EnchantingTable("efficiency").enchant(helmet, 1)
    );
  }

  @Test
  @DisplayName("Fortune and Silk Touch reject each other in either order")
  void rejectsConflictingStrategies() {
    Item tool = new PickaxeWorkshop().craft(Material.IRON);
    Item fortune = new EnchantingTable("fortune").enchant(tool, 1).item();
    Item silk = new EnchantingTable("silk_touch").enchant(tool, 1).item();
    assertThrows(WorkshopException.class, () ->
      new EnchantingTable("silk_touch").enchant(fortune, 1)
    );
    assertThrows(WorkshopException.class, () ->
      new EnchantingTable("fortune").enchant(silk, 1)
    );
    assertEquals(Map.of("fortune", 1), fortune.enchantments());
    assertEquals(Map.of("silk_touch", 1), silk.enchantments());
  }

  @Test
  @DisplayName("Invalid levels, downgrades and broken targets are rejected")
  void validatesLevelsAndTargetCondition() {
    Item tool = new PickaxeWorkshop().craft(Material.IRON);
    var table = new EnchantingTable("efficiency");
    assertThrows(WorkshopException.class, () -> table.enchant(tool, 0));
    assertThrows(WorkshopException.class, () -> table.enchant(tool, 6));
    Item enchanted = table.enchant(tool, 3).item();
    assertThrows(WorkshopException.class, () -> table.enchant(enchanted, 3));
    assertThrows(WorkshopException.class, () -> table.enchant(enchanted, 2));
    Item broken = tool.update(0, Map.of(), tool.name(), 0);
    assertThrows(WorkshopException.class, () -> table.enchant(broken, 1));
    assertThrows(WorkshopException.class, () -> new EnchantingTable("unknown"));
  }
}
