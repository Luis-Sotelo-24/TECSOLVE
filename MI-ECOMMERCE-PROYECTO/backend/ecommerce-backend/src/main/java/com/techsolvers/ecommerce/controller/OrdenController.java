package com.techsolvers.ecommerce.controller;

import com.techsolvers.ecommerce.dto.OrdenDTO;
import com.techsolvers.ecommerce.service.OrdenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ordenes")
@CrossOrigin(origins = "*")
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    // ENDPOINT DE PRUEBA
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "OrdenController funcionando correctamente");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    // CREAR ORDEN DESDE CARRITO
    @PostMapping("/crear")
    public ResponseEntity<?> crearOrdenDesdeCarrito(@RequestParam Long carritoId, 
                                                   @RequestParam String metodoPago) {
        try {
            OrdenDTO orden = ordenService.crearOrdenDesdeCarrito(carritoId, metodoPago);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Orden creada exitosamente");
            response.put("orden", orden);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER ORDEN POR ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            OrdenDTO orden = ordenService.obtenerPorId(id);
            return ResponseEntity.ok(orden);
        } catch (Exception e) {
            return ResponseEntity.status(404)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER ORDEN POR NÚMERO
    @GetMapping("/numero/{numeroOrden}")
    public ResponseEntity<?> obtenerPorNumero(@PathVariable String numeroOrden) {
        try {
            OrdenDTO orden = ordenService.obtenerPorNumero(numeroOrden);
            return ResponseEntity.ok(orden);
        } catch (Exception e) {
            return ResponseEntity.status(404)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER ÓRDENES POR USUARIO
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> obtenerPorUsuario(@PathVariable Long usuarioId) {
        try {
            List<OrdenDTO> ordenes = ordenService.obtenerPorUsuario(usuarioId);
            return ResponseEntity.ok(Map.of("ordenes", ordenes));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER ÓRDENES POR ESTADO
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> obtenerPorEstado(@PathVariable String estado) {
        try {
            List<OrdenDTO> ordenes = ordenService.obtenerPorEstado(estado);
            return ResponseEntity.ok(Map.of("ordenes", ordenes));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER ÓRDENES PENDIENTES
    @GetMapping("/pendientes")
    public ResponseEntity<?> obtenerOrdenesPendientes() {
        try {
            List<OrdenDTO> ordenes = ordenService.obtenerOrdenesPendientes();
            return ResponseEntity.ok(Map.of("ordenes", ordenes));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER ÚLTIMAS ÓRDENES
    @GetMapping("/ultimas")
    public ResponseEntity<?> obtenerUltimasOrdenes(@RequestParam(defaultValue = "10") int limite) {
        try {
            List<OrdenDTO> ordenes = ordenService.obtenerUltimasOrdenes(limite);
            return ResponseEntity.ok(Map.of("ordenes", ordenes));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ACTUALIZAR ESTADO DE ORDEN
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestParam String nuevoEstado) {
        try {
            OrdenDTO ordenActualizada = ordenService.actualizarEstado(id, nuevoEstado);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estado actualizado exitosamente");
            response.put("orden", ordenActualizada);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // CANCELAR ORDEN
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarOrden(@PathVariable Long id, @RequestParam(required = false) String motivo) {
        try {
            OrdenDTO ordenCancelada = ordenService.cancelarOrden(id, motivo);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Orden cancelada exitosamente");
            response.put("orden", ordenCancelada);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER ÓRDENES POR TRABAJADOR
    @GetMapping("/trabajador/{trabajadorId}")
    public ResponseEntity<?> obtenerPorTrabajador(@PathVariable Long trabajadorId) {
        try {
            List<OrdenDTO> ordenes = ordenService.obtenerPorTrabajador(trabajadorId);
            return ResponseEntity.ok(Map.of("ordenes", ordenes));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // BUSCAR ÓRDENES POR CLIENTE
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorCliente(@RequestParam String q) {
        try {
            List<OrdenDTO> ordenes = ordenService.buscarPorCliente(q);
            return ResponseEntity.ok(Map.of("ordenes", ordenes));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER ÓRDENES POR RANGO DE FECHAS
    @GetMapping("/fechas")
    public ResponseEntity<?> obtenerPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            List<OrdenDTO> ordenes = ordenService.obtenerPorRangoFechas(fechaInicio, fechaFin);
            return ResponseEntity.ok(Map.of("ordenes", ordenes));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ESTADÍSTICAS DE VENTAS
    @GetMapping("/estadisticas")
    public ResponseEntity<?> estadisticasVentas() {
        try {
            Map<String, Object> stats = ordenService.obtenerEstadisticasVentas();
            return ResponseEntity.ok(Map.of("estadisticas", stats));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }
}





