/*
 * Copyright (C) 2015 hcadavid
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.pdsw.webappsintro.jdbc.example.basic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class JDBCExample {

    public static void main(String args[]) {
        try {
            String url = "jdbc:mysql://desarrollo.is.escuelaing.edu.co:3306/bdprueba";
            String driver = "com.mysql.jdbc.Driver";
            String user = "bdprueba";
            String pwd = "bdprueba";

            Class.forName(driver);
            Connection con = DriverManager.getConnection(url, user, pwd);
            con.setAutoCommit(false);

            System.out.println("Valor total pedido 1:" + valorTotalPedido(con, 1));

            List<String> prodsPedido = nombresProductosPedido(con, 2135494);
            
            printTables(con);

            System.out.println("Productos del pedido 1:");
            System.out.println("-----------------------");
            for (String nomprod : prodsPedido) {
                System.out.println(nomprod);
            }
            System.out.println("-----------------------");
            int suCodigoECI = 2135494;
            int suCodigoECI2 = 2103258;
//            registrarNuevoProducto(con, suCodigoECI, "Sergio Rodriguez", 99999999); Solo se puede hacer el registro una vez.
//            registrarNuevoProducto(con, suCodigoECI2, "Jonathan Prieto", 99999999);
            con.commit();

            con.close();

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCExample.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Agregar un nuevo producto con los parámetros dados
     *
     * @param con la conexión JDBC
     * @param codigo
     * @param nombre
     * @param precio
     * @throws SQLException
     */
    public static void registrarNuevoProducto(Connection con, int codigo, String nombre, int precio) throws SQLException {
        //Crear preparedStatement
        //Asignar parámetros
        //usar 'execute'
        PreparedStatement p = con.prepareStatement("INSERT INTO ORD_PRODUCTOS (codigo, nombre, precio) VALUES (?,?,?)");
        p.setInt(1, codigo);
        p.setString(2, nombre);
        p.setInt(3, precio);
        p.execute();
        con.commit();

    }

    /**
     * Consultar los nombres de los productos asociados a un pedido
     *
     * @param con la conexión JDBC
     * @param codigoPedido el código del pedido
     * @return
     */
    public static List<String> nombresProductosPedido(Connection con, int codigoPedido) throws SQLException {
        List<String> np = new LinkedList<>();

        //Crear prepared statement
        //asignar parámetros
        //usar executeQuery
        //Sacar resultados del ResultSet
        //Llenar la lista y retornarla
        PreparedStatement p = con.prepareStatement("SELECT opr.nombre,opr.codigo FROM ORD_PRODUCTOS opr WHERE opr.codigo=?");
        p.setInt(1, codigoPedido);
        ResultSet r = p.executeQuery();
        r.first();
        do {
            np.add(r.getString("nombre"));
        } while (r.next());

        con.rollback();

        return np;
    }

    /**
     * Calcular el costo total de un pedido
     *
     * @param con
     * @param codigoPedido código del pedido cuyo total se calculará
     * @return el costo total del pedido (suma de: cantidades*precios)
     */
    public static int valorTotalPedido(Connection con, int codigoPedido) throws SQLException {

        //Crear prepared statement
        //asignar parámetros
        //usar executeQuery
        //Sacar resultado del ResultSet
        PreparedStatement p = con.prepareStatement("SELECT SUM(pr.precio*pe.cantidad) AS precioTotal "
                + "FROM ORD_PRODUCTOS AS pr JOIN ORD_DETALLES_PEDIDO AS pe ON (pe.producto_fk=pr.codigo) WHERE pe.pedido_fk=?");
        p.setInt(1, codigoPedido);
        
        ResultSet r = p.executeQuery();
        if(!r.first())return 123;
        return r.getInt("precioTotal");
    }
    
    public static void printTables(Connection con) throws SQLException{
        PreparedStatement p2 = con.prepareStatement("SELECT pr.*,pe.* FROM ORD_PRODUCTOS AS pr JOIN ORD_DETALLES_PEDIDO AS pe ON (pe.producto_fk=pr.codigo)");
        ResultSet r1 = p2.executeQuery();
        r1.first();
        do {
            System.out.println(r1.getString("nombre")+" "+r1.getInt("precio")+" "+r1.getInt("cantidad")+" "+r1.getInt("pedido_fk"));
        } while (r1.next());
        
    }

}
