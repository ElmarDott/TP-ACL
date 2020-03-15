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
import org.europa.together.domain.acl.RolesDO;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@SuppressWarnings("unchecked")
@RunWith(JUnitPlatform.class)
public class RoleServiceIT {

    private static final Logger LOGGER = new LogbackLogger(RoleServiceIT.class);
    private static DatabaseActions actions = new JdbcActions(true);
    private static final String FLUSH_TABLES = "TRUNCATE ROLES, ACCOUNT, LOGIN, PERMISSIONS, RESOURCES;";
    private static final String FILE = "org/europa/together/sql/acl/testdata_ACL.sql";

    private static HttpServer server;
    private static WebTarget target;

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
    void testGetRoleStatus200() {
        LOGGER.log("TEST CASE: getRole() 200 : OK", LogLevel.DEBUG);

        String json = "{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":false,\"description\":\"After a simple validation process of an existing EMail address.\",\"name\":\"User\"}";
        Response response = target
                .path("/acl").path("/role").path("/User")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());
        assertEquals(json, response.readEntity(String.class));
    }

    @Test
    void testGetRoleStatus404() {
        LOGGER.log("TEST CASE: getRole() 404 : NOT FOUND", LogLevel.DEBUG);

        Response response = target
                .path("/acl").path("/role").path("/NotExist")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    @Disabled
    void testGetRoleStatus403() {
        LOGGER.log("TEST CASE: getRole() 401 : UNATHORIZED", LogLevel.DEBUG);

        Response response = target
                .path("/acl").path("/role").path("/Guest")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(403, response.getStatus());
    }

    @Test
    void testGetProtectedRoles() {
        LOGGER.log("TEST CASE: getProtectedRoles() 200 : OK", LogLevel.DEBUG);

        String json = "{\"list\": [\n"
                + "{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":false,\"description\":\"Default Role for all new Users.\",\"name\":\"Guest\"}\n"
                + "{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":false,\"description\":\"After a simple validation process of an existing EMail address.\",\"name\":\"User\"}\n"
                + "{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":false,\"description\":\"Full privilege.\",\"name\":\"Administrator\"}\n"
                + "]}";
        Response response = target
                .path("/acl").path("/role").path("/protected")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());
        assertEquals(json, response.readEntity(String.class));
    }

    @Test
    void testGetAllRoles() {
        LOGGER.log("TEST CASE: getAllRoles() 200 : OK", LogLevel.DEBUG);
        String json = "{\"list\": [\n"
                + "{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":true,\"description\":\"Role to test delete when Permission exist.\",\"name\":\"Sample\"}\n"
                + "{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":true,\"description\":\"Role to test delete when Permission exist.\",\"name\":\"Temp\"}\n"
                + "{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":false,\"description\":\"Default Role for all new Users.\",\"name\":\"Guest\"}\n"
                + "{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":false,\"description\":\"After a simple validation process of an existing EMail address.\",\"name\":\"User\"}\n"
                + "{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":true,\"description\":\"Higher privileged role than a standard user.\",\"name\":\"Moderator\"}\n"
                + "{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":false,\"description\":\"Full privilege.\",\"name\":\"Administrator\"}\n"
                + "]}";
        Response response = target
                .path("/acl").path("/role")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());
        assertEquals(json, response.readEntity(String.class));
    }

    @Test
    void testDeleteRoleStatus410() {
        LOGGER.log("TEST CASE: deleteRoles() 410 : DELETE", LogLevel.DEBUG);

        Response response = target
                .path("/acl").path("/role").path("/Temp")
                .request()
                .delete(Response.class);

        assertEquals(410, response.getStatus());
    }

    @Test
    void testDeleteRoleStatus409() {
        LOGGER.log("TEST CASE: deleteRoles() 409 : CONFLICT", LogLevel.DEBUG);

        Response response = target
                .path("/acl").path("/role").path("/Sample")
                .request()
                .delete(Response.class);

        assertEquals(409, response.getStatus());
    }

    @Test
    void testDeleteRoleStatus403() {
        LOGGER.log("TEST CASE: deleteRoles() 403 : FORBIDDEN", LogLevel.DEBUG);

        Response response = target
                .path("/acl").path("/role").path("/Guest")
                .request()
                .delete(Response.class);

        assertEquals(403, response.getStatus());
    }

    @Test
    void testDeleteRoleStatus404() {
        LOGGER.log("TEST CASE: deleteRoles() 404 : NOT FOUND", LogLevel.DEBUG);

        Response response = target
                .path("/acl").path("/role").path("/NotExist")
                .request()
                .delete(Response.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    void testCreateRoleStatus201() {
        LOGGER.log("TEST CASE: createRole() 201 : CREATED", LogLevel.DEBUG);

        Response response;

        RolesDO role = new RolesDO("Rest");
        role.setDeleteable(true);
        role.setDescription("Lorem ispsum.");

        response = target
                .path("/acl").path("/role")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(role));

        assertEquals(201, response.getStatus());

        response = target
                .path("/acl").path("/role").path("/Rest")
                .request()
                .delete(Response.class);

        assertEquals(410, response.getStatus());
    }

    @Test
    void testUpdateRoleStatus202() {
        LOGGER.log("TEST CASE: updateRole() 202 : ACCEPTED", LogLevel.DEBUG);

        RolesDO role = new RolesDO("Moderator");
        role.setDescription("Update toLorem ispsum.");

        Response response = target
                .path("/acl").path("/role")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(role));

        assertEquals(202, response.getStatus());
    }

    @Test
    void testUpdateRoleStatus404() {
        LOGGER.log("TEST CASE: updateRole() 404 : NOT FOUND", LogLevel.DEBUG);

        RolesDO role = new RolesDO("NotExist");

        Response response = target
                .path("/acl").path("/role")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(role));

        assertEquals(404, response.getStatus());
    }
}
