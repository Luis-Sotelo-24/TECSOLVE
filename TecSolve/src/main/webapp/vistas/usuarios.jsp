<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="dao.usuarioDao"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Tabla de Usuarios</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css" integrity="sha384-xOolHFLEh07PJGoPkLv1IbcEPTNtaed2xpHsD9ESMhqIYd0nLMwNLD69Npy4HI+N" crossorigin="anonymous">    </head>
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
                            <a class="nav-link" href="<%=request.getContextPath()%>/controladorAdmin?accion=dashboard">Inicio</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="<%=request.getContextPath()%>/controladorAdmin?accion=usuario">Usuario</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="<%=request.getContextPath()%>/controladorAdmin?accion=producto">Producto</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="<%=request.getContextPath()%>/controladorAdmin?accion=venta">Venta</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="./vistas/logout.jsp">Cerrar Sesion</a>
                        </li>
                    </ul>
            </div>
            <!--Gaaaaa -->
            <!-- Contenido principal -->
            <div class="container mt-5">
                <h2 class="mb-4">Lista de Usuarios</h2>
                <div class="table-responsive">
                    <table class="table table-striped table-bordered">
                        <thead class="thead-dark">
                            <tr>
                                <th>Id</th>
                                <th>Nombre</th>
                                <th>Apellido</th>
                                <th>Correo</th>
                                <th>Teléfono</th>
                                <th>Dirección</th>
                                <th>DNI</th>
                                <th>Rol</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="usuario" items="${lista}">
                                <tr>
                                    <td>${usuario.idUsuario}</td>
                                    <td>${usuario.nombre}</td>
                                    <td>${usuario.apellido}</td>
                                    <td>${usuario.correo}</td>
                                    <td>${usuario.telefono}</td>
                                    <td>${usuario.dni}</td>
                                    <td>${usuario.direccion}</td>
                                    <td>${usuario.rol}</td>
                                    <td class="accion">
                                        <a href="<%=request.getContextPath()%>/controladorAdmin?accion=editarUsuario&id=${usuario.idUsuario}" class="btn btn-primary">Editar</a>
                                        <a href="<%=request.getContextPath()%>/controladorAdmin?accion=borrarUsuario&id=${usuario.idUsuario}"><i class="btn btn-danger">Eliminar</i></a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
            <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/js/bootstrap.bundle.min.js"></script>
            </body>
            </html>
