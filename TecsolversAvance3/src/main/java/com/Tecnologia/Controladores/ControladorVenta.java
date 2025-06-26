package com.Tecnologia.Controladores;

import com.Tecnologia.Modelo.Carrito;
import com.Tecnologia.Modelo.Clientes;
import com.Tecnologia.Modelo.PedidoForm;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControladorVenta {

    @GetMapping("/cuestionario")
    public String mostrarFormularioCompra(Model model, HttpSession session) {
        Clientes cliente = (Clientes) session.getAttribute("cliente");
        if (cliente != null) {
            model.addAttribute("nombreCli", cliente.getNombreCli());
            model.addAttribute("apellidosCli", cliente.getApellidosCli());
            model.addAttribute("dni", cliente.getDni());
            model.addAttribute("direccion", cliente.getDireccion());
            model.addAttribute("telefono", cliente.getTelefono());
            model.addAttribute("correo", cliente.getCorreo());
        }

        Carrito carrito = (Carrito) session.getAttribute("carrito");
        if (carrito != null) {
            model.addAttribute("carrito", carrito.getItems());
            model.addAttribute("total", carrito.getTotal());
        } else {
            model.addAttribute("carrito", null);
            model.addAttribute("total", 0.0);
        }

        // Agregamos formulario vacío para el binding
        model.addAttribute("pedidoForm", new PedidoForm());

        return "Cuestionario";
    }
}
