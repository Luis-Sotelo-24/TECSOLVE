/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyect.tecsolve.servicios;

import com.proyect.tecsolve.interfaces.CategoriasRepositorio;
import com.proyect.tecsolve.modelo.Categorias;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Guillermo
 */
@Service
public class CategoriaServicio {

    @Autowired
    private CategoriasRepositorio categoriasRepository;

    public List<Categorias> listarCategorias() {
        return categoriasRepository.findAll();
    }

    public Optional<Categorias> obtenerCategoriasPorId(Integer id) {
        return categoriasRepository.findById(id);
    }

    public Categorias guardarCategorias(Categorias categorias) {
        return categoriasRepository.save(categorias);
    }

    public void eliminarCategorias(Integer id) {
        categoriasRepository.deleteById(id);
    }
}
