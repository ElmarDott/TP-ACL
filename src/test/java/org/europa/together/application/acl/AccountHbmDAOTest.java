package org.europa.together.application.acl;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import java.sql.SQLException;
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
import org.europa.together.utils.Constraints;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SuppressWarnings("unchecked")
@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"/applicationContext.xml"})
public class AccountHbmDAOTest {

    private static final Logger LOGGER = new LogbackLogger(AccountHbmDAOTest.class);

    private static final String FLUSH_TABLE
            = "TRUNCATE ROLES, ACCOUNT, LOGIN, PERMISSIONS, RESOURCES;";
    private static final String FILE
            = "org/europa/together/sql/acl/testdata_ACL.sql";

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private RolesDAO rolesDAO;

    private static DatabaseActions jdbcActions = new JdbcActions();

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
    void testCaseTermination() throws SQLException {
        jdbcActions.executeQuery(FLUSH_TABLE);
        LOGGER.log("TEST CASE TERMINATED.", LogLevel.TRACE);
    }
    //</editor-fold>

    @Test
    void constructor() {
        LOGGER.log("TEST CASE: constructor", LogLevel.DEBUG);
        assertThat(AccountHbmDAO.class, hasValidBeanConstructor());
    }

    @Test
    void findAccountByVerificationCode() {
        LOGGER.log("TEST CASE: findAccountByVerificationCode", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        AccountDO account = accountDAO
                .findAccountByVerificationCode("2b421f4c-4408-4f99-b1aa-25002b85ea87");

        assertEquals("admin@sample.org", account.getEmail());
    }

    @Test
    void findAccountByEmail() {
        LOGGER.log("TEST CASE: findAccountByEmail", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        AccountDO account = accountDAO.find("admin@sample.org");
        RolesDO role = account.getRole();

        assertNotNull(account);
        assertEquals("Administrator", role.getName());
    }

    @Test
    void createAccountWithRole() {
        LOGGER.log("TEST CASE: createAccountWithRole", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        AccountDO account = new AccountDO("create@sample.org");
        account.setRole(rolesDAO.find("Guest"));
        assertTrue(accountDAO.create(account));
    }

    @Test
    void updateAccount() {
        LOGGER.log("TEST CASE: updateAccount", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        AccountDO account = accountDAO.find("moderator_01@sample.org");

        assertNull(account.isVerified());
        account.setVerified();

        assertTrue(accountDAO.update("moderator_01@sample.org", account));
        assertNotNull(accountDAO.find("moderator_01@sample.org").isVerified());
    }

    @Test
    void deleteAccountWhitoutLogins() {
        LOGGER.log("TEST CASE: deleteAccountWhitoutLogins", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        AccountDO account = accountDAO.find("moderator_02@sample.org");

        assertTrue(accountDAO.delete("moderator_02@sample.org"));
    }

    @Test
    void failDeleteAccountWithLogins() throws Exception {
        LOGGER.log("TEST CASE: failDeleteAccountWithLogins", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        AccountDO account = accountDAO.find("moderator@sample.org");
        assertNotNull(account);

        assertThrows(Exception.class, () -> {
            accountDAO.delete("moderator@sample.org");
        });
    }

    @Test
    void listDeactivatedAccounts() {
        LOGGER.log("TEST CASE: listDeactivatedAccounts", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        assertEquals(3, accountDAO.listDeactivatedAccounts().size());
    }

    @Test
    void listActivatedAccounts() {
        LOGGER.log("TEST CASE: listActivatedAccounts", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        assertEquals(3, accountDAO.listActivatedAccounts().size());
    }

    @Test
    void listUnverifiedAccounts() {
        LOGGER.log("TEST CASE: listUnverifiedAccounts", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        assertEquals(1, accountDAO.listNotConfirmedAccounts().size());
    }

    @Test
    void verifyAccount() {
        LOGGER.log("TEST CASE: verifyAccounts", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        assertTrue(accountDAO.verifyAccount("moderator_02@sample.org"));
    }

    @Test
    void failVerifyAccount() {
        LOGGER.log("TEST CASE: failVerifyAccounts", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        assertFalse(accountDAO.verifyAccount("moderator_01@sample.org"));
    }

    @Test
    void deactivateAccount() {
        LOGGER.log("TEST CASE: deactivateAccounts", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        assertTrue(accountDAO.deactivateAccount("moderator_01@sample.org"));
    }

    @Test
    void failDeactivateAccount() {
        LOGGER.log("TEST CASE: failDeactivateAccounts", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        assertFalse(accountDAO.deactivateAccount("moderator_02@sample.org"));
    }

    @Test
    void getAllAccountsOfARole() {
        LOGGER.log("TEST CASE: getAccountsOfRole", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        List<AccountDO> accounts = accountDAO.listAccountsOfRole("Moderator");
        assertEquals(3, accounts.size());
    }

    @Test
    void getRegistrationDateBefore() {
        LOGGER.log("TEST CASE: getRegistrationDateBefore", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        Timestamp date = new Timestamp(System.currentTimeMillis());
        List<AccountDO> accounts = accountDAO.listRegisterdAcountsBeforeDate(date);

        assertEquals(6, accounts.size());
    }

    @Test
    void getRegistrationDateAfter() {
        try {
            LOGGER.log("TEST CASE: getRegistrationDateAfter", LogLevel.DEBUG);

            jdbcActions.executeSqlFromClasspath(FILE);
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
