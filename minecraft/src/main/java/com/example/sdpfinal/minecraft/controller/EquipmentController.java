package com.example.sdpfinal.minecraft.controller;

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

    @GetMapping("/diamond")
    public List<EquipmentItem> getDiamondSet() {
        return diamondFacade.craftFullDiamondSet();
    }

    @GetMapping("/netherite")
    public List<EquipmentItem> getNetheriteSet() {
        List<EquipmentItem> fullSet = new ArrayList<>();

        netheriteContext.setStrategy(netheriteArmorStrategy);
        fullSet.addAll(netheriteContext.executeStrategy());

        netheriteContext.setStrategy(netheriteToolStrategy);
        fullSet.addAll(netheriteContext.executeStrategy());

        return fullSet;
    }
}