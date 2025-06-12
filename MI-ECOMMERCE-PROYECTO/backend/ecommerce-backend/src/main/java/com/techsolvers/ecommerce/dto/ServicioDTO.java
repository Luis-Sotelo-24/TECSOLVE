package com.techsolvers.ecommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicioDTO {
    
    private Long id;
    private String titulo;
    private String descripcion;
    private String tipoServicio;
    private String imagenUrl;
    private String contenidoDetallado;
    private BigDecimal precioDesde;
    private String tiempoEstimado;
    private Boolean activo;
    private Boolean mostrarEnInicio;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    
    // Datos del usuario que lo creó
    private Long creadoPorId;
    private String creadoPorNombre;
    private String creadoPorApellido;
    
    // Información útil para el frontend
    private String tipoServicioNombre; // Nombre legible del tipo
    private Integer totalContactos; // Cantidad de contactos recibidos
    
    // Constructor completo
    public ServicioDTO(Long id, String titulo, String descripcion, String tipoServicio,
                      String imagenUrl, String contenidoDetallado, BigDecimal precioDesde,
                      String tiempoEstimado, Boolean activo, Boolean mostrarEnInicio,
                      LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion,
                      Long creadoPorId, String creadoPorNombre, String creadoPorApellido) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipoServicio = tipoServicio;
        this.imagenUrl = imagenUrl;
        this.contenidoDetallado = contenidoDetallado;
        this.precioDesde = precioDesde;
        this.tiempoEstimado = tiempoEstimado;
        this.activo = activo;
        this.mostrarEnInicio = mostrarEnInicio;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.creadoPorId = creadoPorId;
        this.creadoPorNombre = creadoPorNombre;
        this.creadoPorApellido = creadoPorApellido;
        
        this.tipoServicioNombre = convertirTipoServicio(tipoServicio);
    }
    
    // Constructor para página principal
    public ServicioDTO(Long id, String titulo, String descripcion, String tipoServicio,
                      String imagenUrl, BigDecimal precioDesde, String tiempoEstimado) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipoServicio = tipoServicio;
        this.imagenUrl = imagenUrl;
        this.precioDesde = precioDesde;
        this.tiempoEstimado = tiempoEstimado;
        this.activo = true;
        this.mostrarEnInicio = true;
        
        this.tipoServicioNombre = convertirTipoServicio(tipoServicio);
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

