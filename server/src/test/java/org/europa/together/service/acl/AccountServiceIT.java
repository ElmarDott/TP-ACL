package org.europa.together.service.acl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class AccountServiceIT {

    private static final Logger LOGGER = new LogbackLogger(AccountServiceIT.class);

    private static final String FLUSH_TABLE
            = "TRUNCATE " + RolesDO.TABLE_NAME + ", "
            + AccountDO.TABLE_NAME + ", "
            + LoginDO.TABLE_NAME + ", "
            + PermissionDO.TABLE_NAME + ", "
            + ResourcesDO.TABLE_NAME + ";";
    private static final String FILE
            = "org/europa/together/sql/acl/testdata_ACL.sql";
    private static final String API_PATH
            = "/acl/" + Constraints.REST_API_VERSION + "/account";

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
        server.shutdownNow();
        LOGGER.log("TEST CASE TERMINATED.", LogLevel.TRACE);
    }

    @BeforeEach
    void testCaseInitialization() {
        jdbcActions.executeSqlFromClasspath(FILE);
    }

    @AfterEach
    void testCaseTermination() throws Exception {
        jdbcActions.executeQuery(FLUSH_TABLE);
        LOGGER.log("TEST CASE TERMINATED.", LogLevel.TRACE);
    }
    //</editor-fold>

    @Test
    void getAccountStatus200() throws Exception {
        LOGGER.log("TEST CASE: getAccount() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/admin@sample.org")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());

        ObjectMapper mapper = new ObjectMapper();
        AccountDO object = mapper.readValue(response.readEntity(String.class), AccountDO.class);
        assertEquals("admin@sample.org", object.getEmail());
    }

    @Test
    void getAccountStatus404() {
        LOGGER.log("TEST CASE: getAccount() 404 : NOT FOUND", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/NotExist")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    @Disabled
    void getAccountStatus403() {
        LOGGER.log("TEST CASE: getAccount() 403 : UNATHORIZED", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/forbidden")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(403, response.getStatus());
    }

    @Test
    void getAllAccount() throws Exception {
        LOGGER.log("TEST CASE: getAllAccount() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/list")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());

        ObjectMapper mapper = new ObjectMapper();
        AccountDO[] list = mapper.readValue(response.readEntity(String.class), AccountDO[].class);
        assertEquals(6, list.length);
    }

    @Test
    void getLoginsOfAccount() throws Exception {
        LOGGER.log("TEST CASE: getLoginsOfAccount() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/list/logins").path("/moderator@sample.org")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());

        ObjectMapper mapper = new ObjectMapper();
        LoginDO[] list = mapper.readValue(response.readEntity(String.class), LoginDO[].class);
        assertEquals(3, list.length);
    }

    @Test
    void getDeactivatedAccount() throws Exception {
        LOGGER.log("TEST CASE: getDeactivatedAccount() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/list/deactivated")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());

        ObjectMapper mapper = new ObjectMapper();
        List<AccountDO> list = mapper.readValue(response.readEntity(String.class), new TypeReference<List<AccountDO>>() {
        });
        assertEquals(3, list.size());
    }

    @Test
    void getActivatedAccount() throws Exception {
        LOGGER.log("TEST CASE: getActivatedAccount() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/list/activated")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());

        ObjectMapper mapper = new ObjectMapper();
        AccountDO[] list = mapper.readValue(response.readEntity(String.class), AccountDO[].class);
        assertEquals(3, list.length);
    }

    @Test
    void getNotConfirmedAccount() throws Exception {
        LOGGER.log("TEST CASE: getNotConfirmedAccount() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/list/not-confirmed")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());

        ObjectMapper mapper = new ObjectMapper();
        AccountDO[] list = mapper.readValue(response.readEntity(String.class), AccountDO[].class);
        assertEquals(1, list.length);
    }

    @Test
    void getAccountsOfRole() throws Exception {
        LOGGER.log("TEST CASE: getAccountsOfRole() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/list").path("/Moderator")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());

        ObjectMapper mapper = new ObjectMapper();
        AccountDO[] list = mapper.readValue(response.readEntity(String.class), AccountDO[].class);
        assertEquals(3, list.length);
    }

    @Test
    void deleteAccountStatus410() {
        LOGGER.log("TEST CASE: deleteAccount() 410 : DELETE", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/moderator_02@sample.org")
                .request()
                .delete(Response.class);

        assertEquals(410, response.getStatus());
    }

    @Test
    void deleteAccountStatus409() {
        LOGGER.log("TEST CASE: deleteAccount() 409 : CONFLICT", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/moderator@sample.org")
                .request()
                .delete(Response.class);

        assertEquals(409, response.getStatus());
    }

    @Test
    void deleteAccountStatus404() {
        LOGGER.log("TEST CASE: deleteAccount() 404 : NOT FOUND", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/NotExist@sample.com")
                .request()
                .delete(Response.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    void deactivateAccount() {
        LOGGER.log("TEST CASE: deactivateAccount() 202 : ACCEPTED", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/deactivate/").path("/moderator_02@sample.org")
                .request(MediaType.TEXT_PLAIN)
                .put(Entity.text("moderator_02@sample.org"));

        assertEquals(202, response.getStatus());
    }

    @Test
    void verifyAccount() {
        LOGGER.log("TEST CASE: verifyAccount() 202 : ACCEPTED", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/verify/").path("/user@sample.org")
                .request(MediaType.TEXT_PLAIN)
                .put(Entity.text("moderator_02@sample.org"));

        assertEquals(202, response.getStatus());
    }

    @Test
    void createAccountStatus201() {
        LOGGER.log("TEST CASE: createAccount() 201 : CREATED", LogLevel.DEBUG);

        AccountDO account = new AccountDO("new@sample.org");
        LOGGER.log(account.toString(), LogLevel.DEBUG);

        Response response = target
                .path(API_PATH)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(account));

        LOGGER.log("RESPONSE: " + response.getHeaders().toString(), LogLevel.DEBUG);
        assertEquals(201, response.getStatus());

        response = target
                .path(API_PATH).path("/new@sample.org")
                .request()
                .delete(Response.class);

        assertEquals(410, response.getStatus());
    }

    @Test
    void updateAccountStatus202() {
        LOGGER.log("TEST CASE: updateAccount() 202 : ACCEPTED", LogLevel.DEBUG);

        AccountDO account = new AccountDO("user@sample.org");
        account.setDefaultLocale("DE_de");
        account.setVerificationCode("e0acf91e-e44d-4d41-8134-f9b1eb5d2412");
        LOGGER.log(account.toString(), LogLevel.DEBUG);

        Response response = target
                .path(API_PATH)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(account));

        assertEquals(202, response.getStatus());
    }

    @Test
    void updateAccountStatus404() {
        LOGGER.log("TEST CASE: updateAccount() 404 : NOT FOUND", LogLevel.DEBUG);

        AccountDO account = new AccountDO("not.exist@sample.org");

        Response response = target
                .path(API_PATH)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(account));

        assertEquals(404, response.getStatus());
    }
}
