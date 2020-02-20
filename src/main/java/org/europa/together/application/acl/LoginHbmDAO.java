package org.europa.together.application.acl;

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
        query.where(builder.equal(root.get("account"), new AccountDO(account)));

        return mainEntityManagerFactory.createQuery(query).getResultList();
    }
}
