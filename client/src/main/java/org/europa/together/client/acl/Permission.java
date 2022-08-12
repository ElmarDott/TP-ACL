package org.europa.together.client.acl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.domain.LogLevel;
import org.europa.together.domain.acl.PermissionDO;
import org.europa.together.utils.acl.Constraints;
import org.glassfish.jersey.client.ClientConfig;

/**
 * @author https://elmar-dott.com
 */
public class Permission {

    private static final Logger LOGGER = new LogbackLogger(Permission.class);
    private final String API_PATH
            = "/acl/" + Constraints.REST_API_VERSION + "/permission";
    private WebTarget target;

    public Permission(String baseURI) {
        LOGGER.log("instance class", LogLevel.INFO);

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);
        target = client.target(baseURI);
        LOGGER.log("BASE URI: " + target.getUri().toString()
                + " Path: " + API_PATH, LogLevel.INFO);
    }

    public PermissionDO getPermission(String permission) {

        Response response = target
                .path(API_PATH).path(permission)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);
        LOGGER.log("(get) HTTP STATUS CODE: " + response.getStatus(), LogLevel.INFO);

        return response.readEntity(PermissionDO.class);
    }

    public void createPermission(PermissionDO permission) {
        Response response = target
                .path(API_PATH)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(permission));
        LOGGER.log("(create) HTTP STATUS CODE: " + response.getStatus(), LogLevel.INFO);
    }

    public void updatePermission(PermissionDO permission) {
        Response response = target
                .path(API_PATH)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(permission));
        LOGGER.log("(update) HTTP STATUS CODE: " + response.getStatus(), LogLevel.INFO);
    }

    public void deletePermission(String permission) {
        Response response = target
                .path(API_PATH).path(permission)
                .request()
                .delete(Response.class);
        LOGGER.log("(delete) HTTP STATUS CODE: " + response.getStatus(), LogLevel.INFO);
    }

    public List<PermissionDO> listPermissions() throws JsonProcessingException {
        List<PermissionDO> permissions = new ArrayList<>();
        Response response = target
                .path(API_PATH).path("/list")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);
        LOGGER.log("(list) HTTP STATUS CODE: " + response.getStatus(), LogLevel.INFO);

        ObjectMapper mapper = new ObjectMapper();
        PermissionDO[] result = mapper.readValue(response.readEntity(String.class), PermissionDO[].class);
        permissions = Arrays.asList(result);
        return permissions;
    }
}
