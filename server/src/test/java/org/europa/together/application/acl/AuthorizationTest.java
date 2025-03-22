package org.europa.together.application.acl;

import java.sql.SQLException;
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
    private static final DatabaseActions jdbcActions
            = JUnit5Preperator.JDBC_CONNECTION;

    @Autowired
    private ResourcesDAO resourcesDAO;

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private PermissionDAO permissionDAO;

    @Autowired
    private Authorization authorization;

    //<editor-fold defaultstate="collapsed" desc="Test Preparation">
    @BeforeAll
    static void setUp() throws Exception {
        Assumptions.assumeTrue(JUnit5Preperator.isConnected(), "JDBC DBMS Connection failed.");
        jdbcActions.executeSqlFromClasspath(FILE);

        LOGGER.log("Assumptions passed ...\n\n", LogLevel.DEBUG);
    }

    @AfterAll
    static void tearDown() throws Exception {
        jdbcActions.executeQuery(FLUSH_TABLE);
    }

    @BeforeEach
    void testCaseInitialization() {
    }

    @AfterEach
    void testCaseTermination() {
    }
    //</editor-fold>

    @Test
    void exisitingUserHasPermission() {
        LOGGER.log("exisitingUserHasPermission: access allowed", LogLevel.DEBUG);

        AccountDO account = accountDAO.find("admin@sample.org");
        LOGGER.log(account.toString(), LogLevel.DEBUG);

        ResourcesDO resource = resourcesDAO.find("ACL", "role");
        LOGGER.log(resource.toString(), LogLevel.DEBUG);

        String roleName = account.getRole().getName();
        List<PermissionDO> permissions = permissionDAO
                .listRolePermissions(roleName);

        LOGGER.log("(" + permissions.size() + ") Permissions for Role " + roleName, LogLevel.DEBUG);
        LOGGER.log(permissions.toString(), LogLevel.DEBUG);

        boolean result = authorization.accountHasPermissionForAction(
                account, resource, JakartaAuthorization.READ);

        assertTrue(result);
    }

    @Test
    void exisitingUserHasNoPermission() {
        LOGGER.log("exisitingUserHasNoPermission: access denied", LogLevel.DEBUG);

        AccountDO account = accountDAO.find("guest@sample.org");
        LOGGER.log(account.toString(), LogLevel.DEBUG);

        ResourcesDO resource = resourcesDAO.find("ACL", "account");
        LOGGER.log(resource.toString(), LogLevel.DEBUG);

        String roleName = account.getRole().getName();
        List<PermissionDO> permissions = permissionDAO
                .listRolePermissions(roleName);

        LOGGER.log("(" + permissions.size() + ") Permissions for Role " + roleName, LogLevel.DEBUG);
        LOGGER.log(permissions.toString(), LogLevel.DEBUG);

        boolean result = authorization.accountHasPermissionForAction(
                account, resource, JakartaAuthorization.READ);

        assertFalse(result);
    }

    @Test
    void notExistingUserAccessDenied() {
        LOGGER.log("notExistingUserHasNoPermission: access denied", LogLevel.DEBUG);

        AccountDO account = accountDAO.find("notexist@sample.org");
        ResourcesDO resource = resourcesDAO.find("ACL", "login");

        boolean result = authorization.accountHasPermissionForAction(
                account, resource, JakartaAuthorization.READ);

        assertFalse(result);
    }

    @Test
    void userAllocateResourceNotExist() {
        LOGGER.log("userAllocateResourceNotExist: access denied", LogLevel.DEBUG);

        AccountDO account = accountDAO.find("guest@sample.org");
        ResourcesDO resource = null;

        boolean result = authorization.accountHasPermissionForAction(
                account, resource, JakartaAuthorization.READ);

        assertFalse(result);
    }

    @Test
    void resourcesReadPermissionAllowed() {
        LOGGER.log("resourcesReadPermissionAllowed", LogLevel.DEBUG);

        AccountDO account = accountDAO.find("moderator@sample.org");
        ResourcesDO resource = resourcesDAO.find("ACL", "role");

        boolean result = authorization.accountHasPermissionForAction(
                account, resource, JakartaAuthorization.READ);

        assertTrue(result);
    }

    @Test
    void resourcesCreatePermissionAllowed() {
        LOGGER.log("resourcesCreatePermissionAllowed", LogLevel.DEBUG);

        AccountDO account = accountDAO.find("moderator@sample.org");
        ResourcesDO resource = resourcesDAO.find("ACL", "role");

        boolean result = authorization.accountHasPermissionForAction(
                account, resource, JakartaAuthorization.CREATE);

        assertTrue(result);
    }

    @Test
    void resourcesDeletePermissionAllowed() {
        LOGGER.log("resourcesDeletePermissionAllowed", LogLevel.DEBUG);

        AccountDO account = accountDAO.find("moderator@sample.org");
        ResourcesDO resource = resourcesDAO.find("ACL", "role");

        boolean result = authorization.accountHasPermissionForAction(
                account, resource, JakartaAuthorization.DELETE);

        assertTrue(result);
    }

    @Test
    void resourcesChangePermissionAllowed() {
        LOGGER.log("resourcesChangePermissionAllowed", LogLevel.DEBUG);

        AccountDO account = accountDAO.find("moderator@sample.org");
        ResourcesDO resource = resourcesDAO.find("ACL", "role");

        boolean result = authorization.accountHasPermissionForAction(
                account, resource, JakartaAuthorization.CHANGE);

        assertTrue(result);
    }

    @Test
    void resourcesReadPermissionDenied() {
        LOGGER.log("resourcesReadPermissionDenied", LogLevel.DEBUG);

        AccountDO account = accountDAO.find("moderator@sample.org");
        ResourcesDO resource = resourcesDAO.find("ACL", "permission");

        boolean result = authorization.accountHasPermissionForAction(
                account, resource, JakartaAuthorization.READ);

        assertFalse(result);
    }

    @Test
    void resourcesCreatePermissionDenied() {
        LOGGER.log("resourcesCreatePermissionDenied", LogLevel.DEBUG);

        AccountDO account = accountDAO.find("moderator@sample.org");
        ResourcesDO resource = resourcesDAO.find("ACL", "permission");

        boolean result = authorization.accountHasPermissionForAction(
                account, resource, JakartaAuthorization.CREATE);

        assertFalse(result);
    }

    @Test
    void resourcesDeletePermissionDenied() {
        LOGGER.log("resourcesDeletePermissionDenied", LogLevel.DEBUG);

        AccountDO account = accountDAO.find("moderator@sample.org");
        ResourcesDO resource = resourcesDAO.find("ACL", "permission");

        boolean result = authorization.accountHasPermissionForAction(
                account, resource, JakartaAuthorization.DELETE);

        assertFalse(result);
    }

    @Test
    void resourcesChangePermissionDenied() {
        LOGGER.log("resourcesChangePermissionDenied", LogLevel.DEBUG);

        AccountDO account = accountDAO.find("moderator@sample.org");
        ResourcesDO resource = resourcesDAO.find("ACL", "permission");

        boolean result = authorization.accountHasPermissionForAction(
                account, resource, JakartaAuthorization.CHANGE);

        assertFalse(result);
    }
}
