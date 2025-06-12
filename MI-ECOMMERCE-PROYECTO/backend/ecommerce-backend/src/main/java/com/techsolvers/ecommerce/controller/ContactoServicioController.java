package com.techsolvers.ecommerce.controller;

import com.techsolvers.ecommerce.dto.ContactoServicioDTO;
import com.techsolvers.ecommerce.service.ContactoServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contacto-servicios")
@CrossOrigin(origins = "*")
public class ContactoServicioController {

    @Autowired
    private ContactoServicioService contactoServicioService;

    // ENDPOINT DE PRUEBA
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "ContactoServicioController funcionando correctamente");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    // CREAR NUEVO CONTACTO (FORMULARIO PÚBLICO)
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ContactoServicioDTO contactoDTO) {
        try {
            ContactoServicioDTO contactoNuevo = contactoServicioService.crear(contactoDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Contacto enviado exitosamente. Te responderemos pronto.");
            response.put("contacto", contactoNuevo);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // LISTAR TODOS LOS CONTACTOS (ADMIN/TRABAJADOR)
    @GetMapping
    public ResponseEntity<?> listarTodos() {
        try {
            List<ContactoServicioDTO> contactos = contactoServicioService.listarTodos();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("contactos", contactos);
            response.put("total", contactos.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER CONTACTOS POR ESTADO
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> obtenerPorEstado(@PathVariable String estado) {
        try {
            List<ContactoServicioDTO> contactos = contactoServicioService.obtenerPorEstado(estado);
            return ResponseEntity.ok(Map.of("contactos", contactos));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER CONTACTOS PENDIENTES
    @GetMapping("/pendientes")
    public ResponseEntity<?> obtenerPendientes() {
        try {
            List<ContactoServicioDTO> contactos = contactoServicioService.obtenerPendientes();
            return ResponseEntity.ok(Map.of("contactos", contactos));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER CONTACTO POR ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            ContactoServicioDTO contacto = contactoServicioService.obtenerPorId(id);
            return ResponseEntity.ok(contacto);
        } catch (Exception e) {
            return ResponseEntity.status(404)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER CONTACTOS POR SERVICIO
    @GetMapping("/servicio/{servicioId}")
    public ResponseEntity<?> obtenerPorServicio(@PathVariable Long servicioId) {
        try {
            List<ContactoServicioDTO> contactos = contactoServicioService.obtenerPorServicio(servicioId);
            return ResponseEntity.ok(Map.of("contactos", contactos));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // BUSCAR CONTACTOS
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarContactos(@RequestParam String q) {
        try {
            List<ContactoServicioDTO> contactos = contactoServicioService.buscarContactos(q);
            return ResponseEntity.ok(Map.of("contactos", contactos));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ACTUALIZAR ESTADO DEL CONTACTO
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, 
                                             @RequestParam String nuevoEstado,
                                             @RequestParam Long atendidoPorId,
                                             @RequestParam(required = false) String notasAtencion) {
        try {
            ContactoServicioDTO contactoActualizado = contactoServicioService.actualizarEstado(
                id, nuevoEstado, atendidoPorId, notasAtencion);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estado actualizado exitosamente");
            response.put("contacto", contactoActualizado);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // MARCAR COMO CONTACTADO
    @PutMapping("/{id}/contactado")
    public ResponseEntity<?> marcarComoContactado(@PathVariable Long id, 
                                                 @RequestParam Long atendidoPorId,
                                                 @RequestParam(required = false) String notasAtencion) {
        try {
            ContactoServicioDTO contactoActualizado = contactoServicioService.marcarComoContactado(
                id, atendidoPorId, notasAtencion);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Contacto marcado como contactado");
            response.put("contacto", contactoActualizado);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // MARCAR COMO RESUELTO
    @PutMapping("/{id}/resuelto")
    public ResponseEntity<?> marcarComoResuelto(@PathVariable Long id, 
                                               @RequestParam Long atendidoPorId,
                                               @RequestParam(required = false) String notasAtencion) {
        try {
            ContactoServicioDTO contactoActualizado = contactoServicioService.marcarComoResuelto(
                id, atendidoPorId, notasAtencion);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Contacto marcado como resuelto");
            response.put("contacto", contactoActualizado);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER CONTACTOS ATENDIDOS POR USUARIO
    @GetMapping("/atendidos-por/{usuarioId}")
    public ResponseEntity<?> obtenerAtendidosPor(@PathVariable Long usuarioId) {
        try {
            List<ContactoServicioDTO> contactos = contactoServicioService.obtenerAtendidosPor(usuarioId);
            return ResponseEntity.ok(Map.of("contactos", contactos));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER CONTACTOS POR RANGO DE FECHAS
    @GetMapping("/fechas")
    public ResponseEntity<?> obtenerPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        try {
            List<ContactoServicioDTO> contactos = contactoServicioService.obtenerPorRangoFechas(fechaInicio, fechaFin);
            return ResponseEntity.ok(Map.of("contactos", contactos));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ELIMINAR CONTACTO
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = contactoServicioService.eliminar(id);
            if (eliminado) {
                return ResponseEntity.ok(Map.of("message", "Contacto eliminado exitosamente"));
            } else {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "No se pudo eliminar el contacto"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ESTADÍSTICAS
    @GetMapping("/estadisticas")
    public ResponseEntity<?> estadisticas() {
        try {
            Map<String, Object> stats = contactoServicioService.obtenerEstadisticas();
            return ResponseEntity.ok(Map.of("estadisticas", stats));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }
}



