package org.europa.together.application.acl;

import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.europa.together.application.GenericHbmDAO;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.business.acl.ResourcesDAO;
import org.europa.together.domain.LogLevel;
import org.europa.together.domain.acl.ResourcesDO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the ResourcesDAO.
 */
@Repository
@Transactional
public class ResourcesHbmDAO extends GenericHbmDAO<ResourcesDO, String> implements ResourcesDAO {

    private static final long serialVersionUID = 4L;
    private static final Logger LOGGER = new LogbackLogger(ResourcesDAO.class);

    /**
     * Default Constructor.
     */
    public ResourcesHbmDAO() {
        super();
        LOGGER.log("instance class", LogLevel.INFO);
    }

    @Override
    public ResourcesDO find(final String resource) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean update(final String id, final ResourcesDO object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean delete(final String resourceID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @Transactional(readOnly = true)
    public ResourcesDO find(final String resource, final String view) {
        CriteriaBuilder builder = mainEntityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<ResourcesDO> query = builder.createQuery(ResourcesDO.class);
        // create Criteria
        Root<ResourcesDO> root = query.from(ResourcesDO.class);
        query.where(builder.equal(root.get("resourceName"), resource),
                builder.equal(root.get("view"), view));
        return mainEntityManagerFactory.createQuery(query).getSingleResult();
    }

    @Override
    public boolean update(final ResourcesDO resource) {
        boolean success = false;
        if (resource != null) {
            mainEntityManagerFactory.merge(resource);
            success = true;
        }
        return success;
    }

    @Override
    public boolean delete(final ResourcesDO resource) {
        boolean success = false;
        ResourcesDO object = find(resource.getName(), resource.getView());
        if (object.isDeleteable()) {
            mainEntityManagerFactory.remove(object);
            success = true;
        } else {
            LOGGER.log("Resource(" + object.getName() + "['" + object.getView()
                    + "']) can't deleted, because it is protected.", LogLevel.DEBUG);
        }
        return success;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourcesDO> listResourcesOfSameType(final String resource) {
        CriteriaBuilder builder = mainEntityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<ResourcesDO> query = builder.createQuery(ResourcesDO.class);
        // create Criteria
        Root<ResourcesDO> root = query.from(ResourcesDO.class);
        query.where(builder.equal(root.get("resourceName"), resource));

        return mainEntityManagerFactory.createQuery(query).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourcesDO> listProtectedResources() {
        CriteriaBuilder builder = mainEntityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<ResourcesDO> query = builder.createQuery(ResourcesDO.class);
        // create Criteria
        Root<ResourcesDO> root = query.from(ResourcesDO.class);
        query.where(builder.equal(root.get("deleteable"), false));

        return mainEntityManagerFactory.createQuery(query).getResultList();
    }
}
