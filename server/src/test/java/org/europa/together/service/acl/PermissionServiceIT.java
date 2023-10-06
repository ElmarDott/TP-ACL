package org.europa.together.service.acl;

import java.util.List;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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
import org.europa.together.domain.acl.PermissionId;
import org.europa.together.domain.acl.ResourcesDO;
import org.europa.together.domain.acl.RolesDO;
import org.europa.together.utils.acl.Constraints;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
public class PermissionServiceIT {

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
            = "/acl/" + Constraints.REST_API_VERSION + "/permission";

    private static DatabaseActions jdbcActions = new JdbcActions();
    private static HttpServer server;
    private static WebTarget target;

    //<editor-fold defaultstate="collapsed" desc="Test Preparation">
    @BeforeAll
    static void setUp() {
        Assumptions.assumeTrue(jdbcActions.connect("test"), "JDBC DBMS Connection failed.");

        try {
            server = EmbeddedGrizzly.startServer();
            ClientConfig config = new ClientConfig();
            Client client = ClientBuilder.newClient(config);
            target = client.target(EmbeddedGrizzly.BASE_URI);
        } catch (Exception ex) {
            LOGGER.catchException(ex);
        }
        Assumptions.assumeTrue(server.isStarted(), "Starting Grizzly Server failed.");
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
    void getPermissionStatus200() throws Exception {
        LOGGER.log("TEST CASE: getPermission() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/1f4c2b42-4408-4f99-b1aa-25002b85ea87")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());

        JsonTools<PermissionDO> json = new JacksonJsonTools<>();
        PermissionDO permission = json.deserializeJsonAsObject(
                response.readEntity(String.class), PermissionDO.class);
        assertEquals("1f4c2b42-4408-4f99-b1aa-25002b85ea87", permission.getUuid());
    }

    @Test
    void getPermissionStatus404() {
        LOGGER.log("TEST CASE: getPermission() 404 : NOT FOUND", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/NotExist")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    @Disabled
    void getPermissionStatus403() {
        LOGGER.log("TEST CASE: getPermission() 401 : UNATHORIZED", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/forbidden")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(403, response.getStatus());
    }

    @Test
    void getAllPermissions() {
        LOGGER.log("TEST CASE: getAllPermissions() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/list")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());
    }

    @Test
    void getAllPermissionOfARoleStatus200() throws Exception {
        LOGGER.log("TEST CASE: getAllPermissionOfARole() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/list/forRole").path("/User")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());

        JsonTools<PermissionDO> json = new JacksonJsonTools<>();
        List<PermissionDO> list = json.deserializeJsonAsList(
                response.readEntity(String.class));
        assertEquals(2, list.size());
    }

    @Test
    void getAllPermissionOfARoleStatus404() {
        LOGGER.log("TEST CASE: getAllPermissionOfARole() 404 : NOT FOUND", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/list/forRole").path("/NotExist")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    void updatePermissionStatus202() {
        LOGGER.log("TEST CASE: updatePermission() 202 : ACCEPTED", LogLevel.DEBUG);

        Response find = target
                .path(API_PATH).path("/9b5e3e50-4fdb-46de-bbed-8e011d35cfe8")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        PermissionDO permission = find.readEntity(PermissionDO.class);
        permission.setChange(false);
        permission.setCreate(false);
        permission.setDelete(false);
        permission.setRead(false);

        LOGGER.log(permission.toString(), LogLevel.DEBUG);

        Response update = target
                .path(API_PATH)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(permission));

        assertEquals(202, update.getStatus());
    }

    @Test
    void updatePermissionStatus404() {
        LOGGER.log("TEST CASE: updatePermission() 404 : NOT FOUND", LogLevel.DEBUG);

        PermissionDO permission = new PermissionDO();
        permission.setPermissionId(new PermissionId(new ResourcesDO("NotExist"), new RolesDO("any")));

        Response response = target
                .path(API_PATH)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(permission));

        assertEquals(404, response.getStatus());
    }

    @Test
    void deletePermissionStatus410() {
        LOGGER.log("TEST CASE: deletePermission() 410 : DELETE", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/eb7bb730-95a0-45ac-983c-258b7a56f1f4")
                .request()
                .delete(Response.class);

        assertEquals(410, response.getStatus());
    }

    @Test
    void deletePermissionStatus404() {
        LOGGER.log("TEST CASE: deletePermission() 404 : NOT FOUND", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/NotExist")
                .request()
                .delete(Response.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    void createPermissionStatus201() {
        LOGGER.log("TEST CASE: createPermission() 201 : CREATED", LogLevel.DEBUG);

        ResourcesDO resource = new ResourcesDO("Sample");
        resource.setView("update");
        PermissionDO permission = new PermissionDO(new PermissionId(
                resource, new RolesDO("Temp")
        ));
        permission.setChange(true);
        permission.setCreate(true);
        permission.setDelete(true);
        permission.setRead(true);

        Response response = target
                .path(API_PATH)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(permission));

        assertEquals(201, response.getStatus());

        response = target
                .path(API_PATH).path("/" + permission.getUuid())
                .request()
                .delete(Response.class);

        assertEquals(410, response.getStatus());
    }
}
