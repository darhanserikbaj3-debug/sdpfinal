package com.example.sdpfinal.minecraft.factorymethod;

import com.example.sdpfinal.minecraft.model.*;
import java.util.*;

public abstract class ToolWorkshop {

    protected abstract Tool createTool(Material material);

    public final Item craft(Material material) {
        if (material == null) {
            throw new WorkshopException("Choose a material");
        }
        return createTool(material).item();
    }

    public final Map<String, Integer> harvest(Item item) {
        return createTool(item.material()).harvest(item);
    }

    public final String action(Material material) {
        return createTool(material).action();
    }

    public static ToolWorkshop forKind(String kind) {
        if (kind == null) {
            throw new WorkshopException("Choose a tool");
        }
        if ("pickaxe".equals(kind)) {
            return new PickaxeWorkshop();
        }
        if ("axe".equals(kind)) {
            return new AxeWorkshop();
        }
        if ("shovel".equals(kind)) {
            return new ShovelWorkshop();
        }
        throw new WorkshopException("Unknown tool");
    }

    public static int cost(String kind) {
        if ("pickaxe".equals(kind) || "axe".equals(kind)) {
            return 3;
        }
        if ("shovel".equals(kind)) {
            return 1;
        }
        throw new WorkshopException("Unknown tool");
    }
}
