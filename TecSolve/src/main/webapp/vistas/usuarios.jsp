<%-- 
    Document   : usuarios
    Created on : 12 may. 2025, 4:59:17 p. m.
    Author     : Guillermo
--%>

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
    <body>
        <div class="container mt-5">
            <h2 class="mb-4">Lista de Usuarios</h2>
            <div class="table-responsive">
                <table class="table table-striped table-bordered">
                    <thead class="thead-dark">
                        <tr>
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
                                <td>${usuario.nombre}</td>
                                <td>${usuario.apellido}</td>
                                <td>${usuario.direccion}</td>
                                <td>${usuario.telefono}</td>
                                <td>${usuario.correo}</td>
                                <td>${usuario.dni}</td>
                                <td>${usuario.rol}</td>
                                <td class="accion">
                                    <a href="<%=request.getContextPath()%>/ControladorAdmin?accion=editar&id=${material.idMaterial}" class="btn btn-primary">Editar</a>
                                    <a href="<%=request.getContextPath()%>/ControladorPrincipal?accion=delete&id=${material.idMaterial}"><i class="btn btn-danger">Eliminar</i></a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
        <div class="container mt-5">
            <!-- Botón para abrir la ventana emergente -->
            <button type="button" class="btn btn-success" data-toggle="modal" data-target="#formModal">
                Agregar Usuario
            </button>
        </div>

        <!-- Modal -->
        <div class="modal fade" id="formModal" tabindex="-1" role="dialog" aria-labelledby="formModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header bg-success text-white">
                        <h5 class="modal-title" id="formModalLabel">Nuevo Usuario</h5>
                        <button type="button" class="close text-white" data-dismiss="modal" aria-label="Cerrar">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <form>
                        <div class="modal-body">
                            <div class="form-row">
                                <div class="form-group col-md-6">
                                    <label for="nombre">Nombre</label>
                                    <input type="text" class="form-control" id="nombre" placeholder="Juan">
                                </div>
                                <div class="form-group col-md-6">
                                    <label for="apellido">Apellido</label>
                                    <input type="text" class="form-control" id="apellido" placeholder="Pérez">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="correo">Correo</label>
                                <input type="email" class="form-control" id="correo" placeholder="correo@ejemplo.com">
                            </div>
                            <div class="form-row">
                                <div class="form-group col-md-6">
                                    <label for="telefono">Teléfono</label>
                                    <input type="text" class="form-control" id="telefono" placeholder="987654321">
                                </div>
                                <div class="form-group col-md-6">
                                    <label for="dni">DNI</label>
                                    <input type="text" class="form-control" id="dni" placeholder="12345678">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="direccion">Dirección</label>
                                <input type="text" class="form-control" id="direccion" placeholder="Av. Siempre Viva 123">
                            </div>
                            <div class="form-group">
                                <label for="rol">Rol</label>
                                <select class="form-control" id="rol">
                                    <option>Administrador</option>
                                    <option>Usuario</option>
                                    <option>Invitado</option>
                                </select>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                            <button type="submit" class="btn btn-success">Guardar</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
