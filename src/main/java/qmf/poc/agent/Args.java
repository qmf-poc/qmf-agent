package qmf.poc.agent;

import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URI;

public class Args {
    final String syntax;

    public final String db2cs;
    public final String db2user;
    public final String db2password;
    public final String db2charsetName;
    public final boolean parallel;
    public final boolean printHelp;
    public final boolean printCatalog;
    public final int repeat;
    public final URI serviceUri;
    public final boolean connectToService;
    public final boolean printVersion;

    public Args(String[] args) throws ParseException {
        syntax = "java -jar agent-[version].jar";
        final CommandLineParser parser = new DefaultParser();
        final CommandLine cmd = parser.parse(getOptions(), args);

        db2cs = getOptionValue(cmd, DB2CS, "jdbc:db2://qmfdb2.s4y.solutions:50000/sample");
        db2user = getOptionValue(cmd, DB2USER, "db2inst1");
        db2password = getOptionValue(cmd, DB2PASSWORD, "password");
        db2charsetName = getOptionValue(cmd, CHARSET, "UTF-8");
        parallel = hasOption(cmd, PARALLEL);
        printHelp = hasOption(cmd, HELP);
        printVersion = hasOption(cmd, VERSION);
        printCatalog = hasOption(cmd, PRINT_CATALOG);
        if (hasOption(cmd, WEBSOCKET_URI)) {
            try {
                serviceUri = URI.create(getOptionValue(cmd, WEBSOCKET_URI, "ws://localhost:8080"));
            } catch (Exception e) {
                throw new ParseException(e.getMessage());
            }
        } else {
            serviceUri = null;
        }
        connectToService = serviceUri != null;
        try {
            repeat = Integer.parseInt(cmd.getOptionValue(REPEAT, "1"));
        } catch (NumberFormatException e) {
            throw new ParseException(e.getMessage());
        }
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

    private Boolean hasOption(CommandLine cmd, String option) {
        if (cmd.hasOption(option))
            return true;
        return System.getenv(option.toUpperCase().replace('-', '_')) != null;
    }

    private static Options getOptions() {
        final Options options = new Options();
        options.addOption("c", CHARSET, true, "charset for long data, default UTF-8");
        options.addOption("h", HELP, false, "Show help");
        options.addOption("g", PRINT_CATALOG, false, "Fetch catalog and print");
        options.addOption("l", PARALLEL, false, "use parallel threads");
        options.addOption("n", REPEAT, true, "repeat operation");
        options.addOption("p", DB2PASSWORD, true, "db2 password");
        options.addOption("s", DB2CS, true, "db2 connection string: jdbc:db2://host:port/db");
        options.addOption("u", DB2USER, true, "db2 user");
        options.addOption("v", VERSION, false, "print version");
        options.addOption("w", WEBSOCKET_URI, true, "service websocket uri");
        return options;
    }

    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog("agent");
    private static final String CHARSET = "charset";
    private static final String HELP = "help";
    private static final String VERSION = "version";
    private static final String REPEAT = "repeat";
    private static final String DB2CS = "db2cs";
    private static final String DB2USER = "db2user";
    private static final String DB2PASSWORD = "db2password";
    private static final String PRINT_CATALOG = "print-catalog";
    private static final String PARALLEL = "parallel";
    private static final String WEBSOCKET_URI = "websocket-uri";
}
