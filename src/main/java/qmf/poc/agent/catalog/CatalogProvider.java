package qmf.poc.agent.catalog;

import org.apache.commons.dbcp2.*;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qmf.poc.agent.Args;
import qmf.poc.agent.catalog.models.Catalog;
import qmf.poc.agent.catalog.models.ObjectData;
import qmf.poc.agent.catalog.models.ObjectDirectory;
import qmf.poc.agent.catalog.models.ObjectRemarks;

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

    public CatalogProvider(String db2sc, String user, String password, String charsetName, boolean parallelEnabled) {
        charset = Charset.forName(charsetName);
        dbExecutor = parallelEnabled ? Executors.newFixedThreadPool(4) : null;
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
                args.db2charsetName,
                args.parallel);
    }

    public List<ObjectData> objectDataList() throws SQLException {
        List<ObjectData> result = new ArrayList<>();
        log.trace("objectDataList.getConnection...");
        try (final Connection connection = dataSource.getConnection();
             final Statement stmt = connection.createStatement();
             final ResultSet row = stmt.executeQuery(QUERY_DATA)) {
            log.trace("objectDataList.iterate");
            int count = 0;
            while (row.next()) {
                log.trace("objectDataList.iterate: " + ++count);
                final String owner = row.getString("OWNER");
                final String name = row.getString("NAME");
                final String type = row.getString("TYPE");
                final byte[] applDataBinary = row.getBytes("CONCATENATED_APPLDATA");
                final String concatenatedApplData = new String(applDataBinary, charset); // TODO: should be handled by JDBC

                final ObjectData objectData = new ObjectData(owner, name, type, concatenatedApplData);
                result.add(objectData);
            }
            log.trace("objectDataList.done: " + count);
        }
        return result;
    }

    public CompletableFuture<List<ObjectData>> objectDataListAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return objectDataList(); // Call the blocking method safely
            } catch (SQLException e) {
                log.error("Error fetching object data", e);
                return Collections.emptyList(); // Return empty list on failure
            }
        }, dbExecutor);
    }

    public List<ObjectDirectory> objectDirectoryList() throws SQLException {
        final List<ObjectDirectory> result = new ArrayList<>();
        log.trace("objectDirectoryList.getConnection...");
        try (
                final Connection connection = dataSource.getConnection();
                final Statement stmt = connection.createStatement();
                final ResultSet row = stmt.executeQuery(QUERY_DIRECTORY)) {
            log.trace("objectDirectoryList.iterate");
            int count = 0;
            while (row.next()) {
                log.trace("objectDirectoryList.iterate: " + ++count);
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

                final ObjectDirectory objectDirectory = new ObjectDirectory(owner, name, type, subType, objectLevel, restricted, model, created, modified, lastUsed);
                result.add(objectDirectory);
            }
            log.trace("objectDirectoryList.done: " + count);
        }
        return result;
    }

    public CompletableFuture<List<ObjectRemarks>> objectRemarksListAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return objectRemarksList();
            } catch (SQLException e) {
                log.error("Error fetching object remarks", e);
                return Collections.emptyList();
            }
        }, dbExecutor);
    }

    public List<ObjectRemarks> objectRemarksList() throws SQLException {
        List<ObjectRemarks> result = new ArrayList<>();
        log.trace("objectRemarksList.getConnection...");
        try (
                final Connection connection = dataSource.getConnection();
                final Statement stmt = connection.createStatement();
                final ResultSet row = stmt.executeQuery(QUERY_REMARKS)) {
            log.trace("objectRemarksList.iterate");
            int count = 0;
            while (row.next()) {
                log.trace("objectRemarksList.iterate: " + ++count);
                final String owner = row.getString("OWNER");
                final String name = row.getString("NAME");
                final String type = row.getString("TYPE");
                final String remarks = row.getString("REMARKS");

                final ObjectRemarks objectRemarks = new ObjectRemarks(owner, name, type, remarks);
                result.add(objectRemarks);
            }
            log.trace("objectRemarksList.done: " + count);
        }
        return result;
    }

    public CompletableFuture<List<ObjectDirectory>> objectDirectoryListAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return objectDirectoryList();
            } catch (SQLException e) {
                log.error("Error fetching object directories", e);
                return Collections.emptyList();
            }
        }, dbExecutor);
    }

    public Catalog catalog() throws SQLException {
        final List<ObjectData> objectDataList = objectDataList();
        final List<ObjectRemarks> objectRemarksList = objectRemarksList();
        final List<ObjectDirectory> objectDirectoryList = objectDirectoryList();
        return new Catalog(objectDataList, objectRemarksList, objectDirectoryList);
    }

    public CompletableFuture<Catalog> catalogAsync() {
        CompletableFuture<List<ObjectData>> objectDataFuture = objectDataListAsync();
        CompletableFuture<List<ObjectRemarks>> objectRemarksFuture = objectRemarksListAsync();
        CompletableFuture<List<ObjectDirectory>> objectDirectoryFuture = objectDirectoryListAsync();

        return CompletableFuture.allOf(objectDataFuture, objectRemarksFuture, objectDirectoryFuture)
                .thenApply(v -> new Catalog(
                        objectDataFuture.join(),
                        objectRemarksFuture.join(),
                        objectDirectoryFuture.join()
                ));
    }

    public boolean parallelEnabled() {
        return dbExecutor != null;
    }

    private static final String QUERY_DATA = String.join("\n",
            "SELECT",
            "  OWNER,",
            "  NAME,",
            "  \"TYPE\",",
            "  LISTAGG(APPLDATA, '') WITHIN GROUP (ORDER BY SEQ) AS CONCATENATED_APPLDATA",
            "FROM",
            "  Q.OBJECT_DATA",
            "GROUP BY",
            "  OWNER, NAME, \"TYPE\""
    );

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

    private static final String QUERY_DIRECTORY = "SELECT * FROM Q.OBJECT_DIRECTORY";

    private static final String QUERY_REMARKS = "SELECT * FROM Q.OBJECT_REMARKS OFFSET 1 ROWS FETCH NEXT 335 ROWS ONLY";

    private static final Logger log = LoggerFactory.getLogger("agent");

}
