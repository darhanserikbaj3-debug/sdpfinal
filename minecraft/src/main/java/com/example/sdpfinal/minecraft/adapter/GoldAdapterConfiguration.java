package com.example.sdpfinal.minecraft.adapter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoldAdapterConfiguration {
    @Bean
    public GoldEquipmentTarget goldEquipmentTarget() {
        return new GoldEquipmentAdapter(new LegacyGoldForge());
    }
}
