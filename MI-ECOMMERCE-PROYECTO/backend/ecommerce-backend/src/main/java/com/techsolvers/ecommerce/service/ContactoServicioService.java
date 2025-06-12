

package com.techsolvers.ecommerce.service;

import com.techsolvers.ecommerce.dto.ContactoServicioDTO;
import com.techsolvers.ecommerce.model.ContactoServicio;
import com.techsolvers.ecommerce.model.Servicio;
import com.techsolvers.ecommerce.model.Usuario;
import com.techsolvers.ecommerce.repository.ContactoServicioRepository;
import com.techsolvers.ecommerce.repository.ServicioRepository;
import com.techsolvers.ecommerce.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactoServicioService {

    @Autowired
    private ContactoServicioRepository contactoServicioRepository;
    
    @Autowired
    private ServicioRepository servicioRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Crear nuevo contacto de servicio
    public ContactoServicioDTO crear(ContactoServicioDTO contactoDTO) {
        try {
            Servicio servicio = servicioRepository.findById(contactoDTO.getServicioId())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

            ContactoServicio contacto = new ContactoServicio();
            contacto.setServicio(servicio);
            contacto.setNombre(contactoDTO.getNombre());
            contacto.setApellido(contactoDTO.getApellido());
            contacto.setEmail(contactoDTO.getEmail());
            contacto.setTelefono(contactoDTO.getTelefono());
            contacto.setMensaje(contactoDTO.getMensaje());
            contacto.setDispositivoProblema(contactoDTO.getDispositivoProblema());
            contacto.setEstado("pendiente");

            ContactoServicio contactoGuardado = contactoServicioRepository.save(contacto);
            return convertirADTO(contactoGuardado);

        } catch (Exception e) {
            throw new RuntimeException("Error al crear contacto: " + e.getMessage());
        }
    }

    // Obtener todos los contactos
    public List<ContactoServicioDTO> listarTodos() {
        try {
            List<ContactoServicio> contactos = contactoServicioRepository.findAll();
            return contactos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al listar contactos: " + e.getMessage());
        }
    }

    // Obtener contactos por estado
    public List<ContactoServicioDTO> obtenerPorEstado(String estado) {
        try {
            List<ContactoServicio> contactos = contactoServicioRepository.findByEstadoOrderByFechaCreacionDesc(estado);
            return contactos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener contactos por estado: " + e.getMessage());
        }
    }

    // Obtener contactos pendientes
    public List<ContactoServicioDTO> obtenerPendientes() {
        try {
            return obtenerPorEstado("pendiente");
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener contactos pendientes: " + e.getMessage());
        }
    }

    // Obtener contacto por ID
    public ContactoServicioDTO obtenerPorId(Long id) {
        try {
            ContactoServicio contacto = contactoServicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contacto no encontrado"));
            return convertirADTO(contacto);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener contacto: " + e.getMessage());
        }
    }

    // Obtener contactos por servicio
    public List<ContactoServicioDTO> obtenerPorServicio(Long servicioId) {
        try {
            Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

            List<ContactoServicio> contactos = contactoServicioRepository.findByServicioOrderByFechaCreacionDesc(servicio);
            return contactos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener contactos del servicio: " + e.getMessage());
        }
    }

    // Buscar contactos por nombre o email
    public List<ContactoServicioDTO> buscarContactos(String busqueda) {
        try {
            List<ContactoServicio> contactos = contactoServicioRepository.buscarPorNombreOEmail(busqueda);
            return contactos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar contactos: " + e.getMessage());
        }
    }

    // Actualizar estado del contacto
    public ContactoServicioDTO actualizarEstado(Long id, String nuevoEstado, Long atendidoPorId, String notasAtencion) {
        try {
            ContactoServicio contacto = contactoServicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contacto no encontrado"));

            contacto.setEstado(nuevoEstado);
            contacto.setNotasAtencion(notasAtencion);

            if (atendidoPorId != null) {
                Usuario atendidoPor = usuarioRepository.findById(atendidoPorId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                contacto.setAtendidoPor(atendidoPor);
            }

            ContactoServicio contactoActualizado = contactoServicioRepository.save(contacto);
            return convertirADTO(contactoActualizado);

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar estado: " + e.getMessage());
        }
    }

    // Marcar como contactado
    public ContactoServicioDTO marcarComoContactado(Long id, Long atendidoPorId, String notasAtencion) {
        try {
            return actualizarEstado(id, "contactado", atendidoPorId, notasAtencion);
        } catch (Exception e) {
            throw new RuntimeException("Error al marcar como contactado: " + e.getMessage());
        }
    }

    // Marcar como resuelto
    public ContactoServicioDTO marcarComoResuelto(Long id, Long atendidoPorId, String notasAtencion) {
        try {
            return actualizarEstado(id, "resuelto", atendidoPorId, notasAtencion);
        } catch (Exception e) {
            throw new RuntimeException("Error al marcar como resuelto: " + e.getMessage());
        }
    }

    // Obtener contactos atendidos por usuario
    public List<ContactoServicioDTO> obtenerAtendidosPor(Long usuarioId) {
        try {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<ContactoServicio> contactos = contactoServicioRepository.findByAtendidoPorOrderByFechaCreacionDesc(usuario);
            return contactos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener contactos atendidos: " + e.getMessage());
        }
    }

    // Obtener contactos por rango de fechas
    public List<ContactoServicioDTO> obtenerPorRangoFechas(java.time.LocalDateTime fechaInicio, java.time.LocalDateTime fechaFin) {
        try {
            List<ContactoServicio> contactos = contactoServicioRepository.findByFechaCreacionBetweenOrderByFechaCreacionDesc(fechaInicio, fechaFin);
            return contactos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener contactos por fechas: " + e.getMessage());
        }
    }

    // Eliminar contacto
    public boolean eliminar(Long id) {
        try {
            ContactoServicio contacto = contactoServicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contacto no encontrado"));

            contactoServicioRepository.delete(contacto);
            return true;

        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar contacto: " + e.getMessage());
        }
    }

    // Obtener estadísticas
    public java.util.Map<String, Object> obtenerEstadisticas() {
        try {
            java.util.Map<String, Object> estadisticas = new java.util.HashMap<>();
            
            long totalContactos = contactoServicioRepository.count();
            long contactosPendientes = contactoServicioRepository.contarPorEstado("pendiente");
            long contactosEnRevision = contactoServicioRepository.contarPorEstado("en_revision");
            long contactosContactados = contactoServicioRepository.contarPorEstado("contactado");
            long contactosResueltos = contactoServicioRepository.contarPorEstado("resuelto");
            
            estadisticas.put("totalContactos", totalContactos);
            estadisticas.put("contactosPendientes", contactosPendientes);
            estadisticas.put("contactosEnRevision", contactosEnRevision);
            estadisticas.put("contactosContactados", contactosContactados);
            estadisticas.put("contactosResueltos", contactosResueltos);
            
            // Estadísticas por servicio
            List<Object[]> estadisticasServicio = contactoServicioRepository.estadisticasPorServicio();
            for (Object[] stat : estadisticasServicio) {
                String servicio = (String) stat[0];
                Long cantidad = (Long) stat[1];
                estadisticas.put("contactos" + servicio.replace(" ", ""), cantidad);
            }
            
            return estadisticas;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener estadísticas: " + e.getMessage());
        }
    }

    // Convertir Entity a DTO
    private ContactoServicioDTO convertirADTO(ContactoServicio contacto) {
        ContactoServicioDTO dto = new ContactoServicioDTO();
        dto.setId(contacto.getId());
        dto.setNombre(contacto.getNombre());
        dto.setApellido(contacto.getApellido());
        dto.setEmail(contacto.getEmail());
        dto.setTelefono(contacto.getTelefono());
        dto.setMensaje(contacto.getMensaje());
        dto.setDispositivoProblema(contacto.getDispositivoProblema());
        dto.setEstado(contacto.getEstado());
        dto.setFechaCreacion(contacto.getFechaCreacion());
        dto.setFechaActualizacion(contacto.getFechaActualizacion());
        dto.setNotasAtencion(contacto.getNotasAtencion());
        
        // Datos del servicio
        if (contacto.getServicio() != null) {
            dto.setServicioId(contacto.getServicio().getId());
            dto.setServicioTitulo(contacto.getServicio().getTitulo());
            dto.setServicioTipoServicio(contacto.getServicio().getTipoServicio());
        }
        
        // Datos del usuario que atendió
        if (contacto.getAtendidoPor() != null) {
            dto.setAtendidoPorId(contacto.getAtendidoPor().getId());
            dto.setAtendidoPorNombre(contacto.getAtendidoPor().getNombre());
            dto.setAtendidoPorApellido(contacto.getAtendidoPor().getApellido());
        }
        
        return dto;
    }
}


