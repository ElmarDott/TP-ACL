package org.europa.together.utils.acl;

import java.util.TimeZone;
import org.europa.together.application.PropertyFileReader;
import org.europa.together.business.PropertyReader;

/**
 * Constraints for the package Portal.
 */
public final class Constraints {

    /**
     * Constructor.
     */
    private Constraints() {
        throw new UnsupportedOperationException();
    }

    /**
     * Module name.
     */
    public static final String MODULE_NAME = "acl";

    /**
     * Version of the module.
     */
    public static final String MODULE_VERSION = getVersion();

    /**
     * A short description of the module.
     */
    public static final String MODULE_DESCRIPTION = "Access Control List";

    /**
     * The Software License of the artifact.
     */
    public static final String LICENSE = "Apache License 2.0";

    /**
     * The default timezone is used by the artifact.
     */
    public static final TimeZone SYSTEM_DEFAULT_TIMEZONE = TimeZone.getTimeZone("UTC");

    /**
     * Implements a static version of toString();.
     *
     * @return Constraints as String
     */
    public static String printConstraintInfo() {
        return "ACL Constraints DEBUG Info."
                + "\n\t Module Name: " + MODULE_NAME
                + "\n\t Module Version: " + MODULE_VERSION
                + "\n\t Module Description: " + MODULE_DESCRIPTION
                + "\n\t Software License: " + LICENSE;
    }

    private static String getVersion() {
        String filePath
                = "org/europa/together/configuration/acl/version.properties";
        PropertyReader propertyReader = new PropertyFileReader();
        propertyReader.appendPropertiesFromClasspath(filePath);
        return propertyReader.getPropertyAsString("version");
    }
}
