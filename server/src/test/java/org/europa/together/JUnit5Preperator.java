package org.europa.together;

import java.net.URI;
import java.sql.SQLException;
import org.europa.together.application.JdbcActions;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.DatabaseActions;
import org.europa.together.business.Logger;
import org.europa.together.domain.LogLevel;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

/**
 * JUnit5 Extension to run code beforr all test, like setup test suite and
 * shutdown after all the testenvironment.<br>
 * <b>USAGE: </b> @ExtendWith({JUnit5DbPreperator.class})
 */
public class JUnit5Preperator
        implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

    private static final Logger LOGGER = new LogbackLogger(JUnit5Preperator.class);

    public static DatabaseActions JDBC_CONNECTION;
    private static boolean isConnected = false;

    public static HttpServer SERVER;
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8999/";
    public static final String BASE_PACKAGE = "org.europa.together";

    @Override
    public void beforeAll(ExtensionContext context) {
        LOGGER.log("TEST ENVIRONMENT will be prepared.", LogLevel.DEBUG);

        if (!JUnit5Preperator.isConnected) {
            JDBC_CONNECTION = new JdbcActions();
            try {
                JDBC_CONNECTION.connect("test");
                LOGGER.log("JDBC: " + JDBC_CONNECTION.getJdbcMetaData().toString(), LogLevel.DEBUG);

                JUnit5Preperator.isConnected = true;
                context.getRoot().getStore(GLOBAL).put("TogetherPlatform", this);

            } catch (SQLException ex) {
                LOGGER.catchException(ex);
            }
        } else {
            LOGGER.log("JDBC Connecton is already established.", LogLevel.DEBUG);
        }

        LOGGER.log("TEST ENVIRONMENT prepared... \n", LogLevel.DEBUG);
    }

    @Override
    public void close() {
        LOGGER.log("TEST ENVIRONMENT will be shut down.", LogLevel.DEBUG);
    }

    public static boolean isConnected() {
        return JUnit5Preperator.isConnected;
    }

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this
     * application.
     *
     * @param rc as ResourceConfig
     * @return Grizzly HTTP server.
     */
    public static boolean startServer() {
        boolean success = false;
        if (JUnit5Preperator.SERVER == null || !JUnit5Preperator.SERVER.isStarted()) {
            try {
                // create a resource config that scans for JAX-RS resources and
                // providers in BASE_PACKAGE
                final ResourceConfig rc
                        = new ResourceConfig().packages(BASE_PACKAGE);
                // create a new instance of grizzly http server
                // exposing the Jersey application at BASE_URI
                JUnit5Preperator.SERVER
                        = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);

                JUnit5Preperator.SERVER.start();
                System.in.read();

                success = true;
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        } else {
            success = true;
            LOGGER.log("Grizzly HTTP Server already started.", LogLevel.DEBUG);
        }
        return success;
    }
}
