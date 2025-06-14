package com.Tecnologia.Controladores;

import com.Tecnologia.Modelo.Clientes;
import com.Tecnologia.Repositorio.ClientesRepositorio;
import com.Tecnologia.seguridad.authService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class ControladorLogin {

    @Autowired
    private ClientesRepositorio clientesRepo;

    
    @GetMapping("/login")
    public String Login(Model modelo) {
        return "FrmLogin";  
    }

    
@PostMapping("/login")
@ResponseBody
public String procesarLogin(@RequestParam String correo, @RequestParam String contraseña, HttpSession session) {
    Optional<Clientes> clienteOpt = clientesRepo.findByCorreo(correo);
    if (clienteOpt.isPresent()) {
        Clientes cliente = clienteOpt.get();
        if (authService.verificar(contraseña, cliente.getContraseña())) {
            // Guardar el objeto completo del cliente en sesión
            session.setAttribute("cliente", cliente);

            // Guardar nombre en sesión si es admin
            if (correo.equals("admin@admin.com")) {
                session.setAttribute("nombreUsuario", cliente.getNombreCli());  // <-- Aquí
                return "ok_admin";
            }

            // Para usuarios normales no se modifica nada
            return "ok_user";
        }
    }
    return "error";
}
    
    
    
   
    @GetMapping("/home")
    public String paginaPrincipal() {
        return "home"; 
    }
    
     // Cerrar sesión
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();  
        return "redirect:/login";  
    }

   
}