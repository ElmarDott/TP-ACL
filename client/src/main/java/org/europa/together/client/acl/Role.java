package org.europa.together.client.acl;

import java.util.ArrayList;
import java.util.List;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.JsonTools;
import org.europa.together.business.Logger;
import org.europa.together.domain.LogLevel;
import org.europa.together.domain.acl.RolesDO;
import org.europa.together.exceptions.JsonProcessingException;
import org.europa.together.utils.acl.Constraints;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author https://elmar-dott.com
 */
public class Role {

    private static final Logger LOGGER = new LogbackLogger(Role.class);
    private final String API_PATH
            = "/acl/" + Constraints.REST_API_VERSION + "/role";
    private WebTarget target;

    @Autowired
    private JsonTools<RolesDO> jsonTools;

    public Role(String baseURI) {
        LOGGER.log("instance class", LogLevel.INFO);

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);
        target = client.target(baseURI);
        LOGGER.log("BASE URI: " + target.getUri().toString()
                + " Path: " + API_PATH, LogLevel.INFO);
    }

    public RolesDO getRole(String role)
            throws JsonProcessingException, ClassNotFoundException {
        Response response = target
                .path(API_PATH).path(role)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);
        LOGGER.log("(get) HTTP STATUS CODE: " + response.getStatus(), LogLevel.INFO);
        return jsonTools
                .deserializeJsonAsObject(response.readEntity(String.class), RolesDO.class);
    }

    public void createRole(RolesDO role) {
        Response response = target
                .path(API_PATH)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(role));
        LOGGER.log("(create) HTTP STATUS CODE: " + response.getStatus(), LogLevel.INFO);
    }

    public void updateRole(RolesDO role) {
        Response response = target
                .path(API_PATH)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(role));
        LOGGER.log("(update) HTTP STATUS CODE: " + response.getStatus(), LogLevel.INFO);
    }

    public void deleteRole(String role) {
        Response response = target
                .path(API_PATH).path(role)
                .request()
                .delete(Response.class);
        LOGGER.log("(delete) HTTP STATUS CODE: " + response.getStatus(), LogLevel.INFO);
    }

    public List<RolesDO> listRoles()
            throws JsonProcessingException, ClassNotFoundException {
        List<RolesDO> roles = new ArrayList<>();
        Response response = target
                .path(API_PATH).path("/list")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);
        LOGGER.log("(list) HTTP STATUS CODE: " + response.getStatus(), LogLevel.INFO);
        roles = jsonTools
                .deserializeJsonAsList(response.readEntity(String.class));
        return roles;
    }
}
