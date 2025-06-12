package com.techsolvers.ecommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactoServicioDTO {
    
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String mensaje;
    private String dispositivoProblema;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String notasAtencion;
    
    // Datos del servicio
    private Long servicioId;
    private String servicioTitulo;
    private String servicioTipoServicio;
    
    // Datos del usuario que atendió
    private Long atendidoPorId;
    private String atendidoPorNombre;
    private String atendidoPorApellido;
    
    // Información útil para el frontend
    private String nombreCompleto; // Nombre + apellido
    private String estadoDescripcion; // Descripción legible del estado
    private String tipoServicioDescripcion; // Descripción legible del tipo de servicio
    private String atendidoPorNombreCompleto; // Nombre completo de quien atendió
    
    // Constructor completo
    public ContactoServicioDTO(Long id, String nombre, String apellido, String email, String telefono,
                              String mensaje, String dispositivoProblema, String estado,
                              LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion, String notasAtencion,
                              Long servicioId, String servicioTitulo, String servicioTipoServicio,
                              Long atendidoPorId, String atendidoPorNombre, String atendidoPorApellido) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.mensaje = mensaje;
        this.dispositivoProblema = dispositivoProblema;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.notasAtencion = notasAtencion;
        this.servicioId = servicioId;
        this.servicioTitulo = servicioTitulo;
        this.servicioTipoServicio = servicioTipoServicio;
        this.atendidoPorId = atendidoPorId;
        this.atendidoPorNombre = atendidoPorNombre;
        this.atendidoPorApellido = atendidoPorApellido;
        
        this.nombreCompleto = (nombre + " " + apellido).trim();
        this.estadoDescripcion = convertirEstado(estado);
        this.tipoServicioDescripcion = convertirTipoServicio(servicioTipoServicio);
        this.atendidoPorNombreCompleto = atendidoPorNombre != null ? 
            (atendidoPorNombre + " " + atendidoPorApellido).trim() : null;
    }
    
    // Constructor para listados
    public ContactoServicioDTO(Long id, String nombre, String apellido, String email, String mensaje,
                              String estado, LocalDateTime fechaCreacion, String servicioTitulo) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.mensaje = mensaje;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.servicioTitulo = servicioTitulo;
        
        this.nombreCompleto = (nombre + " " + apellido).trim();
        this.estadoDescripcion = convertirEstado(estado);
    }
    
    // Constructor para formulario público (sin datos de atención)
    public ContactoServicioDTO(String nombre, String apellido, String email, String telefono,
                              String mensaje, String dispositivoProblema, Long servicioId) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.mensaje = mensaje;
        this.dispositivoProblema = dispositivoProblema;
        this.servicioId = servicioId;
        this.estado = "pendiente";
        
        this.nombreCompleto = (nombre + " " + apellido).trim();
        this.estadoDescripcion = "Pendiente";
    }
    
    private String convertirEstado(String estado) {
        if (estado == null) return "";
        
        switch (estado.toLowerCase()) {
            case "pendiente":
                return "Pendiente";
            case "en_revision":
                return "En Revisión";
            case "contactado":
                return "Cliente Contactado";
            case "resuelto":
                return "Resuelto";
            default:
                return estado;
        }
    }
    
    private String convertirTipoServicio(String tipo) {
        if (tipo == null) return "";
        
        switch (tipo.toLowerCase()) {
            case "reparacion_pc":
                return "Reparación de PC";
            case "reparacion_celular":
                return "Reparación de Celulares";
            case "reparacion_impresora":
                return "Reparación de Impresoras";
            default:
                return tipo;
        }
    }
}