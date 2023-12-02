package ru.netology.aqa.data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHelper {

    private static final QueryRunner runner = new QueryRunner();

    private SQLHelper() {
    }

    private static Connection getConn() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/app", "app", "pass");
    }

    @SneakyThrows
    public static DataHelper.CreditRequestEntity getCreditEntity() {
        var codeSQL = "SELECT * FROM credit_request_entity ORDER BY created DESC LIMIT 1";
        var conn = getConn();
        var result = runner.query(conn, codeSQL, new BeanHandler<>(DataHelper.CreditRequestEntity.class));
        return result;
    }

    @SneakyThrows
    public static DataHelper.OrderEntity getOrderEntity() {
        var codeSQL = "SELECT * FROM order_entity ORDER BY created DESC LIMIT 1";
        var conn = getConn();
        var result = runner.query(conn, codeSQL, new BeanHandler<>(DataHelper.OrderEntity.class));
        return result;
    }

    @SneakyThrows
    public static DataHelper.PaymentEntity getPaymentEntity() {
        var codeSQL = "SELECT * FROM payment_entity ORDER BY created DESC LIMIT 1";
        var conn = getConn();
        var result = runner.query(conn, codeSQL, new BeanHandler<>(DataHelper.PaymentEntity.class));
        return result;
    }

}
