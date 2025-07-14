package com.Tecnologia.Controladores;

import com.Tecnologia.Modelo.Carrito;
import com.Tecnologia.Modelo.Clientes;
import com.Tecnologia.Modelo.Item;
import com.Tecnologia.Modelo.Pedido;
import com.Tecnologia.Modelo.PedidoItem;
import com.Tecnologia.Modelo.Producto;
import com.Tecnologia.Repositorio.PedidoItemRepositorio;
import com.Tecnologia.Repositorio.PedidoRepositorio;
import com.Tecnologia.dao.ProductoRepositorio;
import com.Tecnologia.dto.PedidoItemDTO;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControladorPedido {

    @Autowired
    private PedidoRepositorio pedidoRepository;

    @Autowired
    private PedidoItemRepositorio pedidoItemRepository;

    @Autowired
    private ProductoRepositorio productoRepository;

    @PostMapping("/pedido")
    public String procesarPedido(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Carrito carrito = (Carrito) session.getAttribute("carrito");
        Clientes cliente = (Clientes) session.getAttribute("cliente");

        if (carrito == null || carrito.getItems().isEmpty() || cliente == null) {
            return "redirect:/carritocompras";
        }

        // Crear el pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado("pendiente");

        List<PedidoItem> items = new ArrayList<>();
        BigDecimal totalPedido = BigDecimal.ZERO;

        for (Item item : carrito.getItems()) {
            Producto producto = productoRepository.findById(item.getId()).orElseThrow();

            // Verificar stock antes de procesar
            if (producto.getStock() < item.getCantidad()) {
                redirectAttributes.addFlashAttribute("error", "No hay suficiente stock para el producto: " + producto.getNombre() + ". Stock disponible: " + producto.getStock());
                return "redirect:/carritocompras";
            }

            PedidoItem pedidoItem = new PedidoItem();
            pedidoItem.setPedido(pedido);
            pedidoItem.setProducto(producto);
            pedidoItem.setCantidad(item.getCantidad());
            pedidoItem.setPrecioUnitario(BigDecimal.valueOf(producto.getPrecio()));
            BigDecimal totalItem = BigDecimal.valueOf(producto.getPrecio())
                    .multiply(BigDecimal.valueOf(item.getCantidad()));
            pedidoItem.setTotal(totalItem);

            producto.setStock(producto.getStock() - pedidoItem.getCantidad());

            items.add(pedidoItem);
            totalPedido = totalPedido.add(totalItem);
        }

        pedido.setItems(items);
        pedido.setTotal(totalPedido);

        pedidoRepository.save(pedido);

        // Agregar datos al modelo para el resumen
        model.addAttribute("items", carrito.getItems());
        model.addAttribute("total", carrito.getTotal());

        // Vaciar el carrito
        carrito.setItems(new ArrayList<>());
        session.setAttribute("carrito", carrito);

        return "Pedido"; // muestra la vista resumen o éxito
    }

    @GetMapping("/listarPedido")
    public String listarPedidos(HttpSession session, Model model) {
        Clientes cliente = (Clientes) session.getAttribute("cliente");
        if (cliente != null) {
            List<Pedido> pedidos = pedidoRepository.findByClienteIdCli(cliente.getIdCli());
            model.addAttribute("listaPedidos", pedidos);
            return "PedidoResumen";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/detallePedido/{id}")
    @ResponseBody
    public List<PedidoItemDTO> obtenerDetallePedido(@PathVariable Long id) {
        List<PedidoItem> pedidoItems = pedidoItemRepository.findByPedidoIdPedido(id);
        List<PedidoItemDTO> dtoList = pedidoItems.stream()
                .map(item -> new PedidoItemDTO(
                item.getProducto().getNombre(), // Ajusta según tu modelo
                item.getCantidad(),
                item.getPrecioUnitario()
        ))
                .toList();

        return dtoList;
    }

}
