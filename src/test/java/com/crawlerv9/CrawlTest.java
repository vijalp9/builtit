package com.crawlerv9;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.*;

import java.io.File;
import java.net.URI;

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
}
