package qmf.poc.agent.run;

import com.ibm.qmf.api.QMF;
import com.ibm.qmf.api.Query;
import com.ibm.qmf.api.QueryResults;
import com.ibm.qmf.api.SaveResultsToFileOptions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import qmf.poc.agent.Args;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class QMFObjectRunner {
    private final String qmfFolder;
    private final String qmfConnection;
    private final String qmfUser;
    private final String qmfPassword;
    private final String qmfDatasource;
    private final String user;
    private final String password;

    public QMFObjectRunner(Args args) {
        qmfFolder = args.qmfFolder;
        qmfConnection = args.qmfConnection;
        qmfUser = args.qmfUser;
        qmfPassword = args.qmfPassword;
        qmfDatasource = args.qmfDatasource;
        user = args.db2user;
        password = args.db2password;
    }

    public String retrieveObjectHTML(String owner, String name, String format) throws Exception {
        log.debug("QMFObjectRunner use folder: " + qmfFolder);
        QMF api = new QMF(qmfFolder);

        log.debug("QMFObjectRunner use repository: connection=" + qmfConnection);
        api.setActiveRepository(qmfConnection, qmfUser, qmfPassword, user, password);

        log.debug("QMFObjectRunner use data source: ");
        var session = api.createSession(qmfDatasource, user, password);

        Object retrieved = session.retrieveObject(owner, name);
        if (retrieved instanceof Query) {
            Query query = (Query) retrieved;
            SaveResultsToFileOptions options = new SaveResultsToFileOptions(session);
            Path tempFile = Files.createTempFile("qmf_run", ".html");

            try {
                options.setFileName(tempFile.toAbsolutePath().toString());
                log.debug("QMFObjectRunner run query: " + query);
                query.run();
                QueryResults results = query.getResults();

                log.debug("QMFObjectRunner save html result to tmp file: " + tempFile);
                results.saveToFile(options, QueryResults.DATA_HTML, "");
                return Files.readString(tempFile, StandardCharsets.UTF_8);
            } finally {
                Files.deleteIfExists(tempFile);
            }
        } else if (retrieved == null) {
            throw new Exception("Object (owner=" + owner + ", name=" + name + ") not found");
        } else {
            throw new Exception("Unsupported object type " + retrieved);
        }
    }

    public static void runQMFObject(Args args) {
        String[] run = args.qmfRun.split(",");
        if (run.length != 2) {
            log.error("Invalid run parameter: " + args.qmfRun+", expected <owner>,<name>");
        }
        try {
            QMFObjectRunner qmfObjectRunner = new QMFObjectRunner(args);
            String owner = run[0];
            String name = run[1];
            String format = "html";
            String result = qmfObjectRunner.retrieveObjectHTML(owner, name, format);
            System.out.println(result);
        } catch (Exception e) {
            log.error("Failed to run QMF object", e);
        }
    }
    private static final Log log = LogFactory.getLog("agent");
}
