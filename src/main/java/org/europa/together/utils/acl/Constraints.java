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
     * Module name used for ConfigurationDAO.
     */
    public static final String MODULE_NAME = "acl";

    /**
     * Version of the module. This Version is autogenerated by the Maven POM
     * file version entry.
     */
    public static final String MODULE_VERSION = getVersion();

    /**
     * URI Parameter for versioning the public REST API of this module. Reasons
     * to increment the version number are: <br/>
     * <li>Changes in the data structure of any JSON object.</li>
     * <li>Removing of any API call</li>
     * <li>Incompatibilities</li>
     */
    public static final int REST_API_VERSION = 1;

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
