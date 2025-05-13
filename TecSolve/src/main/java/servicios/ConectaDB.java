package servicios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConectaDB {

       public static Connection getConnection() {

        //return cnx ;
        Connection cnx = null;

        String url = "jdbc:mysql://localhost:3306/tecsolve?useTimeZone=true&"
                + "serverTimezone=UTC&autoReconnect=true";

        String user = "root";
        String clave = "12345678";

        String Driver = "com.mysql.cj.jdbc.Driver";

        try {
            Class.forName(Driver);

            cnx = DriverManager.getConnection(url, user, clave);
        } catch (ClassNotFoundException ex) {
        } catch (SQLException ex) {
        }

        return cnx;
    }

    public static void main(String[] args) throws SQLException{
        Connection cnx = ConectaDB.getConnection();
        
        System.out.println(""+ cnx.getCatalog());
    
    }
    
}
