package org.europa.together.application.acl;

import java.util.List;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.business.acl.Authorization;
import org.europa.together.business.acl.PermissionDAO;
import org.europa.together.domain.LogLevel;
import org.europa.together.domain.acl.AccountDO;
import org.europa.together.domain.acl.PermissionDO;
import org.europa.together.domain.acl.ResourcesDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the Authorization to access functions.
 */
@Repository
@Transactional
public class JakartaAuthorization implements Authorization {

    private static final Logger LOGGER = new LogbackLogger(JakartaAuthorization.class);

    /**
     * READ Action.
     */
    public static final String READ = "read";

    /**
     * CREATE ACtion.
     */
    public static final String CREATE = "create";

    /**
     * CHANGE Action.
     */
    public static final String CHANGE = "change";

    /**
     * Delete Action.
     */
    public static final String DELETE = "delete";

    @Autowired
    private PermissionDAO permissionDAO;

    public JakartaAuthorization() {
        LOGGER.log("instance class", LogLevel.INFO);
    }

    @Override
    public boolean accountHasPermissionForAction(final AccountDO account, final ResourcesDO resource,
            final String action) {
        boolean allowed = false;

        if (account != null && resource != null) {
            LOGGER.log(account.toString(), LogLevel.DEBUG);
            String userRole = account.getRole().getName();
            LOGGER.log("Permissions for Role: " + userRole, LogLevel.DEBUG);

            List<PermissionDO> permissions
                    = permissionDAO.listRolePermissions(userRole);

            if (permissions != null) {
                LOGGER.log(permissions.toString(), LogLevel.DEBUG);

                for (PermissionDO element : permissions) {
                    if (resource.equals(element.getResource())) {
                        if (action.equals(READ) && element.isRead()) {
                            allowed = true;
                        } else if (action.equals(DELETE) && element.isDelete()) {
                            allowed = true;
                        } else if (action.equals(CREATE) && element.isCreate()) {
                            allowed = true;
                        } else if (action.equals(CHANGE) && element.isChange()) {
                            allowed = true;
                        }
                    }
                }
            }
        }
        return allowed;
    }
}
