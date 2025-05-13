<%-- 
    Document   : index
    Created on : 12 may. 2025, 4:31:41 p. m.
    Author     : Guillermo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <% response.sendRedirect(request.getContextPath()
        + "/controladorAdmin?accion=principal");%>
    </body>
</html>
