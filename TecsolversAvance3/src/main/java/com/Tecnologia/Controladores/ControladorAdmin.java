package com.Tecnologia.Controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControladorAdmin {
    @GetMapping("/admin")
    public String Admin(Model modelo){
        return "Admin";
    }
}
