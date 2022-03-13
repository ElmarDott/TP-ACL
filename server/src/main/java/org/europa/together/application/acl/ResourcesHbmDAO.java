package org.europa.together.application.acl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.europa.together.application.GenericHbmDAO;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.business.acl.ResourcesDAO;
import org.europa.together.domain.JpaPagination;
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
    @Deprecated
    public void update(final String id, final ResourcesDO object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @Deprecated
    public void delete(final String resourceID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @Transactional(readOnly = true)
    public ResourcesDO find(final String resource) {
        return find(resource, "default");
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
    public void update(final ResourcesDO resource) throws EntityNotFoundException {
        if (resource != null
                && this.find(resource.getName(), resource.getView()) != null) {
            mainEntityManagerFactory.merge(resource);
            LOGGER.log("DAO (" + resource.getClass().getSimpleName() + ") update",
                    LogLevel.TRACE);
        } else {
            throw new EntityNotFoundException("update: " + resource.toString());
        }
    }

    @Override
    public boolean delete(final ResourcesDO resource) throws EntityNotFoundException {
        boolean success = false;
        ResourcesDO object = this.find(resource.getName(), resource.getView());
        if (object != null) {
            if (object.isDeleteable()) {
                mainEntityManagerFactory.remove(object);
                success = true;
            }
        }
        return success;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourcesDO> listResourcesOfSameType(final String resource) {

        Map<String, String> filter = new HashMap<>();
        filter.put("resourceName", resource);

        JpaPagination pagination = new JpaPagination("resourceName");
        pagination.setFilterStringCriteria(filter);

        return listAllElements(pagination);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourcesDO> listProtectedResources() {

        Map<String, Boolean> filter = new HashMap<>();
        filter.put("deleteable", Boolean.FALSE);

        JpaPagination pagination = new JpaPagination("resourceName");
        pagination.setFilterBooleanCriteria(filter);

        return listAllElements(pagination);
    }
}
