package org.europa.together.service.acl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.europa.together.business.acl.ResourcesDAO;
import org.europa.together.business.acl.RolesDAO;
import org.europa.together.domain.LogLevel;
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
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@SuppressWarnings("unchecked")
@RunWith(JUnitPlatform.class)
public class PermissionServiceIT {

    private static final Logger LOGGER = new LogbackLogger(ResourceServiceIT.class);
    private static DatabaseActions actions = new JdbcActions(true);
    private static final String FLUSH_TABLES = "TRUNCATE ROLES, ACCOUNT, LOGIN, PERMISSIONS, RESOURCES;";
    private static final String FILE = "org/europa/together/sql/acl/testdata_ACL.sql";

    private static HttpServer server;
    private static WebTarget target;
    private final String API_PATH
            = Constraints.MODULE_NAME + "/" + Constraints.REST_API_VERSION;

    @Autowired
    @Qualifier("rolesHbmDAO")
    private RolesDAO rolesDAO;

    @Autowired
    @Qualifier("resourcesHbmDAO")
    private ResourcesDAO resourcesDAO;

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
    void testGetPermissionStatus200() throws JsonProcessingException {
        LOGGER.log("TEST CASE: getPermission() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/permission").path("/1f4c2b42-4408-4f99-b1aa-25002b85ea87")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        PermissionDO permission = response.readEntity(PermissionDO.class);

        assertEquals(200, response.getStatus());
        assertEquals("1f4c2b42-4408-4f99-b1aa-25002b85ea87", permission.getUuid());
    }

    @Test
    void testGetPermissionStatus404() {
        LOGGER.log("TEST CASE: getPermission() 404 : NOT FOUND", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/permission").path("/NotExist")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    @Disabled
    void testGetPermissionStatus403() {
        LOGGER.log("TEST CASE: getPermission() 401 : UNATHORIZED", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/role").path("/Guest")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(403, response.getStatus());
    }

    @Test
    void testGetAllPermissions() {
        LOGGER.log("TEST CASE: getAllPermissions() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/permission").path("/all")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());
    }

    @Test
    void testGetAllPermissionOfARoleStatus200() throws JsonProcessingException {
        LOGGER.log("TEST CASE: getAllPermissionOfARole() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/permission").path("/forRole").path("/User")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        ObjectMapper mapper = new ObjectMapper();
        PermissionDO[] list = mapper.readValue(response.readEntity(String.class), PermissionDO[].class);

        assertEquals(200, response.getStatus());
        assertEquals(2, list.length);
    }

    @Test
    void testGetAllPermissionOfARoleStatus404() {
        LOGGER.log("TEST CASE: getAllPermissionOfARole() 404 : NOT FOUND", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/permission").path("/forRole").path("/NotExist")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    void testUpdatePermissionStatus202() {
        LOGGER.log("TEST CASE: updatePermission() 202 : ACCEPTED", LogLevel.DEBUG);

        Response find = target
                .path(API_PATH).path("/permission").path("/9b5e3e50-4fdb-46de-bbed-8e011d35cfe8")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        PermissionDO permission = find.readEntity(PermissionDO.class);

        assertEquals("Sample", permission.getPermissionId().getRole().getName());

        permission.setChange(false);
        permission.setCreate(false);
        permission.setDelete(false);
        permission.setRead(false);

        Response update = target
                .path(API_PATH).path("/permission")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(permission));

        assertEquals(202, update.getStatus());
    }

    @Test
    void testUpdatePermissionStatus404() {
        LOGGER.log("TEST CASE: updatePermission() 404 : NOT FOUND", LogLevel.DEBUG);

        PermissionDO permission = new PermissionDO();
        permission.setPermissionId(new PermissionId(new ResourcesDO("NotExist"), new RolesDO("any")));

        Response response = target
                .path(API_PATH).path("/permission")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(permission));

        assertEquals(404, response.getStatus());
    }

    @Test
    void testDeletePermissionStatus410() {
        LOGGER.log("TEST CASE: deletePermission() 410 : DELETE", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/permission").path("/eb7bb730-95a0-45ac-983c-258b7a56f1f4")
                .request()
                .delete(Response.class);

        assertEquals(410, response.getStatus());
    }

    @Test
    void testDeletePermissionStatus404() {
        LOGGER.log("TEST CASE: deletePermission() 404 : NOT FOUND", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/permission").path("/NotExist")
                .request()
                .delete(Response.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    void testCreatePermissionStatus201() {
        LOGGER.log("TEST CASE: createPermission() 201 : CREATED", LogLevel.DEBUG);

        PermissionDO entry = new PermissionDO(new PermissionId(
                resourcesDAO.find("Sample", "default"),
                rolesDAO.find("Temp")
        ));
        entry.setChange(true);
        entry.setCreate(true);
        entry.setDelete(true);
        entry.setRead(true);

        LOGGER.log(entry.toString(), LogLevel.DEBUG);

        Response response;
        response = target
                .path(API_PATH).path("/permission")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(entry));

        assertEquals(201, response.getStatus());

//        response = target
//                .path(API_PATH).path("/permission").path("/" + permission.getPermissionId())
//                .request()
//                .delete(Response.class);
//
//        assertEquals(410, response.getStatus());
    }
}
