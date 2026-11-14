# Архитектура мастерской

JavaScript отображает серверный каталог и состояние игрока. Ни рецепты операций, ни совместимость чар, ни окончательная стоимость наковальни не определяются браузером. Предварительный просмотр и применение выполняются через разные endpoints; применение заново рассчитывает результат под общей блокировкой инвентаря.

## Компоненты

```mermaid
flowchart LR
 UI[Minecraft UI] -->|fetch JSON| C[WorkshopController]
 C --> F[WorkshopFacade]
 C --> B[AnvilOrder.Builder]
 B --> O[AnvilOrder]
 O --> F
 F --> AF[ArmorFactory]
 AF --> IR[IronArmorFactory]
 AF --> DI[DiamondArmorFactory]
 F --> FM[ToolWorkshop]
 FM --> P[PickaxeWorkshop]
 FM --> A[AxeWorkshop]
 FM --> SH[ShovelWorkshop]
 F --> EN[EnchantingTable]
 EN --> ST[EnchantmentStrategy]
 F --> BR[BrewingSystem]
 BR --> BA[BrewingAdapter]
 BA --> LE[LegacyBrewingSystem]
 F --> AN[AnvilService]
 F --> INV[InventoryService]
 INV --> SNAP[Operation.State]
 SNAP --> UI
```

## Abstract Factory и Factory Method

```mermaid
classDiagram
 class ArmorFactory {
  <<interface>>
  +createHelmet() Helmet
  +createChestplate() Chestplate
  +createLeggings() Leggings
  +createBoots() Boots
 }
 ArmorFactory <|.. IronArmorFactory
 ArmorFactory <|.. DiamondArmorFactory
 ArmorFactory ..> Helmet
 ArmorFactory ..> Chestplate
 ArmorFactory ..> Leggings
 ArmorFactory ..> Boots
 ArmorPiece <|-- Helmet
 ArmorPiece <|-- Chestplate
 ArmorPiece <|-- Leggings
 ArmorPiece <|-- Boots
 Helmet <|.. IronHelmet
 Helmet <|.. DiamondHelmet
 Chestplate <|.. IronChestplate
 Chestplate <|.. DiamondChestplate
 Leggings <|.. IronLeggings
 Leggings <|.. DiamondLeggings
 Boots <|.. IronBoots
 Boots <|.. DiamondBoots
 class ArmorPiece {
  <<interface>>
  +item() Item
 }
 class ToolWorkshop {
  <<abstract>>
  #createTool(material) Tool
  +craft(material) Item
  +harvest(item) Map
 }
 ToolWorkshop <|-- PickaxeWorkshop
 ToolWorkshop <|-- AxeWorkshop
 ToolWorkshop <|-- ShovelWorkshop
 ToolWorkshop ..> Tool
 class Tool {
  <<interface>>
  +item() Item
  +action() String
  +harvest(item) Map
 }
 Tool <|.. Pickaxe
 Tool <|.. Axe
 Tool <|.. Shovel
 PickaxeWorkshop ..> Pickaxe : creates
 AxeWorkshop ..> Axe : creates
 ShovelWorkshop ..> Shovel : creates
```

ArmorPiece и Tool — интерфейсы продуктов. Абстрактные продукты брони — вложенные интерфейсы Helmet, Chestplate, Leggings и Boots. Каждая конкретная фабрика имеет собственные реализации: IronHelmet/DiamondHelmet и т. д. Реализации инструментов вложены в Tool. Item — неизменяемая модель экземпляра для хранения и передачи по API.

## Strategy, Adapter, Builder и Facade

```mermaid
classDiagram
 WorkshopFacade --> ArmorFactory
 WorkshopFacade --> ToolWorkshop
 WorkshopFacade --> EnchantingTable
 WorkshopFacade --> BrewingSystem
 WorkshopFacade --> AnvilService
 WorkshopFacade --> InventoryService
 EnchantingTable --> EnchantmentStrategy
 class EnchantmentStrategy {
  <<interface>>
  +supports(item) boolean
  +apply(item, level) Quote
 }
 EnchantmentStrategy <|.. EfficiencyStrategy
 EnchantmentStrategy <|.. UnbreakingStrategy
 EnchantmentStrategy <|.. FortuneStrategy
 EnchantmentStrategy <|.. SilkTouchStrategy
 EnchantmentStrategy <|.. ProtectionStrategy
 class BrewingSystem {
  <<interface>>
  +brew(recipe, modifier) Brew
 }
 BrewingSystem <|.. BrewingAdapter
 BrewingAdapter --> LegacyBrewingSystem
 class AnvilOrderBuilder {
  +target(id) Builder
  +material(units) Builder
  +sacrifice(id) Builder
  +rename(name) Builder
  +build() AnvilOrder
 }
 AnvilOrderBuilder ..> AnvilOrder
 AnvilService --> AnvilOrder
 AnvilService --> EnchantingTable : compatibility rules
 AnvilService --> InventoryService : read operands
```

`AnvilOrderBuilder` обозначает вложенный класс `AnvilOrder.Builder`. LegacyBrewingSystem — самостоятельная учебная библиотека с несовместимым контрактом: числовые коды ингредиентов и эффектов, длительность в секундах. BrewingAdapter переводит контракт приложения в этот API и обратно.

## Последовательность наковальни

```mermaid
sequenceDiagram
 actor Player as Игрок
 participant UI as app.js
 participant C as WorkshopController
 participant B as AnvilOrder.Builder
 participant F as WorkshopFacade
 participant A as AnvilService
 participant I as InventoryService
 Player->>UI: Предмет + материал/донор + имя
 UI->>C: POST /api/anvil/preview
 C->>B: target / material / sacrifice / rename
 B-->>C: AnvilOrder
 C->>F: anvil(order, true)
 Note over F,I: Блокировка общего состояния
 F->>A: preview(order, inventory)
 A->>I: Прочитать экземпляры по UUID
 I-->>A: Item + второй Item
 A-->>F: Новый Item, материалы, опыт, UUID расходуемых предметов
 F-->>UI: Quote + неизменённое State
 Player->>UI: Применить
 UI->>C: POST /api/anvil
 C->>B: Сборка и проверка заказа
 B-->>C: AnvilOrder
 C->>F: anvil(order, false)
 Note over F,I: Проверка и применение под одной блокировкой
 F->>A: Повторный расчёт по текущим предметам
 A-->>F: Quote
 F->>I: commit(cost, xp, removed, added)
 alt Все проверки пройдены
  I-->>F: Атомарная замена экземпляров и списание ресурсов
  F-->>UI: HTTP 200 + State
 else Недостаточно опыта/ресурсов, конфликт или устаревший UUID
  I-->>C: WorkshopException
  C-->>UI: HTTP 400 + текущее State
 end
 UI-->>Player: Обновлённый инвентарь или причина отказа
```

## Целостность состояния

Предметы неизменяемы; snapshot не раскрывает изменяемые коллекции. Все модификации выполняются через копии ресурсов и экземпляров и применяются после проверок. Facade удерживает монитор InventoryService на всём протяжении составной операции. Ошибки расчёта и нехватка ресурсов не могут частично списать материал, опыт или второй предмет.

Стратегия зачарования создаётся на запрос и не хранится в изменяемом singleton-контексте. AnvilService использует общие правила совместимости и максимальных уровней Strategy, но выполняет объединение по собственным правилам наковальни.

Дальнейшая работа: БД, отдельные игроки, транзакции хранения, идемпотентные команды, полный набор чар и точные правила выбранной редакции Minecraft.
