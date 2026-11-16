package com.example.servigohex.infrastructure.scheduler;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component

public class CategoryScheduler {

    @Scheduled(cron = "0 * * * * *")
    public void generarReporte() {

        System.out.println("\n==================================");
        System.out.println("SERVIGO - MONITOREO AUTOMATICO");
        System.out.println("Fecha: " + LocalDateTime.now());
        System.out.println("Verificacion automatica del sistema ejecutada");
        System.out.println("==================================\n");

    }

}
