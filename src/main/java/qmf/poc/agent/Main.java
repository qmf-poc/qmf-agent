package qmf.poc.agent;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qmf.poc.agent.catalog.CatalogProvider;
import qmf.poc.agent.ws.WebSockerProvider;

public class Main {

    public static void main(String[] cli) {
        try {
            final Args args = new Args(cli);

            if (args.printHelp) args.printHelp();
            if (args.printCatalog) CatalogProvider.printCatalog(args);
            if (args.printVersion) System.out.println("agent-0.0.1");
            if (args.connectToService) WebSockerProvider.listen(args);

        } catch (ParseException e) {
            System.err.println(e.getMessage());
            System.err.println("Use --help to show all options");
        }
    }

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger("agent");
}
