package com.Tecnologia.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackupService {

    public static void hacerBackup() {
        String fecha = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String backupPath = "C:/Proyecto/TECSOLVE/backups/backup_" + fecha + ".sql";
        String mysqldumpPath = "C:/xampp/mysql/bin/mysqldump.exe";

        ProcessBuilder builder = new ProcessBuilder(
                mysqldumpPath,
                "-u", "root",// User
               // "-pCONTRA", // Contraseña
                "tecnologia",// Nombre BD
                "-r", backupPath
        );

        try {
            Process proceso = builder.start();

            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(proceso.getErrorStream()));

            String linea;
            StringBuilder errorOutput = new StringBuilder();
            while ((linea = errorReader.readLine()) != null) {
                errorOutput.append(linea).append("\n");
            }

            int resultado = proceso.waitFor();

            if (resultado == 0) {
                System.out.println(" Backup creado correctamente en: " + backupPath);
            } else {
                System.err.println(" Error al crear backup. Código: " + resultado);
                System.err.println(" Mensaje de error:\n" + errorOutput);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}