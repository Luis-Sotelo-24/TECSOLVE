package com.Tecnologia.Controladores;

import com.Tecnologia.Modelo.Clientes;
import com.Tecnologia.Modelo.Reclamo;
import com.Tecnologia.Repositorio.ClientesRepositorio;
import com.Tecnologia.Repositorio.ReclamosRepositorio;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ControladorAdminReclamos {

    @Autowired
    public ReclamosRepositorio reclamoRepositorio;

    @Autowired
    public ClientesRepositorio clienteRepositorio;

    @GetMapping("/adminreclamos")
    public String listarPedidosFull(@RequestParam(value = "estado", required = false) String estado, Model model) {
        List<Reclamo> reclamos;

        if (estado == null || estado.isEmpty()) {
            reclamos = reclamoRepositorio.findAll();
        } else {
            reclamos = reclamoRepositorio.findByEstado(estado);
        }

        model.addAttribute("listaReclamosFull", reclamos);
        model.addAttribute("estadoSeleccionado", estado); // para mantener seleccionado el filtro

        return "AdminReclamos";
    }

    @PostMapping("/reclamoAdm/guardar")
    public String guardarPedidos(@ModelAttribute Reclamo reclamoForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("reclamo", reclamoForm);
            model.addAttribute("clientes", clienteRepositorio.findAll());
            model.addAttribute("modalError", true);
            return "adminproductos";
        }

        Reclamo reclamoOriginal = reclamoRepositorio.findById(reclamoForm.getIdReclamo())
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + reclamoForm.getIdReclamo()));

        reclamoOriginal.setEstado("Respondido");
        reclamoOriginal.setRespuesta(reclamoForm.getRespuesta());

        reclamoRepositorio.save(reclamoOriginal);

        return "redirect:/adminreclamos";
    }

    @GetMapping("/editar/{id}")
    public String editarProducto(@PathVariable Long id, Model model) {
        model.addAttribute("reclamo", reclamoRepositorio.findById(id));
        return "adminproductos_form";
    }
}
