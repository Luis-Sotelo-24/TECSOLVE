package com.techsolvers.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    @Column(nullable = false, length = 150)
    private String nombre;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    @Column(nullable = false, length = 1000)
    private String descripcion;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock;
    
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    @Column(name = "stock_minimo")
    private Integer stockMinimo = 5;
    
    @Column(name = "imagen_principal", length = 255)
    private String imagenPrincipal;
    
    @Column(name = "imagenes_adicionales", length = 1000)
    private String imagenesAdicionales; // JSON array de URLs
    
    @Size(max = 100, message = "La marca no puede exceder 100 caracteres")
    @Column(length = 100)
    private String marca;
    
    @Size(max = 50, message = "El modelo no puede exceder 50 caracteres")
    @Column(length = 50)
    private String modelo;
    
    @Column(name = "especificaciones_tecnicas", length = 1500)
    private String especificacionesTecnicas; // JSON con especificaciones
    
    @Column(nullable = false)
    private Boolean destacado = false;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    // Relación con Categoría
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
    
    // Relación con Usuario (quien creó el producto)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por", nullable = false)
    private Usuario creadoPor;
    
    // Relación con CarritoItem
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CarritoItem> carritoItems;
    
    // Relación con OrdenItem
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrdenItem> ordenItems;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (activo == null) {
            activo = true;
        }
        if (destacado == null) {
            destacado = false;
        }
        if (stockMinimo == null) {
            stockMinimo = 5;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
    
    // Método para verificar si hay stock disponible
    public boolean hayStock(int cantidad) {
        return this.stock >= cantidad;
    }
    
    // Método para verificar si está en stock mínimo
    public boolean enStockMinimo() {
        return this.stock <= this.stockMinimo;
    }
    
    // Método para reducir stock
    public void reducirStock(int cantidad) {
        if (this.stock >= cantidad) {
            this.stock -= cantidad;
        } else {
            throw new IllegalArgumentException("Stock insuficiente");
        }
    }
    
    // Método para aumentar stock
    public void aumentarStock(int cantidad) {
        this.stock += cantidad;
    }
    
    // Constructor para crear producto rápidamente
    public Producto(String nombre, String descripcion, BigDecimal precio, Integer stock, 
                   Categoria categoria, Usuario creadoPor) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
        this.creadoPor = creadoPor;
        this.activo = true;
        this.destacado = false;
    }
}


