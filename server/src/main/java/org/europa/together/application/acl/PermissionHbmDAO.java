package org.europa.together.application.acl;

import java.util.List;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import org.europa.together.application.GenericHbmDAO;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.business.acl.PermissionDAO;
import org.europa.together.domain.LogLevel;
import org.europa.together.domain.acl.PermissionDO;
import org.europa.together.domain.acl.PermissionId;
import org.europa.together.domain.acl.RolesDO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the PermissionDAO.
 */
@Repository
@Transactional
public class PermissionHbmDAO extends GenericHbmDAO<PermissionDO, PermissionId>
        implements PermissionDAO {

    private static final long serialVersionUID = 3L;
    private static final Logger LOGGER = new LogbackLogger(PermissionDAO.class);

    /**
     * Default Constructor.
     */
    public PermissionHbmDAO() {
        super();
        LOGGER.log("instance class", LogLevel.INFO);
    }

    @Override
    public PermissionDO find(final String permissionId) {
        PermissionDO entry;
        CriteriaBuilder builder = mainEntityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<PermissionDO> query = builder.createQuery(PermissionDO.class);
        //Criteria SQL Parameters
        ParameterExpression<String> paramPermissionId = builder.parameter(String.class);
        // create Criteria
        Root<PermissionDO> root = query.from(PermissionDO.class);
        //Prevent SQL Injections
        query.where(builder.equal(root.get("uuid"), paramPermissionId));
        // wire queries together with parameters
        TypedQuery<PermissionDO> result = mainEntityManagerFactory.createQuery(query);
        result.setParameter(paramPermissionId, permissionId);

        entry = result.getSingleResult();
        return entry;
    }

    @Override
    @Transactional(readOnly = true)
    //TODO: pagination
    public List<PermissionDO> listRolePermissions(final String roleName) {

        CriteriaBuilder builder = mainEntityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<PermissionDO> query = builder.createQuery(PermissionDO.class);
        // create Criteria
        Root<PermissionDO> root = query.from(PermissionDO.class);
        query.where(builder.equal(root.get("permissionId").get("role"), new RolesDO(roleName)));
        query.orderBy(builder.asc(root.get("uuid")));

        return mainEntityManagerFactory.createQuery(query).getResultList();
    }
}
