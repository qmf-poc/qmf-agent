package qmf.poc.agent.catalog;

import org.apache.commons.dbcp2.*;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import qmf.poc.agent.Args;
import qmf.poc.agent.catalog.models.*;

import javax.sql.DataSource;
import java.io.Closeable;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CatalogProvider implements Closeable {
    private final DataSource dataSource;
    private final Charset charset;
    private final ExecutorService dbExecutor;

    public CatalogProvider(String db2sc, String user, String password, String charsetName) {
        charset = Charset.forName(charsetName);
        dbExecutor = Executors.newFixedThreadPool(4);
        final Properties props = new Properties();
        props.put("user", user);
        props.put("password", password);
        final ConnectionFactory connectionFactory =
                new DriverManagerConnectionFactory(db2sc, props);
        final PoolableConnectionFactory poolableConnectionFactory =
                new PoolableConnectionFactory(connectionFactory, null);
        final ObjectPool<PoolableConnection> connectionPool =
                new GenericObjectPool<>(poolableConnectionFactory);
        poolableConnectionFactory.setPool(connectionPool);
        dataSource = new PoolingDataSource<>(connectionPool);
        log.debug("dataSource created");
    }

    public CatalogProvider(Args args) {
        this(
                args.db2cs,
                args.db2user,
                args.db2password,
                args.db2charsetName);
    }

    public List<QMFObject> objectsList() throws SQLException {
        final List<QMFObject> result = new ArrayList<>();
        log.trace("objectsList.getConnection...");
        try (
                final Connection connection = dataSource.getConnection();
                final Statement stmt = connection.createStatement();
                final ResultSet row = stmt.executeQuery(QUERY_OBJECT)) {
            log.trace("objectsList.iterate");
            int count = 0;
            while (row.next()) {
                log.trace("objectsList.iterate: " + ++count);
                final String owner = row.getString("OWNER");
                final String name = row.getString("NAME");
                final String type = row.getString("TYPE");
                final String subType = row.getString("SUBTYPE");
                final int objectLevel = row.getInt("OBJECTLEVEL");
                final String restricted = row.getString("RESTRICTED");
                final String model = row.getString("MODEL");
                final String created = row.getString("CREATED");
                final String modified = row.getString("MODIFIED");
                final String lastUsed = row.getString("LAST_USED");
                final byte[] applDataBinary = row.getBytes("CONCATENATED_APPLDATA");
                final String concatenatedApplData = new String(applDataBinary, charset); // TODO: should be handled by JDBC
                final String remarks = row.getString("REMARKS");

                final QMFObject object = new QMFObject(owner, name, type, subType, objectLevel, restricted, model, created, modified, lastUsed, concatenatedApplData, remarks);
                result.add(object);
            }
            log.trace("objectsList.done: " + count);
        }
        return result;
    }

    public Catalog catalog() throws SQLException {
        final List<QMFObject> objectsList = objectsList();
        return new Catalog(objectsList);
    }

    @Override
    public void close() {
        if (dataSource instanceof PoolingDataSource) {
            try {
                ((PoolingDataSource<?>) dataSource).close();
            } catch (Exception e) {
                log.error("Error closing the connection pool", e);
            }
        }
        if (dbExecutor != null)
            dbExecutor.shutdown();
    }

    public static void printCatalog(Args args) {
        log.debug("printCatalog.enter");
        try (final CatalogProvider provider = new CatalogProvider(args)) {
            for (int i = 1; i <= args.repeat; i++) {
                if (i > 1) {
                    Thread.sleep(1000);
                }
                System.out.println(provider.catalog().toString());
            }
            log.debug("printCatalog.exit");
        } catch (SQLException e) {
            log.error("printCatalog.failed: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String QUERY_OBJECT = String.join("\n",
            "SELECT",
            "  od.OWNER,",
            "  od.NAME,",
            "  od.\"TYPE\",",
            "  od.CONCATENATED_APPLDATA,",
            "  or.REMARKS,",
            "  odir.SUBTYPE,",
            "  odir.OBJECTLEVEL,",
            "  odir.RESTRICTED,",
            "  odir.MODEL,",
            "  odir.CREATED,",
            "  odir.MODIFIED,",
            "  odir.LAST_USED",
            "FROM",
            "  (SELECT",
            "    OWNER,",
            "    NAME,",
            "    \"TYPE\",",
            "    LISTAGG(APPLDATA, '') WITHIN GROUP (ORDER BY SEQ) AS CONCATENATED_APPLDATA",
            "  FROM",
            "    Q.OBJECT_DATA",
            "  GROUP BY",
            "    OWNER, NAME, \"TYPE\") od",
            "LEFT OUTER JOIN Q.OBJECT_REMARKS or ON od.OWNER = or.OWNER AND od.NAME = or.NAME AND od.\"TYPE\" = or.\"TYPE\"",
            "LEFT JOIN Q.OBJECT_DIRECTORY odir ON od.OWNER = odir.OWNER AND od.NAME = odir.NAME AND od.\"TYPE\" = odir.\"TYPE\""
    );

    private static final Log log = LogFactory.getLog("agent");

}
