package qmf.poc.agent.run;

import com.ibm.qmf.api.*;
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

    private Session _cacheSession = null;

    private Session getSession() throws Exception {
        if (_cacheSession == null) {
            log.debug("QMFObjectRunner use folder: " + qmfFolder);
            QMF api = new QMF(qmfFolder);

            log.debug("QMFObjectRunner use repository: connection=" + qmfConnection);
            api.setActiveRepository(qmfConnection, qmfUser, qmfPassword, user, password);

            log.debug("QMFObjectRunner use data source: ");
            _cacheSession = api.createSession(qmfDatasource, user, password);
        } else {
            log.debug("QMFObjectRunner use cached session");
        }
        return _cacheSession;
    }

    public String retrieveObjectHTML(String owner, String name, String format, int limit) throws Exception {
        Session session = getSession();

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

                log.debug("QMFObjectRunner save html result to tmp file: " + tempFile + "...");
                results.saveToFile(options, QueryResults.DATA_HTML, "");
                log.debug("QMFObjectRunner saved html result to tmp file: " + tempFile);
                String full = Files.readString(tempFile, StandardCharsets.UTF_8);
                if (limit >= 0) {
                    log.debug("QMFObjectRunner truncate html result to " + limit + " rows");
                } else {
                    log.debug("QMFObjectRunner no truncation of html result");
                }
                return limit >= 0 ? trunkHTMLTable(full, limit) : full;
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
            log.error("Invalid run parameter: " + args.qmfRun + ", expected <owner>,<name>");
        }
        try {
            QMFObjectRunner qmfObjectRunner = new QMFObjectRunner(args);
            String owner = run[0];
            String name = run[1];
            String format = "html";
            String result = qmfObjectRunner.retrieveObjectHTML(owner, name, format, -1);
            System.out.println(result);
        } catch (Exception e) {
            log.error("Failed to run QMF object", e);
        }
    }

    static String trunkHTMLTable(String html, int nRows) {
        if (html.length() < 10) {
            // don't touch small html
            return html;
        }

        int trClosePos = -1;
        for (int i = 0; i < nRows; i++) {
            trClosePos = html.indexOf("</tr>", trClosePos + 1);
            if (trClosePos == -1) {
                // not enough rows found
                return html;
            }
        }

        // Truncate the HTML after the last </tr> found
        return html.substring(0, trClosePos + 5) + "\n</table></body></html>";
    }

    private static final Log log = LogFactory.getLog("agent");
}
