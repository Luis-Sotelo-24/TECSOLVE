/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.Tecnologia.Modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Guillermo
 */
@Data
@Entity
@Table(name = "reclamo")
public class Reclamo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reclamo")
    private Long idReclamo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cli", nullable = false)
    @JsonIgnore
    private Clientes cliente;

    @Column 
    @NotBlank(message = "El reclamo no puede estar vacío")
    @Size(min = 2, max = 3000, message = "El reclamo debe tener entre 2 y 3000 caracteres")
    private String detalle;
    
    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @Column(length = 20, nullable = false)
    private String estado;

    @Column(nullable = false)
    private String respuesta;
}
