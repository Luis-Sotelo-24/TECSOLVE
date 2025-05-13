/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controlador;

import dao.usuarioDao;
import entidades.usuario;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Guillermo
 */
@WebServlet(name = "controladorAdmin", urlPatterns = {"/controladorAdmin"})
public class controladorAdmin extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String accion = request.getParameter("accion");
        if (accion != null) {
            if (accion.equals("Registrar")) {
                request.getRequestDispatcher("./vistas/dashboard.jsp").forward(request, response);
            } else if (accion.equals("principal")) {
                List<usuario> lista = new usuarioDao().getList();
                request.setAttribute("lista", lista);
                request.getRequestDispatcher("./vistas/dashboard.jsp").forward(request, response);
            } else if (accion.equals("edit")) {
                Integer idx = Integer.valueOf(request.getParameter("id"));
                usuario u = new usuarioDao().get(idx);

                request.setAttribute("nombre", "" + u.getNombre());
                request.setAttribute("apellido", "" + u.getApellido());
                request.setAttribute("correo", "" + u.getCorreo());
                request.setAttribute("direccion", "" + u.getDireccion());
                request.setAttribute("dni", "" +u.getDni());
                request.setAttribute("telefono", "" +u.getTelefono());
                request.setAttribute("dni", "" +u.getDni());
                
            } else if (accion.equals("delete")) {

            }
        }
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
        processRequest(request, response);
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
