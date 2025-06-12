package com.techsolvers.ecommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenDTO {
    
    private Long id;
    private String numeroOrden;
    private String estado;
    private String tipoAtencion;
    private String metodoPago;
    private BigDecimal total;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaEntrega;
    private String notas;
    
    // Datos del cliente (guardados al momento de la compra)
    private String clienteNombre;
    private String clienteApellido;
    private String clienteEmail;
    private String clienteTelefono;
    private String clienteDireccion;
    private String clienteDni;
    
    // Datos del usuario actual (referencia)
    private Long usuarioId;
    
    // Datos del trabajador (si aplica)
    private Long trabajadorId;
    private String trabajadorNombre;
    private String trabajadorApellido;
    
    // Items de la orden
    private List<OrdenItemDTO> items = new ArrayList<>();
    
    // Información útil para el frontend
    private Integer totalItems; // Cantidad total de productos diferentes
    private Integer totalCantidad; // Suma de todas las cantidades
    private String estadoDescripcion; // Descripción legible del estado
    private String tipoAtencionDescripcion; // Descripción legible del tipo
    private String metodoPagoDescripcion; // Descripción legible del método
    private String clienteNombreCompleto; // Nombre + apellido
    private String trabajadorNombreCompleto; // Nombre + apellido del trabajador
    
    // Constructor completo
    public OrdenDTO(Long id, String numeroOrden, String estado, String tipoAtencion, String metodoPago,
                   BigDecimal total, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion,
                   LocalDateTime fechaEntrega, String notas, String clienteNombre, String clienteApellido,
                   String clienteEmail, String clienteTelefono, String clienteDireccion, String clienteDni,
                   Long usuarioId, Long trabajadorId, String trabajadorNombre, String trabajadorApellido) {
        this.id = id;
        this.numeroOrden = numeroOrden;
        this.estado = estado;
        this.tipoAtencion = tipoAtencion;
        this.metodoPago = metodoPago;
        this.total = total;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.fechaEntrega = fechaEntrega;
        this.notas = notas;
        this.clienteNombre = clienteNombre;
        this.clienteApellido = clienteApellido;
        this.clienteEmail = clienteEmail;
        this.clienteTelefono = clienteTelefono;
        this.clienteDireccion = clienteDireccion;
        this.clienteDni = clienteDni;
        this.usuarioId = usuarioId;
        this.trabajadorId = trabajadorId;
        this.trabajadorNombre = trabajadorNombre;
        this.trabajadorApellido = trabajadorApellido;
        
        this.estadoDescripcion = convertirEstado(estado);
        this.tipoAtencionDescripcion = convertirTipoAtencion(tipoAtencion);
        this.metodoPagoDescripcion = convertirMetodoPago(metodoPago);
        this.clienteNombreCompleto = (clienteNombre + " " + clienteApellido).trim();
        this.trabajadorNombreCompleto = trabajadorNombre != null ? 
            (trabajadorNombre + " " + trabajadorApellido).trim() : null;
    }
    
    // Constructor para listados simples
    public OrdenDTO(Long id, String numeroOrden, String estado, BigDecimal total,
                   LocalDateTime fechaCreacion, String clienteNombre, String clienteApellido,
                   String metodoPago) {
        this.id = id;
        this.numeroOrden = numeroOrden;
        this.estado = estado;
        this.total = total;
        this.fechaCreacion = fechaCreacion;
        this.clienteNombre = clienteNombre;
        this.clienteApellido = clienteApellido;
        this.metodoPago = metodoPago;
        
        this.estadoDescripcion = convertirEstado(estado);
        this.metodoPagoDescripcion = convertirMetodoPago(metodoPago);
        this.clienteNombreCompleto = (clienteNombre + " " + clienteApellido).trim();
    }
    
    private String convertirEstado(String estado) {
        if (estado == null) return "";
        
        switch (estado.toLowerCase()) {
            case "pendiente":
                return "Pendiente de Proceso";
            case "en_proceso":
                return "En Proceso";
            case "entregado":
                return "Entregado";
            case "cancelado":
                return "Cancelado";
            default:
                return estado;
        }
    }
    
    private String convertirTipoAtencion(String tipo) {
        if (tipo == null) return "";
        
        switch (tipo.toLowerCase()) {
            case "autoservicio":
                return "Compra Online";
            case "presencial":
                return "Atención Presencial";
            default:
                return tipo;
        }
    }
    
    private String convertirMetodoPago(String metodo) {
        if (metodo == null) return "";
        
        switch (metodo.toLowerCase()) {
            case "yape":
                return "Yape";
            case "tarjeta":
                return "Tarjeta de Crédito/Débito";
            case "deposito":
                return "Depósito Bancario";
            default:
                return metodo;
        }
    }
    
    // Método para calcular totales
    public void calcularTotales() {
        if (items != null) {
            this.totalItems = items.size();
            this.totalCantidad = items.stream()
                .mapToInt(OrdenItemDTO::getCantidad)
                .sum();
        } else {
            this.totalItems = 0;
            this.totalCantidad = 0;
        }
    }
}