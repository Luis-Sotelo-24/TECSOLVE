<%-- 
    Document   : contacto
    Created on : 15 may. 2025, 2:07:33 a. m.
    Author     : Guillermo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Contacto - TechSolvers</title>
  <style>
    * {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
    }

    body {
      font-family: 'Segoe UI', sans-serif;
      background-color: #f6f6f6;
      color: #333;
    }

    main {
      padding: 40px 20px;
    }

    /* Fila principal que contiene imagen y ubicación+horario */
    .fila-contacto {
      display: flex;
      flex-wrap: wrap;
      gap: 40px;
      justify-content: center;
      align-items: flex-start;
    }

    /* Columna izquierda: Tienda Física */
    .tienda-fisica {
      flex: 1;
      max-width: 500px;
    }

    .tienda-fisica h2 {
      font-size: 2rem;
      margin-bottom: 20px;
      text-align: left;
    }

    .imagen-vertical {
      width: 100%;
      height: 600px;
      object-fit: cover;
      border-radius: 15px;
    }

    /* Columna derecha: Ubicación y horarios uno debajo del otro */
    .ubicacion-y-horarios {
      flex: 1;
      max-width: 800px;
      display: flex;
      flex-direction: column;
      gap: 30px;
    }

    .mapa, .horarios {
      width: 100%;
    }

    .mapa h3, .horarios h3 {
      font-size: 1.5rem;
      margin-bottom: 10px;
      text-align: left;
    }

    .mapa iframe {
      width: 100%;
      height: 450px;
      border: none;
      border-radius: 10px;
    }

    .horarios {
      background-color: white;
      padding: 20px;
      border-radius: 10px;
      box-shadow: 0 4px 8px rgba(0,0,0,0.1);
    }

    .horario p {
      margin-bottom: 5px;
      font-size: 1.1rem;
      text-align: left;
    }

    /* Header */
    header {
      background-color: #222;
      color: white;
      padding: 15px 30px;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .logo {
      font-size: 1.5rem;
      font-weight: bold;
    }

    nav ul {
      list-style: none;
      display: flex;
      gap: 20px;
    }

    nav ul li a {
      color: white;
      text-decoration: none;
      font-weight: 500;
    }

    /* Footer */
    footer {
      background-color: #000;
      color: white;
      padding: 20px;
      font-family: Arial, sans-serif;
    }

    .footer-container {
      display: flex;
      justify-content: space-between;
      flex-wrap: wrap;
    }

    .footer-col {
      flex: 1;
      min-width: 200px;
      margin: 10px;
    }

    .footer-col h4 {
      margin-bottom: 10px;
      font-size: 18px;
    }

    .footer-col ul {
      list-style: none;
      padding: 0;
    }

    .footer-col li {
      margin-bottom: 8px;
      display: flex;
      align-items: center;
    }

    .logo-footer {
      width: 120px;
      height: auto;
      border-radius: 15px;
    }

    .icono-whatsapp {
      width: 20px;
      height: 20px;
      margin-right: 8px;
    }

    .footer-copy {
      text-align: center;
      margin-top: 30px;
      font-size: 14px;
      padding-top: 10px;
    }
  </style>
</head>
<body>

  <!-- Header -->
  <header>
    <div class="logo">🔧 TechSolvers</div>
    <nav>
      <ul>
        <li><a href="${pageContext.request.contextPath}/vistas/index.jsp">Inicio</a></li>
        <li><a href="#">Productos</a></li>
        <li><a href="#">Nosotros</a></li>
        <li><a href="${pageContext.request.contextPath}/vistas/contacto.jsp">Contacto</a></li>
        <li><a href="${pageContext.request.contextPath}/vistas/logout.jsp">Cerrar Sesion</a></li>
      </ul>
    </nav>
  </header>

  <!-- Cuerpo principal -->
  <main>

    <!-- Fila que contiene imagen + ubicación y horarios -->
    <section class="fila-contacto">

      <!-- Izquierda: Tienda Física -->
      <div class="tienda-fisica">
        <h2>Tienda Física</h2>
        <img src="../imagenes/imagencomprador.jpg" alt="Tienda Física" class="imagen-vertical" />
      </div>

      <!-- Derecha: Ubicación y horarios debajo -->
      <div class="ubicacion-y-horarios">

        <!-- Mapa -->
        <div class="mapa">
          <h3>Ubicación</h3>
          <iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d8032.782633660827!2d-77.04489146788436!3d-12.039941454971778!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x9105c99dc7c26d85%3A0x84aaa88abddae7ef!2sTECHSOLVERS-Audio%20y%20Tecnologia!5e0!3m2!1ses-419!2spe!4v1746912234941!5m2!1ses-419!2spe" allowfullscreen="" loading="lazy"></iframe>
        </div>

        <!-- Horarios -->
        <div class="horarios">
          <h3>Horario de Atención</h3>
          <div class="horario">
            <p>Lunes: 10:00 am - 7:00 pm</p>
            <p>Martes: 10:00 am - 7:00 pm</p>
            <p>Miércoles: 10:00 am - 7:00 pm</p>
            <p>Jueves: 10:00 am - 7:00 pm</p>
            <p>Viernes: 10:00 am - 7:00 pm</p>
            <p>Sábado: 10:00 am - 7:00 pm</p>
            <p>Domingo: 10:00 am - 7:00 pm</p>
          </div>
        </div>

      </div>
    </section>

  </main>

  <!-- Footer -->
  <footer>
    <div class="footer-container">
      <div class="footer-col">
        <img src="../imagenes/logotechsolvers.jpg" alt="Logo TechSolvers" class="logo-footer">
      </div>
      <div class="footer-col">
        <h4>Envíos y garantías</h4>
        <ul>
          <li>¿Cómo comprar?</li>
          <li>Envíos</li>
          <li>Términos y condiciones de Garantía</li>
        </ul>
      </div>
      <div class="footer-col">
        <h4>¿Quiénes somos?</h4>
        <ul>
          <li>Nuestra tienda</li>
          <li>Privacidad</li>
        </ul>
      </div>
      <div class="footer-col">
        <h4>Contacto</h4>
        <ul>
          <li><img src="../imagenes/logoWhassap.png" alt="WhatsApp" class="icono-whatsapp">Asesor de ventas</li>
          <li><img src="../imagenes/logoWhassap.png" alt="WhatsApp" class="icono-whatsapp">Área de garantía</li>
        </ul>
      </div>
    </div>
    <div class="footer-copy">
      &copy; TechSolvers 2025
    </div>
  </footer>

</body>
</html>
