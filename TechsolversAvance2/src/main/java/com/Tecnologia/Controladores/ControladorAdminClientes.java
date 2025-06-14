package com.Tecnologia.Controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControladorAdminClientes {
    @GetMapping("/adminclientes")
    public String AdminClientes(Model modelo){
        return "Adminclientes";
    }
}
