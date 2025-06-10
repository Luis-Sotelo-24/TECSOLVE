/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controlador;

import dao.categoriaDao;
import dao.productoDao;
import dao.usuarioDao;
import entidades.categoria;
import entidades.producto;
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
public class AdminServlet extends HttpServlet {

    productoDao dao = new productoDao();

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
            if (accion.equals("dashboard")) {
                request.getRequestDispatcher("./vistas/dashboard.jsp").forward(request, response);

            } else if (accion.equals("usuario")) {
                List<usuario> lista = new usuarioDao().getList();
                request.setAttribute("lista", lista);
                request.getRequestDispatcher("./vistas/usuarios.jsp").forward(request, response);

            } else if (accion.equals("producto")) {
                List<categoria> listaCat = new categoriaDao().getList();
                request.setAttribute("listaCategoria", listaCat);
                List<producto> lista = new productoDao().getList();
                request.setAttribute("listaProd", lista);
                request.getRequestDispatcher("./vistas/producto.jsp").forward(request, response);

            } else if (accion.equals("venta")) {
                List<usuario> lista = new usuarioDao().getList();
                request.setAttribute("lista", lista);
                request.getRequestDispatcher("./vistas/venta.jsp").forward(request, response);

            } else if (accion.equals("editarUsuario")) {
                Integer idx = Integer.valueOf(request.getParameter("idUsuario"));
                usuario u = new usuarioDao().get(idx);
                request.setAttribute("idUsuario", "" + u.getIdUsuario());
                request.setAttribute("nombre", "" + u.getNombre());
                request.setAttribute("apellido", "" + u.getApellido());
                request.setAttribute("correo", "" + u.getCorreo());
                request.setAttribute("direccion", "" + u.getDireccion());
                request.setAttribute("dni", "" + u.getDni());
                request.setAttribute("telefono", "" + u.getTelefono());
                request.setAttribute("dni", "" + u.getDni());
                request.getRequestDispatcher("./vistas/dashboard.jsp").forward(request, response);

            } else if (accion.equals("borrarUsuario")) {

            } else if (accion.equals("editarProducto")) {
                List<categoria> listaCat = new categoriaDao().getList();
                request.setAttribute("listaCategoria", listaCat);
                int id = Integer.parseInt(request.getParameter("id"));
                producto prod = dao.get(id);
                request.setAttribute("producto", prod);
                request.getRequestDispatcher("./vistas/producto.jsp").forward(request, response);
            } else if (accion.equals("borrarProducto")) {
                int id = Integer.parseInt(request.getParameter("id"));
                productoDao dao = new productoDao();
                dao.eliminarProducto(id);
                response.sendRedirect("controladorAdmin?accion=producto");
            } else if (accion.equals("login")) {
                request.getRequestDispatcher("./vistas/login.jsp").forward(request, response);
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
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        String accion = request.getParameter("accion");

        String nombre = request.getParameter("nombreProducto");
        String descripcion = request.getParameter("descripcion");
        double precio = Double.parseDouble(request.getParameter("precio"));
        int stock = Integer.parseInt(request.getParameter("stock"));
        int idCategoria = Integer.parseInt(request.getParameter("idCategoria"));

        categoria cat = new categoria();
        cat.setIdCategoria(idCategoria);

        productoDao dao = new productoDao();

        if ("agregar".equals(accion)) {
            producto nuevo = new producto();
            nuevo.setNombreProducto(nombre);
            nuevo.setDescripcion(descripcion);
            nuevo.setPrecio(precio);
            nuevo.setStock(stock);
            nuevo.setCategoria(cat);

            dao.agregarProducto(nuevo);
        } else if ("actualizar".equals(accion)) {
            int idProducto = Integer.parseInt(request.getParameter("idProducto"));

            producto actualizado = new producto();
            actualizado.setIdProducto(idProducto);
            actualizado.setNombreProducto(nombre);
            actualizado.setDescripcion(descripcion);
            actualizado.setPrecio(precio);
            actualizado.setStock(stock);
            actualizado.setCategoria(cat);

            dao.actualizarProducto(actualizado);
        }

        response.sendRedirect("controladorAdmin?accion=producto");
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
