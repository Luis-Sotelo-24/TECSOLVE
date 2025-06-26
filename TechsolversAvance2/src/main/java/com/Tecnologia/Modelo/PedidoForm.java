package com.Tecnologia.Modelo;
//PedidoForm, Item y Carrito son procesos internos, no tiene BD
import java.util.ArrayList;
import java.util.List;

public class PedidoForm {
    
    private List<ItemPedidoForm> items = new ArrayList<>();

    // getters y setters
    public List<ItemPedidoForm> getItems() {
        return items;
    }

    public void setItems(List<ItemPedidoForm> items) {
        this.items = items;
    }

    public static class ItemPedidoForm {
        private Long id_prod;
        private Integer cantidad;

        // getters y setters
        public Long getId_prod() {
            return id_prod;
        }
        public void setId_prod(Long id_prod) {
            this.id_prod = id_prod;
        }
        public Integer getCantidad() {
            return cantidad;
        }
        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }
}