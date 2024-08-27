package org.europa.together.presentation.acl;

import jakarta.inject.Named;
import java.io.Serializable;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.domain.LogLevel;

/**
 * Basic Display Helper Functions for JSF.
 *
 * @author elmar.dott@gmail.com
 */
@Named
public class DisplayHelper implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = new LogbackLogger(DisplayHelper.class);

    /**
     * Default constructor.
     */
    public DisplayHelper() {
        LOGGER.log("instance class", LogLevel.INFO);
    }
}
