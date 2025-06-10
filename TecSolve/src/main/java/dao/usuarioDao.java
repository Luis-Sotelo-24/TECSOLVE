package dao;

import entidades.usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import seguridad.authService;
import servicios.ConectaDB;

/**
 *
 * @author Guillermo
 */
public class usuarioDao {

    private Connection cnx;

    public usuarioDao() {
        cnx = new ConectaDB().getConnection();
    }

    public usuario get(int idx) {
        usuario u = null;
        PreparedStatement ps;
        ResultSet rs;

        String cadSQL = "select idUsuario, nombre, apellido, correo, telefono, dni, direccion, rol from usuario where idUsuario=?";

        try {
            ps = cnx.prepareStatement(cadSQL);
            ps.setInt(1, idx);
            rs = ps.executeQuery();

            if (rs.next()) {
                u = new usuario();
                u.setIdUsuario(rs.getInt("idUsuario"));
                u.setNombre(rs.getString("nombre"));
                u.setApellido(rs.getString("apellido"));
                u.setCorreo(rs.getString("correo"));
                u.setTelefono(rs.getString("telefono"));
                u.setDni(rs.getString("dni"));
                u.setDireccion(rs.getString("direccion"));
                u.setRol(rs.getString("rol"));
            }
            rs.close();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return u;
    }

    public usuario buscarEmail(String email) {
        usuario u = null;
        PreparedStatement ps;
        ResultSet rs;

        String cadSQL = "select * from usuario where correo=?";

        try {
            ps = cnx.prepareStatement(cadSQL);
            ps.setString(1, email);
            rs = ps.executeQuery();

            if (rs.next()) {
                u = new usuario();
                u.setIdUsuario(rs.getInt("idUsuario"));
                u.setNombre(rs.getString("nombre"));
                u.setApellido(rs.getString("apellido"));
                u.setCorreo(rs.getString("correo"));
                u.setTelefono(rs.getString("telefono"));
                u.setDni(rs.getString("dni"));
                u.setDireccion(rs.getString("direccion"));
                u.setClave(rs.getString("clave"));
                u.setRol(rs.getString("rol"));
                u.setFechaRegistro(rs.getString("fechaRegistro"));
            }
            rs.close();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return u;
    }

    public List<usuario> getList() {
        PreparedStatement ps;
        ResultSet rs;
        String cadSQL = "select idUsuario, nombre, apellido, direccion, telefono, dni, correo, rol from usuario";
        List<usuario> lista = null;
        try {
            ps = cnx.prepareStatement(cadSQL);
            rs = ps.executeQuery();
            lista = new ArrayList<>();
            while (rs.next()) {
                usuario u = new usuario(
                        rs.getInt("idUsuario"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("direccion"),
                        rs.getString("telefono"),
                        rs.getString("dni"),
                        rs.getString("correo"),
                        rs.getString("rol")
                );
                lista.add(u);
            }
            rs.close();
        } catch (SQLException ex) {
        }
        return lista;
    }

    public void agregarUsuario(usuario u) {
        String sql = "INSERT INTO usuario (nombre, apellido, direccion, telefono, dni, correo,clave, rol) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellido());
            ps.setString(3, u.getDireccion());
            ps.setString(4, u.getTelefono());
            ps.setString(5, u.getDni());
            ps.setString(6, u.getCorreo());
            ps.setString(7, authService.encriptar(u.getClave()));
            ps.setString(8, u.getRol());
            ps.executeUpdate();
        } catch (SQLException e) {
        }
    }
}
