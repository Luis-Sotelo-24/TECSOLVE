// ========================================
// JAVASCRIPT ESPECÍFICO - PÁGINA DE INICIO (INDEX)
// Funcionalidades exclusivas de la página principal
// ========================================

// CONFIGURACIÓN ESPECÍFICA DEL INDEX
const CONFIG_INDEX = {
    categoriasAMostrar: 6,
    productosDestacados: 8,
    serviciosAMostrar: 3
};

// ESTADO DEL INDEX
let estadoIndex = {
    categorias: [],
    productosDestacados: [],
    servicios: [],
    cargando: {
        categorias: false,
        productos: false,
        servicios: false
    }
};

document.addEventListener('DOMContentLoaded', function() {
    console.log('🏠 Index JS cargado');
    
    // Inicializar página principal
    inicializarIndex();
    
    console.log('✅ Index completamente inicializado');
});

// ========================================
// INICIALIZACIÓN PRINCIPAL
// ========================================
async function inicializarIndex() {
    try {
        // Mostrar estados de carga
        mostrarCargando();
        
        // Cargar datos en paralelo
        await Promise.all([
            cargarCategorias(),
            cargarProductosDestacados(),
            cargarServicios()
        ]);
        
        // Renderizar contenido
        renderizarCategorias();
        renderizarProductosDestacados();
        renderizarServicios();
        
        // Inicializar eventos específicos
        inicializarEventosIndex();
        
        console.log('🎯 Index completamente cargado');
        
    } catch (error) {
        console.error('❌ Error al inicializar index:', error);
        mostrarErrorGeneral();
    }
}

// ========================================
// CARGA DE DATOS DESDE EL BACKEND
// ========================================
async function cargarCategorias() {
    estadoIndex.cargando.categorias = true;
    
    try {
        console.log('📂 Cargando categorías...');
        
        const categorias = await window.TechSolvers.realizarPeticionAPI('/categorias');
        estadoIndex.categorias = categorias.slice(0, CONFIG_INDEX.categoriasAMostrar);
        
        console.log('✅ Categorías cargadas:', estadoIndex.categorias.length);
        
    } catch (error) {
        console.error('❌ Error al cargar categorías:', error);
        estadoIndex.categorias = [];
    } finally {
        estadoIndex.cargando.categorias = false;
    }
}

async function cargarProductosDestacados() {
    estadoIndex.cargando.productos = true;
    
    try {
        console.log('⭐ Cargando productos destacados...');
        
        const productos = await window.TechSolvers.realizarPeticionAPI('/productos/destacados');
        estadoIndex.productosDestacados = productos.slice(0, CONFIG_INDEX.productosDestacados);
        
        console.log('✅ Productos destacados cargados:', estadoIndex.productosDestacados.length);
        
    } catch (error) {
        console.error('❌ Error al cargar productos destacados:', error);
        // Fallback: cargar productos normales
        try {
            const productos = await window.TechSolvers.realizarPeticionAPI('/productos');
            estadoIndex.productosDestacados = productos.slice(0, CONFIG_INDEX.productosDestacados);
        } catch (fallbackError) {
            estadoIndex.productosDestacados = [];
        }
    } finally {
        estadoIndex.cargando.productos = false;
    }
}

async function cargarServicios() {
    estadoIndex.cargando.servicios = true;
    
    try {
        console.log('🔧 Cargando servicios...');
        
        const servicios = await window.TechSolvers.realizarPeticionAPI('/servicios/inicio');
        estadoIndex.servicios = servicios.slice(0, CONFIG_INDEX.serviciosAMostrar);
        
        console.log('✅ Servicios cargados:', estadoIndex.servicios.length);
        
    } catch (error) {
        console.error('❌ Error al cargar servicios:', error);
        estadoIndex.servicios = [];
    } finally {
        estadoIndex.cargando.servicios = false;
    }
}

// ========================================
// RENDERIZADO DE CONTENIDO
// ========================================
function mostrarCargando() {
    const contenedores = [
        '.categorias-grid',
        '.productos-grid',
        '.servicios-grid'
    ];
    
    contenedores.forEach(selector => {
        const contenedor = document.querySelector(selector);
        if (contenedor) {
            contenedor.innerHTML = `
                <div class="loading">
                    <div class="loading-spinner"></div>
                    <p>Cargando contenido...</p>
                </div>
            `;
        }
    });
}

function renderizarCategorias() {
    const contenedor = document.querySelector('.categorias-grid');
    if (!contenedor) return;
    
    if (estadoIndex.categorias.length === 0) {
        contenedor.innerHTML = `
            <div class="error-mensaje">
                <p>❌ No se pudieron cargar las categorías</p>
                <button onclick="cargarCategorias().then(renderizarCategorias)" class="btn-primary">
                    Reintentar
                </button>
            </div>
        `;
        return;
    }
    
    const categoriasHTML = estadoIndex.categorias.map(categoria => {
        const icono = obtenerIconoCategoria(categoria.titulo);
        const contador = categoria.totalProductos || 0;
        
        return `
            <div class="categoria-card" onclick="irACategoria(${categoria.id})">
                <span class="categoria-icono">${icono}</span>
                <h3 class="categoria-titulo">${categoria.titulo}</h3>
                <p class="categoria-descripcion">${categoria.descripcion || 'Explora nuestra selección'}</p>
                <span class="categoria-contador">${contador} productos</span>
            </div>
        `;
    }).join('');
    
    contenedor.innerHTML = categoriasHTML;
    
    // Animar aparición
    animarElementos('.categoria-card');
}

function renderizarProductosDestacados() {
    const contenedor = document.querySelector('.productos-grid');
    if (!contenedor) return;
    
    if (estadoIndex.productosDestacados.length === 0) {
        contenedor.innerHTML = `
            <div class="error-mensaje">
                <p>❌ No se pudieron cargar los productos destacados</p>
                <button onclick="cargarProductosDestacados().then(renderizarProductosDestacados)" class="btn-primary">
                    Reintentar
                </button>
            </div>
        `;
        return;
    }
    
    const productosHTML = estadoIndex.productosDestacados.map(producto => {
        const precioFormateado = window.TechSolvers.formatearPrecio(producto.precio);
        const stockClass = producto.stock > 0 ? 'stock-disponible' : 'stock-agotado';
        const stockTexto = producto.stock > 0 ? 'En Stock' : 'Agotado';
        
        return `
            <div class="producto-card">
                <img 
                    src="${producto.imagenUrl || 'imagenes/producto-default.jpg'}" 
                    alt="${producto.nombre}"
                    class="producto-imagen"
                    onerror="this.src='imagenes/producto-default.jpg'"
                >
                <div class="producto-info">
                    <div class="producto-categoria">${producto.categoria?.titulo || 'General'}</div>
                    <h3 class="producto-nombre">${producto.nombre}</h3>
                    <p class="producto-descripcion">${producto.descripcion || 'Descripción no disponible'}</p>
                    
                    <div class="producto-precio-container">
                        <span class="producto-precio">${precioFormateado}</span>
                        <span class="producto-stock ${stockClass}">${stockTexto}</span>
                    </div>
                    
                    <div class="producto-acciones">
                        <button 
                            class="btn-agregar-carrito" 
                            onclick="agregarProductoAlCarrito(${producto.id})"
                            ${producto.stock <= 0 ? 'disabled' : ''}
                        >
                            ${producto.stock > 0 ? '🛒 Agregar' : 'Sin Stock'}
                        </button>
                        <a href="productos.html?id=${producto.id}" class="btn-ver-detalles">
                            👁️ Ver
                        </a>
                    </div>
                </div>
            </div>
        `;
    }).join('');
    
    contenedor.innerHTML = productosHTML;
    
    // Animar aparición
    animarElementos('.producto-card');
}

function renderizarServicios() {
    const contenedor = document.querySelector('.servicios-grid');
    if (!contenedor) return;
    
    if (estadoIndex.servicios.length === 0) {
        contenedor.innerHTML = `
            <div class="error-mensaje">
                <p>❌ No se pudieron cargar los servicios</p>
                <button onclick="cargarServicios().then(renderizarServicios)" class="btn-primary">
                    Reintentar
                </button>
            </div>
        `;
        return;
    }
    
    const serviciosHTML = estadoIndex.servicios.map(servicio => {
        const icono = obtenerIconoServicio(servicio.tipoServicio);
        
        return `
            <div class="servicio-card">
                <div class="servicio-icono">${icono}</div>
                <h3 class="servicio-titulo">${servicio.titulo}</h3>
                <p class="servicio-descripcion">${servicio.descripcion}</p>
                <a href="servicio-tecnico.html?tipo=${servicio.tipoServicio}" class="btn-servicio">
                    Más información
                </a>
            </div>
        `;
    }).join('');
    
    contenedor.innerHTML = serviciosHTML;
    
    // Animar aparición
    animarElementos('.servicio-card');
}

// ========================================
// FUNCIONES DE INTERACCIÓN
// ========================================
function agregarProductoAlCarrito(productoId) {
    const producto = estadoIndex.productosDestacados.find(p => p.id === productoId);
    
    if (!producto) {
        window.TechSolvers.mostrarNotificacion('Producto no encontrado', 'error');
        return;
    }
    
    if (producto.stock <= 0) {
        window.TechSolvers.mostrarNotificacion('Producto sin stock', 'warning');
        return;
    }
    
    // Usar función global del carrito
    window.TechSolvers.agregarAlCarrito(producto);
}

function irACategoria(categoriaId) {
    console.log('📂 Navegando a categoría:', categoriaId);
    window.location.href = `productos.html?categoria=${categoriaId}`;
}

function irAProductos() {
    window.location.href = 'productos.html';
}

function irAServicios() {
    window.location.href = 'servicio-tecnico.html';
}

// ========================================
// FUNCIONES DE UTILIDAD
// ========================================
function obtenerIconoCategoria(titulo) {
    const iconos = {
        'computadoras': '💻',
        'laptops': '💻',
        'pc': '🖥️',
        'audio': '🔊',
        'audifonos': '🎧',
        'parlantes': '🔊',
        'celulares': '📱',
        'smartphones': '📱',
        'tablets': '📱',
        'impresoras': '🖨️',
        'accesorios': '🔌',
        'gaming': '🎮',
        'componentes': '⚙️'
    };
    
    const tituloLower = titulo.toLowerCase();
    
    for (const [key, icono] of Object.entries(iconos)) {
        if (tituloLower.includes(key)) {
            return icono;
        }
    }
    
    return '🛍️'; // Icono por defecto
}

function obtenerIconoServicio(tipoServicio) {
    const iconos = {
        'PC': '🖥️',
        'CELULAR': '📱',
        'IMPRESORA': '🖨️',
        'LAPTOP': '💻',
        'TABLET': '📱',
        'AUDIO': '🔊'
    };
    
    return iconos[tipoServicio?.toUpperCase()] || '🔧';
}

function animarElementos(selector) {
    const elementos = document.querySelectorAll(selector);
    
    elementos.forEach((elemento, index) => {
        elemento.style.opacity = '0';
        elemento.style.transform = 'translateY(20px)';
        elemento.style.transition = 'all 0.6s ease';
        
        setTimeout(() => {
            elemento.style.opacity = '1';
            elemento.style.transform = 'translateY(0)';
        }, index * 100);
    });
}

function mostrarErrorGeneral() {
    const main = document.querySelector('main');
    if (main) {
        main.innerHTML = `
            <div class="error-mensaje" style="max-width: 600px; margin: 50px auto; text-align: center;">
                <h2>❌ Error al cargar la página</h2>
                <p>No se pudo conectar con el servidor. Verifica tu conexión e inténtalo nuevamente.</p>
                <button onclick="location.reload()" class="btn-primary" style="margin-top: 20px;">
                    🔄 Recargar página
                </button>
            </div>
        `;
    }
}

// ========================================
// EVENTOS ESPECÍFICOS DEL INDEX
// ========================================
function inicializarEventosIndex() {
    // Eventos para los botones del hero
    const btnProductos = document.querySelector('.btn-primary[href*="productos"]');
    const btnServicios = document.querySelector('.btn-secondary[href*="servicio"]');
    
    if (btnProductos) {
        btnProductos.addEventListener('click', function(e) {
            console.log('🛍️ Clic en Ver Productos');
        });
    }
    
    if (btnServicios) {
        btnServicios.addEventListener('click', function(e) {
            console.log('🔧 Clic en Servicios Técnicos');
        });
    }
    
    // Evento para scroll suave a secciones
    const enlaces = document.querySelectorAll('a[href^="#"]');
    enlaces.forEach(enlace => {
        enlace.addEventListener('click', function(e) {
            e.preventDefault();
            const destino = document.querySelector(this.getAttribute('href'));
            if (destino) {
                destino.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
    
    console.log('🎯 Eventos del index inicializados');
}

// ========================================
// FUNCIONES PÚBLICAS PARA EL HTML
// ========================================
window.IndexFunctions = {
    agregarProductoAlCarrito,
    irACategoria,
    irAProductos,
    irAServicios,
    cargarCategorias,
    cargarProductosDestacados,
    cargarServicios,
    renderizarCategorias,
    renderizarProductosDestacados,
    renderizarServicios
};

console.log('🏠 Index JS completamente configurado');