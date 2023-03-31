package org.europa.together.service.acl;

import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.europa.together.EmbeddedGrizzly;
import org.europa.together.application.JacksonJsonTools;
import org.europa.together.application.JdbcActions;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.DatabaseActions;
import org.europa.together.business.JsonTools;
import org.europa.together.business.Logger;
import org.europa.together.domain.LogLevel;
import org.europa.together.domain.acl.AccountDO;
import org.europa.together.domain.acl.LoginDO;
import org.europa.together.domain.acl.PermissionDO;
import org.europa.together.domain.acl.ResourcesDO;
import org.europa.together.domain.acl.RolesDO;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SuppressWarnings("unchecked")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"/applicationContext.xml"})
public class ResourceServiceIT {

    private static final Logger LOGGER = new LogbackLogger(ResourceServiceIT.class);

    private static final String FLUSH_TABLE
            = "TRUNCATE " + RolesDO.TABLE_NAME + ", "
            + AccountDO.TABLE_NAME + ", "
            + LoginDO.TABLE_NAME + ", "
            + PermissionDO.TABLE_NAME + ", "
            + ResourcesDO.TABLE_NAME + ";";
    private static final String FILE
            = "org/europa/together/sql/acl/testdata_ACL.sql";
    private final String API_PATH
            = "/acl/" + Constraints.REST_API_VERSION + "/resource";

    private static DatabaseActions jdbcActions = new JdbcActions();
    private static HttpServer server;
    private static WebTarget target;

    //<editor-fold defaultstate="collapsed" desc="Test Preparation">
    @BeforeAll
    static void setUp() {
        Assumptions.assumeTrue(jdbcActions.connect("test"), "JDBC DBMS Connection faild.");

        try {
            server = EmbeddedGrizzly.startServer();
            ClientConfig config = new ClientConfig();
            Client client = ClientBuilder.newClient(config);
            target = client.target(EmbeddedGrizzly.BASE_URI);
        } catch (Exception ex) {
            LOGGER.catchException(ex);
        }
        Assumptions.assumeTrue(server.isStarted(), "Starting Grizzly Server faild.");
    }

    @AfterAll
    static void tearDown() {
        server.shutdownNow();
    }

    @BeforeEach
    void testCaseInitialization() {
        jdbcActions.executeSqlFromClasspath(FILE);
    }

    @AfterEach
    void testCaseTermination() throws Exception {
        jdbcActions.executeQuery(FLUSH_TABLE);
    }
    //</editor-fold>

    @Test
    void getResourceByDefaultStatus200() throws Exception {
        LOGGER.log("TEST CASE: getResourceByDefault() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/Article")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());

        JsonTools<ResourcesDO> json = new JacksonJsonTools<>();
        ResourcesDO object = json.deserializeJsonAsObject(
                response.readEntity(String.class), ResourcesDO.class);
        assertEquals("Articledefault", object.getName() + object.getView());
    }

    @Test
    void getResourceStatus200() throws Exception {
        LOGGER.log("TEST CASE: getResource() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/Article").queryParam("resourceView", "teaser")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());

        JsonTools<ResourcesDO> json = new JacksonJsonTools<>();
        ResourcesDO object = json.deserializeJsonAsObject(
                response.readEntity(String.class), ResourcesDO.class);
        assertEquals("Articleteaser", object.getName() + object.getView());
    }

    @Test
    void getResourceStatus404() {
        LOGGER.log("TEST CASE: getResource() 404 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/NotExist")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    void getResourcebyDefaultStatus404() {
        LOGGER.log("TEST CASE: getResource() 404 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/NotExist").queryParam("resourceView", "notExist")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    @Disabled
    void getResourceStatus403() {
        LOGGER.log("TEST CASE: getResource() 403 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/forbidden")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(403, response.getStatus());
    }

    @Test
    void getProtectedResources() throws Exception {
        LOGGER.log("TEST CASE: getProtectedResources() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/list/protected")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());

        JsonTools<ResourcesDO> json = new JacksonJsonTools<>();
        List<ResourcesDO> list = json.deserializeJsonAsList(
                response.readEntity(String.class));
        assertEquals(2, list.size());
    }

    @Test
    void getAllResources() {
        LOGGER.log("TEST CASE: getAllResources() 200 : OK", LogLevel.DEBUG);
        Response response = target
                .path(API_PATH).path("/list")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());
    }

    @Test
    void getAllResourcesOfSameType() throws Exception {
        LOGGER.log("TEST CASE: getAllResourcesOfSameType() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/list/equalType").path("/Article")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());

        JsonTools<ResourcesDO> json = new JacksonJsonTools<>();
        List<ResourcesDO> list = json.deserializeJsonAsList(
                response.readEntity(String.class));
        assertEquals(3, list.size());
    }

    @Test
    void getAllResourcesOfSameType404() {
        LOGGER.log("TEST CASE: getAllResourcesOfSameType() 404 : NOT FOUND", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/list/equalType").path("/NotExist")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    void updateResourcesStatus202() throws Exception {
        LOGGER.log("TEST CASE: updateResources() 202 : ACCEPTED", LogLevel.DEBUG);

        ResourcesDO resource = new ResourcesDO("Article");
        resource.setView("teaser");
        resource.setDeleteable(false);

        Response response = target
                .path(API_PATH)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(resource));

        assertEquals(202, response.getStatus());
    }

    @Test
    void updateResourcesStatus404() {
        LOGGER.log("TEST CASE: updateResources() 404 : NOT EXIST", LogLevel.DEBUG);

        ResourcesDO resource = new ResourcesDO("NotExist");

        Response response = target
                .path(API_PATH)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(resource));

        assertEquals(404, response.getStatus());
    }

    @Test
    void deleteResourceStatus410() {
        LOGGER.log("TEST CASE: deleteResource() 410 : DELETE", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/Article")
                .queryParam("resourceView", "teaser")
                .request(MediaType.APPLICATION_JSON)
                .delete(Response.class);

        assertEquals(409, response.getStatus());
    }

    @Test
    void deleteResourceStatus409() {
        LOGGER.log("TEST CASE: deleteResource() 409 : CONFLICT", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/Article")
                .queryParam("resourceView", "teaser")
                .request(MediaType.APPLICATION_JSON)
                .delete(Response.class);

        assertEquals(409, response.getStatus());
    }

    @Test
    void deleteResourcesStatus403() {
        LOGGER.log("TEST CASE: deleteResources() 403 : FORBIDDEN", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/Document")
                .queryParam("resourceView", "default")
                .request(MediaType.APPLICATION_JSON)
                .delete(Response.class);

        assertEquals(403, response.getStatus());
    }

    @Test
    void deleteResourceStatus404() {
        LOGGER.log("TEST CASE: deleteResource() 404 : NOT FOUND", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/NotExist")
                .queryParam("resourceView", "default")
                .request()
                .delete(Response.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    void createResourcesStatus201() {
        LOGGER.log("TEST CASE: createResources() 201 : CREATED", LogLevel.DEBUG);

        ResourcesDO resource = new ResourcesDO();
        resource.setName("CREATE");
        resource.setView("default");
        resource.setDeleteable(true);

        Response response = target
                .path(API_PATH)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(resource));

        assertEquals(201, response.getStatus());

        response = target
                .path(API_PATH).path("/CREATE")
                .queryParam("resourceView", "default")
                .request(MediaType.APPLICATION_JSON)
                .delete(Response.class);

        assertEquals(410, response.getStatus());
    }
}
