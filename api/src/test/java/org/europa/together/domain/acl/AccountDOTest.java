package org.europa.together.domain.acl;

import static com.google.code.beanmatchers.BeanMatchers.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.domain.LogLevel;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
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
    void regitsrationDate() throws InterruptedException {
        LOGGER.log("TEST CASE: register()", LogLevel.DEBUG);

        AccountDO account = new AccountDO("register");
        Date registration = account.isRegistered();
        assertNotNull(registration);

        TimeUnit.MILLISECONDS.sleep(10);
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
    void verifyAccount() {
        AccountDO account = new AccountDO("verify");

        assertNull(account.isVerified());
        account.setVerified();
        assertNotNull(account.isVerified());
    }

    @Test
    void isEqual() {
        LOGGER.log("TEST CASE: objectIsEqual()", LogLevel.DEBUG);

        AccountDO first = new AccountDO("test@sample.org");
        AccountDO second = new AccountDO("test@sample.org");

        assertTrue(first.equals(second));
    }
}
