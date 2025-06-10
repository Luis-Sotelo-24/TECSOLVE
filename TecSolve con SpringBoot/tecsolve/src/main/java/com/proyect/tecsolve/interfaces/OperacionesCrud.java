package com.proyect.tecsolve.interfaces;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author Guillermo
 * @param <T>
 */
public interface OperacionesCrud<T> {

    List<T> getList();            // Listar todos

    Optional<T> get(int id);      // Buscar por ID

    T save(T entity);             // Guardar o actualizar

    void delete(int id);          // Eliminar por ID
}


















