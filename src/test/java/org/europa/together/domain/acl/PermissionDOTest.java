package org.europa.together.domain.acl;

import static com.google.code.beanmatchers.BeanMatchers.*;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.domain.LogLevel;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@SuppressWarnings("unchecked")
@RunWith(JUnitPlatform.class)
public class PermissionDOTest {

    private static final Logger LOGGER = new LogbackLogger(PermissionDOTest.class);
    private static ValidatorFactory validatorFactory;
    private static Validator validate;

    //<editor-fold defaultstate="collapsed" desc="Test Preparation">
    @BeforeAll
    static void setUp() {
        LOGGER.log("### TEST SUITE INICIATED.", LogLevel.TRACE);
        LOGGER.log("Assumption terminated. TestSuite will be executed.\n", LogLevel.TRACE);
    }

    @AfterAll
    static void tearDown() {
        LOGGER.log("### TEST SUITE TERMINATED.", LogLevel.TRACE);
    }

    @BeforeEach
    void testCaseInitialization() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validate = validatorFactory.getValidator();
    }

    @AfterEach
    void testCaseTermination() {
        validatorFactory.close();
        LOGGER.log("TEST CASE TERMINATED.\n", LogLevel.TRACE);
    }
    //</editor-fold>

    @Test
    void testDomainObject() {
        LOGGER.log("TEST CASE: domainObject()", LogLevel.DEBUG);

        assertThat(PermissionDO.class, hasValidBeanConstructor());
        assertThat(PermissionDO.class, hasValidGettersAndSetters());
        assertThat(PermissionDO.class, hasValidBeanToString());
        assertThat(PermissionDO.class, hasValidBeanHashCodeFor("uuid"));
        assertThat(PermissionDO.class, hasValidBeanEqualsFor("uuid"));
    }

    @Test
    void testObjectIsEqual() {
        LOGGER.log("TEST CASE: objectIsEqual()", LogLevel.DEBUG);

        String uuid = "X";
        PermissionDO first = new PermissionDO();
        first.setUuid(uuid);
        PermissionDO second = new PermissionDO();
        second.setUuid(uuid);

        assertTrue(first.equals(second));
    }

    @Test
    void testDomainObjectisNotEqualByUuid() {
        LOGGER.log("TEST CASE: objectisNotEqualByUuid()", LogLevel.DEBUG);

        RolesDO role = new RolesDO("test");
        ResourcesDO resource = new ResourcesDO("document");

        PermissionDO first = new PermissionDO(new PermissionId(resource, role));
        PermissionDO second = new PermissionDO(new PermissionId(resource, role));

        assertFalse(first.equals(second));
    }

    @Test
    void testDomainObjectIsNotEqual() throws Exception {
        LOGGER.log("TEST CASE: objectisNotEqual()", LogLevel.DEBUG);

        PermissionId id_1 = new PermissionId();
        id_1.setRole(new RolesDO("test"));
        PermissionId id_2 = new PermissionId();
        id_2.setRole(new RolesDO("role"));

        PermissionDO first = new PermissionDO(id_1);
        PermissionDO second = new PermissionDO(id_2);

        assertFalse(first.equals(second));
        assertFalse(first.equals(null));
        assertFalse(first.equals(new String()));
    }

}
