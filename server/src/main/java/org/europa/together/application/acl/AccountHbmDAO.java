package org.europa.together.application.acl;

import java.sql.Timestamp;
import java.util.List;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Root;
import org.europa.together.application.GenericHbmDAO;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.business.acl.AccountDAO;
import org.europa.together.domain.ConfigurationDO;
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
    public AccountDO findAccountByVerificationCode(final String verificationCode) {
        AccountDO entry;
        CriteriaBuilder builder = mainEntityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<AccountDO> query = builder.createQuery(AccountDO.class);
        // create Criteria
        Root<AccountDO> root = query.from(AccountDO.class);
        //Criteria SQL Parameters
        ParameterExpression<String> paramVerificationCode = builder.parameter(String.class);
        //Prevent SQL Injections
        query.where(builder.equal(root.get("verificationCode"), paramVerificationCode));
        // wire queries tog parametersether with parameters
        TypedQuery<AccountDO> result = mainEntityManagerFactory.createQuery(query);
        result.setParameter(paramVerificationCode, verificationCode);

        entry = result.getSingleResult();
        return entry;
    }

    @Override
    public boolean deactivateAccount(final String email) {
        boolean success = false;
        AccountDO account = find(email);
        if (account.isDeactivated() != null) {
            account.setDeactivated();
            success = true;
        } else {
            LOGGER.log("Account " + email + " already is deactivated.", LogLevel.WARN);
        }
        return success;
    }

    @Override
    public boolean verifyAccount(final String email) {
        boolean success = false;
        AccountDO account = find(email);
        if (account.isVerified() != null) {
            account.setVerified();
            success = true;
        } else {
            LOGGER.log("Account " + email + " already is verified.", LogLevel.WARN);
        }
        return success;
    }

    @Override
    @Transactional(readOnly = true)
    //TODO: pagination
    public List<AccountDO> listActivatedAccounts() {
        CriteriaBuilder builder = mainEntityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<AccountDO> query = builder.createQuery(AccountDO.class);
        // create Criteria
        Root<AccountDO> root = query.from(AccountDO.class);
        query.where(builder.isNull(root.get("deactivated")));
        query.orderBy(builder.asc(root.get("email")));

        return mainEntityManagerFactory.createQuery(query).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    //TODO: pagination
    public List<AccountDO> listDeactivatedAccounts() {
        CriteriaBuilder builder = mainEntityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<AccountDO> query = builder.createQuery(AccountDO.class);
        // create Criteria
        Root<AccountDO> root = query.from(AccountDO.class);
        query.where(builder.isNotNull(root.get("deactivated")));
        query.orderBy(builder.asc(root.get("email")));

        return mainEntityManagerFactory.createQuery(query).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    //TODO: pagination
    public List<AccountDO> listNotConfirmedAccounts() {
        CriteriaBuilder builder = mainEntityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<AccountDO> query = builder.createQuery(AccountDO.class);
        // create Criteria
        Root<AccountDO> root = query.from(AccountDO.class);
        query.where(builder.isNull(root.get("verified")));
        query.orderBy(builder.asc(root.get("email")));

        return mainEntityManagerFactory.createQuery(query).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    //TODO: pagination
    public List<AccountDO> listAccountsOfRole(final String roleName) {
        CriteriaBuilder builder = mainEntityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<AccountDO> query = builder.createQuery(AccountDO.class);
        // create Criteria
        Root<AccountDO> root = query.from(AccountDO.class);
        query.where(builder.equal(root.get("role"), new RolesDO(roleName)));
        query.orderBy(builder.asc(root.get("email")));

        return mainEntityManagerFactory.createQuery(query).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    //TODO: pagination
    public List<AccountDO> listRegisterdAcountsBeforeDate(final Timestamp date) {
        CriteriaBuilder builder = mainEntityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<AccountDO> query = builder.createQuery(AccountDO.class);
        // create Criteria
        Root<AccountDO> root = query.from(AccountDO.class);
        query.where(builder.lessThanOrEqualTo(root.get("registered"), date));
        query.orderBy(builder.asc(root.get("email")));

        return mainEntityManagerFactory.createQuery(query).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    //TODO: pagination
    public List<AccountDO> listRegisterdAcountsAfterDate(final Timestamp date) {
        CriteriaBuilder builder = mainEntityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<AccountDO> query = builder.createQuery(AccountDO.class);
        // create Criteria
        Root<AccountDO> root = query.from(AccountDO.class);
        query.where(builder.greaterThanOrEqualTo(root.get("registered"), date));
        query.orderBy(builder.asc(root.get("email")));

        return mainEntityManagerFactory.createQuery(query).getResultList();
    }
}
