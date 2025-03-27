package qmf.poc.agent;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        db2cs = cmd.getOptionValue("db2cs", "jdbc:db2://qmfdb2.s4y.solutions:50000/sample");
        db2user = cmd.getOptionValue("db2user", "db2inst1");
        db2password = cmd.getOptionValue("db2password", "password");
        db2charsetName = cmd.getOptionValue("charset", "UTF-8");
        parallel = cmd.hasOption("parallel");
        printHelp = cmd.hasOption("help");
        printVersion = cmd.hasOption("version");
        printCatalog = cmd.hasOption("print-catalog");
        if (cmd.hasOption("websocket-uri")) {
            try {
                serviceUri = URI.create(cmd.getOptionValue("websocket-uri"));
            } catch (Exception e) {
                throw new ParseException(e.getMessage());
            }
        } else {
            serviceUri = null;
        }
        connectToService = serviceUri != null;
        try {
            repeat = Integer.parseInt(cmd.getOptionValue("repeat", "1"));
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

    private static Options getOptions() {
        final Options options = new Options();
        options.addOption("c", "charset", true, "charset for long data, default UTF-8");
        options.addOption("h", "help", false, "Show help");
        options.addOption("g", "print-catalog", false, "Fetch catalog and print");
        options.addOption("l", "parallel", false, "use parallel threads");
        options.addOption("n", "repeat", true, "repeat operation");
        options.addOption("p", "db2password", true, "db2 password");
        options.addOption("s", "db2cs", true, "db2 connection string: jdbc:db2://host:port/db");
        options.addOption("u", "db2user", true, "db2 user");
        options.addOption("v", "version", false, "print version");
        options.addOption("w", "websocket-uri", true, "service websocket uri");
        return options;
    }

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger("agent");
}
