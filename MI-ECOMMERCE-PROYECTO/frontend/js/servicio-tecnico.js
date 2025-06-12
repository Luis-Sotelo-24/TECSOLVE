// ========================================
// JAVASCRIPT ESPECÍFICO - PÁGINA SERVICIO TÉCNICO
// Funcionalidades exclusivas de servicios técnicos
// ========================================

// CONFIGURACIÓN ESPECÍFICA
const CONFIG_SERVICIOS = {
    serviciosPerPage: 12,
    recargarIntervalo: 30000, // 30 segundos
    tiposServicio: ['PC', 'CELULAR', 'IMPRESORA', 'LAPTOP', 'TABLET', 'AUDIO']
};

// ESTADO DE LA PÁGINA
let estadoServicios = {
    servicios: [],
    serviciosAdmin: [],
    consultas: [],
    consultaSeleccionada: null,
    filtros: {
        tipo: '',
        busqueda: '',
        visibilidad: ''
    },
    cargando: {
        servicios: false,
        consultas: false,
        estadisticas: false
    },
    modoAdmin: false
};

// ========================================
// INICIALIZACIÓN PRINCIPAL
// ========================================
document.addEventListener('DOMContentLoaded', function() {
    console.log('🔧 Servicio Técnico JS cargado');
    
    inicializarPaginaServicios();
    
    console.log('✅ Servicio Técnico completamente inicializado');
});

async function inicializarPaginaServicios() {
    try {
        // Verificar permisos de usuario
        verificarPermisosUsuario();
        
        // Cargar datos iniciales
        await Promise.all([
            cargarServicios(),
            cargarServiciosParaFormulario()
        ]);
        
        // Renderizar contenido
        renderizarServicios();
        
        // Si es admin/trabajador, cargar datos adicionales
        if (estadoServicios.modoAdmin) {
            await cargarDatosAdmin();
            renderizarPanelAdmin();
        }
        
        // Cargar consultas del usuario si está logueado
        if (window.TechSolvers.usuario) {
            await cargarMisConsultas();
            renderizarMisConsultas();
        }
        
        // Inicializar eventos
        inicializarEventos();
        
    } catch (error) {
        console.error('❌ Error al inicializar página de servicios:', error);
        mostrarErrorGeneral();
    }
}

// ========================================
// VERIFICACIÓN DE PERMISOS
// ========================================
function verificarPermisosUsuario() {
    const usuario = window.TechSolvers.usuario;
    
    if (usuario && (usuario.rol === 'admin' || usuario.rol === 'trabajador')) {
        estadoServicios.modoAdmin = true;
        
        // Mostrar panel de administración
        const panelAdmin = document.getElementById('panel-admin');
        if (panelAdmin) {
            panelAdmin.style.display = 'block';
        }
        
        console.log('👑 Modo administrador activado');
    }
    
    if (usuario) {
        // Mostrar sección "Mis Consultas"
        const misConsultas = document.getElementById('mis-consultas');
        if (misConsultas) {
            misConsultas.style.display = 'block';
        }
        
        // Prellenar datos del formulario
        document.getElementById('nombre').value = usuario.nombre || '';
        document.getElementById('email').value = usuario.email || '';
    }
}

// ========================================
// CARGA DE DATOS DESDE EL BACKEND
// ========================================
async function cargarServicios() {
    estadoServicios.cargando.servicios = true;
    
    try {
        console.log('🔧 Cargando servicios técnicos...');
        
        const servicios = await window.TechSolvers.realizarPeticionAPI('/servicios');
        estadoServicios.servicios = servicios;
        
        console.log('✅ Servicios cargados:', estadoServicios.servicios.length);
        
    } catch (error) {
        console.error('❌ Error al cargar servicios:', error);
        estadoServicios.servicios = [];
    } finally {
        estadoServicios.cargando.servicios = false;
    }
}

async function cargarServiciosParaFormulario() {
    try {
        const servicios = estadoServicios.servicios;
        const selectServicio = document.getElementById('servicio-id');
        
        if (selectServicio) {
            selectServicio.innerHTML = '<option value="">Selecciona un servicio</option>';
            
            servicios.forEach(servicio => {
                const option = document.createElement('option');
                option.value = servicio.id;
                option.textContent = `${servicio.titulo} (${servicio.tipoServicio})`;
                selectServicio.appendChild(option);
            });
        }
        
    } catch (error) {
        console.error('❌ Error al cargar servicios para formulario:', error);
    }
}

async function cargarDatosAdmin() {
    try {
        console.log('📊 Cargando datos de administración...');
        
        // Cargar todos los servicios (incluidos ocultos)
        const todosServicios = await window.TechSolvers.realizarPeticionAPI('/servicios');
        estadoServicios.serviciosAdmin = todosServicios;
        
        // Cargar consultas pendientes si es admin
        if (window.TechSolvers.usuario?.rol === 'admin') {
            const consultas = await window.TechSolvers.realizarPeticionAPI('/contacto-servicios');
            estadoServicios.consultas = consultas;
        }
        
        console.log('✅ Datos de administración cargados');
        
    } catch (error) {
        console.error('❌ Error al cargar datos de administración:', error);
    }
}

async function cargarMisConsultas() {
    estadoServicios.cargando.consultas = true;
    
    try {
        console.log('📋 Cargando mis consultas...');
        
        const consultas = await window.TechSolvers.realizarPeticionAPI('/contacto-servicios');
        // Filtrar solo las consultas del usuario actual
        const usuario = window.TechSolvers.usuario;
        estadoServicios.consultas = consultas.filter(c => c.email === usuario.email);
        
        console.log('✅ Mis consultas cargadas:', estadoServicios.consultas.length);
        
    } catch (error) {
        console.error('❌ Error al cargar mis consultas:', error);
        estadoServicios.consultas = [];
    } finally {
        estadoServicios.cargando.consultas = false;
    }
}

// ========================================
// RENDERIZADO DE CONTENIDO
// ========================================
function renderizarServicios() {
    const contenedor = document.getElementById('servicios-grid');
    if (!contenedor) return;
    
    if (estadoServicios.cargando.servicios) {
        contenedor.innerHTML = `
            <div class="loading">
                <div class="loading-spinner"></div>
                <p>Cargando servicios técnicos...</p>
            </div>
        `;
        return;
    }
    
    if (estadoServicios.servicios.length === 0) {
        contenedor.innerHTML = `
            <div class="sin-resultados">
                <div class="sin-resultados-icono">🔧</div>
                <h3>No hay servicios disponibles</h3>
                <p>No se encontraron servicios técnicos en este momento.</p>
            </div>
        `;
        return;
    }
    
    // Filtrar servicios
    const serviciosFiltrados = filtrarServicios(estadoServicios.servicios);
    
    if (serviciosFiltrados.length === 0) {
        contenedor.innerHTML = `
            <div class="sin-resultados">
                <div class="sin-resultados-icono">🔍</div>
                <h3>No se encontraron servicios</h3>
                <p>Intenta modificar los filtros de búsqueda.</p>
                <button class="btn-primary" onclick="limpiarFiltros()">
                    🗑️ Limpiar filtros
                </button>
            </div>
        `;
        return;
    }
    
    const serviciosHTML = serviciosFiltrados.map(servicio => {
        const icono = obtenerIconoServicio(servicio.tipoServicio);
        const precio = servicio.precio ? window.TechSolvers.formatearPrecio(servicio.precio) : 'Consultar';
        
        return `
            <div class="servicio-card">
                <div class="servicio-header">
                    <div class="servicio-icono">${icono}</div>
                    <span class="servicio-tipo-badge">${servicio.tipoServicio}</span>
                </div>
                
                <h3 class="servicio-titulo">${servicio.titulo}</h3>
                <p class="servicio-descripcion">${servicio.descripcion}</p>
                
                ${servicio.precio ? `<div class="servicio-precio">${precio}</div>` : ''}
                
                <div class="servicio-acciones">
                    <button class="btn-solicitar" onclick="solicitarServicio(${servicio.id})">
                        📞 Solicitar
                    </button>
                    <button class="btn-info" onclick="verDetallesServicio(${servicio.id})">
                        ℹ️ Info
                    </button>
                </div>
            </div>
        `;
    }).join('');
    
    contenedor.innerHTML = serviciosHTML;
    
    // Animar aparición
    animarElementos('.servicio-card');
}

function renderizarPanelAdmin() {
    const contenedor = document.getElementById('servicios-admin-grid');
    if (!contenedor) return;
    
    const servicios = estadoServicios.serviciosAdmin;
    
    if (servicios.length === 0) {
        contenedor.innerHTML = `
            <div class="sin-resultados">
                <h3>No hay servicios registrados</h3>
                <button class="btn-primary" onclick="mostrarModalCrearServicio()">
                    ➕ Crear primer servicio
                </button>
            </div>
        `;
        return;
    }
    
    const serviciosHTML = servicios.map(servicio => {
        const visibilidadTexto = servicio.mostrarEnInicio ? 'Visible en inicio' : 'Oculto en inicio';
        const visibilidadClass = servicio.mostrarEnInicio ? 'estado-visible' : 'estado-oculto';
        const precio = servicio.precio ? window.TechSolvers.formatearPrecio(servicio.precio) : 'Sin precio';
        
        return `
            <div class="servicio-admin-card">
                <div class="servicio-admin-header">
                    <span class="servicio-admin-tipo">${servicio.tipoServicio}</span>
                    <div class="servicio-admin-acciones">
                        <button class="btn-admin btn-editar" onclick="editarServicio(${servicio.id})" title="Editar">
                            ✏️
                        </button>
                        <button class="btn-admin btn-toggle" 
                                onclick="toggleVisibilidad(${servicio.id}, ${!servicio.mostrarEnInicio})" 
                                title="Cambiar visibilidad">
                            ${servicio.mostrarEnInicio ? '👁️' : '🙈'}
                        </button>
                        <button class="btn-admin btn-eliminar" onclick="eliminarServicio(${servicio.id})" title="Eliminar">
                            🗑️
                        </button>
                    </div>
                </div>
                
                <h4 class="servicio-admin-titulo">${servicio.titulo}</h4>
                <p class="servicio-admin-descripcion">${servicio.descripcion}</p>
                <div class="servicio-admin-precio">${precio}</div>
                
                <div class="servicio-estado">
                    <span class="${visibilidadClass}">${visibilidadTexto}</span>
                </div>
            </div>
        `;
    }).join('');
    
    contenedor.innerHTML = serviciosHTML;
}

function renderizarMisConsultas() {
    const contenedor = document.getElementById('consultas-grid');
    if (!contenedor) return;
    
    if (estadoServicios.cargando.consultas) {
        contenedor.innerHTML = `
            <div class="loading">
                <div class="loading-spinner"></div>
                <p>Cargando consultas...</p>
            </div>
        `;
        return;
    }
    
    const consultas = estadoServicios.consultas;
    
    if (consultas.length === 0) {
        contenedor.innerHTML = `
            <div class="sin-resultados">
                <div class="sin-resultados-icono">📋</div>
                <h3>No tienes consultas de servicio</h3>
                <p>Cuando solicites un servicio, aparecerá aquí.</p>
            </div>
        `;
        return;
    }
    
    const consultasHTML = consultas.map(consulta => {
        const fecha = window.TechSolvers.formatearFecha(consulta.fechaCreacion);
        const estadoClass = `estado-${consulta.estado.toLowerCase().replace('_', '-')}`;
        const estadoTexto = {
            'PENDIENTE': 'Pendiente',
            'EN_REVISION': 'En Revisión',
            'CONTACTADO': 'Contactado',
            'RESUELTO': 'Resuelto'
        }[consulta.estado] || consulta.estado;
        
        return `
            <div class="consulta-card" onclick="verDetallesConsulta(${consulta.id})">
                <div class="consulta-header">
                    <div class="consulta-servicio">${consulta.servicio?.titulo || 'Servicio no especificado'}</div>
                    <span class="consulta-estado ${estadoClass}">${estadoTexto}</span>
                </div>
                
                <div class="consulta-fecha">${fecha}</div>
                <div class="consulta-mensaje">${consulta.mensaje}</div>
            </div>
        `;
    }).join('');
    
    contenedor.innerHTML = consultasHTML;
}

// ========================================
// FUNCIONES DE FILTRADO
// ========================================
function filtrarServicios(servicios) {
    return servicios.filter(servicio => {
        const cumpleTipo = !estadoServicios.filtros.tipo || 
                          servicio.tipoServicio === estadoServicios.filtros.tipo;
        
        const cumpleBusqueda = !estadoServicios.filtros.busqueda || 
                              servicio.titulo.toLowerCase().includes(estadoServicios.filtros.busqueda.toLowerCase()) ||
                              servicio.descripcion.toLowerCase().includes(estadoServicios.filtros.busqueda.toLowerCase());
        
        return cumpleTipo && cumpleBusqueda;
    });
}

function filtrarServiciosPublicos() {
    const filtroTipo = document.getElementById('filtro-tipo-publico');
    const busqueda = document.getElementById('buscar-servicios-publico');
    
    estadoServicios.filtros.tipo = filtroTipo?.value || '';
    estadoServicios.filtros.busqueda = busqueda?.value || '';
    
    console.log('🔍 Filtrando servicios:', estadoServicios.filtros);
    renderizarServicios();
}

function filtrarServiciosAdmin() {
    const filtroTipo = document.getElementById('filtro-tipo-admin');
    const filtroVisibilidad = document.getElementById('filtro-visibilidad');
    
    let serviciosFiltrados = [...estadoServicios.serviciosAdmin];
    
    if (filtroTipo?.value) {
        serviciosFiltrados = serviciosFiltrados.filter(s => s.tipoServicio === filtroTipo.value);
    }
    
    if (filtroVisibilidad?.value !== '') {
        const mostrar = filtroVisibilidad.value === 'true';
        serviciosFiltrados = serviciosFiltrados.filter(s => s.mostrarEnInicio === mostrar);
    }
    
    // Renderizar servicios filtrados temporalmente
    estadoServicios.serviciosAdmin = serviciosFiltrados;
    renderizarPanelAdmin();
}

function buscarServiciosPublicos() {
    filtrarServiciosPublicos();
}

function buscarServiciosAdmin() {
    const busqueda = document.getElementById('buscar-servicios-admin');
    const termino = busqueda?.value.toLowerCase() || '';
    
    if (!termino) {
        // Recargar todos los servicios
        renderizarPanelAdmin();
        return;
    }
    
    const serviciosFiltrados = estadoServicios.serviciosAdmin.filter(servicio =>
        servicio.titulo.toLowerCase().includes(termino) ||
        servicio.descripcion.toLowerCase().includes(termino) ||
        servicio.tipoServicio.toLowerCase().includes(termino)
    );
    
    // Renderizar resultados de búsqueda temporalmente
    const serviciosOriginal = [...estadoServicios.serviciosAdmin];
    estadoServicios.serviciosAdmin = serviciosFiltrados;
    renderizarPanelAdmin();
    estadoServicios.serviciosAdmin = serviciosOriginal;
}

function filtrarMisConsultas() {
    const filtroEstado = document.getElementById('filtro-estado-consultas');
    const estado = filtroEstado?.value || '';
    
    let consultasFiltradas = [...estadoServicios.consultas];
    
    if (estado) {
        consultasFiltradas = consultasFiltradas.filter(c => c.estado === estado);
    }
    
    // Renderizar consultas filtradas temporalmente
    const consultasOriginal = [...estadoServicios.consultas];
    estadoServicios.consultas = consultasFiltradas;
    renderizarMisConsultas();
    estadoServicios.consultas = consultasOriginal;
}

function limpiarFiltros() {
    // Limpiar filtros públicos
    const filtroTipoPublico = document.getElementById('filtro-tipo-publico');
    const busquedaPublica = document.getElementById('buscar-servicios-publico');
    
    if (filtroTipoPublico) filtroTipoPublico.value = '';
    if (busquedaPublica) busquedaPublica.value = '';
    
    // Limpiar estado de filtros
    estadoServicios.filtros = {
        tipo: '',
        busqueda: '',
        visibilidad: ''
    };
    
    // Renderizar sin filtros
    renderizarServicios();
    
    window.TechSolvers.mostrarNotificacion('Filtros limpiados', 'success');
}

// ========================================
// FUNCIONES DE INTERACCIÓN
// ========================================
function solicitarServicio(servicioId) {
    const servicio = estadoServicios.servicios.find(s => s.id === servicioId);
    
    if (!servicio) {
        window.TechSolvers.mostrarNotificacion('Servicio no encontrado', 'error');
        return;
    }
    
    // Prellenar el formulario y hacer scroll
    const selectServicio = document.getElementById('servicio-id');
    if (selectServicio) {
        selectServicio.value = servicioId;
    }
    
    // Scroll al formulario
    const formulario = document.getElementById('contacto-section');
    if (formulario) {
        formulario.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
    
    window.TechSolvers.mostrarNotificacion(`Formulario preparado para: ${servicio.titulo}`, 'success');
}

function verDetallesServicio(servicioId) {
    const servicio = estadoServicios.servicios.find(s => s.id === servicioId);
    
    if (!servicio) {
        window.TechSolvers.mostrarNotificacion('Servicio no encontrado', 'error');
        return;
    }
    
    const modal = document.getElementById('modal-consulta');
    const detalles = document.getElementById('consulta-detalles');
    
    if (modal && detalles) {
        const precio = servicio.precio ? window.TechSolvers.formatearPrecio(servicio.precio) : 'Consultar precio';
        
        detalles.innerHTML = `
            <div class="detalle-item">
                <span class="detalle-label">Servicio:</span>
                <div class="detalle-valor">${servicio.titulo}</div>
            </div>
            <div class="detalle-item">
                <span class="detalle-label">Tipo:</span>
                <div class="detalle-valor">${servicio.tipoServicio}</div>
            </div>
            <div class="detalle-item">
                <span class="detalle-label">Descripción:</span>
                <div class="detalle-valor">${servicio.descripcion}</div>
            </div>
            <div class="detalle-item">
                <span class="detalle-label">Precio estimado:</span>
                <div class="detalle-valor">${precio}</div>
            </div>
        `;
        
        // Ocultar acciones de admin
        const adminActions = document.getElementById('consulta-admin-actions');
        if (adminActions) {
            adminActions.style.display = 'none';
        }
        
        modal.classList.add('activo');
    }
}

function verDetallesConsulta(consultaId) {
    const consulta = estadoServicios.consultas.find(c => c.id === consultaId);
    
    if (!consulta) {
        window.TechSolvers.mostrarNotificacion('Consulta no encontrada', 'error');
        return;
    }
    
    estadoServicios.consultaSeleccionada = consulta;
    
    const modal = document.getElementById('modal-consulta');
    const detalles = document.getElementById('consulta-detalles');
    
    if (modal && detalles) {
        const fecha = window.TechSolvers.formatearFecha(consulta.fechaCreacion);
        const estadoTexto = {
            'PENDIENTE': 'Pendiente',
            'EN_REVISION': 'En Revisión',
            'CONTACTADO': 'Contactado',
            'RESUELTO': 'Resuelto'
        }[consulta.estado] || consulta.estado;
        
        detalles.innerHTML = `
            <div class="detalle-item">
                <span class="detalle-label">Servicio:</span>
                <div class="detalle-valor">${consulta.servicio?.titulo || 'No especificado'}</div>
            </div>
            <div class="detalle-item">
                <span class="detalle-label">Estado:</span>
                <div class="detalle-valor">${estadoTexto}</div>
            </div>
            <div class="detalle-item">
                <span class="detalle-label">Fecha:</span>
                <div class="detalle-valor">${fecha}</div>
            </div>
            <div class="detalle-item">
                <span class="detalle-label">Nombre:</span>
                <div class="detalle-valor">${consulta.nombre}</div>
            </div>
            <div class="detalle-item">
                <span class="detalle-label">Email:</span>
                <div class="detalle-valor">${consulta.email}</div>
            </div>
            <div class="detalle-item">
                <span class="detalle-label">Mensaje:</span>
                <div class="detalle-valor">${consulta.mensaje}</div>
            </div>
            ${consulta.atendidoPor ? `
                <div class="detalle-item">
                    <span class="detalle-label">Atendido por:</span>
                    <div class="detalle-valor">${consulta.atendidoPor.nombre}</div>
                </div>
            ` : ''}
        `;
        
        // Mostrar acciones de admin si corresponde
        const adminActions = document.getElementById('consulta-admin-actions');
        if (adminActions && estadoServicios.modoAdmin) {
            adminActions.style.display = 'flex';
        }
        
        modal.classList.add('activo');
    }
}

// ========================================
// CRUD DE SERVICIOS (ADMIN/TRABAJADOR)
// ========================================
function mostrarModalCrearServicio() {
    const modal = document.getElementById('modal-servicio');
    const titulo = document.getElementById('modal-servicio-titulo');
    const form = document.getElementById('form-servicio');
    
    if (modal && titulo && form) {
        titulo.textContent = '➕ Nuevo Servicio';
        form.reset();
        document.getElementById('servicio-edit-id').value = '';
        modal.classList.add('activo');
    }
}

function editarServicio(servicioId) {
    const servicio = estadoServicios.serviciosAdmin.find(s => s.id === servicioId);
    
    if (!servicio) {
        window.TechSolvers.mostrarNotificacion('Servicio no encontrado', 'error');
        return;
    }
    
    const modal = document.getElementById('modal-servicio');
    const titulo = document.getElementById('modal-servicio-titulo');
    
    if (modal && titulo) {
        titulo.textContent = '✏️ Editar Servicio';
        
        // Prellenar formulario
        document.getElementById('servicio-edit-id').value = servicio.id;
        document.getElementById('servicio-titulo').value = servicio.titulo;
        document.getElementById('servicio-tipo').value = servicio.tipoServicio;
        document.getElementById('servicio-descripcion').value = servicio.descripcion;
        document.getElementById('servicio-precio').value = servicio.precio || '';
        document.getElementById('servicio-mostrar-inicio').checked = servicio.mostrarEnInicio;
        
        modal.classList.add('activo');
    }
}

async function toggleVisibilidad(servicioId, nuevaVisibilidad) {
    try {
        const datos = { mostrarEnInicio: nuevaVisibilidad };
        
        await window.TechSolvers.realizarPeticionAPI(`/servicios/${servicioId}/visibilidad`, {
            method: 'PUT',
            body: JSON.stringify(datos)
        });
        
        // Actualizar estado local
        const servicio = estadoServicios.serviciosAdmin.find(s => s.id === servicioId);
        if (servicio) {
            servicio.mostrarEnInicio = nuevaVisibilidad;
        }
        
        renderizarPanelAdmin();
        
        const accion = nuevaVisibilidad ? 'mostrado' : 'ocultado';
        window.TechSolvers.mostrarNotificacion(`Servicio ${accion} en inicio`, 'success');
        
    } catch (error) {
        console.error('❌ Error al cambiar visibilidad:', error);
        window.TechSolvers.mostrarNotificacion('Error al cambiar visibilidad', 'error');
    }
}

async function eliminarServicio(servicioId) {
    const servicio = estadoServicios.serviciosAdmin.find(s => s.id === servicioId);
    
    if (!servicio) {
        window.TechSolvers.mostrarNotificacion('Servicio no encontrado', 'error');
        return;
    }
    
    if (!confirm(`¿Estás seguro de eliminar el servicio "${servicio.titulo}"?`)) {
        return;
    }
    
    try {
        await window.TechSolvers.realizarPeticionAPI(`/servicios/${servicioId}`, {
            method: 'DELETE'
        });
        
        // Remover del estado local
        estadoServicios.serviciosAdmin = estadoServicios.serviciosAdmin.filter(s => s.id !== servicioId);
        estadoServicios.servicios = estadoServicios.servicios.filter(s => s.id !== servicioId);
        
        renderizarPanelAdmin();
        renderizarServicios();
        
        window.TechSolvers.mostrarNotificacion('Servicio eliminado correctamente', 'success');
        
    } catch (error) {
        console.error('❌ Error al eliminar servicio:', error);
        window.TechSolvers.mostrarNotificacion('Error al eliminar servicio', 'error');
    }
}

// ========================================
// MANEJO DE FORMULARIOS
// ========================================
async function enviarFormularioServicio(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const servicioId = document.getElementById('servicio-edit-id').value;
    
    const datos = {
        titulo: formData.get('titulo'),
        tipoServicio: formData.get('tipoServicio'),
        descripcion: formData.get('descripcion'),
        precio: formData.get('precio') ? parseFloat(formData.get('precio')) : null,
        mostrarEnInicio: formData.get('mostrarEnInicio') === 'on',
        creadoPor: window.TechSolvers.usuario?.id
    };
    
    try {
        let servicio;
        
        if (servicioId) {
            // Actualizar servicio existente
            servicio = await window.TechSolvers.realizarPeticionAPI(`/servicios/${servicioId}`, {
                method: 'PUT',
                body: JSON.stringify(datos)
            });
            
            window.TechSolvers.mostrarNotificacion('Servicio actualizado correctamente', 'success');
        } else {
            // Crear nuevo servicio
            servicio = await window.TechSolvers.realizarPeticionAPI('/servicios', {
                method: 'POST',
                body: JSON.stringify(datos)
            });
            
            window.TechSolvers.mostrarNotificacion('Servicio creado correctamente', 'success');
        }
        
        // Actualizar datos locales
        await cargarDatosAdmin();
        await cargarServicios();
        await cargarServiciosParaFormulario();
        
        renderizarPanelAdmin();
        renderizarServicios();
        
        cerrarModalServicio();
        
    } catch (error) {
        console.error('❌ Error al guardar servicio:', error);
        window.TechSolvers.mostrarNotificacion('Error al guardar servicio', 'error');
    }
}

async function enviarFormularioContacto(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const btnEnviar = event.target.querySelector('.btn-enviar');
    
    // Deshabilitar botón mientras se envía
    btnEnviar.disabled = true;
    btnEnviar.textContent = 'Enviando...';
    
    const datos = {
        servicioId: parseInt(formData.get('servicioId')),
        nombre: formData.get('nombre'),
        email: formData.get('email'),
        telefono: formData.get('telefono'),
        mensaje: formData.get('mensaje'),
        urgencia: formData.get('urgencia') || 'MEDIA'
    };
    
    try {
        const consulta = await window.TechSolvers.realizarPeticionAPI('/contacto-servicios', {
            method: 'POST',
            body: JSON.stringify(datos)
        });
        
        window.TechSolvers.mostrarNotificacion('Consulta enviada correctamente. Te contactaremos pronto.', 'success');
        
        // Limpiar formulario
        event.target.reset();
        
        // Recargar consultas si el usuario está logueado
        if (window.TechSolvers.usuario) {
            await cargarMisConsultas();
            renderizarMisConsultas();
        }
        
    } catch (error) {
        console.error('❌ Error al enviar consulta:', error);
        window.TechSolvers.mostrarNotificacion('Error al enviar consulta', 'error');
    } finally {
        btnEnviar.disabled = false;
        btnEnviar.textContent = '📧 Enviar Solicitud';
    }
}

function limpiarFormulario() {
    const form = document.getElementById('form-contacto-servicio');
    if (form) {
        form.reset();
        window.TechSolvers.mostrarNotificacion('Formulario limpiado', 'success');
    }
}

// ========================================
// GESTIÓN DE CONSULTAS (ADMIN)
// ========================================
async function marcarComoContactado() {
    if (!estadoServicios.consultaSeleccionada) return;
    
    try {
        const consultaId = estadoServicios.consultaSeleccionada.id;
        
        await window.TechSolvers.realizarPeticionAPI(`/contacto-servicios/${consultaId}/contactado`, {
            method: 'PUT',
            body: JSON.stringify({
                atendidoPor: window.TechSolvers.usuario?.id
            })
        });
        
        window.TechSolvers.mostrarNotificacion('Consulta marcada como contactada', 'success');
        
        await cargarMisConsultas();
        renderizarMisConsultas();
        cerrarModalConsulta();
        
    } catch (error) {
        console.error('❌ Error al marcar como contactado:', error);
        window.TechSolvers.mostrarNotificacion('Error al actualizar estado', 'error');
    }
}

async function marcarComoResuelto() {
    if (!estadoServicios.consultaSeleccionada) return;
    
    try {
        const consultaId = estadoServicios.consultaSeleccionada.id;
        
        await window.TechSolvers.realizarPeticionAPI(`/contacto-servicios/${consultaId}/resuelto`, {
            method: 'PUT',
            body: JSON.stringify({
                atendidoPor: window.TechSolvers.usuario?.id
            })
        });
        
        window.TechSolvers.mostrarNotificacion('Consulta marcada como resuelta', 'success');
        
        await cargarMisConsultas();
        renderizarMisConsultas();
        cerrarModalConsulta();
        
    } catch (error) {
        console.error('❌ Error al marcar como resuelto:', error);
        window.TechSolvers.mostrarNotificacion('Error al actualizar estado', 'error');
    }
}

// ========================================
// GESTIÓN DE MODALES
// ========================================
function cerrarModalServicio() {
    const modal = document.getElementById('modal-servicio');
    if (modal) {
        modal.classList.remove('activo');
    }
}

function cerrarModalConsulta() {
    const modal = document.getElementById('modal-consulta');
    if (modal) {
        modal.classList.remove('activo');
    }
    estadoServicios.consultaSeleccionada = null;
}

// ========================================
// ESTADÍSTICAS (ADMIN)
// ========================================
async function verEstadisticas() {
    try {
        const estadisticas = await window.TechSolvers.realizarPeticionAPI('/servicios/estadisticas');
        
        const modal = document.getElementById('modal-consulta');
        const detalles = document.getElementById('consulta-detalles');
        
        if (modal && detalles) {
            detalles.innerHTML = `
                <div class="detalle-item">
                    <span class="detalle-label">Total de servicios:</span>
                    <div class="detalle-valor">${estadisticas.totalServicios || 0}</div>
                </div>
                <div class="detalle-item">
                    <span class="detalle-label">Servicios activos:</span>
                    <div class="detalle-valor">${estadisticas.serviciosActivos || 0}</div>
                </div>
                <div class="detalle-item">
                    <span class="detalle-label">Consultas totales:</span>
                    <div class="detalle-valor">${estadisticas.totalConsultas || 0}</div>
                </div>
                <div class="detalle-item">
                    <span class="detalle-label">Consultas pendientes:</span>
                    <div class="detalle-valor">${estadisticas.consultasPendientes || 0}</div>
                </div>
                <div class="detalle-item">
                    <span class="detalle-label">Tipo más solicitado:</span>
                    <div class="detalle-valor">${estadisticas.tipoMasSolicitado || 'N/A'}</div>
                </div>
            `;
            
            const adminActions = document.getElementById('consulta-admin-actions');
            if (adminActions) {
                adminActions.style.display = 'none';
            }
            
            modal.classList.add('activo');
        }
        
    } catch (error) {
        console.error('❌ Error al cargar estadísticas:', error);
        window.TechSolvers.mostrarNotificacion('Error al cargar estadísticas', 'error');
    }
}

// ========================================
// UTILIDADES
// ========================================
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
                <h2>❌ Error al cargar servicios técnicos</h2>
                <p>No se pudo conectar con el servidor. Verifica tu conexión e inténtalo nuevamente.</p>
                <button onclick="location.reload()" class="btn-primary" style="margin-top: 20px;">
                    🔄 Recargar página
                </button>
            </div>
        `;
    }
}

// ========================================
// INICIALIZACIÓN DE EVENTOS
// ========================================
function inicializarEventos() {
    // Formulario de servicio (modal)
    const formServicio = document.getElementById('form-servicio');
    if (formServicio) {
        formServicio.addEventListener('submit', enviarFormularioServicio);
    }
    
    // Formulario de contacto
    const formContacto = document.getElementById('form-contacto-servicio');
    if (formContacto) {
        formContacto.addEventListener('submit', enviarFormularioContacto);
    }
    
    // Cerrar modales al hacer clic fuera
    document.addEventListener('click', function(event) {
        if (event.target.classList.contains('modal-overlay')) {
            event.target.classList.remove('activo');
        }
    });
    
    // Buscar con Enter en filtros
    const busquedasInputs = [
        'buscar-servicios-publico',
        'buscar-servicios-admin'
    ];
    
    busquedasInputs.forEach(inputId => {
        const input = document.getElementById(inputId);
        if (input) {
            input.addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    if (inputId.includes('publico')) {
                        buscarServiciosPublicos();
                    } else {
                        buscarServiciosAdmin();
                    }
                }
            });
        }
    });
    
    console.log('🎯 Eventos de servicios técnicos inicializados');
}

// ========================================
// FUNCIONES PÚBLICAS PARA EL HTML
// ========================================
window.ServicioTecnicoFunctions = {
    // Funciones de filtrado
    filtrarServiciosPublicos,
    filtrarServiciosAdmin,
    filtrarMisConsultas,
    buscarServiciosPublicos,
    buscarServiciosAdmin,
    limpiarFiltros,
    
    // Funciones de interacción
    solicitarServicio,
    verDetallesServicio,
    verDetallesConsulta,
    
    // CRUD de servicios
    mostrarModalCrearServicio,
    editarServicio,
    toggleVisibilidad,
    eliminarServicio,
    
    // Gestión de formularios
    limpiarFormulario,
    
    // Gestión de consultas
    marcarComoContactado,
    marcarComoResuelto,
    
    // Modales
    cerrarModalServicio,
    cerrarModalConsulta,
    
    // Estadísticas
    verEstadisticas,
    
    // Recarga de datos
    cargarServicios,
    cargarDatosAdmin,
    cargarMisConsultas
};

// Hacer funciones globalmente accesibles
Object.assign(window, window.ServicioTecnicoFunctions);

console.log('🔧 Servicio Técnico JS completamente configurado');

