package com.techsolvers.ecommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {
    
    private Long id;
    private String titulo;
    private String descripcion;
    private String imagenUrl;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    
    // Datos del usuario que la creó (solo lo básico)
    private Long creadoPorId;
    private String creadoPorNombre;
    private String creadoPorApellido;
    
    // Estadísticas útiles
    private Integer totalProductos; // Cantidad de productos en esta categoría
    
    // Constructor para crear desde entidad
    public CategoriaDTO(Long id, String titulo, String descripcion, String imagenUrl, 
                       Boolean activo, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion,
                       Long creadoPorId, String creadoPorNombre, String creadoPorApellido) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.creadoPorId = creadoPorId;
        this.creadoPorNombre = creadoPorNombre;
        this.creadoPorApellido = creadoPorApellido;
    }
    
    // Constructor simple para listados
    public CategoriaDTO(Long id, String titulo, String descripcion, String imagenUrl) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.activo = true;
    }
}

