package com.Tecnologia.Servicios;

import com.Tecnologia.Modelo.Producto;
import com.Tecnologia.dao.ProductoRepositorio;

import java.util.List;

public interface ProductoServicio {
    List<Producto> get();
    Producto get(Long id);
    void save(Producto producto);
    void update(Producto producto);
    void delete(Long id);
    List<Producto> getByCategoria(String categoria);
    List<Producto> buscar(String texto);
    List<Producto> buscarPorTextoYCategoria(String texto, String categoria);
    
}