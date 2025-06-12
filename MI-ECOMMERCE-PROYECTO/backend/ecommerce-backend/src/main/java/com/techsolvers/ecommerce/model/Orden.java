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
@Table(name = "ordenes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orden {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_orden", unique = true, nullable = false, length = 20)
    private String numeroOrden; // ORD-202501-0001
    
    // Relación con Usuario (cliente)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    // Relación con Trabajador (si fue atendido presencialmente)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trabajador_id")
    private Usuario trabajador;
    
    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "pendiente"; // "pendiente", "en_proceso", "entregado", "cancelado"
    
    @Column(name = "tipo_atencion", nullable = false, length = 20)
    private String tipoAtencion; // "autoservicio", "presencial"
    
    @Column(name = "metodo_pago", nullable = false, length = 20)
    private String metodoPago; // "yape", "tarjeta", "deposito"
    
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    // Datos del cliente (guardados al momento de la compra)
    @Column(name = "cliente_nombre", nullable = false, length = 100)
    private String clienteNombre;
    
    @Column(name = "cliente_apellido", nullable = false, length = 100)
    private String clienteApellido;
    
    @Column(name = "cliente_email", nullable = false, length = 100)
    private String clienteEmail;
    
    @Column(name = "cliente_telefono", length = 15)
    private String clienteTelefono;
    
    @Column(name = "cliente_direccion", length = 200)
    private String clienteDireccion;
    
    @Column(name = "cliente_dni", length = 20)
    private String clienteDni;
    
    // Información adicional de la orden
    @Column(name = "notas", length = 500)
    private String notas;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;
    
    // Relación con OrdenItems
    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrdenItem> items = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
        if (estado == null) {
            estado = "pendiente";
        }
        if (numeroOrden == null) {
            generarNumeroOrden();
        }
        // Copiar datos del usuario
        if (usuario != null) {
            this.clienteNombre = usuario.getNombre();
            this.clienteApellido = usuario.getApellido();
            this.clienteEmail = usuario.getEmail();
            this.clienteTelefono = usuario.getTelefono();
            this.clienteDireccion = usuario.getDireccion();
            this.clienteDni = usuario.getDni();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
        if ("entregado".equals(estado) && fechaEntrega == null) {
            fechaEntrega = LocalDateTime.now();
        }
    }
    
    // Método para generar número de orden único
    private void generarNumeroOrden() {
        LocalDateTime now = LocalDateTime.now();
        String fecha = String.format("%04d%02d", now.getYear(), now.getMonthValue());
        // En implementación real, se debería consultar BD para obtener siguiente número
        this.numeroOrden = "ORD-" + fecha + "-" + String.format("%04d", (int)(Math.random() * 9999) + 1);
    }
    
    // Método para calcular total
    public void calcularTotal() {
        this.total = items.stream()
            .map(OrdenItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Método para crear desde carrito
    public static Orden crearDesdeCarrito(Carrito carrito, String metodoPago) {
        Orden orden = new Orden();
        orden.setUsuario(carrito.getUsuario());
        orden.setTrabajador(carrito.getTrabajador());
        orden.setTipoAtencion(carrito.getTipoAtencion());
        orden.setMetodoPago(metodoPago);
        orden.setTotal(carrito.getTotal());
        
        // Convertir CarritoItems a OrdenItems
        for (CarritoItem carritoItem : carrito.getItems()) {
            OrdenItem ordenItem = new OrdenItem(
                orden,
                carritoItem.getProducto(),
                carritoItem.getCantidad(),
                carritoItem.getPrecioUnitario()
            );
            orden.getItems().add(ordenItem);
        }
        
        return orden;
    }
    
    // Constructor para crear orden rápidamente
    public Orden(Usuario usuario, String metodoPago, String tipoAtencion) {
        this.usuario = usuario;
        this.metodoPago = metodoPago;
        this.tipoAtencion = tipoAtencion;
        this.estado = "pendiente";
        this.items = new ArrayList<>();
    }
}


