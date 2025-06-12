// ========================================
// JAVASCRIPT ESPECÍFICO - PÁGINA DE CATEGORÍAS
// Funcionalidades exclusivas para la vista de categorías
// ========================================

// CONFIGURACIÓN ESPECÍFICA DE CATEGORÍAS
const CONFIG_CATEGORIAS = {
    vistaDefault: 'grid',
    ordenDefault: 'nombre-asc',
    categoriasPopulares: 5
};

// ESTADO DE LA PÁGINA
let estadoCategorias = {
    categorias: [],
    categoriasFiltradas: [],
    vistaActual: CONFIG_CATEGORIAS.vistaDefault,
    ordenActual: CONFIG_CATEGORIAS.ordenDefault,
    busqueda: '',
    cargando: false,
    estadisticas: []
};

document.addEventListener('DOMContentLoaded', function() {
    console.log('📦 Categorías JS cargado');
    
    // Inicializar página de categorías
    inicializarCategorias();
    
    console.log('✅ Categorías completamente inicializado');
});

// ========================================
// INICIALIZACIÓN PRINCIPAL
// ========================================
async function inicializarCategorias() {
    try {
        // Mostrar estado de carga
        mostrarCargando();
        
        // Cargar datos
        await cargarCategorias();
        
        // Renderizar contenido
        aplicarFiltrosYOrden();
        
        // Cargar estadísticas
        await cargarEstadisticas();
        
        // Inicializar eventos
        inicializarEventosCategorias();
        
        console.log('🎯 Página de categorías completamente cargada');
        
    } catch (error) {
        console.error('❌ Error al inicializar categorías:', error);
        mostrarError();
    }
}

// ========================================
// CARGA DE DATOS DESDE EL BACKEND
// ========================================
async function cargarCategorias() {
    estadoCategorias.cargando = true;
    
    try {
        console.log('📂 Cargando todas las categorías...');
        
        // Cargar categorías con productos
        const categorias = await window.TechSolvers.realizarPeticionAPI('/categorias/con-productos');
        
        // Procesar y enriquecer categorías
        estadoCategorias.categorias = categorias.map(categoria => ({
            ...categoria,
            icono: obtenerIconoCategoria(categoria.titulo),
            totalProductos: categoria.productos?.length || 0,
            productosActivos: categoria.productos?.filter(p => p.activo).length || 0
        }));
        
        estadoCategorias.categoriasFiltradas = [...estadoCategorias.categorias];
        
        console.log('✅ Categorías cargadas:', estadoCategorias.categorias.length);
        
    } catch (error) {
        console.error('❌ Error al cargar categorías:', error);
        // Intentar cargar categorías simples como fallback
        try {
            const categorias = await window.TechSolvers.realizarPeticionAPI('/categorias');
            estadoCategorias.categorias = categorias.map(categoria => ({
                ...categoria,
                icono: obtenerIconoCategoria(categoria.titulo),
                totalProductos: 0,
                productosActivos: 0
            }));
            estadoCategorias.categoriasFiltradas = [...estadoCategorias.categorias];
        } catch (fallbackError) {
            estadoCategorias.categorias = [];
            estadoCategorias.categoriasFiltradas = [];
        }
    } finally {
        estadoCategorias.cargando = false;
    }
}

async function cargarEstadisticas() {
    try {
        console.log('📊 Cargando estadísticas de categorías...');
        
        const estadisticas = await window.TechSolvers.realizarPeticionAPI('/categorias/estadisticas');
        estadoCategorias.estadisticas = estadisticas;
        
        // Renderizar categorías populares
        renderizarCategoriasPopulares();
        
    } catch (error) {
        console.error('❌ Error al cargar estadísticas:', error);
        // Calcular estadísticas localmente
        calcularEstadisticasLocales();
    }
}

// ========================================
// RENDERIZADO DE CONTENIDO
// ========================================
function renderizarCategorias() {
    const contenedor = document.getElementById('categorias-grid');
    const totalElement = document.querySelector('#total-categorias strong');
    const noCategoriasElement = document.getElementById('no-categorias');
    
    if (!contenedor) return;
    
    // Actualizar contador
    if (totalElement) {
        totalElement.textContent = estadoCategorias.categoriasFiltradas.length;
    }
    
    // Si no hay categorías
    if (estadoCategorias.categoriasFiltradas.length === 0) {
        contenedor.style.display = 'none';
        if (noCategoriasElement) {
            noCategoriasElement.style.display = 'block';
        }
        return;
    }
    
    // Ocultar mensaje de "no categorías"
    contenedor.style.display = 'grid';
    if (noCategoriasElement) {
        noCategoriasElement.style.display = 'none';
    }
    
    // Aplicar clase de vista
    contenedor.className = `categorias-grid ${estadoCategorias.vistaActual === 'lista' ? 'vista-lista' : ''}`;
    
    // Generar HTML de categorías
    const categoriasHTML = estadoCategorias.categoriasFiltradas.map(categoria => {
        const imagenUrl = categoria.imagenUrl || `https://via.placeholder.com/400x300/667eea/ffffff?text=${encodeURIComponent(categoria.titulo)}`;
        
        return `
            <div class="categoria-item" onclick="irAProductosCategoria(${categoria.id})">
                <div class="categoria-imagen-container">
                    <img 
                        src="${imagenUrl}" 
                        alt="${categoria.titulo}"
                        class="categoria-imagen"
                        onerror="this.src='https://via.placeholder.com/400x300/667eea/ffffff?text=${encodeURIComponent(categoria.titulo)}'"
                    >
                    <div class="categoria-overlay"></div>
                    <span class="categoria-icono-grande">${categoria.icono}</span>
                </div>
                
                <div class="categoria-contenido">
                    <div class="categoria-header">
                        <h2 class="categoria-nombre">${categoria.titulo}</h2>
                        <p class="categoria-descripcion">${categoria.descripcion || 'Explora nuestra selección de productos'}</p>
                    </div>
                    
                    <div class="categoria-footer">
                        <div class="categoria-stats">
                            <div class="stat-item">
                                <strong>${categoria.totalProductos}</strong> productos
                            </div>
                            ${categoria.productosActivos > 0 ? `
                                <div class="stat-item">
                                    <strong>${categoria.productosActivos}</strong> disponibles
                                </div>
                            ` : ''}
                        </div>
                        
                        <a href="productos.html?categoria=${categoria.id}" class="btn-ver-categoria">
                            Ver productos →
                        </a>
                    </div>
                </div>
            </div>
        `;
    }).join('');
    
    contenedor.innerHTML = categoriasHTML;
    
    // Animar aparición
    animarElementos('.categoria-item');
}

function renderizarCategoriasPopulares() {
    const contenedor = document.getElementById('categorias-populares');
    if (!contenedor || !estadoCategorias.estadisticas) return;
    
    // Ordenar categorías por popularidad
    const categoriasOrdenadas = [...estadoCategorias.categorias]
        .sort((a, b) => b.totalProductos - a.totalProductos)
        .slice(0, CONFIG_CATEGORIAS.categoriasPopulares);
    
    const popularesHTML = categoriasOrdenadas.map((categoria, index) => `
        <div class="stat-card" onclick="irAProductosCategoria(${categoria.id})">
            <div class="stat-numero">#${index + 1}</div>
            <div class="stat-categoria">${categoria.titulo}</div>
            <div class="stat-descripcion">${categoria.totalProductos} productos</div>
        </div>
    `).join('');
    
    contenedor.innerHTML = popularesHTML;
}

// ========================================
// FILTROS Y ORDENAMIENTO
// ========================================
function aplicarFiltrosYOrden() {
    // Filtrar por búsqueda
    let categoriasFiltradas = estadoCategorias.categorias;
    
    if (estadoCategorias.busqueda) {
        const busqueda = estadoCategorias.busqueda.toLowerCase();
        categoriasFiltradas = categoriasFiltradas.filter(categoria => 
            categoria.titulo.toLowerCase().includes(busqueda) ||
            (categoria.descripcion && categoria.descripcion.toLowerCase().includes(busqueda))
        );
    }
    
    // Ordenar
    categoriasFiltradas = ordenarCategorias(categoriasFiltradas, estadoCategorias.ordenActual);
    
    // Actualizar estado
    estadoCategorias.categoriasFiltradas = categoriasFiltradas;
    
    // Renderizar
    renderizarCategorias();
}

function ordenarCategorias(categorias, criterio) {
    const categoriasOrdenadas = [...categorias];
    
    switch (criterio) {
        case 'nombre-asc':
            return categoriasOrdenadas.sort((a, b) => a.titulo.localeCompare(b.titulo));
            
        case 'nombre-desc':
            return categoriasOrdenadas.sort((a, b) => b.titulo.localeCompare(a.titulo));
            
        case 'productos-desc':
            return categoriasOrdenadas.sort((a, b) => b.totalProductos - a.totalProductos);
            
        case 'productos-asc':
            return categoriasOrdenadas.sort((a, b) => a.totalProductos - b.totalProductos);
            
        case 'recientes':
            return categoriasOrdenadas.sort((a, b) => 
                new Date(b.fechaCreacion || 0) - new Date(a.fechaCreacion || 0)
            );
            
        default:
            return categoriasOrdenadas;
    }
}

// ========================================
// EVENTOS Y HANDLERS
// ========================================
function inicializarEventosCategorias() {
    // Buscador
    const buscador = document.getElementById('buscar-categoria');
    if (buscador) {
        buscador.addEventListener('input', debounce(function(e) {
            estadoCategorias.busqueda = e.target.value.trim();
            aplicarFiltrosYOrden();
        }, 300));
    }
    
    // Ordenamiento
    const selectorOrden = document.getElementById('ordenar-categorias');
    if (selectorOrden) {
        selectorOrden.addEventListener('change', function(e) {
            estadoCategorias.ordenActual = e.target.value;
            aplicarFiltrosYOrden();
        });
    }
    
    // Botones de vista
    const botonesVista = document.querySelectorAll('.btn-vista');
    botonesVista.forEach(btn => {
        btn.addEventListener('click', function() {
            // Remover clase active de todos
            botonesVista.forEach(b => b.classList.remove('active'));
            
            // Agregar clase active al clickeado
            this.classList.add('active');
            
            // Cambiar vista
            estadoCategorias.vistaActual = this.dataset.vista;
            renderizarCategorias();
        });
    });
    
    console.log('🎯 Eventos de categorías inicializados');
}

// ========================================
// FUNCIONES DE NAVEGACIÓN
// ========================================
function irAProductosCategoria(categoriaId) {
    console.log('📂 Navegando a productos de categoría:', categoriaId);
    window.location.href = `productos.html?categoria=${categoriaId}`;
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
        'componentes': '⚙️',
        'redes': '🌐',
        'almacenamiento': '💾',
        'monitores': '🖥️',
        'perifericos': '⌨️',
        'software': '💿',
        'camaras': '📷'
    };
    
    const tituloLower = titulo.toLowerCase();
    
    for (const [key, icono] of Object.entries(iconos)) {
        if (tituloLower.includes(key)) {
            return icono;
        }
    }
    
    return '📦'; // Icono por defecto
}

function calcularEstadisticasLocales() {
    // Calcular estadísticas basadas en los datos locales
    const stats = estadoCategorias.categorias.reduce((acc, categoria) => {
        acc.totalProductos += categoria.totalProductos;
        acc.totalCategorias += 1;
        acc.productosActivos += categoria.productosActivos;
        return acc;
    }, {
        totalProductos: 0,
        totalCategorias: 0,
        productosActivos: 0
    });
    
    estadoCategorias.estadisticas = stats;
    renderizarCategoriasPopulares();
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
        }, index * 50);
    });
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

function mostrarCargando() {
    const contenedor = document.getElementById('categorias-grid');
    if (contenedor) {
        contenedor.innerHTML = `
            <div class="loading" style="grid-column: 1 / -1;">
                <div class="loading-spinner"></div>
                <p>Cargando categorías...</p>
            </div>
        `;
    }
}

function mostrarError() {
    const contenedor = document.getElementById('categorias-grid');
    if (contenedor) {
        contenedor.innerHTML = `
            <div class="error-mensaje" style="grid-column: 1 / -1;">
                <h3>❌ Error al cargar las categorías</h3>
                <p>No se pudo conectar con el servidor. Verifica tu conexión e inténtalo nuevamente.</p>
                <button onclick="location.reload()" class="btn-primary" style="margin-top: 20px;">
                    🔄 Recargar página
                </button>
            </div>
        `;
    }
}

// ========================================
// FUNCIONES PÚBLICAS PARA EL HTML
// ========================================
window.CategoriasFunctions = {
    irAProductosCategoria,
    aplicarFiltrosYOrden,
    cargarCategorias
};

console.log('📦 Categorías JS completamente configurado');





