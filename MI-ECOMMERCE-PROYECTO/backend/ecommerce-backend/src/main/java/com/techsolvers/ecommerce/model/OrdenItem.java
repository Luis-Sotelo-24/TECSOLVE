package com.techsolvers.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "orden_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Relación con Orden
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    private Orden orden;
    
    // Relación con Producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer cantidad;
    
    @NotNull(message = "El precio unitario es obligatorio")
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    
    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    // Datos del producto (guardados al momento de la compra)
    @Column(name = "producto_nombre", nullable = false, length = 150)
    private String productoNombre;
    
    @Column(name = "producto_descripcion", length = 500)
    private String productoDescripcion;
    
    @Column(name = "producto_marca", length = 100)
    private String productoMarca;
    
    @Column(name = "producto_modelo", length = 50)
    private String productoModelo;
    
    @PrePersist
    protected void onCreate() {
        calcularSubtotal();
        // Copiar datos del producto
        if (producto != null) {
            this.productoNombre = producto.getNombre();
            this.productoDescripcion = producto.getDescripcion();
            this.productoMarca = producto.getMarca();
            this.productoModelo = producto.getModelo();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        calcularSubtotal();
    }
    
    // Método para calcular subtotal
    public void calcularSubtotal() {
        if (precioUnitario != null && cantidad != null) {
            this.subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
        }
    }
    
    // Constructor para crear item rápidamente
    public OrdenItem(Orden orden, Producto producto, Integer cantidad, BigDecimal precioUnitario) {
        this.orden = orden;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }
}












