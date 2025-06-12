package com.techsolvers.ecommerce.service;

import com.techsolvers.ecommerce.dto.CategoriaDTO;
import com.techsolvers.ecommerce.model.Categoria;
import com.techsolvers.ecommerce.model.Usuario;
import com.techsolvers.ecommerce.repository.CategoriaRepository;
import com.techsolvers.ecommerce.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import java.time.LocalDateTime; // No se usa - eliminado por error de VS Code
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Crear nueva categoría
    public CategoriaDTO crear(CategoriaDTO categoriaDTO, Long creadoPorId) {
        try {
            Usuario creador = usuarioRepository.findById(creadoPorId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Validar que no exista una categoría con el mismo título
            if (categoriaRepository.existsByTituloIgnoreCase(categoriaDTO.getTitulo())) {
                throw new RuntimeException("Ya existe una categoría con ese título");
            }

            Categoria categoria = new Categoria();
            categoria.setTitulo(categoriaDTO.getTitulo());
            categoria.setDescripcion(categoriaDTO.getDescripcion());
            categoria.setImagenUrl(categoriaDTO.getImagenUrl());
            categoria.setCreadoPor(creador);
            categoria.setActivo(true);

            Categoria categoriaGuardada = categoriaRepository.save(categoria);
            return convertirADTO(categoriaGuardada);

        } catch (Exception e) {
            throw new RuntimeException("Error al crear categoría: " + e.getMessage());
        }
    }

    // Obtener todas las categorías activas
    public List<CategoriaDTO> listarActivas() {
        try {
            List<Categoria> categorias = categoriaRepository.findByActivoTrue();
            return categorias.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al listar categorías: " + e.getMessage());
        }
    }

    // Obtener todas las categorías (activas e inactivas)
    public List<CategoriaDTO> listarTodas() {
        try {
            List<Categoria> categorias = categoriaRepository.findAll();
            return categorias.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al listar todas las categorías: " + e.getMessage());
        }
    }

    // Obtener categoría por ID
    public CategoriaDTO obtenerPorId(Long id) {
        try {
            Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            return convertirADTO(categoria);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener categoría: " + e.getMessage());
        }
    }

    // Actualizar categoría
    public CategoriaDTO actualizar(Long id, CategoriaDTO categoriaDTO) {
        try {
            Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

            // Validar que no exista otra categoría con el mismo título
            if (!categoria.getTitulo().equalsIgnoreCase(categoriaDTO.getTitulo()) &&
                categoriaRepository.existsByTituloIgnoreCase(categoriaDTO.getTitulo())) {
                throw new RuntimeException("Ya existe una categoría con ese título");
            }

            categoria.setTitulo(categoriaDTO.getTitulo());
            categoria.setDescripcion(categoriaDTO.getDescripcion());
            categoria.setImagenUrl(categoriaDTO.getImagenUrl());

            Categoria categoriaActualizada = categoriaRepository.save(categoria);
            return convertirADTO(categoriaActualizada);

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar categoría: " + e.getMessage());
        }
    }

    // Eliminar (desactivar) categoría
    public boolean eliminar(Long id) {
        try {
            Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

            categoria.setActivo(false);
            categoriaRepository.save(categoria);
            return true;

        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar categoría: " + e.getMessage());
        }
    }

    // Buscar categorías por título
    public List<CategoriaDTO> buscarPorTitulo(String titulo) {
        try {
            List<Categoria> categorias = categoriaRepository.findByTituloContainingIgnoreCase(titulo);
            return categorias.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar categorías: " + e.getMessage());
        }
    }

    // Obtener categorías con productos
    public List<CategoriaDTO> obtenerCategoriasConProductos() {
        try {
            List<Categoria> categorias = categoriaRepository.findCategoriasConProductos();
            return categorias.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener categorías con productos: " + e.getMessage());
        }
    }

    // Obtener categorías por usuario creador
    public List<CategoriaDTO> obtenerPorCreador(Long usuarioId) {
        try {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<Categoria> categorias = categoriaRepository.findByCreadoPor(usuario);
            return categorias.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener categorías por creador: " + e.getMessage());
        }
    }

    // Obtener estadísticas
    public java.util.Map<String, Object> obtenerEstadisticas() {
        try {
            java.util.Map<String, Object> estadisticas = new java.util.HashMap<>();
            
            long totalActivas = categoriaRepository.contarCategoriasActivas();
            long totalInactivas = categoriaRepository.count() - totalActivas;
            
            estadisticas.put("totalActivas", totalActivas);
            estadisticas.put("totalInactivas", totalInactivas);
            estadisticas.put("totalGeneral", categoriaRepository.count());
            
            return estadisticas;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener estadísticas: " + e.getMessage());
        }
    }

    // Convertir Entity a DTO
    private CategoriaDTO convertirADTO(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setTitulo(categoria.getTitulo());
        dto.setDescripcion(categoria.getDescripcion());
        dto.setImagenUrl(categoria.getImagenUrl());
        dto.setActivo(categoria.getActivo());
        dto.setFechaCreacion(categoria.getFechaCreacion());
        dto.setFechaActualizacion(categoria.getFechaActualizacion());
        
        if (categoria.getCreadoPor() != null) {
            dto.setCreadoPorId(categoria.getCreadoPor().getId());
            dto.setCreadoPorNombre(categoria.getCreadoPor().getNombre());
            dto.setCreadoPorApellido(categoria.getCreadoPor().getApellido());
        }
        
        // Contar productos si es necesario
        if (categoria.getProductos() != null) {
            dto.setTotalProductos(categoria.getProductos().size());
        }
        
        return dto;
    }
}