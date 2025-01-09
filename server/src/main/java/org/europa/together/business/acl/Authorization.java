package org.europa.together.business.acl;

import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.STABLE;
import org.europa.together.domain.acl.AccountDO;
import org.europa.together.domain.acl.ResourcesDO;
import org.springframework.stereotype.Component;

/**
 * Define the dynamic mechanism for the autorization to execute actions.
 */
@API(status = STABLE, since = "1.0", consumers = "JakartaAuthorization")
@Component
public interface Authorization {

    /**
     * The permission is given when the role of the current user is fetched and
     * the configured permission for this action is given.
     *
     * @param account as AcoountDO
     * @param resource as ResourcesDO
     * @param action as String
     * @return true on successs
     */
    @API(status = STABLE, since = "1.0")
    boolean accountHasPermissionForAction(final AccountDO account, final ResourcesDO resource,
            final String action);
}
