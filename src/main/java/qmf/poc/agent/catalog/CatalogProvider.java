package qmf.poc.agent.catalog;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qmf.poc.agent.catalog.models.Catalog;
import qmf.poc.agent.catalog.models.ObjectData;
import qmf.poc.agent.catalog.models.ObjectDirectory;
import qmf.poc.agent.catalog.models.ObjectRemarks;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CatalogProvider {
    public CatalogProvider(SqlClient client, Charset charset) {
        this.client = client;
        this.charset = charset;
    }

    public Future<List<ObjectData>> objectDataList() {
        // Return a Future that will complete when the query finishes
        return client.query(QUERY_DATA).execute()
                .compose(rows -> {
                    List<ObjectData> result = new ArrayList<>();

                    for (Row row : rows) {
                        String owner = row.getString("OWNER");
                        String name = row.getString("NAME");
                        String type = row.getString("TYPE");
                        byte[] applDataBinary = row.getBuffer("CONCATENATED_APPLDATA").getBytes();
                        String concatenatedApplData = new String(applDataBinary, charset);

                        ObjectData objectData = new ObjectData(owner, name, type, concatenatedApplData);
                        result.add(objectData);
                    }

                    // Return the result list wrapped in a Future
                    return Future.succeededFuture(result);
                })
                .onFailure(cause -> log.error("An error occurred: {}", cause.getMessage(), cause));
    }

    public Future<List<ObjectDirectory>> objectDirectoryList() {
        return client.query(QUERY_DIRECTORY).execute()
                .compose(rows -> {
                    List<ObjectDirectory> result = new ArrayList<>();

                    for (Row row : rows) {
                        String owner = row.getString("OWNER");
                        String name = row.getString("NAME");
                        String type = row.getString("TYPE");
                        String subType = row.getString("SUBTYPE");
                        int objectLevel = row.getInteger("OBJECTLEVEL");
                        String restricted = row.getString("RESTRICTED");
                        String model = row.getString("MODEL");
                        String created = row.getString("CREATED");
                        String modified = row.getString("MODIFIED");
                        String lastUsed = row.getString("LASTUSED");

                        ObjectDirectory objectDirectory = new ObjectDirectory(owner, name, type, subType, objectLevel, restricted, model, created, modified, lastUsed);
                        result.add(objectDirectory);
                    }

                    return Future.succeededFuture(result);
                })
                .onFailure(cause -> log.error("An error occurred: {}", cause.getMessage(), cause));
    }

    public Future<List<ObjectRemarks>> objectRemarksList() {
        return client.preparedQuery(QUERY_REMARKS)
        /*
        return client.query(QUERY_REMARKS).execute()
                .compose(rows -> {
                    List<ObjectRemarks> result = new ArrayList<>();

                    for (Row row : rows) {
                        String owner = row.getString("OWNER");
                        String name = row.getString("NAME");
                        String type = row.getString("TYPE");
                        String remarks = row.getString("REMARKS");

                        ObjectRemarks objectRemarks = new ObjectRemarks(owner, name, type, remarks);
                        result.add(objectRemarks);
                    }

                    return Future.succeededFuture(result);
                })
                .onFailure(cause ->
                        log.error("An error occurred: {}", cause.getMessage(), cause));
         */
    }

    public Future<Catalog> catalog() {
        // Fetch object data, object remarks, and object directories in parallel
        Future<List<ObjectData>> objectDataFuture = objectDataList();
        Future<List<ObjectRemarks>> objectRemarksFuture = objectRemarksList();
        Future<List<ObjectDirectory>> objectDirectoryFuture = objectDirectoryList();

        // Use composite future to gather all results and create a Catalog object
        return Future.all(objectDataFuture, objectRemarksFuture, objectDirectoryFuture)
                .compose(compositeFuture -> {
                    // Extract results from futures
                    List<ObjectData> objectDataList = compositeFuture.resultAt(0);
                    List<ObjectRemarks> objectRemarksList = compositeFuture.resultAt(1);
                    List<ObjectDirectory> objectDirectoryList = compositeFuture.resultAt(2);

                    // Create a Catalog object
                    Catalog catalog = new Catalog(objectDataList, objectRemarksList, objectDirectoryList);

                    // Return the Catalog wrapped in a future
                    return Future.succeededFuture(catalog);
                })
                .onFailure(cause -> {
                    log.error("An error occurred while fetching catalog data: {}", cause.getMessage(), cause);
                });
    }

    private SqlClient client;
    private Charset charset;

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

    private static final String QUERY_DIRECTORY = "SELECT * FROM Q.OBJECT_DIRECTORY";

    private static final String QUERY_REMARKS = "SELECT * FROM Q.OBJECT_REMARKS OFFSET 1 ROWS FETCH NEXT 335 ROWS ONLY";

    private static final Logger log = LoggerFactory.getLogger("agent");
}
