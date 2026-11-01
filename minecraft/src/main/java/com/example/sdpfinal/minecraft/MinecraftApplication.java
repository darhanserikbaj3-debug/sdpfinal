package com.example.sdpfinal.minecraft;

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
            System.out.println("\n--- STARTING FACADE PATTERN DEMO ---");
            
            // Single clean call to the Facade
            List<EquipmentItem> diamondSet = diamondFacade.craftFullDiamondSet();

            System.out.println("\n--- RESULTING INVENTORY (" + diamondSet.size() + " items) ---");
            diamondSet.forEach(System.out::println);
        };
    }
}