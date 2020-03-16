package org.europa.together.service.acl;

import javax.ws.rs.Path;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.business.acl.ResourcesDAO;
import org.europa.together.domain.LogLevel;
import org.europa.together.utils.acl.Constraints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * RESTful Service for Resources.
 *
 * @author elmar.dott@gmail.com
 * @version 1.0
 * @since 1.0
 */
@Service
@Path(Constraints.MODULE_NAME + "/" + Constraints.REST_API_VERSION)
public class ResourceService {

    private static final Logger LOGGER = new LogbackLogger(ResourceService.class);

    @Autowired
    @Qualifier("resourcesHbmDAO")
    private ResourcesDAO resourcesDAO;

    public ResourceService() {
        LOGGER.log("instance class", LogLevel.INFO);
    }

}
