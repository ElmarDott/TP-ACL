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
import org.europa.together.domain.acl.AccountDO;
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
    void testGetAccountStatus200() {
        LOGGER.log("TEST CASE: getAccount() 200 : OK", LogLevel.DEBUG);

        String json = "{\"activated\":true,\"class\":\"org.europa.together.domain.acl.AccountDO\",\"defaultLocale\":null,\"defaultTimezone\":null,\"email\":\"admin@sample.org\",\"password\":\"\",\"registrationDate\":449668801000,\"role\":{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":false,\"description\":\"Full privilege.\",\"name\":\"Administrator\"},\"verificationCode\":\"2b421f4c-4408-4f99-b1aa-25002b85ea87\",\"verified\":true}";
        Response response = target
                .path(API_PATH).path("/account").path("/admin@sample.org")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());
        assertEquals(json, response.readEntity(String.class));
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
    void testGetAllAccount() {
        LOGGER.log("TEST CASE: getAllAccount() 200 : OK", LogLevel.DEBUG);
        String json = "{\"list\": [\n"
                + "{\"activated\":true,\"class\":\"org.europa.together.domain.acl.AccountDO\",\"defaultLocale\":null,\"defaultTimezone\":null,\"email\":\"guest@sample.org\",\"password\":\"\",\"registrationDate\":449668801000,\"role\":{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":false,\"description\":\"Default Role for all new Users.\",\"name\":\"Guest\"},\"verificationCode\":\"03a057d0-6ee7-43b1-ac68-d9a7681165a4\",\"verified\":true}\n"
                + "{\"activated\":false,\"class\":\"org.europa.together.domain.acl.AccountDO\",\"defaultLocale\":null,\"defaultTimezone\":null,\"email\":\"moderator@sample.org\",\"password\":\"\",\"registrationDate\":449668801000,\"role\":{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":true,\"description\":\"Higher privileged role than a standard user.\",\"name\":\"Moderator\"},\"verificationCode\":\"3644b892-1438-45e4-87c5-0ea140fea92b\",\"verified\":true}\n"
                + "{\"activated\":true,\"class\":\"org.europa.together.domain.acl.AccountDO\",\"defaultLocale\":null,\"defaultTimezone\":null,\"email\":\"moderator_01@sample.org\",\"password\":\"\",\"registrationDate\":449668801000,\"role\":{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":true,\"description\":\"Higher privileged role than a standard user.\",\"name\":\"Moderator\"},\"verificationCode\":\"46b25a22-69b6-43eb-a711-1dcf1ed0eb5e\",\"verified\":false}\n"
                + "{\"activated\":true,\"class\":\"org.europa.together.domain.acl.AccountDO\",\"defaultLocale\":null,\"defaultTimezone\":null,\"email\":\"admin@sample.org\",\"password\":\"\",\"registrationDate\":449668801000,\"role\":{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":false,\"description\":\"Full privilege.\",\"name\":\"Administrator\"},\"verificationCode\":\"2b421f4c-4408-4f99-b1aa-25002b85ea87\",\"verified\":true}\n"
                + "{\"activated\":false,\"class\":\"org.europa.together.domain.acl.AccountDO\",\"defaultLocale\":\"DE_de\",\"defaultTimezone\":\"UTC+00:00\",\"email\":\"user@sample.org\",\"password\":null,\"registrationDate\":1584856360708,\"role\":null,\"verificationCode\":\"e0acf91e-e44d-4d41-8134-f9b1eb5d2412\",\"verified\":false}\n"
                + "]}";

        Response response = target
                .path(API_PATH).path("/account/list")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());
        assertEquals(7, response.readEntity(String.class).split("\n").length);
    }

    @Test
    void testGetLoginsOfAccount() {
        LOGGER.log("TEST CASE: getLoginsOfAccount() 200 : OK", LogLevel.DEBUG);
        String json = "{\"list\": [\n"
                + "{\"account\":{\"activated\":false,\"class\":\"org.europa.together.domain.acl.AccountDO\",\"defaultLocale\":null,\"defaultTimezone\":null,\"email\":\"moderator@sample.org\",\"password\":\"\",\"registrationDate\":449668801000,\"role\":{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":true,\"description\":\"Higher privileged role than a standard user.\",\"name\":\"Moderator\"},\"verificationCode\":\"3644b892-1438-45e4-87c5-0ea140fea92b\",\"verified\":true},\"browserID\":\"\",\"class\":\"org.europa.together.domain.acl.LoginDO\",\"ipAddress\":\"127.0.0.1\",\"loginDate\":1277467201000,\"logout\":false,\"operationSystem\":\"\",\"uuid\":\"2b421f4c-4408-4f99-b1aa-25002b85ea87\"}\n"
                + "{\"account\":{\"activated\":false,\"class\":\"org.europa.together.domain.acl.AccountDO\",\"defaultLocale\":null,\"defaultTimezone\":null,\"email\":\"moderator@sample.org\",\"password\":\"\",\"registrationDate\":449668801000,\"role\":{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":true,\"description\":\"Higher privileged role than a standard user.\",\"name\":\"Moderator\"},\"verificationCode\":\"3644b892-1438-45e4-87c5-0ea140fea92b\",\"verified\":true},\"browserID\":\"\",\"class\":\"org.europa.together.domain.acl.LoginDO\",\"ipAddress\":\"localhost\",\"loginDate\":1276776001000,\"logout\":false,\"operationSystem\":\"\",\"uuid\":\"a674b42e-6670-4674-a8c7-8e86132eaaa2\"}\n"
                + "{\"account\":{\"activated\":false,\"class\":\"org.europa.together.domain.acl.AccountDO\",\"defaultLocale\":null,\"defaultTimezone\":null,\"email\":\"moderator@sample.org\",\"password\":\"\",\"registrationDate\":449668801000,\"role\":{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":true,\"description\":\"Higher privileged role than a standard user.\",\"name\":\"Moderator\"},\"verificationCode\":\"3644b892-1438-45e4-87c5-0ea140fea92b\",\"verified\":true},\"browserID\":\"\",\"class\":\"org.europa.together.domain.acl.LoginDO\",\"ipAddress\":\"127.0.0.1\",\"loginDate\":1276603201000,\"logout\":false,\"operationSystem\":\"\",\"uuid\":\"03a057d0-6ee7-43b1-ac68-d9a7681165a4\"}\n"
                + "]}";
        Response response = target
                .path(API_PATH).path("/account").path("/list/logins").path("/moderator@sample.org")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());
        assertEquals(json, response.readEntity(String.class));
    }

    @Test
    void testGetDeactivatedAccount() {
        LOGGER.log("TEST CASE: getDeactivatedAccount() 200 : OK", LogLevel.DEBUG);
        String json = "{\"list\": [\n"
                + "{\"activated\":false,\"class\":\"org.europa.together.domain.acl.AccountDO\",\"defaultLocale\":null,\"defaultTimezone\":null,\"email\":\"user@sample.org\",\"password\":\"\",\"registrationDate\":449668801000,\"role\":{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":false,\"description\":\"After a simple validation process of an existing EMail address.\",\"name\":\"User\"},\"verificationCode\":\"a674b42e-6670-4674-a8c7-8e86132eaaa2\",\"verified\":true}\n"
                + "{\"activated\":false,\"class\":\"org.europa.together.domain.acl.AccountDO\",\"defaultLocale\":null,\"defaultTimezone\":null,\"email\":\"moderator@sample.org\",\"password\":\"\",\"registrationDate\":449668801000,\"role\":{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":true,\"description\":\"Higher privileged role than a standard user.\",\"name\":\"Moderator\"},\"verificationCode\":\"3644b892-1438-45e4-87c5-0ea140fea92b\",\"verified\":true}\n"
                + "{\"activated\":false,\"class\":\"org.europa.together.domain.acl.AccountDO\",\"defaultLocale\":null,\"defaultTimezone\":null,\"email\":\"moderator_02@sample.org\",\"password\":\"\",\"registrationDate\":449668801000,\"role\":{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":true,\"description\":\"Higher privileged role than a standard user.\",\"name\":\"Moderator\"},\"verificationCode\":\"fda79c72-59de-430d-8d5f-8451c7a00c04\",\"verified\":true}\n"
                + "]}";

        Response response = target
                .path(API_PATH).path("/account").path("/list/deactivated")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());
        assertEquals(json, response.readEntity(String.class));
    }

    @Test
    void testGetActivatedAccount() {
        LOGGER.log("TEST CASE: getActivatedAccount() 200 : OK", LogLevel.DEBUG);
        String json = "{\"list\": [\n"
                + "{\"activated\":true,\"class\":\"org.europa.together.domain.acl.AccountDO\",\"defaultLocale\":null,\"defaultTimezone\":null,\"email\":\"guest@sample.org\",\"password\":\"\",\"registrationDate\":449668801000,\"role\":{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":false,\"description\":\"Default Role for all new Users.\",\"name\":\"Guest\"},\"verificationCode\":\"03a057d0-6ee7-43b1-ac68-d9a7681165a4\",\"verified\":true}\n"
                + "{\"activated\":true,\"class\":\"org.europa.together.domain.acl.AccountDO\",\"defaultLocale\":null,\"defaultTimezone\":null,\"email\":\"moderator_01@sample.org\",\"password\":\"\",\"registrationDate\":449668801000,\"role\":{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":true,\"description\":\"Higher privileged role than a standard user.\",\"name\":\"Moderator\"},\"verificationCode\":\"46b25a22-69b6-43eb-a711-1dcf1ed0eb5e\",\"verified\":false}\n"
                + "{\"activated\":true,\"class\":\"org.europa.together.domain.acl.AccountDO\",\"defaultLocale\":null,\"defaultTimezone\":null,\"email\":\"admin@sample.org\",\"password\":\"\",\"registrationDate\":449668801000,\"role\":{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":false,\"description\":\"Full privilege.\",\"name\":\"Administrator\"},\"verificationCode\":\"2b421f4c-4408-4f99-b1aa-25002b85ea87\",\"verified\":true}\n"
                + "]}";

        Response response = target
                .path(API_PATH).path("/account").path("/list/activated")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());
        assertEquals(json, response.readEntity(String.class));
    }

    @Test
    void testGetNotConfirmedAccount() {
        LOGGER.log("TEST CASE: getActivatedAccount() 200 : OK", LogLevel.DEBUG);
        String json = "{\"list\": [\n"
                + "{\"activated\":true,\"class\":\"org.europa.together.domain.acl.AccountDO\",\"defaultLocale\":null,\"defaultTimezone\":null,\"email\":\"moderator_01@sample.org\",\"password\":\"\",\"registrationDate\":449668801000,\"role\":{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":true,\"description\":\"Higher privileged role than a standard user.\",\"name\":\"Moderator\"},\"verificationCode\":\"46b25a22-69b6-43eb-a711-1dcf1ed0eb5e\",\"verified\":false}\n"
                + "]}";

        Response response = target
                .path(API_PATH).path("/account").path("/list/not-confirmed")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());
        assertEquals(json, response.readEntity(String.class));
    }

    @Test
    void testGetAccountsOfRole() {
        LOGGER.log("TEST CASE: getAccountsOfRole() 200 : OK", LogLevel.DEBUG);
        String json = "{\"list\": [\n"
                + "{\"activated\":false,\"class\":\"org.europa.together.domain.acl.AccountDO\",\"defaultLocale\":null,\"defaultTimezone\":null,\"email\":\"moderator@sample.org\",\"password\":\"\",\"registrationDate\":449668801000,\"role\":{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":true,\"description\":\"Higher privileged role than a standard user.\",\"name\":\"Moderator\"},\"verificationCode\":\"3644b892-1438-45e4-87c5-0ea140fea92b\",\"verified\":true}\n"
                + "{\"activated\":true,\"class\":\"org.europa.together.domain.acl.AccountDO\",\"defaultLocale\":null,\"defaultTimezone\":null,\"email\":\"moderator_01@sample.org\",\"password\":\"\",\"registrationDate\":449668801000,\"role\":{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":true,\"description\":\"Higher privileged role than a standard user.\",\"name\":\"Moderator\"},\"verificationCode\":\"46b25a22-69b6-43eb-a711-1dcf1ed0eb5e\",\"verified\":false}\n"
                //                + "{\"activated\":false,\"class\":\"org.europa.together.domain.acl.AccountDO\",\"defaultLocale\":null,\"defaultTimezone\":null,\"email\":\"moderator_02@sample.org\",\"password\":\"\",\"registrationDate\":449668801000,\"role\":{\"class\":\"org.europa.together.domain.acl.RolesDO\",\"deleteable\":true,\"description\":\"Higher privileged role than a standard user.\",\"name\":\"Moderator\"},\"verificationCode\":\"fda79c72-59de-430d-8d5f-8451c7a00c04\",\"verified\":true}\n"
                + "]}";

        Response response = target
                .path(API_PATH).path("/account/list").path("/Moderator")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        assertEquals(200, response.getStatus());
        assertEquals(json, response.readEntity(String.class));
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
