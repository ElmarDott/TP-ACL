package org.europa.together.business.acl;

import java.util.List;
import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.STABLE;
import org.europa.together.business.GenericDAO;
import org.europa.together.domain.acl.PermissionDO;
import org.springframework.stereotype.Repository;

/**
 * Permissions are linked to roles. Every permission is just to one resource
 * related. A role contains multiply permissions. To avoid configuration
 * inconsistencies, it only allowed to link once a permission with a specific
 * resource and view to a role.
 *
 * @author elmar.dott@gmail.com
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface PermissionDAO extends GenericDAO<PermissionDO, String> {

    /**
     * Override the find() method to fetch a permission by the given id.
     *
     * @param permissionId as String
     * @return PermissionDO as Object
     */
    @API(status = STABLE, since = "1.0")
    PermissionDO find(String permissionId);

    /**
     * Override the find() method to fetch a permission by the given roleName,
     * resource and view.
     */
    @API(status = STABLE, since = "1.0")
    PermissionDO find(String roleName, String resource, String view);

    /**
     * List all permissions of a role. If a role not exist the result will be an
     * empty list.
     *
     * @param roleName as String
     * @return List of PermissionDO
     */
    @API(status = STABLE, since = "1.0")
    List<PermissionDO> listRolePermissions(String roleName);
}
