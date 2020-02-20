package org.europa.together.domain.acl;

import static com.google.code.beanmatchers.BeanMatchers.*;
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

        assertThat(AccountDO.class, hasValidBeanConstructor());
        assertThat(AccountDO.class, hasValidGettersAndSetters());
        assertThat(AccountDO.class, hasValidBeanToString());
        assertThat(AccountDO.class, hasValidBeanHashCodeFor("email"));
        assertThat(AccountDO.class, hasValidBeanEqualsFor("email"));
    }

    @Test
    void testIsEqual() {
        LOGGER.log("TEST CASE: objectIsEqual()", LogLevel.DEBUG);

        AccountDO first = new AccountDO("test@sample.org");
        AccountDO second = new AccountDO("test@sample.org");

        assertTrue(first.equals(second));
    }

    @Test
    void testIsNotEqual() {
        LOGGER.log("TEST CASE: objectisNotEqual()", LogLevel.DEBUG);

        AccountDO first = new AccountDO("a@sample.org");
        AccountDO second = new AccountDO("b@sample.org");

        assertFalse(first.equals(second));
        assertFalse(first.equals(null));
        assertFalse(first.equals(new String()));
    }
}
