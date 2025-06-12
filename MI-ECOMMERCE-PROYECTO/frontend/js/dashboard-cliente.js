// ========================================
// JAVASCRIPT ESPECÍFICO - DASHBOARD CLIENTE
// Panel personal completo con todas las funcionalidades
// ========================================

// ESTADO DEL DASHBOARD CLIENTE
let estadoDashboard = {
    usuario: null,
    ordenes: [],
    servicios: [],
    carrito: [],
    seccionActual: 'resumen',
    cargando: {
        resumen: false,
        perfil: false,
        ordenes: false,
        carrito: false,
        servicios: false
    }
};

document.addEventListener('DOMContentLoaded', function() {
    console.log('🏠 Dashboard Cliente JS cargado');
    
    // Verificar autenticación antes de inicializar
    if (!verificarAutenticacionCliente()) {
        return; // Se redirige automáticamente en la función
    }
    
    // Inicializar dashboard
    inicializarDashboard();
    
    console.log('✅ Dashboard Cliente completamente inicializado');
});

// ========================================
// VERIFICACIÓN DE AUTENTICACIÓN
// ========================================
function verificarAutenticacionCliente() {
    const usuario = window.TechSolvers.usuario;
    
    if (!usuario) {
        window.TechSolvers.mostrarNotificacion('Debes iniciar sesión para acceder', 'warning');
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 2000);
        return false;
    }
    
    if (usuario.rol.toLowerCase() !== 'cliente') {
        window.TechSolvers.mostrarNotificacion('Acceso denegado: Solo para clientes', 'error');
        setTimeout(() => {
            const destinos = {
                'admin': 'dashboard-admin.html',
                'trabajador': 'dashboard-trabajador.html'
            };
            window.location.href = destinos[usuario.rol.toLowerCase()] || 'index.html';
        }, 2000);
        return false;
    }
    
    estadoDashboard.usuario = usuario;
    return true;
}

// ========================================
// INICIALIZACIÓN PRINCIPAL
// ========================================
async function inicializarDashboard() {
    try {
        // Configurar interfaz básica
        configurarUsuarioHeader();
        configurarNavegacion();
        configurarEventos();
        
        // Cargar datos iniciales
        await cargarResumenGeneral();
        
        // Mostrar sección inicial
        mostrarSeccion('resumen');
        
        console.log('🎯 Dashboard completamente cargado');
        
    } catch (error) {
        console.error('❌ Error al inicializar dashboard:', error);
        mostrarErrorGeneral();
    }
}

function configurarUsuarioHeader() {
    const { nombre, apellido, fechaCreacion } = estadoDashboard.usuario;
    
    // Actualizar nombre
    const usuarioNombre = document.getElementById('usuario-nombre');
    if (usuarioNombre) {
        usuarioNombre.textContent = `${nombre} ${apellido || ''}`.trim();
    }
    
    // Actualizar fecha de registro
    const usuarioFecha = document.getElementById('usuario-fecha');
    if (usuarioFecha) {
        const fecha = new Date(fechaCreacion || Date.now());
        usuarioFecha.textContent = fecha.toLocaleDateString('es-PE', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    }
}

// ========================================
// NAVEGACIÓN DEL DASHBOARD
// ========================================
function configurarNavegacion() {
    const menuLinks = document.querySelectorAll('.menu-link[data-seccion]');
    
    menuLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const seccion = this.dataset.seccion;
            mostrarSeccion(seccion);
        });
    });
}

function mostrarSeccion(seccion) {
    // Actualizar estado
    estadoDashboard.seccionActual = seccion;
    
    // Actualizar navegación
    document.querySelectorAll('.menu-link').forEach(link => {
        link.classList.remove('active');
    });
    
    const linkActivo = document.querySelector(`.menu-link[data-seccion="${seccion}"]`);
    if (linkActivo) {
        linkActivo.classList.add('active');
    }
    
    // Mostrar sección
    document.querySelectorAll('.seccion-dashboard').forEach(section => {
        section.classList.remove('active');
    });
    
    const seccionElement = document.getElementById(`seccion-${seccion}`);
    if (seccionElement) {
        seccionElement.classList.add('active');
    }
    
    // Cargar datos específicos de la sección
    cargarDatosSeccion(seccion);
    
    console.log(`📂 Sección activa: ${seccion}`);
}

async function cargarDatosSeccion(seccion) {
    if (estadoDashboard.cargando[seccion]) return;
    
    try {
        estadoDashboard.cargando[seccion] = true;
        
        switch (seccion) {
            case 'resumen':
                await cargarResumenGeneral();
                break;
            case 'perfil':
                await cargarPerfilUsuario();
                break;
            case 'ordenes':
                await cargarOrdenesUsuario();
                break;
            case 'carrito':
                await cargarCarritoUsuario();
                break;
            case 'servicios':
                await cargarServiciosUsuario();
                break;
            case 'seguridad':
                // No requiere carga de datos
                break;
        }
        
    } catch (error) {
        console.error(`❌ Error al cargar sección ${seccion}:`, error);
        mostrarErrorSeccion(seccion);
    } finally {
        estadoDashboard.cargando[seccion] = false;
    }
}

// ========================================
// SECCIÓN RESUMEN GENERAL
// ========================================
async function cargarResumenGeneral() {
    try {
        // Cargar datos en paralelo
        const [ordenes, carrito, servicios] = await Promise.all([
            cargarOrdenesBasico(),
            cargarCarritoBasico(),
            cargarServiciosBasico()
        ]);
        
        // Actualizar estadísticas
        actualizarEstadisticas(ordenes, carrito, servicios);
        
        // Mostrar resúmenes
        mostrarUltimoPedido(ordenes);
        mostrarResumenCarrito(carrito);
        mostrarResumenServicios(servicios);
        mostrarActividadReciente(ordenes, servicios);
        
    } catch (error) {
        console.error('❌ Error al cargar resumen:', error);
    }
}

async function cargarOrdenesBasico() {
    try {
        const ordenes = await window.TechSolvers.realizarPeticionAPI(
            `/ordenes/usuario/${estadoDashboard.usuario.id}`
        );
        return ordenes || [];
    } catch (error) {
        console.error('Error al cargar órdenes básico:', error);
        return [];
    }
}

async function cargarCarritoBasico() {
    try {
        const carrito = await window.TechSolvers.realizarPeticionAPI(
            `/carritos/usuario/${estadoDashboard.usuario.id}`
        );
        return carrito || { items: [], total: 0 };
    } catch (error) {
        console.error('Error al cargar carrito básico:', error);
        return { items: [], total: 0 };
    }
}

async function cargarServiciosBasico() {
    try {
        // Buscar servicios por email del usuario
        const servicios = await window.TechSolvers.realizarPeticionAPI(
            `/contacto-servicios?email=${estadoDashboard.usuario.email}`
        );
        return servicios || [];
    } catch (error) {
        console.error('Error al cargar servicios básico:', error);
        return [];
    }
}

function actualizarEstadisticas(ordenes, carrito, servicios) {
    // Total órdenes
    const totalOrdenes = document.getElementById('total-ordenes');
    if (totalOrdenes) {
        totalOrdenes.textContent = ordenes.length;
    }
    
    // Items en carrito
    const carritoItems = document.getElementById('carrito-items');
    if (carritoItems) {
        const totalItems = carrito.items ? carrito.items.length : 0;
        carritoItems.textContent = totalItems;
    }
    
    // Servicios solicitados
    const serviciosSolicitados = document.getElementById('servicios-solicitados');
    if (serviciosSolicitados) {
        serviciosSolicitados.textContent = servicios.length;
    }
}

function mostrarUltimoPedido(ordenes) {
    const container = document.getElementById('ultimo-pedido-content');
    const estadoBadge = document.getElementById('ultimo-pedido-estado');
    
    if (!container) return;
    
    if (ordenes.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <p>Aún no has realizado ningún pedido</p>
                <a href="productos.html" class="btn-action">Hacer mi primer pedido</a>
            </div>
        `;
        if (estadoBadge) estadoBadge.textContent = 'Sin pedidos';
        return;
    }
    
    // Ordenar por fecha más reciente
    const ultimoPedido = ordenes.sort((a, b) => 
        new Date(b.fechaCreacion) - new Date(a.fechaCreacion)
    )[0];
    
    if (estadoBadge) {
        estadoBadge.textContent = ultimoPedido.estado || 'Pendiente';
        estadoBadge.className = `card-badge estado-${(ultimoPedido.estado || '').toLowerCase().replace('_', '-')}`;
    }
    
    container.innerHTML = `
        <div class="orden-resumen">
            <div class="orden-info">
                <strong>Pedido #${ultimoPedido.numeroOrden || ultimoPedido.id}</strong>
                <div class="orden-fecha">${window.TechSolvers.formatearFecha(ultimoPedido.fechaCreacion)}</div>
            </div>
            <div class="orden-total">${window.TechSolvers.formatearPrecio(ultimoPedido.total || 0)}</div>
        </div>
        <button onclick="mostrarSeccion('ordenes')" class="btn-action" style="margin-top: 10px;">
            Ver todos mis pedidos
        </button>
    `;
}

function mostrarResumenCarrito(carrito) {
    const container = document.getElementById('carrito-resumen-content');
    const totalBadge = document.getElementById('carrito-total');
    
    if (!container) return;
    
    const items = carrito.items || [];
    const total = carrito.total || 0;
    
    if (totalBadge) {
        totalBadge.textContent = window.TechSolvers.formatearPrecio(total);
    }
    
    if (items.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <p>Tu carrito está vacío</p>
                <a href="productos.html" class="btn-action">Agregar productos</a>
            </div>
        `;
        return;
    }
    
    const itemsHTML = items.slice(0, 3).map(item => `
        <div class="carrito-item-resumen">
            <strong>${item.producto?.nombre || 'Producto'}</strong>
            <span>Cantidad: ${item.cantidad} - ${window.TechSolvers.formatearPrecio(item.subtotal || 0)}</span>
        </div>
    `).join('');
    
    container.innerHTML = `
        ${itemsHTML}
        ${items.length > 3 ? `<p>... y ${items.length - 3} productos más</p>` : ''}
        <button onclick="mostrarSeccion('carrito')" class="btn-action" style="margin-top: 10px;">
            Ver carrito completo
        </button>
    `;
}

function mostrarResumenServicios(servicios) {
    const container = document.getElementById('servicios-resumen-content');
    const badge = document.getElementById('servicios-badge');
    
    if (!container) return;
    
    const serviciosPendientes = servicios.filter(s => 
        s.estado === 'PENDIENTE' || s.estado === 'EN_REVISION'
    );
    
    if (badge) {
        badge.textContent = serviciosPendientes.length;
    }
    
    if (servicios.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <p>No has solicitado servicios técnicos</p>
                <a href="servicio-tecnico.html" class="btn-action">Solicitar servicio</a>
            </div>
        `;
        return;
    }
    
    const serviciosHTML = serviciosPendientes.slice(0, 2).map(servicio => `
        <div class="servicio-resumen">
            <strong>${servicio.servicio?.titulo || 'Servicio Técnico'}</strong>
            <div class="servicio-estado estado-${(servicio.estado || '').toLowerCase()}">
                ${servicio.estado || 'Pendiente'}
            </div>
        </div>
    `).join('');
    
    container.innerHTML = `
        ${serviciosHTML}
        <button onclick="mostrarSeccion('servicios')" class="btn-action" style="margin-top: 10px;">
            Ver todos los servicios
        </button>
    `;
}

function mostrarActividadReciente(ordenes, servicios) {
    const container = document.getElementByI