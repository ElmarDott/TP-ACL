package org.europa.together;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.domain.LogLevel;

/**
 * Start the Grizzly HTTP Server.
 */
public class Main {

    private static final Logger LOGGER = new LogbackLogger(Main.class);

    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:9999/together";
    public static final String BASE_PACKAGE = "org.europa.together.service.acl";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this
     * application.
     *
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {

        // create a resource config that scans for JAX-RS resources and providers
        // in BASE_PACKAGE
        final ResourceConfig rc = new ResourceConfig().packages(BASE_PACKAGE);

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();

        LOGGER.log("GRIZZLY HTTP SERVER STARTED", LogLevel.INFO);
        System.in.read();
        server.shutdown();
    }
}
