package com.Tecnologia.Servicios;

import com.Tecnologia.Modelo.Clientes;
import com.Tecnologia.Modelo.Rol;
import com.Tecnologia.Repositorio.ClientesRepositorio;
import com.Tecnologia.Repositorio.RolRepositorio;
import com.Tecnologia.seguridad.authService;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientesServicio {

    @Autowired
    private ClientesRepositorio clienteRepository;

    @Autowired
    private RolRepositorio rolRepository;

    @Transactional
    public void asignarRolACliente(Integer clienteId, String rolNombre) {
        Clientes cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Rol rol = rolRepository.findByNombre(rolNombre)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        cliente.getRoles().add(rol);
        clienteRepository.save(cliente);
    }

    public List<Clientes> obtenerTodosLosClientes() {
        return clienteRepository.findAll();
    }

    public void agregarCliente(Clientes cliente) {
        String contraseñaEncriptada = authService.encriptar(cliente.getContraseña());
        cliente.setContraseña(contraseñaEncriptada);
        clienteRepository.save(cliente); // Guarda el cliente en la base de datos
    }

    public void borrarCliente(Integer id) {
        clienteRepository.deleteById(id); // Elimina el cliente por ID
    }

    public Optional<Clientes> findClienteByCorreo(String correo) {
        return clienteRepository.findByCorreo(correo);
    }
}
