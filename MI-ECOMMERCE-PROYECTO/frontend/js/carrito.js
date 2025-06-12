// ========================================
// JAVASCRIPT ESPECÍFICO - CARRITO DE COMPRAS
// Proceso completo de checkout con múltiples pasos
// ========================================

// ESTADO DEL CARRITO
let estadoCarrito = {
    paso: 1,
    carrito: [],
    datosCliente: {},
    metodoPago: null,
    orden: null,
    cargando: false,
    costoEnvio: 0,
    descuento: 0,
    cuponAplicado: null
};

// CONFIGURACIÓN DEL CARRITO
const CONFIG_CARRITO = {
    costoEnvioGratis: 150,
    costoEnvioStandard: 15,
    tiemposEntrega: {
        'domicilio': '2-3 días hábiles',
        'tienda': '1-2 días hábiles'
    },
    distritos: [
        'Lima', 'Miraflores', 'San Isidro', 'La Molina', 'Surco',
        'San Borja', 'Jesús María', 'Magdalena', 'Pueblo Libre',
        'Lince', 'Breña'
    ]
};

document.addEventListener('DOMContentLoaded', function() {
    console.log('🛒 Carrito JS cargado');
    
    // Inicializar carrito
    inicializarCarrito();
    
    console.log('✅ Carrito completamente inicializado');
});

// ========================================
// INICIALIZACIÓN PRINCIPAL
// ========================================
async function inicializarCarrito() {
    try {
        // Cargar carrito desde localStorage o backend
        await cargarCarrito();
        
        // Configurar eventos
        configurarEventos();
        
        // Mostrar paso inicial
        mostrarPaso(1);
        
        console.log('🎯 Carrito completamente cargado');
        
    } catch (error) {
        console.error('❌ Error al inicializar carrito:', error);
        mostrarErrorCarrito();
    }
}

async function cargarCarrito() {
    // Cargar desde localStorage (carrito global)
    estadoCarrito.carrito = window.TechSolvers.carrito || [];
    
    // Si hay usuario logueado, intentar sincronizar con backend
    if (window.TechSolvers.usuario) {
        try {
            const carritoBackend = await window.TechSolvers.realizarPeticionAPI(
                `/carritos/usuario/${window.TechSolvers.usuario.id}`
            );
            
            if (carritoBackend && carritoBackend.items) {
                // Sincronizar carritos
                estadoCarrito.carrito = carritoBackend.items.map(item => ({
                    id: item.producto.id,
                    nombre: item.producto.nombre,
                    precio: item.precioUnitario,
                    imagen: item.producto.imagenUrl,
                    cantidad: item.cantidad,
                    stock: item.producto.stock
                }));
            }
        } catch (error) {
            console.warn('No se pudo cargar carrito del backend:', error);
        }
    }
    
    console.log(`🛒 Carrito cargado: ${estadoCarrito.carrito.length} items`);
}

// ========================================
// GESTIÓN DE PASOS
// ========================================
function mostrarPaso(numeroPaso) {
    if (numeroPaso < 1 || numeroPaso > 5) return;
    
    estadoCarrito.paso = numeroPaso;
    
    // Actualizar progress bar
    actualizarProgressBar();
    
    // Ocultar todos los pasos
    document.querySelectorAll('.carrito-step').forEach(step => {
        step.classList.remove('active');
    });
    
    // Mostrar paso actual
    const pasoActual = document.getElementById(obtenerIdPaso(numeroPaso));
    if (pasoActual) {
        pasoActual.classList.add('active');
    }
    
    // Cargar contenido del paso
    switch (numeroPaso) {
        case 1:
            mostrarCarritoItems();
            break;
        case 2:
            mostrarFormularioDatos();
            break;
        case 3:
            mostrarMetodosPago();
            break;
        case 4:
            mostrarConfirmacion();
            break;
        case 5:
            mostrarPedidoCompletado();
            break;
    }
    
    console.log(`📍 Mostrando paso ${numeroPaso}`);
}

function obtenerIdPaso(numero) {
    const pasos = {
        1: 'paso-carrito',
        2: 'paso-datos',
        3: 'paso-pago',
        4: 'paso-confirmacion',
        5: 'paso-completado'
    };
    return pasos[numero];
}

function actualizarProgressBar() {
    document.querySelectorAll('.progress-step').forEach((step, index) => {
        const numeroStep = index + 1;
        
        step.classList.remove('active', 'completed');
        
        if (numeroStep < estadoCarrito.paso) {
            step.classList.add('completed');
        } else if (numeroStep === estadoCarrito.paso) {
            step.classList.add('active');
        }
    });
    
    // Actualizar líneas de progreso
    document.querySelectorAll('.progress-line').forEach((line, index) => {
        if (index + 1 < estadoCarrito.paso) {
            line.classList.add('completed');
        } else {
            line.classList.remove('completed');
        }
    });
}

// ========================================
// PASO 1: MOSTRAR CARRITO
// ========================================
function mostrarCarritoItems() {
    const carritoItemsContainer = document.getElementById('carrito-items');
    const carritoResumenContainer = document.getElementById('carrito-resumen');
    
    if (!carritoItemsContainer || !carritoResumenContainer) return;
    
    if (estadoCarrito.carrito.length === 0) {
        carritoItemsContainer.innerHTML = `
            <div class="carrito-vacio">
                <div class="vacio-icon">🛒</div>
                <h2>Tu carrito está vacío</h2>
                <p>Explora nuestros productos y agrega algunos a tu carrito</p>
                <a href="productos.html" class="btn-primary">Ir a Comprar</a>
            </div>
        `;
        
        carritoResumenContainer.innerHTML = '';
        document.getElementById('continuar-datos').disabled = true;
        return;
    }
    
    // Mostrar items del carrito
    const itemsHTML = estadoCarrito.carrito.map((item, index) => `
        <div class="carrito-item">
            <img src="${item.imagen || 'imagenes/producto-default.jpg'}" 
                 alt="${item.nombre}" 
                 class="item-imagen"
                 onerror="this.src='imagenes/producto-default.jpg'">
            
            <div class="item-info">
                <h3 class="item-nombre">${item.nombre}</h3>
                <div class="item-descripcion">Stock disponible: ${item.stock}</div>
                <div class="item-precio-unitario">${window.TechSolvers.formatearPrecio(item.precio)} c/u</div>
            </div>
            
            <div class="item-controles">
                <div class="cantidad-container">
                    <button class="cantidad-btn" onclick="cambiarCantidad(${index}, ${item.cantidad - 1})"
                            ${item.cantidad <= 1 ? 'disabled' : ''}>-</button>
                    <span class="cantidad-display">${item.cantidad}</span>
                    <button class="cantidad-btn" onclick="cambiarCantidad(${index}, ${item.cantidad + 1})"
                            ${item.cantidad >= item.stock ? 'disabled' : ''}>+</button>
                </div>
                
                <div class="item-subtotal">${window.TechSolvers.formatearPrecio(item.precio * item.cantidad)}</div>
                
                <button class="btn-eliminar-item" onclick="eliminarItem(${index})">
                    🗑️ Eliminar
                </button>
            </div>
        </div>
    `).join('');
    
    carritoItemsContainer.innerHTML = itemsHTML;
    
    // Mostrar resumen
    mostrarResumenCarrito(carritoResumenContainer);
    
    // Habilitar botón continuar
    document.getElementById('continuar-datos').disabled = false;
}

function mostrarResumenCarrito(container) {
    const subtotal = calcularSubtotal();
    const costoEnvio = calcularCostoEnvio(subtotal);
    const descuento = estadoCarrito.descuento;
    const total = subtotal + costoEnvio - descuento;
    
    container.innerHTML = `
        <h3 class="resumen-titulo">📋 Resumen del Pedido</h3>
        
        <div class="resumen-item">
            <span class="resumen-label">Subtotal (${estadoCarrito.carrito.length} productos)</span>
            <span class="resumen-valor">${window.TechSolvers.formatearPrecio(subtotal)}</span>
        </div>
        
        <div class="resumen-item">
            <span class="resumen-label">Costo de envío</span>
            <span class="resumen-valor">
                ${costoEnvio === 0 ? 'GRATIS' : window.TechSolvers.formatearPrecio(costoEnvio)}
            </span>
        </div>
        
        ${descuento > 0 ? `
            <div class="resumen-item">
                <span class="resumen-label">Descuento (${estadoCarrito.cuponAplicado})</span>
                <span class="resumen-valor" style="color: #28a745;">-${window.TechSolvers.formatearPrecio(descuento)}</span>
            </div>
        ` : ''}
        
        <div class="resumen-total">
            <div class="resumen-item">
                <span class="resumen-label">TOTAL</span>
                <span class="resumen-valor">${window.TechSolvers.formatearPrecio(total)}</span>
            </div>
        </div>
        
        ${subtotal < CONFIG_CARRITO.costoEnvioGratis ? `
            <div style="margin-top: 15px; padding: 12px; background: #fff3cd; border-radius: 8px; font-size: 0.9rem; color: #856404;">
                💡 Agrega ${window.TechSolvers.formatearPrecio(CONFIG_CARRITO.costoEnvioGratis - subtotal)} más para envío GRATIS
            </div>
        ` : ''}
        
        <div class="cupon-section">
            <div class="cupon-input-group">
                <input type="text" id="cupon-input" class="cupon-input" placeholder="Código de cupón">
                <button class="btn-aplicar-cupon" onclick="aplicarCupon()">Aplicar</button>
            </div>
        </div>
    `;
}

function cambiarCantidad(index, nuevaCantidad) {
    if (nuevaCantidad < 1) {
        eliminarItem(index);
        return;
    }
    
    const item = estadoCarrito.carrito[index];
    if (nuevaCantidad > item.stock) {
        window.TechSolvers.mostrarNotificacion('Cantidad excede el stock disponible', 'warning');
        return;
    }
    
    item.cantidad = nuevaCantidad;
    
    // Actualizar localStorage
    window.TechSolvers.carrito = estadoCarrito.carrito;
    localStorage.setItem('carrito', JSON.stringify(estadoCarrito.carrito));
    
    // Actualizar contador global
    window.TechSolvers.actualizarContadorCarrito();
    
    // Recargar vista
    mostrarCarritoItems();
    
    window.TechSolvers.mostrarNotificacion('Cantidad actualizada', 'success');
}

function eliminarItem(index) {
    const item = estadoCarrito.carrito[index];
    estadoCarrito.carrito.splice(index, 1);
    
    // Actualizar localStorage
    window.TechSolvers.carrito = estadoCarrito.carrito;
    localStorage.setItem('carrito', JSON.stringify(estadoCarrito.carrito));
    
    // Actualizar contador global
    window.TechSolvers.actualizarContadorCarrito();
    
    // Recargar vista
    mostrarCarritoItems();
    
    window.TechSolvers.mostrarNotificacion(`${item.nombre} eliminado del carrito`, 'success');
}

async function aplicarCupon() {
    const cuponInput = document.getElementById('cupon-input');
    const codigo = cuponInput.value.trim().toUpperCase();
    
    if (!codigo) {
        window.TechSolvers.mostrarNotificacion('Ingresa un código de cupón', 'warning');
        return;
    }
    
    // Simulación de cupones (en producción sería una llamada al backend)
    const cupones = {
        'DESCUENTO10': { descuento: 0.10, tipo: 'porcentaje' },
        'ENVIOGRATIS': { descuento: CONFIG_CARRITO.costoEnvioStandard, tipo: 'fijo' },
        'NUEVO2025': { descuento: 0.15, tipo: 'porcentaje' }
    };
    
    if (cupones[codigo]) {
        const cupon = cupones[codigo];
        const subtotal = calcularSubtotal();
        
        if (cupon.tipo === 'porcentaje') {
            estadoCarrito.descuento = subtotal * cupon.descuento;
        } else {
            estadoCarrito.descuento = cupon.descuento;
        }
        
        estadoCarrito.cuponAplicado = codigo;
        cuponInput.value = '';
        
        mostrarCarritoItems();
        window.TechSolvers.mostrarNotificacion(`Cupón ${codigo} aplicado correctamente`, 'success');
    } else {
        window.TechSolvers.mostrarNotificacion('Código de cupón inválido', 'error');
    }
}

// ========================================
// PASO 2: DATOS DEL CLIENTE
// ========================================
function mostrarFormularioDatos() {
    // Pre-llenar con datos del usuario si está logueado
    if (window.TechSolvers.usuario) {
        const usuario = window.TechSolvers.usuario;
        
        document.getElementById('nombre').value = usuario.nombre || '';
        document.getElementById('apellido').value = usuario.apellido || '';
        document.getElementById('email').value = usuario.email || '';
        document.getElementById('telefono').value = usuario.telefono || '';
        document.getElementById('dni').value = usuario.dni || '';
        document.getElementById('direccion').value = usuario.direccion || '';
        document.getElementById('distrito').value = usuario.distrito || '';
    }
    
    // Configurar eventos específicos de este paso
    configurarEventosDatos();
}

function configurarEventosDatos() {
    // Cambio de tipo de entrega
    const tipoEntregaInputs = document.querySelectorAll('input[name="tipoEntrega"]');
    tipoEntregaInputs.forEach(input => {
        input.addEventListener('change', function() {
            const direccionCampos = document.getElementById('direccion-campos');
            if (this.value === 'tienda') {
                direccionCampos.classList.add('oculto');
                direccionCampos.querySelectorAll('input, select, textarea').forEach(field => {
                    field.removeAttribute('required');
                });
            } else {
                direccionCampos.classList.remove('oculto');
                direccionCampos.querySelectorAll('input[data-required], select[data-required], textarea[data-required]').forEach(field => {
                    field.setAttribute('required', 'required');
                });
            }
        });
    });
    
    // Validación en tiempo real
    const camposRequeridos = document.querySelectorAll('#datos-form input[required], #datos-form select[required]');
    camposRequeridos.forEach(campo => {
        campo.addEventListener('input', validarFormularioDatos);
        campo.addEventListener('change', validarFormularioDatos);
    });
}

function validarFormularioDatos() {
    const form = document.getElementById('datos-form');
    const continuarBtn = document.getElementById('continuar-pago');
    
    if (form.checkValidity()) {
        continuarBtn.disabled = false;
        // Guardar datos
        recopilarDatosCliente();
    } else {
        continuarBtn.disabled = true;
    }
}

function recopilarDatosCliente() {
    const tipoEntrega = document.querySelector('input[name="tipoEntrega"]:checked').value;
    
    estadoCarrito.datosCliente = {
        nombre: document.getElementById('nombre').value.trim(),
        apellido: document.getElementById('apellido').value.trim(),
        email: document.getElementById('email').value.trim(),
        telefono: document.getElementById('telefono').value.trim(),
        dni: document.getElementById('dni').value.trim(),
        tipoEntrega: tipoEntrega,
        direccion: tipoEntrega === 'domicilio' ? document.getElementById('direccion').value.trim() : '',
        distrito: tipoEntrega === 'domicilio' ? document.getElementById('distrito').value : '',
        referencia: tipoEntrega === 'domicilio' ? document.getElementById('referencia').value.trim() : '',
        notas: document.getElementById('notas').value.trim()
    };
    
    console.log('💾 Datos del cliente guardados:', estadoCarrito.datosCliente);
}

// ========================================
// PASO 3: MÉTODO DE PAGO
// ========================================
function mostrarMetodosPago() {
    // Mostrar resumen de pago
    mostrarResumenPago();
    
    // Configurar eventos de métodos de pago
    configurarEventosPago();
}

function mostrarResumenPago() {
    const container = document.getElementById('pago-resumen');
    if (!container) return;
    
    const subtotal = calcularSubtotal();
    const costoEnvio = calcularCostoEnvio(subtotal);
    const descuento = estadoCarrito.descuento;
    const total = subtotal + costoEnvio - descuento;
    
    container.innerHTML = `
        <h3 class="resumen-titulo">💰 Resumen de Pago</h3>
        
        <div class="resumen-item">
            <span class="resumen-label">Subtotal</span>
            <span class="resumen-valor">${window.TechSolvers.formatearPrecio(subtotal)}</span>
        </div>
        
        <div class="resumen-item">
            <span class="resumen-label">Envío</span>
            <span class="resumen-valor">
                ${costoEnvio === 0 ? 'GRATIS' : window.TechSolvers.formatearPrecio(costoEnvio)}
            </span>
        </div>
        
        ${descuento > 0 ? `
            <div class="resumen-item">
                <span class="resumen-label">Descuento</span>
                <span class="resumen-valor" style="color: #28a745;">-${window.TechSolvers.formatearPrecio(descuento)}</span>
            </div>
        ` : ''}
        
        <div class="resumen-total">
            <div class="resumen-item">
                <span class="resumen-label">TOTAL A PAGAR</span>
                <span class="resumen-valor">${window.TechSolvers.formatearPrecio(total)}</span>
            </div>
        </div>
        
        <div style="margin-top: 20px; padding: 15px; background: #e8f5e8; border-radius: 8px; font-size: 0.9rem;">
            🔒 <strong>Pago 100% seguro</strong><br>
            Tus datos están protegidos con encriptación SSL
        </div>
    `;
}

function configurarEventosPago() {
    // Eventos para métodos de pago
    const metodoPagoInputs = document.querySelectorAll('input[name="metodoPago"]');
    metodoPagoInputs.forEach(input => {
        input.addEventListener('change', function() {
            // Ocultar todos los detalles
            document.querySelectorAll('.metodo-detalles').forEach(detalle => {
                detalle.classList.remove('activo');
            });
            
            // Mostrar detalles del método seleccionado
            const detalles = document.getElementById(`detalles-${this.value}`);
            if (detalles) {
                detalles.classList.add('activo');
            }
            
            estadoCarrito.metodoPago = this.value;
            
            // Habilitar botón continuar
            document.getElementById('continuar-confirmacion').disabled = false;
            
            console.log(`💳 Método de pago seleccionado: ${this.value}`);
        });
    });
    
    // Eventos para subida de archivos
    configurarSubidaArchivos();
    
    // Formateo de campos de tarjeta
    configurarCamposTarjeta();
}

function configurarSubidaArchivos() {
    const uploadsInputs = document.querySelectorAll('.upload-input');
    uploadsInputs.forEach(input => {
        input.addEventListener('change', function() {
            const file = this.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    mostrarPreviewImagen(input.id, e.target.result, file.name);
                };
                reader.readAsDataURL(file);
                
                window.TechSolvers.mostrarNotificacion('Comprobante cargado correctamente', 'success');
            }
        });
    });
}

function mostrarPreviewImagen(inputId, src, fileName) {
    const container = document.querySelector(`#${inputId}`).parentNode;
    
    // Remover preview anterior si existe
    const previewExistente = container.querySelector('.upload-preview');
    if (previewExistente) {
        previewExistente.remove();
    }
    
    // Crear nuevo preview
    const preview = document.createElement('div');
    preview.className = 'upload-preview';
    preview.innerHTML = `
        <img src="${src}" alt="${fileName}">
        <p>${fileName}</p>
    `;
    
    container.appendChild(preview);
}

function configurarCamposTarjeta() {
    // Formateo de número de tarjeta
    const numeroTarjeta = document.getElementById('numero-tarjeta');
    if (numeroTarjeta) {
        numeroTarjeta.addEventListener('input', function() {
            let valor = this.value.replace(/\s/g, '').replace(/[^0-9]/gi, '');
            let valorFormateado = valor.match(/.{1,4}/g)?.join(' ') || valor;
            if (valorFormateado !== this.value) {
                this.value = valorFormateado;
            }
        });
    }
    
    // Formateo de fecha de vencimiento
    const vencimiento = document.getElementById('vencimiento');
    if (vencimiento) {
        vencimiento.addEventListener('input', function() {
            let valor = this.value.replace(/\D/g, '');
            if (valor.length >= 2) {
                valor = valor.substring(0, 2) + '/' + valor.substring(2, 4);
            }
            this.value = valor;
        });
    }
    
    // Formateo de CVV
    const cvv = document.getElementById('cvv');
    if (cvv) {
        cvv.addEventListener('input', function() {
            this.value = this.value.replace(/[^0-9]/g, '');
        });
    }
}

// ========================================
// PASO 4: CONFIRMACIÓN
// ========================================
function mostrarConfirmacion() {
    mostrarResumenPedido();
    mostrarResumenEntrega();
    mostrarResumenMetodoPago();
    
    // Configurar checkbox de términos
    const terminosCheckbox = document.getElementById('aceptar-terminos');
    const finalizarBtn = document.getElementById('finalizar-pedido');
    
    if (terminosCheckbox && finalizarBtn) {
        terminosCheckbox.addEventListener('change', function() {
            finalizarBtn.disabled = !this.checked;
        });
    }
}

function mostrarResumenPedido() {
    const container = document.getElementById('pedido-resumen');
    if (!container) return;
    
    const productosHTML = estadoCarrito.carrito.map(item => `
        <div class="resumen-producto">
            <img src="${item.imagen || 'imagenes/producto-default.jpg'}" 
                 alt="${item.nombre}" 
                 class="resumen-imagen">
            <div class="resumen-info">
                <div class="resumen-nombre">${item.nombre}</div>
                <div class="resumen-detalles">Cantidad: ${item.cantidad} × ${window.TechSolvers.formatearPrecio(item.precio)}</div>
            </div>
            <div class="resumen-precio">${window.TechSolvers.formatearPrecio(item.precio * item.cantidad)}</div>
        </div>
    `).join('');
    
    const subtotal = calcularSubtotal();
    const costoEnvio = calcularCostoEnvio(subtotal);
    const descuento = estadoCarrito.descuento;
    const total = subtotal + costoEnvio - descuento;
    
    container.innerHTML = `
        ${productosHTML}
        
        <div style="margin-top: 20px; padding-top: 20px; border-top: 2px solid #ddd;">
            <div class="dato-item">
                <span class="dato-label">Subtotal:</span>
                <span class="dato-valor">${window.TechSolvers.formatearPrecio(subtotal)}</span>
            </div>
            <div class="dato-item">
                <span class="dato-label">Envío:</span>
                <span class="dato-valor">${costoEnvio === 0 ? 'GRATIS' : window.TechSolvers.formatearPrecio(costoEnvio)}</span>
            </div>
            ${descuento > 0 ? `
                <div class="dato-item">
                    <span class="dato-label">Descuento:</span>
                    <span class="dato-valor" style="color: #28a745;">-${window.TechSolvers.formatearPrecio(descuento)}</span>
                </div>
            ` : ''}
            <div class="dato-item" style="border-top: 2px solid #007bff; font-weight: bold; font-size: 1.2rem;">
                <span class="dato-label">TOTAL:</span>
                <span class="dato-valor" style="color: #007bff;">${window.TechSolvers.formatearPrecio(total)}</span>
            </div>
        </div>
    `;
}

function mostrarResumenEntrega() {
    const container = document.getElementById('entrega-resumen');
    if (!container) return;
    
    const datos = estadoCarrito.datosCliente;
    const tiempoEntrega = CONFIG_CARRITO.tiemposEntrega[datos.tipoEntrega];
    
    container.innerHTML = `
        <div class="dato-item">
            <span class="dato-label">Nombre:</span>
            <span class="dato-valor">${datos.nombre} ${datos.apellido}</span>
        </div>
        <div class="dato-item">
            <span class="dato-label">Email:</span>
            <span class="dato-valor">${datos.email}</span>
        </div>
        <div class="dato-item">
            <span class="dato-label">Teléfono:</span>
            <span class="dato-valor">${datos.telefono}</span>
        </div>
        <div class="dato-item">
            <span class="dato-label">Tipo de entrega:</span>
            <span class="dato-valor">${datos.tipoEntrega === 'domicilio' ? '🚚 Entrega a domicilio' : '🏪 Recoger en tienda'}</span>
        </div>
        ${datos.tipoEntrega === 'domicilio' ? `
            <div class="dato-item">
                <span class="dato-label">Dirección:</span>
                <span class="dato-valor">${datos.direccion}</span>
            </div>
            <div class="dato-item">
                <span class="dato-label">Distrito:</span>
                <span class="dato-valor">${datos.distrito}</span>
            </div>
            ${datos.referencia ? `
                <div class="dato-item">
                    <span class="dato-label">Referencia:</span>
                    <span class="dato-valor">${datos.referencia}</span>
                </div>
            ` : ''}
        ` : ''}
        <div class="dato-item">
            <span class="dato-label">Tiempo estimado:</span>
            <span class="dato-valor">${tiempoEntrega}</span>
        </div>
        ${datos.notas ? `
            <div class="dato-item">
                <span class="dato-label">Notas:</span>
                <span class="dato-valor">${datos.notas}</span>
            </div>
        ` : ''}
    `;
}

function mostrarResumenMetodoPago() {
    const container = document.getElementById('pago-confirmacion');
    if (!container) return;
    
    const metodos = {
        'yape': '📱 Yape',
        'tarjeta': '💳 Tarjeta de Crédito/Débito',
        'deposito': '🏦 Depósito/Transferencia Bancaria'
    };
    
    const metodoPagoNombre = metodos[estadoCarrito.metodoPago] || 'No seleccionado';
    
    container.innerHTML = `
        <div class="dato-item">
            <span class="dato-label">Método seleccionado:</span>
            <span class="dato-valor">${metodoPagoNombre}</span>
        </div>
        
        ${estadoCarrito.metodoPago === 'yape' ? `
            <div style="margin-top: 15px; padding: 15px; background: #fff3cd; border-radius: 8px;">
                <strong>📱 Instrucciones Yape:</strong><br>
                Realiza el pago por Yape y sube el comprobante. Tu pedido se procesará una vez confirmemos el pago.
            </div>
        ` : ''}
        
        ${estadoCarrito.metodoPago === 'deposito' ? `
            <div style="margin-top: 15px; padding: 15px; background: #cce5ff; border-radius: 8px;">
                <strong>🏦 Instrucciones Depósito:</strong><br>
                Realiza la transferencia/depósito y sube el voucher. Tu pedido se procesará en 24-48 horas hábiles.
            </div>
        ` : ''}
        
        ${estadoCarrito.metodoPago === 'tarjeta' ? `
            <div style="margin-top: 15px; padding: 15px; background: #e8f5e8; border-radius: 8px;">
                <strong>💳 Pago con Tarjeta:</strong><br>
                Tu pago se procesará de forma segura y recibirás confirmación inmediata.
            </div>
        ` : ''}
    `;
}

// ========================================
// PASO 5: FINALIZAR PEDIDO
// ========================================
async function finalizarPedido() {
    if (estadoCarrito.cargando) return;
    
    try {
        estadoCarrito.cargando = true;
        
        const finalizarBtn = document.getElementById('finalizar-pedido');
        if (finalizarBtn) {
            finalizarBtn.disabled = true;
            finalizarBtn.innerHTML = '⏳ Procesando...';
        }
        
        // Crear orden en el backend
        const ordenData = construirOrdenData();
        
        const ordenCreada = await window.TechSolvers.realizarPeticionAPI('/ordenes/crear', {
            method: 'POST',
            body: JSON.stringify(ordenData)
        });
        
        estadoCarrito.orden = ordenCreada;
        
        // Limpiar carrito
        vaciarCarritoCompletamente();
        
        // Mostrar página de éxito
        mostrarPaso(5);
        
        window.TechSolvers.mostrarNotificacion('¡Pedido creado exitosamente!', 'success');
        
    } catch (error) {
        console.error('❌ Error al finalizar pedido:', error);
        window.TechSolvers.mostrarNotificacion('Error al procesar el pedido. Inténtalo nuevamente.', 'error');
        
        // Restaurar botón
        const finalizarBtn = document.getElementById('finalizar-pedido');
        if (finalizarBtn) {
            finalizarBtn.disabled = false;
            finalizarBtn.innerHTML = '🎉 Finalizar Pedido';
        }
    } finally {
        estadoCarrito.cargando = false;
    }
}

function construirOrdenData() {
    const subtotal = calcularSubtotal();
    const costoEnvio = calcularCostoEnvio(subtotal);
    const descuento = estadoCarrito.descuento;
    const total = subtotal + costoEnvio - descuento;
    
    return {
        usuarioId: window.TechSolvers.usuario?.id || null,
        trabajadorId: null, // Para compra autoservicio
        estado: 'PENDIENTE',
        metodoPago: estadoCarrito.metodoPago.toUpperCase(),
        total: total,
        datosCliente: {
            nombre: estadoCarrito.datosCliente.nombre,
            apellido: estadoCarrito.datosCliente.apellido,
            email: estadoCarrito.datosCliente.email,
            telefono: estadoCarrito.datosCliente.telefono,
            dni: estadoCarrito.datosCliente.dni,
            direccion: estadoCarrito.datosCliente.direccion,
            distrito: estadoCarrito.datosCliente.distrito,
            tipoEntrega: estadoCarrito.datosCliente.tipoEntrega,
            referencia: estadoCarrito.datosCliente.referencia,
            notas: estadoCarrito.datosCliente.notas
        },
        items: estadoCarrito.carrito.map(item => ({
            productoId: item.id,
            cantidad: item.cantidad,
            precioUnitario: item.precio
        })),
        tipoAtencion: 'AUTOSERVICIO',
        costoEnvio: costoEnvio,
        descuento: descuento,
        cuponAplicado: estadoCarrito.cuponAplicado
    };
}

function vaciarCarritoCompletamente() {
    estadoCarrito.carrito = [];
    window.TechSolvers.carrito = [];
    localStorage.setItem('carrito', JSON.stringify([]));
    window.TechSolvers.actualizarContadorCarrito();
    
    // Enviar evento de carrito actualizado
    window.dispatchEvent(new CustomEvent('carritoActualizado', { 
        detail: { carrito: [] } 
    }));
}

function mostrarPedidoCompletado() {
    if (!estadoCarrito.orden) return;
    
    // Actualizar información del pedido completado
    const numeroPedido = document.getElementById('numero-pedido-final');
    const emailConfirmacion = document.getElementById('email-confirmacion');
    const tiempoEntrega = document.getElementById('tiempo-entrega');
    
    if (numeroPedido) {
        numeroPedido.textContent = `#${estadoCarrito.orden.numeroOrden || estadoCarrito.orden.id}`;
    }
    
    if (emailConfirmacion) {
        emailConfirmacion.textContent = estadoCarrito.datosCliente.email;
    }
    
    if (tiempoEntrega) {
        tiempoEntrega.textContent = CONFIG_CARRITO.tiemposEntrega[estadoCarrito.datosCliente.tipoEntrega];
    }
    
    // Agregar efecto de celebración
    document.querySelector('.success-icon').classList.add('efecto-bounce');
    
    // Auto-scroll al top
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

// ========================================
// FUNCIONES DE UTILIDAD
// ========================================
function calcularSubtotal() {
    return estadoCarrito.carrito.reduce((total, item) => {
        return total + (item.precio * item.cantidad);
    }, 0);
}

function calcularCostoEnvio(subtotal) {
    if (estadoCarrito.datosCliente.tipoEntrega === 'tienda') {
        return 0; // Recoger en tienda es gratis
    }
    
    if (subtotal >= CONFIG_CARRITO.costoEnvioGratis) {
        return 0; // Envío gratis por monto
    }
    
    return CONFIG_CARRITO.costoEnvioStandard;
}

function imprimirRecibo() {
    // Preparar contenido para impresión
    const contenidoImpresion = `
        <html>
            <head>
                <title>Recibo - TechSolvers</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    .header { text-align: center; margin-bottom: 30px; }
                    .info { margin-bottom: 20px; }
                    .productos { margin-bottom: 20px; }
                    .total { font-weight: bold; font-size: 1.2rem; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>TechSolvers</h1>
                    <h2>Recibo de Compra</h2>
                    <p>Pedido #${estadoCarrito.orden?.numeroOrden || estadoCarrito.orden?.id}</p>
                </div>
                
                <div class="info">
                    <strong>Cliente:</strong> ${estadoCarrito.datosCliente.nombre} ${estadoCarrito.datosCliente.apellido}<br>
                    <strong>Email:</strong> ${estadoCarrito.datosCliente.email}<br>
                    <strong>Fecha:</strong> ${new Date().toLocaleDateString('es-PE')}<br>
                    <strong>Método de pago:</strong> ${estadoCarrito.metodoPago}
                </div>
                
                <div class="productos">
                    <h3>Productos:</h3>
                    ${estadoCarrito.carrito.map(item => `
                        <div>${item.nombre} - Cantidad: ${item.cantidad} - ${window.TechSolvers.formatearPrecio(item.precio * item.cantidad)}</div>
                    `).join('')}
                </div>
                
                <div class="total">
                    Total: ${window.TechSolvers.formatearPrecio(calcularSubtotal() + calcularCostoEnvio(calcularSubtotal()) - estadoCarrito.descuento)}
                </div>
            </body>
        </html>
    `;
    
    const ventanaImpresion = window.open('', '_blank');
    ventanaImpresion.document.write(contenidoImpresion);
    ventanaImpresion.document.close();
    ventanaImpresion.print();
}

function mostrarErrorCarrito() {
    const carritoContainer = document.querySelector('.carrito-container');
    if (carritoContainer) {
        carritoContainer.innerHTML = `
            <div class="carrito-vacio">
                <div class="vacio-icon">❌</div>
                <h2>Error al cargar el carrito</h2>
                <p>No se pudo cargar el carrito de compras. Verifica tu conexión e inténtalo nuevamente.</p>
                <button onclick="window.location.reload()" class="btn-primary">
                    🔄 Recargar página
                </button>
            </div>
        `;
    }
}

// ========================================
// CONFIGURACIÓN DE EVENTOS
// ========================================
function configurarEventos() {
    // Navegación entre pasos
    document.getElementById('continuar-datos')?.addEventListener('click', () => {
        if (estadoCarrito.carrito.length === 0) {
            window.TechSolvers.mostrarNotificacion('Tu carrito está vacío', 'warning');
            return;
        }
        mostrarPaso(2);
    });
    
    document.getElementById('volver-carrito')?.addEventListener('click', () => mostrarPaso(1));
    
    document.getElementById('continuar-pago')?.addEventListener('click', () => {
        recopilarDatosCliente();
        mostrarPaso(3);
    });
    
    document.getElementById('volver-datos')?.addEventListener('click', () => mostrarPaso(2));
    
    document.getElementById('continuar-confirmacion')?.addEventListener('click', () => {
        if (!estadoCarrito.metodoPago) {
            window.TechSolvers.mostrarNotificacion('Selecciona un método de pago', 'warning');
            return;
        }
        mostrarPaso(4);
    });
    
    document.getElementById('volver-pago')?.addEventListener('click', () => mostrarPaso(3));
    
    document.getElementById('finalizar-pedido')?.addEventListener('click', finalizarPedido);
    
    // Botón de imprimir recibo
    document.getElementById('imprimir-recibo')?.addEventListener('click', imprimirRecibo);
    
    // Escuchar cambios en el carrito global
    window.addEventListener('carritoActualizado', function(event) {
        estadoCarrito.carrito = event.detail.carrito;
        if (estadoCarrito.paso === 1) {
            mostrarCarritoItems();
        }
    });
    
    console.log('🎯 Eventos del carrito configurados');
}

// ========================================
// FUNCIONES PÚBLICAS PARA EL HTML
// ========================================
window.CarritoFunctions = {
    cambiarCantidad,
    eliminarItem,
    aplicarCupon,
    mostrarPaso,
    finalizarPedido,
    imprimirRecibo,
    validarFormularioDatos
};

console.log('🛒 Carrito JS completamente configurado');


