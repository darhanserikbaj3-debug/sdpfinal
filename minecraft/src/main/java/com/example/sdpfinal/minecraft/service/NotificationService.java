package com.example.sdpfinal.minecraft.service;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void notifyPlayer(String message) {
        System.out.println("[NotificationService] Игроку: " + message);
    }
}