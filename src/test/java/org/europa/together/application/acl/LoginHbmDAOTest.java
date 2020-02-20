package org.europa.together.application.acl;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import org.europa.together.application.JdbcActions;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.DatabaseActions;
import org.europa.together.business.Logger;
import org.europa.together.business.acl.AccountDAO;
import org.europa.together.business.acl.LoginDAO;
import org.europa.together.domain.LogLevel;
import org.europa.together.domain.acl.LoginDO;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SuppressWarnings("unchecked")
@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:/org/europa/together/configuration/spring-dao-test.xml"})
public class LoginHbmDAOTest {

    private static final Logger LOGGER = new LogbackLogger(LoginHbmDAOTest.class);
    private static DatabaseActions actions = new JdbcActions(true);
    private static final String FLUSH_TABLES = "TRUNCATE ROLES, ACCOUNT, LOGIN, PERMISSIONS, RESOURCES;";
    private static final String FILE = "org/europa/together/sql/acl/testdata_ACL.sql";

    @Autowired
    @Qualifier("loginHbmDAO")
    private LoginDAO loginDAO;

    @Autowired
    @Qualifier("accountHbmDAO")
    private AccountDAO accountDAO;

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
        LOGGER.log("Assumption terminated. TestSuite will be " + out, LogLevel.TRACE);
        Assumptions.assumeTrue(check);
    }

    @AfterAll
    static void tearDown() {
        LOGGER.log("### TEST SUITE TERMINATED.\n", LogLevel.TRACE);
    }

    @BeforeEach
    void testCaseInitialization() {
    }

    @AfterEach
    void testCaseTermination() {
        actions.executeQuery(FLUSH_TABLES);
        LOGGER.log("TEST CASE TERMINATED.", LogLevel.TRACE);
    }
    //</editor-fold>

    @Test
    void testConstructor() {
        LOGGER.log("TEST CASE: constructor", LogLevel.DEBUG);
        assertThat(LoginHbmDAO.class, hasValidBeanConstructor());
    }

    @Test
    void testFindLoginById() {
        LOGGER.log("TEST CASE: findLoginById", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        LoginDO login = loginDAO.find("2b421f4c-4408-4f99-b1aa-25002b85ea87");

        assertNotNull(login);
        assertEquals("moderator@sample.org", login.getAccount().getEmail());
    }

    @Test
    void testFailCreateLoginWhitoutAccount() throws Exception {
        LOGGER.log("TEST CASE: failCreateLoginWhitoutAccount", LogLevel.DEBUG);

        LoginDO login = new LoginDO();
        assertThrows(Exception.class, () -> {
            loginDAO.create(login);
        });
    }

    @Test
    void testCreateLoginWithAccount() {
        LOGGER.log("TEST CASE: createSimpleLogin", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        LoginDO login = new LoginDO(accountDAO.find("moderator@sample.org"));
        assertTrue(loginDAO.create(login));
    }

    @Test
    void testUpdateLogin() {
        LOGGER.log("TEST CASE: updateLogin", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        String loginId = "2b421f4c-4408-4f99-b1aa-25002b85ea87";
        LoginDO login = loginDAO.find(loginId);
        login.setOperationSystem("UBUNTU MATE LINUX");

        assertTrue(loginDAO.update(loginId, login));
    }

    @Test
    void testDeleteLogin() {
        LOGGER.log("TEST CASE: deleteLogin", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        assertTrue(loginDAO.delete("2b421f4c-4408-4f99-b1aa-25002b85ea87"));
    }

    @Test
    void testGetLoginsFromAccount() {
        LOGGER.log("TEST CASE: getLoginsFromAccount", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        assertEquals(3, loginDAO.getLoginsFromAccount("moderator@sample.org").size());
    }
}
