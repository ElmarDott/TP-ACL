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
import org.europa.together.domain.acl.AccountDO;
import org.europa.together.utils.acl.Constraints;
import org.glassfish.jersey.client.ClientConfig;

/**
 * @author https://elmar-dott.com
 */
public class Account {

    private static final Logger LOGGER = new LogbackLogger(Account.class);
    private static final String API_PATH
            = "/acl/" + Constraints.REST_API_VERSION + "/account";
    private WebTarget target;

    public Account(String baseURI) {
        LOGGER.log("instance class", LogLevel.INFO);

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);
        target = client.target(baseURI);
        LOGGER.log("BASE URI: " + target.getUri().toString()
                + " Path: " + API_PATH, LogLevel.INFO);
    }

    public AccountDO getAccount(String account) throws JsonProcessingException {
        Response response = target
                .path(API_PATH).path(account)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);
        LOGGER.log("(get) HTTP STATUS CODE: " + response.getStatus(), LogLevel.INFO);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response.readEntity(String.class), AccountDO.class);
    }

    public void createAccount(AccountDO account) {
        Response response = target
                .path(API_PATH)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(account));
        LOGGER.log("(create) HTTP STATUS CODE: " + response.getStatus(), LogLevel.INFO);
    }

    public void updateAccount(AccountDO account) {
        Response response = target
                .path(API_PATH)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.json(account));
        LOGGER.log("(update) HTTP STATUS CODE: " + response.getStatus(), LogLevel.INFO);
    }

    public void deleteAccount(String account) {
        Response response = target
                .path(API_PATH).path(account)
                .request()
                .delete(Response.class);
        LOGGER.log("(delete) HTTP STATUS CODE: " + response.getStatus(), LogLevel.INFO);
    }

    public List<AccountDO> listAccounts() throws JsonProcessingException {
        List<AccountDO> accounts = new ArrayList<>();
        Response response = target
                .path(API_PATH).path("/list")
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);
        LOGGER.log("(list) HTTP STATUS CODE: " + response.getStatus(), LogLevel.INFO);

        ObjectMapper mapper = new ObjectMapper();
        AccountDO[] result = mapper.readValue(response.readEntity(String.class), AccountDO[].class);
        accounts = Arrays.asList(result);
        return accounts;
    }
}
