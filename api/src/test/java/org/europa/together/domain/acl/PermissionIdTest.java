package org.europa.together.domain.acl;

import static com.google.code.beanmatchers.BeanMatchers.*;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.domain.LogLevel;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
public class PermissionIdTest {

    private static final Logger LOGGER = new LogbackLogger(PermissionIdTest.class);

    @Test
    void domainObject() {
        LOGGER.log("TEST CASE: domainObject()", LogLevel.DEBUG);

        assertThat(PermissionId.class, hasValidBeanConstructor());
        assertThat(PermissionId.class, hasValidGettersAndSetters());
    }

    @Test
    void constructor() {
        RolesDO role = new RolesDO("test");
        ResourcesDO resurce = new ResourcesDO("article");
        PermissionId permission = new PermissionId(resurce, role);

        assertEquals("article", permission.getResource().getName());
        assertEquals("test", permission.getRole().getName());
    }
}
