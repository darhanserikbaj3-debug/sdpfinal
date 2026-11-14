# Minecraft Workshop

Мастерская снаряжения: создание брони и инструментов, зачарование, приготовление зелий и наковальня. Java 17+, Spring Boot, HTML/CSS/JavaScript. Frontend обслуживается тем же backend; Node.js и база данных для запуска не нужны.

## Запуск

```sh
cd minecraft
./mvnw clean verify
./mvnw spring-boot:run
```

Открыть **http://localhost:8080/**. Не открывать HTML через Live Server: игровые операции требуют Spring Boot. С установленным Maven можно использовать `mvn` вместо `./mvnw`.

Готовая сборка после `mvn clean verify`:

```sh
java -jar target/minecraft-0.0.1-SNAPSHOT.jar
```

Другой порт: `java -jar target/minecraft-0.0.1-SNAPSHOT.jar --server.port=8081`. Все ресурсы и API используют относительные URL. После обновления исходников перезапустить Spring Boot; CSS и JS имеют версию в ссылках для обновления кэша.

## Шесть паттернов

| Паттерн | Обязанность | Классы |
|---|---|---|
| Abstract Factory | Создаёт семейство брони одного материала: четыре совместимых продукта | `ArmorFactory`, `IronArmorFactory`, `DiamondArmorFactory`, `ArmorPiece.Helmet/Chestplate/Leggings/Boots` |
| Factory Method | Общий процесс создания инструмента делегирует конкретный продукт подклассу; продукты имеют разные действия | `ToolWorkshop.createTool()`, `PickaxeWorkshop`, `AxeWorkshop`, `ShovelWorkshop`, `Tool` |
| Strategy | Применяет правила выбранного зачарования: совместимость, уровень, стоимость в опыте и лазурите | `EnchantingTable`, `EnchantmentStrategy`, пять конкретных стратегий |
| Adapter | Подключает несовместимый API зельеварения с числовыми кодами и временем в секундах | `BrewingSystem`, `BrewingAdapter`, `LegacyBrewingSystem` |
| Builder | Пошагово собирает и проверяет описание операции наковальни | `AnvilOrder.Builder` |
| Facade | Координирует все операции, инвентарь, ресурсы, опыт и изменение предметов | `WorkshopFacade` |

Builder не изменяет предметы самостоятельно: он создаёт заказ. `AnvilService` рассчитывает результат и цену; Facade выполняет атомарное изменение инвентаря. Веб-интерфейс показывает игровые действия без технических объяснений паттернов. Серверные ответы содержат `steps` для проверки реализации через Network/Postman.

## Что работает

- Железная/алмазная броня: шлем, нагрудник, поножи, ботинки, полный комплект. Стоимость полного комплекта — 24 единицы материала.
- Кирка добывает алмазы и железо; топор — брёвна и палки; лопата — землю и кремень. Каждый экземпляр имеет UUID, собственную прочность, имя и зачарования.
- Стол зачарований: Efficiency I–V, Unbreaking I–III, Fortune I–III, Silk Touch I, Protection I–IV. Совместимость проверяется сервером.
- Зелья лечения, скорости и огнестойкости; обычные, усиленные или продлённые варианты, где это допустимо. Лечение восстанавливает здоровье; скорость уменьшает длительность действий, огнестойкость предотвращает урон от учебной опасности. Эффекты расходуются действиями.
- Наковальня: ремонт материалом, объединение двух одинаковых предметов, перенос чар с книги, объединение книг, переименование.
- Стоимость/результат зачарования, зелья и наковальни можно посмотреть до выполнения, без списания.
- Отказы сохраняют инвентарь, ресурсы, здоровье и опыт. Повторная проверка выполняется при применении; предварительный просмотр ничего не резервирует.

## Правила наковальни

1. Основной предмет + материал того же типа: единица материала восстанавливает `floor(maxDurability / 4)` прочности. Лишний выбранный материал не списывается.
2. Два одинаковых предмета одного материала: складываются остатки прочности с бонусом 12% от максимума, до полного восстановления; второй предмет расходуется.
3. Совместимые чары переносятся с предмета или книги. Равные уровни повышаются на один, до максимума; разные уровни дают больший уровень.
4. Fortune и Silk Touch несовместимы. Несовместимость отклоняет весь заказ без расхода предметов.
5. Можно только переименовать предмет. Имя — до 50 символов. Повторное применение без изменений отклоняется.
6. Учитывается история предыдущих работ. При цене 40 и выше операция отклоняется как слишком дорогая.

Это учебная модель, вдохновлённая Minecraft, а не полная копия Java/Bedrock Edition. Формула опыта, детерминированный Unbreaking, Fortune, стоимость стола и правила отказа при конфликте упрощены. Случайные зачарования, книжные полки, полный набор чар и все особенности prior-work penalty не воспроизводятся.

## Сценарий защиты

1. Во вкладке «Броня» выбрать материал и создать полный комплект. Показать четыре новых предмета и списание 24 материалов.
2. Во вкладке «Инструменты» создать кирку, топор и лопату. Нажать «Добыть», «Рубить», «Копать» на карточках и сравнить ресурсы и износ.
3. На столе выбрать инструмент, Efficiency/Unbreaking/Fortune, уровень; сначала посмотреть стоимость, затем применить. Сравнить время действия, добычу или износ.
4. В «Зельях» посмотреть ингредиенты и сварить усиленное лечение. Выпить из инвентаря и показать восстановление здоровья. Затем проверить скорость.
5. На наковальне выбрать одну из двух стартовых повреждённых железных кирок, режим «Предмет или книга» и вторую кирку. В предварительном просмотре Efficiency III + III даёт IV, переносится Unbreaking II и восстанавливается прочность. Задать имя и применить; второй экземпляр исчезает.
6. Починить предмет материалом; отдельно перенести чары с книги. Попытка сочетать Fortune и Silk Touch должна закончиться отказом без списания.

## Начальная сессия и ограничения

Одна общая мастерская в памяти процесса. Старт: 100 единиц опыта, 14/20 здоровья, ресурсы, две повреждённые кирки и пять зачарованных книг для демонстрации наковальни. Кнопка «Начать заново» сбрасывает сессию. Перезапуск сервера также сбрасывает данные. Открытые вкладки разделяют инвентарь; для актуализации другой вкладки используется ↻.

Максимум 160 предметов. У сломанного предмета остаётся UUID: он доступен для ремонта. При нулевом здоровье нужно выпить лечение. Мир не моделируется, добыча не ограничена запасами жил. Зелье расходуется при использовании, бутылка не возвращается. Вода для варки считается доступной. Единицы опыта — общий учебный баланс, не формула уровней vanilla Minecraft. Авторизации, БД и фонового течения времени нет.

## API

| Метод | Путь | Тело |
|---|---|---|
| GET | `/api/catalog` | Каталог и цены |
| GET | `/api/state` | Состояние игрока и инвентаря |
| POST | `/api/armor` | `{"material":"IRON","kind":"set"}` |
| POST | `/api/tools` | `{"material":"DIAMOND","kind":"pickaxe"}` |
| POST | `/api/enchant[/preview]` | `{"itemId":"UUID","enchantment":"efficiency","level":3}` |
| POST | `/api/brew[/preview]` | `{"recipe":"healing","modifier":"strong"}` |
| POST | `/api/anvil[/preview]` | `{"itemId":"UUID","sacrificeId":null,"materialUnits":2,"name":"Верная кирка"}` |
| POST | `/api/use` | `{"itemId":"UUID"}` |
| POST | `/api/drink` | `{"itemId":"UUID"}` |
| POST | `/api/reset` | `{}` |

`[/preview]` обозначает два отдельных пути: например `/api/anvil` и `/api/anvil/preview`.

Каждая операция возвращает `requestId`, `serverTime`, `message`, `result`, `state`, `steps`. Состояние включает `resources`, `items`, `experience`, `health`, `effects`, `revision`. Ошибки пользовательских данных возвращаются как HTTP 400 с актуальным состоянием. Проверка и изменение всех частей состояния защищены общим монитором; параллельные операции не могут потратить один предмет или материал дважды.

## Проверки и материалы

`mvn clean verify`: 23 модульных и 5 HTTP-интеграционных тестов. Проверяются семейства брони, поведение инструментов, правила чар, адаптация зелий, Builder, восстановление прочности, объединение уровней, книги, переименование, цена, атомарность и параллельный крафт, выдача CSS/JS/SVG и API.

[UML и архитектура](docs/architecture.md). [Desktop](docs/screenshots/workshop-desktop.png), [Mobile](docs/screenshots/workshop-mobile.png).

Для отчёта: введение, основная часть с обоснованием паттернов, UML, код, скриншоты, заключение, дальнейшая работа и распределение авторства (минимум два паттерна на каждого студента).

Браузерная проверка в Chrome: создание брони и инструментов, все действия инструментов, стоимость/применение зачарований, объединение двух кирок, ремонт материалом, переименование, варка и использование зелий, отклонение несовместимой книги без расхода. Проверены CSS и отсутствие горизонтального переполнения во всех пяти вкладках на ширинах 320, 390 и 768 px; ошибок JavaScript не обнаружено.

## English interface and connection recovery

The entire interface, item names and API messages are in English. The header is compact. Material, crafting, enchantment and potion choices render immediately, even if the catalog request fails. Crafting remains disabled until the real inventory has loaded; no offline progress is simulated. Use Refresh to reconnect. If the HTML is opened directly, the page shows the correct application URL: http://localhost:8080/. A mismatched server catalog prompts a Spring Boot restart.

Verified in Chrome: populated selectors, English-only UI, all gameplay operations, recovery after a failed catalog request, direct-file guidance, and mobile widths of 320/390/768 px. All 28 Java and HTTP tests pass.


## Tests for each design pattern

Each class below contains four focused JUnit tests:

| Pattern | Test class | Coverage |
|---|---|---|
| Abstract Factory | `abstractfactory/AbstractFactoryTests.java` | Iron family, diamond family, interchangeable factories and unique instances, invalid requests |
| Factory Method | `factorymethod/FactoryMethodTests.java` | Pickaxe mining, axe harvesting, shovel digging, invalid creator requests |
| Strategy | `strategy/StrategyTests.java` | Immutable enchantment application, alternative costs and compatibility, conflicts, invalid levels and targets |
| Adapter | `adapter/AdapterTests.java` | Healing recipe conversion, strong modifier, timed effects, legacy error conversion |
| Builder | `builder/BuilderTests.java` | Material repair orders, combination orders, immutable rename orders, invalid inputs |
| Facade | `facade/FacadeTests.java` | Coordinated crafting, preview/apply costs, anvil combination, atomic failure |

The 24 pattern tests supplement the existing 23 regression tests and 5 HTTP tests: **52 tests total**. All tests are under `minecraft/src/test/java/com/example/sdpfinal/minecraft/`.

Run only the pattern tests:

```sh
mvn test -Dtest=AbstractFactoryTests,FactoryMethodTests,StrategyTests,AdapterTests,BuilderTests,FacadeTests
```

## Reading the implementation

Application logic uses explicit types, ordinary loops and named functions. There is no Stream API, lambda processing or bit shifting in the production Java code. The JavaScript uses named event handlers instead of chained map/filter callbacks. Small Java records hold data; interfaces and concrete classes retain the six design patterns. Inventory copies and synchronization are kept to prevent partial resource consumption and concurrent overspending.

After the readability refactor, all 52 tests pass. Browser checks also cover the full gameplay workflow, connection recovery, populated selectors, and mobile layouts.
