package com.Tecnologia.Controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControladorAdminReclamos {
    @GetMapping("/adminreclamos")
    public String AdminReclamos(Model modelo){
        return "Adminreclamos";
    }
}
