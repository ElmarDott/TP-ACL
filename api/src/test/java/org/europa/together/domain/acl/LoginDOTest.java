package org.europa.together.domain.acl;

import static com.google.code.beanmatchers.BeanMatchers.*;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.domain.LogLevel;
import org.europa.together.utils.StringUtils;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
public class LoginDOTest {

    private static final Logger LOGGER = new LogbackLogger(LoginDOTest.class);

    @Test
    void domainObject() {
        LOGGER.log("TEST CASE: domainObject()", LogLevel.DEBUG);

        assertThat(LoginDO.class, hasValidBeanConstructor());
        assertThat(LoginDO.class, hasValidGettersAndSetters());
        assertThat(LoginDO.class, hasValidBeanToString());
        assertThat(LoginDO.class, hasValidBeanHashCodeFor("uuid"));
        assertThat(LoginDO.class, hasValidBeanEqualsFor("uuid"));
    }

    @Test
    void constructor() {
        LoginDO domainObject = new LoginDO(new AccountDO("test@sample.org"));

        assertEquals("test@sample.org", domainObject.getAccount().getEmail());
    }

    @Test
    void isEqual() {
        LOGGER.log("TEST CASE: objectIsEqual()", LogLevel.DEBUG);

        String uuid = StringUtils.generateUUID();
        LoginDO first = new LoginDO();
        first.setUuid(uuid);
        LoginDO second = new LoginDO();
        second.setUuid(uuid);

        assertTrue(first.equals(second));
    }

    @Test
    void isNotEqual() throws Exception {
        LOGGER.log("TEST CASE: objectisNotEqual()", LogLevel.DEBUG);

        LoginDO first = new LoginDO();
        LoginDO second = new LoginDO();

        assertFalse(first.equals(second));
        assertFalse(first.equals(null));
        assertFalse(first.equals(new String()));
    }
}
