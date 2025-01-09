package org.europa.together.orchestration.acl;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.STABLE;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.CryptoTools;
import org.europa.together.business.Logger;
import org.europa.together.business.acl.AccountDAO;
import org.europa.together.business.acl.LoginDAO;
import org.europa.together.domain.HashAlgorithm;
import org.europa.together.domain.LogLevel;
import org.europa.together.domain.acl.AccountDO;
import org.europa.together.domain.acl.LoginDO;
import org.europa.together.exceptions.DAOException;
import org.europa.together.utils.acl.Constraints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Authehtification Service.
 *
 * @author elmar.dott@gmail.com
 * @version 1.0
 * @since 1.0
 */
@Service
@Path("acl/" + Constraints.REST_API_VERSION)
public class Authentification {

    private static final Logger LOGGER = new LogbackLogger(Authentification.class);

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private LoginDAO loginDAO;

    @Autowired
    private CryptoTools cryptoTools;

    /**
     * Default Constructor.
     */
    public Authentification() {
        LOGGER.log("instance class", LogLevel.INFO);
    }

    @GET
    @Path("/login")
    @API(status = STABLE, since = "1")
    /**
     * Login procedure for ACL. <br><br>
     *
     * 200: OK - User & Password correct<br>
     * 401: UNAUTHORIZED - Password wrong<br>
     * 404: NOT FOUND - User don't exist
     */
    public Response login(@QueryParam("loginId") String loginId,
            @QueryParam("loginPwd") String loginPwd)
            throws DAOException {
        Response response = null;

        try {
            String password = "";
            AccountDO account = accountDAO.find(loginId);

            LOGGER.log(account.toString(), LogLevel.INFO);

            password = account.getPassword();
            String pwdComperator = cryptoTools.calculateHash(
                    account.getVerificationCode() + loginPwd, HashAlgorithm.SHA3512);

            if (password.equals(pwdComperator)) {

                response = Response.status(Response.Status.OK)
                        .encoding("UTF-8")
                        .build();

                LoginDO login = new LoginDO(account);
                loginDAO.create(login);

                LOGGER.log("comperator: TRUE", LogLevel.INFO);
            } else {
                response = Response.status(Response.Status.UNAUTHORIZED)
                        .encoding("UTF-8")
                        .build();
                LOGGER.log("comperator: FALSE", LogLevel.INFO);
            }

        } catch (NullPointerException ex) {
            LOGGER.catchException(ex);

            response = Response.status(Response.Status.NOT_FOUND)
                    .encoding("UTF-8")
                    .build();
        }

        LOGGER.log("HTTP STATUS: " + response.getStatus(), LogLevel.DEBUG);
        return response;
    }

    @GET
    @Path("/logout")
    @API(status = STABLE, since = "1")
    public Response logout(@QueryParam("loginId") String loginId)
            throws DAOException {
        Response response = null;

        try {
            loginDAO.doLogout(loginId);

            // kill session & invlid cookie
            response = Response.status(Response.Status.OK)
                    .encoding("UTF-8")
                    .build();

        } catch (DAOException ex) {
            response = Response.status(Response.Status.NOT_FOUND)
                    .encoding("UTF-8")
                    .build();
        }
        return response;
    }

}
