package com.Tecnologia.dao;

import com.Tecnologia.Modelo.Producto;

import java.util.List;

public interface ProductoDAO {
   List<Producto> get();
    Producto get(Long id);
    void save(Producto producto);
    void update(Producto producto);
    void delete(Long id);
    List<Producto> getByCategoria(String categoria);
}