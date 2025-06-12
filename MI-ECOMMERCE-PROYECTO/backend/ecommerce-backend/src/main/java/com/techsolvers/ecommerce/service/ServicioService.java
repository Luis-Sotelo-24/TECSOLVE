package com.techsolvers.ecommerce.service;

import com.techsolvers.ecommerce.dto.ServicioDTO;
import com.techsolvers.ecommerce.model.Servicio;
import com.techsolvers.ecommerce.model.Usuario;
import com.techsolvers.ecommerce.repository.ServicioRepository;
import com.techsolvers.ecommerce.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Crear nuevo servicio
    public ServicioDTO crear(ServicioDTO servicioDTO, Long creadoPorId) {
        try {
            Usuario creador = usuarioRepository.findById(creadoPorId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Validar que no exista un servicio con el mismo título
            if (servicioRepository.existsByTituloIgnoreCase(servicioDTO.getTitulo())) {
                throw new RuntimeException("Ya existe un servicio con ese título");
            }

            Servicio servicio = new Servicio();
            servicio.setTitulo(servicioDTO.getTitulo());
            servicio.setDescripcion(servicioDTO.getDescripcion());
            servicio.setTipoServicio(servicioDTO.getTipoServicio());
            servicio.setImagenUrl(servicioDTO.getImagenUrl());
            servicio.setContenidoDetallado(servicioDTO.getContenidoDetallado());
            servicio.setPrecioDesde(servicioDTO.getPrecioDesde());
            servicio.setTiempoEstimado(servicioDTO.getTiempoEstimado());
            servicio.setMostrarEnInicio(servicioDTO.getMostrarEnInicio() != null ? servicioDTO.getMostrarEnInicio() : true);
            servicio.setCreadoPor(creador);
            servicio.setActivo(true);

            Servicio servicioGuardado = servicioRepository.save(servicio);
            return convertirADTO(servicioGuardado);

        } catch (Exception e) {
            throw new RuntimeException("Error al crear servicio: " + e.getMessage());
        }
    }

    // Obtener todos los servicios activos
    public List<ServicioDTO> listarActivos() {
        try {
            List<Servicio> servicios = servicioRepository.findByActivoTrue();
            return servicios.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al listar servicios: " + e.getMessage());
        }
    }

    // Obtener servicios para mostrar en inicio
    public List<ServicioDTO> listarParaInicio() {
        try {
            List<Servicio> servicios = servicioRepository.findByMostrarEnInicioTrueAndActivoTrue();
            return servicios.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al listar servicios para inicio: " + e.getMessage());
        }
    }

    // Obtener servicio por ID
    public ServicioDTO obtenerPorId(Long id) {
        try {
            Servicio servicio = servicioRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
            return convertirADTO(servicio);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener servicio: " + e.getMessage());
        }
    }

    // Obtener servicios por tipo
    public List<ServicioDTO> obtenerPorTipo(String tipoServicio) {
        try {
            List<Servicio> servicios = servicioRepository.findByTipoServicioAndActivoTrue(tipoServicio);
            return servicios.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener servicios por tipo: " + e.getMessage());
        }
    }

    // Buscar servicios
    public List<ServicioDTO> buscarServicios(String busqueda) {
        try {
            List<Servicio> servicios = servicioRepository.buscarServicios(busqueda);
            return servicios.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar servicios: " + e.getMessage());
        }
    }

    // Actualizar servicio
    public ServicioDTO actualizar(Long id, ServicioDTO servicioDTO) {
        try {
            Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

            // Validar que no exista otro servicio con el mismo título
            if (!servicio.getTitulo().equalsIgnoreCase(servicioDTO.getTitulo()) &&
                servicioRepository.existsByTituloIgnoreCase(servicioDTO.getTitulo())) {
                throw new RuntimeException("Ya existe un servicio con ese título");
            }

            servicio.setTitulo(servicioDTO.getTitulo());
            servicio.setDescripcion(servicioDTO.getDescripcion());
            servicio.setTipoServicio(servicioDTO.getTipoServicio());
            servicio.setImagenUrl(servicioDTO.getImagenUrl());
            servicio.setContenidoDetallado(servicioDTO.getContenidoDetallado());
            servicio.setPrecioDesde(servicioDTO.getPrecioDesde());
            servicio.setTiempoEstimado(servicioDTO.getTiempoEstimado());
            servicio.setMostrarEnInicio(servicioDTO.getMostrarEnInicio());

            Servicio servicioActualizado = servicioRepository.save(servicio);
            return convertirADTO(servicioActualizado);

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar servicio: " + e.getMessage());
        }
    }

    // Eliminar (desactivar) servicio
    public boolean eliminar(Long id) {
        try {
            Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

            servicio.setActivo(false);
            servicioRepository.save(servicio);
            return true;

        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar servicio: " + e.getMessage());
        }
    }

    // Obtener servicios por creador
    public List<ServicioDTO> obtenerPorCreador(Long usuarioId) {
        try {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<Servicio> servicios = servicioRepository.findByCreadoPor(usuario);
            return servicios.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener servicios por creador: " + e.getMessage());
        }
    }

    // Obtener servicios con precios
    public List<ServicioDTO> obtenerServiciosConPrecios() {
        try {
            List<Servicio> servicios = servicioRepository.findServiciosConPrecios();
            return servicios.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener servicios con precios: " + e.getMessage());
        }
    }

    // Cambiar visibilidad en inicio
    public ServicioDTO cambiarVisibilidadEnInicio(Long id, Boolean mostrarEnInicio) {
        try {
            Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

            servicio.setMostrarEnInicio(mostrarEnInicio);
            Servicio servicioActualizado = servicioRepository.save(servicio);
            return convertirADTO(servicioActualizado);

        } catch (Exception e) {
            throw new RuntimeException("Error al cambiar visibilidad: " + e.getMessage());
        }
    }

    // Obtener estadísticas
    public java.util.Map<String, Object> obtenerEstadisticas() {
        try {
            java.util.Map<String, Object> estadisticas = new java.util.HashMap<>();
            
            long totalActivos = servicioRepository.contarServiciosActivos();
            long totalInactivos = servicioRepository.count() - totalActivos;
            long totalEnInicio = servicioRepository.findByMostrarEnInicioTrueAndActivoTrue().size();
            
            // Estadísticas por tipo
            long totalPC = servicioRepository.findServiciosPorTipo("reparacion_pc").size();
            long totalCelular = servicioRepository.findServiciosPorTipo("reparacion_celular").size();
            long totalImpresora = servicioRepository.findServiciosPorTipo("reparacion_impresora").size();
            
            estadisticas.put("totalActivos", totalActivos);
            estadisticas.put("totalInactivos", totalInactivos);
            estadisticas.put("totalEnInicio", totalEnInicio);
            estadisticas.put("totalGeneral", servicioRepository.count());
            estadisticas.put("totalPC", totalPC);
            estadisticas.put("totalCelular", totalCelular);
            estadisticas.put("totalImpresora", totalImpresora);
            
            return estadisticas;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener estadísticas: " + e.getMessage());
        }
    }

    // Convertir Entity a DTO
    private ServicioDTO convertirADTO(Servicio servicio) {
        ServicioDTO dto = new ServicioDTO();
        dto.setId(servicio.getId());
        dto.setTitulo(servicio.getTitulo());
        dto.setDescripcion(servicio.getDescripcion());
        dto.setTipoServicio(servicio.getTipoServicio());
        dto.setImagenUrl(servicio.getImagenUrl());
        dto.setContenidoDetallado(servicio.getContenidoDetallado());
        dto.setPrecioDesde(servicio.getPrecioDesde());
        dto.setTiempoEstimado(servicio.getTiempoEstimado());
        dto.setActivo(servicio.getActivo());
        dto.setMostrarEnInicio(servicio.getMostrarEnInicio());
        dto.setFechaCreacion(servicio.getFechaCreacion());
        dto.setFechaActualizacion(servicio.getFechaActualizacion());
        
        // Datos del creador
        if (servicio.getCreadoPor() != null) {
            dto.setCreadoPorId(servicio.getCreadoPor().getId());
            dto.setCreadoPorNombre(servicio.getCreadoPor().getNombre());
            dto.setCreadoPorApellido(servicio.getCreadoPor().getApellido());
        }
        
        // Contar contactos si es necesario
        if (servicio.getContactos() != null) {
            dto.setTotalContactos(servicio.getContactos().size());
        }
        
        return dto;
    }
}




