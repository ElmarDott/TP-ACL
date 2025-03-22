package org.europa.together.orchestration.acl;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.europa.together.JUnit5Preperator;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.DatabaseActions;
import org.europa.together.business.Logger;
import org.europa.together.business.acl.LoginDAO;
import org.europa.together.domain.LogLevel;
import org.europa.together.domain.acl.AccountDO;
import org.europa.together.domain.acl.LoginDO;
import org.europa.together.domain.acl.PermissionDO;
import org.europa.together.domain.acl.ResourcesDO;
import org.europa.together.domain.acl.RolesDO;
import org.europa.together.utils.acl.Constraints;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import static org.hamcrest.MatcherAssert.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SuppressWarnings("unchecked")
@ExtendWith({SpringExtension.class, JUnit5Preperator.class})
@ContextConfiguration(locations = {"/applicationContext.xml"})
public class AuthentificationIT {

    private static final Logger LOGGER = new LogbackLogger(AuthentificationIT.class);
    private static final String FLUSH_TABLE
            = "TRUNCATE "
            + RolesDO.TABLE_NAME + ", "
            + AccountDO.TABLE_NAME + ", "
            + LoginDO.TABLE_NAME + ", "
            + PermissionDO.TABLE_NAME + ", "
            + ResourcesDO.TABLE_NAME
            + " CASCADE;";
    private static final String FILE
            = "org/europa/together/sql/acl/testdata_ACL.sql";
    private static final String API_PATH
            = Constraints.MODULE_NAME + "/" + Constraints.REST_API_VERSION;

    private static DatabaseActions jdbcActions
            = JUnit5Preperator.JDBC_CONNECTION;
    private static HttpServer server;
    private static WebTarget target;

    @Autowired
    private LoginDAO loginDAO;

    //<editor-fold defaultstate="collapsed" desc="Test Preparation">
    @BeforeAll
    static void setUp() throws Exception {
        Assumptions.assumeTrue(
                JUnit5Preperator.isConnected(), "JDBC DBMS Connection failed.");

        Assumptions.assumeTrue(
                JUnit5Preperator.startServer(), "Starting Grizzly Server failed.");

        server = JUnit5Preperator.SERVER;
        Client client = ClientBuilder.newClient(new ClientConfig());
        target = client.target(JUnit5Preperator.BASE_URI);
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
    void getLoginStatus404() {
        LOGGER.log("TEST CASE: login() 404 : NOT FOUND", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/login")
                .queryParam("loginId", "NotExist")
                .queryParam("loginPwd", "empty")
                .request()
                .get(Response.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    void getLoginStatus403() {
        LOGGER.log("TEST CASE: login() 401 : UNATHORIZED", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/login")
                .queryParam("loginId", "moderator@sample.org")
                .queryParam("loginPwd", "wrongPWD")
                .request()
                .get(Response.class);

        assertEquals(401, response.getStatus());
    }

    @Test
    void getLoginStatus200() {
        LOGGER.log("TEST CASE: login() 200 : OK", LogLevel.DEBUG);

        Response response = target
                .path(API_PATH).path("/login")
                .queryParam("loginId", "admin@sample.org")
                .queryParam("loginPwd", "g3h31m")
                .request()
                .get(Response.class);

        assertEquals(200, response.getStatus());
    }

    @Test
    void isLoginCreated() {
        LOGGER.log("TEST CASE: isLoginCreated", LogLevel.DEBUG);

        String account = "admin@sample.org";
        Response response = target
                .path(API_PATH).path("/login")
                .queryParam("loginId", account)
                .queryParam("loginPwd", "g3h31m")
                .request()
                .get(Response.class);
        List<LoginDO> login = loginDAO.getLoginsFromAccount(account);
        LOGGER.log(login.get(0).toString(), LogLevel.DEBUG);

        assertEquals(1, login.size());
        assertNull(login.get(0).getLogout());
    }

    @Test
    @Disabled
    void doLogout() {
        LOGGER.log("TEST CASE: doLogout", LogLevel.DEBUG);

        String account = "admin@sample.org";
        Response preResponse = target
                .path(API_PATH).path("/login")
                .queryParam("loginId", account)
                .queryParam("loginPwd", "g3h31m")
                .request()
                .get(Response.class);

        List<LoginDO> check = loginDAO.getLoginsFromAccount(account);
        String loginId = check.get(0).getUuid();

        Response response = target
                .path(API_PATH).path("/logout")
                .queryParam("loginId", loginId)
                .request()
                .get(Response.class);

        assertEquals(200, response.getStatus());

        List<LoginDO> login = loginDAO.getLoginsFromAccount(account);
        LOGGER.log(login.get(0).toString(), LogLevel.DEBUG);

        assertNotNull(login.get(0).getLogout());
    }
}
