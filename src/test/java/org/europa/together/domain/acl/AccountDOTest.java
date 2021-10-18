package org.europa.together.domain.acl;

import static com.google.code.beanmatchers.BeanMatchers.*;
import java.util.Date;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.domain.LogLevel;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Assert;
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
public class AccountDOTest {

    private static final Logger LOGGER = new LogbackLogger(AccountDOTest.class);

    @Test
    void domainObject() {
        LOGGER.log("TEST CASE: domainObject()", LogLevel.DEBUG);

        assertThat(AccountDO.class, hasValidBeanConstructor());
        assertThat(AccountDO.class, hasValidGettersAndSetters());
        assertThat(AccountDO.class, hasValidBeanToString());
        assertThat(AccountDO.class, hasValidBeanHashCodeFor("email"));
        assertThat(AccountDO.class, hasValidBeanEqualsFor("email"));
    }

    @Test
    void register() {
        LOGGER.log("TEST CASE: register()", LogLevel.DEBUG);

        AccountDO account = new AccountDO("register");
        Date registration = account.isRegistered();
        assertNotNull(registration);

        account.setRegistered();
        assertNotEquals(registration, account.isRegistered());
    }

    @Test
    void reactivateAccount() {
        LOGGER.log("TEST CASE: reactivatedAccount()", LogLevel.DEBUG);

        AccountDO account = new AccountDO("reactivate");
        account.setDeactivated();

        assertNotNull(account.isDeactivated());
        account.reactivateAccount();
        assertNull(account.isDeactivated());
    }

    @Test
    void isEqual() {
        LOGGER.log("TEST CASE: objectIsEqual()", LogLevel.DEBUG);

        AccountDO first = new AccountDO("test@sample.org");
        AccountDO second = new AccountDO("test@sample.org");

        assertTrue(first.equals(second));
    }

    @Test
    void isNotEqual() {
        LOGGER.log("TEST CASE: objectisNotEqual()", LogLevel.DEBUG);

        AccountDO first = new AccountDO("a@sample.org");
        AccountDO second = new AccountDO("b@sample.org");

        assertFalse(first.equals(second));
        assertFalse(first.equals(null));
        assertFalse(first.equals(new String()));
    }
}
