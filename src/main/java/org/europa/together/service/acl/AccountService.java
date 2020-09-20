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
import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.STABLE;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.business.acl.AccountDAO;
import org.europa.together.business.acl.LoginDAO;
import org.europa.together.business.acl.RolesDAO;
import org.europa.together.domain.LogLevel;
import org.europa.together.domain.acl.AccountDO;
import org.europa.together.domain.acl.LoginDO;
import org.europa.together.domain.acl.RolesDO;
import org.europa.together.utils.acl.Constraints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * RESTful Service for Accounts.
 *
 * @author elmar.dott@gmail.com
 * @version 1.0
 * @since 1.0
 */
@Service
@Path(Constraints.MODULE_NAME + "/" + Constraints.REST_API_VERSION)
public class AccountService {

    private static final Logger LOGGER = new LogbackLogger(AccountService.class);

    @Autowired
    @Qualifier("accountHbmDAO")
    private AccountDAO accountDAO;

    @Autowired
    @Qualifier("loginHbmDAO")
    private LoginDAO loginDAO;

    @Autowired
    @Qualifier("rolesHbmDAO")
    private RolesDAO rolesDAO;

    public AccountService() {
        LOGGER.log("instance class", LogLevel.INFO);
    }

    @GET
    @Path("/account/{account}")
    @Produces({MediaType.APPLICATION_JSON})
    @API(status = STABLE, since = "1")
    public Response fetchAccount(final @PathParam("account") String accountId) {
        Response response = null;
        try {
            AccountDO account = accountDAO.find(accountId);
            String json = accountDAO.serializeAsJson(account);
            if (account != null) {
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
    @Path("/account/deactivate/{account}")
    @Consumes({MediaType.TEXT_PLAIN})
    @API(status = STABLE, since = "1")
    public Response deactivateAccount(final @PathParam("account") String accountId) {
        Response response = null;
        try {
            AccountDO account = accountDAO.find(accountId);
            if (account != null) {
                accountDAO.deactivateAccount(accountId);
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

    @PUT
    @Path("/account/verify/{account}")
    @Consumes({MediaType.TEXT_PLAIN})
    @API(status = STABLE, since = "1")
    public Response verifyAccount(final @PathParam("account") String accountId) {
        Response response = null;
        try {
            AccountDO account = accountDAO.find(accountId);
            if (account != null) {
                account.setVerified(true);
                accountDAO.update(accountId, account);
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

    @PUT
    @Path("/account")
    @Consumes({MediaType.APPLICATION_JSON})
    @API(status = STABLE, since = "1")
    public Response updateAccount(final AccountDO account) {
        Response response = null;
        try {
            boolean success = accountDAO.update(account.getEmail(), account);
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
    @Path("/account")
    @Consumes({MediaType.APPLICATION_JSON})
    @API(status = STABLE, since = "1")
    public Response createAccount(final AccountDO account) {
        Response response = null;
        AccountDO entry = account;

        try {
            RolesDO role = account.getRole();
            if (role == null) {
                role = rolesDAO.find("Guest");
                entry.setRole(role);
            }
            accountDAO.create(entry);
            response = Response.status(Response.Status.CREATED).build();

        } catch (Exception ex) {
            LOGGER.log("ERROR CODE 500 " + ex.getMessage(), LogLevel.DEBUG);
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return response;
    }

    @DELETE
    @Path("/account/{account}")
    @API(status = STABLE, since = "1")
    public Response deleteAccount(final @PathParam("account") String accountId) {
        Response response = null;
        try {
            boolean success = accountDAO.delete(accountId);
            if (success) {
                LOGGER.log("ERROR CODE 410 - deleted", LogLevel.DEBUG);
                response = Response.status(Response.Status.GONE).build();
            } else {
                if (accountDAO.find(accountId) == null) {
                    LOGGER.log("ERROR CODE 404 - not found", LogLevel.DEBUG);
                    response = Response.status(Response.Status.NOT_FOUND).build();
                }
            }
        } catch (Exception ex) {
            String exception = ex.getClass().getSimpleName();
            Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
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

    @GET
    @Path("/account/list")
    @Produces({MediaType.APPLICATION_JSON})
    @API(status = STABLE, since = "1")
    public Response fetchAllAccount(final @PathParam("account") String accountId) {
        Response response = null;
        try {
            List<AccountDO> accounts = accountDAO.listAllElements();
            String json = accountsObjectListToJson(accounts);
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
    @Path("/account/list/not-confirmed")
    @Produces({MediaType.APPLICATION_JSON})
    @API(status = STABLE, since = "1")
    public Response fetchNotConfirmedAccount() {
        Response response = null;
        try {
            List<AccountDO> accounts = accountDAO.listNotConfirmedAccounts();
            String json = accountsObjectListToJson(accounts);
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
    @Path("/account/list/activated")
    @Produces({MediaType.APPLICATION_JSON})
    @API(status = STABLE, since = "1")
    public Response fetchActivatedAccount() {
        Response response = null;
        try {
            List<AccountDO> accounts = accountDAO.listActivatedAccounts();
            String json = accountsObjectListToJson(accounts);
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
    @Path("/account/list/deactivated")
    @Produces({MediaType.APPLICATION_JSON})
    @API(status = STABLE, since = "1")
    public Response fetchDeactivatedAccount() {
        Response response = null;
        try {
            List<AccountDO> accounts = accountDAO.listDeactivatedAccounts();
            String json = accountsObjectListToJson(accounts);
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
    @Path("/account/list/{role}")
    @Produces({MediaType.APPLICATION_JSON})
    @API(status = STABLE, since = "1")
    public Response fetchAccountsOfRole(final @PathParam("role") String role) {
        Response response = null;
        try {
            List<AccountDO> accounts = accountDAO.listAccountsOfRole(role);
            String json = accountsObjectListToJson(accounts);
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
    @Path("/account/list/logins/{account}")
    @Produces({MediaType.APPLICATION_JSON})
    @API(status = STABLE, since = "1")
    public Response fetchLoginsOfAccount(final @PathParam("account") String accountId) {
        Response response = null;
        try {
            List<LoginDO> logins = loginDAO.getLoginsFromAccount(accountId);
            String json = loginObjectListToJson(logins);
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

    // #########################################################################
    private String accountsObjectListToJson(final List<AccountDO> accounts) {
        int cnt = 0;
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (AccountDO account : accounts) {
            if (cnt != 0) {
                json.append(", \n");
            }
            json.append(accountDAO.serializeAsJson(account));
            cnt++;
        }
        json.append("]");
        return json.toString();
    }

    private String loginObjectListToJson(final List<LoginDO> logins) {
        int cnt = 0;
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (LoginDO login : logins) {
            if (cnt != 0) {
                json.append(", \n");
            }
            json.append(loginDAO.serializeAsJson(login));
            cnt++;
        }
        json.append("]");
        return json.toString();
    }
}
