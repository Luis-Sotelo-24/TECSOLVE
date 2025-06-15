
package com.Tecnologia.Controladores;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class ControladorGuiaPedido {
    @GetMapping("/Pedido")
    public String Admin(Model modelo){
        return "GuiaPedido";
    }
}

