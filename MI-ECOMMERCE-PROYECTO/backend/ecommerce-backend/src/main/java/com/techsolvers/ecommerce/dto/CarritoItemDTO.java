package com.techsolvers.ecommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItemDTO {
    
    private Long id;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private LocalDateTime fechaAgregado;
    
    // Datos del carrito
    private Long carritoId;
    
    // Datos del producto
    private Long productoId;
    private String productoNombre;
    private String productoDescripcion;
    private String productoImagenPrincipal;
    private String productoMarca;
    private String productoModelo;
    private Integer productoStock; // Stock actual del producto
    private BigDecimal productoPrecioActual; // Precio actual del producto
    
    // Datos de la categoría del producto
    private String categoriaTitulo;
    
    // Información útil para el frontend
    private Boolean stockSuficiente; // Si hay stock suficiente para la cantidad solicitada
    private Boolean precioActualizado; // Si el precio unitario coincide con el precio actual
    private String disponibilidad; // "disponible", "stock_limitado", "no_disponible"
    
    // Constructor completo
    public CarritoItemDTO(Long id, Integer cantidad, BigDecimal precioUnitario, BigDecimal subtotal,
                         LocalDateTime fechaAgregado, Long carritoId, Long productoId,
                         String productoNombre, String productoDescripcion, String productoImagenPrincipal,
                         String productoMarca, String productoModelo, Integer productoStock,
                         BigDecimal productoPrecioActual, String categoriaTitulo) {
        this.id = id;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
        this.fechaAgregado = fechaAgregado;
        this.carritoId = carritoId;
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.productoDescripcion = productoDescripcion;
        this.productoImagenPrincipal = productoImagenPrincipal;
        this.productoMarca = productoMarca;
        this.productoModelo = productoModelo;
        this.productoStock = productoStock;
        this.productoPrecioActual = productoPrecioActual;
        this.categoriaTitulo = categoriaTitulo;
        
        this.stockSuficiente = calcularStockSuficiente();
        this.precioActualizado = calcularPrecioActualizado();
        this.disponibilidad = calcularDisponibilidad();
    }
    
    // Constructor simple para listados
    public CarritoItemDTO(Long id, Integer cantidad, BigDecimal precioUnitario, BigDecimal subtotal,
                         String productoNombre, String productoImagenPrincipal, String productoMarca) {
        this.id = id;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
        this.productoNombre = productoNombre;
        this.productoImagenPrincipal = productoImagenPrincipal;
        this.productoMarca = productoMarca;
    }
    
    private Boolean calcularStockSuficiente() {
        return productoStock != null && cantidad != null && productoStock >= cantidad;
    }
    
    private Boolean calcularPrecioActualizado() {
        return precioUnitario != null && productoPrecioActual != null && 
               precioUnitario.compareTo(productoPrecioActual) == 0;
    }
    
    private String calcularDisponibilidad() {
        if (productoStock == null || productoStock <= 0) {
            return "no_disponible";
        } else if (!stockSuficiente) {
            return "stock_limitado";
        } else {
            return "disponible";
        }
    }
}



