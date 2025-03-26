package qmf.poc.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

import static java.sql.DriverManager.getConnection;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        System.out.println("set log level: -Dorg.slf4j.simpleLogger.defaultLogLevel=TRACE|DEBUG|INFO|WARN|ERROR");
        System.out.println("db2 cs: -Dqmf.db2cs=jdbc:db2://host:port/db");
        String db2cs = System.getProperty("qmf.db2cs", "jdbc:db2://qmfdb2.s4y.solutions:50000/sample");
        try {
            final Connection connection = getConnection(db2cs, "db2inst1", "password");
            log.trace(connection.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
