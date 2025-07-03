/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Tecnologia.Controladores;

import com.Tecnologia.Modelo.Clientes;
import com.Tecnologia.Modelo.Reclamo;
import com.Tecnologia.Repositorio.ReclamosRepositorio;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author Guillermo
 */
@Controller
public class ControladorReclamo {

    @Autowired
    public ReclamosRepositorio reclamoRepositorio;

    @PostMapping("/reclamo/guardar")
    public String procesarPedido(HttpSession session, @RequestParam String detalle, Model model) {
        Clientes cliente = (Clientes) session.getAttribute("cliente");
        // Crear el pedido
        Reclamo reclamo = new Reclamo();
        reclamo.setCliente(cliente);
        reclamo.setDetalle(detalle);
        reclamo.setEstado("Pendiente");
        reclamo.setRespuesta("Pendiente de respuesta");

        reclamoRepositorio.save(reclamo);

        return "redirect:/reclamo"; // muestra la vista resumen o éxito
    }

    @GetMapping("/reclamo")
    public String listarReclamo(HttpSession session, Model model) {
        Clientes cliente = (Clientes) session.getAttribute("cliente");
        if (cliente != null) {
            List<Reclamo> reclamos = reclamoRepositorio.findByClienteIdCli(cliente.getIdCli());
            System.out.println("Cantidad de reclamos encontrados: " + reclamos.size()); // debug aquí
            model.addAttribute("listaReclamos", reclamos);
            model.addAttribute("cliente", cliente);
            return "ReclamoResumen";
        } else {
            return "redirect:/login";
        }
    }

}
