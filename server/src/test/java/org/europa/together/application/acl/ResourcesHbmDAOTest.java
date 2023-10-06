package org.europa.together.application.acl;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import java.sql.SQLException;
import java.util.List;
import org.europa.together.application.JdbcActions;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.DatabaseActions;
import org.europa.together.business.Logger;
import org.europa.together.business.acl.ResourcesDAO;
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SuppressWarnings("unchecked")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"/applicationContext.xml"})
public class ResourcesHbmDAOTest {

    private static final Logger LOGGER = new LogbackLogger(ResourcesHbmDAO.class);
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
    private static DatabaseActions jdbcActions = new JdbcActions();

    @Autowired
    private ResourcesDAO resourcesDAO;

    //<editor-fold defaultstate="collapsed" desc="Test Preparation">
    @BeforeAll
    static void setUp() throws Exception {
        Assumptions.assumeTrue(jdbcActions.connect("test"), "JDBC DBMS Connection failed.");
        jdbcActions.executeSqlFromClasspath(FILE);
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
    void constructor() {
        LOGGER.log("TEST CASE: constructor", LogLevel.DEBUG);
        assertThat(ResourcesHbmDAO.class, hasValidBeanConstructor());
    }

    @Test
    void findResource() {
        LOGGER.log("TEST CASE: findResource", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        ResourcesDO resource = resourcesDAO.find("Article", "teaser");

        assertNotNull(resource);
        assertTrue(resource.isDeleteable());
        assertEquals("teaser", resource.getView());
    }

    @Test
    void findResourceByDefaultView() {
        LOGGER.log("TEST CASE: findResourceByDefaultView", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        ResourcesDO resource = resourcesDAO.find("Article");

        assertEquals("default", resource.getView());
    }

    @Test
    void failFindResource() throws Exception {
        LOGGER.log("TEST CASE: failFindResource", LogLevel.DEBUG);

        assertThrows(EmptyResultDataAccessException.class, () -> {
            resourcesDAO.find("none");
        });
    }

    @Test
    void createResource() throws Exception {
        LOGGER.log("TEST CASE: createResource", LogLevel.DEBUG);

        ResourcesDO resource = new ResourcesDO("Link");
        assertTrue(resourcesDAO.create(resource));
    }

    @Test
    void createResourceWithManyViews() throws Exception {
        LOGGER.log("TEST CASE: createResourceWithManyViews", LogLevel.DEBUG);

        ResourcesDO resource_1 = new ResourcesDO("Link");
        ResourcesDO resource_2 = new ResourcesDO("Link");
        resource_2.setView("template_A");
        ResourcesDO resource_3 = new ResourcesDO("Link");
        resource_3.setView("template_B");
        assertTrue(resourcesDAO.create(resource_1));
        assertTrue(resourcesDAO.create(resource_2));
        assertTrue(resourcesDAO.create(resource_3));
    }

    @Test
    void failCreateDuplicateResource() throws Exception {
        LOGGER.log("TEST CASE: failCreateDuplicateResource", LogLevel.DEBUG);

        ResourcesDO resource_1 = new ResourcesDO("Link");
        ResourcesDO resource_2 = new ResourcesDO("Link");
        assertTrue(resourcesDAO.create(resource_1));

        assertThrows(Exception.class, () -> {
            resourcesDAO.create(resource_2);
        });
    }

    @Test
    void updateResource() {
        LOGGER.log("TEST CASE: updadteResource", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        ResourcesDO resource = resourcesDAO.find("Document", "default");
        resourcesDAO.update(resource);
    }

    @Test
    void failUpdateResourceNotExist() throws Exception {
        LOGGER.log("TEST CASE: failUpdateResourceNotExist", LogLevel.DEBUG);

        assertThrows(Exception.class, () -> {
            resourcesDAO.update(new ResourcesDO("NotExist"));
        });
    }

    @Test
    void deprecatedUpdateResource() throws Exception {
        LOGGER.log("TEST CASE: deprecatedUpdateResource", LogLevel.DEBUG);

        assertThrows(UnsupportedOperationException.class, () -> {
            resourcesDAO.update("", null);
        });
    }

    @Test
    void deleteResource() {
        LOGGER.log("TEST CASE: deleteResource", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        ResourcesDO resource = resourcesDAO.find("Article", "delete");

        assertTrue(resource.isDeleteable());
        assertTrue(resourcesDAO.delete(resource));
    }

    @Test
    void failDeleteResource() {
        LOGGER.log("TEST CASE: failDeleteResource", LogLevel.DEBUG);

        assertThrows(UnsupportedOperationException.class, () -> {
            resourcesDAO.delete("dectivated");
        });
    }

    @Test
    void deleteResourceWithExistingPermission() throws Exception {
        LOGGER.log("TEST CASE: failDeleteResourceWithExistingPermission", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        ResourcesDO resource = resourcesDAO.find("Article", "teaser");
        assertTrue(resource.isDeleteable());

        assertThrows(Exception.class, () -> {
            resourcesDAO.delete(resource);
        });
    }

    @Test
    void failDeleteProtectedResources() throws Exception {
        LOGGER.log("TEST CASE: failDeleteProtectedResources", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        ResourcesDO resource = resourcesDAO.find("Document", "default");

        assertFalse(resource.isDeleteable());
        assertFalse(resourcesDAO.delete(resource));
    }

    @Test
    void listProtectedResources() {
        LOGGER.log("TEST CASE: listProtectedResources", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        List<ResourcesDO> resources = resourcesDAO.listProtectedResources();
        assertEquals(2, resources.size());
    }

    @Test
    void listResourcesOfSameType() {
        LOGGER.log("TEST CASE: listResourcesOfSameType", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        List<ResourcesDO> resources = resourcesDAO.listResourcesOfSameType("Article");
        assertEquals(3, resources.size());
    }
}
