package com.example.sdpfinal.minecraft.controller;

import com.example.sdpfinal.minecraft.adapter.GoldEquipment;
import com.example.sdpfinal.minecraft.adapter.GoldEquipmentTarget;
import com.example.sdpfinal.minecraft.builder.EquipmentDirector;
import com.example.sdpfinal.minecraft.builder.EquipmentObjectBuilder;
import com.example.sdpfinal.minecraft.builder.IronEquipment;
import com.example.sdpfinal.minecraft.facade.DiamondEquipmentFacade;
import com.example.sdpfinal.minecraft.model.EquipmentItem;
import com.example.sdpfinal.minecraft.strategy.NetheriteArmorStrategy;
import com.example.sdpfinal.minecraft.strategy.NetheriteContext;
import com.example.sdpfinal.minecraft.strategy.NetheriteToolStrategy;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@CrossOrigin(origins = "*")
public class EquipmentController {

    private final GoldEquipmentTarget goldAdapter;
    private final DiamondEquipmentFacade diamondFacade;
    private final NetheriteContext netheriteContext;
    private final NetheriteArmorStrategy netheriteArmorStrategy;
    private final NetheriteToolStrategy netheriteToolStrategy;

    public EquipmentController(GoldEquipmentTarget goldAdapter,
                               DiamondEquipmentFacade diamondFacade,
                               NetheriteContext netheriteContext,
                               NetheriteArmorStrategy netheriteArmorStrategy,
                               NetheriteToolStrategy netheriteToolStrategy) {
        this.goldAdapter = goldAdapter;
        this.diamondFacade = diamondFacade;
        this.netheriteContext = netheriteContext;
        this.netheriteArmorStrategy = netheriteArmorStrategy;
        this.netheriteToolStrategy = netheriteToolStrategy;
    }

    // 1. Facade Pattern -> Diamond
    @GetMapping("/diamond")
    public List<EquipmentItem> getDiamondSet() {
        return diamondFacade.craftFullDiamondSet();
    }

    // 2. Strategy Pattern -> Netherite
    @GetMapping("/netherite")
    public List<EquipmentItem> getNetheriteSet() {
        List<EquipmentItem> fullSet = new ArrayList<>();

        netheriteContext.setStrategy(netheriteArmorStrategy);
        fullSet.addAll(netheriteContext.executeStrategy());

        netheriteContext.setStrategy(netheriteToolStrategy);
        fullSet.addAll(netheriteContext.executeStrategy());

        return fullSet;
    }

    // Adapter Pattern -> Gold
    @GetMapping("/gold")
    public List<EquipmentItem> getGoldSet() {
        GoldEquipment goldSet = goldAdapter.craftFullGoldSet();
        return List.of(
                new EquipmentItem(goldSet.getHelmet(), 32, "ARMOR", 5),
                new EquipmentItem(goldSet.getChestplate(), 32, "ARMOR", 8),
                new EquipmentItem(goldSet.getLeggings(), 32, "ARMOR", 7),
                new EquipmentItem(goldSet.getBoots(), 32, "ARMOR", 4),
                new EquipmentItem(goldSet.getAxe(), 32, "TOOL", 3),
                new EquipmentItem(goldSet.getSword(), 32, "TOOL", 2),
                new EquipmentItem(goldSet.getPickaxe(), 32, "TOOL", 3)
        );
    }

    // 3. Builder Pattern 
    @GetMapping("/iron")
    public List<EquipmentItem> getIronSet() {
        EquipmentDirector director = new EquipmentDirector();
        EquipmentObjectBuilder builder = new EquipmentObjectBuilder();

        director.makeFullIronSet(builder);
        IronEquipment ironObj = builder.getResult(); 

        List<EquipmentItem> items = new ArrayList<>();
        items.add(new EquipmentItem(ironObj.getHelmet(), 250, "ARMOR", 5));
        items.add(new EquipmentItem(ironObj.getChestplate(), 250, "ARMOR", 8));
        items.add(new EquipmentItem(ironObj.getLeggings(), 250, "ARMOR", 7));
        items.add(new EquipmentItem(ironObj.getBoots(), 250, "ARMOR", 4));
        items.add(new EquipmentItem(ironObj.getSword(), 250, "TOOL", 2));
        items.add(new EquipmentItem(ironObj.getPickaxe(), 250, "TOOL", 3));
        items.add(new EquipmentItem(ironObj.getShovel(), 250, "TOOL", 1));

        return items;
    }
}
