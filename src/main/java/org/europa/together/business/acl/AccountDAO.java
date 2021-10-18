package org.europa.together.business.acl;

import java.sql.Timestamp;
import java.util.List;
import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.STABLE;
import org.europa.together.business.GenericDAO;
import org.europa.together.domain.acl.AccountDO;
import org.springframework.stereotype.Repository;

/**
 * An account is identified by E-Mail addresses.
 *
 * @author elmar.dott@gmail.com
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface AccountDAO extends GenericDAO<AccountDO, String> {

    /**
     * Fetch an account by his verification code, to enanble the activation /
     * registration procedure.
     *
     * @param verificationCode as String
     * @return account as AccountDO
     */
    @API(status = STABLE, since = "1.0")
    AccountDO findAccountByVerificationCode(String verificationCode);

    /**
     * Deactivates an Account. The Account PrimaryKey (ID) is the email address.
     *
     * @param email as String
     * @return true on success
     */
    @API(status = STABLE, since = "1.0")
    boolean deactivateAccount(String email);

    /**
     * Set an Account to verified. The Account PrimaryKey (ID) is the email
     * address.
     *
     * @param email as String
     * @return true on success
     */
    @API(status = STABLE, since = "1.0")
    boolean verifyAccount(String email);

    /**
     * Get all activated Accounts.
     *
     * @return List of AccountDO
     */
    @API(status = STABLE, since = "1.0")
    List<AccountDO> listActivatedAccounts();

    /**
     * Get all deactivated Accounts.
     *
     * @return List of AccountDO
     */
    @API(status = STABLE, since = "1.0")
    List<AccountDO> listDeactivatedAccounts();

    /**
     * Get all accounts, which are not verified.
     *
     * @return List of AccountDO
     */
    @API(status = STABLE, since = "1.0")
    List<AccountDO> listNotConfirmedAccounts();

    /**
     * Get all Accounts for a specified role.
     *
     * @param roleName as String
     * @return List of AccountDO
     */
    @API(status = STABLE, since = "1.0")
    List<AccountDO> listAccountsOfRole(String roleName);

    /**
     * Get all Accounts which registered before a given date.
     *
     * @param date as Timestamp
     * @return List of AccountDO
     */
    @API(status = STABLE, since = "1.0")
    List<AccountDO> listRegisterdAcountsBeforeDate(Timestamp date);

    /**
     * Get all Accounts which registered after a given date.
     *
     * @param date as Timestamp
     * @return List of AccountDO
     */
    @API(status = STABLE, since = "1.0")
    List<AccountDO> listRegisterdAcountsAfterDate(Timestamp date);
}
