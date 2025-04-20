package qmf.poc.agent;

import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URI;

public class Args {
    final String syntax;

    public final String agentId;
    public final String db2cs;
    public final String db2user;
    public final String db2password;
    public final String db2charsetName;
    public final boolean printHelp;
    public final boolean printCatalog;
    public final boolean runQMFObject;
    public final int repeat;
    public final URI serviceUri;
    public final boolean connectToService;
    public final boolean printVersion;
    public final String qmfConnection;
    public final String qmfUser;
    public final String qmfPassword;
    public final String qmfDatasource;
    public final String qmfFolder;
    public final String qmfRun;
    public final boolean json;

    public Args(String[] args) throws ParseException {
        syntax = "java -jar agent-[version].jar";
        final CommandLineParser parser = new DefaultParser();
        final CommandLine cmd = parser.parse(getOptions(), args);

        agentId = getOptionValue(cmd, AGENT_ID, "agent-" + System.currentTimeMillis() % 10000);
        db2cs = getOptionValue(cmd, DB2CS, "jdbc:db2://qmfdb2.s4y.solutions:50000/sample");
        db2user = getOptionValue(cmd, DB2USER, "db2inst1");
        db2password = getOptionValue(cmd, DB2PASSWORD, "password");
        db2charsetName = getOptionValue(cmd, CHARSET, "UTF-8");
        printHelp = hasOption(cmd, HELP);
        printVersion = hasOption(cmd, VERSION);
        printCatalog = hasOption(cmd, PRINT_CATALOG);
        if (hasOption(cmd, WEBSOCKET_URI)) {
            try {
                serviceUri = new URI(withAgentId(getOptionValue(cmd, WEBSOCKET_URI, "")));
            } catch (Exception e) {
                throw new ParseException(e.getMessage());
            }
        } else if (hasOption(cmd, AGENT_MODE)) {
            serviceUri = URI.create(withAgentId(DEFAULT_URI));
        } else {
            serviceUri = null;
        }
        connectToService = serviceUri != null;
        try {
            repeat = Integer.parseInt(cmd.getOptionValue(REPEAT, "1"));
        } catch (NumberFormatException e) {
            throw new ParseException(e.getMessage());
        }
        qmfConnection = getOptionValue(cmd, QMF_CONNECTION, "Connection to Test 1");
        qmfUser = getOptionValue(cmd, QMF_USER, "admin");
        qmfPassword = getOptionValue(cmd, QMF_PASSWORD, "password");
        qmfDatasource = getOptionValue(cmd, QMF_DATASOURCE, "Test1 ds");
        qmfFolder = getOptionValue(cmd, QMF_FOLDER, System.getProperty("user.home") + "/Application Data/IBM/QMF for WebSphere");
        qmfRun = getOptionValue(cmd, QMF_RUN, null);
        runQMFObject = qmfRun != null;
        json = hasOption(cmd, JSON);
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(syntax, getOptions());
        System.out.println("\nProperties:");
        System.out.println("-Dorg.slf4j.simpleLogger.defaultLogLevel=TRACE|DEBUG|INFO|WARN|ERROR");
    }

    private String getOptionValue(CommandLine cmd, String option, String defaultValue) {
        String arg = cmd.getOptionValue(option, null);
        if (arg == null) {
            arg = System.getenv(option.toUpperCase().replace('-', '_'));
        }
        if (arg == null) {
            arg = defaultValue;
        }
        return arg;
    }

    private String withAgentId(String uri) {
        if (uri == null) return null;
        return uri + "?agent=" + agentId;
    }

    private Boolean hasOption(CommandLine cmd, String option) {
        if (cmd.hasOption(option))
            return true;
        return System.getenv(option.toUpperCase().replace('-', '_')) != null;
    }

    private static Options getOptions() {
        final Options options = new Options();
        options.addOption("a", AGENT_MODE, false, "agent mode. Shorthand for -w " + DEFAULT_URI);
        options.addOption("c", CHARSET, true, "charset for long data, default UTF-8");
        options.addOption("d", AGENT_ID, true, "agent id. Arbitrary string");
        options.addOption("e", QMF_CONNECTION, true, "QMF connection");
        options.addOption("f", JSON, false, "print as JSON");
        options.addOption("g", PRINT_CATALOG, false, "Fetch catalog and print");
        options.addOption("h", HELP, false, "Show help");
        options.addOption("i", QMF_USER, true, "QMF user");
        options.addOption("j", QMF_PASSWORD, true, "QMF password");
        options.addOption("k", QMF_DATASOURCE, true, "QMF datasource");
        options.addOption("n", REPEAT, true, "repeat operation");
        options.addOption("p", DB2PASSWORD, true, "db2 password");
        options.addOption("q", QMF_FOLDER, true, "qmf folder, usually ~/Application Data/IBM/QMF for WebSphere");
        options.addOption("r", QMF_RUN, true, "executes qmf object, --run owner,name");
        options.addOption("s", DB2CS, true, "db2 connection string: jdbc:db2://host:port/db");
        options.addOption("u", DB2USER, true, "db2 user");
        options.addOption("v", VERSION, false, "print version");
        options.addOption("w", WEBSOCKET_URI, true, "service websocket uri: [ws://]host:port[/path]");
        return options;
    }

    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog("agent");
    private static final String AGENT_ID = "id";
    private static final String AGENT_MODE = "agent";
    private static final String CHARSET = "charset";
    private static final String HELP = "help";
    private static final String VERSION = "version";
    private static final String REPEAT = "repeat";
    private static final String DB2CS = "db2cs";
    private static final String DB2USER = "db2user";
    private static final String DB2PASSWORD = "db2password";
    private static final String PRINT_CATALOG = "print-catalog";
    private static final String JSON = "json";
    private static final String WEBSOCKET_URI = "websocket-uri";
    private static final String QMF_CONNECTION = "qmf-connection";
    private static final String QMF_USER = "qmf-user";
    private static final String QMF_PASSWORD = "qmf-password";
    private static final String QMF_DATASOURCE = "qmf-datasource";
    private static final String QMF_FOLDER = "qmf-folder";
    private static final String QMF_RUN = "qmf-run";

    private static final String DEFAULT_URI = "ws://localhost:8082/agent";
}
