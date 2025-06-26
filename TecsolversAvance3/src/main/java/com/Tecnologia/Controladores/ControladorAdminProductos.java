package com.Tecnologia.Controladores;

import com.Tecnologia.Modelo.Producto;
import com.Tecnologia.Servicios.ProductoServicio;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.validation.BindingResult;

@Controller
public class ControladorAdminProductos {

    @Autowired
    private ProductoServicio productoServicio;

    @GetMapping("/adminproductos")
    public String listarProductos(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String categoria,
            Model model) {

        List<Producto> productos;

        boolean hayCategoria = categoria != null && !categoria.isEmpty() && !categoria.equals("--Todas--");
        boolean hayBusqueda = search != null && !search.isEmpty();

        if (hayCategoria && hayBusqueda) {
            productos = productoServicio.buscarPorTextoYCategoria(search, categoria);
        } else if (hayCategoria) {
            productos = productoServicio.getByCategoria(categoria);
        } else if (hayBusqueda) {
            productos = productoServicio.buscar(search);
        } else {
            productos = productoServicio.get();
        }

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", productoServicio.get().stream().map(Producto::getCategoria).distinct().toList());
        model.addAttribute("categoriaSeleccionada", categoria);
        model.addAttribute("search", search);
        // Total productos
        long totalProductos = productos.size();
        model.addAttribute("totalProductos", totalProductos);

        // Total categorías
        long totalCategorias = productos.stream()
                .map(Producto::getCategoria)
                .filter(c -> c != null && !c.trim().isEmpty())
                .distinct()
                .count();
        model.addAttribute("totalCategorias", totalCategorias);

        // Productos sin stock
        long productosSinStock = productos.stream()
                .filter(p -> p.getStock() == 0)
                .count();
        model.addAttribute("productosSinStock", productosSinStock);

        // Productos con stock bajo
        long productosStockBajo = productos.stream()
                .filter(p -> p.getStock() > 0 && p.getStock() <= 10)
                .count();
        model.addAttribute("productosStockBajo", productosStockBajo);

        return "adminproductos";
    }

    @PostMapping("/adminproductos/guardar")
    public String guardarProducto(@Valid @ModelAttribute Producto producto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("producto", producto);
            model.addAttribute("modalError", true);  // abrir modal en la vista
            return "adminproductos";
        }
        productoServicio.save(producto);
        return "redirect:/adminproductos";
    }

    @GetMapping("/adminproductos/editar/{id}")
    public String editarProducto(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoServicio.get(id));
        return "adminproductos_form";
    }

    @GetMapping("/adminproductos/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        productoServicio.delete(id);
        return "redirect:/adminproductos";
    }

    @GetMapping("/listaaudifonos")
    public String ListaKekes(Model modelo) {
        List<Producto> lista = productoServicio.getByCategoria("Audifonos");
        modelo.addAttribute("listaproductos", lista);
        return "ListaAudifonos";
    }

    @GetMapping("/listasmartphones")
    public String ListaPanes(Model modelo) {
        List<Producto> lista = productoServicio.getByCategoria("Smartphones");
        modelo.addAttribute("listaproductos", lista);
        return "ListaSmartphones";
    }

    @GetMapping("/listatv")
    public String ListaTortas(Model modelo) {
        List<Producto> lista = productoServicio.getByCategoria("Tv");
        modelo.addAttribute("listaproductos", lista);
        return "ListaTv";
    }

    @GetMapping("/listasmartwatch")
    public String ListaBocaditos(Model modelo) {
        List<Producto> lista = productoServicio.getByCategoria("Smart Watch");
        modelo.addAttribute("listaproductos", lista);
        return "ListaSmartWatch";
    }

    @GetMapping("/listacomputadoras")
    public String ListaSalados(Model modelo) {
        List<Producto> lista = productoServicio.getByCategoria("Computadoras");
        modelo.addAttribute("listaproductos", lista);
        return "ListaComputadoras";
    }
    
    @GetMapping("/exportarProductosExcel")
    public void exportarProductosExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=productos.xlsx");

        List<Producto> producto = productoServicio.get();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Pedidos");

        // Cabecera
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Nombre");
        headerRow.createCell(2).setCellValue("Descripcion");
        headerRow.createCell(3).setCellValue("Precio");
        headerRow.createCell(4).setCellValue("Stock");
        headerRow.createCell(5).setCellValue("Categoria");

        // Datos
        int rowNum = 1;
        for (Producto productos : producto) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(productos.getId_prod().toString());
            row.createCell(1).setCellValue(productos.getNombre());
            row.createCell(2).setCellValue(productos.getDescripcion());
            row.createCell(3).setCellValue(productos.getPrecio().toString());
            row.createCell(4).setCellValue(productos.getStock().toString());
            row.createCell(5).setCellValue(productos.getCategoria());
        }

        // Ajustar columnas
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }

        // Escribir al response
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
