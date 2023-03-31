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
public class RoleServiceIT {

    private static final Logger LOGGER = new LogbackLogger(RoleServiceIT.class);

    private static final String FLUSH_TABLE
            = "TRUNCATE " + RolesDO.TABLE_NAME + ", "
            + AccountDO.TABLE_NAME + ", "
            + LoginDO.TABLE_NAME + ", "
            + PermissionDO.TABLE_NAME + ", "
            + ResourcesDO.TABLE_NAME + ";";
    private static final String FILE
            = "org/europa/together/sql/acl/testdata_ACL.sql";
    private final String API_PATH
            = "/acl/" + Constraints.REST_API_VERSION + "/role";

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
    void getRoleStatus200() throws Exception {
        LOGGER.log("TEST CASE: getRole() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/User")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());

        JsonTools<RolesDO> json = new JacksonJsonTools<>();
        RolesDO object = json.deserializeJsonAsObject(
                response.readEntity(String.class), RolesDO.class);
        assertEquals("User", object.getName());
    }

    @Test
    void getRoleStatus404() {
        LOGGER.log("TEST CASE: getRole() 404 : NOT FOUND", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/NotExist")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    @Disabled
    void getRoleStatus403() {
        LOGGER.log("TEST CASE: getRole() 401 : UNATHORIZED", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/forbidden")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(403, response.getStatus());
    }

    @Test
    void getProtectedRoles() throws Exception {
        LOGGER.log("TEST CASE: getProtectedRoles() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/list/protected")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());

        JsonTools<RolesDO> json = new JacksonJsonTools<>();
        List<RolesDO> list = json.deserializeJsonAsList(
                response.readEntity(String.class));
        assertEquals(3, list.size());
    }

    @Test
    void getAllRoles() throws Exception {
        LOGGER.log("TEST CASE: getAllRoles() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/list")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());

        JsonTools<RolesDO> json = new JacksonJsonTools<>();
        List<RolesDO> list = json.deserializeJsonAsList(
                response.readEntity(String.class));
        assertEquals(6, list.size());
    }

    @Test
    void deleteRoleStatus410() throws Exception {
        LOGGER.log("TEST CASE: deleteRoles() 410 : GONE", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/Temp")
                .request()
                .delete(Response.class);

        assertEquals(410, response.getStatus());
    }

    @Test
    void deleteRoleStatus409() throws Exception {
        LOGGER.log("TEST CASE: deleteRoles() 409 : CONFLICT", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/Sample")
                .request()
                .delete(Response.class);

        assertEquals(409, response.getStatus());
    }

    @Test
    void deleteRoleStatus403() throws Exception {
        LOGGER.log("TEST CASE: deleteRoles() 403 : FORBIDDEN", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/Guest")
                .request()
                .delete(Response.class);

        assertEquals(403, response.getStatus());
    }

    @Test
    void deleteRoleStatus404() throws Exception {
        LOGGER.log("TEST CASE: deleteRoles() 404 : NOT FOUND", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/NotExist")
                .request()
                .delete(Response.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    void createRoleStatus201() {
        LOGGER.log("TEST CASE: createRole() 201 : CREATED", LogLevel.DEBUG);

        Response response;

        RolesDO role = new RolesDO("Rest");
        role.setDeleteable(true);
        role.setDescription("Lorem ispsum.");

        response = target
                .path(API_PATH)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(role));

        assertEquals(201, response.getStatus());

        response = target
                .path(API_PATH).path("/Rest")
                .request()
                .delete(Response.class);

        assertEquals(410, response.getStatus());
    }

    @Test
    void updateRoleStatus202() {
        LOGGER.log("TEST CASE: updateRole() 202 : ACCEPTED", LogLevel.DEBUG);

        RolesDO role = new RolesDO("Moderator");
        role.setDescription("Update toLorem ispsum.");

        Response response = target
                .path(API_PATH)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(role));

        assertEquals(202, response.getStatus());
    }

    @Test
    void updateRoleStatus404() {
        LOGGER.log("TEST CASE: updateRole() 404 : NOT FOUND", LogLevel.DEBUG);

        RolesDO role = new RolesDO("NotExist");

        Response response = target
                .path(API_PATH)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(role));

        assertEquals(404, response.getStatus());
    }
}
