/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package seguridad;

import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Guillermo
 */
public class authService {

    public static String encriptar(String clave) {
        return BCrypt.hashpw(clave, BCrypt.gensalt());
    }

    public static boolean verificar(String claveIngresada, String hashGuardado) {
        return BCrypt.checkpw(claveIngresada, hashGuardado);
    }
}
