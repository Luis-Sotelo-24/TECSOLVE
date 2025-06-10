/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.proyect.tecsolve.interfaces;

import com.proyect.tecsolve.modelo.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Guillermo
 */
@Repository
public interface VentaRepositorio extends JpaRepository<Venta,Integer>{
    
}
