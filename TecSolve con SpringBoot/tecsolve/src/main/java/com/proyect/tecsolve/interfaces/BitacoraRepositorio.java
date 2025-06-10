package com.proyect.tecsolve.interfaces;


import com.proyect.tecsolve.modelo.Bitacora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BitacoraRepositorio extends JpaRepository<Bitacora, Integer> {
    
}
