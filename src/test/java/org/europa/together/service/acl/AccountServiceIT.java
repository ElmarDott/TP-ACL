package org.europa.together.service.acl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
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
import org.europa.together.domain.acl.AccountDO;
import org.europa.together.domain.acl.LoginDO;
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
public class AccountServiceIT {

    private static final Logger LOGGER = new LogbackLogger(AccountServiceIT.class);
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
    void testGetAccountStatus200() throws JsonProcessingException {
        LOGGER.log("TEST CASE: getAccount() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/account").path("/admin@sample.org")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        ObjectMapper mapper = new ObjectMapper();
        AccountDO object = mapper.readValue(response.readEntity(String.class), AccountDO.class);

        assertEquals(200, response.getStatus());
        assertEquals("admin@sample.org", object.getEmail());
    }

    @Test
    void testGetAccountStatus404() {
        LOGGER.log("TEST CASE: getAccount() 404 : NOT FOUND", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/account").path("/NotExist")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    @Disabled
    void testGetAccountStatus403() {
        LOGGER.log("TEST CASE: getAccount() 403 : UNATHORIZED", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/account").path("/")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(403, response.getStatus());
    }

    @Test
    void testGetAllAccount() throws JsonProcessingException {
        LOGGER.log("TEST CASE: getAllAccount() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/account/list")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        ObjectMapper mapper = new ObjectMapper();
        AccountDO[] list = mapper.readValue(response.readEntity(String.class), AccountDO[].class);

        assertEquals(200, response.getStatus());
        assertEquals(5, list.length);
    }

    @Test
    void testGetLoginsOfAccount() throws JsonProcessingException {
        LOGGER.log("TEST CASE: getLoginsOfAccount() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/account").path("/list/logins").path("/moderator@sample.org")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        ObjectMapper mapper = new ObjectMapper();
        LoginDO[] list = mapper.readValue(response.readEntity(String.class), LoginDO[].class);

        assertEquals(200, response.getStatus());
        assertEquals(3, list.length);
    }

    @Test
    void testGetDeactivatedAccount() throws JsonProcessingException {
        LOGGER.log("TEST CASE: getDeactivatedAccount() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/account").path("/list/deactivated")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        ObjectMapper mapper = new ObjectMapper();
        List<AccountDO> list = mapper.readValue(response.readEntity(String.class), new TypeReference<List<AccountDO>>() {
        });

        assertEquals(200, response.getStatus());
        assertEquals(3, list.size());
    }

    @Test
    void testGetActivatedAccount() throws JsonProcessingException {
        LOGGER.log("TEST CASE: getActivatedAccount() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/account").path("/list/activated")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        ObjectMapper mapper = new ObjectMapper();
        AccountDO[] list = mapper.readValue(response.readEntity(String.class), AccountDO[].class);

        assertEquals(200, response.getStatus());
        assertEquals(3, list.length);
    }

    @Test
    void testGetNotConfirmedAccount() throws JsonProcessingException {
        LOGGER.log("TEST CASE: getNotConfirmedAccount() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/account").path("/list/not-confirmed")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        ObjectMapper mapper = new ObjectMapper();
        AccountDO[] list = mapper.readValue(response.readEntity(String.class), AccountDO[].class);

        assertEquals(200, response.getStatus());
        assertEquals(1, list.length);
    }

    @Test
    void testGetAccountsOfRole() throws JsonProcessingException {
        LOGGER.log("TEST CASE: getAccountsOfRole() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/account/list").path("/Moderator")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        ObjectMapper mapper = new ObjectMapper();
        AccountDO[] list = mapper.readValue(response.readEntity(String.class), AccountDO[].class);

        assertEquals(200, response.getStatus());
        assertEquals(2, list.length);
    }

    @Test
    void testDeleteAccountStatus410() {
        LOGGER.log("TEST CASE: deleteAccount() 410 : DELETE", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/account").path("/moderator_02@sample.org")
                .request()
                .delete(Response.class);

        assertEquals(410, response.getStatus());
    }

    @Test
    void testDeleteAccountStatus409() {
        LOGGER.log("TEST CASE: deleteAccount() 409 : CONFLICT", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/account").path("/moderator@sample.org")
                .request()
                .delete(Response.class);

        assertEquals(409, response.getStatus());
    }

    @Test
    void testDeleteAccountStatus404() {
        LOGGER.log("TEST CASE: deleteAccount() 404 : NOT FOUND", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/account").path("/NotExist")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .delete(Response.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    void testDeactivateAccount() {
        LOGGER.log("TEST CASE: deactivateAccount() 202 : ACCEPTED", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/account/deactivate/").path("/moderator_02@sample.org")
                .request(MediaType.TEXT_PLAIN)
                .put(Entity.text("moderator_02@sample.org"));

        assertEquals(202, response.getStatus());
    }

    @Test
    void testVerifyAccount() {
        LOGGER.log("TEST CASE: verifyAccount() 202 : ACCEPTED", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/account/verify/").path("/user@sample.org")
                .request(MediaType.TEXT_PLAIN)
                .put(Entity.text("moderator_02@sample.org"));

        assertEquals(202, response.getStatus());
    }

    @Test
    void testCreateAccountStatus201() {
        LOGGER.log("TEST CASE: createAccount() 201 : CREATED", LogLevel.DEBUG);

        Response response;
        AccountDO account = new AccountDO("new@sample.org");
        LOGGER.log(account.toString(), LogLevel.DEBUG);

        response = target
                .path(API_PATH).path("/account")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(account));

        LOGGER.log("RESPONSE: " + response.getHeaders().toString(), LogLevel.DEBUG);
        assertEquals(201, response.getStatus());

        response = target
                .path(API_PATH).path("/account").path("/new@sample.org")
                .request()
                .delete(Response.class);

        assertEquals(410, response.getStatus());
    }

    @Test
    void testUpdateAccountStatus202() {

        LOGGER.log("TEST CASE: updateAccount() 202 : ACCEPTED", LogLevel.DEBUG);

        AccountDO account = new AccountDO("user@sample.org");
        account.setDefaultLocale("DE_de");
        account.setVerificationCode("e0acf91e-e44d-4d41-8134-f9b1eb5d2412");
        LOGGER.log(account.toString(), LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/account")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(account));

        assertEquals(202, response.getStatus());
    }

    @Test
    void testUpdateAccountStatus404() {
        LOGGER.log("TEST CASE: updateAccount() 404 : NOT FOUND", LogLevel.DEBUG);

        AccountDO account = new AccountDO("not.exist@sample.org");

        Response response = target
                .path(API_PATH).path("/account")
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(account));

        assertEquals(404, response.getStatus());
    }
}
