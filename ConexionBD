/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BD;

/**
 *
 * @author Admin
 */

// INICIO - LIBRERIAS DE CONEXION  A LA BASE DE DATOS
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;
// FIN - LIBRERIAS DE CONEXION  A LA BASE DE DATOS

public class ConexionBD {

    // INICIO - CONEXION  A LA BASE DE DATOS : HUELLAS , MYSQL
    public String puerto = "3306";
    public String nomservidor = "localhost";
    public String db = "huellas";
    public String user = "root";
    public String pass = "";
    Connection conn = null;
         
    
    public Connection conectar(){
        try{
            String ruta = "jdbc:mysql://";
            String servidor = nomservidor+":"+puerto+"/";
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(ruta+servidor+db, user, pass);
            if(conn!=null){
                System.out.println("Conexion a la Base de Datos, LISTO...");
            }
            else if (conn == null)
            {
                throw new SQLException();
            }            
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }catch(ClassNotFoundException e){
            JOptionPane.showMessageDialog(null, "Se produjo el siguiente error: "+e.getMessage());                  
        }catch(NullPointerException e){
            JOptionPane.showMessageDialog(null, "Se produjo el siguiente error: "+e.getMessage());    
        }finally{
            return conn;
        }    
    }
    public void desconectar(){
        conn = null;
        System.out.println("Desconexion a la Base de Datos, BYE...");
    }
    // FIN - CONEXION  A LA BASE DE DATOS : HUELLAS , MYSQL
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
}
