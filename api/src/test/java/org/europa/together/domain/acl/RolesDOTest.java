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
public class RolesDOTest {

    private static final Logger LOGGER = new LogbackLogger(RolesDOTest.class);

    @Test
    void domainObject() {
        LOGGER.log("TEST CASE: domainObject()", LogLevel.DEBUG);

        assertThat(RolesDO.class, hasValidBeanConstructor());
        assertThat(RolesDO.class, hasValidGettersAndSetters());
        assertThat(RolesDO.class, hasValidBeanToString());
        assertThat(RolesDO.class, hasValidBeanHashCodeFor("name"));
        assertThat(RolesDO.class, hasValidBeanEqualsFor("name"));
    }

    @Test
    void isEqual() {
        LOGGER.log("TEST CASE: objectIsEqual()", LogLevel.DEBUG);
        RolesDO first = new RolesDO("abcd");
        RolesDO second = new RolesDO("abcd");

        assertTrue(first.equals(second));
    }

    @Test
    void isNotEqual() throws Exception {
        LOGGER.log("TEST CASE: objectisNotEqual()", LogLevel.DEBUG);

        RolesDO first = new RolesDO("abcd");
        RolesDO second = new RolesDO("ABCD");

        assertFalse(first.equals(second));
        assertFalse(first.equals(null));
        assertFalse(first.equals(new String()));
    }
}
