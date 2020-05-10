package data;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHelper {

    private static final String url = System.getProperty("db.url");
    private static final String user = "app";
    private static final String password = "pass";


    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static String findPaymentStatus() throws SQLException {
        String stmt = "select status from payment_entity order by created desc limit 1;";
        String columnName = "status";
        return getData(stmt, columnName);
    }

    public static String findCreditStatus() throws SQLException {
        String stmt = "select status from credit_request_entity order by created desc limit 1;";
        String columnName = "status";
        return getData(stmt, columnName);
    }

    public static String findPaymentId() throws SQLException {
        String stmt = "select payment_id from order_entity order by created desc limit 1;";
        String columnName = "payment_id";
        return getData(stmt, columnName);
    }

    public static String findCreditId() throws SQLException {
        String stmt = "select credit_id from order_entity order by created desc limit 1;";
        String columnName = "credit_id";
        return getData(stmt, columnName);
    }

    public static boolean isNotEmpty() throws SQLException {
        String stmt = "select * from order_entity;";
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(stmt);
        ResultSet resultSet = statement.executeQuery();
        boolean dbNotEmpty = resultSet.next();
        connection.close();
        return dbNotEmpty;
    }

    private static String getData(String stmt, String columnName) throws SQLException {
        String result;
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(stmt);
        ResultSet resultSet = statement.executeQuery();
        resultSet.next();
        result = resultSet.getString(columnName);
        connection.close();
        return result;
    }

    public static void cleanTables() {
        String deleteOrderEntity = "delete from order_entity;";
        String deletePaymentEntity = "delete from payment_entity;";
        String deleteCreditEntity = "delete from credit_request_entity;";

        try {
            Connection connection = getConnection();
            PreparedStatement orderEntity = connection.prepareStatement(deleteOrderEntity);
            PreparedStatement paymentEntity = connection.prepareStatement(deletePaymentEntity);
            PreparedStatement creditEntity = connection.prepareStatement(deleteCreditEntity);
            orderEntity.executeUpdate();
            paymentEntity.executeUpdate();
            creditEntity.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            e.getErrorCode();
        }
    }

}