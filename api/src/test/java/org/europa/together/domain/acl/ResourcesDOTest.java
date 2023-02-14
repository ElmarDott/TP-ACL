package org.europa.together.domain.acl;

import static com.google.code.beanmatchers.BeanMatchers.*;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.domain.LogLevel;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
public class ResourcesDOTest {

    private static final Logger LOGGER = new LogbackLogger(ResourcesDOTest.class);

    @Test
    void domainObject() {
        LOGGER.log("TEST CASE: domainObject()", LogLevel.DEBUG);

        assertThat(ResourcesDO.class, hasValidBeanConstructor());
        assertThat(ResourcesDO.class, hasValidGettersAndSetters());
        assertThat(ResourcesDO.class, hasValidBeanToString());
        assertThat(ResourcesDO.class, hasValidBeanHashCodeFor("name", "view"));
        assertThat(ResourcesDO.class, hasValidBeanEqualsFor("name", "view"));
    }

    @Test
    void isEqual() {
        LOGGER.log("TEST CASE: objectIsEqual()", LogLevel.DEBUG);

        ResourcesDO first = new ResourcesDO("article");
        ResourcesDO second = new ResourcesDO("article");

        assertTrue(first.equals(second));
    }
}
