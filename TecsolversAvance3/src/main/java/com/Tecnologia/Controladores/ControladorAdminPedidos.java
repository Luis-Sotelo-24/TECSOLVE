package com.Tecnologia.Controladores;

import com.Tecnologia.Modelo.Clientes;
import com.Tecnologia.Modelo.Pedido;
import com.Tecnologia.Repositorio.ClientesRepositorio;
import com.Tecnologia.Repositorio.PedidoRepositorio;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    public String listarPedidosFull(HttpSession session, Model model) {
        Clientes cliente = (Clientes) session.getAttribute("cliente");
        if (cliente != null) {
            List<Pedido> pedidos = pedidoRepository.findAll();
            model.addAttribute("listaPedidosFull", pedidos);
            return "AdminPedidos";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute Pedido pedidoForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pedido", pedidoForm);
            model.addAttribute("clientes", clienteRepository.findAll());
            model.addAttribute("modalError", true);
            return "adminproductos";
        }

        Pedido pedidoOriginal = pedidoRepository.findById(pedidoForm.getIdPedido())
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + pedidoForm.getIdPedido()));

        pedidoOriginal.setEstado(pedidoForm.getEstado());

        pedidoRepository.save(pedidoOriginal);

        return "redirect:/adminventas";
    }

    @GetMapping("/editar/{id}")
    public String editarProducto(@PathVariable Long id, Model model) {
        model.addAttribute("pedido", pedidoRepository.findById(id));
        return "adminproductos_form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPedido(@PathVariable Long id) {
        pedidoRepository.deleteById(id);
        return "redirect:/adminventas";
    }
}
