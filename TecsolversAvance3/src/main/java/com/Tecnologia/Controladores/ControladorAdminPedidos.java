package com.Tecnologia.Controladores;

import com.Tecnologia.Modelo.Pedido;
import com.Tecnologia.Repositorio.ClientesRepositorio;
import com.Tecnologia.Repositorio.PedidoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/adminventas")
public class ControladorAdminPedidos {

    @Autowired
    private PedidoRepositorio pedidoRepository;

    @Autowired
    private ClientesRepositorio clienteRepository;

    @GetMapping
    public String listarPedidos(Model model) {
        model.addAttribute("pedidos", pedidoRepository.findAll());
        return "AdminPedidos";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("pedido", new Pedido());
        model.addAttribute("clientes", clienteRepository.findAll());
        return "FormularioPedido";
    }

    @PostMapping("/guardar")
    public String guardarPedido(@ModelAttribute Pedido pedido) {
        pedidoRepository.save(pedido);
        return "redirect:/adminventas";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("ID no válido: " + id));
        model.addAttribute("pedido", pedido);
        model.addAttribute("clientes", clienteRepository.findAll());
        return "FormularioPedido";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPedido(@PathVariable Long id) {
        pedidoRepository.deleteById(id);
        return "redirect:/adminventas";
    }
}
