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
