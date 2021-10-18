package org.europa.together.domain.acl;

import static com.google.code.beanmatchers.BeanMatchers.*;
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

@RunWith(JUnitPlatform.class)
@SuppressWarnings("unchecked")
public class ResourceIdTest {

    private static final Logger LOGGER = new LogbackLogger(ResourceIdTest.class);

    @Test
    void domainObject() {
        LOGGER.log("TEST CASE: domainObject()", LogLevel.DEBUG);

        assertThat(ResourceId.class, hasValidBeanConstructor());
        assertThat(ResourceId.class, hasValidGettersAndSetters());
        assertThat(ResourceId.class, hasValidBeanEqualsFor("resourceName", "view"));
        assertThat(ResourceId.class, hasValidBeanHashCodeFor("resourceName", "view"));
    }
}
