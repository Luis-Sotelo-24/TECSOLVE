<%-- 
    Document   : dashboard
    Created on : 12 may. 2025, 4:23:10 p. m.
    Author     : Guillermo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css" integrity="sha384-xOolHFLEh07PJGoPkLv1IbcEPTNtaed2xpHsD9ESMhqIYd0nLMwNLD69Npy4HI+N" crossorigin="anonymous">
        <style>
            body {
                background-color: #e6f4ea;
            }

            .sidebar {
                min-height: 100vh;
                background-color: #2e7d60;
                color: white;
            }

            .sidebar .nav-link {
                color: #cce9dd;
            }

            .sidebar .nav-link.active {
                background-color: #1b5e44;
                color: #ffffff;
            }

            .content {
                background-color: #f8fcf9;
                padding: 20px;
                min-height: 100vh;
            }

            .navbar-custom {
                background-color: #a5d6ba;
            }
        </style>
    </head>
    <body>
        <div class="container-fluid">
            <div class="row no-gutters">
                <!-- Sidebar -->
                <div class="col-md-2 sidebar p-3">
                    <h4 class="text-white">Mi Dashboard</h4>
                    <ul class="nav flex-column mt-4">
                        <li class="nav-item">
                            <a class="nav-link active" href="#">Inicio</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="#">Reportes</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="#">Estadísticas</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="#">Configuración</a>
                        </li>
                    </ul>
                </div>

                <!-- Contenido principal -->
                <div class="col-md-10 content">
                    <nav class="navbar navbar-expand-lg navbar-light navbar-custom mb-4">
                        <span class="navbar-brand mb-0 h1">Panel Principal</span>
                    </nav>

                    <div class="container-fluid">
                    <%@include file="usuarios.jsp" %>
                    </div>
                </div>
            </div>

            <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
