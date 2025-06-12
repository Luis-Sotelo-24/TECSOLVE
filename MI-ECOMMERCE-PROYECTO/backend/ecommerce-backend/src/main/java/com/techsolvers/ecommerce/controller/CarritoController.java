package com.techsolvers.ecommerce.controller;

import com.techsolvers.ecommerce.dto.CarritoDTO;
import com.techsolvers.ecommerce.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/carritos")
@CrossOrigin(origins = "*")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    // ENDPOINT DE PRUEBA
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "CarritoController funcionando correctamente");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    // OBTENER CARRITO ACTIVO DEL USUARIO
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> obtenerCarritoActivo(@PathVariable Long usuarioId) {
        try {
            CarritoDTO carrito = carritoService.obtenerCarritoActivo(usuarioId);
            return ResponseEntity.ok(carrito);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // AGREGAR PRODUCTO AL CARRITO
    @PostMapping("/agregar")
    public ResponseEntity<?> agregarProducto(@RequestParam Long usuarioId, 
                                           @RequestParam Long productoId, 
                                           @RequestParam Integer cantidad) {
        try {
            CarritoDTO carrito = carritoService.agregarProducto(usuarioId, productoId, cantidad);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producto agregado al carrito exitosamente");
            response.put("carrito", carrito);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ACTUALIZAR CANTIDAD DE PRODUCTO
    @PutMapping("/actualizar")
    public ResponseEntity<?> actualizarCantidad(@RequestParam Long usuarioId, 
                                              @RequestParam Long productoId, 
                                              @RequestParam Integer nuevaCantidad) {
        try {
            CarritoDTO carrito = carritoService.actualizarCantidad(usuarioId, productoId, nuevaCantidad);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cantidad actualizada exitosamente");
            response.put("carrito", carrito);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ELIMINAR PRODUCTO DEL CARRITO
    @DeleteMapping("/eliminar")
    public ResponseEntity<?> eliminarProducto(@RequestParam Long usuarioId, 
                                             @RequestParam Long productoId) {
        try {
            CarritoDTO carrito = carritoService.eliminarProducto(usuarioId, productoId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Producto eliminado del carrito exitosamente");
            response.put("carrito", carrito);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // VACIAR CARRITO
    @DeleteMapping("/vaciar/{usuarioId}")
    public ResponseEntity<?> vaciarCarrito(@PathVariable Long usuarioId) {
        try {
            CarritoDTO carrito = carritoService.vaciarCarrito(usuarioId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Carrito vaciado exitosamente");
            response.put("carrito", carrito);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // CREAR CARRITO PRESENCIAL
    @PostMapping("/presencial")
    public ResponseEntity<?> crearCarritoPresencial(@RequestParam Long usuarioId, 
                                                   @RequestParam Long trabajadorId) {
        try {
            CarritoDTO carrito = carritoService.crearCarritoPresencial(usuarioId, trabajadorId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Carrito presencial creado exitosamente");
            response.put("carrito", carrito);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER CARRITOS POR TRABAJADOR
    @GetMapping("/trabajador/{trabajadorId}")
    public ResponseEntity<?> obtenerCarritosPorTrabajador(@PathVariable Long trabajadorId) {
        try {
            List<CarritoDTO> carritos = carritoService.obtenerCarritosPorTrabajador(trabajadorId);
            return ResponseEntity.ok(Map.of("carritos", carritos));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER CARRITOS ABANDONADOS
    @GetMapping("/abandonados")
    public ResponseEntity<?> obtenerCarritosAbandonados(@RequestParam(defaultValue = "7") int diasLimite) {
        try {
            List<CarritoDTO> carritos = carritoService.obtenerCarritosAbandonados(diasLimite);
            return ResponseEntity.ok(Map.of("carritos", carritos));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ESTADÍSTICAS
    @GetMapping("/estadisticas")
    public ResponseEntity<?> estadisticas() {
        try {
            Map<String, Object> stats = carritoService.obtenerEstadisticas();
            return ResponseEntity.ok(Map.of("estadisticas", stats));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }
}






