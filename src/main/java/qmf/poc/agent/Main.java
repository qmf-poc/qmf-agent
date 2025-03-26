package qmf.poc.agent;

import io.vertx.db2client.DB2Builder;
import io.vertx.db2client.DB2ConnectOptions;
import io.vertx.sqlclient.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qmf.poc.agent.catalog.CatalogProvider;
import qmf.poc.agent.catalog.models.ObjectRemarks;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static java.sql.DriverManager.getConnection;

public class Main {
    private static final Logger log = LoggerFactory.getLogger("agent");

    public static void main(String[] args) {
        System.out.println("set log level: -Dorg.slf4j.simpleLogger.defaultLogLevel=TRACE|DEBUG|INFO|WARN|ERROR");
        System.out.println("db2 connection string: -Dqmf.db2cs=jdbc:db2://host:port/db");
        String db2cs = System.getProperty("qmf.db2cs", "jdbc:db2://qmfdb2.s4y.solutions:50000/sample");
        final DB2ConnectOptions connectOptions = new DB2ConnectOptions()
                .setPort(50000)
                .setHost("qmfdb2.s4y.solutions")
                .setDatabase("sample")
                .setUser("db2inst1")
                .setPassword("password");
        final PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(5);
        final SqlClient client = DB2Builder.client()
                .with(poolOptions)
                .connectingTo(connectOptions)
                .build();

        // final CatalogProvider provider = new CatalogProvider(client, StandardCharsets.UTF_8);
        final CatalogProvider provider = new CatalogProvider(client, Charset.forName("IBM037"));
        provider.objectRemarksList()
                .onComplete(ar -> {
                    if (ar.succeeded()) {
                        final List<ObjectRemarks> r = ar.result();
                        log.info("Catalog: {}", r.toString());
                        client.close();
                    } else {
                        // Handle failure, log the error
                        log.error("Error occurred:", ar.cause());
                    }
                });
    }
}
