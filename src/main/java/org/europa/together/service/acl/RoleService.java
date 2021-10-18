package org.europa.together.service.acl;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.STABLE;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.business.acl.RolesDAO;
import org.europa.together.domain.LogLevel;
import org.europa.together.domain.acl.RolesDO;
import org.europa.together.utils.acl.Constraints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * RESTful Service for Roles.
 *
 * @author elmar.dott@gmail.com
 * @version 1.0
 * @since 1.0
 */
@Service
@Path("/acl/" + Constraints.REST_API_VERSION + "/role")
public class RoleService {

    private static final Logger LOGGER = new LogbackLogger(RoleService.class);

    @Autowired
    private RolesDAO rolesDAO;

    public RoleService() {
        LOGGER.log("instance class", LogLevel.INFO);
    }

    @GET
    @Path("/{role}")
    @Produces({MediaType.APPLICATION_JSON})
    @API(status = STABLE, since = "1")
    public Response fetchRole(final @PathParam("role") String roleName) {
        Response response = null;
        try {
            RolesDO role = rolesDAO.find(roleName);
            String json = rolesDAO.serializeAsJson(role);
            if (role != null) {
                response = Response.status(Response.Status.OK)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(json)
                        .encoding("UTF-8")
                        .build();
            } else {
                response = Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception ex) {
            LOGGER.log("ERROR CODE 500 " + ex.getMessage(), LogLevel.DEBUG);
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return response;
    }

    @GET
    @Path("/list/protected")
    @Produces({MediaType.APPLICATION_JSON})
    @API(status = STABLE, since = "1")
    public Response fetchProtectedRoles() {
        Response response = null;
        try {
            List<RolesDO> roles = rolesDAO.listProtectedRoles();
            String json = objectListToJson(roles);
            response = Response.status(Response.Status.OK)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(json)
                    .encoding("UTF-8")
                    .build();

        } catch (Exception ex) {
            LOGGER.log("ERROR CODE 500 " + ex.getMessage(), LogLevel.DEBUG);
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return response;
    }

    @GET
    @Path("/list")
    @Produces({MediaType.APPLICATION_JSON})
    @API(status = STABLE, since = "1")
    public Response fetchAllRoles() {
        Response response = null;
        try {
            List<RolesDO> roles = rolesDAO.listAllElements();
            String json = objectListToJson(roles);
            response = Response.status(Response.Status.OK)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(json)
                    .encoding("UTF-8")
                    .build();

        } catch (Exception ex) {
            LOGGER.log("ERROR CODE 500 " + ex.getMessage(), LogLevel.DEBUG);
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return response;
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @API(status = STABLE, since = "1")
    public Response updateRole(final RolesDO role) {
        Response response = null;
        try {
            boolean success = rolesDAO.update(role.getName(), role);
            if (success) {
                response = Response.status(Response.Status.ACCEPTED).build();
            } else {
                response = Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception ex) {
            LOGGER.log("ERROR CODE 500 " + ex.getMessage(), LogLevel.DEBUG);
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return response;
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @API(status = STABLE, since = "1")
    public Response createRole(final RolesDO role) {
        Response response = null;
        try {
            rolesDAO.create(role);
            response = Response.status(Response.Status.CREATED).build();

        } catch (Exception ex) {
            LOGGER.log("ERROR CODE 500 " + ex.getMessage(), LogLevel.DEBUG);
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return response;
    }

    @DELETE
    @Path("/{role}")
    @API(status = STABLE, since = "1")
    public Response deleteRole(final @PathParam("role") String roleName) {
        Response response = null;
        try {
            boolean success = rolesDAO.delete(roleName);
            if (success) {
                LOGGER.log("ERROR CODE 410 - deleted", LogLevel.DEBUG);
                response = Response.status(Response.Status.GONE).build();
            } else {
                if (rolesDAO.find(roleName) == null) {
                    LOGGER.log("ERROR CODE 404 - not found", LogLevel.DEBUG);
                    response = Response.status(Response.Status.NOT_FOUND).build();
                } else {
                    LOGGER.log("ERROR CODE 403 - forbidden entity protected", LogLevel.DEBUG);
                    response = Response.status(Response.Status.FORBIDDEN).build();
                }
            }
        } catch (Exception ex) {
            String exception = ex.getClass().getSimpleName();
            Status status = Response.Status.INTERNAL_SERVER_ERROR;
            if (exception.equals("DataIntegrityViolationException")) {
                status = Response.Status.CONFLICT;
            }
            if (exception.equals("EntityNotFoundException")) {
                status = Response.Status.NOT_FOUND;
            }

            LOGGER.log("ERROR CODE " + status.getStatusCode() + " - " + exception, LogLevel.DEBUG);
            response = Response.status(status).build();
        }
        return response;
    }

    // #########################################################################
    private String objectListToJson(final List<RolesDO> roles) {
        int cnt = 0;
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (RolesDO role : roles) {
            if (cnt != 0) {
                json.append(", \n");
            }
            json.append(rolesDAO.serializeAsJson(role));
            cnt++;
        }
        json.append("]");
        return json.toString();
    }
}
