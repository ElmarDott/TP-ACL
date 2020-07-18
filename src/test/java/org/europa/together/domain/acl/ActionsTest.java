package org.europa.together.domain.acl;

import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.domain.LogLevel;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SuppressWarnings("unchecked")
public class ActionsTest {

    private static final Logger LOGGER = new LogbackLogger(ActionsTest.class);

    @Test
    void testEnumValues() {
        LOGGER.log("TEST CASE: enumValues()", LogLevel.DEBUG);

        assertEquals("ALL",
                Actions.ALL.toString());
        assertEquals("APPROVAL",
                Actions.APPROVAL.toString());
        assertEquals("PUBLISH",
                Actions.PUBLISH.toString());
        assertEquals("REJECT",
                Actions.REJECT.toString());
        assertEquals("REVOKE",
                Actions.REVOKE.toString());
        assertEquals("SUBMIT",
                Actions.SUBMIT.toString());
    }

}
