package com.example.sdpfinal.minecraft.controller;

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

    private final DiamondEquipmentFacade diamondFacade;
    private final NetheriteContext netheriteContext;
    private final NetheriteArmorStrategy netheriteArmorStrategy;
    private final NetheriteToolStrategy netheriteToolStrategy;

    public EquipmentController(DiamondEquipmentFacade diamondFacade,
                               NetheriteContext netheriteContext,
                               NetheriteArmorStrategy netheriteArmorStrategy,
                               NetheriteToolStrategy netheriteToolStrategy) {
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

    // 3. Builder Pattern 
    @GetMapping("/iron")
    public List<EquipmentItem> getIronSet() {
        EquipmentDirector director = new EquipmentDirector();
        EquipmentObjectBuilder builder = new EquipmentObjectBuilder();

        director.makeFullIronSet(builder);
        IronEquipment ironObj = builder.getResult(); 

        List<EquipmentItem> items = new ArrayList<>();
        items.add(new EquipmentItem("Iron Helmet", 250, "ARMOR", 5));
        items.add(new EquipmentItem("Iron Chestplate", 250, "ARMOR", 8));
        items.add(new EquipmentItem("Iron Leggings", 250, "ARMOR", 7));
        items.add(new EquipmentItem("Iron Boots", 250, "ARMOR", 4));
        items.add(new EquipmentItem("Iron Sword", 250, "TOOL", 2));
        items.add(new EquipmentItem("Iron Pickaxe", 250, "TOOL", 3));
        items.add(new EquipmentItem("Iron Shovel", 250, "TOOL", 1));

        return items;
    }
}