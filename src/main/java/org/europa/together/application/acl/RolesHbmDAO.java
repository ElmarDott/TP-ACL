package org.europa.together.application.acl;

import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.europa.together.application.GenericHbmDAO;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.business.acl.RolesDAO;
import org.europa.together.domain.LogLevel;
import org.europa.together.domain.acl.RolesDO;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the RolesDAO.
 */
@Repository
@Transactional
public class RolesHbmDAO extends GenericHbmDAO<RolesDO, String> implements RolesDAO {

    private static final long serialVersionUID = 5L;
    private static final Logger LOGGER = new LogbackLogger(RolesDAO.class);

    /**
     * Default Constructor.
     */
    public RolesHbmDAO() {
        super();
        LOGGER.log("instance class", LogLevel.INFO);
    }

    @Override
    public boolean delete(final String roleName) {
        boolean success = false;
        RolesDO role = find(roleName);
        if (role.isDeleteable()) {
            mainEntityManagerFactory.remove(role);
            success = true;
        } else {
            LOGGER.log("Role(" + role.getName() + ") can't deleted, because it is protected.",
                    LogLevel.DEBUG);
        }
        return success;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolesDO> listProtectedRoles() {
        CriteriaBuilder builder = mainEntityManagerFactory.getCriteriaBuilder();
        CriteriaQuery<RolesDO> query = builder.createQuery(RolesDO.class);
        // create Criteria
        Root<RolesDO> root = query.from(RolesDO.class);
        query.where(builder.equal(root.get("deleteable"), false));

        return mainEntityManagerFactory.createQuery(query).getResultList();
    }
}
