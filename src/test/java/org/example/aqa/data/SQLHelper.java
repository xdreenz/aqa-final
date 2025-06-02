package org.example.aqa.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.io.IOException;
import java.sql.*;

public class SQLHelper {

    private static final QueryRunner runner = new QueryRunner();

    private SQLHelper() {
    }

    private static Connection getConn() {
        try {
            return DriverManager.getConnection(
                    ConfigReader.getInstance().getConfig().getDbURL(),
                    ConfigReader.getInstance().getConfig().getDbUser(),
                    ConfigReader.getInstance().getConfig().getDbPassword());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void cleanDatabase() {
        var conn = getConn();
        try {
            runner.execute(conn, "DELETE FROM order_entity");
            runner.execute(conn, "DELETE FROM payment_entity");
            runner.execute(conn, "DELETE FROM credit_request_entity");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static CreditRequestEntity getCreditRequestEntity() {
        var codeSQL = "SELECT bank_id, status FROM credit_request_entity ORDER BY created DESC LIMIT 1";
        var conn = getConn();
        try {
            return runner.query(conn, codeSQL, new BeanHandler<>(CreditRequestEntity.class));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static OrderEntity getOrderEntity() {
        var codeSQL = "SELECT credit_id, payment_id FROM order_entity ORDER BY created DESC LIMIT 1";
        var conn = getConn();
        try {
            return runner.query(conn, codeSQL, new BeanHandler<>(OrderEntity.class));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static PaymentEntity getPaymentEntity() {
        var codeSQL = "SELECT amount, status, transaction_id FROM payment_entity ORDER BY created DESC LIMIT 1";
        var conn = getConn();
        try {
            return runner.query(conn, codeSQL, new BeanHandler<>(PaymentEntity.class));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Boolean isTheTableEmpty(String tableName) {
        Integer result;
        var codeSQL = "SELECT COUNT(*) FROM " + tableName;
        var conn = getConn();
        try {
            result = runner.query(conn, codeSQL, new ScalarHandler<>());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result.equals(0);
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