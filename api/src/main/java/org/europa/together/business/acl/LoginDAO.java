package org.europa.together.business.acl;

import java.util.List;
import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.STABLE;
import org.europa.together.business.GenericDAO;
import org.europa.together.domain.acl.LoginDO;
import org.europa.together.exceptions.DAOException;
import org.springframework.stereotype.Repository;

/**
 * Every account can have multiply logins. The logins track the last activity to
 * increase security. Every user can see his last logins to detect unauthorized
 * access.
 *
 * @author elmar.dott@gmail.com
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface LoginDAO extends GenericDAO<LoginDO, String> {

    /**
     * Get all Logins by a given account. The account identifier is the e-mail
     * address.
     *
     * @param account as String
     * @return List of LoginDO
     */
    @API(status = STABLE, since = "1.0")
    List<LoginDO> getLoginsFromAccount(String account);

    /**
     * Fethc the last (newest) login object of an account to perform a logout. A
     * logout means, that the logout timestamp will set to the datarow.
     *
     * @param account as String
     * @throws org.europa.together.exceptions.DAOException
     */
    @API(status = STABLE, since = "1.0")
    void doLogout(String account) throws DAOException;
}
