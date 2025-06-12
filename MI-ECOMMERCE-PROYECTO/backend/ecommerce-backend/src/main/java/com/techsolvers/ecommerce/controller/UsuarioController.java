package com.techsolvers.ecommerce.controller;

import com.techsolvers.ecommerce.dto.UsuarioDTO;
import com.techsolvers.ecommerce.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")  // ✅ CAMBIADO: Quitamos /api porque ya está en context-path
@CrossOrigin(origins = { "http://localhost:3000", "http://127.0.0.1:5500" })
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // ========================================
    // ENDPOINT TEMPORAL PARA GENERAR HASH BCRYPT
    // ========================================

    /**
     * GET /api/usuarios/generar-hash/{password}
     * ENDPOINT TEMPORAL: Genera hash BCrypt para cualquier password
     * USAR SOLO PARA PRUEBAS - ELIMINAR EN PRODUCCIÓN
     */
    @GetMapping("/generar-hash/{password}")
    public ResponseEntity<String> generarHash(@PathVariable String password) {
        // Crear instancia del encoder BCrypt
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        // Generar hash del password
        String hash = encoder.encode(password);

        // Retornar el hash generado
        return ResponseEntity.ok("Hash para '" + password + "': " + hash);
    }

    /**
     * GET /api/usuarios/test
     * ENDPOINT DE PRUEBA: Verificar que el controller funciona
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "UsuarioController funcionando correctamente");
        response.put("timestamp", LocalDateTime.now());
        response.put("endpoints_disponibles", Arrays.asList(
                "POST /api/usuarios/login",
                "POST /api/usuarios/registro",
                "GET /api/usuarios",
                "GET /api/usuarios/{id}",
                "GET /api/usuarios/estadisticas"));
        return ResponseEntity.ok(response);
    }
    // ========================================
    // ENDPOINTS DE AUTENTICACIÓN
    // ========================================

    /**
     * POST /api/usuarios/login
     * Login de usuario
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = loginData.get("email");
            String password = loginData.get("password");

            if (email == null || password == null) {
                response.put("success", false);
                response.put("message", "Email y contraseña son obligatorios");
                return ResponseEntity.badRequest().body(response);
            }

            UsuarioDTO usuario = usuarioService.login(email, password);

            if (usuario != null) {
                response.put("success", true);
                response.put("message", "Login exitoso");
                response.put("usuario", usuario);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Credenciales incorrectas");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error en el servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST /api/usuarios/registro
     * Registro de nuevo cliente
     */
    @PostMapping("/registro")
    public ResponseEntity<Map<String, Object>> registro(@Valid @RequestBody Map<String, String> registroData) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Crear DTO desde los datos recibidos
            UsuarioDTO usuarioDTO = new UsuarioDTO();
            usuarioDTO.setNombre(registroData.get("nombre"));
            usuarioDTO.setApellido(registroData.get("apellido"));
            usuarioDTO.setEmail(registroData.get("email"));
            usuarioDTO.setTelefono(registroData.get("telefono"));
            usuarioDTO.setDireccion(registroData.get("direccion"));
            usuarioDTO.setDni(registroData.get("dni"));

            String password = registroData.get("password");

            if (password == null || password.length() < 6) {
                response.put("success", false);
                response.put("message", "La contraseña debe tener al menos 6 caracteres");
                return ResponseEntity.badRequest().body(response);
            }

            UsuarioDTO usuarioCreado = usuarioService.registrarCliente(usuarioDTO, password);

            response.put("success", true);
            response.put("message", "Usuario registrado exitosamente");
            response.put("usuario", usuarioCreado);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error en el servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ========================================
    // ENDPOINTS CRUD (PARA ADMIN)
    // ========================================

    /**
     * GET /api/usuarios
     * Obtener todos los usuarios
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerTodosLosUsuarios() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<UsuarioDTO> usuarios = usuarioService.obtenerTodosLosUsuarios();
            response.put("success", true);
            response.put("usuarios", usuarios);
            response.put("total", usuarios.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener usuarios: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/usuarios/rol/{rol}
     * Obtener usuarios por rol
     */
    @GetMapping("/rol/{rol}")
    public ResponseEntity<Map<String, Object>> obtenerUsuariosPorRol(@PathVariable String rol) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<UsuarioDTO> usuarios = usuarioService.obtenerUsuariosPorRol(rol);
            response.put("success", true);
            response.put("usuarios", usuarios);
            response.put("total", usuarios.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener usuarios por rol: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/usuarios/{id}
     * Obtener usuario por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerUsuarioPorId(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            UsuarioDTO usuario = usuarioService.obtenerUsuarioPorId(id);

            if (usuario != null) {
                response.put("success", true);
                response.put("usuario", usuario);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * POST /api/usuarios/trabajador
     * Crear trabajador (solo admin)
     */
    @PostMapping("/trabajador")
    public ResponseEntity<Map<String, Object>> crearTrabajador(@Valid @RequestBody Map<String, String> trabajadorData) {
        Map<String, Object> response = new HashMap<>();

        try {
            UsuarioDTO usuarioDTO = new UsuarioDTO();
            usuarioDTO.setNombre(trabajadorData.get("nombre"));
            usuarioDTO.setApellido(trabajadorData.get("apellido"));
            usuarioDTO.setEmail(trabajadorData.get("email"));
            usuarioDTO.setTelefono(trabajadorData.get("telefono"));
            usuarioDTO.setDireccion(trabajadorData.get("direccion"));
            usuarioDTO.setDni(trabajadorData.get("dni"));

            String password = trabajadorData.get("password");

            UsuarioDTO trabajadorCreado = usuarioService.crearTrabajador(usuarioDTO, password);

            response.put("success", true);
            response.put("message", "Trabajador creado exitosamente");
            response.put("usuario", trabajadorCreado);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error en el servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * PUT /api/usuarios/{id}
     * Actualizar usuario
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarUsuario(@PathVariable Long id,
            @Valid @RequestBody UsuarioDTO usuarioDTO) {
        Map<String, Object> response = new HashMap<>();

        try {
            UsuarioDTO usuarioActualizado = usuarioService.actualizarUsuario(id, usuarioDTO);

            if (usuarioActualizado != null) {
                response.put("success", true);
                response.put("message", "Usuario actualizado exitosamente");
                response.put("usuario", usuarioActualizado);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return ResponseEntity.notFound().build();
            }

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error en el servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * DELETE /api/usuarios/{id}
     * Eliminar usuario (desactivar)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarUsuario(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean eliminado = usuarioService.eliminarUsuario(id);

            if (eliminado) {
                response.put("success", true);
                response.put("message", "Usuario eliminado exitosamente");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /api/usuarios/buscar?q={busqueda}
     * Buscar usuarios por nombre o apellido
     */
    @GetMapping("/buscar")
    public ResponseEntity<Map<String, Object>> buscarUsuarios(@RequestParam("q") String busqueda) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<UsuarioDTO> usuarios = usuarioService.buscarUsuarios(busqueda);
            response.put("success", true);
            response.put("usuarios", usuarios);
            response.put("total", usuarios.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error en la búsqueda: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ========================================
    // ENDPOINTS DE ESTADÍSTICAS
    // ========================================

    /**
     * GET /api/usuarios/estadisticas
     * Obtener estadísticas de usuarios
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Long> estadisticas = new HashMap<>();
            estadisticas.put("totalActivos", usuarioService.contarUsuariosActivos());
            estadisticas.put("totalClientes", usuarioService.contarUsuariosPorRol("cliente"));
            estadisticas.put("totalTrabajadores", usuarioService.contarUsuariosPorRol("trabajador"));
            estadisticas.put("totalAdmins", usuarioService.contarUsuariosPorRol("admin"));

            response.put("success", true);
            response.put("estadisticas", estadisticas);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener estadísticas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}