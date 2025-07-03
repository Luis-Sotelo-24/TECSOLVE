/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.Tecnologia.Repositorio;

import com.Tecnologia.Modelo.Reclamo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Guillermo
 */
public interface ReclamosRepositorio extends JpaRepository<Reclamo, Long>{
    List<Reclamo> findByClienteIdCli(Integer idCli);
    List<Reclamo> findByEstado(String estado);
}
