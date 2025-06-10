/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyect.tecsolve.modelo;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;

/**
 *
 * @author Guillermo
 */
@Data
@Entity
@Access(AccessType.FIELD)
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProducto")
    private Integer idProducto;

    @ManyToOne
    @JoinColumn(name = "idCategoria")
    private Categorias categoria;

    @Column(name = "nombreProducto")
    private String nombreProducto;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "precio")
    private BigDecimal precio;
    
    @Column(name = "stock")
    private Integer stock;
    
    @Column(name = "imagenUrl")
    private String imagenUrl;
}
