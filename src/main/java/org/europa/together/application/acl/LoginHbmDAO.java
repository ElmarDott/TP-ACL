package org.europa.together.application.acl;

import java.util.Date;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.europa.together.application.GenericHbmDAO;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.business.acl.LoginDAO;
import org.europa.together.domain.LogLevel;
import org.europa.together.domain.acl.AccountDO;
import org.europa.together.domain.acl.LoginDO;
import org.europa.together.exceptions.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the LoginDAO.
 */
@Repository
@Transactional
public class LoginHbmDAO extends GenericHbmDAO<LoginDO, String> implements LoginDAO {

    private static final long serialVersionUID = 2L;
    private static final Logger LOGGER = new LogbackLogger(LoginDAO.class);

    /**
     * Default Constructor.
     */
    public LoginHbmDAO() {
        super();
        LOGGER.log("instance class", LogLevel.INFO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoginDO> getLoginsFromAccount(final String account) {
        CriteriaBuilder builder = mainEntityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<LoginDO> query = builder.createQuery(LoginDO.class);
        // create Criteria
        Root<LoginDO> root = query.from(LoginDO.class);
        query.where(builder.equal(root.get("account"), new AccountDO(account)))
                .orderBy(builder.desc(root.get("login")));

        return mainEntityManagerFactory.createQuery(query).getResultList();
    }

    @Override
    public void doLogout(final String account) throws DAOException {
        try {
            Date logout = new Date(System.currentTimeMillis());
            LoginDO login = getLoginsFromAccount(account).get(0);
            login.setLogout(logout);
            update(account, login);
            LOGGER.log(account + " successful logout.", LogLevel.DEBUG);

        } catch (Exception ex) {
            LOGGER.catchException(ex);
            throw new DAOException(account + " don't exist. can't perform a logout.");
        }
    }
}
