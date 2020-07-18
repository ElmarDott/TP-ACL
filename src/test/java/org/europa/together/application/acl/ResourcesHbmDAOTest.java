package org.europa.together.application.acl;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import java.util.List;
import org.europa.together.application.JdbcActions;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.DatabaseActions;
import org.europa.together.business.Logger;
import org.europa.together.business.acl.ResourcesDAO;
import org.europa.together.domain.LogLevel;
import org.europa.together.domain.acl.Actions;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SuppressWarnings("unchecked")
@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:/org/europa/together/configuration/spring-dao-test.xml"})
public class ResourcesHbmDAOTest {

    private static final Logger LOGGER = new LogbackLogger(ResourcesHbmDAO.class);
    private static DatabaseActions actions = new JdbcActions(true);
    private static final String FLUSH_TABLES = "TRUNCATE ROLES, ACCOUNT, LOGIN, PERMISSIONS, RESOURCES;";
    private static final String FILE = "org/europa/together/sql/acl/testdata_ACL.sql";

    @Autowired
    @Qualifier("resourcesHbmDAO")
    private ResourcesDAO resourcesDAO;

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
        assertThat(ResourcesHbmDAO.class, hasValidBeanConstructor());
    }

    @Test
    void findResource() {
        LOGGER.log("TEST CASE: findResource", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        ResourcesDO resource = resourcesDAO.find("Article", "teaser");

        assertNotNull(resource);
        assertTrue(resource.isDeleteable());
        assertEquals("teaser", resource.getView());
    }

    @Test
    void findResourceByDefaultView() {
        LOGGER.log("TEST CASE: findResourceByDefaultView", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
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
    void testCreateResource() {
        LOGGER.log("TEST CASE: createResource", LogLevel.DEBUG);

        ResourcesDO resource = new ResourcesDO("Link");
        assertTrue(resourcesDAO.create(resource));
    }

    @Test
    void testCreateResourceWithManyViews() {
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
    void testFailCreateDuplicateResource() throws Exception {
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

        actions.executeSqlFromClasspath(FILE);
        ResourcesDO resource = resourcesDAO.find("Document", "default");
        assertNotNull(resource);
        resource.setActions(Actions.REVOKE.toString());

        assertTrue(resourcesDAO.update(resource));
    }

    @Test
    void failDepecatedUpdateResource() throws UnsupportedOperationException {
        LOGGER.log("TEST CASE: failFindResource", LogLevel.DEBUG);

        resourcesDAO.update(null);

        assertThrows(UnsupportedOperationException.class, () -> {
            resourcesDAO.update("", null);
        });
    }

    @Test
    void testDeleteResource() {
        LOGGER.log("TEST CASE: deleteResource", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
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
    void failDeletResourceWithExistingPermission() throws Exception {
        LOGGER.log("TEST CASE: failDeleteResourceWithExistingPermission", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        ResourcesDO resource = resourcesDAO.find("Article", "teaser");
        assertTrue(resource.isDeleteable());

        assertThrows(Exception.class, () -> {
            resourcesDAO.delete(resource);
        });
    }

    @Test
    void testProtectedResources() {
        LOGGER.log("TEST CASE: deleteProtectedResources", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        ResourcesDO resource = resourcesDAO.find("Document", "default");

        assertFalse(resource.isDeleteable());
        assertFalse(resourcesDAO.delete(resource));
    }

    @Test
    void testListProtectedResources() {
        LOGGER.log("TEST CASE: listProtectedResources", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        List<ResourcesDO> resources = resourcesDAO.listProtectedResources();
        assertEquals(2, resources.size());
    }

    @Test
    void testListResourcesOfSameType() {
        LOGGER.log("TEST CASE: listResourcesOfSameType", LogLevel.DEBUG);

        actions.executeSqlFromClasspath(FILE);
        List<ResourcesDO> resources = resourcesDAO.listResourcesOfSameType("Article");
        assertEquals(3, resources.size());
    }
}
