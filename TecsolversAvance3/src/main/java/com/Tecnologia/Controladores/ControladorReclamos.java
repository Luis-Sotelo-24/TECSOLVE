package com.Tecnologia.Controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControladorReclamos {
    
    @GetMapping("/reclamos")
    public String Reclamos(Model modelo){
        return "FrmReclamos";
    }
}
