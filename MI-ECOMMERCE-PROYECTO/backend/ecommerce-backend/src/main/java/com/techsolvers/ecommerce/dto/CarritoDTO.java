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
public class CarritoDTO {
    
    private Long id;
    private String estado;
    private String tipoAtencion;
    private BigDecimal total;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String sessionId;
    
    // Datos del usuario (cliente)
    private Long usuarioId;
    private String usuarioNombre;
    private String usuarioApellido;
    private String usuarioEmail;
    
    // Datos del trabajador (si aplica)
    private Long trabajadorId;
    private String trabajadorNombre;
    private String trabajadorApellido;
    
    // Items del carrito
    private List<CarritoItemDTO> items = new ArrayList<>();
    
    // Información útil para el frontend
    private Integer totalItems; // Cantidad total de productos
    private Integer totalCantidad; // Suma de todas las cantidades
    private String estadoDescripcion; // Descripción legible del estado
    private String tipoAtencionDescripcion; // Descripción legible del tipo
    
    // Constructor completo
    public CarritoDTO(Long id, String estado, String tipoAtencion, BigDecimal total,
                     LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion, String sessionId,
                     Long usuarioId, String usuarioNombre, String usuarioApellido, String usuarioEmail,
                     Long trabajadorId, String trabajadorNombre, String trabajadorApellido) {
        this.id = id;
        this.estado = estado;
        this.tipoAtencion = tipoAtencion;
        this.total = total;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.sessionId = sessionId;
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.usuarioApellido = usuarioApellido;
        this.usuarioEmail = usuarioEmail;
        this.trabajadorId = trabajadorId;
        this.trabajadorNombre = trabajadorNombre;
        this.trabajadorApellido = trabajadorApellido;
        
        this.estadoDescripcion = convertirEstado(estado);
        this.tipoAtencionDescripcion = convertirTipoAtencion(tipoAtencion);
    }
    
    // Constructor simple
    public CarritoDTO(Long id, String estado, String tipoAtencion, BigDecimal total,
                     String usuarioNombre, String usuarioApellido) {
        this.id = id;
        this.estado = estado;
        this.tipoAtencion = tipoAtencion;
        this.total = total;
        this.usuarioNombre = usuarioNombre;
        this.usuarioApellido = usuarioApellido;
        
        this.estadoDescripcion = convertirEstado(estado);
        this.tipoAtencionDescripcion = convertirTipoAtencion(tipoAtencion);
    }
    
    private String convertirEstado(String estado) {
        if (estado == null) return "";
        
        switch (estado.toLowerCase()) {
            case "activo":
                return "Carrito Activo";
            case "procesando":
                return "Procesando Compra";
            case "completado":
                return "Compra Completada";
            case "abandonado":
                return "Carrito Abandonado";
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
    
    // Método para calcular totales
    public void calcularTotales() {
        if (items != null) {
            this.totalItems = items.size();
            this.totalCantidad = items.stream()
                .mapToInt(CarritoItemDTO::getCantidad)
                .sum();
        } else {
            this.totalItems = 0;
            this.totalCantidad = 0;
        }
    }
}




