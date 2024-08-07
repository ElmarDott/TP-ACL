package org.europa.together.domain.acl;

import static com.google.code.beanmatchers.BeanMatchers.*;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.domain.LogLevel;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
public class PermissionDOTest {

    private static final Logger LOGGER = new LogbackLogger(PermissionDOTest.class);

    @Test
    void domainObject() {
        LOGGER.log("TEST CASE: domainObject()", LogLevel.DEBUG);

        assertThat(PermissionDO.class, hasValidBeanConstructor());
        assertThat(PermissionDO.class, hasValidGettersAndSetters());
        assertThat(PermissionDO.class, hasValidBeanToString());
        assertThat(PermissionDO.class, hasValidBeanHashCodeFor("uuid"));
        assertThat(PermissionDO.class, hasValidBeanEqualsFor("uuid"));
    }

    @Test
    void isEqual() {
        LOGGER.log("TEST CASE: objectIsEqual()", LogLevel.DEBUG);

        String uuid = "X";
        PermissionDO first = new PermissionDO();
        first.setUuid(uuid);
        PermissionDO second = new PermissionDO();
        second.setUuid(uuid);

        assertTrue(first.equals(second));
    }

    @Test
    void isNotEqualByUuid() {
        LOGGER.log("TEST CASE: objectisNotEqualByUuid()", LogLevel.DEBUG);

        RolesDO role = new RolesDO("test");
        ResourcesDO resource = new ResourcesDO("document");

        PermissionDO first = new PermissionDO(role, resource);
        PermissionDO second = new PermissionDO(role, resource);

        assertFalse(first.equals(second));
    }
}
