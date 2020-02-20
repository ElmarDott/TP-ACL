package org.europa.together.business.acl;

import java.util.List;
import org.apiguardian.api.API;
import static org.apiguardian.api.API.Status.STABLE;
import org.europa.together.business.GenericDAO;
import org.europa.together.domain.acl.ResourcesDO;
import org.springframework.stereotype.Repository;

/**
 * A resource is linked to a permission. This avoid duplications and simplify
 * the administration.
 *
 * @author elmar.dott@gmail.com
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface ResourcesDAO extends GenericDAO<ResourcesDO, String> {

    /**
     * Fetch a specific resource defined by the resource name and the view.
     *
     * @param resource as String
     * @param view as String
     * @return ResourceDo as Object
     */
    @API(status = STABLE, since = "1.0")
    ResourcesDO find(String resource, String view);

    /**
     * Overwrite the regular DAO update method, because of composite Primary
     * Key.
     *
     * @param resource as ResourceDO
     * @return true on success
     */
    @API(status = STABLE, since = "1.0")
    boolean update(final ResourcesDO resource);

    /**
     * If the resource object exist in the persistence context, it will be
     * deleted.
     *
     * @param resource as ResourceDO
     * @return true on success
     */
    @API(status = STABLE, since = "1.0")
    boolean delete(ResourcesDO resource);

    /**
     * Fetch a list of resources with the same name. A system could contain
     * multiply resources with the same name. To distinguish this resources a
     * view is defined. The default entry for views is <b>default</b>.
     *
     * @param resource as string
     * @return List of ResourcesDO
     */
    @API(status = STABLE, since = "1.0")
    List<ResourcesDO> listResourcesOfSameType(String resource);

    /**
     * Return a list of all resources which protected against deleting, to
     * prevent accidents.
     *
     * @return List of ResourcesDO
     */
    @API(status = STABLE, since = "1.0")
    List<ResourcesDO> listProtectedResources();
}
