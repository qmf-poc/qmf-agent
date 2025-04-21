package qmf.poc.agent;

import org.apache.commons.cli.*;
import qmf.poc.agent.jsonrpc.JsonRpcHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qmf.poc.agent.catalog.CatalogProvider;
import qmf.poc.agent.run.QMFObjectRunner;
import qmf.poc.agent.ws.WebSockerProvider;

public class Main {

    public static void main(String[] cli) {
        try {
            final Args args = new Args(cli);

            if (args.printHelp) args.printHelp();
            if (args.printVersion) Version.printVersion(log);
            if (args.printCatalog) CatalogProvider.printCatalog(args);
            if (args.runQMFObject) QMFObjectRunner.runQMFObject(args);
            if (args.connectToService)
                WebSockerProvider.listen(args, new JsonRpcHandler(new CatalogProvider(args), new QMFObjectRunner(args)));

        } catch (ParseException e) {
            System.err.println(e.getMessage());
            System.err.println("Use --help to show all options");
        }
    }

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger("agent");
}
