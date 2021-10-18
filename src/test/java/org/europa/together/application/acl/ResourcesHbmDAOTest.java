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
import org.europa.together.domain.acl.ResourcesDO;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SuppressWarnings("unchecked")
@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"/applicationContext.xml"})
public class ResourcesHbmDAOTest {

    private static final Logger LOGGER = new LogbackLogger(ResourcesHbmDAO.class);
    private static final String FLUSH_TABLE
            = "TRUNCATE ROLES, ACCOUNT, LOGIN, PERMISSIONS, RESOURCES;";
    private static final String FILE
            = "org/europa/together/sql/acl/testdata_ACL.sql";

    @Autowired
    private ResourcesDAO resourcesDAO;

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
    void failFindResource() throws EmptyResultDataAccessException {
        LOGGER.log("TEST CASE: failFindResource", LogLevel.DEBUG);

        assertThrows(EmptyResultDataAccessException.class, () -> {
            resourcesDAO.find("none");
        });
    }

    @Test
    void createResource() {
        LOGGER.log("TEST CASE: createResource", LogLevel.DEBUG);

        ResourcesDO resource = new ResourcesDO("Link");
        assertTrue(resourcesDAO.create(resource));
    }

    @Test
    void createResourceWithManyViews() {
        LOGGER.log("TEST CASE: createResourceWithManyView", LogLevel.DEBUG);

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
        assertNotNull(resource);
        assertTrue(resourcesDAO.update(resource));
    }

    @Test
    void deprecatedUpdateResource() throws UnsupportedOperationException {
        LOGGER.log("TEST CASE: failFindResource", LogLevel.DEBUG);

        resourcesDAO.update(null);

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
    void failDeleteResource() throws UnsupportedOperationException {
        LOGGER.log("TEST CASE: failDeleteResource", LogLevel.DEBUG);

        assertThrows(UnsupportedOperationException.class, () -> {
            resourcesDAO.delete("dectivated");
        });
    }

    @Test
    void deletResourceWithExistingPermission() throws Exception {
        LOGGER.log("TEST CASE: failDeleteResourceWithExistingPermission", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        ResourcesDO resource = resourcesDAO.find("Article", "teaser");
        assertTrue(resource.isDeleteable());

        assertThrows(Exception.class, () -> {
            resourcesDAO.delete(resource);
        });
    }

    @Test
    void protectedResources() throws Exception {
        LOGGER.log("TEST CASE: deleteProtectedResources", LogLevel.DEBUG);

        jdbcActions.executeSqlFromClasspath(FILE);
        ResourcesDO resource = resourcesDAO.find("Document", "default");

        assertFalse(resource.isDeleteable());
        assertThrows(DataIntegrityViolationException.class, () -> {
            assertFalse(resourcesDAO.delete(resource));
        });
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
