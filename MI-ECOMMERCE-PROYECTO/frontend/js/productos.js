// ========================================
// JAVASCRIPT ESPECÍFICO - PÁGINA DE PRODUCTOS
// Filtros avanzados, múltiples vistas, paginación
// ========================================

// CONFIGURACIÓN ESPECÍFICA DE PRODUCTOS
const CONFIG_PRODUCTOS = {
    productosPorPagina: 12,
    vistaDefault: 'grid',
    filtrosEnStorage: true,
    animarCarga: true
};

// ESTADO DE LA PÁGINA DE PRODUCTOS
let estadoProductos = {
    productos: [],
    productosFiltrados: [],
    categorias: [],
    marcas: [],
    filtrosActivos: {
        busqueda: '',
        categoria: '',
        precioMin: '',
        precioMax: '',
        marcasSeleccionadas: [],
        soloDisponibles: false,
        incluirAgotados: false
    },
    ordenamiento: 'nombre-asc',
    vista: 'grid',
    paginaActual: 1,
    productosPorPagina: 12,
    cargando: false,
    totalPaginas: 0
};

document.addEventListener('DOMContentLoaded', function() {
    console.log('🛍️ Productos JS cargado');
    
    // Inicializar página de productos
    inicializarProductos();
    
    console.log('✅ Productos completamente inicializado');
});

// ========================================
// INICIALIZACIÓN PRINCIPAL
// ========================================
async function inicializarProductos() {
    try {
        // Mostrar carga inicial
        mostrarCargandoProductos();
        
        // Cargar configuración guardada
        cargarConfiguracionGuardada();
        
        // Obtener parámetros de la URL
        procesarParametrosURL();
        
        // Cargar datos desde el backend
        await Promise.all([
            cargarTodosLosProductos(),
            cargarCategorias(),
            cargarMarcas()
        ]);
        
        // Configurar interfaz
        configurarFiltros();
        configurarEventos();
        
        // Aplicar filtros y mostrar productos
        aplicarFiltrosYMostrar();
        
        console.log('🎯 Productos completamente cargados');
        
    } catch (error) {
        console.error('❌ Error al inicializar productos:', error);
        mostrarErrorProductos();
    }
}

// ========================================
// CARGA DE DATOS DESDE EL BACKEND
// ========================================
async function cargarTodosLosProductos() {
    try {
        console.log('📦 Cargando todos los productos...');
        
        const productos = await window.TechSolvers.realizarPeticionAPI('/productos');
        estadoProductos.productos = productos || [];
        estadoProductos.productosFiltrados = [...estadoProductos.productos];
        
        console.log(`✅ ${estadoProductos.productos.length} productos cargados`);
        
    } catch (error) {
        console.error('❌ Error al cargar productos:', error);
        estadoProductos.productos = [];
        estadoProductos.productosFiltrados = [];
        throw error;
    }
}

async function cargarCategorias() {
    try {
        console.log('📂 Cargando categorías...');
        
        const categorias = await window.TechSolvers.realizarPeticionAPI('/categorias');
        estadoProductos.categorias = categorias || [];
        
        console.log(`✅ ${estadoProductos.categorias.length} categorías cargadas`);
        
    } catch (error) {
        console.error('❌ Error al cargar categorías:', error);
        estadoProductos.categorias = [];
    }
}

async function cargarMarcas() {
    try {
        // Extraer marcas únicas de los productos
        const marcasUnicas = [...new Set(
            estadoProductos.productos
                .map(p => p.marca)
                .filter(marca => marca && marca.trim() !== '')
        )].sort();
        
        // Contar productos por marca
        estadoProductos.marcas = marcasUnicas.map(marca => ({
            nombre: marca,
            cantidad: estadoProductos.productos.filter(p => p.marca === marca).length
        }));
        
        console.log(`✅ ${estadoProductos.marcas.length} marcas extraídas`);
        
    } catch (error) {
        console.error('❌ Error al procesar marcas:', error);
        estadoProductos.marcas = [];
    }
}

// ========================================
// CONFIGURACIÓN DE FILTROS
// ========================================
function configurarFiltros() {
    // Llenar select de categorías
    const categoriaSelect = document.getElementById('categoria-filtro');
    if (categoriaSelect) {
        categoriaSelect.innerHTML = '<option value="">Todas las categorías</option>';
        
        estadoProductos.categorias.forEach(categoria => {
            const option = document.createElement('option');
            option.value = categoria.id;
            option.textContent = categoria.titulo;
            categoriaSelect.appendChild(option);
        });
    }
    
    // Llenar marcas
    const marcasContainer = document.querySelector('.marcas-container');
    if (marcasContainer) {
        marcasContainer.innerHTML = '';
        
        estadoProductos.marcas.forEach(marca => {
            const marcaDiv = document.createElement('div');
            marcaDiv.className = 'marca-item';
            marcaDiv.innerHTML = `
                <label class="marca-checkbox">
                    <input type="checkbox" value="${marca.nombre}" class="marca-check">
                    <span>${marca.nombre}</span>
                </label>
                <span class="marca-count">${marca.cantidad}</span>
            `;
            marcasContainer.appendChild(marcaDiv);
        });
    }
    
    // Configurar vista y productos por página
    const vistaSelector = document.getElementById('vista-selector');
    if (vistaSelector) {
        vistaSelector.value = estadoProductos.vista;
    }
    
    const productosPorPagina = document.getElementById('productos-por-pagina');
    if (productosPorPagina) {
        productosPorPagina.value = estadoProductos.productosPorPagina;
    }
    
    // Configurar ordenamiento
    const ordenarPor = document.getElementById('ordenar-por');
    if (ordenarPor) {
        ordenarPor.value = estadoProductos.ordenamiento;
    }
}

// ========================================
// CONFIGURACIÓN DE EVENTOS
// ========================================
function configurarEventos() {
    // Filtros
    document.getElementById('aplicar-filtros')?.addEventListener('click', aplicarFiltrosYMostrar);
    document.getElementById('limpiar-filtros')?.addEventListener('click', limpiarFiltros);
    
    // Búsqueda en tiempo real
    document.getElementById('busqueda-filtro')?.addEventListener('input', 
        debounce(aplicarFiltrosYMostrar, 500));
    
    // Filtros de precio
    document.getElementById('precio-min')?.addEventListener('change', aplicarFiltrosYMostrar);
    document.getElementById('precio-max')?.addEventListener('change', aplicarFiltrosYMostrar);
    
    // Rangos rápidos de precio
    document.querySelectorAll('.precio-rapido').forEach(btn => {
        btn.addEventListener('click', function() {
            const min = this.dataset.min;
            const max = this.dataset.max;
            
            document.getElementById('precio-min').value = min;
            document.getElementById('precio-max').value = max || '';
            
            aplicarFiltrosYMostrar();
        });
    });
    
    // Checkboxes de marca
    document.addEventListener('change', function(e) {
        if (e.target.classList.contains('marca-check')) {
            aplicarFiltrosYMostrar();
        }
    });
    
    // Checkboxes de disponibilidad
    document.getElementById('solo-disponibles')?.addEventListener('change', aplicarFiltrosYMostrar);
    document.getElementById('incluir-agotados')?.addEventListener('change', aplicarFiltrosYMostrar);
    
    // Categoría select
    document.getElementById('categoria-filtro')?.addEventListener('change', aplicarFiltrosYMostrar);
    
    // Ordenamiento
    document.getElementById('ordenar-por')?.addEventListener('change', function() {
        estadoProductos.ordenamiento = this.value;
        estadoProductos.paginaActual = 1;
        aplicarFiltrosYMostrar();
    });
    
    // Vista
    document.getElementById('vista-selector')?.addEventListener('change', function() {
        estadoProductos.vista = this.value;
        cambiarVista();
        guardarConfiguracion();
    });
    
    // Productos por página
    document.getElementById('productos-por-pagina')?.addEventListener('change', function() {
        estadoProductos.productosPorPagina = parseInt(this.value);
        estadoProductos.paginaActual = 1;
        aplicarFiltrosYMostrar();
        guardarConfiguracion();
    });
    
    // Filtros móviles
    document.getElementById('filtros-mobile-toggle')?.addEventListener('click', toggleFiltrosMobile);
    document.getElementById('filtros-toggle')?.addEventListener('click', toggleFiltrosMobile);
    
    // Cerrar filtros al hacer click en overlay
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('filtros-overlay')) {
            toggleFiltrosMobile();
        }
    });
}

// ========================================
// APLICACIÓN DE FILTROS
// ========================================
function aplicarFiltrosYMostrar() {
    if (estadoProductos.cargando) return;
    
    estadoProductos.cargando = true;
    mostrarCargandoProductos();
    
    // Recopilar filtros activos
    recopilarFiltros();
    
    // Aplicar filtros
    let productosFiltrados = [...estadoProductos.productos];
    
    // Filtro por búsqueda
    if (estadoProductos.filtrosActivos.busqueda) {
        const termino = estadoProductos.filtrosActivos.busqueda.toLowerCase();
        productosFiltrados = productosFiltrados.filter(producto => 
            producto.nombre.toLowerCase().includes(termino) ||
            producto.descripcion?.toLowerCase().includes(termino) ||
            producto.marca?.toLowerCase().includes(termino) ||
            producto.modelo?.toLowerCase().includes(termino)
        );
    }
    
    // Filtro por categoría
    if (estadoProductos.filtrosActivos.categoria) {
        productosFiltrados = productosFiltrados.filter(producto => 
            producto.categoria?.id == estadoProductos.filtrosActivos.categoria
        );
    }
    
    // Filtro por precio
    if (estadoProductos.filtrosActivos.precioMin) {
        productosFiltrados = productosFiltrados.filter(producto => 
            producto.precio >= parseFloat(estadoProductos.filtrosActivos.precioMin)
        );
    }
    
    if (estadoProductos.filtrosActivos.precioMax) {
        productosFiltrados = productosFiltrados.filter(producto => 
            producto.precio <= parseFloat(estadoProductos.filtrosActivos.precioMax)
        );
    }
    
    // Filtro por marcas
    if (estadoProductos.filtrosActivos.marcasSeleccionadas.length > 0) {
        productosFiltrados = productosFiltrados.filter(producto => 
            estadoProductos.filtrosActivos.marcasSeleccionadas.includes(producto.marca)
        );
    }
    
    // Filtro por disponibilidad
    if (estadoProductos.filtrosActivos.soloDisponibles) {
        productosFiltrados = productosFiltrados.filter(producto => producto.stock > 0);
    }
    
    if (!estadoProductos.filtrosActivos.incluirAgotados) {
        // Por defecto no incluir agotados si no está marcado
    }
    
    // Aplicar ordenamiento
    productosFiltrados = ordenarProductos(productosFiltrados);
    
    // Guardar resultados
    estadoProductos.productosFiltrados = productosFiltrados;
    
    // Calcular paginación
    calcularPaginacion();
    
    // Mostrar productos
    mostrarProductos();
    
    // Mostrar filtros activos
    mostrarFiltrosActivos();
    
    // Actualizar información
    actualizarInformacionResultados();
    
    // Guardar estado
    guardarConfiguracion();
    
    estadoProductos.cargando = false;
}

function recopilarFiltros() {
    estadoProductos.filtrosActivos = {
        busqueda: document.getElementById('busqueda-filtro')?.value || '',
        categoria: document.getElementById('categoria-filtro')?.value || '',
        precioMin: document.getElementById('precio-min')?.value || '',
        precioMax: document.getElementById('precio-max')?.value || '',
        marcasSeleccionadas: Array.from(document.querySelectorAll('.marca-check:checked')).map(cb => cb.value),
        soloDisponibles: document.getElementById('solo-disponibles')?.checked || false,
        incluirAgotados: document.getElementById('incluir-agotados')?.checked || false
    };
}

function ordenarProductos(productos) {
    const [campo, direccion] = estadoProductos.ordenamiento.split('-');
    
    return productos.sort((a, b) => {
        let valorA, valorB;
        
        switch (campo) {
            case 'nombre':
                valorA = a.nombre.toLowerCase();
                valorB = b.nombre.toLowerCase();
                break;
            case 'precio':
                valorA = a.precio;
                valorB = b.precio;
                break;
            case 'stock':
                valorA = a.stock;
                valorB = b.stock;
                break;
            case 'fecha':
                valorA = new Date(a.fechaCreacion || a.id);
                valorB = new Date(b.fechaCreacion || b.id);
                break;
            default:
                return 0;
        }
        
        if (direccion === 'asc') {
            return valorA > valorB ? 1 : valorA < valorB ? -1 : 0;
        } else {
            return valorA < valorB ? 1 : valorA > valorB ? -1 : 0;
        }
    });
}

// ========================================
// RENDERIZADO DE PRODUCTOS
// ========================================
function mostrarProductos() {
    const productosGrid = document.getElementById('productos-grid');
    if (!productosGrid) return;
    
    // Calcular productos para la página actual
    const inicio = (estadoProductos.paginaActual - 1) * estadoProductos.productosPorPagina;
    const fin = inicio + estadoProductos.productosPorPagina;
    const productosPagina = estadoProductos.productosFiltrados.slice(inicio, fin);
    
    // Aplicar clase de vista
    cambiarVista();
    
    if (productosPagina.length === 0) {
        mostrarProductosVacio();
        return;
    }
    
    // Generar HTML de productos
    const productosHTML = productosPagina.map((producto, index) => 
        generarHTMLProducto(producto, index)
    ).join('');
    
    productosGrid.innerHTML = productosHTML;
    
    // Generar paginación
    generarPaginacion();
    
    // Animar productos si está habilitado
    if (CONFIG_PRODUCTOS.animarCarga) {
        animarProductos();
    }
}

function generarHTMLProducto(producto, index) {
    const precioFormateado = window.TechSolvers.formatearPrecio(producto.precio);
    const stockClass = producto.stock > 0 ? 
        (producto.stock <= 5 ? 'stock-bajo' : 'stock-disponible') : 'stock-agotado';
    const stockTexto = producto.stock > 0 ? 
        (producto.stock <= 5 ? `Quedan ${producto.stock}` : 'En Stock') : 'Agotado';
    
    // Badges especiales
    let badges = '';
    if (producto.destacado) badges += '<div class="producto-badge badge-destacado">Destacado</div>';
    if (producto.nuevo) badges += '<div class="producto-badge badge-nuevo">Nuevo</div>';
    if (producto.oferta) badges += '<div class="producto-badge badge-oferta">Oferta</div>';
    
    // Vista lista tiene estructura diferente
    if (estadoProductos.vista === 'list') {
        return `
            <div class="producto-card" style="animation-delay: ${index * 0.1}s">
                ${badges}
                <img 
                    src="${producto.imagenUrl || 'imagenes/producto-default.jpg'}" 
                    alt="${producto.nombre}"
                    class="producto-imagen"
                    onerror="this.src='imagenes/producto-default.jpg'"
                    loading="lazy"
                >
                <div class="producto-info">
                    <div class="producto-detalles-left">
                        <div class="producto-categoria">${producto.categoria?.titulo || 'General'}</div>
                        <h3 class="producto-nombre">${producto.nombre}</h3>
                        <p class="producto-descripcion">${producto.descripcion || 'Sin descripción disponible'}</p>
                        <div class="producto-detalles">
                            ${producto.marca ? `<div class="producto-marca">Marca: ${producto.marca}</div>` : ''}
                            ${producto.modelo ? `<div class="producto-modelo">Modelo: ${producto.modelo}</div>` : ''}
                        </div>
                    </div>
                    <div class="producto-detalles-right">
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
                            <a href="producto-detalle.html?id=${producto.id}" class="btn-ver-detalles">
                                👁️ Ver
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }
    
    // Vista grid y compact (estructura estándar)
    return `
        <div class="producto-card" style="animation-delay: ${index * 0.1}s">
            ${badges}
            <img 
                src="${producto.imagenUrl || 'imagenes/producto-default.jpg'}" 
                alt="${producto.nombre}"
                class="producto-imagen"
                onerror="this.src='imagenes/producto-default.jpg'"
                loading="lazy"
            >
            <div class="producto-info">
                <div class="producto-categoria">${producto.categoria?.titulo || 'General'}</div>
                <h3 class="producto-nombre">${producto.nombre}</h3>
                <p class="producto-descripcion">${producto.descripcion || 'Sin descripción disponible'}</p>
                
                <div class="producto-detalles">
                    ${producto.marca ? `<div class="producto-marca">Marca: ${producto.marca}</div>` : ''}
                    ${producto.modelo ? `<div class="producto-modelo">Modelo: ${producto.modelo}</div>` : ''}
                </div>
                
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
                    <a href="producto-detalle.html?id=${producto.id}" class="btn-ver-detalles">
                        👁️ Ver
                    </a>
                </div>
            </div>
        </div>
    `;
}

function cambiarVista() {
    const productosGrid = document.getElementById('productos-grid');
    if (!productosGrid) return;
    
    // Remover clases de vista
    productosGrid.classList.remove('vista-grid', 'vista-list', 'vista-compact');
    
    // Agregar clase de vista actual
    productosGrid.classList.add(`vista-${estadoProductos.vista}`);
}

function mostrarProductosVacio() {
    const productosGrid = document.getElementById('productos-grid');
    if (!productosGrid) return;
    
    const tienesFiltros = Object.values(estadoProductos.filtrosActivos).some(valor => 
        valor && (Array.isArray(valor) ? valor.length > 0 : true)
    );
    
    productosGrid.innerHTML = `
        <div class="productos-vacio">
            <h3>😔 No se encontraron productos</h3>
            <p>${tienesFiltros ? 
                'No hay productos que coincidan con los filtros aplicados.' : 
                'Aún no hay productos disponibles en el catálogo.'
            }</p>
            ${tienesFiltros ? 
                '<button class="btn-limpiar-busqueda" onclick="limpiarFiltros()">🗑️ Limpiar filtros</button>' : 
                '<a href="index.html" class="btn-limpiar-busqueda">🏠 Volver al inicio</a>'
            }
        </div>
    `;
    
    // Limpiar paginación
    const paginacionContainer = document.getElementById('paginacion-container');
    if (paginacionContainer) {
        paginacionContainer.innerHTML = '';
    }
}

// ========================================
// PAGINACIÓN
// ========================================
function calcularPaginacion() {
    const totalProductos = estadoProductos.productosFiltrados.length;
    estadoProductos.totalPaginas = Math.ceil(totalProductos / estadoProductos.productosPorPagina);
    
    // Asegurar que la página actual sea válida
    if (estadoProductos.paginaActual > estadoProductos.totalPaginas) {
        estadoProductos.paginaActual = Math.max(1, estadoProductos.totalPaginas);
    }
}

function generarPaginacion() {
    const paginacionContainer = document.getElementById('paginacion-container');
    if (!paginacionContainer || estadoProductos.totalPaginas <= 1) {
        if (paginacionContainer) paginacionContainer.innerHTML = '';
        return;
    }
    
    let paginacionHTML = '';
    
    // Botón anterior
    paginacionHTML += `
        <button class="paginacion-btn ${estadoProductos.paginaActual === 1 ? 'disabled' : ''}" 
                onclick="irAPagina(${estadoProductos.paginaActual - 1})"
                ${estadoProductos.paginaActual === 1 ? 'disabled' : ''}>
            « Anterior
        </button>
    `;
    
    // Números de página
    const maxPaginasVisibles = 5;
    let inicio = Math.max(1, estadoProductos.paginaActual - Math.floor(maxPaginasVisibles / 2));
    let fin = Math.min(estadoProductos.totalPaginas, inicio + maxPaginasVisibles - 1);
    
    // Ajustar inicio si estamos cerca del final
    if (fin - inicio < maxPaginasVisibles - 1) {
        inicio = Math.max(1, fin - maxPaginasVisibles + 1);
    }
    
    // Primera página si no está visible
    if (inicio > 1) {
        paginacionHTML += `
            <button class="paginacion-btn" onclick="irAPagina(1)">1</button>
        `;
        if (inicio > 2) {
            paginacionHTML += '<span class="paginacion-info">...</span>';
        }
    }
    
    // Páginas visibles
    for (let i = inicio; i <= fin; i++) {
        paginacionHTML += `
            <button class="paginacion-btn ${i === estadoProductos.paginaActual ? 'activo' : ''}" 
                    onclick="irAPagina(${i})">
                ${i}
            </button>
        `;
    }
    
    // Última página si no está visible
    if (fin < estadoProductos.totalPaginas) {
        if (fin < estadoProductos.totalPaginas - 1) {
            paginacionHTML += '<span class="paginacion-info">...</span>';
        }
        paginacionHTML += `
            <button class="paginacion-btn" onclick="irAPagina(${estadoProductos.totalPaginas})">
                ${estadoProductos.totalPaginas}
            </button>
        `;
    }
    
    // Botón siguiente
    paginacionHTML += `
        <button class="paginacion-btn ${estadoProductos.paginaActual === estadoProductos.totalPaginas ? 'disabled' : ''}" 
                onclick="irAPagina(${estadoProductos.paginaActual + 1})"
                ${estadoProductos.paginaActual === estadoProductos.totalPaginas ? 'disabled' : ''}>
            Siguiente »
        </button>
    `;
    
    // Información de página
    const inicio = (estadoProductos.paginaActual - 1) * estadoProductos.productosPorPagina + 1;
    const fin2 = Math.min(estadoProductos.paginaActual * estadoProductos.productosPorPagina, estadoProductos.productosFiltrados.length);
    
    paginacionHTML += `
        <span class="paginacion-info">
            ${inicio}-${fin2} de ${estadoProductos.productosFiltrados.length}
        </span>
    `;
    
    paginacionContainer.innerHTML = paginacionHTML;
}

function irAPagina(pagina) {
    if (pagina < 1 || pagina > estadoProductos.totalPaginas || pagina === estadoProductos.paginaActual) {
        return;
    }
    
    estadoProductos.paginaActual = pagina;
    mostrarProductos();
    
    // Scroll suave al inicio de productos
    document.querySelector('.productos-content')?.scrollIntoView({ 
        behavior: 'smooth', 
        block: 'start' 
    });
}

// ========================================
// GESTIÓN DE FILTROS ACTIVOS
// ========================================
function mostrarFiltrosActivos() {
    const filtrosActivosContainer = document.querySelector('.filtros-activos');
    
    // Crear contenedor si no existe
    if (!filtrosActivosContainer) {
        const nuevoContainer = document.createElement('div');
        nuevoContainer.className = 'filtros-activos';
        nuevoContainer.style.display = 'none';
        
        const productosContent = document.querySelector('.productos-content');
        const ordenamientoBar = document.querySelector('.ordenamiento-bar');
        
        if (productosContent && ordenamientoBar) {
            productosContent.insertBefore(nuevoContainer, ordenamientoBar.nextSibling);
        }
    }
    
    const container = document.querySelector('.filtros-activos');
    if (!container) return;
    
    const filtrosActivos = [];
    
    // Recopilar filtros activos
    if (estadoProductos.filtrosActivos.busqueda) {
        filtrosActivos.push({
            tipo: 'busqueda',
            etiqueta: `Búsqueda: "${estadoProductos.filtrosActivos.busqueda}"`,
            valor: ''
        });
    }
    
    if (estadoProductos.filtrosActivos.categoria) {
        const categoria = estadoProductos.categorias.find(c => c.id == estadoProductos.filtrosActivos.categoria);
        filtrosActivos.push({
            tipo: 'categoria',
            etiqueta: `Categoría: ${categoria?.titulo || 'Desconocida'}`,
            valor: ''
        });
    }
    
    if (estadoProductos.filtrosActivos.precioMin || estadoProductos.filtrosActivos.precioMax) {
        const min = estadoProductos.filtrosActivos.precioMin || '0';
        const max = estadoProductos.filtrosActivos.precioMax || '∞';
        filtrosActivos.push({
            tipo: 'precio',
            etiqueta: `Precio: S/ ${min} - ${max}`,
            valor: ''
        });
    }
    
    estadoProductos.filtrosActivos.marcasSeleccionadas.forEach(marca => {
        filtrosActivos.push({
            tipo: 'marca',
            etiqueta: `Marca: ${marca}`,
            valor: marca
        });
    });
    
    if (estadoProductos.filtrosActivos.soloDisponibles) {
        filtrosActivos.push({
            tipo: 'disponibilidad',
            etiqueta: 'Solo disponibles',
            valor: 'disponibles'
        });
    }
    
    // Mostrar u ocultar contenedor
    if (filtrosActivos.length === 0) {
        container.style.display = 'none';
        return;
    }
    
    container.style.display = 'block';
    
    // Generar HTML
    container.innerHTML = `
        <div class="filtros-activos-titulo">Filtros aplicados:</div>
        <div class="filtros-activos-lista">
            ${filtrosActivos.map(filtro => `
                <div class="filtro-activo">
                    <span>${filtro.etiqueta}</span>
                    <button class="filtro-activo-eliminar" 
                            onclick="eliminarFiltroActivo('${filtro.tipo}', '${filtro.valor}')">
                        ✕
                    </button>
                </div>
            `).join('')}
            <button class="filtro-activo" onclick="limpiarFiltros()" style="background: #dc3545;">
                <span>Limpiar todo</span>
            </button>
        </div>
    `;
}

function eliminarFiltroActivo(tipo, valor) {
    switch (tipo) {
        case 'busqueda':
            document.getElementById('busqueda-filtro').value = '';
            break;
        case 'categoria':
            document.getElementById('categoria-filtro').value = '';
            break;
        case 'precio':
            document.getElementById('precio-min').value = '';
            document.getElementById('precio-max').value = '';
            break;
        case 'marca':
            const marcaCheck = document.querySelector(`.marca-check[value="${valor}"]`);
            if (marcaCheck) marcaCheck.checked = false;
            break;
        case 'disponibilidad':
            document.getElementById('solo-disponibles').checked = false;
            break;
    }
    
    aplicarFiltrosYMostrar();
}

// ========================================
// FUNCIONES DE UTILIDAD
// ========================================
function limpiarFiltros() {
    // Limpiar todos los campos
    document.getElementById('busqueda-filtro').value = '';
    document.getElementById('categoria-filtro').value = '';
    document.getElementById('precio-min').value = '';
    document.getElementById('precio-max').value = '';
    document.getElementById('solo-disponibles').checked = false;
    document.getElementById('incluir-agotados').checked = false;
    
    // Limpiar marcas
    document.querySelectorAll('.marca-check').forEach(check => {
        check.checked = false;
    });
    
    // Resetear página
    estadoProductos.paginaActual = 1;
    
    // Aplicar filtros
    aplicarFiltrosYMostrar();
    
    window.TechSolvers.mostrarNotificacion('Filtros limpiados', 'success');
}

function agregarProductoAlCarrito(productoId) {
    const producto = estadoProductos.productos.find(p => p.id === productoId);
    
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

function mostrarCargandoProductos() {
    const productosGrid = document.getElementById('productos-grid');
    if (productosGrid) {
        productosGrid.innerHTML = `
            <div class="loading-productos">
                <div class="loading-spinner"></div>
                <p>Cargando productos...</p>
            </div>
        `;
    }
}

function mostrarErrorProductos() {
    const productosGrid = document.getElementById('productos-grid');
    if (productosGrid) {
        productosGrid.innerHTML = `
            <div class="productos-error">
                <h3>❌ Error al cargar productos</h3>
                <p>No se pudieron cargar los productos. Verifica tu conexión e inténtalo nuevamente.</p>
                <button onclick="window.location.reload()" class="btn-limpiar-busqueda">
                    🔄 Recargar página
                </button>
            </div>
        `;
    }
}

function actualizarInformacionResultados() {
    const totalProductosElement = document.getElementById('total-productos');
    if (totalProductosElement) {
        const total = estadoProductos.productosFiltrados.length;
        const totalGeneral = estadoProductos.productos.length;
        
        if (total === totalGeneral) {
            totalProductosElement.textContent = `${total} productos encontrados`;
        } else {
            totalProductosElement.textContent = `${total} de ${totalGeneral} productos`;
        }
    }
}

function animarProductos() {
    const productos = document.querySelectorAll('.producto-card');
    productos.forEach((producto, index) => {
        producto.style.animationDelay = `${index * 0.1}s`;
    });
}

function toggleFiltrosMobile() {
    const sidebar = document.getElementById('filtros-sidebar');
    const overlay = document.querySelector('.filtros-overlay') || crearOverlay();
    
    if (sidebar) {
        sidebar.classList.toggle('activo');
        overlay.classList.toggle('activo');
        
        // Prevenir scroll del body cuando el sidebar está abierto
        if (sidebar.classList.contains('activo')) {
            document.body.style.overflow = 'hidden';
        } else {
            document.body.style.overflow = '';
        }
    }
}

function crearOverlay() {
    const overlay = document.createElement('div');
    overlay.className = 'filtros-overlay';
    document.body.appendChild(overlay);
    return overlay;
}

// ========================================
// GESTIÓN DE PARÁMETROS URL Y STORAGE
// ========================================
function procesarParametrosURL() {
    const urlParams = new URLSearchParams(window.location.search);
    
    // Búsqueda
    const buscar = urlParams.get('buscar');
    if (buscar) {
        document.getElementById('busqueda-filtro').value = buscar;
        estadoProductos.filtrosActivos.busqueda = buscar;
    }
    
    // Categoría
    const categoria = urlParams.get('categoria');
    if (categoria) {
        estadoProductos.filtrosActivos.categoria = categoria;
    }
    
    // Precio
    const precioMin = urlParams.get('precio_min');
    const precioMax = urlParams.get('precio_max');
    if (precioMin) {
        document.getElementById('precio-min').value = precioMin;
        estadoProductos.filtrosActivos.precioMin = precioMin;
    }
    if (precioMax) {
        document.getElementById('precio-max').value = precioMax;
        estadoProductos.filtrosActivos.precioMax = precioMax;
    }
    
    // Página
    const pagina = parseInt(urlParams.get('pagina')) || 1;
    estadoProductos.paginaActual = pagina;
}

function cargarConfiguracionGuardada() {
    if (!CONFIG_PRODUCTOS.filtrosEnStorage) return;
    
    try {
        const configGuardada = localStorage.getItem('productos_config');
        if (configGuardada) {
            const config = JSON.parse(configGuardada);
            estadoProductos.vista = config.vista || 'grid';
            estadoProductos.productosPorPagina = config.productosPorPagina || 12;
            estadoProductos.ordenamiento = config.ordenamiento || 'nombre-asc';
        }
    } catch (error) {
        console.warn('Error al cargar configuración guardada:', error);
    }
}

function guardarConfiguracion() {
    if (!CONFIG_PRODUCTOS.filtrosEnStorage) return;
    
    try {
        const config = {
            vista: estadoProductos.vista,
            productosPorPagina: estadoProductos.productosPorPagina,
            ordenamiento: estadoProductos.ordenamiento
        };
        localStorage.setItem('productos_config', JSON.stringify(config));
    } catch (error) {
        console.warn('Error al guardar configuración:', error);
    }
}

// ========================================
// FUNCIÓN DEBOUNCE PARA BÚSQUEDA
// ========================================
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
// FUNCIONES PÚBLICAS PARA EL HTML
// ========================================
window.ProductosFunctions = {
    agregarProductoAlCarrito,
    irAPagina,
    limpiarFiltros,
    eliminarFiltroActivo,
    toggleFiltrosMobile,
    aplicarFiltrosYMostrar
};

console.log('🛍️ Productos JS completamente configurado');