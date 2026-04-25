/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Tecnologia.seguridad;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

/**
 *
 * @author Guillermo
 */
@Service
public class authService {

    public String encriptar(String clave) {
        return BCrypt.hashpw(clave, BCrypt.gensalt());
    }

    public boolean verificar(String claveIngresada, String hashGuardado) {
        return BCrypt.checkpw(claveIngresada, hashGuardado);
    }
    
}
