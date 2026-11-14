package com.example.sdpfinal.minecraft;

import static org.junit.jupiter.api.Assertions.*;

import com.example.sdpfinal.minecraft.model.*;
import com.example.sdpfinal.minecraft.service.*;
import java.net.*;
import java.net.http.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WorkshopApiTests {

  @LocalServerPort
  int port;

  @Autowired
  InventoryService inventory;

  HttpClient client = HttpClient.newHttpClient();

  @BeforeEach
  void reset() {
    inventory.reset();
  }

  HttpResponse<String> call(String path, String body) throws Exception {
    var request = HttpRequest.newBuilder(
      URI.create("http://localhost:" + port + path)
    );
    if (body != null) request
      .header("Content-Type", "application/json")
      .POST(HttpRequest.BodyPublishers.ofString(body));
    return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
  }

  @Test
  void frontendStylesSpritesAndCatalogAreServed() throws Exception {
    for (String path : new String[] {
      "/",
      "/style.css?v=10",
      "/app.js?v=10",
      "/assets/items.svg",
      "/assets/landscape.svg",
      "/api/catalog",
      "/api/state",
    })
      assertEquals(200, call(path, null).statusCode(), path);
    String html = call("/", null).body();
    assertTrue(html.contains("Anvil"));
    assertFalse(html.contains("Abstract Factory"));
    assertTrue(call("/api/state", null).body().contains("\"enchantments\""));
  }

  @Test
  void craftingAndEnchantingMutateBackendAndReturnTrace() throws Exception {
    var response = call("/tools", "{}");
    assertEquals(404, response.statusCode());
    response = call(
      "/api/tools",
      "{\"material\":\"DIAMOND\",\"kind\":\"axe\"}"
    );
    assertEquals(200, response.statusCode());
    assertTrue(response.body().contains("Factory Method"));
    Item axe = inventory
      .snapshot()
      .items()
      .stream()
      .filter(i -> i.kind().equals("axe"))
      .findFirst()
      .orElseThrow();
    response = call(
      "/api/enchant",
      "{\"itemId\":\"" +
        axe.id() +
        "\",\"enchantment\":\"efficiency\",\"level\":2}"
    );
    assertEquals(200, response.statusCode());
    assertEquals(2, inventory.item(axe.id()).enchantments().get("efficiency"));
    assertTrue(response.body().contains("Strategy"));
  }

  @Test
  void anvilPreviewDoesNotConsumeAndApplyDoes() throws Exception {
    Item target = inventory
      .snapshot()
      .items()
      .stream()
      .filter(Item::tool)
      .findFirst()
      .orElseThrow();
    var before = inventory.snapshot();
    String body =
      "{\"itemId\":\"" +
      target.id() +
      "\",\"materialUnits\":1,\"name\":\"My Pickaxe\"}";
    var preview = call("/api/anvil/preview", body);
    assertEquals(200, preview.statusCode());
    assertEquals(before, inventory.snapshot());
    var applied = call("/api/anvil", body);
    assertEquals(200, applied.statusCode());
    assertEquals("My Pickaxe", inventory.item(target.id()).name());
    assertTrue(applied.body().contains("Builder"));
  }

  @Test
  void brewingUsesAdapterAndDrinkingChangesHealth() throws Exception {
    var response = call(
      "/api/brew",
      "{\"recipe\":\"healing\",\"modifier\":\"strong\"}"
    );
    assertEquals(200, response.statusCode());
    assertTrue(response.body().contains("Adapter"));
    Item potion = inventory
      .snapshot()
      .items()
      .stream()
      .filter(i -> i.potion() != null)
      .findFirst()
      .orElseThrow();
    assertEquals(
      200,
      call("/api/drink", "{\"itemId\":\"" + potion.id() + "\"}").statusCode()
    );
    assertEquals(20, inventory.snapshot().health());
  }

  @Test
  void malformedUnknownAndFractionalInputsAreRejectedAtomically()
    throws Exception {
    var before = inventory.snapshot();
    for (String body : new String[] {
      "{",
      "{\"material\":\"GOLD\",\"kind\":\"helmet\"}",
      "{}",
    }) {
      var response = call("/api/armor", body);
      assertEquals(400, response.statusCode());
      assertTrue(response.body().contains("\"message\""));
    }
    var fraction = call(
      "/api/enchant",
      "{\"itemId\":\"x\",\"enchantment\":\"efficiency\",\"level\":1.5}"
    );
    assertEquals(400, fraction.statusCode());
    assertEquals(before, inventory.snapshot());
  }
}
