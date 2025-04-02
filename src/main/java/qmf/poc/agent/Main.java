package qmf.poc.agent;

import com.cete.dynamicpdf.io.Q;
import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import qmf.poc.agent.broker.Broker;
import qmf.poc.agent.catalog.CatalogProvider;
import qmf.poc.agent.run.QMFObjectRunner;
import qmf.poc.agent.ws.WebSockerProvider;

public class Main {

    public static void main(String[] cli) {
        try {
            final Args args = new Args(cli);

            if (args.printHelp) args.printHelp();
            if (args.printVersion) System.out.println("agent-0.0.1");
            if (args.printCatalog) CatalogProvider.printCatalog(args);
            if (args.runQMFObject) QMFObjectRunner.runQMFObject(args);
            if (args.connectToService)
                WebSockerProvider.listen(args, new Broker(new CatalogProvider(args), new QMFObjectRunner(args)));

        } catch (ParseException e) {
            System.err.println(e.getMessage());
            System.err.println("Use --help to show all options");
        }
    }

    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog("agent");
}
