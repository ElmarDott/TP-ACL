package org.europa.together.application.acl;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import java.util.List;
import org.europa.together.application.JdbcActions;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.DatabaseActions;
import org.europa.together.business.Logger;
import org.europa.together.business.acl.PermissionDAO;
import org.europa.together.business.acl.ResourcesDAO;
import org.europa.together.business.acl.RolesDAO;
import org.europa.together.domain.LogLevel;
import org.europa.together.domain.acl.PermissionDO;
import org.europa.together.domain.acl.PermissionId;
import org.europa.together.domain.acl.ResourcesDO;
import org.europa.together.domain.acl.RolesDO;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
public class PermissionHbmDAOTest {

    private static final Logger LOGGER = new LogbackLogger(PermissionHbmDAO.class);
    private static DatabaseActions actions = new JdbcActions(true);
    private static final String FLUSH_TABLES = "TRUNCATE ROLES, ACCOUNT, LOGIN, PERMISSIONS, RESOURCES;";
    private static final String FILE = "org/europa/together/sql/acl/testdata_ACL.sql";

    @Autowired
    @Qualifier("permissionHbmDAO")
    private PermissionDAO permissionDAO;

    @Autowired
    @Qualifier("rolesHbmDAO")
    private RolesDAO rolesDAO;

    @Autowired
    @Qualifier("resourcesHbmDAO")
    private ResourcesDAO resourcesDAO;

    ResourcesDO resource;
    RolesDO role;

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

    public PermissionHbmDAOTest() {
        resource = new ResourcesDO("Document");
        role = new RolesDO("User");
    }

    @Test
    void testConstructor() {
        LOGGER.log("TEST CASE: constructor", LogLevel.DEBUG);
        assertThat(PermissionHbmDAO.class, hasValidBeanConstructor());
    }

    @Test
    void testFindPermission() {
        LOGGER.log("TEST CASE: findPermission", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        RolesDO _role = rolesDAO.find("Guest");
        ResourcesDO _resource = resourcesDAO.find("Article", "teaser");
        PermissionDO permission = permissionDAO.find(new PermissionId(_resource, _role));

        assertNotNull(permission);
        assertEquals(permission.getPermissionId().getRole().getName(), "Guest");
        assertEquals(permission.getPermissionId().getResource().getName(), "Article");
        assertEquals(permission.getPermissionId().getResource().getView(), "teaser");
        assertEquals(permission.getPermissionId().getResource().getActions(), "ALL");
        assertTrue(permission.isRead());
        assertFalse(permission.isChange());
        assertFalse(permission.isCreate());
        assertFalse(permission.isDelete());
    }

    @Test
    void testFindPermissionById() {
        LOGGER.log("TEST CASE: findPermissionById", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        String id = "1f4c2b42-4408-4f99-b1aa-25002b85ea87";
        PermissionDO permission = permissionDAO.find(id);
        assertNotNull(permission);

        String role = permission.getPermissionId().getRole().getName();
        String resource = permission.getPermissionId().getResource().getName();
        String view = permission.getPermissionId().getResource().getView();
        String actions = permission.getPermissionId().getResource().getActions();

        assertEquals(role, "Guest");
        assertEquals(resource, "Article");
        assertEquals(view, "teaser");
        assertEquals(actions, "ALL");
        assertTrue(permission.isRead());
        assertFalse(permission.isChange());
        assertFalse(permission.isCreate());
        assertFalse(permission.isDelete());
    }

    @Test
    void testCreatePermission() {
        LOGGER.log("TEST CASE: createPermission", LogLevel.DEBUG);

        assertTrue(rolesDAO.create(role));
        assertTrue(resourcesDAO.create(resource));
        PermissionDO permission = new PermissionDO(
                new PermissionId(resource, role));

        assertTrue(permissionDAO.create(permission));
    }

    @Test
    void testCreateEqualPermissionForDifferentUser() throws Exception {
        LOGGER.log("TEST CASE: createEqualPermissionForDifferentUser", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        resource = resourcesDAO.find("Document", "default");

        PermissionDO permission_A = new PermissionDO(
                new PermissionId(resource, rolesDAO.find("Moderator")));
        PermissionDO permission_B = new PermissionDO(
                new PermissionId(resource, rolesDAO.find("Guest")));

        assertTrue(permissionDAO.create(permission_A));
        assertTrue(permissionDAO.create(permission_B));
    }

    @Test
    void testFailDuplicateEntry() throws Exception {
        LOGGER.log("TEST CASE: failDuplicateEntry", LogLevel.DEBUG);

        assertTrue(rolesDAO.create(role));
        assertTrue(resourcesDAO.create(resource));

        PermissionDO permission_A = new PermissionDO(
                new PermissionId(resource, role));
        PermissionDO permission_B = new PermissionDO(
                new PermissionId(resource, role));

        assertTrue(permissionDAO.create(permission_A));
        assertThrows(Exception.class, () -> {
            permissionDAO.create(permission_B);
        });
    }

    @Test
    void testDeletePermission() {
        LOGGER.log("TEST CASE: deletePermission", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        PermissionDO permission = permissionDAO.find("eb7bb730-95a0-45ac-983c-258b7a56f1f4");
        assertTrue(permissionDAO.delete(permission.getPermissionId()));
    }

    @Test
    void testUpdatePermission() {
        LOGGER.log("TEST CASE: deletePermission", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        PermissionDO permission = permissionDAO.find("5d1da892-6316-4781-94ea-d82d8a15e350");
        permission.setDelete(false);
        assertTrue(permissionDAO.update(permission.getPermissionId(), permission));

        assertFalse(permissionDAO.find("5d1da892-6316-4781-94ea-d82d8a15e350").isDelete());
    }

    @Test
    void testListRolesOfPermission() {
        LOGGER.log("TEST CASE: listRolesOfPermission", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        List<PermissionDO> permissions = permissionDAO.listRolePermissions("User");

        assertEquals(2, permissions.size());
    }

}
