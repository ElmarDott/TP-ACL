package org.europa.together.service.acl;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.europa.together.Main;
import org.europa.together.application.JdbcActions;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.DatabaseActions;
import org.europa.together.business.Logger;
import org.europa.together.domain.LogLevel;
import org.europa.together.domain.acl.ResourcesDO;
import org.europa.together.utils.acl.Constraints;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@SuppressWarnings("unchecked")
@RunWith(JUnitPlatform.class)
public class ResourceServiceIT {

    private static final Logger LOGGER = new LogbackLogger(ResourceServiceIT.class);
    private static DatabaseActions actions = new JdbcActions(true);
    private static final String FLUSH_TABLES = "TRUNCATE ROLES, ACCOUNT, LOGIN, PERMISSIONS, RESOURCES;";
    private static final String FILE = "org/europa/together/sql/acl/testdata_ACL.sql";

    private static HttpServer server;
    private static WebTarget target;
    private final String API_PATH
            = Constraints.MODULE_NAME + "/" + Constraints.REST_API_VERSION;

    //<editor-fold defaultstate="collapsed" desc="Test Preparation">
    @BeforeAll
    static void setUp() {
        LOGGER.log("### TEST SUITE INICIATED.", LogLevel.TRACE);
        boolean check = true;
        String out = "executed";
        boolean socket = actions.connect("default");
        if (!socket) {
            out = "skiped.";
            check = false;
        }
        actions.executeSqlFromClasspath(FILE);

        try {
            server = Main.startServer();
            ClientConfig config = new ClientConfig();
            Client client = ClientBuilder.newClient(config);
            target = client.target(Main.BASE_URI);

            if (!server.isStarted()) {
                out = "skiped.";
                check = false;
            }
        } catch (Exception ex) {
            LOGGER.catchException(ex);
        }

        LOGGER.log(target.toString(), LogLevel.TRACE);
        LOGGER.log("Assumption terminated. TestSuite will be " + out, LogLevel.TRACE);
        Assumptions.assumeTrue(check);
    }

    @AfterAll
    static void tearDown() {
        server.shutdown();
        actions.executeQuery(FLUSH_TABLES);
        LOGGER.log("### TEST SUITE TERMINATED.\n", LogLevel.TRACE);
    }

    @BeforeEach
    void testCaseInitialization() {
    }

    @AfterEach
    void testCaseTermination() {
        LOGGER.log("TEST CASE TERMINATED.", LogLevel.TRACE);
    }
    //</editor-fold>

    @Test
    void testGetResourceByDefaultStatus200() {
        LOGGER.log("TEST CASE: getResourceByDefault() 200 : OK", LogLevel.DEBUG);

        String json = "{\"actions\":\"ALL\",\"class\":\"org.europa.together.domain.acl.ResourcesDO\",\"deleteable\":false,\"name\":\"Article\",\"view\":\"default\"}";
        Response response = target
                .path(API_PATH).path("/resource").path("/Article")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());
        assertEquals(json, response.readEntity(String.class));
    }

    @Test
    void testGetResourceStatus200() {
        LOGGER.log("TEST CASE: getResource() 200 : OK", LogLevel.DEBUG);

        String json = "{\"actions\":\"ALL\",\"class\":\"org.europa.together.domain.acl.ResourcesDO\",\"deleteable\":true,\"name\":\"Article\",\"view\":\"teaser\"}";
        Response response = target
                .path(API_PATH).path("/resource").path("/Article").queryParam("resourceView", "teaser")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());
        assertEquals(json, response.readEntity(String.class));
    }

    @Test
    void testGetResourceStatus404() {
        LOGGER.log("TEST CASE: getResource() 404 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/resource").path("/NotExist")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    void testGetResourcebyDefaultStatus404() {
        LOGGER.log("TEST CASE: getResource() 404 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/resource").path("/NotExist").queryParam("resourceView", "notExist")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    @Disabled
    void testGetResourceStatus403() {
        LOGGER.log("TEST CASE: getResource() 403 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/resource").path("/PermissionDenied")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(403, response.getStatus());
    }

    @Test
    void testGetProtectedResources() {
        LOGGER.log("TEST CASE: getProtectedResources() 200 : OK", LogLevel.DEBUG);

        String json = "{\"list\": [\n"
                + "{\"actions\":\"ALL\",\"class\":\"org.europa.together.domain.acl.ResourcesDO\",\"deleteable\":false,\"name\":\"Document\",\"view\":\"default\"}\n"
                + "{\"actions\":\"ALL\",\"class\":\"org.europa.together.domain.acl.ResourcesDO\",\"deleteable\":false,\"name\":\"Article\",\"view\":\"default\"}\n"
                + "]}";
        Response response = target
                .path(API_PATH).path("/resource").path("/protected")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());
        assertEquals(json, response.readEntity(String.class));
    }

    @Test
    void testGetAllResources() {
        LOGGER.log("TEST CASE: getAllResources() 200 : OK", LogLevel.DEBUG);
        Response response = target
                .path(API_PATH).path("/resource").path("/all")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());
    }

    @Test
    void testGetAllResourcesOfSameType() {
        LOGGER.log("TEST CASE: getAllResourcesOfSameType() 200 : OK", LogLevel.DEBUG);

        String json = "{\"list\": [\n"
                + "{\"actions\":\"ALL\",\"class\":\"org.europa.together.domain.acl.ResourcesDO\",\"deleteable\":false,\"name\":\"Article\",\"view\":\"default\"}\n"
                + "{\"actions\":\"ALL\",\"class\":\"org.europa.together.domain.acl.ResourcesDO\",\"deleteable\":true,\"name\":\"Article\",\"view\":\"delete\"}\n"
                + "{\"actions\":\"ALL\",\"class\":\"org.europa.together.domain.acl.ResourcesDO\",\"deleteable\":true,\"name\":\"Article\",\"view\":\"teaser\"}\n"
                + "]}";
        Response response = target
                .path(API_PATH).path("/resource").path("/type").path("/Article")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());
        assertEquals(json, response.readEntity(String.class));
    }

    @Test
    void testGetAllResourcesOfSameType404() {
        LOGGER.log("TEST CASE: getAllResourcesOfSameType() 404 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/resource").path("/type").path("/NotExist")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    void testUpdateResourcesStatus202() {
        LOGGER.log("TEST CASE: updateResources() 202 : ACCEPTED", LogLevel.DEBUG);

        ResourcesDO resource = new ResourcesDO();
        resource.setName("Sample");
        resource.setView("update");
        resource.setActions("OVERWRITTEN");

        Response response = target
                .path(API_PATH).path("/resource")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(resource));

        assertEquals(202, response.getStatus());
    }

    @Test
    void testUpdateResourcesStatus404() {
        LOGGER.log("TEST CASE: updateResources() 404 : NOT EXIST", LogLevel.DEBUG);

        ResourcesDO resource = new ResourcesDO();
        resource.setName("NotExist");
        resource.setView("default");

        Response response = target
                .path(API_PATH).path("/resource")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(resource));

        assertEquals(404, response.getStatus());
    }

    @Test
    void testDeleteResourceStatus410() {
        LOGGER.log("TEST CASE: deleteResource() 410 : DELETE", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/resource").path("/Article")
                .queryParam("resourceView", "teaser")
                .request(MediaType.APPLICATION_JSON)
                .delete(Response.class);

        assertEquals(409, response.getStatus());
    }

    @Test
    void testDeleteResourceStatus409() {
        LOGGER.log("TEST CASE: deleteResource() 409 : CONFLICT", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/resource").path("/Article")
                .queryParam("resourceView", "teaser")
                .request(MediaType.APPLICATION_JSON)
                .delete(Response.class);

        assertEquals(409, response.getStatus());
    }

    @Test
    void testDeleteResourcesStatus403() {
        LOGGER.log("TEST CASE: deleteResources() 403 : FORBIDDEN", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/resource").path("/Document")
                .queryParam("resourceView", "default")
                .request(MediaType.APPLICATION_JSON)
                .delete(Response.class);

        assertEquals(403, response.getStatus());
    }

    @Test
    void testDeleteResourceStatus404() {
        LOGGER.log("TEST CASE: deleteResource() 404 : NOT FOUND", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/resource").path("/NotExist")
                .queryParam("resourceView", "default")
                .request()
                .delete(Response.class);

        assertEquals(404, response.getStatus());
    }

    @Test
//    @Disabled
    void testCreateResourcesStatus201() {
        LOGGER.log("TEST CASE: createResources() 201 : CREATED", LogLevel.DEBUG);

        Response response;

        ResourcesDO resource = new ResourcesDO();
        resource.setName("CREATE");
        resource.setView("default");
        resource.setDeleteable(true);

        response = target
                .path(API_PATH).path("/resource")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(resource));

        assertEquals(201, response.getStatus());

        response = target
                .path(API_PATH).path("/resource").path("/CREATE")
                .queryParam("resourceView", "default")
                .request(MediaType.APPLICATION_JSON)
                .delete(Response.class);

        assertEquals(410, response.getStatus());
    }
}
