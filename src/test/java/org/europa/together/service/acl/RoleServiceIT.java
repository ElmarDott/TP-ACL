package org.europa.together.service.acl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.SQLException;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.europa.together.EmbeddedGrizzly;
import org.europa.together.application.JdbcActions;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.DatabaseActions;
import org.europa.together.business.Logger;
import org.europa.together.domain.LogLevel;
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
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SuppressWarnings("unchecked")
@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"/applicationContext.xml"})
public class RoleServiceIT {

    private static final Logger LOGGER = new LogbackLogger(RoleServiceIT.class);

    private static final String FLUSH_TABLE
            = "TRUNCATE ROLES, ACCOUNT, LOGIN, PERMISSIONS, RESOURCES;";
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
        Assumptions.assumeTrue(jdbcActions.connect("test"));

        try {
            server = EmbeddedGrizzly.startServer();
            ClientConfig config = new ClientConfig();
            Client client = ClientBuilder.newClient(config);
            target = client.target(EmbeddedGrizzly.BASE_URI);
        } catch (Exception ex) {
            LOGGER.catchException(ex);
        }
        Assumptions.assumeTrue(server.isStarted());

        LOGGER.log("### TEST SUITE INICIATED.", LogLevel.TRACE);
    }

    @AfterAll
    static void tearDown() {
        LOGGER.log("TEST CASE TERMINATED.", LogLevel.TRACE);
    }

    @BeforeEach
    void testCaseInitialization() {
        jdbcActions.executeSqlFromClasspath(FILE);
    }

    @AfterEach
    void testCaseTermination() throws SQLException {
        jdbcActions.executeQuery(FLUSH_TABLE);
        LOGGER.log("TEST CASE TERMINATED.", LogLevel.TRACE);
    }
    //</editor-fold>

    @Test
    void getRoleStatus200() throws JsonProcessingException {
        LOGGER.log("TEST CASE: getRole() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/role").path("/User")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        ObjectMapper mapper = new ObjectMapper();
        RolesDO object = mapper.readValue(response.readEntity(String.class), RolesDO.class);

        assertEquals(200, response.getStatus());
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
    void getProtectedRoles() throws JsonProcessingException {
        LOGGER.log("TEST CASE: getProtectedRoles() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/list/protected")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        ObjectMapper mapper = new ObjectMapper();
        List<RolesDO> list = mapper.readValue(response.readEntity(String.class), new TypeReference<List<RolesDO>>() {
        });

        assertEquals(200, response.getStatus());
        assertEquals(3, list.size());
    }

    @Test
    void getAllRoles() throws JsonProcessingException {
        LOGGER.log("TEST CASE: getAllRoles() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/list")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        ObjectMapper mapper = new ObjectMapper();
        List<RolesDO> list = mapper.readValue(response.readEntity(String.class), new TypeReference<List<RolesDO>>() {
        });

        assertEquals(200, response.getStatus());
        assertEquals(6, list.size());
    }

    @Test
    void deleteRoleStatus410() {
        LOGGER.log("TEST CASE: deleteRoles() 410 : DELETE", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/Temp")
                .request()
                .delete(Response.class);

        assertEquals(410, response.getStatus());
    }

    @Test
    void deleteRoleStatus409() {
        LOGGER.log("TEST CASE: deleteRoles() 409 : CONFLICT", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/Sample")
                .request()
                .delete(Response.class);

        assertEquals(409, response.getStatus());
    }

    @Test
    void deleteRoleStatus403() {
        LOGGER.log("TEST CASE: deleteRoles() 403 : FORBIDDEN", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/Guest")
                .request()
                .delete(Response.class);

        assertEquals(403, response.getStatus());
    }

    @Test
    void deleteRoleStatus404() {
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
