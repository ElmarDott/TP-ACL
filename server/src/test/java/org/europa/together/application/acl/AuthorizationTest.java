package org.europa.together.application.acl;

import java.util.List;
import org.europa.together.JUnit5Preperator;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.DatabaseActions;
import org.europa.together.business.Logger;
import org.europa.together.business.acl.AccountDAO;
import org.europa.together.business.acl.Authorization;
import org.europa.together.business.acl.PermissionDAO;
import org.europa.together.business.acl.ResourcesDAO;
import org.europa.together.domain.LogLevel;
import org.europa.together.domain.acl.AccountDO;
import org.europa.together.domain.acl.LoginDO;
import org.europa.together.domain.acl.PermissionDO;
import org.europa.together.domain.acl.ResourcesDO;
import org.europa.together.domain.acl.RolesDO;
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
@ExtendWith({SpringExtension.class, JUnit5Preperator.class})
@ContextConfiguration(locations = {"/applicationContext.xml"})
public class AuthorizationTest {

    private static final Logger LOGGER = new LogbackLogger(AuthorizationTest.class);
    private static final String FLUSH_TABLE
            = "TRUNCATE "
            + RolesDO.TABLE_NAME + ", "
            + AccountDO.TABLE_NAME + ", "
            + LoginDO.TABLE_NAME + ", "
            + PermissionDO.TABLE_NAME + ", "
            + ResourcesDO.TABLE_NAME
            + " CASCADE;";
    private static final String FILE
            = "org/europa/together/sql/acl/auth.sql";
    private static DatabaseActions jdbcActions
            = JUnit5Preperator.JDBC_CONNECTION;

    @Autowired
    private ResourcesDAO resourcesDAO;

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private PermissionDAO permissionDAO;

    //<editor-fold defaultstate="collapsed" desc="Test Preparation">
    @BeforeAll
    static void setUp() throws Exception {
        Assumptions.assumeTrue(JUnit5Preperator.isConnected(), "JDBC DBMS Connection failed.");
        jdbcActions.executeSqlFromClasspath(FILE);

        LOGGER.log("Assumptions passed ...\n\n", LogLevel.DEBUG);
    }

    @AfterAll
    static void tearDown() {
    }

    @BeforeEach
    void testCaseInitialization() {
    }

    @AfterEach
    void testCaseTermination() throws Exception {
        jdbcActions.executeQuery(FLUSH_TABLE);
    }
    //</editor-fold>

    @Test
    void exisitingUserHasNoPermission() {
        LOGGER.log("exisitingUserHasNoPermission", LogLevel.DEBUG);

        ResourcesDO resource = resourcesDAO.find("ACL", "role");
        LOGGER.log(resource.toString(), LogLevel.DEBUG);

        AccountDO account = accountDAO.find("moderator@sample.org");
        LOGGER.log(account.toString(), LogLevel.DEBUG);

        String roleName = account.getRole().getName();
        List<PermissionDO> permissions = permissionDAO
                .listRolePermissions(roleName);
        LOGGER.log("(" + permissions.size() + ") Permissions for Role " + roleName, LogLevel.DEBUG);
        LOGGER.log(permissions.toString(), LogLevel.DEBUG);

        Authorization auth = new JakartaAuthorization();
        boolean result = auth.accountHasPermissionForAction(
                account, resource, JakartaAuthorization.READ);
        assertFalse(result);
    }

}
