package org.europa.together.application.acl;

import java.sql.Timestamp;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.europa.together.application.GenericHbmDAO;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.business.acl.AccountDAO;
import org.europa.together.domain.LogLevel;
import org.europa.together.domain.acl.AccountDO;
import org.europa.together.domain.acl.RolesDO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the AccountDAO.
 */
@Repository
@Transactional
public class AccountHbmDAO extends GenericHbmDAO<AccountDO, String> implements AccountDAO {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = new LogbackLogger(AccountDAO.class);

    /**
     * Default Constructor.
     */
    public AccountHbmDAO() {
        super();
        LOGGER.log("instance class", LogLevel.INFO);
    }

    @Override
    public boolean deactivateAccount(final String email) {
        boolean success = false;
        AccountDO account = find(email);
        if (account.isActivated()) {
            account.setActivated(false);
            success = true;
        } else {
            LOGGER.log("Account " + email + " already is deactivated.", LogLevel.WARN);
        }
        return success;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDO> listActivatedAccounts() {
        CriteriaBuilder builder = mainEntityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<AccountDO> query = builder.createQuery(AccountDO.class);
        // create Criteria
        Root<AccountDO> root = query.from(AccountDO.class);
        query.where(builder.equal(root.get("activated"), true));

        return mainEntityManagerFactory.createQuery(query).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDO> listDeactivatedAccounts() {
        CriteriaBuilder builder = mainEntityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<AccountDO> query = builder.createQuery(AccountDO.class);
        // create Criteria
        Root<AccountDO> root = query.from(AccountDO.class);
        query.where(builder.equal(root.get("activated"), false));

        return mainEntityManagerFactory.createQuery(query).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDO> listNotConfirmedAccounts() {
        CriteriaBuilder builder = mainEntityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<AccountDO> query = builder.createQuery(AccountDO.class);
        // create Criteria
        Root<AccountDO> root = query.from(AccountDO.class);
        query.where(builder.equal(root.get("verified"), false));

        return mainEntityManagerFactory.createQuery(query).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDO> listAccountsOfRole(final String roleName) {
        CriteriaBuilder builder = mainEntityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<AccountDO> query = builder.createQuery(AccountDO.class);
        // create Criteria
        Root<AccountDO> root = query.from(AccountDO.class);
        query.where(builder.equal(root.get("role"), new RolesDO(roleName)));

        return mainEntityManagerFactory.createQuery(query).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDO> listRegisterdAcountsBeforeDate(final Timestamp date) {
        CriteriaBuilder builder = mainEntityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<AccountDO> query = builder.createQuery(AccountDO.class);
        // create Criteria
        Root<AccountDO> root = query.from(AccountDO.class);
        query.where(builder.lessThanOrEqualTo(root.get("registrationDate"), date));

        return mainEntityManagerFactory.createQuery(query).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDO> listRegisterdAcountsAfterDate(final Timestamp date) {
        CriteriaBuilder builder = mainEntityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<AccountDO> query = builder.createQuery(AccountDO.class);
        // create Criteria
        Root<AccountDO> root = query.from(AccountDO.class);
        query.where(builder.greaterThanOrEqualTo(root.get("registrationDate"), date));

        return mainEntityManagerFactory.createQuery(query).getResultList();
    }
}
