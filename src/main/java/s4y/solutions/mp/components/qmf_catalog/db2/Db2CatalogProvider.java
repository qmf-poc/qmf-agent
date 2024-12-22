package s4y.solutions.mp.components.qmf_catalog.db2;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import s4y.solutions.mp.components.connections_pool.IConnectionsPool;
import s4y.solutions.mp.components.qmf_catalog.*;
import s4y.solutions.mp.errorhandler.IErrorHandler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

// Assume the essential use case of the Catalog component is to read it at once
// and then work with the data in memory
/**
 * <p>Db2CatalogProvider class.</p>
 *
 * @author dsa
 */
public class Db2CatalogProvider<CPL extends IConnectionsPool> implements ICatalogProvider {
    // TODO: hardcoded schema
    private static final String queryDirectory = "SELECT * FROM Q.OBJECT_DIRECTORY";
    private static final String queryRemarks = "SELECT * FROM Q.OBJECT_REMARKS";
    private static final String queryData = "SELECT * FROM Q.OBJECT_DATA";
    private final CPL connectionPool;
    private final IErrorHandler errorHandler;

    private Db2CatalogProvider(CPL connectionPool, IErrorHandler errorHandler) {
        this.connectionPool = connectionPool;
        this.errorHandler = errorHandler;
        logger.debug("Instantiated");
    }

    /** {@inheritDoc} */
    @Override
    public Catalog getCatalog() {
        Connection connection = connectionPool.getConnection();
        List<ObjectDirectory> directories = new LinkedList<>();
        List<ObjectRemarks> remarks = new LinkedList<>();
        List<ObjectData> data = new LinkedList<>();

        logger.debug("reading the catalog from the database..");
        try (
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(queryDirectory)
        ) {
            while (rs.next()) {
                String owner = rs.getString("OWNER");
                String name = rs.getString("NAME");
                String type = rs.getString("TYPE");
                String subtype = rs.getString("SUBTYPE");
                int objectlevel = rs.getInt("OBJECTLEVEL");
                String restricted = rs.getString("RESTRICTED");
                String model = rs.getString("MODEL");
                Date created = rs.getDate("CREATED");
                Date modified = rs.getDate("MODIFIED");
                Date lastUsed = rs.getDate("LAST_USED");

                directories.add(new ObjectDirectory(
                        owner,
                        name,
                        type,
                        subtype,
                        objectlevel,
                        "Y".equals(restricted),
                        model,
                        created,
                        modified,
                        lastUsed));
            }
        } catch (Exception e) {
            errorHandler.handleError(e);
            return null;
        }
        try (
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(queryRemarks)
        ) {
            while (rs.next()) {
                String owner = rs.getString("OWNER");
                String name = rs.getString("NAME");
                String type = rs.getString("TYPE");
                String rem = rs.getString("REMARKS");

                remarks.add(new ObjectRemarks(owner, name, type, rem));
            }
        } catch (Exception e) {
            errorHandler.handleError(e);
            return null;
        }
        try (
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(queryData)
        ) {
            while (rs.next()) {
                String owner = rs.getString("OWNER");
                String name = rs.getString("NAME");
                String type = rs.getString("TYPE");
                Short seq = rs.getShort("SEQ");
                byte[] bytes = rs.getBytes("APPLDATA");
                data.add(new ObjectData(owner, name, type, seq, bytes));
            }
        } catch (Exception e) {
            errorHandler.handleError(e);
            return null;
        }
        logger.info("catalog read successfully");
        return new Catalog(data, remarks, directories);
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        logger.debug("Shutting down...");
        logger.info("Shut down");
    }

    /**
     * <p>define.</p>
     *
     * @param errorHandler a {@link s4y.solutions.mp.errorhandler.IErrorHandler} object
     * @param modifier a {@link java.util.function.Consumer} object
     * @param <CPL> a CPL class
     * @param <D_CPL> a D_CPL class
     * @return a {@link s4y.solutions.mp.components.qmf_catalog.db2.Db2CatalogProvider.Definition} object
     */
    @NotNull
    public static <CPL extends IConnectionsPool, D_CPL extends IConnectionsPool.Definition<CPL>>
    Definition<CPL, D_CPL> define(IErrorHandler errorHandler, @NotNull Consumer<Definition<CPL, D_CPL>.DSL> modifier) {
        final Definition<CPL, D_CPL> definition = new Definition<>(errorHandler);
        modifier.accept(definition.dsl);
        return definition;
    }

    static public class Definition<
            CPL extends IConnectionsPool,
            D_CPL extends IConnectionsPool.Definition<CPL>> implements ICatalogProvider.Definition<Db2CatalogProvider<CPL>> {
        private D_CPL connectionPoolDefinition;
        private final IErrorHandler errorHandler;

        public Definition(IErrorHandler errorHandler) {
            this.errorHandler = errorHandler;
        }

        private Db2CatalogProvider<CPL> singleton;

        public Db2CatalogProvider<CPL> use() {
            if (singleton == null) {
                CPL connectionPool = connectionPoolDefinition.use();
                if (connectionPool == null) return null;
                singleton = new Db2CatalogProvider<>(connectionPool, errorHandler);
            }
            return singleton;
        }

        // TODO: must be changed back to private
        public final DSL dsl = new DSL();

        public class DSL {
            public void connectionPool(@NotNull Supplier<D_CPL> init) {
                connectionPoolDefinition = init.get();
                logger.debug("configured with the connection pool: {}",
                        connectionPoolDefinition.getClass());
            }
        }
    }

    static final Logger logger = LoggerFactory.getLogger(Db2CatalogProvider.class);
}
