package com.techsolvers.ecommerce.controller;

import com.techsolvers.ecommerce.dto.ServicioDTO;
import com.techsolvers.ecommerce.service.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/servicios")
@CrossOrigin(origins = "*")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    // ENDPOINT DE PRUEBA
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "ServicioController funcionando correctamente");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    // CREAR NUEVO SERVICIO
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ServicioDTO servicioDTO, @RequestParam Long creadoPorId) {
        try {
            ServicioDTO servicioNuevo = servicioService.crear(servicioDTO, creadoPorId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Servicio creado exitosamente");
            response.put("servicio", servicioNuevo);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // LISTAR SERVICIOS ACTIVOS
    @GetMapping
    public ResponseEntity<?> listarActivos() {
        try {
            List<ServicioDTO> servicios = servicioService.listarActivos();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("servicios", servicios);
            response.put("total", servicios.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // LISTAR SERVICIOS PARA INICIO
    @GetMapping("/inicio")
    public ResponseEntity<?> listarParaInicio() {
        try {
            List<ServicioDTO> servicios = servicioService.listarParaInicio();
            return ResponseEntity.ok(Map.of("servicios", servicios));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER SERVICIO POR ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            ServicioDTO servicio = servicioService.obtenerPorId(id);
            return ResponseEntity.ok(servicio);
        } catch (Exception e) {
            return ResponseEntity.status(404)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER SERVICIOS POR TIPO
    @GetMapping("/tipo/{tipoServicio}")
    public ResponseEntity<?> obtenerPorTipo(@PathVariable String tipoServicio) {
        try {
            List<ServicioDTO> servicios = servicioService.obtenerPorTipo(tipoServicio);
            return ResponseEntity.ok(Map.of("servicios", servicios));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // BUSCAR SERVICIOS
    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(@RequestParam String q) {
        try {
            List<ServicioDTO> servicios = servicioService.buscarServicios(q);
            return ResponseEntity.ok(Map.of("servicios", servicios));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ACTUALIZAR SERVICIO
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ServicioDTO servicioDTO) {
        try {
            ServicioDTO servicioActualizado = servicioService.actualizar(id, servicioDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Servicio actualizado exitosamente");
            response.put("servicio", servicioActualizado);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ELIMINAR SERVICIO
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = servicioService.eliminar(id);
            if (eliminado) {
                return ResponseEntity.ok(Map.of("message", "Servicio eliminado exitosamente"));
            } else {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "No se pudo eliminar el servicio"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER SERVICIOS POR CREADOR
    @GetMapping("/creador/{usuarioId}")
    public ResponseEntity<?> obtenerPorCreador(@PathVariable Long usuarioId) {
        try {
            List<ServicioDTO> servicios = servicioService.obtenerPorCreador(usuarioId);
            return ResponseEntity.ok(Map.of("servicios", servicios));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER SERVICIOS CON PRECIOS
    @GetMapping("/con-precios")
    public ResponseEntity<?> obtenerServiciosConPrecios() {
        try {
            List<ServicioDTO> servicios = servicioService.obtenerServiciosConPrecios();
            return ResponseEntity.ok(Map.of("servicios", servicios));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // CAMBIAR VISIBILIDAD EN INICIO
    @PutMapping("/{id}/visibilidad")
    public ResponseEntity<?> cambiarVisibilidadEnInicio(@PathVariable Long id, @RequestParam Boolean mostrarEnInicio) {
        try {
            ServicioDTO servicioActualizado = servicioService.cambiarVisibilidadEnInicio(id, mostrarEnInicio);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Visibilidad actualizada exitosamente");
            response.put("servicio", servicioActualizado);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ESTADÍSTICAS
    @GetMapping("/estadisticas")
    public ResponseEntity<?> estadisticas() {
        try {
            Map<String, Object> stats = servicioService.obtenerEstadisticas();
            return ResponseEntity.ok(Map.of("estadisticas", stats));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }
}

