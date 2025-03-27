package qmf.poc.agent.catalog;

import org.apache.commons.cli.*;

public class Args {
    final String syntax;

    final String db2cs;
    final String db2user;
    final String db2password;
    final String db2charsetName;
    final boolean parallel;
    public final boolean printHelp;
    public final boolean printCatalog;

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
        printCatalog = cmd.hasOption("print-catalog");
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(syntax, getOptions());
        System.out.println("\nProperties:");
        System.out.println("-Dorg.slf4j.simpleLogger.defaultLogLevel=TRACE|DEBUG|INFO|WARN|ERROR");
    }

    private static Options getOptions() {
        final Options options = new Options();
        options.addOption("h", "help", false, "Show help");
        options.addOption("d", "db2cs", true, "db2 connection string: jdbc:db2://host:port/db");
        options.addOption("u", "db2user", true, "db2 user");
        options.addOption("p", "db2password", true, "db2 password");
        options.addOption("c", "charset", true, "charset for long data, default UTF-8");
        options.addOption("r", "print-catalog", false, "Fetch catalog and print");
        options.addOption("l", "parallel", false, "Use parallel threads");
        return options;
    }
}
