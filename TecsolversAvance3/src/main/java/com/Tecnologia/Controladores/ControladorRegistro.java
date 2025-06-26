package com.Tecnologia.Controladores;

import com.Tecnologia.Modelo.Clientes;
import com.Tecnologia.Modelo.Rol;
import com.Tecnologia.Repositorio.ClientesRepositorio;
import com.Tecnologia.Repositorio.RolRepositorio;
import com.Tecnologia.seguridad.authService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ControladorRegistro {

    private final ClientesRepositorio clientesRepo;
    private final RolRepositorio rolRepo;

    public ControladorRegistro(ClientesRepositorio clientesRepo, RolRepositorio rolRepo) {
        this.clientesRepo = clientesRepo;
        this.rolRepo = rolRepo;
    }

    @GetMapping("/registro")
    public String Registro() {
        return "FrmRegistro";
    }

     @PostMapping("/registro")
public ResponseEntity<String> registrarUsuario(
        @RequestParam String nombre_cli,
        @RequestParam String apellidos_cli,
        @RequestParam String correo,
        @RequestParam String contraseña,
        @RequestParam String dni,
        @RequestParam String direccion,
        @RequestParam String telefono) {

    // Verificar si el correo ya está registrado
    if (clientesRepo.findByCorreo(correo).isPresent()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("correo_exist");  // El correo ya existe
    }

    // Crear un nuevo cliente
    Clientes nuevoCliente = new Clientes();
    nuevoCliente.setNombreCli(nombre_cli);
    nuevoCliente.setApellidosCli(apellidos_cli);
    nuevoCliente.setCorreo(correo);
    nuevoCliente.setContraseña(authService.encriptar(contraseña));
    nuevoCliente.setDni(dni);
    nuevoCliente.setDireccion(direccion);
    nuevoCliente.setTelefono(telefono);

    // Asignar el rol según el correo
    if ("admin@admin.com".equals(correo)) {
        // Asignar rol ROLE_ADMIN si es el correo del admin
        Rol rolAdmin = rolRepo.findByNombre("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Rol ROLE_ADMIN no encontrado"));
        nuevoCliente.getRoles().add(rolAdmin);
    } else {
        // Asignar rol ROLE_USER si es un usuario normal
        Rol rolUsuario = rolRepo.findByNombre("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Rol ROLE_USER no encontrado"));
        nuevoCliente.getRoles().add(rolUsuario);
    }

    // Guardar el cliente en la base de datos
    clientesRepo.save(nuevoCliente);

    // Devolver una respuesta exitosa
    return ResponseEntity.ok("ok");
}
}
