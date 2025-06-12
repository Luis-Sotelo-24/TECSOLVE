// ========================================
// JAVASCRIPT GLOBAL - TECHSOLVERS ECOMMERCE
// Para: Header, Footer, Buscador, Carrito, Funciones Comunes
// Usar en TODAS las páginas
// ========================================

// CONFIGURACIÓN DEL BACKEND
const API_BASE_URL = 'http://localhost:8080/api';

// ESTADO GLOBAL
window.TechSolvers = {
    usuario: null,
    carrito: [],
    configuracion: {
        productosPerPage: 12,
        moneda: 'S/',
        empresa: {
            nombre: 'TechSolvers',
            telefono: '+51 123 456 789',
            whatsapp: '51123456789',
            direccion: 'Av. Principal 123, Lima, Perú'
        }
    }
};

// ========================================
// INICIALIZACIÓN AL CARGAR LA PÁGINA
// ========================================
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 TechSolvers Global JS cargado');
    
    // Inicializar funcionalidades globales
    inicializarBuscador();
    inicializarCarrito();
    inicializarUsuario();
    inicializarNotificaciones();
    verificarAutenticacion();
    
    console.log('✅ Todas las funcionalidades globales inicializadas');
});

// ========================================
// GESTIÓN DE USUARIO Y AUTENTICACIÓN
// ========================================
function inicializarUsuario() {
    // Verificar si hay usuario logueado
    const usuarioGuardado = localStorage.getItem('usuario');
    if (usuarioGuardado) {
        try {
            window.TechSolvers.usuario = JSON.parse(usuarioGuardado);
            actualizarHeaderUsuario();
        } catch (error) {
            console.error('Error al cargar usuario:', error);
            localStorage.removeItem('usuario');
        }
    }
}

function verificarAutenticacion() {
    const paginasProtegidas = ['dashboard-cliente.html', 'dashboard-admin.html', 'dashboard-trabajador.html'];
    const paginaActual = window.location.pathname.split('/').pop();
    
    if (paginasProtegidas.includes(paginaActual) && !window.TechSolvers.usuario) {
        mostrarNotificacion('Debes iniciar sesión para acceder a esta página', 'warning');
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 2000);
    }
}

function actualizarHeaderUsuario() {
    const userActions = document.querySelector('.user-actions');
    if (!userActions) return;

    if (window.TechSolvers.usuario) {
        const { nombre, rol } = window.TechSolvers.usuario;
        
        userActions.innerHTML = `
            <a href="dashboard-cliente.html">👤 ${nombre}</a>
            <a href="carrito.html" id="carrito-link">🛒 Carrito</a>
            <a href="#" onclick="cerrarSesion()">Cerrar Sesión</a>
        `;
        
        // Redirigir según el rol si está en página de login
        const paginaActual = window.location.pathname.split('/').pop();
        if (paginaActual === 'login.html') {
            redirigirSegunRol(rol);
        }
    } else {
        userActions.innerHTML = `
            <a href="login.html">Login</a>
            <a href="registro-nuevo.html">Registro</a>
            <a href="carrito.html" id="carrito-link">🛒 Carrito</a>
        `;
    }
    
    // Actualizar contador del carrito
    actualizarContadorCarrito();
}

function redirigirSegunRol(rol) {
    const destinos = {
        'admin': 'dashboard-admin.html',
        'trabajador': 'dashboard-trabajador.html',
        'cliente': 'dashboard-cliente.html'
    };
    
    const destino = destinos[rol.toLowerCase()] || 'index.html';
    window.location.href = destino;
}

function cerrarSesion() {
    localStorage.removeItem('usuario');
    window.TechSolvers.usuario = null;
    window.TechSolvers.carrito = [];
    
    mostrarNotificacion('Sesión cerrada correctamente', 'success');
    
    setTimeout(() => {
        window.location.href = 'index.html';
    }, 1500);
}

// ========================================
// FUNCIONALIDAD DEL BUSCADOR
// ========================================
function inicializarBuscador() {
    const searchBox = document.getElementById('search');
    const searchIcon = document.querySelector('.search-icon');
    
    if (searchBox) {
        // Búsqueda al presionar Enter
        searchBox.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                realizarBusqueda(this.value.trim());
            }
        });
        
        // Búsqueda al hacer clic en el ícono
        if (searchIcon) {
            searchIcon.addEventListener('click', function() {
                realizarBusqueda(searchBox.value.trim());
            });
        }
        
        // Obtener término de búsqueda de la URL si existe
        const urlParams = new URLSearchParams(window.location.search);
        const terminoBusqueda = urlParams.get('buscar');
        if (terminoBusqueda) {
            searchBox.value = terminoBusqueda;
        }
    }
}

function realizarBusqueda(termino) {
    if (!termino) {
        mostrarNotificacion('Ingresa un término de búsqueda', 'warning');
        return;
    }
    
    console.log('🔍 Realizando búsqueda:', termino);
    
    // Redirigir a productos con el término de búsqueda
    const url = `productos.html?buscar=${encodeURIComponent(termino)}`;
    
    // Si ya estamos en productos.html, recargar con nueva búsqueda
    if (window.location.pathname.includes('productos.html')) {
        window.location.href = url;
    } else {
        window.location.href = url;
    }
}

// ========================================
// GESTIÓN DEL CARRITO
// ========================================
function inicializarCarrito() {
    // Cargar carrito del localStorage o inicializar vacío
    const carritoGuardado = localStorage.getItem('carrito');
    if (carritoGuardado) {
        try {
            window.TechSolvers.carrito = JSON.parse(carritoGuardado);
        } catch (error) {
            console.error('Error al cargar carrito:', error);
            window.TechSolvers.carrito = [];
        }
    }
    
    actualizarContadorCarrito();
}

function agregarAlCarrito(producto) {
    if (!producto || !producto.id) {
        mostrarNotificacion('Error: Producto inválido', 'error');
        return;
    }

    // Verificar stock
    if (producto.stock <= 0) {
        mostrarNotificacion('Producto sin stock disponible', 'warning');
        return;
    }

    const carritoActual = window.TechSolvers.carrito;
    const existente = carritoActual.find(item => item.id === producto.id);
    
    if (existente) {
        if (existente.cantidad >= producto.stock) {
            mostrarNotificacion('No hay más stock disponible', 'warning');
            return;
        }
        existente.cantidad += 1;
        mostrarNotificacion(`Cantidad actualizada: ${existente.nombre}`, 'success');
    } else {
        carritoActual.push({
            id: producto.id,
            nombre: producto.nombre,
            precio: producto.precio,
            imagen: producto.imagenUrl || 'imagenes/producto-default.jpg',
            cantidad: 1,
            stock: producto.stock
        });
        mostrarNotificacion(`Agregado al carrito: ${producto.nombre}`, 'success');
    }
    
    // Guardar en localStorage
    localStorage.setItem('carrito', JSON.stringify(carritoActual));
    actualizarContadorCarrito();
    
    // Enviar evento personalizado para actualizar otras páginas
    window.dispatchEvent(new CustomEvent('carritoActualizado', { 
        detail: { carrito: carritoActual } 
    }));
}

function eliminarDelCarrito(productoId) {
    const carritoActual = window.TechSolvers.carrito;
    const indice = carritoActual.findIndex(item => item.id === productoId);
    
    if (indice !== -1) {
        const producto = carritoActual[indice];
        carritoActual.splice(indice, 1);
        
        localStorage.setItem('carrito', JSON.stringify(carritoActual));
        actualizarContadorCarrito();
        
        mostrarNotificacion(`Eliminado del carrito: ${producto.nombre}`, 'success');
        
        // Enviar evento personalizado
        window.dispatchEvent(new CustomEvent('carritoActualizado', { 
            detail: { carrito: carritoActual } 
        }));
    }
}

function actualizarCantidadCarrito(productoId, nuevaCantidad) {
    const carritoActual = window.TechSolvers.carrito;
    const item = carritoActual.find(item => item.id === productoId);
    
    if (item) {
        if (nuevaCantidad <= 0) {
            eliminarDelCarrito(productoId);
            return;
        }
        
        if (nuevaCantidad > item.stock) {
            mostrarNotificacion('Cantidad excede el stock disponible', 'warning');
            return;
        }
        
        item.cantidad = nuevaCantidad;
        localStorage.setItem('carrito', JSON.stringify(carritoActual));
        actualizarContadorCarrito();
        
        // Enviar evento personalizado
        window.dispatchEvent(new CustomEvent('carritoActualizado', { 
            detail: { carrito: carritoActual } 
        }));
    }
}

function vaciarCarrito() {
    window.TechSolvers.carrito = [];
    localStorage.setItem('carrito', JSON.stringify([]));
    actualizarContadorCarrito();
    
    mostrarNotificacion('Carrito vaciado', 'success');
    
    // Enviar evento personalizado
    window.dispatchEvent(new CustomEvent('carritoActualizado', { 
        detail: { carrito: [] } 
    }));
}

function actualizarContadorCarrito() {
    const carritoLink = document.getElementById('carrito-link');
    if (!carritoLink) return;
    
    const totalItems = window.TechSolvers.carrito.reduce((total, item) => total + item.cantidad, 0);
    
    // Remover badge existente
    const badgeExistente = carritoLink.querySelector('.carrito-badge');
    if (badgeExistente) {
        badgeExistente.remove();
    }
    
    // Agregar nuevo badge si hay items
    if (totalItems > 0) {
        const badge = document.createElement('span');
        badge.className = 'carrito-badge';
        badge.textContent = totalItems;
        carritoLink.appendChild(badge);
    }
}

function obtenerTotalCarrito() {
    return window.TechSolvers.carrito.reduce((total, item) => {
        return total + (item.precio * item.cantidad);
    }, 0);
}

// ========================================
// SISTEMA DE NOTIFICACIONES
// ========================================
function inicializarNotificaciones() {
    // Crear contenedor de notificaciones si no existe
    if (!document.querySelector('.notificaciones-container')) {
        const container = document.createElement('div');
        container.className = 'notificaciones-container';
        document.body.appendChild(container);
    }
}

function mostrarNotificacion(mensaje, tipo = 'success', duracion = 3000) {
    const container = document.querySelector('.notificaciones-container');
    if (!container) return;
    
    const notificacion = document.createElement('div');
    notificacion.className = `notificacion notificacion-${tipo}`;
    notificacion.textContent = mensaje;
    
    // Icono según el tipo
    const iconos = {
        success: '✅',
        error: '❌',
        warning: '⚠️',
        info: 'ℹ️'
    };
    
    notificacion.innerHTML = `${iconos[tipo] || '📢'} ${mensaje}`;
    
    container.appendChild(notificacion);
    
    // Remover después del tiempo especificado
    setTimeout(() => {
        if (notificacion.parentNode) {
            notificacion.remove();
        }
    }, duracion);
}

// ========================================
// FUNCIONES DE API - CONEXIÓN CON BACKEND
// ========================================
async function realizarPeticionAPI(endpoint, opciones = {}) {
    try {
        const url = `${API_BASE_URL}${endpoint}`;
        const config = {
            headers: {
                'Content-Type': 'application/json',
                ...opciones.headers
            },
            ...opciones
        };
        
        console.log(`🌐 Petición API: ${config.method || 'GET'} ${url}`);
        
        const response = await fetch(url, config);
        
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        
        const data = await response.json();
        console.log('✅ Respuesta API exitosa:', data);
        
        return data;
    } catch (error) {
        console.error('❌ Error en petición API:', error);
        mostrarNotificacion(`Error de conexión: ${error.message}`, 'error');
        throw error;
    }
}

// ========================================
// UTILIDADES GENERALES
// ========================================
function formatearPrecio(precio) {
    const config = window.TechSolvers.configuracion;
    return `${config.moneda} ${precio.toFixed(2)}`;
}

function formatearFecha(fecha) {
    const opciones = { 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    };
    return new Date(fecha).toLocaleDateString('es-PE', opciones);
}

function validarEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}

function validarTelefono(telefono) {
    const regex = /^(\+51|51)?[0-9]{9}$/;
    return regex.test(telefono.replace(/\s/g, ''));
}

function generarSlug(texto) {
    return texto
        .toLowerCase()
        .replace(/[áàäâ]/g, 'a')
        .replace(/[éèëê]/g, 'e')
        .replace(/[íìïî]/g, 'i')
        .replace(/[óòöô]/g, 'o')
        .replace(/[úùüû]/g, 'u')
        .replace(/[ñ]/g, 'n')
        .replace(/[^a-z0-9]/g, '-')
        .replace(/-+/g, '-')
        .replace(/^-|-$/g, '');
}

// ========================================
// EVENTOS GLOBALES
// ========================================

// Escuchar cambios en el carrito
window.addEventListener('carritoActualizado', function(event) {
    console.log('🛒 Carrito actualizado:', event.detail.carrito);
});

// Escuchar tecla Escape para cerrar modales
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        const modalesAbiertos = document.querySelectorAll('.modal.activo, .overlay.activo');
        modalesAbiertos.forEach(modal => {
            modal.classList.remove('activo');
        });
    }
});

// ========================================
// EXPOSICIÓN DE FUNCIONES GLOBALES
// ========================================
window.TechSolvers = {
    ...window.TechSolvers,
    
    // Autenticación
    cerrarSesion,
    verificarAutenticacion,
    
    // Carrito
    agregarAlCarrito,
    eliminarDelCarrito,
    actualizarCantidadCarrito,
    vaciarCarrito,
    obtenerTotalCarrito,
    
    // Búsqueda
    realizarBusqueda,
    
    // Notificaciones
    mostrarNotificacion,
    
    // API
    realizarPeticionAPI,
    
    // Utilidades
    formatearPrecio,
    formatearFecha,
    validarEmail,
    validarTelefono,
    generarSlug
};

console.log('🎯 TechSolvers Global JS completamente cargado y listo');