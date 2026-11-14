package com.example.sdpfinal.minecraft;

import static org.junit.jupiter.api.Assertions.*;

import com.example.sdpfinal.minecraft.abstractfactory.*;
import com.example.sdpfinal.minecraft.adapter.*;
import com.example.sdpfinal.minecraft.builder.*;
import com.example.sdpfinal.minecraft.facade.*;
import com.example.sdpfinal.minecraft.factorymethod.*;
import com.example.sdpfinal.minecraft.model.*;
import com.example.sdpfinal.minecraft.service.*;
import com.example.sdpfinal.minecraft.strategy.*;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;

class WorkshopTests {

  InventoryService inventory;
  WorkshopFacade facade;
  AnvilService anvil;

  @BeforeEach
  void setup() {
    inventory = new InventoryService();
    anvil = new AnvilService();
    facade = new WorkshopFacade(inventory, anvil, new BrewingAdapter());
  }

  Item target() {
    return inventory
      .snapshot()
      .items()
      .stream()
      .filter(i -> i.kind().equals("pickaxe"))
      .findFirst()
      .orElseThrow();
  }

  Item book(String key) {
    return inventory
      .snapshot()
      .items()
      .stream()
      .filter(i -> i.kind().equals("book") && i.enchantments().containsKey(key))
      .findFirst()
      .orElseThrow();
  }

  AnvilOrder materialOrder(Item item, int units) {
    return new AnvilOrder.Builder().target(item.id()).material(units).build();
  }

  @Test
  void armorFactoryCreatesFourCompatibleProducts() {
    for (Material material : Material.values()) {
      var f = ArmorFactory.forMaterial(material);
      var pieces = List.of(
        f.createHelmet().item(),
        f.createChestplate().item(),
        f.createLeggings().item(),
        f.createBoots().item()
      );
      assertEquals(4, pieces.stream().map(Item::kind).distinct().count());
      assertTrue(pieces.stream().allMatch(i -> i.material() == material));
    }
    assertTrue(
      new DiamondArmorFactory().createHelmet().item().maxDurability() >
        new IronArmorFactory().createHelmet().item().maxDurability()
    );
  }

  @Test
  void armorSetCosts24AndAddsFourInstances() {
    var result = facade.craftArmor(Material.IRON, "set");
    assertEquals(40, result.state().resources().get("iron"));
    assertEquals(
      4,
      result.state().items().stream().filter(Item::armor).count()
    );
    assertTrue(
      result
        .steps()
        .stream()
        .anyMatch(s -> s.pattern().equals("Abstract Factory"))
    );
  }

  @Test
  void toolProductsHaveDistinctActionsAndHarvests() {
    var pick = new PickaxeWorkshop();
    var axe = new AxeWorkshop();
    var shovel = new ShovelWorkshop();
    assertEquals(
      Map.of("diamond", 1, "iron", 2),
      pick.harvest(pick.craft(Material.IRON))
    );
    assertEquals(
      Map.of("log", 4, "stick", 2),
      axe.harvest(axe.craft(Material.IRON))
    );
    assertEquals(
      Map.of("dirt", 4, "flint", 1),
      shovel.harvest(shovel.craft(Material.IRON))
    );
  }

  @Test
  void insufficientResourcesNeverConsumeAnything() {
    inventory.commit(Map.of("diamond", 48), 0, List.of(), List.of());
    var before = inventory.snapshot();
    assertThrows(WorkshopException.class, () ->
      facade.craftArmor(Material.DIAMOND, "set")
    );
    assertEquals(before, inventory.snapshot());
  }

  @Test
  void strategyEnchantsAndChargesExactlyOnce() {
    var item = target();
    var before = inventory.snapshot();
    var preview = facade.enchant(item.id(), "unbreaking", 3, true);
    assertEquals(before, inventory.snapshot());
    assertEquals(
      9,
      ((EnchantmentStrategy.Quote) preview.result().get("preview")).experience()
    );
    facade.enchant(item.id(), "unbreaking", 3, false);
    assertEquals(91, inventory.snapshot().experience());
    assertEquals(61, inventory.snapshot().resources().get("lapis"));
    assertEquals(3, inventory.item(item.id()).enchantments().get("unbreaking"));
  }

  @Test
  void incompatibleEnchantAndConflictsAreAtomic() {
    facade.craftArmor(Material.IRON, "helmet");
    var armor = inventory
      .snapshot()
      .items()
      .stream()
      .filter(Item::armor)
      .findFirst()
      .orElseThrow();
    var before = inventory.snapshot();
    assertThrows(WorkshopException.class, () ->
      facade.enchant(armor.id(), "efficiency", 1, false)
    );
    assertEquals(before, inventory.snapshot());
    var targetId = target().id();
    facade.enchant(targetId, "fortune", 1, false);
    before = inventory.snapshot();
    assertThrows(WorkshopException.class, () ->
      facade.enchant(targetId, "silk_touch", 1, false)
    );
    assertEquals(before, inventory.snapshot());
  }

  @Test
  void enchantLevelsCannotBeDowngradedOrExceeded() {
    var before = inventory.snapshot();
    assertThrows(WorkshopException.class, () ->
      facade.enchant(target().id(), "efficiency", 3, false)
    );
    assertThrows(WorkshopException.class, () ->
      facade.enchant(target().id(), "efficiency", 6, false)
    );
    assertEquals(before, inventory.snapshot());
  }

  @Test
  void adapterConvertsReagentsEffectsAndDuration() {
    var adapter = new BrewingAdapter();
    var healing = adapter.brew("healing", "strong");
    assertEquals("healing", healing.potion().potion().effect());
    assertEquals(2, healing.potion().potion().power());
    assertEquals(1, healing.cost().get("glowstone_dust"));
    var speed = adapter.brew("swiftness", "extended");
    assertEquals(10, speed.potion().potion().turns());
    assertEquals(1, speed.cost().get("redstone"));
  }

  @Test
  void invalidPotionModifierCannotSpendResources() {
    var before = inventory.snapshot();
    assertThrows(WorkshopException.class, () ->
      facade.brew("healing", "extended", false)
    );
    assertEquals(before, inventory.snapshot());
  }

  @Test
  void brewingPreviewDoesNotMutateAndDrinkConsumesPotion() {
    var before = inventory.snapshot();
    facade.brew("healing", "strong", true);
    assertEquals(before, inventory.snapshot());
    facade.brew("healing", "strong", false);
    var potion = inventory
      .snapshot()
      .items()
      .stream()
      .filter(i -> i.kind().equals("potion"))
      .findFirst()
      .orElseThrow();
    facade.drink(potion.id());
    assertEquals(20, inventory.snapshot().health());
    assertThrows(WorkshopException.class, () -> inventory.item(potion.id()));
    assertEquals(11, inventory.snapshot().resources().get("glass_bottle"));
  }

  @Test
  void builderRejectsMissingOrConflictingInputs() {
    assertThrows(WorkshopException.class, () ->
      new AnvilOrder.Builder().build()
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
      new AnvilOrder.Builder().target("x").rename(" ").build()
    );
    assertThrows(WorkshopException.class, () ->
      new AnvilOrder.Builder().target("x").material(-1)
    );
  }

  @Test
  void anvilMaterialRepairRoundsCapsAndChargesActualUnits() {
    var item = target();
    var before = inventory.snapshot();
    var preview = facade.anvil(materialOrder(item, 64), true);
    assertEquals(before, inventory.snapshot());
    var quote = (AnvilService.Quote) preview.result().get("preview");
    assertEquals(250, quote.result().durability());
    assertEquals(3, quote.cost().get("iron"));
    facade.anvil(materialOrder(item, 64), false);
    assertEquals(250, inventory.item(item.id()).durability());
    assertEquals(61, inventory.snapshot().resources().get("iron"));
    assertEquals(97, inventory.snapshot().experience());
  }

  @Test
  void anvilCombinesDurabilityAndRaisesEqualEnchantmentLevels() {
    var target = target();
    var second = inventory
      .snapshot()
      .items()
      .stream()
      .filter(i -> i.kind().equals("pickaxe") && !i.id().equals(target.id()))
      .findFirst()
      .orElseThrow();
    var order = new AnvilOrder.Builder()
      .target(target.id())
      .sacrifice(second.id())
      .rename("Trusty Pickaxe")
      .build();
    facade.anvil(order, false);
    Item result = inventory.item(target.id());
    assertEquals(170, result.durability());
    assertEquals(4, result.enchantments().get("efficiency"));
    assertEquals(2, result.enchantments().get("unbreaking"));
    assertEquals("Trusty Pickaxe", result.name());
    assertThrows(WorkshopException.class, () -> inventory.item(second.id()));
    assertEquals(1, result.anvilUses());
  }

  @Test
  void enchantedBookTransfersAndIsConsumed() {
    var item = target();
    var book = book("fortune");
    facade.anvil(
      new AnvilOrder.Builder().target(item.id()).sacrifice(book.id()).build(),
      false
    );
    assertEquals(2, inventory.item(item.id()).enchantments().get("fortune"));
    assertThrows(WorkshopException.class, () -> inventory.item(book.id()));
  }

  @Test
  void anvilRejectsIncompatibleBookAndPreservesEverything() {
    var before = inventory.snapshot();
    assertThrows(WorkshopException.class, () ->
      facade.anvil(
        new AnvilOrder.Builder()
          .target(target().id())
          .sacrifice(book("protection").id())
          .build(),
        false
      )
    );
    assertEquals(before, inventory.snapshot());
  }

  @Test
  void anvilConflictingEnchantmentsDoNotConsumeSacrifice() {
    var targetId = target().id();
    facade.enchant(targetId, "fortune", 2, false);
    var before = inventory.snapshot();
    assertThrows(WorkshopException.class, () ->
      facade.anvil(
        new AnvilOrder.Builder()
          .target(targetId)
          .sacrifice(book("silk_touch").id())
          .build(),
        false
      )
    );
    assertEquals(before, inventory.snapshot());
  }

  @Test
  void renameOnlyPreservesDurabilityAndEnchantments() {
    var item = target();
    facade.anvil(
      new AnvilOrder.Builder().target(item.id()).rename("Miner").build(),
      false
    );
    var updated = inventory.item(item.id());
    assertEquals(item.durability(), updated.durability());
    assertEquals(item.enchantments(), updated.enchantments());
    assertEquals("Miner", updated.name());
    assertEquals(99, inventory.snapshot().experience());
  }

  @Test
  void anvilDoesNotExceedMaxLevel() {
    var item = target();
    item = item.update(
      item.durability(),
      Map.of("efficiency", 5),
      item.name(),
      0
    );
    inventory.commit(Map.of(), 0, List.of(item.id()), List.of(item));
    var second = Item.book(Map.of("efficiency", 5));
    inventory.commit(Map.of(), 0, List.of(), List.of(second));
    var id = item.id();
    var before = inventory.snapshot();
    assertThrows(WorkshopException.class, () ->
      facade.anvil(
        new AnvilOrder.Builder().target(id).sacrifice(second.id()).build(),
        false
      )
    );
    assertEquals(before, inventory.snapshot());
  }

  @Test
  void tooExpensiveAnvilOrderIsRejected() {
    var item = target();
    item = item.update(item.durability(), item.enchantments(), item.name(), 6);
    inventory.commit(Map.of(), 0, List.of(item.id()), List.of(item));
    var id = item.id();
    var before = inventory.snapshot();
    assertThrows(WorkshopException.class, () ->
      facade.anvil(
        new AnvilOrder.Builder().target(id).rename("Expensive").build(),
        false
      )
    );
    assertEquals(before, inventory.snapshot());
  }

  @Test
  void insufficientExperienceCannotConsumeDonorOrMaterial() {
    inventory.commit(Map.of(), 100, List.of(), List.of());
    var before = inventory.snapshot();
    assertThrows(WorkshopException.class, () ->
      facade.anvil(materialOrder(target(), 1), false)
    );
    assertEquals(before, inventory.snapshot());
  }

  @Test
  void useAppliesEnchantmentsAndPotionEffects() {
    var item = target();
    facade.enchant(item.id(), "fortune", 2, false);
    facade.enchant(item.id(), "unbreaking", 3, false);
    var result = facade.use(item.id());
    assertEquals(
      3,
      ((Map<?, ?>) result.result().get("produced")).get("diamond")
    );
    assertEquals(6, result.result().get("wear"));
    assertEquals(6, result.result().get("duration"));
    facade.brew("swiftness", "strong", false);
    var potion = inventory
      .snapshot()
      .items()
      .stream()
      .filter(i -> i.potion() != null)
      .findFirst()
      .orElseThrow();
    facade.drink(potion.id());
    result = facade.use(item.id());
    assertEquals(2, result.result().get("duration"));
    assertEquals(3, result.state().effects().get("swiftness_ii"));
  }

  @Test
  void silkTouchProducesOreAndBrokenToolNeedsRepair() {
    var item = target();
    facade.enchant(item.id(), "silk_touch", 1, false);
    var result = facade.use(item.id());
    assertEquals(Map.of("diamond_ore", 1), result.result().get("produced"));
    item = inventory.item(item.id());
    item = item.update(0, item.enchantments(), item.name(), 0);
    inventory.commit(Map.of(), 0, List.of(item.id()), List.of(item));
    var id = item.id();
    var before = inventory.snapshot();
    assertThrows(WorkshopException.class, () -> facade.use(id));
    assertEquals(before, inventory.snapshot());
    facade.anvil(materialOrder(item, 1), false);
    assertTrue(inventory.item(id).durability() > 0);
  }

  @Test
  void parallelFullSetCraftsCannotOverspend() throws Exception {
    inventory.commit(Map.of("iron", 40), 0, List.of(), List.of());
    var executor = Executors.newFixedThreadPool(6);
    try {
      var tasks = new ArrayList<Callable<Boolean>>();
      for (int i = 0; i < 6; i++) tasks.add(() -> {
        try {
          facade.craftArmor(Material.IRON, "set");
          return true;
        } catch (WorkshopException e) {
          return false;
        }
      });
      int count = 0;
      for (var result : executor.invokeAll(tasks)) if (result.get()) count++;
      assertEquals(1, count);
      assertEquals(0, inventory.snapshot().resources().get("iron"));
    } finally {
      executor.shutdownNow();
    }
  }
}
