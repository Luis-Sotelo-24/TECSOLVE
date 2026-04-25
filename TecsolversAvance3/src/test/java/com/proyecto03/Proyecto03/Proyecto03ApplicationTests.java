package com.proyecto03.Proyecto03;

import com.Tecnologia.Controladores.ControladorAdminProductos;
import com.Tecnologia.Controladores.ControladorLogin;
import com.Tecnologia.Controladores.ControladorRegistro;
import com.Tecnologia.Modelo.Clientes;
import com.Tecnologia.Modelo.Producto;
import com.Tecnologia.Modelo.Rol;
import com.Tecnologia.Repositorio.ClientesRepositorio;
import com.Tecnologia.Repositorio.RolRepositorio;
import com.Tecnologia.Servicios.ProductoServicio;
import com.Tecnologia.seguridad.authService;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@ExtendWith(MockitoExtension.class)
class Proyecto03ApplicationTests {

    @Mock
    private ClientesRepositorio clientesRepo;

    @Mock
    private authService auth;

    @Mock
    private ProductoServicio productoServicio;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Model model;
    
    @Mock
    private RolRepositorio rolRepo;

    @InjectMocks
    private ControladorAdminProductos controller;

    @InjectMocks
    private ControladorRegistro controladorRegistro;

    @Test
    void loginAdmin() {

        // Crear controller con constructor
        ControladorLogin controller = new ControladorLogin(auth, clientesRepo);

        Clientes cliente = new Clientes();
        cliente.setCorreo("admin@admin.com");
        cliente.setContraseña("1234");
        cliente.setNombreCli("Admin");

        when(clientesRepo.findByCorreo("admin@admin.com"))
                .thenReturn(Optional.of(cliente));

        doReturn(true).when(auth).verificar(anyString(), anyString());

        HttpSession session = new MockHttpSession();

        String resultado = controller.procesarLogin(
                "admin@admin.com",
                "1234",
                session
        );

        assertEquals("ok_admin", resultado);
        assertEquals("Admin", session.getAttribute("nombreUsuario")); // 🔥 extra
    }

    @Test
    void guardarProducto_ok() {

        Producto producto = new Producto();
        producto.setNombre("Laptop");

        // No hay errores
        when(bindingResult.hasErrors()).thenReturn(false);

        String vista = controller.guardarProducto(producto, bindingResult, model);

        assertEquals("redirect:/adminproductos", vista);

        // Verifica que se guardó
        verify(productoServicio).save(producto);
    }

    @Test
    void registrarCorreoExistente() {

        when(clientesRepo.findByCorreo("test@test.com"))
                .thenReturn(Optional.of(new Clientes()));

        ResponseEntity<String> response = controladorRegistro.registrarUsuario(
                "Luis", "Soto", "test@test.com",
                "1234", "12345678", "Lima", "999999999"
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("correo_exist", response.getBody());

        verify(clientesRepo, never()).save(any());
    }
    
    @Test
    void registroAdmin(){
        when(clientesRepo.findByCorreo("admin@admin.com")).thenReturn(Optional.empty());
        when(auth.encriptar(anyString())).thenReturn("hash");
        
        Rol rolAdmin = new Rol();
        
        rolAdmin.setNombre("ROLE_ADMIN");
        
        when(rolRepo.findByNombre("ROLE_ADMIN")).thenReturn(Optional.of(rolAdmin));
        
        ResponseEntity<String> response = controladorRegistro.registrarUsuario("Admin", "Admin", "admin@admin.com", "1234", "88888888", "aaaaaaaa", "999999999");
        
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals("ok",response.getBody());
        
        verify(clientesRepo).save(any(Clientes.class));
    }
    
    @Test
    void registroUsuario(){
        when(clientesRepo.findByCorreo("usuario@gmail.com")).thenReturn(Optional.empty());
        when(auth.encriptar(anyString())).thenReturn("hash");
        
        Rol rolAdmin = new Rol();
        
        rolAdmin.setNombre("ROLE_USER");
        
        when(rolRepo.findByNombre("ROLE_USER")).thenReturn(Optional.of(rolAdmin));
        
        ResponseEntity<String> response = controladorRegistro.registrarUsuario("Usuario", "Usuario", "usuario@gmail.com", "1234", "88888888", "aaaaaaaa", "999999999");
        
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals("ok",response.getBody());
        
        verify(clientesRepo).save(any(Clientes.class));
    }

}
