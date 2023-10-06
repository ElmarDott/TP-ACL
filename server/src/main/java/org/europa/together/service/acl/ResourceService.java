package org.europa.together.service.acl;

import java.util.List;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.persistence.NoResultException;
import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.STABLE;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.business.acl.ResourcesDAO;
import org.europa.together.domain.JpaPagination;
import org.europa.together.domain.LogLevel;
import org.europa.together.domain.acl.ResourcesDO;
import org.europa.together.exceptions.JsonProcessingException;
import org.europa.together.utils.StringUtils;
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
@Path("/acl/" + Constraints.REST_API_VERSION + "/resource")
public class ResourceService {

    private static final Logger LOGGER = new LogbackLogger(ResourceService.class);

    @Autowired
    private ResourcesDAO resourcesDAO;

    public ResourceService() {
        LOGGER.log("instance class", LogLevel.INFO);
    }

    @GET
    @Path("/{resource}")
    @Produces({MediaType.APPLICATION_JSON})
    @API(status = STABLE, since = "1")
    public Response fetchResource(final @PathParam("resource") String resourceName,
            final @QueryParam("resourceView") String resourceView) {
        Response response = null;
        try {
            ResourcesDO resource = null;
            if (!StringUtils.isEmpty(resourceView)) {
                resource = resourcesDAO.find(resourceName, resourceView);
            } else {
                resource = resourcesDAO.find(resourceName);
            }
            String json = resourcesDAO.serializeAsJson(resource);
            response = Response.status(Response.Status.OK)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(json)
                    .encoding("UTF-8")
                    .build();

        } catch (EmptyResultDataAccessException | NoResultException ex) {
            LOGGER.log("ERROR CODE 404 " + ex.getMessage(), LogLevel.DEBUG);
            response = Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception ex) {
            LOGGER.log("ERROR CODE 500 " + ex.getMessage() + " " + ex.toString(), LogLevel.DEBUG);
            ex.printStackTrace();
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return response;
    }

    @GET
    @Path("/list")
    @Produces({MediaType.APPLICATION_JSON})
    @API(status = STABLE, since = "1")
    public Response fetchAllResources() {
        Response response = null;
        try {
            JpaPagination seekElement = new JpaPagination();
            seekElement.setPrimaryKey("resourceName");

            List<ResourcesDO> resources = resourcesDAO.listAllElements(seekElement);
            String json = objectListToJson(resources);
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
    @Path("/list/protected")
    @Produces({MediaType.APPLICATION_JSON})
    @API(status = STABLE, since = "1")
    public Response fetchProtectedResources() {
        Response response = null;
        try {
            List<ResourcesDO> resource = resourcesDAO.listProtectedResources();
            String json = objectListToJson(resource);
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
    @Path("/list/equalType/{resource}")
    @Produces({MediaType.APPLICATION_JSON})
    @API(status = STABLE, since = "1")
    public Response fetchResourceOfSameType(final @PathParam("resource") String resourceType) {
        Response response = null;
        try {
            List<ResourcesDO> resources = resourcesDAO.listResourcesOfSameType(resourceType);
            if (!resources.isEmpty()) {
                String json = objectListToJson(resources);
                response = Response.status(Response.Status.OK)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(json)
                        .encoding("UTF-8")
                        .build();
            } else {
                response = Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception ex) {
            LOGGER.log("ERROR CODE 500 " + ex.getMessage() + " " + ex.toString(), LogLevel.DEBUG);
            ex.printStackTrace();
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return response;
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @API(status = STABLE, since = "1")
    public Response updateResource(final ResourcesDO resource) {
        Response response = null;
        try {
            resourcesDAO.update(resource);
            response = Response.status(Response.Status.ACCEPTED).build();

        } catch (Exception ex) {
            String exception = ex.getClass().getSimpleName();
            Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;

            if (exception.equals("EmptyResultDataAccessException")
                    || exception.equals("EntityNotFoundException")) {
                status = Response.Status.NOT_FOUND;
            }

            LOGGER.log("ERROR CODE " + status.getStatusCode() + " - " + exception, LogLevel.DEBUG);
            response = Response.status(status).build();
        }
        return response;
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @API(status = STABLE, since = "1")
    public Response createResource(final ResourcesDO resource) {
        Response response = null;
        try {
            resourcesDAO.create(resource);
            response = Response.status(Response.Status.CREATED).build();

        } catch (Exception ex) {
            LOGGER.log("ERROR CODE 500 " + ex.getMessage(), LogLevel.DEBUG);
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return response;
    }

    @DELETE
    @Path("/{resource}")
    @API(status = STABLE, since = "1")
    public Response deleteResource(final @PathParam("resource") String resource,
            final @QueryParam("resourceView") String resourceView) {

        Response response = null;
        try {
            ResourcesDO object = resourcesDAO.find(resource, resourceView);
            if (object.isDeleteable()) {
                resourcesDAO.delete(object);
                response = Response.status(Response.Status.GONE).build();
            } else {
                response = Response.status(Response.Status.FORBIDDEN).build();
            }

        } catch (Exception ex) {
            String exception = ex.getClass().getSimpleName();
            Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;

            if (exception.equals("EmptyResultDataAccessException")
                    || exception.equals("EntityNotFoundException")) {
                status = Response.Status.NOT_FOUND;
            }
            if (exception.equals("DataIntegrityViolationException")) {
                status = Response.Status.CONFLICT;
            }

            LOGGER.log("ERROR CODE " + status.getStatusCode() + " - " + exception, LogLevel.DEBUG);
            response = Response.status(status).build();
        }
        return response;
    }

    // #########################################################################
    private String objectListToJson(final List<ResourcesDO> resources)
            throws JsonProcessingException {

        int cnt = 0;
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (ResourcesDO resource : resources) {
            if (cnt != 0) {
                json.append(", \n");
            }
            json.append(resourcesDAO.serializeAsJson(resource));
            cnt++;
        }
        json.append("]");
        return json.toString();
    }
}
