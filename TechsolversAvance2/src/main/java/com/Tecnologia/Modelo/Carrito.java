package com.Tecnologia.Modelo;
//PedidoForm, Item y Carrito son procesos internos, no tiene BD
import java.util.ArrayList;
import java.util.List;

public class Carrito{

    private List<Item> items = new ArrayList<>();

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void agregarItem(Item nuevo) {
        for (Item item : items) {
            if (item.getId().equals(nuevo.getId())) {
                item.setCantidad(item.getCantidad() + nuevo.getCantidad());
                return;
            }
        }
        items.add(nuevo);
    }

    public void eliminarItem(Long id) {
        items.removeIf(item -> item.getId().equals(id));
    }

    public double getTotal() {
        return items.stream().mapToDouble(Item::getTotal).sum();
    }

    public void actualizarCantidad(Long id, String accion) {
        for (Item item : items) {
            if (item.getId().equals(id)) {
                if ("sumar".equals(accion)) {
                    item.setCantidad(item.getCantidad() + 1);
                } else if ("restar".equals(accion) && item.getCantidad() > 1) {
                    item.setCantidad(item.getCantidad() - 1);
                }
                return;
            }
        }
    }

    public boolean estaVacio() {
        return items.isEmpty();
    }    
    public void vaciar() {
        items.clear();
    }
}
