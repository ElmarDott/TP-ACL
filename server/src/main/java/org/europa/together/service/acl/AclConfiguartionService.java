package org.europa.together.service.acl;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.europa.together.domain.LogLevel;
import org.europa.together.utils.acl.Constraints;
import org.springframework.stereotype.Service;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.ConfigurationDAO;
import org.europa.together.business.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * RESTful Service for ACL Configuration.
 *
 * @author elmar.dott@gmail.com
 * @version 1.0
 * @since 1.0
 */
@Service
@Path("/acl/" + Constraints.REST_API_VERSION + "/configuration")
public class AclConfiguartionService {

    private static final Logger LOGGER = new LogbackLogger(AclConfiguartionService.class);

    @Autowired
    private ConfigurationDAO configurationDAO;

    public AclConfiguartionService() {
        LOGGER.log("instance class", LogLevel.INFO);
    }

    public Response getConfiguration() {
        Response response = null;
        try {

        } catch (Exception ex) {
            LOGGER.log("ERROR CODE 500 " + ex.getMessage(), LogLevel.DEBUG);
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return response;
    }

    public Response updateConfiguration() {
        Response response = null;
        try {

        } catch (Exception ex) {
            LOGGER.log("ERROR CODE 500 " + ex.getMessage(), LogLevel.DEBUG);
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return response;
    }

    public Response resetModuleToDefault() {
        Response response = null;
        try {

        } catch (Exception ex) {
            LOGGER.log("ERROR CODE 500 " + ex.getMessage(), LogLevel.DEBUG);
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return response;
    }

}
