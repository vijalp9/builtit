package com.crawlerv9;

import com.crawlerv9.crawlers.WebCrawler;
import com.crawlerv9.output.PageSummary;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.*;

import static org.junit.Assert.*;


import java.io.File;
import java.net.URI;
import java.util.Collection;

public class CrawlTest {

    private static Server server;

    private static URI serverUri;

    @BeforeClass
    public static void startJetty() throws Exception {
        System.out.println("Starting Jetty Server...");
        server = new Server();

        // Set the connector port [auto-configure]
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0);
        server.addConnector(connector);

        // Auto-serve our resources/mysite/* files
        ServletContextHandler context = new ServletContextHandler();
        context.setWelcomeFiles(new String[]{"Index.html"});

        ServletHolder defaultServ = new ServletHolder("default", DefaultServlet.class);

        // A little hacky but okay...:
        defaultServ.setInitParameter("resourceBase", new File(CrawlTest.class.getClassLoader().getResource("mysite").toURI()).getPath());
        defaultServ.setInitParameter("dirAllowed", "true");
        context.addServlet(defaultServ, "/");
        server.setHandler(context);

        // Start the server
        server.start();

        serverUri = server.getURI();
        System.out.println("Test server running at " + serverUri);
    }

    @AfterClass
    public static void stopJetty() throws Exception {
        System.out.println("Stopping Jetty Server...");
        server.stop();
    }

    @Test
    // Not really a test as we don't automatically verify the results, but just to trigger the output
    // to the console from our sample pages...
    public void crawlLocal() throws Exception {
        Crawl.main(new String[]{serverUri.toString()});
    }

    @Test
    public void crawlAndVerify() throws Exception {
        Collection<PageSummary> pageSummaries = new WebCrawler(serverUri.toString()).crawl();
        // Note: Only 3 actual pages are provided.  1 dead page, plus root "/" and /Index.html get counted separately...
        assertEquals("Incorrect number of pages found", 5, pageSummaries.size());
        for (PageSummary ps : pageSummaries) {

            if (ps.getPageUrl().endsWith("Page4.html")) {

                System.out.println("Testing Page4.html...");
                this.assertPageSummary(ps, true, 0, 0, 0);

            } else if (ps.getPageUrl().endsWith("Page3.html")) {

                System.out.println("Testing Page3.html...");
                this.assertPageSummary(ps, false, 0, 1, 1);

            } else if (ps.getPageUrl().endsWith("Page2.html")) {

                System.out.println("Testing Page2.html...");
                this.assertPageSummary(ps, false, 0, 0, 1);

            } else if (ps.getPageUrl().endsWith("Index.html")) {

                System.out.println("Testing Index.html...");
                this.assertPageSummary(ps, false, 1, 2, 3);

            } else {
                System.out.println("Testing root index....");
                this.assertPageSummary(ps, false, 1, 2, 3);
            }
        }
    }

    private void assertPageSummary(PageSummary ps, boolean deadLink, int images, int externalLinks, int sameDomainLinks) {
        assertEquals(ps.getPageUrl() + ": Invalid number of image links", images, ps.getImageLinks().size());
        assertEquals(ps.getPageUrl() + ": Invalid number of external links", externalLinks, ps.getExternalLinks().size());
        assertEquals(ps.getPageUrl() + ": Invalid number of same domain links", sameDomainLinks, ps.getSameDomainLinks().size());
        assertEquals(ps.getPageUrl() + ": Dead link", deadLink, ps.isDeadLink());

    }
}
