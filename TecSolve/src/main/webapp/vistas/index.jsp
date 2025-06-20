<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>TechSolvers</title>
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

    .banner {
      text-align: center;
      background: linear-gradient(to right, #0f2027, #203a43, #2c5364);
      color: white;
      padding: 60px 20px;
    }

    .productos {
      padding: 40px 20px;
      text-align: center;
    }

    .productos h2 {
      margin-bottom: 30px;
    }

    .producto {
      display: inline-block;
      background-color: white;
      padding: 20px;
      margin: 10px;
      border-radius: 10px;
      width: 200px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    .producto img {
      width: 100%;
      border-radius: 5px;
    }

    .producto h3 {
      margin: 10px 0 5px;
    }

    .producto p {
      color: #009578;
      font-weight: bold;
    }

    button {
      margin-top: 10px;
      padding: 10px;
      border: none;
      background-color: #009578;
      color: white;
      border-radius: 5px;
      cursor: pointer;
    }

    button:hover {
      background-color: #007a63;
    }

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

  <header>
    <div class="logo">🔧 TechSolvers</div>
    <nav>
      <ul>
        <li><a href="index.jsp">Inicio</a></li>
        <li><a href="#">Productos</a></li>
        <li><a href="#">Nosotros</a></li>
        <li><a href="${pageContext.request.contextPath}/vistas/contacto.jsp">Contacto</a></li>
        <li><a href="${pageContext.request.contextPath}/vistas/logout.jsp">Cerrar Sesion</a></li>
      </ul>
    </nav>
  </header>

  <section class="banner">
    <h1>Bienvenido a TechSolvers</h1>
    <p>¡Tu tienda confiable de electrónica y soluciones tecnológicas!</p>
  </section>

  <section class="productos">
    <h2>Nuestros Productos</h2>
    <div class="producto">
      <img src="${pageContext.request.contextPath}/imagenes/tarjetagrafica.jpg" alt="Producto 1" />
      <h3>Tarjeta grafica</h3>
      <p>S/400.00</p>
      <img src="${pageContext.request.contextPath}/imagenes/tarjetagrafica.jpg" alt="Mouse Gamer" />
      <h3>Laptop Lenovo</h3>
      <p>S/ 2200.00</p>
      <button>Agregar al carrito</button>
    </div>
    <div class="producto">
      <img src="${pageContext.request.contextPath}/imagenes/mousegamer.jpg" alt="Producto 2" />
      <h3>Mouse Gamer</h3>
      <p>S/ 150.00</p>
      <button>Agregar al carrito</button>
    </div>
  </section>

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
          <li>
            <img src="../imagenes/logoWhassap.png" alt="WhatsApp" class="icono-whatsapp">
            Asesor de ventas
          </li>
          <li>
            <img src="../imagenes/logoWhassap.png" alt="WhatsApp" class="icono-whatsapp">
            Área de garantía
          </li>
        </ul>
      </div>
    </div>

    <div class="footer-copy">
      &copy; TechSolvers 2025
    </div>
  </footer>

</body>
</html>
