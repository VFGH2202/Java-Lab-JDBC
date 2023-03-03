package Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class DbHandler extends Configs {
    Connection dbConnection;

    public Connection getDbConnection()
            throws ClassNotFoundException, SQLException{
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        String connectionString = "jdbc:mysql://" + dbHost + ":"
                + dbPort + "/" + dbName;


        dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);

        return dbConnection;
    }

    public void create_product(String name, Integer Quant, Integer MinQuant) {
        String insert = "INSERT INTO " + Const.PROD_TABLE + "(" +
                Const.PROD_NAME + "," + Const.PROD_QUANTITY + "," + Const.PROD_MINIMUM_QUANTITY +
                ")" + "VALUES(?,?,?)";

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(insert);
            prSt.setString(1, name);
            prSt.setInt(2, Quant);
            prSt.setInt(3, MinQuant);
            prSt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete_item(Integer id){
        String del = "DELETE FROM " + Const.PROD_TABLE + " WHERE id = " + id.toString() + ";";
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(del);
            prSt.execute();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet getAll() {
        ResultSet resSet = null;

        String select = "SELECT * FROM " + Const.PROD_TABLE;

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            resSet = prSt.executeQuery();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return resSet;
    }

    public void UpdateCol(String prod, Integer col) {
        String update = "UPDATE " + Const.PROD_TABLE + " SET " + Const.PROD_QUANTITY + " = "
                + Const.PROD_QUANTITY + " - " + col + " WHERE "
                + Const.PROD_NAME + " = \"" + prod + "\"";
        System.out.println(update);

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(update);
            prSt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void Purchase(String prod, Integer col) {
        String update = "UPDATE " + Const.PROD_TABLE + " SET " + Const.PROD_QUANTITY + " = "
                + col + " WHERE "
                + Const.PROD_NAME + " = \"" + prod + "\"";
        System.out.println(update);

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(update);
            prSt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void add_order(String name, String loc, String fullname, Integer quant) {
        String insert = "INSERT INTO " + dbName + "." + Const.ORDER_TABLE + "(" +
                Const.ORDER_QUATITY + "," + Const.ORDER_FULLNAME + "," + Const.ORDER_LOCATION + "," + Const.ORDER_NAME +
                ")" + "VALUES(?,?,?,?)";

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(insert);
            prSt.setInt(1, quant);
            prSt.setString(2, fullname);
            prSt.setString(3, loc);
            prSt.setString(4, name);
            prSt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet orderGetAll() {
        ResultSet resSet = null;

        String select = "SELECT * FROM " + dbName + "." + Const.ORDER_TABLE;

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            resSet = prSt.executeQuery();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return resSet;
    }

    public void orderDeleteItem(Integer id) {
        String del = "DELETE FROM " + dbName + "." + Const.ORDER_TABLE + " WHERE id = " + id.toString() + ";";
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(del);
            prSt.execute();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
