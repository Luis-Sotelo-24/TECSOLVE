package com.techsolvers.ecommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private Integer stockMinimo;
    private String imagenPrincipal;
    private String imagenesAdicionales;
    private String marca;
    private String modelo;
    private String especificacionesTecnicas;
    private Boolean destacado;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    
    // Datos de la categoría
    private Long categoriaId;
    private String categoriaTitulo;
    private String categoriaImagenUrl;
    
    // Datos del usuario que lo creó
    private Long creadoPorId;
    private String creadoPorNombre;
    private String creadoPorApellido;
    
    // Información útil para el frontend
    private Boolean hayStock; // stock > 0
    private Boolean enStockMinimo; // stock <= stockMinimo
    private String estadoStock; // "disponible", "stock_bajo", "agotado"
    
    // Constructor completo
    public ProductoDTO(Long id, String nombre, String descripcion, BigDecimal precio, 
                      Integer stock, Integer stockMinimo, String imagenPrincipal, 
                      String imagenesAdicionales, String marca, String modelo, 
                      String especificacionesTecnicas, Boolean destacado, Boolean activo,
                      LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion,
                      Long categoriaId, String categoriaTitulo, String categoriaImagenUrl,
                      Long creadoPorId, String creadoPorNombre, String creadoPorApellido) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.imagenPrincipal = imagenPrincipal;
        this.imagenesAdicionales = imagenesAdicionales;
        this.marca = marca;
        this.modelo = modelo;
        this.especificacionesTecnicas = especificacionesTecnicas;
        this.destacado = destacado;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.categoriaId = categoriaId;
        this.categoriaTitulo = categoriaTitulo;
        this.categoriaImagenUrl = categoriaImagenUrl;
        this.creadoPorId = creadoPorId;
        this.creadoPorNombre = creadoPorNombre;
        this.creadoPorApellido = creadoPorApellido;
        
        // Calcular estados
        this.hayStock = stock != null && stock > 0;
        this.enStockMinimo = stock != null && stockMinimo != null && stock <= stockMinimo;
        this.estadoStock = calcularEstadoStock();
    }
    
    // Constructor para catálogo simple
    public ProductoDTO(Long id, String nombre, String descripcion, BigDecimal precio, 
                      Integer stock, String imagenPrincipal, String marca, String modelo,
                      Boolean destacado, String categoriaTitulo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.imagenPrincipal = imagenPrincipal;
        this.marca = marca;
        this.modelo = modelo;
        this.destacado = destacado;
        this.categoriaTitulo = categoriaTitulo;
        this.activo = true;
        
        this.hayStock = stock != null && stock > 0;
        this.estadoStock = calcularEstadoStock();
    }
    
    private String calcularEstadoStock() {
        if (stock == null || stock <= 0) {
            return "agotado";
        } else if (stockMinimo != null && stock <= stockMinimo) {
            return "stock_bajo";
        } else {
            return "disponible";
        }
    }
}









