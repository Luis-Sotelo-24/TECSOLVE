
package com.techsolvers.ecommerce.controller;

import com.techsolvers.ecommerce.dto.ProductoDTO;
import com.techsolvers.ecommerce.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // ENDPOINT DE PRUEBA
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "ProductoController funcionando correctamente");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    // CREAR NUEVO PRODUCTO
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ProductoDTO productoDTO, 
                                  @RequestParam Long categoriaId, 
                                  @RequestParam Long creadoPorId) {
        try {
            ProductoDTO productoNuevo = productoService.crear(productoDTO, categoriaId, creadoPorId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producto creado exitosamente");
            response.put("producto", productoNuevo);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // LISTAR PRODUCTOS ACTIVOS
    @GetMapping
    public ResponseEntity<?> listarActivos() {
        try {
            List<ProductoDTO> productos = productoService.listarActivos();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("productos", productos);
            response.put("total", productos.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // LISTAR PRODUCTOS DESTACADOS
    @GetMapping("/destacados")
    public ResponseEntity<?> listarDestacados() {
        try {
            List<ProductoDTO> productos = productoService.listarDestacados();
            return ResponseEntity.ok(Map.of("productos", productos));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER PRODUCTO POR ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            ProductoDTO producto = productoService.obtenerPorId(id);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.status(404)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER PRODUCTOS POR CATEGORÍA
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<?> obtenerPorCategoria(@PathVariable Long categoriaId) {
        try {
            List<ProductoDTO> productos = productoService.obtenerPorCategoria(categoriaId);
            return ResponseEntity.ok(Map.of("productos", productos));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // BUSCAR PRODUCTOS
    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(@RequestParam String q) {
        try {
            List<ProductoDTO> productos = productoService.buscarProductos(q);
            return ResponseEntity.ok(Map.of("productos", productos));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // BUSCAR POR RANGO DE PRECIOS
    @GetMapping("/precio")
    public ResponseEntity<?> buscarPorPrecio(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        try {
            List<ProductoDTO> productos = productoService.buscarPorPrecio(min, max);
            return ResponseEntity.ok(Map.of("productos", productos));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ACTUALIZAR PRODUCTO
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ProductoDTO productoDTO) {
        try {
            ProductoDTO productoActualizado = productoService.actualizar(id, productoDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producto actualizado exitosamente");
            response.put("producto", productoActualizado);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ELIMINAR PRODUCTO
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = productoService.eliminar(id);
            if (eliminado) {
                return ResponseEntity.ok(Map.of("message", "Producto eliminado exitosamente"));
            } else {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "No se pudo eliminar el producto"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ACTUALIZAR STOCK
    @PutMapping("/{id}/stock")
    public ResponseEntity<?> actualizarStock(@PathVariable Long id, @RequestParam Integer nuevoStock) {
        try {
            ProductoDTO productoActualizado = productoService.actualizarStock(id, nuevoStock);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Stock actualizado exitosamente");
            response.put("producto", productoActualizado);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER PRODUCTOS CON STOCK BAJO
    @GetMapping("/stock-bajo")
    public ResponseEntity<?> obtenerProductosConStockBajo() {
        try {
            List<ProductoDTO> productos = productoService.obtenerProductosConStockBajo();
            return ResponseEntity.ok(Map.of("productos", productos));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // VERIFICAR DISPONIBILIDAD
    @GetMapping("/{id}/disponibilidad")
    public ResponseEntity<?> verificarDisponibilidad(@PathVariable Long id, @RequestParam Integer cantidad) {
        try {
            boolean disponible = productoService.verificarDisponibilidad(id, cantidad);
            
            Map<String, Object> response = new HashMap<>();
            response.put("disponible", disponible);
            response.put("productoId", id);
            response.put("cantidadSolicitada", cantidad);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ESTADÍSTICAS
    @GetMapping("/estadisticas")
    public ResponseEntity<?> estadisticas() {
        try {
            Map<String, Object> stats = productoService.obtenerEstadisticas();
            return ResponseEntity.ok(Map.of("estadisticas", stats));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }
}


