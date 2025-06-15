package com.Tecnologia.Modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@Data
@NoArgsConstructor
@Entity
@Table(name = "clientes")
public class Clientes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCli;

    private String nombreCli;
    private String apellidosCli;
    private String dni;
    private String direccion;
    private String telefono;
    private String correo;
    private String contraseña;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "cliente_roles",
        joinColumns = @JoinColumn(name = "id_cli"),
        inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    private Collection<Rol> roles = new ArrayList<>();

    // Método para verificar si el cliente tiene un rol específico
    public boolean hasRole(String roleName) {
        return roles.stream().anyMatch(rol -> rol.getNombre().equals(roleName));
    }
}