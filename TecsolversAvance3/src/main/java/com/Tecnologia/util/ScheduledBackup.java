package com.Tecnologia.util;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledBackup {

    @Scheduled(cron = "*/10 * * * * *")
    public void respaldoProgramado() {
        System.out.println(" Ejecutando backup automático...");
        BackupService.hacerBackup();
    }
}