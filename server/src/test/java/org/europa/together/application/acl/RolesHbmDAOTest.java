package org.europa.together.application.acl;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import java.util.List;
import org.europa.together.application.JdbcActions;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.DatabaseActions;
import org.europa.together.business.Logger;
import org.europa.together.business.acl.RolesDAO;
import org.europa.together.domain.LogLevel;
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
public class RolesHbmDAOTest {

    private static final Logger LOGGER = new LogbackLogger(RolesHbmDAOTest.class);

    private static final String FLUSH_TABLE
            = "TRUNCATE ROLES, ACCOUNT, LOGIN, PERMISSIONS, RESOURCES;";
    private static final String FILE
            = "org/europa/together/sql/acl/testdata_ACL.sql";

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
    void testCaseTermination() throws Exception {
        jdbcActions.executeQuery(FLUSH_TABLE);
        LOGGER.log("TEST CASE TERMINATED.", LogLevel.TRACE);
    }
    //</editor-fold>

    @Test
    void constructor() {
        LOGGER.log("TEST CASE: constructor", LogLevel.DEBUG);
        assertThat(RolesHbmDAO.class, hasValidBeanConstructor());
    }

    @Test
    void findRoleByName() {
        LOGGER.log("TEST CASE: findRoleByName", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        RolesDO role = rolesDAO.find("User");

        assertNotNull(role);
        assertEquals("User", role.getName());
    }

    @Test
    void createRole() throws Exception {
        LOGGER.log("TEST CASE: createRole", LogLevel.DEBUG);

        RolesDO role = new RolesDO("create");
        assertTrue(rolesDAO.create(role));
    }

    @Test
    void updateRole() throws Exception {
        LOGGER.log("TEST CASE: updateRole", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        RolesDO role = rolesDAO.find("User");
        role.setDescription("Update role description.");
        rolesDAO.update("User", role);

        assertEquals("Update role description.",
                rolesDAO.find("User").getDescription());
    }

    @Test
    void deleteRoleWhitoutAccount() throws Exception {
        LOGGER.log("TEST CASE: deleteDeleteRoleWhitoutAccount", LogLevel.DEBUG);
        RolesDO role = new RolesDO("delete");
        assertTrue(rolesDAO.create(role));
        rolesDAO.delete("delete");
    }

    @Test
    void failDeleteProtectedRoles() throws Exception {
        LOGGER.log("TEST CASE: failDeleteProtectedRoles", LogLevel.DEBUG);

        assertThrows(Exception.class, () -> {
            rolesDAO.delete("Administrator");
        });
    }

    @Test
    void failDeleteRoleLinkedToAccount() throws Exception {
        LOGGER.log("TEST CASE: failDeleteRoleLinkedToAccount", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        RolesDO role = rolesDAO.find("Moderator");
        assertNotNull(role);

        assertThrows(Exception.class, () -> {
            rolesDAO.delete("Moderator");
        });
    }

    @Test
    void failDeleteRoleLinkedToPermission() throws Exception {
        LOGGER.log("TEST CASE: failDeleteRoleLinkedToPermission", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        RolesDO role = rolesDAO.find("Sample");

        assertNotNull(role);
        assertThrows(Exception.class, () -> {
            rolesDAO.delete("Sample");
        });
    }

    @Test
    void failDeleteRoleNotExist() throws Exception {
        LOGGER.log("TEST CASE: failDeleteRoleNotExist", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);

        assertThrows(Exception.class, () -> {
            rolesDAO.delete("NotExist");
        });
    }

    @Test
    void listProtectedRoles() throws Exception {
        LOGGER.log("TEST CASE: listProtectedRoles", LogLevel.DEBUG);

        RolesDO role_01 = new RolesDO("first");
        role_01.setDeleteable(false);
        RolesDO role_02 = new RolesDO("second");
        role_02.setDeleteable(false);
        RolesDO role_03 = new RolesDO("third");

        rolesDAO.create(role_01);
        rolesDAO.create(role_02);
        rolesDAO.create(role_03);

        List<RolesDO> roles = rolesDAO.listProtectedRoles();
        assertEquals(2, roles.size());
    }
}
