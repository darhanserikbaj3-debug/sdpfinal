"use strict";

const labels = {
    IRON: "Iron",
    DIAMOND: "Diamond",
    iron: "Iron",
    diamond: "Diamonds",
    stick: "Sticks",
    lapis: "Lapis lazuli",
    glass_bottle: "Bottles",
    nether_wart: "Nether wart",
    blaze_powder: "Blaze powder",
    sugar: "Sugar",
    glistering_melon: "Glistering melon",
    magma_cream: "Magma cream",
    redstone: "Redstone",
    glowstone_dust: "Glowstone dust",
    helmet: "Helmet",
    chestplate: "Chestplate",
    leggings: "Leggings",
    boots: "Boots",
    set: "Full set",
    pickaxe: "Pickaxe",
    axe: "Axe",
    shovel: "Shovel",
    efficiency: "Efficiency",
    unbreaking: "Unbreaking",
    fortune: "Fortune",
    silk_touch: "Silk Touch",
    protection: "Protection",
    healing: "Healing",
    swiftness: "Swiftness",
    swiftness_ii: "Swiftness II",
    fire_resistance: "Fire Resistance",
    none: "Standard",
    strong: "Strong II",
    extended: "Extended",
    diamond_ore: "Diamond ore",
    log: "Logs",
    dirt: "Dirt",
    flint: "Flint",
    book: "Book",
    potion: "Potion",
};
const defaultCatalog = {
    materials: ["IRON", "DIAMOND"],
    armor: { helmet: 5, chestplate: 8, leggings: 7, boots: 4, set: 24 },
    tools: { pickaxe: 3, axe: 3, shovel: 1 },
    enchantments: [
        { id: "efficiency", maxLevel: 5, price: 2 },
        { id: "unbreaking", maxLevel: 3, price: 3 },
        { id: "fortune", maxLevel: 3, price: 4 },
        { id: "silk_touch", maxLevel: 1, price: 6 },
        { id: "protection", maxLevel: 4, price: 2 },
    ],
    potions: [
        { id: "healing", modifiers: ["none", "strong"] },
        { id: "swiftness", modifiers: ["none", "strong", "extended"] },
        { id: "fire_resistance", modifiers: ["none", "extended"] },
    ],
};

let catalog = defaultCatalog;
let catalogLoaded = false;
let state = null;
let busy = false;

function getElement(selector) {
    return document.querySelector(selector);
}

function getName(key) {
    if (labels[key]) {
        return labels[key];
    }
    return key;
}

function escapeHtml(value) {
    let text = String(value);
    text = text.replaceAll("&", "&amp;");
    text = text.replaceAll("<", "&lt;");
    text = text.replaceAll(">", "&gt;");
    text = text.replaceAll('"', "&quot;");
    text = text.replaceAll("'", "&#39;");
    return text;
}

function roman(level) {
    const levels = ["", "I", "II", "III", "IV", "V"];
    if (levels[level]) {
        return levels[level];
    }
    return level;
}

function icon(key, material, extraClass) {
    if (!material) material = "IRON";
    if (!extraClass) extraClass = "";
    const shapes = {
        iron: "ingot",
        diamond: "gem",
        lapis: "gem",
        glass_bottle: "potion",
        nether_wart: "wart",
        blaze_powder: "powder",
        sugar: "powder",
        glistering_melon: "melon",
        magma_cream: "magma",
        redstone: "powder",
        glowstone_dust: "powder",
        log: "log",
        diamond_ore: "gem",
        set: "chestplate",
        dirt: "block",
        flint: "coal",
    };
    let shape = key;
    if (shapes[key]) shape = shapes[key];
    const cssClass = material.toLowerCase() + " icon-" + key + " " + extraClass;
    return `<svg class="item-sprite ${cssClass}" viewBox="0 0 16 16" aria-hidden="true"><use href="./assets/items.svg#${shape}"></use></svg>`;
}

function chips(resources) {
    let html = "";
    for (const key of Object.keys(resources)) {
        const label = escapeHtml(getName(key));
        html += `<span class="chip">${icon(key)}${label} <b>×${resources[key]}</b></span>`;
    }
    if (html === "") html = '<span class="subtle">—</span>';
    return html;
}

function enchantText(item) {
    const texts = [];
    for (const key of Object.keys(item.enchantments)) {
        texts.push(getName(key) + " " + roman(item.enchantments[key]));
    }
    return texts.join(" · ");
}

function itemSummary(item) {
    let text = item.name;
    if (item.maxDurability > 0) {
        text += " · " + item.durability + "/" + item.maxDurability;
    }
    const enchantments = enchantText(item);
    if (enchantments !== "") text += " · " + enchantments;
    return text;
}

function itemButtons(item) {
    const id = item.id;
    if (item.kind === "potion") {
        return `<button data-action="drink" data-id="${id}">Drink</button>`;
    }
    let html = "";
    if (item.kind !== "book") {
        let action = "Test";
        if (item.kind === "pickaxe") action = "Mine";
        if (item.kind === "axe") action = "Chop";
        if (item.kind === "shovel") action = "Dig";
        html += `<button data-action="use" data-id="${id}">${action}</button>`;
    }
    html += `<button data-action="enchant" data-id="${id}" title="Enchant">✦</button>`;
    html += `<button data-action="anvil" data-id="${id}" title="Use anvil">⚒</button>`;
    return html;
}

function itemCard(item, compact) {
    let extraClass = "";
    let potionInfo = "";
    if (item.potion) {
        extraClass = "effect-" + item.potion.effect;
        if (item.potion.effect === "healing") {
            potionInfo = "+" + item.potion.power * 6 + " health";
        } else {
            potionInfo = item.potion.turns + " actions";
        }
    }
    let html = '<div class="item-top">';
    html += icon(item.kind, item.material, extraClass);
    html += "<div><strong>" + escapeHtml(item.name) + "</strong>";
    html += '<small class="enchant-text">' + escapeHtml(enchantText(item)) + "</small>";
    if (potionInfo !== "") html += "<small>" + potionInfo + "</small>";
    html += "</div></div>";

    if (item.maxDurability > 0) {
        const percent = Math.round((item.durability / item.maxDurability) * 100);
        let barClass = "durability";
        if (percent < 25) barClass += " low";
        html += `<div class="${barClass}"><i style="width:${percent}%"></i></div>`;
        html += `<small class="durability-label">${item.durability} / ${item.maxDurability}</small>`;
    }
    if (!compact) html += '<div class="item-actions">' + itemButtons(item) + "</div>";
    return html;
}

function setOptions(selector, items, placeholder) {
    const select = getElement(selector);
    const oldValue = select.value;
    let html = '<option value="">' + escapeHtml(placeholder) + "</option>";
    let oldValueExists = false;
    for (const item of items) {
        html += `<option value="${item.id}">${escapeHtml(itemSummary(item))}</option>`;
        if (item.id === oldValue) oldValueExists = true;
    }
    select.innerHTML = html;
    if (oldValueExists) select.value = oldValue;
}

function findItem(id) {
    if (!state) return null;
    for (const item of state.items) {
        if (item.id === id) return item;
    }
    return null;
}

function updateSacrifice() {
    const target = findItem(getElement("#anvil-item").value);
    const choices = [];
    if (target) {
        for (const item of state.items) {
            const sameKind = item.kind === target.kind;
            const sameMaterial = item.material === target.material;
            if (item.id !== target.id && (item.kind === "book" || (sameKind && sameMaterial))) {
                choices.push(item);
            }
        }
    }
    setOptions("#anvil-sacrifice", choices, "Choose a second item");
}

function compareResources(first, second) {
    return getName(first).localeCompare(getName(second), "en");
}

function renderState(nextState) {
    state = nextState;
    getElement("#experience").textContent = state.experience;
    getElement("#item-count").textContent = state.items.length;

    let resourceHtml = "";
    const resourceKeys = Object.keys(state.resources);
    resourceKeys.sort(compareResources);
    for (const key of resourceKeys) {
        const amount = state.resources[key];
        if (amount > 0) {
            resourceHtml += `<div class="resource-slot">${icon(key)}<span>${escapeHtml(getName(key))}</span><b>${amount}</b></div>`;
        }
    }
    getElement("#resources").innerHTML = resourceHtml;

    let inventoryHtml = "";
    const equipment = [];
    for (const item of state.items) {
        inventoryHtml += `<article class="item-card" data-item="${item.id}">${itemCard(item, false)}</article>`;
        if (item.kind !== "potion") equipment.push(item);
    }
    if (inventoryHtml === "") inventoryHtml = '<p class="subtle">No items yet. Craft something.</p>';
    getElement("#inventory").innerHTML = inventoryHtml;

    let effectsHtml = "";
    for (const key of Object.keys(state.effects)) {
        effectsHtml += `<span class="effect">${escapeHtml(getName(key))} · ${state.effects[key]}</span>`;
    }
    getElement("#effects").innerHTML = effectsHtml;
    setOptions("#enchant-item", equipment, "Choose an item");
    setOptions("#anvil-item", equipment, "Choose an item");
    updateSacrifice();
}

function message(text, error) {
    getElement("#status").textContent = text;
    getElement("#status").classList.toggle("error", Boolean(error));
}

function delta(label, amount) {
    if (amount === 0) return "";
    let cssClass = "negative";
    let sign = "";
    if (amount > 0) {
        cssClass = "positive";
        sign = "+";
    }
    return `<span class="delta ${cssClass}">${sign}${amount} ${escapeHtml(label)}</span>`;
}

function showChanges(before, after) {
    let html = "";
    if (before) {
        const keys = Object.keys(before.resources);
        for (const key of Object.keys(after.resources)) {
            if (!keys.includes(key)) keys.push(key);
        }
        for (const key of keys) {
            let oldAmount = before.resources[key];
            let newAmount = after.resources[key];
            if (oldAmount === undefined) oldAmount = 0;
            if (newAmount === undefined) newAmount = 0;
            html += delta(getName(key), newAmount - oldAmount);
        }
        html += delta("XP", after.experience - before.experience);
        html += delta("health", after.health - before.health);
    }
    getElement("#changes").innerHTML = html;
}

async function request(path, body) {
    if (location.protocol === "file:") {
        throw new Error("Start Spring Boot and open http://localhost:8080.");
    }
    const before = state;
    const controller = new AbortController();
    const timeout = setTimeout(function () {
        controller.abort();
    }, 12000);
    const options = { method: "GET", headers: { Accept: "application/json" }, signal: controller.signal };
    if (body !== undefined) {
        options.method = "POST";
        options.headers["Content-Type"] = "application/json";
        options.body = JSON.stringify(body);
    }

    let response;
    let data;
    try {
        response = await fetch("./api/" + path, options);
        data = await response.json();
    } catch (error) {
        throw new Error("Connection lost. Refresh before retrying.");
    } finally {
        clearTimeout(timeout);
    }
    if (data.state) {
        renderState(data.state);
        showChanges(before, data.state);
        let text = data.message;
        if (data.result && data.result.duration) {
            text += " · " + data.result.duration + " sec · wear " + data.result.wear;
        }
        message(text, !response.ok);
    }
    if (!response.ok) {
        throw new Error(data.message || "Action failed.");
    }
    return data;
}

function updateButtons() {
    for (const button of document.querySelectorAll("button")) {
        let disabled = busy;
        if (!busy && !state && button.id !== "refresh" && !button.hasAttribute("data-tab")) {
            disabled = true;
        }
        button.disabled = disabled;
    }
}

function hidePreviews() {
    for (const preview of document.querySelectorAll(".preview")) {
        preview.hidden = true;
    }
}

async function performAction(path, body, previewSelector) {
    if (busy) return;
    busy = true;
    updateButtons();
    hidePreviews();
    try {
        const data = await request(path, body);
        if (previewSelector) showPreview(previewSelector, data);
    } catch (error) {
        message(error.message, true);
    } finally {
        busy = false;
        updateButtons();
    }
}

function showTab(key) {
    for (const button of document.querySelectorAll("[data-tab]")) {
        button.classList.toggle("active", button.dataset.tab === key);
    }
    for (const panel of document.querySelectorAll(".tab-panel")) {
        panel.hidden = panel.id !== key + "-panel";
    }
}

function materialOptions() {
    let html = "";
    for (const material of catalog.materials) {
        html += `<option value="${material}">${getName(material)}</option>`;
    }
    return html;
}

function materialSelect(type) {
    if (type === "armor") return getElement("#armor-material");
    return getElement("#tool-material");
}

function renderCraftChoices(type) {
    let kinds = ["pickaxe", "axe", "shovel"];
    let container = getElement("#tool-options");
    if (type === "armor") {
        kinds = ["helmet", "chestplate", "leggings", "boots", "set"];
        container = getElement("#armor-options");
    }
    const selected = document.querySelector('[name="' + type + '-kind"]:checked');
    let selectedKind = kinds[0];
    if (selected) selectedKind = selected.value;
    const material = materialSelect(type).value;
    let html = "";
    for (const kind of kinds) {
        let checked = "";
        if (kind === selectedKind) checked = "checked";
        html += `<label class="craft-choice"><input type="radio" name="${type}-kind" value="${kind}" ${checked}>${icon(kind, material)}<b>${getName(kind)}</b></label>`;
    }
    container.innerHTML = html;
    renderCraftCost(type);
}

function renderCraftCost(type) {
    const selected = document.querySelector('[name="' + type + '-kind"]:checked');
    if (!selected) return;
    const material = materialSelect(type).value.toLowerCase();
    const cost = {};
    let container;
    if (type === "armor") {
        cost[material] = catalog.armor[selected.value];
        container = getElement("#armor-cost");
    } else {
        cost[material] = catalog.tools[selected.value];
        cost.stick = 2;
        container = getElement("#tool-cost");
    }
    container.innerHTML = chips(cost);
}

function updateLevels() {
    let enchantment = null;
    for (const option of catalog.enchantments) {
        if (option.id === getElement("#enchantment").value) enchantment = option;
    }
    if (!enchantment) return;
    const select = getElement("#enchant-level");
    let oldLevel = Number(select.value);
    if (oldLevel < 1) oldLevel = 1;
    let html = "";
    for (let level = 1; level <= enchantment.maxLevel; level++) {
        html += `<option value="${level}">${roman(level)}</option>`;
    }
    select.innerHTML = html;
    select.value = Math.min(oldLevel, enchantment.maxLevel);
    const hints = {
        efficiency: "Faster harvesting.",
        unbreaking: "Less durability loss.",
        fortune: "More drops.",
        silk_touch: "Collect ore blocks.",
        protection: "Less damage.",
    };
    getElement("#enchant-hint").textContent = hints[enchantment.id];
}

function updateModifiers() {
    const selected = getElement('[name="potion"]:checked');
    if (!selected) return;
    let html = "";
    for (const potion of catalog.potions) {
        if (potion.id === selected.value) {
            for (const modifier of potion.modifiers) {
                html += `<option value="${modifier}">${getName(modifier)}</option>`;
            }
        }
    }
    getElement("#brew-modifier").innerHTML = html;
}

function renderCatalog() {
    getElement("#armor-material").innerHTML = materialOptions();
    getElement("#tool-material").innerHTML = materialOptions();
    renderCraftChoices("armor");
    renderCraftChoices("tools");
    let html = "";
    for (const enchantment of catalog.enchantments) {
        html += `<option value="${enchantment.id}">${getName(enchantment.id)}</option>`;
    }
    getElement("#enchantment").innerHTML = html;
    updateLevels();

    html = "";
    for (let i = 0; i < catalog.potions.length; i++) {
        const potion = catalog.potions[i];
        let checked = "";
        if (i === 0) checked = "checked";
        html += `<label class="craft-choice potion-${potion.id}"><input type="radio" name="potion" value="${potion.id}" ${checked}>${icon("potion", "IRON", "effect-" + potion.id)}<b>${getName(potion.id)}</b></label>`;
    }
    getElement("#potion-options").innerHTML = html;
    updateModifiers();
}

function showPreview(selector, data) {
    const quote = data.result.preview;
    let item = quote.item;
    if (!item) item = quote.result;
    if (!item) item = quote.potion;
    let cost = quote.cost;
    if (!cost) cost = { lapis: quote.lapis };
    let priceHtml = chips(cost);
    if (quote.experience > 0) {
        priceHtml += `<span class="chip xp-price">✦ ${quote.experience} XP</span>`;
    }
    const preview = getElement(selector);
    preview.hidden = false;
    preview.innerHTML =
        '<div class="preview-item">' +
        itemCard(item, true) +
        "</div>" +
        '<div class="preview-price"><span class="subtle">Cost</span><div class="chips">' +
        priceHtml +
        "</div></div>";
}

function enchantBody() {
    return {
        itemId: getElement("#enchant-item").value,
        enchantment: getElement("#enchantment").value,
        level: Number(getElement("#enchant-level").value),
    };
}

function brewBody() {
    return {
        recipe: getElement('[name="potion"]:checked').value,
        modifier: getElement("#brew-modifier").value,
    };
}

function anvilBody() {
    const mode = getElement("#anvil-mode").value;
    const body = { itemId: getElement("#anvil-item").value, sacrificeId: null, materialUnits: 0, name: null };
    if (mode === "combine") body.sacrificeId = getElement("#anvil-sacrifice").value;
    if (mode === "material") body.materialUnits = Number(getElement("#anvil-units").value);
    const name = getElement("#anvil-name").value.trim();
    if (name !== "") body.name = name;
    return body;
}

function updateAnvilMode() {
    const mode = getElement("#anvil-mode").value;
    getElement("#material-field").hidden = mode !== "material";
    getElement("#anvil-units").required = mode === "material";
    getElement("#anvil-units").disabled = mode !== "material";
    getElement("#sacrifice-field").hidden = mode !== "combine";
    getElement("#anvil-sacrifice").required = mode === "combine";
}

function handleClick(event) {
    const tab = event.target.closest("[data-tab]");
    if (tab) showTab(tab.dataset.tab);
    const button = event.target.closest("[data-action]");
    if (!button) return;
    const action = button.dataset.action;
    const id = button.dataset.id;
    if (action === "enchant" || action === "anvil") {
        showTab(action);
        getElement("#" + action + "-item").value = id;
        if (action === "anvil") updateSacrifice();
        hidePreviews();
    } else {
        performAction(action, { itemId: id });
    }
}

function handleChange(event) {
    hidePreviews();
    const field = event.target;
    if (field.id === "armor-material") renderCraftChoices("armor");
    if (field.id === "tool-material") renderCraftChoices("tools");
    if (field.name === "armor-kind") renderCraftCost("armor");
    if (field.name === "tools-kind") renderCraftCost("tools");
    if (field.id === "enchantment") updateLevels();
    if (field.name === "potion") updateModifiers();
    if (field.id === "anvil-item") updateSacrifice();
    if (field.id === "anvil-mode") updateAnvilMode();
}

function craftArmor(event) {
    event.preventDefault();
    const body = {
        material: getElement("#armor-material").value,
        kind: getElement('[name="armor-kind"]:checked').value,
    };
    performAction("armor", body);
}

function craftTool(event) {
    event.preventDefault();
    const body = {
        material: getElement("#tool-material").value,
        kind: getElement('[name="tools-kind"]:checked').value,
    };
    performAction("tools", body);
}

function enchant(event) {
    event.preventDefault();
    performAction("enchant", enchantBody(), "#enchant-result");
}

function brew(event) {
    event.preventDefault();
    performAction("brew", brewBody(), "#brew-result");
}

function applyAnvil(event) {
    event.preventDefault();
    performAction("anvil", anvilBody(), "#anvil-result");
}

function previewEnchant() {
    if (getElement("#enchant-form").reportValidity()) {
        performAction("enchant/preview", enchantBody(), "#enchant-result");
    }
}

function previewBrew() {
    if (getElement("#brew-form").reportValidity()) {
        performAction("brew/preview", brewBody(), "#brew-result");
    }
}

function previewAnvil() {
    if (getElement("#anvil-form").reportValidity()) {
        performAction("anvil/preview", anvilBody(), "#anvil-result");
    }
}

function resetWorkshop() {
    if (confirm("Start over? All crafted items will be reset.")) {
        performAction("reset", {});
    }
}

async function loadCatalog() {
    const next = await request("catalog");
    if (
        !Array.isArray(next.materials) ||
        !next.armor ||
        !next.tools ||
        !Array.isArray(next.enchantments) ||
        !Array.isArray(next.potions)
    ) {
        throw new Error("Server version mismatch. Restart Spring Boot.");
    }
    catalog = next;
    catalogLoaded = true;
    renderCatalog();
}

async function loadWorkshop() {
    if (busy) return;
    busy = true;
    updateButtons();
    try {
        if (!catalogLoaded) await loadCatalog();
        await request("state");
        hidePreviews();
    } catch (error) {
        message(error.message, true);
    } finally {
        busy = false;
        updateButtons();
    }
}

// Register the handlers once; item buttons are handled through their data-action attribute.
document.addEventListener("click", handleClick);
document.addEventListener("change", handleChange);
getElement("#anvil-name").addEventListener("input", hidePreviews);
getElement("#anvil-units").addEventListener("input", hidePreviews);
getElement("#armor-form").addEventListener("submit", craftArmor);
getElement("#tools-form").addEventListener("submit", craftTool);
getElement("#enchant-form").addEventListener("submit", enchant);
getElement("#brew-form").addEventListener("submit", brew);
getElement("#anvil-form").addEventListener("submit", applyAnvil);
getElement("#enchant-preview").addEventListener("click", previewEnchant);
getElement("#brew-preview").addEventListener("click", previewBrew);
getElement("#anvil-preview").addEventListener("click", previewAnvil);
getElement("#refresh").addEventListener("click", loadWorkshop);
getElement("#reset").addEventListener("click", resetWorkshop);

renderCatalog();
updateAnvilMode();
setOptions("#enchant-item", [], "Connect to load items");
setOptions("#anvil-item", [], "Connect to load items");
setOptions("#anvil-sacrifice", [], "Choose a target item first");
loadWorkshop();
