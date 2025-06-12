// ========================================
// JAVASCRIPT ESPECÍFICO - PÁGINA NOSOTROS
// Funcionalidades exclusivas de la página nosotros
// ========================================

// CONFIGURACIÓN ESPECÍFICA DE NOSOTROS
const CONFIG_NOSOTROS = {
    animacionDelay: 200,
    estadisticasInterval: 5000,
    scrollOffset: 100
};

// ESTADO DE LA PÁGINA
let estadoNosotros = {
    animacionesActivadas: false,
    contadoresAnimados: false,
    seccionVisible: null
};

// ========================================
// INICIALIZACIÓN PRINCIPAL
// ========================================
document.addEventListener('DOMContentLoaded', function() {
    console.log('🏢 Nosotros JS cargado');
    
    inicializarPaginaNosotros();
    
    console.log('✅ Página Nosotros completamente inicializada');
});

function inicializarPaginaNosotros() {
    try {
        // Inicializar animaciones
        inicializarAnimaciones();
        
        // Configurar scroll spy
        configurarScrollSpy();
        
        // Animar elementos visibles
        animarElementosVisibles();
        
        // Inicializar contadores
        inicializarContadores();
        
        // Configurar timeline interactivo
        configurarTimeline();
        
        // Inicializar efectos de hover
        inicializarEfectosHover();
        
        // Configurar smooth scroll
        configurarSmoothScroll();
        
        console.log('🎯 Página nosotros completamente configurada');
        
    } catch (error) {
        console.error('❌ Error al inicializar página nosotros:', error);
    }
}

// ========================================
// SISTEMA DE ANIMACIONES
// ========================================
function inicializarAnimaciones() {
    // Intersection Observer para animar elementos cuando entran en vista
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animate-in');
                
                // Animar contadores si es la sección de estadísticas
                if (entry.target.classList.contains('hero-stats') && !estadoNosotros.contadoresAnimados) {
                    animarContadores();
                    estadoNosotros.contadoresAnimados = true;
                }
            }
        });
    }, {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    });

    // Observar elementos animables
    const elementosAnimables = document.querySelectorAll(`
        .mision-card, .vision-card, .valor-card, .timeline-item,
        .tech-feature, .plan-card, .miembro-card, .hero-stats
    `);

    elementosAnimables.forEach(elemento => {
        observer.observe(elemento);
    });
}

function animarElementosVisibles() {
    const elementos = document.querySelectorAll('.hero-text, .hero-image');
    
    elementos.forEach((elemento, index) => {
        setTimeout(() => {
            elemento.style.opacity = '1';
            elemento.style.transform = 'translateY(0)';
        }, index * CONFIG_NOSOTROS.animacionDelay);
    });
}

// ========================================
// CONTADORES ANIMADOS
// ========================================
function inicializarContadores() {
    // Preparar elementos para animación
    const contadores = document.querySelectorAll('.stat-number');
    
    contadores.forEach(contador => {
        contador.style.opacity = '0';
        contador.style.transform = 'translateY(20px)';
    });
}

function animarContadores() {
    const contadores = [
        { elemento: document.querySelectorAll('.stat-number')[0], valor: 5, sufijo: '+' },
        { elemento: document.querySelectorAll('.stat-number')[1], valor: 1000, sufijo: '+' },
        { elemento: document.querySelectorAll('.stat-number')[2], valor: 500, sufijo: '+' },
        { elemento: document.querySelectorAll('.stat-number')[3], valor: 24, sufijo: '/7' }
    ];

    contadores.forEach((contador, index) => {
        if (contador.elemento) {
            setTimeout(() => {
                contador.elemento.style.opacity = '1';
                contador.elemento.style.transform = 'translateY(0)';
                contador.elemento.style.transition = 'all 0.6s ease';
                
                animarNumero(contador.elemento, contador.valor, contador.sufijo);
            }, index * 200);
        }
    });
}

function animarNumero(elemento, valorFinal, sufijo = '') {
    let valorInicial = 0;
    const duracion = 2000;
    const incremento = valorFinal / (duracion / 16);
    
    const timer = setInterval(() => {
        valorInicial += incremento;
        
        if (valorInicial >= valorFinal) {
            valorInicial = valorFinal;
            clearInterval(timer);
        }
        
        elemento.textContent = Math.floor(valorInicial) + sufijo;
    }, 16);
}

// ========================================
// SCROLL SPY PARA NAVEGACIÓN
// ========================================
function configurarScrollSpy() {
    const secciones = document.querySelectorAll('section[class*="section"]');
    
    const observerSpy = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                estadoNosotros.seccionVisible = entry.target.className;
                actualizarNavegacion(entry.target);
            }
        });
    }, {
        threshold: 0.3
    });

    secciones.forEach(seccion => {
        observerSpy.observe(seccion);
    });
}

function actualizarNavegacion(seccionActiva) {
    // Aquí se puede agregar lógica para actualizar navegación si es necesario
    console.log('📍 Sección visible:', seccionActiva.className);
}

// ========================================
// TIMELINE INTERACTIVO
// ========================================
function configurarTimeline() {
    const timelineItems = document.querySelectorAll('.timeline-item');
    
    timelineItems.forEach((item, index) => {
        // Agregar efecto de aparición progresiva
        item.style.opacity = '0';
        item.style.transform = 'translateX(-30px)';
        
        // Observar cuando entre en vista
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    setTimeout(() => {
                        entry.target.style.opacity = '1';
                        entry.target.style.transform = 'translateX(0)';
                        entry.target.style.transition = 'all 0.8s ease';
                    }, index * 300);
                }
            });
        });
        
        observer.observe(item);
        
        // Agregar evento de hover
        item.addEventListener('mouseenter', function() {
            this.style.transform = 'translateX(0) scale(1.02)';
        });
        
        item.addEventListener('mouseleave', function() {
            this.style.transform = 'translateX(0) scale(1)';
        });
    });
}

// ========================================
// EFECTOS DE HOVER MEJORADOS
// ========================================
function inicializarEfectosHover() {
    // Efecto paralaje en hero
    inicializarEfectoParalaje();
    
    // Efectos en tarjetas
    inicializarEfectosTarjetas();
    
    // Efectos en botones
    inicializarEfectosBotones();
}

function inicializarEfectoParalaje() {
    const heroSection = document.querySelector('.hero-nosotros');
    
    if (heroSection) {
        window.addEventListener('scroll', () => {
            const scrolled = window.pageYOffset;
            const parallaxSpeed = 0.5;
            
            heroSection.style.transform = `translateY(${scrolled * parallaxSpeed}px)`;
        });
    }
}

function inicializarEfectosTarjetas() {
    const tarjetas = document.querySelectorAll(`
        .valor-card, .tech-feature, .plan-card, .miembro-card,
        .mision-card, .vision-card, .sector-card, .legal-card
    `);

    tarjetas.forEach(tarjeta => {
        tarjeta.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-8px) scale(1.02)';
        });
        
        tarjeta.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0) scale(1)';
        });
    });
}

function inicializarEfectosBotones() {
    const botones = document.querySelectorAll('.btn-primary, .btn-secondary, .btn-whatsapp, .btn-contacto');
    
    botones.forEach(boton => {
        // Efecto ripple al hacer clic
        boton.addEventListener('click', function(e) {
            crearEfectoRipple(e, this);
        });
    });
}

function crearEfectoRipple(event, elemento) {
    const ripple = document.createElement('span');
    const rect = elemento.getBoundingClientRect();
    const size = Math.max(rect.width, rect.height);
    const x = event.clientX - rect.left - size / 2;
    const y = event.clientY - rect.top - size / 2;
    
    ripple.style.cssText = `
        position: absolute;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.3);
        transform: scale(0);
        animation: ripple 0.6s linear;
        left: ${x}px;
        top: ${y}px;
        width: ${size}px;
        height: ${size}px;
        pointer-events: none;
    `;
    
    elemento.style.position = 'relative';
    elemento.style.overflow = 'hidden';
    elemento.appendChild(ripple);
    
    setTimeout(() => {
        ripple.remove();
    }, 600);
}

// ========================================
// SMOOTH SCROLL
// ========================================
function configurarSmoothScroll() {
    const enlaces = document.querySelectorAll('a[href^="#"]');
    
    enlaces.forEach(enlace => {
        enlace.addEventListener('click', function(e) {
            e.preventDefault();
            
            const targetId = this.getAttribute('href').substring(1);
            const targetElement = document.getElementById(targetId);
            
            if (targetElement) {
                const offsetTop = targetElement.offsetTop - CONFIG_NOSOTROS.scrollOffset;
                
                window.scrollTo({
                    top: offsetTop,
                    behavior: 'smooth'
                });
            }
        });
    });
}

// ========================================
// FUNCIONES DE UTILIDAD
// ========================================
function mostrarSeccion(seccionId) {
    const seccion = document.getElementById(seccionId);
    
    if (seccion) {
        const offsetTop = seccion.offsetTop - CONFIG_NOSOTROS.scrollOffset;
        
        window.scrollTo({
            top: offsetTop,
            behavior: 'smooth'
        });
    }
}

function alternarVisibilidad(elementoId) {
    const elemento = document.getElementById(elementoId);
    
    if (elemento) {
        elemento.style.display = elemento.style.display === 'none' ? 'block' : 'none';
    }
}

// ========================================
// EVENTOS ESPECIALES
// ========================================
function inicializarEventosEspeciales() {
    // Evento para cambio de tamaño de ventana
    window.addEventListener('resize', function() {
        // Reajustar animaciones si es necesario
        console.log('📱 Ventana redimensionada');
    });
    
    // Evento para cuando la página se oculta/muestra
    document.addEventListener('visibilitychange', function() {
        if (document.hidden) {
            // Pausar animaciones si la página está oculta
            console.log('⏸️ Página oculta - pausando animaciones');
        } else {
            // Reanudar animaciones
            console.log('▶️ Página visible - reanudando animaciones');
        }
    });
}

// ========================================
// FUNCIONES DE CONTACTO
// ========================================
function abrirWhatsApp() {
    const numero = '51123456789';
    const mensaje = encodeURIComponent('Hola, me interesa conocer más sobre TechSolvers y sus servicios.');
    const url = `https://wa.me/${numero}?text=${mensaje}`;
    
    window.open(url, '_blank');
}

function irAFormularioContacto() {
    window.location.href = 'contacto.html';
}

function irAProductos() {
    window.location.href = 'productos.html';
}

function irAServicios() {
    window.location.href = 'servicio-tecnico.html';
}

// ========================================
// ESTER EGGS Y FUNCIONES ESPECIALES
// ========================================
function inicializarEasterEggs() {
    let clicsEnLogo = 0;
    const logo = document.querySelector('.empresa-logo');
    
    if (logo) {
        logo.addEventListener('click', function() {
            clicsEnLogo++;
            
            if (clicsEnLogo === 5) {
                mostrarMensajeEspecial();
                clicsEnLogo = 0;
            }
        });
    }
}

function mostrarMensajeEspecial() {
    window.TechSolvers.mostrarNotificacion(
        '🎉 ¡Gracias por conocer nuestra historia! Eres especial para nosotros.',
        'success',
        5000
    );
}

// ========================================
// CSS DINÁMICO PARA ANIMACIONES
// ========================================
function inyectarEstilosAnimacion() {
    const estilos = `
        <style>
            @keyframes ripple {
                to {
                    transform: scale(4);
                    opacity: 0;
                }
            }
            
            .animate-in {
                opacity: 1 !important;
                transform: translateY(0) !important;
                transition: all 0.8s ease;
            }
            
            .hero-text, .hero-image {
                opacity: 0;
                transform: translateY(30px);
                transition: all 0.8s ease;
            }
            
            .parallax-bg {
                transition: transform 0.1s ease-out;
            }
        </style>
    `;
    
    document.head.insertAdjacentHTML('beforeend', estilos);
}

// ========================================
// INICIALIZACIÓN FINAL
// ========================================
function completarInicializacion() {
    // Inyectar estilos de animación
    inyectarEstilosAnimacion();
    
    // Inicializar eventos especiales
    inicializarEventosEspeciales();
    
    // Inicializar easter eggs
    inicializarEasterEggs();
    
    // Marcar como inicializado
    estadoNosotros.animacionesActivadas = true;
    
    console.log('🎨 Todas las animaciones y efectos configurados');
}

// Ejecutar inicialización final después de un breve delay
setTimeout(completarInicializacion, 1000);

// ========================================
// FUNCIONES PÚBLICAS PARA EL HTML
// ========================================
window.NosotrosFunctions = {
    mostrarSeccion,
    alternarVisibilidad,
    abrirWhatsApp,
    irAFormularioContacto,
    irAProductos,
    irAServicios,
    animarContadores,
    mostrarMensajeEspecial
};

// Hacer funciones globalmente accesibles
Object.assign(window, window.NosotrosFunctions);

console.log('🏢 Nosotros JS completamente configurado');

// ========================================
// MANEJO DE ERRORES GLOBAL
// ========================================
window.addEventListener('error', function(event) {
    console.error('❌ Error en página nosotros:', event.error);
});

// ========================================
// OPTIMIZACIÓN DE RENDIMIENTO
// ========================================
function optimizarRendimiento() {
    // Lazy loading para imágenes si hay muchas
    const imagenes = document.querySelectorAll('img[data-src]');
    
    if ('IntersectionObserver' in window) {
        const imageObserver = new IntersectionObserver((entries, observer) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const img = entry.target;
                    img.src = img.dataset.src;
                    img.classList.remove('lazy');
                    imageObserver.unobserve(img);
                }
            });
        });

        imagenes.forEach(img => imageObserver.observe(img));
    }
}

// Ejecutar optimizaciones
optimizarRendimiento();