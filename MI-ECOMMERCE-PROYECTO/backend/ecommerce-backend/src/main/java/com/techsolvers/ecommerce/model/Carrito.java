package com.techsolvers.ecommerce.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carritos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Carrito {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Relación con Usuario (cliente)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    // Relación con Trabajador (si es atendido presencialmente)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trabajador_id")
    private Usuario trabajador;
    
    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "activo"; // "activo", "procesando", "completado", "abandonado"
    
    @Column(name = "tipo_atencion", nullable = false, length = 20)
    private String tipoAtencion = "autoservicio"; // "autoservicio", "presencial"
    
    @Column(name = "total", precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @Column(name = "session_id", length = 100)
    private String sessionId; // Para carritos de usuarios no registrados
    
    // Relación con CarritoItems
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CarritoItem> items = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (estado == null) {
            estado = "activo";
        }
        if (tipoAtencion == null) {
            tipoAtencion = "autoservicio";
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
    
    // Método para calcular total
    public void calcularTotal() {
        this.total = items.stream()
            .map(CarritoItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Método para agregar item
    public void agregarItem(Producto producto, int cantidad) {
        CarritoItem itemExistente = items.stream()
            .filter(item -> item.getProducto().getId().equals(producto.getId()))
            .findFirst()
            .orElse(null);
            
        if (itemExistente != null) {
            itemExistente.setCantidad(itemExistente.getCantidad() + cantidad);
            itemExistente.calcularSubtotal();
        } else {
            CarritoItem nuevoItem = new CarritoItem(this, producto, cantidad);
            items.add(nuevoItem);
        }
        calcularTotal();
    }
    
    // Método para quitar item
    public void quitarItem(Long productoId) {
        items.removeIf(item -> item.getProducto().getId().equals(productoId));
        calcularTotal();
    }
    
    // Constructor para crear carrito rápidamente
    public Carrito(Usuario usuario) {
        this.usuario = usuario;
        this.estado = "activo";
        this.tipoAtencion = "autoservicio";
        this.total = BigDecimal.ZERO;
        this.items = new ArrayList<>();
    }
    
    public Carrito(Usuario usuario, Usuario trabajador) {
        this.usuario = usuario;
        this.trabajador = trabajador;
        this.estado = "activo";
        this.tipoAtencion = "presencial";
        this.total = BigDecimal.ZERO;
        this.items = new ArrayList<>();
    }
}


































