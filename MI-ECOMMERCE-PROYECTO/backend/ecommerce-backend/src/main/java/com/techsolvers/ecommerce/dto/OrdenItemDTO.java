package com.techsolvers.ecommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenItemDTO {
    
    private Long id;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    
    // Datos del producto (guardados al momento de la compra)
    private String productoNombre;
    private String productoDescripcion;
    private String productoMarca;
    private String productoModelo;
    
    // Datos actuales del producto (para referencia)
    private Long productoId;
    private String productoImagenPrincipal;
    private BigDecimal productoPrecioActual;
    private Integer productoStockActual;
    
    // Datos de la orden
    private Long ordenId;
    private String numeroOrden;
    
    // Información útil para el frontend
    private String productoNombreCompleto; // Marca + Modelo + Nombre
    private Boolean precioHaCambiado; // Si el precio actual es diferente al de compra
    private String diferenciaPrecio; // Descripción del cambio de precio
    
    // Constructor completo
    public OrdenItemDTO(Long id, Integer cantidad, BigDecimal precioUnitario, BigDecimal subtotal,
                       String productoNombre, String productoDescripcion, String productoMarca,
                       String productoModelo, Long productoId, String productoImagenPrincipal,
                       BigDecimal productoPrecioActual, Integer productoStockActual,
                       Long ordenId, String numeroOrden) {
        this.id = id;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
        this.productoNombre = productoNombre;
        this.productoDescripcion = productoDescripcion;
        this.productoMarca = productoMarca;
        this.productoModelo = productoModelo;
        this.productoId = productoId;
        this.productoImagenPrincipal = productoImagenPrincipal;
        this.productoPrecioActual = productoPrecioActual;
        this.productoStockActual = productoStockActual;
        this.ordenId = ordenId;
        this.numeroOrden = numeroOrden;
        
        this.productoNombreCompleto = construirNombreCompleto();
        this.precioHaCambiado = calcularCambioPrecio();
        this.diferenciaPrecio = calcularDiferenciaPrecio();
    }
    
    // Constructor simple para boletas/reportes
    public OrdenItemDTO(Long id, Integer cantidad, BigDecimal precioUnitario, BigDecimal subtotal,
                       String productoNombre, String productoMarca, String productoModelo) {
        this.id = id;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
        this.productoNombre = productoNombre;
        this.productoMarca = productoMarca;
        this.productoModelo = productoModelo;
        
        this.productoNombreCompleto = construirNombreCompleto();
    }
    
    private String construirNombreCompleto() {
        StringBuilder nombre = new StringBuilder();
        
        if (productoMarca != null && !productoMarca.trim().isEmpty()) {
            nombre.append(productoMarca).append(" ");
        }
        
        if (productoModelo != null && !productoModelo.trim().isEmpty()) {
            nombre.append(productoModelo).append(" ");
        }
        
        if (productoNombre != null && !productoNombre.trim().isEmpty()) {
            nombre.append(productoNombre);
        }
        
        return nombre.toString().trim();
    }
    
    private Boolean calcularCambioPrecio() {
        return precioUnitario != null && productoPrecioActual != null && 
               precioUnitario.compareTo(productoPrecioActual) != 0;
    }
    
    private String calcularDiferenciaPrecio() {
        if (!Boolean.TRUE.equals(precioHaCambiado)) {
            return "Sin cambios";
        }
        
        if (precioUnitario != null && productoPrecioActual != null) {
            BigDecimal diferencia = productoPrecioActual.subtract(precioUnitario);
            
            if (diferencia.compareTo(BigDecimal.ZERO) > 0) {
                return "Precio actual es S/ " + diferencia + " más alto";
            } else {
                return "Precio actual es S/ " + diferencia.abs() + " más bajo";
            }
        }
        
        return "No disponible";
    }
}

