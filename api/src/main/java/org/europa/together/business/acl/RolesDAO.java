package org.europa.together.business.acl;

import java.util.List;
import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.STABLE;
import org.europa.together.business.GenericDAO;
import org.europa.together.domain.acl.RolesDO;
import org.springframework.stereotype.Repository;

/**
 * Every account is just with one role related. The security design do not
 * support inheritance of role objects. This decision reduce the complexity in
 * the administration.
 *
 * @author elmar.dott@gmail.com
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface RolesDAO extends GenericDAO<RolesDO, String> {

    /**
     * Return a list of all roles which protected against deleting, to prevent
     * accidents.
     *
     * @return List of RolesDO
     */
    @API(status = STABLE, since = "1.0")
    List<RolesDO> listProtectedRoles();
}
