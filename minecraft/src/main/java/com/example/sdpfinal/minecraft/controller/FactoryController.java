package com.example.sdpfinal.minecraft.controller;

import com.example.sdpfinal.minecraft.abstractfactory.EquipmentFactory;
import com.example.sdpfinal.minecraft.abstractfactory.CopperEquipmentFactory;
import com.example.sdpfinal.minecraft.abstractfactory.StoneEquipmentFactory;
import com.example.sdpfinal.minecraft.abstractfactory.WoodEquipmentFactory;
import com.example.sdpfinal.minecraft.factorymethod.StoneToolFactory;
import com.example.sdpfinal.minecraft.factorymethod.ToolFactory;
import com.example.sdpfinal.minecraft.factorymethod.WoodToolFactory;
import com.example.sdpfinal.minecraft.model.EquipmentItem;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/equipment")
@CrossOrigin(origins = "*")
public class FactoryController {

    @GetMapping("/abstractfactory")
    public List<EquipmentItem> getAbstractFactorySet(@RequestParam(defaultValue = "copper") String material) {
        EquipmentFactory factory = switch (material) {
            case "copper" -> new CopperEquipmentFactory();
            case "wood" -> new WoodEquipmentFactory();
            case "stone" -> new StoneEquipmentFactory();
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported equipment material");
        };
        List<EquipmentItem> items = new ArrayList<>();
        if (material.equals("copper")) {
            items.addAll(factory.createArmorSet());
        }
        items.addAll(factory.createTools());
        return items;
    }

    @GetMapping("/factorymethod")
    public List<EquipmentItem> getFactoryMethodTool(@RequestParam(defaultValue = "wood") String material) {
        ToolFactory factory = switch (material) {
            case "wood" -> new WoodToolFactory();
            case "stone" -> new StoneToolFactory();
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported tool material");
        };
        return factory.createToolSet();
    }
}
