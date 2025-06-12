package com.techsolvers.ecommerce.controller;

import com.techsolvers.ecommerce.dto.CategoriaDTO;
import com.techsolvers.ecommerce.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    // ENDPOINT DE PRUEBA
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "CategoriaController funcionando correctamente");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    // CREAR NUEVA CATEGORÍA
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CategoriaDTO categoriaDTO, @RequestParam Long creadoPorId) {
        try {
            CategoriaDTO categoriaNueva = categoriaService.crear(categoriaDTO, creadoPorId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Categoría creada exitosamente");
            response.put("categoria", categoriaNueva);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // LISTAR CATEGORÍAS ACTIVAS
    @GetMapping
    public ResponseEntity<?> listarActivas() {
        try {
            List<CategoriaDTO> categorias = categoriaService.listarActivas();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("categorias", categorias);
            response.put("total", categorias.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // LISTAR TODAS LAS CATEGORÍAS (ADMIN)
    @GetMapping("/todas")
    public ResponseEntity<?> listarTodas() {
        try {
            List<CategoriaDTO> categorias = categoriaService.listarTodas();
            return ResponseEntity.ok(Map.of("categorias", categorias));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER CATEGORÍA POR ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            CategoriaDTO categoria = categoriaService.obtenerPorId(id);
            return ResponseEntity.ok(categoria);
        } catch (Exception e) {
            return ResponseEntity.status(404)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ACTUALIZAR CATEGORÍA
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody CategoriaDTO categoriaDTO) {
        try {
            CategoriaDTO categoriaActualizada = categoriaService.actualizar(id, categoriaDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Categoría actualizada exitosamente");
            response.put("categoria", categoriaActualizada);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ELIMINAR CATEGORÍA
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminada = categoriaService.eliminar(id);
            if (eliminada) {
                return ResponseEntity.ok(Map.of("message", "Categoría eliminada exitosamente"));
            } else {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "No se pudo eliminar la categoría"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // BUSCAR CATEGORÍAS
    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(@RequestParam String q) {
        try {
            List<CategoriaDTO> categorias = categoriaService.buscarPorTitulo(q);
            return ResponseEntity.ok(Map.of("categorias", categorias));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER CATEGORÍAS CON PRODUCTOS
    @GetMapping("/con-productos")
    public ResponseEntity<?> obtenerCategoriasConProductos() {
        try {
            List<CategoriaDTO> categorias = categoriaService.obtenerCategoriasConProductos();
            return ResponseEntity.ok(Map.of("categorias", categorias));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // OBTENER CATEGORÍAS POR CREADOR
    @GetMapping("/creador/{usuarioId}")
    public ResponseEntity<?> obtenerPorCreador(@PathVariable Long usuarioId) {
        try {
            List<CategoriaDTO> categorias = categoriaService.obtenerPorCreador(usuarioId);
            return ResponseEntity.ok(Map.of("categorias", categorias));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ESTADÍSTICAS
    @GetMapping("/estadisticas")
    public ResponseEntity<?> estadisticas() {
        try {
            Map<String, Object> stats = categoriaService.obtenerEstadisticas();
            return ResponseEntity.ok(Map.of("estadisticas", stats));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", e.getMessage()));
        }
    }
}
