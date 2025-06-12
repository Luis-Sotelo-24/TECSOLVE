package com.techsolvers.ecommerce.service;

import com.techsolvers.ecommerce.dto.UsuarioDTO;
import com.techsolvers.ecommerce.model.Usuario;
import com.techsolvers.ecommerce.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // ========================================
    // MÉTODOS PARA AUTENTICACIÓN
    // ========================================
    
    /**
     * Login de usuario
     */
    public UsuarioDTO login(String email, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        
        if (usuario.isPresent() && usuario.get().getActivo()) {
            // Verificar password encriptado
            if (passwordEncoder.matches(password, usuario.get().getPassword())) {
                return new UsuarioDTO(usuario.get());
            }
        }
        return null; // Login fallido
    }
    
    /**
     * Registrar nuevo usuario (cliente)
     */
    public UsuarioDTO registrarCliente(UsuarioDTO usuarioDTO, String password) {
        // Validar que no exista el email
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        // Validar que no exista el DNI (si se proporciona)
        if (usuarioDTO.getDni() != null && usuarioRepository.existsByDni(usuarioDTO.getDni())) {
            throw new RuntimeException("El DNI ya está registrado");
        }
        
        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellido(usuarioDTO.getApellido());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(password)); // Encriptar password
        usuario.setTelefono(usuarioDTO.getTelefono());
        usuario.setDireccion(usuarioDTO.getDireccion());
        usuario.setDni(usuarioDTO.getDni());
        usuario.setRol(Usuario.Rol.cliente); // Siempre cliente en registro público
        usuario.setActivo(true);
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return new UsuarioDTO(usuarioGuardado);
    }
    
    // ========================================
    // MÉTODOS CRUD PARA ADMIN
    // ========================================
    
    /**
     * Crear trabajador (solo admin)
     */
    public UsuarioDTO crearTrabajador(UsuarioDTO usuarioDTO, String password) {
        // Validaciones
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        if (usuarioDTO.getDni() != null && usuarioRepository.existsByDni(usuarioDTO.getDni())) {
            throw new RuntimeException("El DNI ya está registrado");
        }
        
        // Crear trabajador
        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellido(usuarioDTO.getApellido());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setTelefono(usuarioDTO.getTelefono());
        usuario.setDireccion(usuarioDTO.getDireccion());
        usuario.setDni(usuarioDTO.getDni());
        usuario.setRol(Usuario.Rol.trabajador);
        usuario.setActivo(true);
        
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return new UsuarioDTO(usuarioGuardado);
    }
    
    /**
     * Obtener todos los usuarios
     */
    public List<UsuarioDTO> obtenerTodosLosUsuarios() {
        return usuarioRepository.findByActivoTrue()
                .stream()
                .map(UsuarioDTO::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener usuarios por rol
     */
    public List<UsuarioDTO> obtenerUsuariosPorRol(String rol) {
        Usuario.Rol rolEnum = Usuario.Rol.valueOf(rol);
        return usuarioRepository.findByRolAndActivoTrue(rolEnum)
                .stream()
                .map(UsuarioDTO::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener usuario por ID
     */
    public UsuarioDTO obtenerUsuarioPorId(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.map(UsuarioDTO::new).orElse(null);
    }
    
    /**
     * Actualizar usuario
     */
    public UsuarioDTO actualizarUsuario(Long id, UsuarioDTO usuarioDTO) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            // Validar email único (si cambió)
            if (!usuario.getEmail().equals(usuarioDTO.getEmail()) && 
                usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
                throw new RuntimeException("El email ya está registrado");
            }
            
            // Validar DNI único (si cambió)
            if (usuarioDTO.getDni() != null && 
                !usuarioDTO.getDni().equals(usuario.getDni()) && 
                usuarioRepository.existsByDni(usuarioDTO.getDni())) {
                throw new RuntimeException("El DNI ya está registrado");
            }
            
            // Actualizar campos
            usuario.setNombre(usuarioDTO.getNombre());
            usuario.setApellido(usuarioDTO.getApellido());
            usuario.setEmail(usuarioDTO.getEmail());
            usuario.setTelefono(usuarioDTO.getTelefono());
            usuario.setDireccion(usuarioDTO.getDireccion());
            usuario.setDni(usuarioDTO.getDni());
            
            Usuario usuarioActualizado = usuarioRepository.save(usuario);
            return new UsuarioDTO(usuarioActualizado);
        }
        
        return null;
    }
    
    /**
     * Eliminar usuario (desactivar)
     */
    public boolean eliminarUsuario(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            usuario.get().setActivo(false);
            usuarioRepository.save(usuario.get());
            return true;
        }
        return false;
    }
    
    /**
     * Buscar usuarios por nombre o apellido
     */
    public List<UsuarioDTO> buscarUsuarios(String busqueda) {
        return usuarioRepository.buscarPorNombreOApellido(busqueda)
                .stream()
                .map(UsuarioDTO::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Cambiar contraseña
     */
    public boolean cambiarPassword(Long id, String passwordActual, String passwordNuevo) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            // Verificar password actual
            if (passwordEncoder.matches(passwordActual, usuario.getPassword())) {
                usuario.setPassword(passwordEncoder.encode(passwordNuevo));
                usuarioRepository.save(usuario);
                return true;
            }
        }
        return false;
    }
    
    // ========================================
    // MÉTODOS DE ESTADÍSTICAS
    // ========================================
    
    /**
     * Contar usuarios por rol
     */
    public long contarUsuariosPorRol(String rol) {
        Usuario.Rol rolEnum = Usuario.Rol.valueOf(rol);
        return usuarioRepository.countByRol(rolEnum);
    }
    
    /**
     * Contar total de usuarios activos
     */
    public long contarUsuariosActivos() {
        return usuarioRepository.countByActivoTrue();
    }
}