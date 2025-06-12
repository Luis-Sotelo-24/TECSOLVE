// ========================================
// JAVASCRIPT ESPECÍFICO - DASHBOARD ADMINISTRADOR
// Panel completo de administración del sistema
// ========================================

// ESTADO DEL DASHBOARD ADMIN
let estadoAdmin = {
    usuario: null,
    seccionActual: 'dashboard',
    datos: {
        usuarios: [],
        productos: [],
        categorias: [],
        servicios: [],
        ordenes: [],
        estadisticas: {}
    },
    filtros: {
        usuarios: {},
        productos: {},
        ordenes: {}
    },
    paginacion: {
        usuarios: { pagina: 1, total: 0 },
        productos: { pagina: 1, total: 0 },
        ordenes: { pagina: 1, total: 0 }
    },
    itemEditando: null,
    cargando: {
        dashboard: false,
        usuarios: false,
        productos: false,
        categorias: false,
        servicios: false,
        ordenes: false,
        reportes: false
    }
};

document.addEventListener('DOMContentLoaded', function() {
    console.log('👑 Dashboard Admin JS cargado');
    
    // Verificar autenticación antes de inicializar
    if (!verificarAutenticacionAdmin()) {
        return;
    }
    
    // Inicializar dashboard
    inicializarDashboardAdmin();
    
    console.log('✅ Dashboard Admin completamente inicializado');
});

// ========================================
// VERIFICACIÓN DE AUTENTICACIÓN
// ========================================
function verificarAutenticacionAdmin() {
    const usuario = window.TechSolvers.usuario;
    
    if (!usuario) {
        window.TechSolvers.mostrarNotificacion('Debes iniciar sesión para acceder', 'warning');
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 2000);
        return false;
    }
    
    if (usuario.rol.toLowerCase() !== 'admin') {
        window.TechSolvers.mostrarNotificacion('Acceso denegado: Solo para administradores', 'error');
        setTimeout(() => {
            const destinos = {
                'cliente': 'dashboard-cliente.html',
                'trabajador': 'dashboard-trabajador.html'
            };
            window.location.href = destinos[usuario.rol.toLowerCase()] || 'index.html';
        }, 2000);
        return false;
    }
    
    estadoAdmin.usuario = usuario;
    return true;
}

// ========================================
// INICIALIZACIÓN PRINCIPAL
// ========================================
async function inicializarDashboardAdmin() {
    try {
        // Configurar interfaz básica
        configurarUsuarioHeader();
        configurarNavegacion();
        configurarEventos();
        configurarModales();
        
        // Cargar datos iniciales del dashboard
        await cargarDashboardPrincipal();
        
        // Mostrar sección inicial
        mostrarSeccion('dashboard');
        
        console.log('🎯 Dashboard Admin completamente cargado');
        
    } catch (error) {
        console.error('❌ Error al inicializar dashboard admin:', error);
        mostrarErrorGeneral();
    }
}

function configurarUsuarioHeader() {
    const { nombre, apellido } = estadoAdmin.usuario;
    
    // Actualizar nombre del admin
    const adminNombre = document.getElementById('admin-nombre');
    if (adminNombre) {
        adminNombre.textContent = `Bienvenido, ${nombre} ${apellido || ''}`.trim();
    }
    
    // Actualizar último acceso
    const ultimoAcceso = document.getElementById('ultimo-acceso');
    if (ultimoAcceso) {
        ultimoAcceso.textContent = new Date().toLocaleDateString('es-PE', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
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
    estadoAdmin.seccionActual = seccion;
    
    // Actualizar navegación
    document.querySelectorAll('.menu-link').forEach(link => {
        link.classList.remove('active');
    });
    
    const linkActivo = document.querySelector(`.menu-link[data-seccion="${seccion}"]`);
    if (linkActivo) {
        linkActivo.classList.add('active');
    }
    
    // Mostrar sección
    document.querySelectorAll('.admin-seccion').forEach(section => {
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
    if (estadoAdmin.cargando[seccion]) return;
    
    try {
        estadoAdmin.cargando[seccion] = true;
        
        switch (seccion) {
            case 'dashboard':
                await cargarDashboardPrincipal();
                break;
            case 'usuarios':
                await cargarUsuarios();
                break;
            case 'productos':
                await cargarProductos();
                break;
            case 'categorias':
                await cargarCategorias();
                break;
            case 'servicios':
                await cargarServicios();
                break;
            case 'ordenes':
                await cargarOrdenes();
                break;
            case 'reportes':
                await cargarReportes();
                break;
            case 'configuracion':
                // No requiere carga de datos
                break;
        }
        
    } catch (error) {
        console.error(`❌ Error al cargar sección ${seccion}:`, error);
        mostrarErrorSeccion(seccion);
    } finally {
        estadoAdmin.cargando[seccion] = false;
    }
}

// ========================================
// DASHBOARD PRINCIPAL
// ========================================
async function cargarDashboardPrincipal() {
    try {
        console.log('📊 Cargando dashboard principal...');
        
        // Cargar estadísticas en paralelo
        const [estadisticas, ordenesRecientes, productosPopulares] = await Promise.all([
            cargarEstadisticasGenerales(),
            cargarOrdenesRecientes(),
            cargarProductosPopulares()
        ]);
        
        // Actualizar estadísticas del header
        actualizarEstadisticasHeader(estadisticas);
        
        // Actualizar widgets
        actualizarWidgetEstadisticas(estadisticas);
        mostrarOrdenesRecientes(ordenesRecientes);
        mostrarProductosPopulares(productosPopulares);
        
        // Cargar gráfico de ventas
        cargarGraficoVentas();
        
        console.log('✅ Dashboard principal cargado');
        
    } catch (error) {
        console.error('❌ Error al cargar dashboard:', error);
    }
}

async function cargarEstadisticasGenerales() {
    try {
        const estadisticas = await window.TechSolvers.realizarPeticionAPI('/usuarios/estadisticas');
        
        // Combinar con otras estadísticas
        const [productosStats, ordenesStats] = await Promise.all([
            window.TechSolvers.realizarPeticionAPI('/productos/estadisticas'),
            window.TechSolvers.realizarPeticionAPI('/ordenes/estadisticas')
        ]);
        
        return {
            ...estadisticas,
            ...productosStats,
            ...ordenesStats
        };
        
    } catch (error) {
        console.error('Error al cargar estadísticas:', error);
        return {
            totalUsuarios: 0,
            totalProductos: 0,
            ordenesDelMes: 0,
            ventasDelMes: 0,
            stockBajo: 0,
            nuevosUsuarios: 0,
            productosVendidos: 0,
            ingresosPeriodo: 0
        };
    }
}

function actualizarEstadisticasHeader(stats) {
    const elementos = {
        'total-usuarios': stats.totalUsuarios || 0,
        'total-productos': stats.totalProductos || 0,
        'total-ordenes': stats.ordenesDelMes || 0,
        'total-ventas': `S/ ${(stats.ventasDelMes || 0).toFixed(2)}`
    };
    
    Object.entries(elementos).forEach(([id, valor]) => {
        const elemento = document.getElementById(id);
        if (elemento) {
            elemento.textContent = valor;
        }
    });
}

function actualizarWidgetEstadisticas(stats) {
    const elementos = {
        'ingresos-periodo': `S/ ${(stats.ingresosPeriodo || 0).toFixed(2)}`,
        'productos-vendidos': stats.productosVendidos || 0,
        'nuevos-usuarios': stats.nuevosUsuarios || 0,
        'stock-bajo': stats.stockBajo || 0
    };
    
    Object.entries(elementos).forEach(([id, valor]) => {
        const elemento = document.getElementById(id);
        if (elemento) {
            elemento.textContent = valor;
        }
    });
}

async function cargarOrdenesRecientes() {
    try {
        const ordenes = await window.TechSolvers.realizarPeticionAPI('/ordenes?limit=5');
        return ordenes || [];
    } catch (error) {
        console.error('Error al cargar órdenes recientes:', error);
        return [];
    }
}

function mostrarOrdenesRecientes(ordenes) {
    const container = document.getElementById('ordenes-recientes');
    if (!container) return;
    
    if (ordenes.length === 0) {
        container.innerHTML = '<div class="empty-state">No hay órdenes recientes</div>';
        return;
    }
    
    const ordenesHTML = ordenes.map(orden => `
        <div class="order-item">
            <div class="order-info">
                <div class="order-number">Pedido #${orden.numeroOrden || orden.id}</div>
                <div class="order-customer">${orden.datosCliente?.nombre || 'Cliente'} ${orden.datosCliente?.apellido || ''}</div>
            </div>
            <div class="order-amount">${window.TechSolvers.formatearPrecio(orden.total || 0)}</div>
            <div class="order-status status-${(orden.estado || '').toLowerCase().replace('_', '-')}">
                ${orden.estado || 'Pendiente'}
            </div>
        </div>
    `).join('');
    
    container.innerHTML = ordenesHTML;
}

async function cargarProductosPopulares() {
    try {
        // Simulación de productos más vendidos - en producción sería un endpoint específico
        const productos = await window.TechSolvers.realizarPeticionAPI('/productos?limit=5');
        return productos || [];
    } catch (error) {
        console.error('Error al cargar productos populares:', error);
        return [];
    }
}

function mostrarProductosPopulares(productos) {
    const container = document.getElementById('productos-populares');
    if (!container) return;
    
    if (productos.length === 0) {
        container.innerHTML = '<div class="empty-state">No hay datos de productos</div>';
        return;
    }
    
    const productosHTML = productos.map(producto => `
        <div class="product-item">
            <div class="product-info">
                <div class="product-name">${producto.nombre}</div>
                <div class="product-category">${producto.categoria?.titulo || 'Sin categoría'}</div>
            </div>
            <div class="product-price">${window.TechSolvers.formatearPrecio(producto.precio || 0)}</div>
        </div>
    `).join('');
    
    container.innerHTML = productosHTML;
}

function cargarGraficoVentas() {
    const canvas = document.getElementById('ventas-chart');
    if (!canvas) return;
    
    // Simulación de datos de ventas - en producción serían datos reales
    const ctx = canvas.getContext('2d');
    
    // Datos simulados para los últimos 7 días
    const datos = [1200, 1900, 3000, 5000, 2000, 3000, 4500];
    const labels = ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom'];
    
    // Crear gráfico simple con Canvas
    canvas.width = 400;
    canvas.height = 200;
    
    const maxValor = Math.max(...datos);
    const padding = 40;
    const width = canvas.width - (padding * 2);
    const height = canvas.height - (padding * 2);
    
    // Limpiar canvas
    ctx.fillStyle = '#f8f9fa';
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    
    // Dibujar líneas de la grilla
    ctx.strokeStyle = '#e0e0e0';
    ctx.lineWidth = 1;
    
    for (let i = 0; i <= 5; i++) {
        const y = padding + (height / 5) * i;
        ctx.beginPath();
        ctx.moveTo(padding, y);
        ctx.lineTo(canvas.width - padding, y);
        ctx.stroke();
    }
    
    // Dibujar línea de ventas
    ctx.strokeStyle = '#dc3545';
    ctx.lineWidth = 3;
    ctx.beginPath();
    
    datos.forEach((valor, index) => {
        const x = padding + (width / (datos.length - 1)) * index;
        const y = padding + height - (valor / maxValor) * height;
        
        if (index === 0) {
            ctx.moveTo(x, y);
        } else {
            ctx.lineTo(x, y);
        }
        
        // Dibujar puntos
        ctx.fillStyle = '#dc3545';
        ctx.beginPath();
        ctx.arc(x, y, 4, 0, 2 * Math.PI);
        ctx.fill();
    });
    
    ctx.stroke();
    
    // Dibujar etiquetas
    ctx.fillStyle = '#666';
    ctx.font = '12px Inter';
    ctx.textAlign = 'center';
    
    labels.forEach((label, index) => {
        const x = padding + (width / (labels.length - 1)) * index;
        ctx.fillText(label, x, canvas.height - 10);
    });
}

// ========================================
// GESTIÓN DE USUARIOS
// ========================================
async function cargarUsuarios() {
    try {
        console.log('👥 Cargando usuarios...');
        
        mostrarCargando('usuarios-tbody');
        
        const usuarios = await window.TechSolvers.realizarPeticionAPI('/usuarios');
        estadoAdmin.datos.usuarios = usuarios || [];
        
        aplicarFiltrosUsuarios();
        
        console.log(`✅ ${estadoAdmin.datos.usuarios.length} usuarios cargados`);
        
    } catch (error) {
        console.error('❌ Error al cargar usuarios:', error);
        mostrarErrorTabla('usuarios-tbody', 'Error al cargar usuarios');
    }
}

function aplicarFiltrosUsuarios() {
    let usuariosFiltrados = [...estadoAdmin.datos.usuarios];
    
    // Aplicar filtros
    const rolFiltro = document.getElementById('usuarios-rol-filtro')?.value;
    const busquedaFiltro = document.getElementById('usuarios-buscar')?.value.toLowerCase();
    
    if (rolFiltro) {
        usuariosFiltrados = usuariosFiltrados.filter(usuario => 
            usuario.rol === rolFiltro
        );
    }
    
    if (busquedaFiltro) {
        usuariosFiltrados = usuariosFiltrados.filter(usuario => 
            usuario.nombre.toLowerCase().includes(busquedaFiltro) ||
            usuario.apellido?.toLowerCase().includes(busquedaFiltro) ||
            usuario.email.toLowerCase().includes(busquedaFiltro)
        );
    }
    
    mostrarTablaUsuarios(usuariosFiltrados);
}

function mostrarTablaUsuarios(usuarios) {
    const tbody = document.getElementById('usuarios-tbody');
    if (!tbody) return;
    
    if (usuarios.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center">No se encontraron usuarios</td>
            </tr>
        `;
        return;
    }
    
    const usuariosHTML = usuarios.map(usuario => `
        <tr>
            <td>${usuario.id}</td>
            <td>${usuario.nombre} ${usuario.apellido || ''}</td>
            <td>${usuario.email}</td>
            <td>
                <span class="badge badge-${usuario.rol.toLowerCase()}">${usuario.rol}</span>
            </td>
            <td>
                <span class="badge ${usuario.activo ? 'badge-success' : 'badge-danger'}">
                    ${usuario.activo ? 'Activo' : 'Inactivo'}
                </span>
            </td>
            <td>${window.TechSolvers.formatearFecha(usuario.fechaCreacion)}</td>
            <td>
                <div class="acciones">
                    <button class="btn-accion btn-editar" onclick="editarUsuario(${usuario.id})">
                        ✏️ Editar
                    </button>
                    <button class="btn-accion btn-eliminar" onclick="eliminarUsuario(${usuario.id})">
                        🗑️ Eliminar
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
    
    tbody.innerHTML = usuariosHTML;
}

function limpiarFiltrosUsuarios() {
    document.getElementById('usuarios-rol-filtro').value = '';
    document.getElementById('usuarios-buscar').value = '';
    aplicarFiltrosUsuarios();
}

// ========================================
// GESTIÓN DE PRODUCTOS
// ========================================
async function cargarProductos() {
    try {
        console.log('📦 Cargando productos...');
        
        mostrarCargando('productos-admin-grid');
        
        const [productos, categorias] = await Promise.all([
            window.TechSolvers.realizarPeticionAPI('/productos'),
            window.TechSolvers.realizarPeticionAPI('/categorias')
        ]);
        
        estadoAdmin.datos.productos = productos || [];
        estadoAdmin.datos.categorias = categorias || [];
        
        // Llenar filtro de categorías
        llenarSelectCategorias();
        
        aplicarFiltrosProductos();
        
        console.log(`✅ ${estadoAdmin.datos.productos.length} productos cargados`);
        
    } catch (error) {
        console.error('❌ Error al cargar productos:', error);
        mostrarErrorGrid('productos-admin-grid', 'Error al cargar productos');
    }
}

function llenarSelectCategorias() {
    const selects = ['productos-categoria-filtro', 'producto-categoria'];
    
    selects.forEach(selectId => {
        const select = document.getElementById(selectId);
        if (select) {
            const opciones = estadoAdmin.datos.categorias.map(categoria => 
                `<option value="${categoria.id}">${categoria.titulo}</option>`
            ).join('');
            
            if (selectId === 'productos-categoria-filtro') {
                select.innerHTML = '<option value="">Todas las categorías</option>' + opciones;
            } else {
                select.innerHTML = '<option value="">Seleccionar categoría</option>' + opciones;
            }
        }
    });
}

function aplicarFiltrosProductos() {
    let productosFiltrados = [...estadoAdmin.datos.productos];
    
    // Aplicar filtros
    const categoriaFiltro = document.getElementById('productos-categoria-filtro')?.value;
    const stockFiltro = document.getElementById('productos-stock-filtro')?.value;
    const busquedaFiltro = document.getElementById('productos-buscar')?.value.toLowerCase();
    
    if (categoriaFiltro) {
        productosFiltrados = productosFiltrados.filter(producto => 
            producto.categoria?.id == categoriaFiltro
        );
    }
    
    if (stockFiltro) {
        switch (stockFiltro) {
            case 'disponible':
                productosFiltrados = productosFiltrados.filter(p => p.stock > 5);
                break;
            case 'bajo':
                productosFiltrados = productosFiltrados.filter(p => p.stock > 0 && p.stock <= 5);
                break;
            case 'agotado':
                productosFiltrados = productosFiltrados.filter(p => p.stock === 0);
                break;
        }
    }
    
    if (busquedaFiltro) {
        productosFiltrados = productosFiltrados.filter(producto => 
            producto.nombre.toLowerCase().includes(busquedaFiltro) ||
            producto.descripcion?.toLowerCase().includes(busquedaFiltro) ||
            producto.marca?.toLowerCase().includes(busquedaFiltro)
        );
    }
    
    mostrarGridProductos(productosFiltrados);
}

function mostrarGridProductos(productos) {
    const grid = document.getElementById('productos-admin-grid');
    if (!grid) return;
    
    if (productos.length === 0) {
        grid.innerHTML = '<div class="empty-state">No se encontraron productos</div>';
        return;
    }
    
    const productosHTML = productos.map(producto => {
        const stockClass = producto.stock > 5 ? 'badge-success' : 
                          producto.stock > 0 ? 'badge-warning' : 'badge-danger';
        const stockText = producto.stock > 5 ? 'En Stock' : 
                         producto.stock > 0 ? `Stock: ${producto.stock}` : 'Agotado';
        
        return `
            <div class="admin-card">
                <img src="${producto.imagenUrl || 'imagenes/producto-default.jpg'}" 
                     alt="${producto.nombre}" 
                     class="admin-card-image"
                     onerror="this.src='imagenes/producto-default.jpg'">
                <div class="admin-card-content">
                    <h3 class="admin-card-title">${producto.nombre}</h3>
                    <div class="admin-card-meta">
                        <span class="admin-card-price">${window.TechSolvers.formatearPrecio(producto.precio)}</span>
                        <span class="admin-card-stock ${stockClass}">${stockText}</span>
                    </div>
                    <p class="admin-card-description">${producto.descripcion || 'Sin descripción'}</p>
                    <div class="admin-card-actions">
                        <button class="btn-accion btn-editar" onclick="editarProducto(${producto.id})">
                            ✏️ Editar
                        </button>
                        <button class="btn-accion btn-eliminar" onclick="eliminarProducto(${producto.id})">
                            🗑️ Eliminar
                        </button>
                    </div>
                </div>
            </div>
        `;
    }).join('');
    
    grid.innerHTML = productosHTML;
}

function limpiarFiltrosProductos() {
    document.getElementById('productos-categoria-filtro').value = '';
    document.getElementById('productos-stock-filtro').value = '';
    document.getElementById('productos-buscar').value = '';
    aplicarFiltrosProductos();
}

// ========================================
// GESTIÓN DE CATEGORÍAS
// ========================================
async function cargarCategorias() {
    try {
        console.log('📂 Cargando categorías...');
        
        mostrarCargando('categorias-admin-grid');
        
        const categorias = await window.TechSolvers.realizarPeticionAPI('/categorias');
        estadoAdmin.datos.categorias = categorias || [];
        
        mostrarGridCategorias();
        
        console.log(`✅ ${estadoAdmin.datos.categorias.length} categorías cargadas`);
        
    } catch (error) {
        console.error('❌ Error al cargar categorías:', error);
        mostrarErrorGrid('categorias-admin-grid', 'Error al cargar categorías');
    }
}

function mostrarGridCategorias() {
    const grid = document.getElementById('categorias-admin-grid');
    if (!grid) return;
    
    if (estadoAdmin.datos.categorias.length === 0) {
        grid.innerHTML = '<div class="empty-state">No hay categorías creadas</div>';
        return;
    }
    
    const categoriasHTML = estadoAdmin.datos.categorias.map(categoria => `
        <div class="admin-card">
            <img src="${categoria.imagenUrl || 'imagenes/categoria-default.jpg'}" 
                 alt="${categoria.titulo}" 
                 class="admin-card-image"
                 onerror="this.src='imagenes/categoria-default.jpg'">
            <div class="admin-card-content">
                <h3 class="admin-card-title">${categoria.titulo}</h3>
                <div class="admin-card-meta">
                    <span class="admin-card-count">${categoria.totalProductos || 0} productos</span>
                    <span class="admin-card-status ${categoria.activo ? 'badge-success' : 'badge-danger'}">
                        ${categoria.activo ? 'Activa' : 'Inactiva'}
                    </span>
                </div>
                <p class="admin-card-description">${categoria.descripcion || 'Sin descripción'}</p>
                <div class="admin-card-actions">
                    <button class="btn-accion btn-editar" onclick="editarCategoria(${categoria.id})">
                        ✏️ Editar
                    </button>
                    <button class="btn-accion btn-eliminar" onclick="eliminarCategoria(${categoria.id})">
                        🗑️ Eliminar
                    </button>
                </div>
            </div>
        </div>
    `).join('');
    
    grid.innerHTML = categoriasHTML;
}

// ========================================
// GESTIÓN DE SERVICIOS
// ========================================
async function cargarServicios() {
    try {
        console.log('🔧 Cargando servicios...');
        
        mostrarCargando('servicios-admin-grid');
        
        const servicios = await window.TechSolvers.realizarPeticionAPI('/servicios');
        estadoAdmin.datos.servicios = servicios || [];
        
        mostrarGridServicios();
        
        console.log(`✅ ${estadoAdmin.datos.servicios.length} servicios cargados`);
        
    } catch (error) {
        console.error('❌ Error al cargar servicios:', error);
        mostrarErrorGrid('servicios-admin-grid', 'Error al cargar servicios');
    }
}

function mostrarGridServicios() {
    const grid = document.getElementById('servicios-admin-grid');
    if (!grid) return;
    
    if (estadoAdmin.datos.servicios.length === 0) {
        grid.innerHTML = '<div class="empty-state">No hay servicios creados</div>';
        return;
    }
    
    const serviciosHTML = estadoAdmin.datos.servicios.map(servicio => `
        <div class="admin-card">
            <div class="admin-card-content">
                <h3 class="admin-card-title">${servicio.titulo}</h3>
                <div class="admin-card-meta">
                    <span class="admin-card-type">${servicio.tipoServicio}</span>
                    <span class="admin-card-price">${servicio.precio ? window.TechSolvers.formatearPrecio(servicio.precio) : 'Consultar'}</span>
                </div>
                <div class="admin-card-meta">
                    <span class="admin-card-status ${servicio.activo ? 'badge-success' : 'badge-danger'}">
                        ${servicio.activo ? 'Activo' : 'Inactivo'}
                    </span>
                    <span class="admin-card-featured ${servicio.mostrarEnInicio ? 'badge-primary' : 'badge-secondary'}">
                        ${servicio.mostrarEnInicio ? 'En Inicio' : 'Oculto'}
                    </span>
                </div>
                <p class="admin-card-description">${servicio.descripcion || 'Sin descripción'}</p>
                <div class="admin-card-actions">
                    <button class="btn-accion btn-editar" onclick="editarServicio(${servicio.id})">
                        ✏️ Editar
                    </button>
                    <button class="btn-accion btn-eliminar" onclick="eliminarServicio(${servicio.id})">
                        🗑️ Eliminar
                    </button>
                </div>
            </div>
        </div>
    `).join('');
    
    grid.innerHTML = serviciosHTML;
}

// ========================================
// GESTIÓN DE ÓRDENES
// ========================================
async function cargarOrdenes() {
    try {
        console.log('📋 Cargando órdenes...');
        
        mostrarCargando('ordenes-admin-lista');
        
        const ordenes = await window.TechSolvers.realizarPeticionAPI('/ordenes');
        estadoAdmin.datos.ordenes = ordenes || [];
        
        aplicarFiltrosOrdenes();
        
        console.log(`✅ ${estadoAdmin.datos.ordenes.length} órdenes cargadas`);
        
    } catch (error) {
        console.error('❌ Error al cargar órdenes:', error);
        mostrarErrorLista('ordenes-admin-lista', 'Error al cargar órdenes');
    }
}

function aplicarFiltrosOrdenes() {
    let ordenesFiltradas = [...estadoAdmin.datos.ordenes];
    
    // Aplicar filtros
    const estadoFiltro = document.getElementById('ordenes-estado-filtro')?.value;
    const fechaDesde = document.getElementById('ordenes-fecha-desde')?.value;
    const fechaHasta = document.getElementById('ordenes-fecha-hasta')?.value;
    const busquedaFiltro = document.getElementById('ordenes-buscar')?.value.toLowerCase();
    
    if (estadoFiltro) {
        ordenesFiltradas = ordenesFiltradas.filter(orden => orden.estado === estadoFiltro);
    }
    
    if (fechaDesde) {
        ordenesFiltradas = ordenesFiltradas.filter(orden => 
            new Date(orden.fechaCreacion) >= new Date(fechaDesde)
        );
    }
    
    if (fechaHasta) {
        ordenesFiltradas = ordenesFiltradas.filter(orden => 
            new Date(orden.fechaCreacion) <= new Date(fechaHasta)
        );
    }
    
    if (busquedaFiltro) {
        ordenesFiltradas = ordenesFiltradas.filter(orden => 
            orden.numeroOrden?.toString().includes(busquedaFiltro) ||
            orden.datosCliente?.nombre?.toLowerCase().includes(busquedaFiltro) ||
            orden.datosCliente?.email?.toLowerCase().includes(busquedaFiltro)
        );
    }
    
    mostrarListaOrdenes(ordenesFiltradas);
}

function mostrarListaOrdenes(ordenes) {
    const lista = document.getElementById('ordenes-admin-lista');
    if (!lista) return;
    
    if (ordenes.length === 0) {
        lista.innerHTML = '<div class="empty-state">No se encontraron órdenes</div>';
        return;
    }
    
    const ordenesHTML = ordenes.map(orden => `
        <div class="orden-admin-card">
            <div class="orden-header">
                <div>
                    <div class="orden-numero">Pedido #${orden.numeroOrden || orden.id}</div>
                    <div class="orden-fecha">${window.TechSolvers.formatearFecha(orden.fechaCreacion)}</div>
                </div>
                <div class="order-status status-${(orden.estado || '').toLowerCase().replace('_', '-')}">
                    ${orden.estado || 'Pendiente'}
                </div>
            </div>
            
            <div class="orden-detalles">
                <div class="detalle-grupo">
                    <h4>Cliente</h4>
                    <div class="orden-cliente">${orden.datosCliente?.nombre || 'N/A'} ${orden.datosCliente?.apellido || ''}</div>
                    <div>${orden.datosCliente?.email || 'N/A'}</div>
                </div>
                
                <div class="detalle-grupo">
                    <h4>Método de Pago</h4>
                    <div>${orden.metodoPago || 'N/A'}</div>
                </div>
                
                <div class="detalle-grupo">
                    <h4>Items</h4>
                    <div>${(orden.items || []).length} productos</div>
                </div>
            </div>
            
            <div class="orden-total">
                Total: ${window.TechSolvers.formatearPrecio(orden.total || 0)}
            </div>
            
            <div class="admin-card-actions" style="margin-top: 15px;">
                <button class="btn-accion btn-ver" onclick="verDetallesOrden(${orden.id})">
                    👁️ Ver Detalles
                </button>
                <button class="btn-accion btn-editar" onclick="cambiarEstadoOrden(${orden.id})">
                    📝 Cambiar Estado
                </button>
            </div>
        </div>
    `).join('');
    
    lista.innerHTML = ordenesHTML;
}

function limpiarFiltrosOrdenes() {
    document.getElementById('ordenes-estado-filtro').value = '';
    document.getElementById('ordenes-fecha-desde').value = '';
    document.getElementById('ordenes-fecha-hasta').value = '';
    document.getElementById('ordenes-buscar').value = '';
    aplicarFiltrosOrdenes();
}

// ========================================
// FUNCIONES DE MODALES
// ========================================
function configurarModales() {
    // Configurar eventos de formularios
    configurarFormularioUsuario();
    configurarFormularioProducto();
    configurarFormularioCategoria();
    configurarFormularioServicio();
    configurarEventosConfiguracion();
}

function configurarFormularioUsuario() {
    const form = document.getElementById('form-usuario');
    if (form) {
        form.addEventListener('submit', async function(e) {
            e.preventDefault();
            await guardarUsuario();
        });
    }
}

function configurarFormularioProducto() {
    const form = document.getElementById('form-producto');
    if (form) {
        form.addEventListener('submit', async function(e) {
            e.preventDefault();
            await guardarProducto();
        });
    }
}

function configurarFormularioCategoria() {
    const form = document.getElementById('form-categoria');
    if (form) {
        form.addEventListener('submit', async function(e) {
            e.preventDefault();
            await guardarCategoria();
        });
    }
}

function configurarFormularioServicio() {
    const form = document.getElementById('form-servicio');
    if (form) {
        form.addEventListener('submit', async function(e) {
            e.preventDefault();
            await guardarServicio();
        });
    }
}

// ========================================
// FUNCIONES CRUD - USUARIOS
// ========================================
function abrirModalUsuario(usuarioId = null) {
    estadoAdmin.itemEditando = usuarioId;
    
    const modal = document.getElementById('modal-usuario-overlay');
    const titulo = document.getElementById('modal-usuario-titulo');
    const form = document.getElementById('form-usuario');
    
    if (usuarioId) {
        titulo.textContent = '✏️ Editar Usuario';
        // Llenar formulario con datos del usuario
        const usuario = estadoAdmin.datos.usuarios.find(u => u.id === usuarioId);
        if (usuario) {
            document.getElementById('usuario-nombre').value = usuario.nombre;
            document.getElementById('usuario-apellido').value = usuario.apellido || '';
            document.getElementById('usuario-email').value = usuario.email;
            document.getElementById('usuario-rol').value = usuario.rol;
            document.getElementById('usuario-telefono').value = usuario.telefono || '';
            
            // Ocultar campo contraseña en edición
            document.getElementById('password-group').style.display = 'none';
        }
    } else {
        titulo.textContent = '➕ Nuevo Usuario';
        form.reset();
        document.getElementById('password-group').style.display = 'block';
    }
    
    modal.classList.add('activo');
}

function cerrarModalUsuario() {
    const modal = document.getElementById('modal-usuario-overlay');
    const form = document.getElementById('form-usuario');
    
    modal.classList.remove('activo');
    form.reset();
    estadoAdmin.itemEditando = null;
}

async function guardarUsuario() {
    try {
        const datos = {
            nombre: document.getElementById('usuario-nombre').value.trim(),
            apellido: document.getElementById('usuario-apellido').value.trim(),
            email: document.getElementById('usuario-email').value.trim(),
            rol: document.getElementById('usuario-rol').value,
            telefono: document.getElementById('usuario-telefono').value.trim()
        };
        
        // Agregar contraseña solo si es nuevo usuario
        if (!estadoAdmin.itemEditando) {
            datos.password = document.getElementById('usuario-password').value;
        }
        
        let resultado;
        if (estadoAdmin.itemEditando) {
            resultado = await window.TechSolvers.realizarPeticionAPI(
                `/usuarios/${estadoAdmin.itemEditando}`,
                { method: 'PUT', body: JSON.stringify(datos) }
            );
        } else {
            resultado = await window.TechSolvers.realizarPeticionAPI(
                '/usuarios/trabajador',
                { method: 'POST', body: JSON.stringify(datos) }
            );
        }
        
        window.TechSolvers.mostrarNotificacion('Usuario guardado correctamente', 'success');
        cerrarModalUsuario();
        await cargarUsuarios();
        
    } catch (error) {
        console.error('Error al guardar usuario:', error);
        window.TechSolvers.mostrarNotificacion('Error al guardar usuario', 'error');
    }
}

function editarUsuario(usuarioId) {
    abrirModalUsuario(usuarioId);
}

async function eliminarUsuario(usuarioId) {
    if (!confirm('¿Estás seguro de que quieres eliminar este usuario?')) {
        return;
    }
    
    try {
        await window.TechSolvers.realizarPeticionAPI(`/usuarios/${usuarioId}`, {
            method: 'DELETE'
        });
        
        window.TechSolvers.mostrarNotificacion('Usuario eliminado correctamente', 'success');
        await cargarUsuarios();
        
    } catch (error) {
        console.error('Error al eliminar usuario:', error);
        window.TechSolvers.mostrarNotificacion('Error al eliminar usuario', 'error');
    }
}

// ========================================
// FUNCIONES CRUD - PRODUCTOS
// ========================================
function abrirModalProducto(productoId = null) {
    estadoAdmin.itemEditando = productoId;
    
    const modal = document.getElementById('modal-producto-overlay');
    const titulo = document.getElementById('modal-producto-titulo');
    const form = document.getElementById('form-producto');
    
    if (productoId) {
        titulo.textContent = '✏️ Editar Producto';
        const producto = estadoAdmin.datos.productos.find(p => p.id === productoId);
        if (producto) {
            document.getElementById('producto-nombre').value = producto.nombre;
            document.getElementById('producto-categoria').value = producto.categoria?.id || '';
            document.getElementById('producto-precio').value = producto.precio;
            document.getElementById('producto-stock').value = producto.stock;
            document.getElementById('producto-marca').value = producto.marca || '';
            document.getElementById('producto-modelo').value = producto.modelo || '';
            document.getElementById('producto-descripcion').value = producto.descripcion || '';
            document.getElementById('producto-imagen').value = producto.imagenUrl || '';
            document.getElementById('producto-destacado').checked = producto.destacado || false;
            document.getElementById('producto-activo').checked = producto.activo !== false;
        }
    } else {
        titulo.textContent = '➕ Nuevo Producto';
        form.reset();
        document.getElementById('producto-activo').checked = true;
    }
    
    modal.classList.add('activo');
}

function cerrarModalProducto() {
    const modal = document.getElementById('modal-producto-overlay');
    const form = document.getElementById('form-producto');
    
    modal.classList.remove('activo');
    form.reset();
    estadoAdmin.itemEditando = null;
}

async function guardarProducto() {
    try {
        const datos = {
            nombre: document.getElementById('producto-nombre').value.trim(),
            categoriaId: parseInt(document.getElementById('producto-categoria').value),
            precio: parseFloat(document.getElementById('producto-precio').value),
            stock: parseInt(document.getElementById('producto-stock').value),
            marca: document.getElementById('producto-marca').value.trim(),
            modelo: document.getElementById('producto-modelo').value.trim(),
            descripcion: document.getElementById('producto-descripcion').value.trim(),
            imagenUrl: document.getElementById('producto-imagen').value.trim(),
            destacado: document.getElementById('producto-destacado').checked,
            activo: document.getElementById('producto-activo').checked
        };
        
        let resultado;
        if (estadoAdmin.itemEditando) {
            resultado = await window.TechSolvers.realizarPeticionAPI(
                `/productos/${estadoAdmin.itemEditando}`,
                { method: 'PUT', body: JSON.stringify(datos) }
            );
        } else {
            resultado = await window.TechSolvers.realizarPeticionAPI(
                '/productos',
                { method: 'POST', body: JSON.stringify(datos) }
            );
        }
        
        window.TechSolvers.mostrarNotificacion('Producto guardado correctamente', 'success');
        cerrarModalProducto();
        await cargarProductos();
        
    } catch (error) {
        console.error('Error al guardar producto:', error);
        window.TechSolvers.mostrarNotificacion('Error al guardar producto', 'error');
    }
}

function editarProducto(productoId) {
    abrirModalProducto(productoId);
}

async function eliminarProducto(productoId) {
    if (!confirm('¿Estás seguro de que quieres eliminar este producto?')) {
        return;
    }
    
    try {
        await window.TechSolvers.realizarPeticionAPI(`/productos/${productoId}`, {
            method: 'DELETE'
        });
        
        window.TechSolvers.mostrarNotificacion('Producto eliminado correctamente', 'success');
        await cargarProductos();
        
    } catch (error) {
        console.error('Error al eliminar producto:', error);
        window.TechSolvers.mostrarNotificacion('Error al eliminar producto', 'error');
    }
}

// ========================================
// FUNCIONES CRUD - CATEGORÍAS
// ========================================
function abrirModalCategoria(categoriaId = null) {
    estadoAdmin.itemEditando = categoriaId;
    
    const modal = document.getElementById('modal-categoria-overlay');
    const titulo = document.getElementById('modal-categoria-titulo');
    const form = document.getElementById('form-categoria');
    
    if (categoriaId) {
        titulo.textContent = '✏️ Editar Categoría';
        const categoria = estadoAdmin.datos.categorias.find(c => c.id === categoriaId);
        if (categoria) {
            document.getElementById('categoria-titulo').value = categoria.titulo;
            document.getElementById('categoria-descripcion').value = categoria.descripcion || '';
            document.getElementById('categoria-imagen').value = categoria.imagenUrl || '';
            document.getElementById('categoria-activa').checked = categoria.activo !== false;
        }
    } else {
        titulo.textContent = '➕ Nueva Categoría';
        form.reset();
        document.getElementById('categoria-activa').checked = true;
    }
    
    modal.classList.add('activo');
}

function cerrarModalCategoria() {
    const modal = document.getElementById('modal-categoria-overlay');
    const form = document.getElementById('form-categoria');
    
    modal.classList.remove('activo');
    form.reset();
    estadoAdmin.itemEditando = null;
}

async function guardarCategoria() {
    try {
        const datos = {
            titulo: document.getElementById('categoria-titulo').value.trim(),
            descripcion: document.getElementById('categoria-descripcion').value.trim(),
            imagenUrl: document.getElementById('categoria-imagen').value.trim(),
            activo: document.getElementById('categoria-activa').checked,
            creadoPor: estadoAdmin.usuario.id
        };
        
        let resultado;
        if (estadoAdmin.itemEditando) {
            resultado = await window.TechSolvers.realizarPeticionAPI(
                `/categorias/${estadoAdmin.itemEditando}`,
                { method: 'PUT', body: JSON.stringify(datos) }
            );
        } else {
            resultado = await window.TechSolvers.realizarPeticionAPI(
                '/categorias',
                { method: 'POST', body: JSON.stringify(datos) }
            );
        }
        
        window.TechSolvers.mostrarNotificacion('Categoría guardada correctamente', 'success');
        cerrarModalCategoria();
        await cargarCategorias();
        
    } catch (error) {
        console.error('Error al guardar categoría:', error);
        window.TechSolvers.mostrarNotificacion('Error al guardar categoría', 'error');
    }
}

function editarCategoria(categoriaId) {
    abrirModalCategoria(categoriaId);
}

async function eliminarCategoria(categoriaId) {
    if (!confirm('¿Estás seguro de que quieres eliminar esta categoría?')) {
        return;
    }
    
    try {
        await window.TechSolvers.realizarPeticionAPI(`/categorias/${categoriaId}`, {
            method: 'DELETE'
        });
        
        window.TechSolvers.mostrarNotificacion('Categoría eliminada correctamente', 'success');
        await cargarCategorias();
        
    } catch (error) {
        console.error('Error al eliminar categoría:', error);
        window.TechSolvers.mostrarNotificacion('Error al eliminar categoría', 'error');
    }
}

// ========================================
// FUNCIONES CRUD - SERVICIOS
// ========================================
function abrirModalServicio(servicioId = null) {
    estadoAdmin.itemEditando = servicioId;
    
    const modal = document.getElementById('modal-servicio-overlay');
    const titulo = document.getElementById('modal-servicio-titulo');
    const form = document.getElementById('form-servicio');
    
    if (servicioId) {
        titulo.textContent = '✏️ Editar Servicio';
        const servicio = estadoAdmin.datos.servicios.find(s => s.id === servicioId);
        if (servicio) {
            document.getElementById('servicio-titulo').value = servicio.titulo;
            document.getElementById('servicio-tipo').value = servicio.tipoServicio;
            document.getElementById('servicio-precio').value = servicio.precio || '';
            document.getElementById('servicio-descripcion').value = servicio.descripcion || '';
            document.getElementById('servicio-mostrar-inicio').checked = servicio.mostrarEnInicio || false;
            document.getElementById('servicio-activo').checked = servicio.activo !== false;
        }
    } else {
        titulo.textContent = '➕ Nuevo Servicio';
        form.reset();
        document.getElementById('servicio-activo').checked = true;
    }
    
    modal.classList.add('activo');
}

function cerrarModalServicio() {
    const modal = document.getElementById('modal-servicio-overlay');
    const form = document.getElementById('form-servicio');
    
    modal.classList.remove('activo');
    form.reset();
    estadoAdmin.itemEditando = null;
}

async function guardarServicio() {
    try {
        const datos = {
            titulo: document.getElementById('servicio-titulo').value.trim(),
            tipoServicio: document.getElementById('servicio-tipo').value,
            precio: parseFloat(document.getElementById('servicio-precio').value) || null,
            descripcion: document.getElementById('servicio-descripcion').value.trim(),
            mostrarEnInicio: document.getElementById('servicio-mostrar-inicio').checked,
            activo: document.getElementById('servicio-activo').checked,
            creadoPor: estadoAdmin.usuario.id
        };
        
        let resultado;
        if (estadoAdmin.itemEditando) {
            resultado = await window.TechSolvers.realizarPeticionAPI(
                `/servicios/${estadoAdmin.itemEditando}`,
                { method: 'PUT', body: JSON.stringify(datos) }
            );
        } else {
            resultado = await window.TechSolvers.realizarPeticionAPI(
                '/servicios',
                { method: 'POST', body: JSON.stringify(datos) }
            );
        }
        
        window.TechSolvers.mostrarNotificacion('Servicio guardado correctamente', 'success');
        cerrarModalServicio();
        await cargarServicios();
        
    } catch (error) {
        console.error('Error al guardar servicio:', error);
        window.TechSolvers.mostrarNotificacion('Error al guardar servicio', 'error');
    }
}

function editarServicio(servicioId) {
    abrirModalServicio(servicioId);
}

async function eliminarServicio(servicioId) {
    if (!confirm('¿Estás seguro de que quieres eliminar este servicio?')) {
        return;
    }
    
    try {
        await window.TechSolvers.realizarPeticionAPI(`/servicios/${servicioId}`, {
            method: 'DELETE'
        });
        
        window.TechSolvers.mostrarNotificacion('Servicio eliminado correctamente', 'success');
        await cargarServicios();
        
    } catch (error) {
        console.error('Error al eliminar servicio:', error);
        window.TechSolvers.mostrarNotificacion('Error al eliminar servicio', 'error');
    }
}

// ========================================
// FUNCIONES DE ÓRDENES
// ========================================
function verDetallesOrden(ordenId) {
    const orden = estadoAdmin.datos.ordenes.find(o => o.id === ordenId);
    if (!orden) return;
    
    alert(`Detalles de la orden #${orden.numeroOrden || orden.id}:\n\n` +
          `Cliente: ${orden.datosCliente?.nombre || 'N/A'}\n` +
          `Email: ${orden.datosCliente?.email || 'N/A'}\n` +
          `Estado: ${orden.estado || 'N/A'}\n` +
          `Total: ${window.TechSolvers.formatearPrecio(orden.total || 0)}\n` +
          `Método de pago: ${orden.metodoPago || 'N/A'}\n` +
          `Fecha: ${window.TechSolvers.formatearFecha(orden.fechaCreacion)}`);
}

async function cambiarEstadoOrden(ordenId) {
    const nuevoEstado = prompt('Nuevo estado (PENDIENTE, EN_PROCESO, ENTREGADO, CANCELADO):');
    if (!nuevoEstado) return;
    
    const estadosValidos = ['PENDIENTE', 'EN_PROCESO', 'ENTREGADO', 'CANCELADO'];
    if (!estadosValidos.includes(nuevoEstado.toUpperCase())) {
        window.TechSolvers.mostrarNotificacion('Estado no válido', 'error');
        return;
    }
    
    try {
        await window.TechSolvers.realizarPeticionAPI(`/ordenes/${ordenId}/estado`, {
            method: 'PUT',
            body: JSON.stringify({ estado: nuevoEstado.toUpperCase() })
        });
        
        window.TechSolvers.mostrarNotificacion('Estado actualizado correctamente', 'success');
        await cargarOrdenes();
        
    } catch (error) {
        console.error('Error al cambiar estado:', error);
        window.TechSolvers.mostrarNotificacion('Error al cambiar estado', 'error');
    }
}

// ========================================
// REPORTES Y ANALYTICS
// ========================================
async function cargarReportes() {
    try {
        console.log('📈 Cargando reportes...');
        
        mostrarCargando('reporte-resumen');
        
        const tipo = document.getElementById('reporte-tipo')?.value || 'ventas';
        const fechaDesde = document.getElementById('reporte-fecha-desde')?.value;
        const fechaHasta = document.getElementById('reporte-fecha-hasta')?.value;
        
        // Generar reporte según el tipo
        let datos;
        switch (tipo) {
            case 'ventas':
                datos = await generarReporteVentas(fechaDesde, fechaHasta);
                break;
            case 'productos':
                datos = await generarReporteProductos();
                break;
            case 'usuarios':
                datos = await generarReporteUsuarios();
                break;
            case 'servicios':
                datos = await generarReporteServicios();
                break;
            default:
                datos = { mensaje: 'Tipo de reporte no válido' };
        }
        
        mostrarReporte(datos, tipo);
        
    } catch (error) {
        console.error('❌ Error al cargar reportes:', error);
        mostrarErrorReporte();
    }
}

async function generarReporteVentas(fechaDesde, fechaHasta) {
    // Simulación de reporte de ventas
    return {
        totalVentas: 45750,
        numeroOrdenes: 123,
        promedioOrden: 372.36,
        metodoPagoMasUsado: 'Yape',
        productoMasVendido: 'Laptop Gaming'
    };
}

async function generarReporteProductos() {
    return {
        totalProductos: estadoAdmin.datos.productos.length,
        stockTotal: estadoAdmin.datos.productos.reduce((sum, p) => sum + p.stock, 0),
        stockBajo: estadoAdmin.datos.productos.filter(p => p.stock <= 5).length,
        categoriaMasProductos: 'Computadoras'
    };
}

async function generarReporteUsuarios() {
    return {
        totalUsuarios: estadoAdmin.datos.usuarios.length,
        administradores: estadoAdmin.datos.usuarios.filter(u => u.rol === 'ADMIN').length,
        trabajadores: estadoAdmin.datos.usuarios.filter(u => u.rol === 'TRABAJADOR').length,
        clientes: estadoAdmin.datos.usuarios.filter(u => u.rol === 'CLIENTE').length
    };
}

async function generarReporteServicios() {
    return {
        totalServicios: estadoAdmin.datos.servicios.length,
        serviciosActivos: estadoAdmin.datos.servicios.filter(s => s.activo).length,
        tipoMasSolicitado: 'Reparación PC'
    };
}

function mostrarReporte(datos, tipo) {
    const container = document.getElementById('reporte-resumen');
    if (!container) return;
    
    let html = `<h3>📊 Reporte de ${tipo.charAt(0).toUpperCase() + tipo.slice(1)}</h3>`;
    
    Object.entries(datos).forEach(([clave, valor]) => {
        const etiqueta = clave.replace(/([A-Z])/g, ' $1').toLowerCase();
        html += `
            <div class="stat-item">
                <div class="stat-details">
                    <div class="stat-label">${etiqueta}</div>
                    <div class="stat-value">${valor}</div>
                </div>
            </div>
        `;
    });
    
    container.innerHTML = html;
}

function mostrarErrorReporte() {
    const container = document.getElementById('reporte-resumen');
    if (container) {
        container.innerHTML = '<div class="error-state">Error al generar el reporte</div>';
    }
}

function generarReporte() {
    cargarReportes();
}

function exportarReporte() {
    window.TechSolvers.mostrarNotificacion('Función de exportación en desarrollo', 'info');
}

function exportarProductos() {
    window.TechSolvers.mostrarNotificacion('Función de exportación en desarrollo', 'info');
}

function exportarOrdenes() {
    window.TechSolvers.mostrarNotificacion('Función de exportación en desarrollo', 'info');
}

// ========================================
// CONFIGURACIÓN DEL SISTEMA
// ========================================
function configurarEventosConfiguracion() {
    // Tabs de configuración
    const configTabs = document.querySelectorAll('.config-tab');
    configTabs.forEach(tab => {
        tab.addEventListener('click', function() {
            const tabTarget = this.dataset.tab;
            
            // Actualizar tabs activos
            configTabs.forEach(t => t.classList.remove('active'));
            this.classList.add('active');
            
            // Mostrar panel correspondiente
            document.querySelectorAll('.config-panel').forEach(panel => {
                panel.classList.remove('active');
            });
            
            const targetPanel = document.getElementById(`config-${tabTarget}`);
            if (targetPanel) {
                targetPanel.classList.add('active');
            }
        });
    });
    
    // Formulario de configuración general
    const formConfigGeneral = document.getElementById('form-config-general');
    if (formConfigGeneral) {
        formConfigGeneral.addEventListener('submit', function(e) {
            e.preventDefault();
            window.TechSolvers.mostrarNotificacion('Configuración guardada', 'success');
        });
    }
}

// ========================================
// FUNCIONES DE EVENTOS
// ========================================
function configurarEventos() {
    // Sidebar móvil
    const sidebarMobileToggle = document.getElementById('sidebar-mobile-toggle');
    const sidebarToggle = document.getElementById('sidebar-toggle');
    
    if (sidebarMobileToggle) {
        sidebarMobileToggle.addEventListener('click', toggleSidebarMobile);
    }
    
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', toggleSidebarMobile);
    }
    
    // Filtros de usuarios
    const usuariosRolFiltro = document.getElementById('usuarios-rol-filtro');
    const usuariosBuscar = document.getElementById('usuarios-buscar');
    
    if (usuariosRolFiltro) {
        usuariosRolFiltro.addEventListener('change', aplicarFiltrosUsuarios);
    }
    
    if (usuariosBuscar) {
        usuariosBuscar.addEventListener('input', debounce(aplicarFiltrosUsuarios, 500));
    }
    
    // Filtros de productos
    const productosCategoriaFiltro = document.getElementById('productos-categoria-filtro');
    const productosStockFiltro = document.getElementById('productos-stock-filtro');
    const productosBuscar = document.getElementById('productos-buscar');
    
    if (productosCategoriaFiltro) {
        productosCategoriaFiltro.addEventListener('change', aplicarFiltrosProductos);
    }
    
    if (productosStockFiltro) {
        productosStockFiltro.addEventListener('change', aplicarFiltrosProductos);
    }
    
    if (productosBuscar) {
        productosBuscar.addEventListener('input', debounce(aplicarFiltrosProductos, 500));
    }
    
    // Filtros de órdenes
    const ordenesEstadoFiltro = document.getElementById('ordenes-estado-filtro');
    const ordenesFechaDesde = document.getElementById('ordenes-fecha-desde');
    const ordenesFechaHasta = document.getElementById('ordenes-fecha-hasta');
    const ordenesBuscar = document.getElementById('ordenes-buscar');
    
    if (ordenesEstadoFiltro) {
        ordenesEstadoFiltro.addEventListener('change', aplicarFiltrosOrdenes);
    }
    
    if (ordenesFechaDesde) {
        ordenesFechaDesde.addEventListener('change', aplicarFiltrosOrdenes);
    }
    
    if (ordenesFechaHasta) {
        ordenesFechaHasta.addEventListener('change', aplicarFiltrosOrdenes);
    }
    
    if (ordenesBuscar) {
        ordenesBuscar.addEventListener('input', debounce(aplicarFiltrosOrdenes, 500));
    }
    
    // Selector de período de estadísticas
    const statsPeriod = document.getElementById('stats-period');
    if (statsPeriod) {
        statsPeriod.addEventListener('change', function() {
            cargarDashboardPrincipal();
        });
    }
    
    // Cerrar modales al hacer clic en overlay
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('modal-overlay')) {
            e.target.classList.remove('activo');
        }
        
        if (e.target.classList.contains('admin-overlay')) {
            toggleSidebarMobile();
        }
    });
    
    // Cerrar modales con tecla Escape
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            document.querySelectorAll('.modal-overlay.activo').forEach(modal => {
                modal.classList.remove('activo');
            });
        }
    });
}

function toggleSidebarMobile() {
    const sidebar = document.getElementById('admin-sidebar');
    const overlay = document.querySelector('.admin-overlay') || crearOverlayAdmin();
    
    if (sidebar) {
        sidebar.classList.toggle('activo');
        overlay.classList.toggle('activo');
        
        if (sidebar.classList.contains('activo')) {
            document.body.style.overflow = 'hidden';
        } else {
            document.body.style.overflow = '';
        }
    }
}

function crearOverlayAdmin() {
    const overlay = document.createElement('div');
    overlay.className = 'admin-overlay';
    document.body.appendChild(overlay);
    return overlay;
}

// ========================================
// FUNCIONES DE UTILIDAD
// ========================================
function mostrarCargando(containerId) {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = '<div class="loading">Cargando datos...</div>';
    }
}

function mostrarErrorTabla(containerId, mensaje) {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = `
            <tr>
                <td colspan="100%" class="error-state">${mensaje}</td>
            </tr>
        `;
    }
}

function mostrarErrorGrid(containerId, mensaje) {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = `<div class="error-state">${mensaje}</div>`;
    }
}

function mostrarErrorLista(containerId, mensaje) {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = `<div class="error-state">${mensaje}</div>`;
    }
}

function mostrarErrorGeneral() {
    const adminContent = document.querySelector('.admin-content');
    if (adminContent) {
        adminContent.innerHTML = `
            <div class="error-state" style="padding: 60px 20px;">
                <h3>❌ Error al cargar el dashboard</h3>
                <p>No se pudo cargar la información del panel de administración.</p>
                <button onclick="window.location.reload()" class="btn-primary">
                    🔄 Recargar página
                </button>
            </div>
        `;
    }
}

function mostrarErrorSeccion(seccion) {
    const seccionElement = document.getElementById(`seccion-${seccion}`);
    if (seccionElement) {
        const content = seccionElement.querySelector('.admin-seccion > *:not(.seccion-header)');
        if (content) {
            content.innerHTML = `
                <div class="error-state">
                    <h3>❌ Error al cargar datos</h3>
                    <p>No se pudieron cargar los datos de esta sección.</p>
                    <button onclick="cargarDatosSeccion('${seccion}')" class="btn-primary">
                        🔄 Reintentar
                    </button>
                </div>
            `;
        }
    }
}

function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// ========================================
// FUNCIÓN DE CERRAR SESIÓN
// ========================================
function cerrarSesion() {
    if (confirm('¿Estás seguro de que quieres cerrar sesión?')) {
        // Limpiar datos del usuario
        localStorage.removeItem('usuario');
        window.TechSolvers.usuario = null;
        
        // Mostrar notificación
        window.TechSolvers.mostrarNotificacion('Sesión cerrada correctamente', 'success');
        
        // Redirigir al login
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 1500);
    }
}

// ========================================
// FUNCIONES PÚBLICAS PARA EL HTML
// ========================================
window.DashboardAdminFunctions = {
    mostrarSeccion,
    abrirModalUsuario,
    cerrarModalUsuario,
    editarUsuario,
    eliminarUsuario,
    abrirModalProducto,
    cerrarModalProducto,
    editarProducto,
    eliminarProducto,
    abrirModalCategoria,
    cerrarModalCategoria,
    editarCategoria,
    eliminarCategoria,
    abrirModalServicio,
    cerrarModalServicio,
    editarServicio,
    eliminarServicio,
    verDetallesOrden,
    cambiarEstadoOrden,
    limpiarFiltrosUsuarios,
    limpiarFiltrosProductos,
    limpiarFiltrosOrdenes,
    generarReporte,
    exportarReporte,
    exportarProductos,
    exportarOrdenes,
    toggleSidebarMobile,
    cerrarSesion
};

// ========================================
// EXPOSICIÓN DE FUNCIONES GLOBALES
// ========================================
// Hacer las funciones accesibles desde el HTML
window.mostrarSeccion = mostrarSeccion;
window.abrirModalUsuario = abrirModalUsuario;
window.cerrarModalUsuario = cerrarModalUsuario;
window.editarUsuario = editarUsuario;
window.eliminarUsuario = eliminarUsuario;
window.abrirModalProducto = abrirModalProducto;
window.cerrarModalProducto = cerrarModalProducto;
window.editarProducto = editarProducto;
window.eliminarProducto = eliminarProducto;
window.abrirModalCategoria = abrirModalCategoria;
window.cerrarModalCategoria = cerrarModalCategoria;
window.editarCategoria = editarCategoria;
window.eliminarCategoria = eliminarCategoria;
window.abrirModalServicio = abrirModalServicio;
window.cerrarModalServicio = cerrarModalServicio;
window.editarServicio = editarServicio;
window.eliminarServicio = eliminarServicio;
window.verDetallesOrden = verDetallesOrden;
window.cambiarEstadoOrden = cambiarEstadoOrden;
window.limpiarFiltrosUsuarios = limpiarFiltrosUsuarios;
window.limpiarFiltrosProductos = limpiarFiltrosProductos;
window.limpiarFiltrosOrdenes = limpiarFiltrosOrdenes;
window.generarReporte = generarReporte;
window.exportarReporte = exportarReporte;
window.exportarProductos = exportarProductos;
window.exportarOrdenes = exportarOrdenes;
window.toggleSidebarMobile = toggleSidebarMobile;
window.cerrarSesion = cerrarSesion;

console.log('👑 Dashboard Admin JS completamente configurado');

