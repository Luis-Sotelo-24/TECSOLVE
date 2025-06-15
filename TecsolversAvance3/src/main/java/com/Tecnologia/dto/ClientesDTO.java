package com.Tecnologia.dto;

import com.Tecnologia.Modelo.Clientes;
import com.Tecnologia.Modelo.Rol;
import java.util.Collection;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class ClientesDTO {

    private Integer idCli;
    private String nombreCli;
    private String apellidosCli;
    private String dni;
    private String direccion;
    private String telefono;
    private String correo;
    private Collection<String> roles;

    // Constructor que recibe un Cliente y crea el DTO
    public ClientesDTO(Clientes cliente) {
        this.idCli = cliente.getIdCli();
        this.nombreCli = cliente.getNombreCli();
        this.apellidosCli = cliente.getApellidosCli();
        this.dni = cliente.getDni();
        this.direccion = cliente.getDireccion();
        this.telefono = cliente.getTelefono();
        this.correo = cliente.getCorreo();
        // Mapea los roles a String
        this.roles = cliente.getRoles().stream()
            .map(Rol::getNombre)
            .collect(Collectors.toList());
    }
}