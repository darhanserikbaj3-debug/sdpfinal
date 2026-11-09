package com.example.sdpfinal.minecraft;

import com.example.sdpfinal.minecraft.builder.EquipmentDirector;
import com.example.sdpfinal.minecraft.builder.EquipmentObjectBuilder;
import com.example.sdpfinal.minecraft.builder.IronEquipment;
import com.example.sdpfinal.minecraft.facade.DiamondEquipmentFacade;
import com.example.sdpfinal.minecraft.model.EquipmentItem;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class MinecraftApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinecraftApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(DiamondEquipmentFacade diamondFacade) {
        return args -> {
            System.out.println("\n--- TESTING DIAMOND FACADE PATTERN ---");
            
            List<EquipmentItem> diamondSet = diamondFacade.craftFullDiamondSet();

            System.out.println("\n--- RESULTING INVENTORY (" + diamondSet.size() + " items) ---");
            for (EquipmentItem item : diamondSet) {
                System.out.println("- " + item.getName() + " [" + item.getType() + "] (Durability: " + item.getDurability() + ")");
            }
            System.out.println("--------------------------------------\n");

            System.out.println("--- TESTING IRON BUILDER PATTERN ---");

            EquipmentDirector director = new EquipmentDirector();
            EquipmentObjectBuilder builder = new EquipmentObjectBuilder();

            director.makeFullIronSet(builder);
            IronEquipment ironSet = builder.getResult();

            System.out.println("\n--- RESULTING IRON EQUIPMENT OBJECT ---");
            System.out.println(ironSet);
            System.out.println("--------------------------------------\n");
        };
    }
}