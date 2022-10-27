package org.europa.together.orchestration.acl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.STABLE;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.business.MailClient;
import org.europa.together.business.acl.AccountDAO;
import org.europa.together.domain.LogLevel;
import org.europa.together.domain.Mail;
import org.europa.together.domain.acl.AccountDO;
import org.europa.together.exceptions.DAOException;
import org.europa.together.service.MailClientService;
import org.europa.together.utils.acl.Constraints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User Registration Service.
 *
 * @author elmar.dott@gmail.com
 * @version 1.0
 * @since 1.0
 */
@Service
@Path("acl/" + Constraints.REST_API_VERSION + "/")
public class Registration {

    private static final Logger LOGGER = new LogbackLogger(Registration.class);
    private static WebTarget target;
    private static final String API_PATH
            = Constraints.MODULE_NAME + "/" + Constraints.REST_API_VERSION;

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private MailClient mailClient;

    public Registration() {
        LOGGER.log("instance class", LogLevel.INFO);
        // check if account not exist
        // add new account (deactivate)
        // send mail
        // (verify) activate account by Mail Link
        // resend activation Link
    }

    @Path("/register")
    @API(status = STABLE, since = "1")
    public Response register(@QueryParam("accountID") String accountID,
            @QueryParam("accountPwd") String accountPwd)
            throws MessagingException, DAOException {

        Response response = null;
        if (lookup(accountID) == null) {

            AccountDO account = new AccountDO(accountID);
            account.setPassword(accountPwd);

            accountDAO.create(account);
            this.mail(accountID, "subject", "content");
            response = Response.status(Response.Status.CREATED)
                    .encoding("UTF-8")
                    .build();

        } else {
            response = Response.status(Response.Status.CONFLICT)
                    .encoding("UTF-8")
                    .build();
        }
        return response;
    }

//    @Path("/verify/{activationCode}")
    @API(status = STABLE, since = "1")
    public void verifyAccount(@PathParam("activationCode") String activationCode) throws DAOException {
        //TODO: DDOS Protection - more than XX Requests in 30 of the same IP or with
        // the same code needed to be blocked

        AccountDO account = accountDAO.findAccountByVerificationCode(activationCode);
        account.setVerified();
        account.setDeactivated();

        if (account != null) {
            accountDAO.update(account.getEmail(), account);
        } else {
            LOGGER.log("Verification Code " + activationCode + " don't exist.", LogLevel.WARN);
        }
    }

    private AccountDO lookup(final String email) {
        return accountDAO.find(email);
    }

    private void mail(final String mailAdress, final String subject, final String content)
            throws AddressException, MessagingException {

        Mail mail = new Mail();
        mail.setSubject(subject);
        mail.setMessage(content);
        mail.addRecipent(mailAdress);

        MailClientService postman = new MailClientService();
        postman.loadConfiguration();
        postman.sendEmail(mail);
    }

}
