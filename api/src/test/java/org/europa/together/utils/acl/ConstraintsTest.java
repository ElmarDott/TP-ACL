package org.europa.together.utils.acl;

import java.lang.reflect.Constructor;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
public class ConstraintsTest {

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
        assertNotNull(info);
    }
}
