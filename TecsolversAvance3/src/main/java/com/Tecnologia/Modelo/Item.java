package com.Tecnologia.Modelo;
//PedidoForm, Item y Carrito son procesos internos, no tiene BD
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Item {

    private Long id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int cantidad;

    public double getTotal() {
        return precio * cantidad;
    }

    public Item(Long id, String nombre, String descripcion, double precio, int cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.cantidad = cantidad;
    }
}
