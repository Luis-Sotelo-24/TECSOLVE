/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controlador;

import dao.usuarioDao;
import entidades.usuario;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import seguridad.authService;

/**
 *
 * @author Guillermo
 */
@WebServlet(name = "controladorLogin", urlPatterns = {"/controladorLogin"})
public class LoginServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        String email = request.getParameter("correo");
        email=email.toLowerCase();
        String clave = request.getParameter("clave");

        usuarioDao dao = new usuarioDao();
        usuario usuario = dao.buscarEmail(email);
        if (usuario != null && authService.verificar(clave, usuario.getClave())) {
            // Guardar datos en sesión
            HttpSession session = request.getSession();
            session.setAttribute("usuario", usuario);

            if (null == usuario.getRol()) {
                response.sendRedirect("login.jsp?error=Rol desconocido");
            } else // Redirección según rol
            {
                switch (usuario.getRol()) {
                    case "admin":
                        response.sendRedirect("./vistas/dashboard.jsp");
                        break;
                    case "cliente":
                        response.sendRedirect("./vistas/index.jsp");
                        break;
                    default:
                        response.sendRedirect("login.jsp?error=Rol desconocido");
                        break;
                }
            }
        } else {
            request.setAttribute("error", "Credenciales inválidas");
            request.getRequestDispatcher("./vistas/login.jsp").forward(request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
