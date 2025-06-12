// ========================================
// JAVASCRIPT ESPECÍFICO - PÁGINA DE CONTACTO
// Funcionalidades exclusivas de contacto
// ========================================

document.addEventListener('DOMContentLoaded', function() {
    console.log('📞 Contacto JS cargado');
    
    // Inicializar funcionalidades específicas de contacto
    inicializarMapa();
    mostrarInfoUbicacion();
    crearBotonWhatsApp();
    inicializarFormularioContacto();
    animarElementosContacto();
    
    console.log('✅ Funcionalidades de contacto inicializadas');
});

// ========================================
// INTERACCIONES CON EL MAPA
// ========================================
function inicializarMapa() {
    const iframe = document.querySelector('.mapa iframe');
    
    if (iframe) {
        // Hacer clic en el mapa para abrir en Google Maps
        iframe.addEventListener('click', function() {
            const url = 'https://maps.google.com/?q=TECHSOLVERS-Audio+y+Tecnologia';
            window.open(url, '_blank');
        });
        
        // Mejorar accesibilidad
        iframe.setAttribute('title', 'Mapa de ubicación de TechSolvers');
        iframe.setAttribute('aria-label', 'Ubicación de nuestra tienda física');
        
        console.log('🗺️ Mapa inicializado');
    }
}

function mostrarInfoUbicacion() {
    const mapaDiv = document.querySelector('.mapa');
    
    if (mapaDiv && !mapaDiv.querySelector('.info-ubicacion')) {
        const config = window.TechSolvers.configuracion.empresa;
        
        const infoUbicacion = document.createElement('div');
        infoUbicacion.className = 'info-ubicacion';
        infoUbicacion.innerHTML = `
            <p><strong>📍 Dirección:</strong> ${config.direccion}</p>
            <p><strong>🏢 Referencia:</strong> Cerca del centro comercial Plaza Norte</p>
            <p><strong>📞 Teléfono:</strong> ${config.telefono}</p>
            <p><strong>💬 WhatsApp:</strong> +${config.whatsapp}</p>
        `;
        
        mapaDiv.appendChild(infoUbicacion);
        console.log('📍 Información de ubicación agregada');
    }
}

// ========================================
// FORMULARIO DE CONTACTO (si existe)
// ========================================
function inicializarFormularioContacto() {
    const formulario = document.getElementById('formulario-contacto');
    
    if (formulario) {
        formulario.addEventListener('submit', function(e) {
            e.preventDefault();
            procesarFormularioContacto();
        });
        
        // Validación en tiempo real
        const inputs = formulario.querySelectorAll('input, textarea');
        inputs.forEach(input => {
            input.addEventListener('blur', function() {
                validarCampo(this);
            });
            
            input.addEventListener('input', function() {
                limpiarError(this);
            });
        });
        
        console.log('📝 Formulario de contacto inicializado');
    }
}

async function procesarFormularioContacto() {
    const formulario = document.getElementById('formulario-contacto');
    const btnEnviar = formulario.querySelector('.btn-enviar');
    
    // Obtener datos del formulario
    const datosFormulario = {
        nombre: formulario.querySelector('#nombre').value.trim(),
        email: formulario.querySelector('#email').value.trim(),
        telefono: formulario.querySelector('#telefono').value.trim(),
        asunto: formulario.querySelector('#asunto').value.trim(),
        mensaje: formulario.querySelector('#mensaje').value.trim()
    };
    
    // Validar formulario
    if (!validarFormularioCompleto(datosFormulario)) {
        return;
    }
    
    // Mostrar estado de carga
    btnEnviar.disabled = true;
    btnEnviar.textContent = 'Enviando...';
    
    try {
        // Simular envío al backend (puedes conectar a tu API)
        await simularEnvioFormulario(datosFormulario);
        
        // Éxito
        window.TechSolvers.mostrarNotificacion('¡Mensaje enviado correctamente! Te contactaremos pronto.', 'success');
        formulario.reset();
        limpiarErrores();
        
    } catch (error) {
        console.error('Error al enviar formulario:', error);
        window.TechSolvers.mostrarNotificacion('Error al enviar el mensaje. Inténtalo nuevamente.', 'error');
    } finally {
        // Restaurar botón
        btnEnviar.disabled = false;
        btnEnviar.textContent = 'Enviar Mensaje';
    }
}

async function simularEnvioFormulario(datos) {
    // Simular delay de red
    await new Promise(resolve => setTimeout(resolve, 2000));
    
    // Aquí puedes conectar con tu backend
    // const response = await window.TechSolvers.realizarPeticionAPI('/contacto-servicios', {
    //     method: 'POST',
    //     body: JSON.stringify(datos)
    // });
    
    console.log('📤 Formulario enviado:', datos);
    return { success: true };
}

function validarFormularioCompleto(datos) {
    let esValido = true;
    
    // Limpiar errores previos
    limpiarErrores();
    
    // Validar nombre
    if (!datos.nombre) {
        mostrarErrorCampo('nombre', 'El nombre es requerido');
        esValido = false;
    } else if (datos.nombre.length < 2) {
        mostrarErrorCampo('nombre', 'El nombre debe tener al menos 2 caracteres');
        esValido = false;
    }
    
    // Validar email
    if (!datos.email) {
        mostrarErrorCampo('email', 'El email es requerido');
        esValido = false;
    } else if (!window.TechSolvers.validarEmail(datos.email)) {
        mostrarErrorCampo('email', 'El formato del email no es válido');
        esValido = false;
    }
    
    // Validar teléfono
    if (!datos.telefono) {
        mostrarErrorCampo('telefono', 'El teléfono es requerido');
        esValido = false;
    } else if (!window.TechSolvers.validarTelefono(datos.telefono)) {
        mostrarErrorCampo('telefono', 'El formato del teléfono no es válido');
        esValido = false;
    }
    
    // Validar asunto
    if (!datos.asunto) {
        mostrarErrorCampo('asunto', 'El asunto es requerido');
        esValido = false;
    }
    
    // Validar mensaje
    if (!datos.mensaje) {
        mostrarErrorCampo('mensaje', 'El mensaje es requerido');
        esValido = false;
    } else if (datos.mensaje.length < 10) {
        mostrarErrorCampo('mensaje', 'El mensaje debe tener al menos 10 caracteres');
        esValido = false;
    }
    
    return esValido;
}

function validarCampo(input) {
    const valor = input.value.trim();
    const nombre = input.name || input.id;
    
    limpiarError(input);
    
    switch (nombre) {
        case 'nombre':
            if (!valor) {
                mostrarErrorCampo(nombre, 'El nombre es requerido');
            } else if (valor.length < 2) {
                mostrarErrorCampo(nombre, 'El nombre debe tener al menos 2 caracteres');
            }
            break;
            
        case 'email':
            if (!valor) {
                mostrarErrorCampo(nombre, 'El email es requerido');
            } else if (!window.TechSolvers.validarEmail(valor)) {
                mostrarErrorCampo(nombre, 'El formato del email no es válido');
            }
            break;
            
        case 'telefono':
            if (!valor) {
                mostrarErrorCampo(nombre, 'El teléfono es requerido');
            } else if (!window.TechSolvers.validarTelefono(valor)) {
                mostrarErrorCampo(nombre, 'El formato del teléfono no es válido');
            }
            break;
            
        case 'mensaje':
            if (!valor) {
                mostrarErrorCampo(nombre, 'El mensaje es requerido');
            } else if (valor.length < 10) {
                mostrarErrorCampo(nombre, 'El mensaje debe tener al menos 10 caracteres');
            }
            break;
    }
}

function mostrarErrorCampo(campo, mensaje) {
    const input = document.getElementById(campo);
    const formGroup = input.closest('.form-group');
    
    formGroup.classList.add('error');
    
    // Crear mensaje de error si no existe
    let errorDiv = formGroup.querySelector('.error-mensaje');
    if (!errorDiv) {
        errorDiv = document.createElement('span');
        errorDiv.className = 'error-mensaje';
        formGroup.appendChild(errorDiv);
    }
    
    errorDiv.textContent = mensaje;
}

function limpiarError(input) {
    const formGroup = input.closest('.form-group');
    formGroup.classList.remove('error');
    
    const errorDiv = formGroup.querySelector('.error-mensaje');
    if (errorDiv) {
        errorDiv.remove();
    }
}

function limpiarErrores() {
    const errores = document.querySelectorAll('.error-mensaje');
    errores.forEach(error => error.remove());
    
    const formGroups = document.querySelectorAll('.form-group.error');
    formGroups.forEach(group => group.classList.remove('error'));
}

// ========================================
// BOTÓN FLOTANTE DE WHATSAPP
// ========================================
function crearBotonWhatsApp() {
    // Verificar si ya existe
    if (document.querySelector('.boton-whatsapp-flotante')) {
        return;
    }
    
    const config = window.TechSolvers.configuracion.empresa;
    const mensaje = encodeURIComponent('¡Hola! Me interesa información sobre sus productos y servicios de TechSolvers 💻📱');
    
    const botonWhatsApp = document.createElement('a');
    botonWhatsApp.href = `https://wa.me/${config.whatsapp}?text=${mensaje}`;
    botonWhatsApp.target = '_blank';
    botonWhatsApp.className = 'boton-whatsapp-flotante';
    botonWhatsApp.innerHTML = '💬';
    botonWhatsApp.title = 'Escríbenos por WhatsApp';
    botonWhatsApp.setAttribute('aria-label', 'Contactar por WhatsApp');
    
    // Agregar evento de click
    botonWhatsApp.addEventListener('click', function() {
        console.log('💬 Clic en botón WhatsApp');
        window.TechSolvers.mostrarNotificacion('Redirigiendo a WhatsApp...', 'info', 1500);
    });
    
    document.body.appendChild(botonWhatsApp);
    console.log('💬 Botón WhatsApp flotante creado');
}

// ========================================
// ANIMACIONES ESPECÍFICAS DE CONTACTO
// ========================================
function animarElementosContacto() {
    const elementosAnimados = [
        '.tienda-fisica',
        '.mapa',
        '.horarios',
        '.formulario-contacto'
    ];
    
    // Configurar Intersection Observer para animaciones
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
                
                // Efecto de desvanecimiento escalonado
                const delay = Array.from(entry.target.parentNode.children).indexOf(entry.target) * 100;
                entry.target.style.transitionDelay = `${delay}ms`;
            }
        });
    }, {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    });
    
    // Aplicar animaciones a elementos encontrados
    elementosAnimados.forEach(selector => {
        const elementos = document.querySelectorAll(selector);
        elementos.forEach(elemento => {
            // Configurar estado inicial
            elemento.style.opacity = '0';
            elemento.style.transform = 'translateY(30px)';
            elemento.style.transition = 'all 0.6s ease';
            
            // Observar elemento
            observer.observe(elemento);
        });
    });
    
    console.log('✨ Animaciones de contacto configuradas');
}

// ========================================
// FUNCIONES DE UTILIDAD ESPECÍFICAS
// ========================================
function copiarDireccion() {
    const direccion = window.TechSolvers.configuracion.empresa.direccion;
    
    if (navigator.clipboard) {
        navigator.clipboard.writeText(direccion).then(() => {
            window.TechSolvers.mostrarNotificacion('Dirección copiada al portapapeles', 'success');
        });
    } else {
        // Fallback para navegadores antiguos
        const textArea = document.createElement('textarea');
        textArea.value = direccion;
        document.body.appendChild(textArea);
        textArea.select();
        document.execCommand('copy');
        document.body.removeChild(textArea);
        window.TechSolvers.mostrarNotificacion('Dirección copiada al portapapeles', 'success');
    }
}

function llamarTelefono() {
    const telefono = window.TechSolvers.configuracion.empresa.telefono;
    window.location.href = `tel:${telefono}`;
    window.TechSolvers.mostrarNotificacion('Realizando llamada...', 'info', 1500);
}

// ========================================
// EVENTOS ESPECÍFICOS DE CONTACTO
// ========================================

// Hacer clic en la información de ubicación para copiar
document.addEventListener('click', function(event) {
    if (event.target.closest('.info-ubicacion p:first-child')) {
        copiarDireccion();
    }
});

// Hacer clic en el teléfono para llamar
document.addEventListener('click', function(event) {
    if (event.target.textContent.includes(window.TechSolvers.configuracion.empresa.telefono)) {
        event.preventDefault();
        llamarTelefono();
    }
});

console.log('📞 Contacto JS completamente configurado');