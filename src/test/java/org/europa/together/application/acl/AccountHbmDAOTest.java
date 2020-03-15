package org.europa.together.application.acl;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.europa.together.application.JdbcActions;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.DatabaseActions;
import org.europa.together.business.Logger;
import org.europa.together.business.acl.AccountDAO;
import org.europa.together.business.acl.RolesDAO;
import org.europa.together.domain.LogLevel;
import org.europa.together.domain.acl.AccountDO;
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
public class AccountHbmDAOTest {

    private static final Logger LOGGER = new LogbackLogger(AccountHbmDAOTest.class);
    private static DatabaseActions actions = new JdbcActions(true);
    private static final String FLUSH_TABLES = "TRUNCATE ROLES, ACCOUNT, LOGIN, PERMISSIONS, RESOURCES;";
    private static final String FILE = "org/europa/together/sql/acl/testdata_ACL.sql";

    @Autowired
    @Qualifier("accountHbmDAO")
    private AccountDAO accountDAO;

    @Autowired
    @Qualifier("rolesHbmDAO")
    private RolesDAO rolesDAO;

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
        assertThat(AccountHbmDAO.class, hasValidBeanConstructor());
    }

    @Test
    void findAccountByEmail() {
        LOGGER.log("TEST CASE: findAccountByEmail", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        AccountDO account = accountDAO.find("admin@sample.org");
        RolesDO role = account.getRole();

        assertNotNull(account);
        assertEquals("Administrator", role.getName());
    }

    @Test
    void testCreateAccountWithRole() {
        LOGGER.log("TEST CASE: createAccount", LogLevel.DEBUG);

        RolesDO role = new RolesDO("Guest");
        rolesDAO.create(role);

        AccountDO account = new AccountDO("create@sample.org");
        assertTrue(accountDAO.create(account));
    }

    @Test
    void testFailCreateAccountWhitoutRole() throws Exception {
        LOGGER.log("TEST CASE: FailCreateAccountWhitoutRole", LogLevel.DEBUG);

        AccountDO account = new AccountDO("fail@sample.org");
        assertThrows(Exception.class, () -> {
            accountDAO.create(account);
        });
    }

    @Test
    void testUpdateAccount() {
        LOGGER.log("TEST CASE: updateAccount", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        AccountDO account = accountDAO.find("moderator_01@sample.org");
        assertFalse(account.isVerified());
        account.setVerified(true);

        assertTrue(accountDAO.update("moderator_01@sample.org", account));
        assertTrue(accountDAO.find("moderator_01@sample.org").isVerified());
    }

    @Test
    void testDeleteAccountWhitoutLogins() {
        LOGGER.log("TEST CASE: deleteAccountWhitoutLogins", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        AccountDO account = accountDAO.find("moderator_02@sample.org");

        assertTrue(accountDAO.delete("moderator_02@sample.org"));
    }

    @Test
    void testFailDeleteAccountWithLogins() throws Exception {
        LOGGER.log("TEST CASE: failDeleteAccountWithLogins", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        AccountDO account = accountDAO.find("moderator@sample.org");
        assertNotNull(account);

        assertThrows(Exception.class, () -> {
            accountDAO.delete("moderator@sample.org");
        });
    }

    @Test
    void testListDeactivatedAccounts() {
        LOGGER.log("TEST CASE: listDeactivatedAccounts", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        assertEquals(3, accountDAO.listDeactivatedAccounts().size());
    }

    @Test
    void testListActivatedAccounts() {
        LOGGER.log("TEST CASE: listActivatedAccounts", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        assertEquals(3, accountDAO.listActivatedAccounts().size());
    }

    @Test
    void testListUnverifiedAccounts() {
        LOGGER.log("TEST CASE: listUnverifiedAccounts", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        assertEquals(1, accountDAO.listNotConfirmedAccounts().size());
    }

    @Test
    void testDeactivateAccount() {
        LOGGER.log("TEST CASE: deactivateAccounts", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        assertTrue(accountDAO.deactivateAccount("moderator_01@sample.org"));
    }

    @Test
    void testFailDeactivateAccount() {
        LOGGER.log("TEST CASE: failDeactivateAccounts", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        assertFalse(accountDAO.deactivateAccount("moderator_02@sample.org"));
    }

    @Test
    void testGetAllAccountsOfARole() {
        LOGGER.log("TEST CASE: getAccountsOfRole", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        List<AccountDO> accounts = accountDAO.listAccountsOfRole("Moderator");
        assertEquals(3, accounts.size());
    }

    @Test
    void testgetRegistrationDateBefore() {
        LOGGER.log("TEST CASE: getRegistrationDateBefore", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        Timestamp date = new Timestamp(System.currentTimeMillis());
        List<AccountDO> accounts = accountDAO.listRegisterdAcountsBeforeDate(date);

        assertEquals(6, accounts.size());
    }

    @Test
    void testgetRegistrationDateAfter() {
        try {
            LOGGER.log("TEST CASE: getRegistrationDateAfter", LogLevel.DEBUG);

            actions.executeSqlFromClasspath(FILE);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = dateFormat.parse("1984-03-30 12:00:01");

            Timestamp stamp = new Timestamp(date.getTime());
            LOGGER.log("Timestamp: " + stamp + " Date: " + date, LogLevel.DEBUG
            );

            List<AccountDO> accounts
                    = accountDAO.listRegisterdAcountsAfterDate(stamp);

            assertEquals(6, accounts.size());

        } catch (ParseException ex) {
            LOGGER.catchException(ex);
        }
    }
}
