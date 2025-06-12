// ========================================
// JAVASCRIPT ESPECÍFICO - PÁGINA FAQ
// Funcionalidades exclusivas de preguntas frecuentes
// ========================================

// CONFIGURACIÓN ESPECÍFICA DEL FAQ
const CONFIG_FAQ = {
    animacionDelay: 300,
    busquedaDelay: 300,
    resaltadoDuracion: 3000,
    scrollOffset: 80
};

// ESTADO DE LA PÁGINA FAQ
let estadoFAQ = {
    categoriaActiva: 'todas',
    preguntasAbiertas: new Set(),
    terminoBusqueda: '',
    preguntasFiltradas: [],
    contadores: {
        todas: 0,
        compras: 0,
        envios: 0,
        garantias: 0,
        servicios: 0,
        pagos: 0,
        cuenta: 0
    }
};

// DEBOUNCE PARA BÚSQUEDA
let timeoutBusqueda = null;

// ========================================
// INICIALIZACIÓN PRINCIPAL
// ========================================
document.addEventListener('DOMContentLoaded', function() {
    console.log('❓ FAQ JS cargado');
    
    inicializarPaginaFAQ();
    
    console.log('✅ FAQ completamente inicializado');
});

function inicializarPaginaFAQ() {
    try {
        // Contar preguntas por categoría
        contarPreguntasPorCategoria();
        
        // Configurar eventos
        configurarEventosFAQ();
        
        // Inicializar búsqueda
        inicializarBuscadorFAQ();
        
        // Configurar acordeón
        configurarAcordeon();
        
        // Verificar parámetros URL
        verificarParametrosURL();
        
        // Inicializar animaciones
        inicializarAnimacionesFAQ();
        
        // Configurar scroll suave
        configurarScrollSuave();
        
        console.log('🎯 FAQ completamente configurado');
        
    } catch (error) {
        console.error('❌ Error al inicializar FAQ:', error);
    }
}

// ========================================
// CONTEO DE PREGUNTAS POR CATEGORÍA
// ========================================
function contarPreguntasPorCategoria() {
    const categorias = ['compras', 'envios', 'garantias', 'servicios', 'pagos', 'cuenta'];
    let total = 0;
    
    categorias.forEach(categoria => {
        const preguntas = document.querySelectorAll(`[data-categoria="${categoria}"] .faq-item`);
        const cantidad = preguntas.length;
        
        estadoFAQ.contadores[categoria] = cantidad;
        total += cantidad;
        
        // Actualizar contador en la interfaz
        const contador = document.getElementById(`contador-${categoria}`);
        if (contador) {
            contador.textContent = cantidad;
        }
    });
    
    estadoFAQ.contadores.todas = total;
    
    const contadorTodas = document.getElementById('contador-todas');
    if (contadorTodas) {
        contadorTodas.textContent = total;
    }
    
    console.log('📊 Contadores de preguntas:', estadoFAQ.contadores);
}

// ========================================
// SISTEMA DE FILTRADO POR CATEGORÍA
// ========================================
function filtrarPorCategoria(categoria) {
    console.log('🗂️ Filtrando por categoría:', categoria);
    
    // Actualizar estado
    estadoFAQ.categoriaActiva = categoria;
    
    // Actualizar botones de categoría
    actualizarBotonesCategorias(categoria);
    
    // Mostrar/ocultar categorías
    mostrarCategorias(categoria);
    
    // Limpiar búsqueda si hay alguna activa
    limpiarBusqueda();
    
    // Animación de entrada
    animarCambioCategoria();
    
    // Actualizar URL
    actualizarURL({ categoria });
}

function actualizarBotonesCategorias(categoriaActiva) {
    const botones = document.querySelectorAll('.categoria-btn');
    
    botones.forEach(boton => {
        const categoria = boton.getAttribute('data-categoria');
        
        if (categoria === categoriaActiva) {
            boton.classList.add('active');
            boton.classList.add('pulsando');
            
            // Remover animación después de completarse
            setTimeout(() => {
                boton.classList.remove('pulsando');
            }, 600);
        } else {
            boton.classList.remove('active');
        }
    });
}

function mostrarCategorias(categoriaSeleccionada) {
    const todasLasCategorias = document.querySelectorAll('.faq-categoria');
    
    todasLasCategorias.forEach((categoria, index) => {
        const categoriaDatos = categoria.getAttribute('data-categoria');
        
        if (categoriaSeleccionada === 'todas' || categoriaDatos === categoriaSeleccionada) {
            // Mostrar con animación
            setTimeout(() => {
                categoria.classList.remove('oculta');
                categoria.style.display = 'block';
            }, index * 100);
        } else {
            // Ocultar
            categoria.classList.add('oculta');
            setTimeout(() => {
                categoria.style.display = 'none';
            }, 300);
        }
    });
    
    // Ocultar mensaje de "no resultados"
    document.getElementById('no-resultados').style.display = 'none';
}

// ========================================
// SISTEMA DE BÚSQUEDA
// ========================================
function inicializarBuscadorFAQ() {
    const inputBusqueda = document.getElementById('search-faq');
    
    if (inputBusqueda) {
        inputBusqueda.addEventListener('input', function() {
            const termino = this.value.trim();
            
            // Limpiar timeout anterior
            if (timeoutBusqueda) {
                clearTimeout(timeoutBusqueda);
            }
            
            // Buscar con delay
            timeoutBusqueda = setTimeout(() => {
                realizarBusqueda(termino);
            }, CONFIG_FAQ.busquedaDelay);
        });
        
        // Buscar al presionar Enter
        inputBusqueda.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                
                if (timeoutBusqueda) {
                    clearTimeout(timeoutBusqueda);
                }
                
                realizarBusqueda(this.value.trim());
            }
        });
    }
}

function realizarBusqueda(termino) {
    console.log('🔍 Buscando:', termino);
    
    estadoFAQ.terminoBusqueda = termino;
    
    if (!termino) {
        // Si no hay término, mostrar todas las categorías
        mostrarTodasLasPreguntas();
        return;
    }
    
    // Buscar en preguntas y respuestas
    const resultados = buscarEnPreguntas(termino);
    
    if (resultados.length === 0) {
        mostrarSinResultados();
    } else {
        mostrarResultadosBusqueda(resultados, termino);
    }
}

function buscarEnPreguntas(termino) {
    const terminoLower = termino.toLowerCase();
    const resultados = [];
    
    // Buscar en todas las preguntas
    const todasLasPreguntas = document.querySelectorAll('.faq-item');
    
    todasLasPreguntas.forEach(pregunta => {
        const textoPregunta = pregunta.querySelector('.faq-pregunta span:first-child').textContent.toLowerCase();
        const textoRespuesta = pregunta.querySelector('.faq-respuesta').textContent.toLowerCase();
        
        if (textoPregunta.includes(terminoLower) || textoRespuesta.includes(terminoLower)) {
            resultados.push({
                elemento: pregunta,
                categoria: pregunta.closest('.faq-categoria').getAttribute('data-categoria'),
                relevancia: calcularRelevancia(terminoLower, textoPregunta, textoRespuesta)
            });
        }
    });
    
    // Ordenar por relevancia
    resultados.sort((a, b) => b.relevancia - a.relevancia);
    
    return resultados;
}

function calcularRelevancia(termino, pregunta, respuesta) {
    let puntuacion = 0;
    
    // Mayor puntuación si aparece en la pregunta
    if (pregunta.includes(termino)) {
        puntuacion += 10;
    }
    
    // Puntuación por apariciones en la respuesta
    const aparicionesRespuesta = (respuesta.match(new RegExp(termino, 'g')) || []).length;
    puntuacion += aparicionesRespuesta * 2;
    
    // Bonificación si aparece al inicio de la pregunta
    if (pregunta.startsWith(termino)) {
        puntuacion += 5;
    }
    
    return puntuacion;
}

function mostrarResultadosBusqueda(resultados, termino) {
    // Ocultar todas las categorías
    const todasLasCategorias = document.querySelectorAll('.faq-categoria');
    todasLasCategorias.forEach(cat => cat.style.display = 'none');
    
    // Mostrar solo las categorías que tienen resultados
    const categoriasConResultados = new Set();
    
    resultados.forEach(resultado => {
        categoriasConResultados.add(resultado.categoria);
        
        // Mostrar la pregunta
        resultado.elemento.style.display = 'block';
        
        // Resaltar término buscado
        resaltarTermino(resultado.elemento, termino);
    });
    
    // Mostrar categorías con resultados
    categoriasConResultados.forEach(categoria => {
        const elementoCategoria = document.querySelector(`[data-categoria="${categoria}"]`);
        if (elementoCategoria) {
            elementoCategoria.style.display = 'block';
            
            // Ocultar preguntas que no coinciden
            const preguntasCategoria = elementoCategoria.querySelectorAll('.faq-item');
            preguntasCategoria.forEach(pregunta => {
                const esResultado = resultados.some(r => r.elemento === pregunta);
                pregunta.style.display = esResultado ? 'block' : 'none';
            });
        }
    });
    
    // Actualizar categoría activa
    estadoFAQ.categoriaActiva = 'busqueda';
    actualizarBotonesCategorias('');
    
    // Ocultar mensaje de sin resultados
    document.getElementById('no-resultados').style.display = 'none';
    
    console.log(`✅ Encontrados ${resultados.length} resultados para "${termino}"`);
}

function resaltarTermino(elemento, termino) {
    if (!termino) return;
    
    const preguntaTexto = elemento.querySelector('.faq-pregunta span:first-child');
    const respuestaTexto = elemento.querySelector('.faq-respuesta');
    
    // Remover resaltados anteriores
    preguntaTexto.innerHTML = preguntaTexto.textContent;
    respuestaTexto.innerHTML = respuestaTexto.textContent;
    
    // Crear regex para resaltar (ignorar mayúsculas)
    const regex = new RegExp(`(${termino})`, 'gi');
    
    // Resaltar en pregunta
    preguntaTexto.innerHTML = preguntaTexto.textContent.replace(regex, '<mark style="background-color: #fff3cd; padding: 2px 4px; border-radius: 3px;">$1</mark>');
    
    // Resaltar en respuesta
    respuestaTexto.innerHTML = respuestaTexto.textContent.replace(regex, '<mark style="background-color: #fff3cd; padding: 2px 4px; border-radius: 3px;">$1</mark>');
}

function mostrarSinResultados() {
    // Ocultar todas las categorías
    const todasLasCategorias = document.querySelectorAll('.faq-categoria');
    todasLasCategorias.forEach(cat => cat.style.display = 'none');
    
    // Mostrar mensaje de sin resultados
    document.getElementById('no-resultados').style.display = 'block';
    
    // Scroll al mensaje
    document.getElementById('no-resultados').scrollIntoView({
        behavior: 'smooth',
        block: 'center'
    });
}

function mostrarTodasLasPreguntas() {
    // Remover resaltados
    const elementosResaltados = document.querySelectorAll('mark');
    elementosResaltados.forEach(el => {
        el.outerHTML = el.textContent;
    });
    
    // Mostrar todas las preguntas
    const todasLasPreguntas = document.querySelectorAll('.faq-item');
    todasLasPreguntas.forEach(pregunta => {
        pregunta.style.display = 'block';
    });
    
    // Mostrar categorías según filtro actual
    mostrarCategorias(estadoFAQ.categoriaActiva);
}

function limpiarBusqueda() {
    const inputBusqueda = document.getElementById('search-faq');
    if (inputBusqueda) {
        inputBusqueda.value = '';
    }
    
    estadoFAQ.terminoBusqueda = '';
    mostrarTodasLasPreguntas();
}

// ========================================
// SISTEMA DE ACORDEÓN
// ========================================
function configurarAcordeon() {
    const preguntasFAQ = document.querySelectorAll('.faq-item');
    
    preguntasFAQ.forEach(item => {
        const pregunta = item.querySelector('.faq-pregunta');
        
        pregunta.addEventListener('click', function() {
            toggleFAQ(item);
        });
    });
}

function toggleFAQ(item) {
    const isActivo = item.classList.contains('activo');
    const preguntaId = item.getAttribute('data-pregunta-id') || Date.now().toString();
    
    if (isActivo) {
        // Cerrar
        item.classList.remove('activo');
        estadoFAQ.preguntasAbiertas.delete(preguntaId);
    } else {
        // Abrir
        item.classList.add('activo');
        estadoFAQ.preguntasAbiertas.add(preguntaId);
        
        // Scroll suave a la pregunta
        setTimeout(() => {
            item.scrollIntoView({
                behavior: 'smooth',
                block: 'nearest'
            });
        }, CONFIG_FAQ.animacionDelay);
    }
    
    // Actualizar URL con preguntas abiertas
    actualizarURL({
        categoria: estadoFAQ.categoriaActiva,
        pregunta: preguntaId
    });
    
    console.log('🎯 FAQ toggled:', preguntaId, isActivo ? 'cerrado' : 'abierto');
}

// ========================================
// GESTIÓN DE URL Y NAVEGACIÓN
// ========================================
function verificarParametrosURL() {
    const urlParams = new URLSearchParams(window.location.search);
    
    // Verificar categoría
    const categoria = urlParams.get('categoria');
    if (categoria && categoria !== 'todas') {
        filtrarPorCategoria(categoria);
    }
    
    // Verificar búsqueda
    const busqueda = urlParams.get('buscar');
    if (busqueda) {
        const inputBusqueda = document.getElementById('search-faq');
        if (inputBusqueda) {
            inputBusqueda.value = busqueda;
            realizarBusqueda(busqueda);
        }
    }
    
    // Verificar pregunta específica
    const pregunta = urlParams.get('pregunta');
    if (pregunta) {
        setTimeout(() => {
            abrirPreguntaEspecifica(pregunta);
        }, 1000);
    }
}

function actualizarURL(params) {
    const url = new URL(window.location);
    
    if (params.categoria && params.categoria !== 'todas') {
        url.searchParams.set('categoria', params.categoria);
    } else {
        url.searchParams.delete('categoria');
    }
    
    if (params.busqueda) {
        url.searchParams.set('buscar', params.busqueda);
    } else {
        url.searchParams.delete('buscar');
    }
    
    if (params.pregunta) {
        url.searchParams.set('pregunta', params.pregunta);
    } else {
        url.searchParams.delete('pregunta');
    }
    
    window.history.replaceState({}, '', url);
}

function abrirPreguntaEspecifica(preguntaId) {
    const pregunta = document.querySelector(`[data-pregunta-id="${preguntaId}"]`);
    
    if (pregunta) {
        toggleFAQ(pregunta);
        
        // Resaltar temporalmente
        pregunta.classList.add('resaltado');
        setTimeout(() => {
            pregunta.classList.remove('resaltado');
        }, CONFIG_FAQ.resaltadoDuracion);
    }
}

// ========================================
// EVENTOS Y CONFIGURACIONES
// ========================================
function configurarEventosFAQ() {
    // Teclas de navegación
    document.addEventListener('keydown', function(e) {
        // Escape para cerrar todas las preguntas
        if (e.key === 'Escape') {
            cerrarTodasLasPreguntas();
        }
        
        // Ctrl + F para enfocar búsqueda
        if (e.ctrlKey && e.key === 'f') {
            e.preventDefault();
            const inputBusqueda = document.getElementById('search-faq');
            if (inputBusqueda) {
                inputBusqueda.focus();
                inputBusqueda.select();
            }
        }
    });
    
    // Click fuera para cerrar preguntas abiertas
    document.addEventListener('click', function(e) {
        if (!e.target.closest('.faq-item') && !e.target.closest('.categoria-btn')) {
            // No cerrar automáticamente - puede ser molesto
        }
    });
}

function cerrarTodasLasPreguntas() {
    const preguntasAbiertas = document.querySelectorAll('.faq-item.activo');
    
    preguntasAbiertas.forEach(pregunta => {
        pregunta.classList.remove('activo');
    });
    
    estadoFAQ.preguntasAbiertas.clear();
    
    window.TechSolvers.mostrarNotificacion('Todas las preguntas cerradas', 'info', 2000);
}

// ========================================
// ANIMACIONES Y EFECTOS
// ========================================
function inicializarAnimacionesFAQ() {
    // Animación de entrada para las categorías
    const categoriasBtn = document.querySelectorAll('.categoria-btn');
    
    categoriasBtn.forEach((btn, index) => {
        btn.style.opacity = '0';
        btn.style.transform = 'translateY(20px)';
        
        setTimeout(() => {
            btn.style.opacity = '1';
            btn.style.transform = 'translateY(0)';
            btn.style.transition = 'all 0.6s ease';
        }, index * 100);
    });
    
    // Observar elementos FAQ para animación de entrada
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    });
    
    const elementosFAQ = document.querySelectorAll('.faq-item');
    elementosFAQ.forEach(elemento => {
        elemento.style.opacity = '0';
        elemento.style.transform = 'translateY(20px)';
        elemento.style.transition = 'all 0.6s ease';
        observer.observe(elemento);
    });
}

function animarCambioCategoria() {
    const categoriasVisibles = document.querySelectorAll('.faq-categoria:not(.oculta)');
    
    categoriasVisibles.forEach((categoria, index) => {
        categoria.style.opacity = '0';
        categoria.style.transform = 'translateX(-20px)';
        
        setTimeout(() => {
            categoria.style.opacity = '1';
            categoria.style.transform = 'translateX(0)';
            categoria.style.transition = 'all 0.5s ease';
        }, index * 150);
    });
}

// ========================================
// SCROLL SUAVE
// ========================================
function configurarScrollSuave() {
    // Enlaces internos de navegación
    const enlacesInternos = document.querySelectorAll('a[href^="#"]');
    
    enlacesInternos.forEach(enlace => {
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
}

// ========================================
// FUNCIONES DE UTILIDAD
// ========================================
function buscarPreguntas() {
    const input = document.getElementById('search-faq');
    if (input) {
        realizarBusqueda(input.value.trim());
    }
}

function irAContacto() {
    window.location.href = 'contacto.html';
}

function abrirWhatsApp() {
    const mensaje = encodeURIComponent('Hola, tengo una pregunta que no está en el FAQ de TechSolvers.');
    window.open(`https://wa.me/51123456789?text=${mensaje}`, '_blank');
}

function enviarEmail() {
    const asunto = encodeURIComponent('Consulta desde FAQ - TechSolvers');
    const mensaje = encodeURIComponent('Hola, tengo una consulta que no encontré en las preguntas frecuentes...');
    window.open(`mailto:info@techsolvers.pe?subject=${asunto}&body=${mensaje}`, '_blank');
}

// ========================================
// FUNCIONES DE ANÁLISIS
// ========================================
function rastrearInteraccionFAQ(accion, categoria, pregunta = null) {
    // Aquí se puede integrar con Google Analytics o similar
    console.log('📊 Interacción FAQ:', {
        accion,
        categoria,
        pregunta,
        timestamp: new Date().toISOString()
    });
}

function obtenerEstadisticasFAQ() {
    return {
        categoriaActiva: estadoFAQ.categoriaActiva,
        preguntasAbiertas: Array.from(estadoFAQ.preguntasAbiertas),
        terminoBusqueda: estadoFAQ.terminoBusqueda,
        totalPreguntas: estadoFAQ.contadores.todas
    };
}

// ========================================
// FUNCIONES PÚBLICAS PARA EL HTML
// ========================================
window.FAQFunctions = {
    filtrarPorCategoria,
    toggleFAQ,
    buscarPreguntas,
    limpiarBusqueda,
    cerrarTodasLasPreguntas,
    irAContacto,
    abrirWhatsApp,
    enviarEmail,
    obtenerEstadisticasFAQ
};

// Hacer funciones globalmente accesibles
Object.assign(window, window.FAQFunctions);

console.log('❓ FAQ JS completamente configurado');

// ========================================
// EASTER EGGS Y FUNCIONES ESPECIALES
// ========================================
function inicializarEasterEggsFAQ() {
    let contadorBusquedasVacias = 0;
    
    const inputBusqueda = document.getElementById('search-faq');
    if (inputBusqueda) {
        inputBusqueda.addEventListener('keypress', function(e) {
            if (e.key === 'Enter' && !this.value.trim()) {
                contadorBusquedasVacias++;
                
                if (contadorBusquedasVacias === 3) {
                    window.TechSolvers.mostrarNotificacion(
                        '🤔 ¿Necesitas ayuda para buscar? ¡Escríbenos por WhatsApp!',
                        'info',
                        4000
                    );
                    contadorBusquedasVacias = 0;
                }
            }
        });
    }
}

// Inicializar easter eggs después de un delay
setTimeout(inicializarEasterEggsFAQ, 2000);