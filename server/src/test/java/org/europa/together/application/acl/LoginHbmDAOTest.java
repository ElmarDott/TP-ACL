package org.europa.together.application.acl;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import java.util.List;
import org.europa.together.application.JdbcActions;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.DatabaseActions;
import org.europa.together.business.Logger;
import org.europa.together.business.acl.AccountDAO;
import org.europa.together.business.acl.LoginDAO;
import org.europa.together.domain.LogLevel;
import org.europa.together.domain.acl.AccountDO;
import org.europa.together.domain.acl.LoginDO;
import org.europa.together.domain.acl.PermissionDO;
import org.europa.together.domain.acl.ResourcesDO;
import org.europa.together.domain.acl.RolesDO;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SuppressWarnings("unchecked")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"/applicationContext.xml"})
public class LoginHbmDAOTest {

    private static final Logger LOGGER = new LogbackLogger(LoginHbmDAOTest.class);
    private static final String FLUSH_TABLE
            = "TRUNCATE " + RolesDO.TABLE_NAME + ", "
            + AccountDO.TABLE_NAME + ", "
            + LoginDO.TABLE_NAME + ", "
            + PermissionDO.TABLE_NAME + ", "
            + ResourcesDO.TABLE_NAME + ";";
    private static final String FILE
            = "org/europa/together/sql/acl/testdata_ACL.sql";
    private static DatabaseActions jdbcActions = new JdbcActions();

    @Autowired
    private LoginDAO loginDAO;
    @Autowired
    private AccountDAO accountDAO;

    //<editor-fold defaultstate="collapsed" desc="Test Preparation">
    @BeforeAll
    static void setUp() {
        Assumptions.assumeTrue(jdbcActions.connect("test"));

        LOGGER.log("### TEST SUITE INICIATED.", LogLevel.TRACE);
        jdbcActions.executeSqlFromClasspath(FILE);
    }

    @AfterAll
    static void tearDown() {
        LOGGER.log("### TEST SUITE TERMINATED.\n", LogLevel.TRACE);
    }

    @BeforeEach
    void testCaseInitialization() {
    }

    @AfterEach
    void testCaseTermination() throws Exception {
        jdbcActions.executeQuery(FLUSH_TABLE);
        LOGGER.log("TEST CASE TERMINATED.", LogLevel.TRACE);
    }
    //</editor-fold>

    @Test
    void constructor() {
        LOGGER.log("TEST CASE: constructor", LogLevel.DEBUG);
        assertThat(LoginHbmDAO.class, hasValidBeanConstructor());
    }

    @Test
    void findLoginById() {
        LOGGER.log("TEST CASE: findLoginById", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        LoginDO login = loginDAO.find("2b421f4c-4408-4f99-b1aa-25002b85ea87");

        assertNotNull(login);
        assertEquals("moderator@sample.org", login.getAccount().getEmail());
    }

    @Test
    void failCreateLoginWhitoutAccount() throws Exception {
        LOGGER.log("TEST CASE: failCreateLoginWhitoutAccount", LogLevel.DEBUG);

        LoginDO login = new LoginDO();
        assertThrows(Exception.class, () -> {
            loginDAO.create(login);
        });
    }

    @Test
    void createLoginWithAccount() throws Exception {
        LOGGER.log("TEST CASE: createLoginWithAccount", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        LoginDO login = new LoginDO(accountDAO.find("moderator@sample.org"));
        assertTrue(loginDAO.create(login));
    }

    @Test
    void updateLogin() throws Exception {
        LOGGER.log("TEST CASE: updateLogin", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        String loginId = "2b421f4c-4408-4f99-b1aa-25002b85ea87";
        LoginDO login = loginDAO.find(loginId);
        login.setOperationSystem("UBUNTU MATE LINUX");

        loginDAO.update(loginId, login);
    }

    @Test
    void failUpdateLoginNotExist() throws Exception {
        LOGGER.log("TEST CASE: failUpdateLoginNotExist", LogLevel.DEBUG);

        assertThrows(Exception.class, () -> {
            loginDAO.update("NotExist", null);
        });
    }

    @Test
    void deleteLogin() throws Exception {
        LOGGER.log("TEST CASE: deleteLogin", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        loginDAO.delete("2b421f4c-4408-4f99-b1aa-25002b85ea87");
    }

    @Test
    void failDeleteLoginNotExist() throws Exception {
        LOGGER.log("TEST CASE: failDeleteLoginNotExist", LogLevel.DEBUG);

        assertThrows(Exception.class, () -> {
            loginDAO.delete("NotExist");
        });
    }

    @Test
    void getLoginsFromAccount() {
        LOGGER.log("TEST CASE: getLoginsFromAccount", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        List<LoginDO> restult
                = loginDAO.getLoginsFromAccount("moderator@sample.org");

        assertEquals(3, restult.size());
        //fetch the news entry
        assertEquals("2b421f4c-4408-4f99-b1aa-25002b85ea87", restult.get(0).getUuid());
    }

    @Test
    void doLogoutSuccess() throws Exception {
        LOGGER.log("TEST CASE: doLogoutSuccess", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        loginDAO.doLogout("moderator@sample.org");

        LoginDO restult
                = loginDAO.getLoginsFromAccount("moderator@sample.org").get(0);

        assertNotNull(restult.getLogout());
        assertEquals("2b421f4c-4408-4f99-b1aa-25002b85ea87", restult.getUuid());
    }

    @Test
    void doLogoutFail() throws Exception {
        LOGGER.log("TEST CASE: doLogoutFail", LogLevel.DEBUG);

        assertThrows(Exception.class, () -> {
            loginDAO.doLogout("accountNotExist@sample.org");
        });
    }
}
