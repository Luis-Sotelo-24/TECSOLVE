package com.Tecnologia.Controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControladorInicio {
    @GetMapping("/inicio")
    public String Inicio(Model modelo){
        return "Inicio";
    }
}
