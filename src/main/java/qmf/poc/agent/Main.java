package qmf.poc.agent;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qmf.poc.agent.catalog.Args;
import qmf.poc.agent.catalog.CatalogProvider;
import qmf.poc.agent.catalog.models.Catalog;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class Main {
    private static final Logger log = LoggerFactory.getLogger("agent");

    public static void main(String[] cli) {
        try {
            final Args args = new Args(cli);

            if (args.printHelp) args.printHelp();
            if (args.printCatalog) printCatalog(args);

        } catch (ParseException e) {
            System.err.println(e.getMessage());
            System.err.println("Use --help to show all options");
        }
    }


    private static void printCatalog(Args args) {
        log.debug("printCatalog.enter");
        try (final CatalogProvider provider = new CatalogProvider(args)) {
            if (provider.parallelEnabled()) {
                log.debug("parallel fetch");
                final CompletableFuture<Catalog> catalogFuture = provider.catalogAsync();
                final Catalog catalog = catalogFuture.join();
                System.out.println(catalog.toString());
            } else {
                System.out.println(provider.catalog().toString());
            }
            log.debug("printCatalog.exit");
        } catch (SQLException e) {
            log.error("printCatalog.failed: " + e.getMessage(), e);
        }
    }
}
