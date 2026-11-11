# Отчёт об очистке проекта

Дата: 11 ноября 2026 года.

## Удалено

- `MaterialToolFactories.java` — пустой файл без объявлений классов.
- `InventoryService.java` — не вызывался из кода приложения и тестов.
- `CraftingBenchService.java` — не вызывался из кода приложения и тестов.
- `NotificationService.java` — не вызывался из кода приложения и тестов.

## Сохранено

- `WoodEquipmentFactory` и `StoneEquipmentFactory`: используются API Abstract Factory.
- `EquipmentTextBuilder`: используется тестами и демонстрирует вторую реализацию Builder.
- Остальные классы участвуют в реализации приложения и его паттернов.

## Проверка

Компиляция и все 8 тестов прошли успешно. Для Mockito явно подключён Java agent, чтобы избежать ошибки автоматического подключения агента в текущем окружении:

```sh
cd minecraft
./mvnw -o -Daether.syncContext.named.factory=noop -DargLine=-javaagent:/Users/danchik/.m2/repository/org/mockito/mockito-core/5.23.0/mockito-core-5.23.0.jar test
```

Путь к агенту относится к локальному окружению; на другом компьютере его нужно заменить. Проверка в браузере не выполнялась.
