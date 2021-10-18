package org.europa.together.service.acl;

import java.util.List;
import javax.persistence.NoResultException;
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
import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.STABLE;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.business.acl.PermissionDAO;
import org.europa.together.business.acl.ResourcesDAO;
import org.europa.together.business.acl.RolesDAO;
import org.europa.together.domain.LogLevel;
import org.europa.together.domain.acl.PermissionDO;
import org.europa.together.domain.acl.PermissionId;
import org.europa.together.utils.acl.Constraints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

/**
 * RESTful Service for Resources.
 *
 * @author elmar.dott@gmail.com
 * @version 1.0
 * @since 1.0
 */
@Service
@Path("/acl/" + Constraints.REST_API_VERSION + "/permission")
public class PermissionService {

    private static final Logger LOGGER = new LogbackLogger(PermissionService.class);

    @Autowired
    private PermissionDAO permissionDAO;

    @Autowired
    private RolesDAO rolesDAO;

    @Autowired
    private ResourcesDAO resourcesDAO;

    public PermissionService() {
        LOGGER.log("instance class", LogLevel.INFO);
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @API(status = STABLE, since = "1")
    public Response fetchPermission(final @PathParam("id") String permissionId) {
        Response response = null;
        try {
            PermissionDO permission = permissionDAO.find(permissionId);
            String json = permissionDAO.serializeAsJson(permission);
            response = Response.status(Response.Status.OK)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(json)
                    .encoding("UTF-8")
                    .build();

        } catch (EmptyResultDataAccessException | NoResultException ex) {
            LOGGER.log("ERROR CODE 404 " + ex.getMessage(), LogLevel.DEBUG);
            response = Response.status(Response.Status.NOT_FOUND).build();

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
    public Response fetchAllPermissions() {
        Response response = null;
        try {
            List<PermissionDO> permissions = permissionDAO.listAllElements();
            String json = objectListToJson(permissions);
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
    @Path("/list/forRole/{roleName}")
    @Produces({MediaType.APPLICATION_JSON})
    @API(status = STABLE, since = "1")
    public Response fetchAllPermissionsOfARole(final @PathParam("roleName") String roleName) {
        Response response = null;
        try {
            List<PermissionDO> permissions = permissionDAO.listRolePermissions(roleName);
            if (!permissions.isEmpty()) {
                String json = objectListToJson(permissions);
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

    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @API(status = STABLE, since = "1")
    public Response updatePermission(final PermissionDO permission) {
        Response response = null;
        try {
            boolean success = permissionDAO.update(permission.getPermissionId(), permission);
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
    public Response createPermission(final PermissionDO entity) {
        Response response = null;
        try {
            PermissionId id = entity.getPermissionId();
            PermissionDO permission = new PermissionDO(new PermissionId(
                    resourcesDAO.find(id.getResource().getName(), id.getResource().getView()),
                    rolesDAO.find(id.getRole().getName())
            ));
            permission.setUuid(entity.getUuid());
            permission.setChange(entity.isChange());
            permission.setCreate(entity.isCreate());
            permission.setDelete(entity.isDelete());
            permission.setRead(entity.isRead());

            LOGGER.log("Service: " + permission.getPermissionId().getRole().toString(),
                    LogLevel.DEBUG);
            LOGGER.log("Service: " + permission.getPermissionId().getResource().toString(),
                    LogLevel.DEBUG);
            LOGGER.log("Service: " + permission.toString(), LogLevel.DEBUG);

            permissionDAO.create(permission);
            response = Response.status(Response.Status.CREATED).build();

        } catch (Exception ex) {
            LOGGER.log("ERROR CODE 500 " + ex.getMessage(), LogLevel.DEBUG);
            if (ex.equals("NullPointerException")) {
                LOGGER.catchException(ex);
            }
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return response;
    }

    @DELETE
    @Path("/{id}")
    @API(status = STABLE, since = "1")
    public Response deletePermission(final @PathParam("id") String permissionId) {
        Response response = null;
        try {
            PermissionDO selection = permissionDAO.find(permissionId);
            boolean success = permissionDAO.delete(selection.getPermissionId());

            if (success) {
                LOGGER.log("ERROR CODE 410 - deleted", LogLevel.DEBUG);
                response = Response.status(Response.Status.GONE).build();
            } else {
                LOGGER.log("ERROR CODE 403 - forbidden entity protected", LogLevel.DEBUG);
                response = Response.status(Response.Status.FORBIDDEN).build();
            }

        } catch (Exception ex) {
            String exception = ex.getClass().getSimpleName();
            Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
            if (exception.equals("EmptyResultDataAccessException")) {
                status = Response.Status.NOT_FOUND;
            }
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
    private String objectListToJson(final List<PermissionDO> permissions) {
        int cnt = 0;
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (PermissionDO permission : permissions) {
            if (cnt != 0) {
                json.append(", \n");
            }
            json.append(permissionDAO.serializeAsJson(permission));
            cnt++;
        }
        json.append("]");
        return json.toString();
    }
}
