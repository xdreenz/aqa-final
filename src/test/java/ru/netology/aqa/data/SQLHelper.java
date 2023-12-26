package ru.netology.aqa.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.*;

public class SQLHelper {

    private static final QueryRunner runner = new QueryRunner();
    private static final String dbURL = System.getProperty("db.url");
    private static final String dbUser = System.getProperty("db.user");
    private static final String dbPass = System.getProperty("db.password");

    private SQLHelper() {
    }

    @SneakyThrows
    private static Connection getConn() {
        return DriverManager.getConnection(dbURL, dbUser, dbPass);
    }

    @SneakyThrows
    public static void cleanDatabase() {
        var conn = getConn();
        runner.execute(conn, "DELETE FROM order_entity");
        runner.execute(conn, "DELETE FROM payment_entity");
        runner.execute(conn, "DELETE FROM credit_request_entity");
    }

    @SneakyThrows
    public static CreditRequestEntity getCreditRequestEntity() {
        var codeSQL = "SELECT bank_id, status FROM credit_request_entity ORDER BY created DESC LIMIT 1";
        var conn = getConn();
        return runner.query(conn, codeSQL, new BeanHandler<>(CreditRequestEntity.class));
    }

    @SneakyThrows
    public static OrderEntity getOrderEntity() {
        var codeSQL = "SELECT " +
                "COALESCE(credit_id, '') AS credit_id, " +
                "COALESCE(payment_id, '') AS payment_id " +
                "FROM order_entity " +
                "ORDER BY created DESC " +
                "LIMIT 1";
        var conn = getConn();
        return runner.query(conn, codeSQL, new BeanHandler<>(OrderEntity.class));
    }

    @SneakyThrows
    public static PaymentEntity getPaymentEntity() {
        var codeSQL = "SELECT amount, status, transaction_id FROM payment_entity ORDER BY created DESC LIMIT 1";
        var conn = getConn();
        return runner.query(conn, codeSQL, new BeanHandler<>(PaymentEntity.class));
    }

    @SneakyThrows
    public static Boolean isTheTableEmpty(String tableName) {
        var codeSQL = "SELECT COUNT(*) FROM " + tableName;
        var conn = getConn();
        var result = ((Long)runner.query(conn, codeSQL, new ScalarHandler<>()));
        return result == 0;
    }

    @Data
    @NoArgsConstructor
    public static class CreditRequestEntity {
        private String bank_id;
        private String status;
    }

    @Data
    @NoArgsConstructor
    public static class PaymentEntity {
        private String amount;
        private String status;
        private String transaction_id;
    }

    @Data
    @NoArgsConstructor
    public static class OrderEntity {
        private String credit_id;
        private String payment_id;

    }
}