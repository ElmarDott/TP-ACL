package org.europa.together.utils.acl;

import java.lang.reflect.Constructor;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.domain.LogLevel;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
public class ConstraintsTest {

    private static final Logger LOGGER = new LogbackLogger(ConstraintsTest.class);

    @Test
    void testPrivateConstructor() throws Exception {
        Constructor<Constraints> clazz
                = Constraints.class.getDeclaredConstructor();
        clazz.setAccessible(true);
        assertThrows(Exception.class, () -> {
            Constraints call = clazz.newInstance();
        });
    }

    @Test
    void testToString() {
        String info = Constraints.printConstraintInfo();
        LOGGER.log(info, LogLevel.DEBUG);
        assertNotNull(info);
    }
}
