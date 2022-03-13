package org.europa.together.application.acl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityNotFoundException;
import org.europa.together.application.GenericHbmDAO;
import org.europa.together.application.LogbackLogger;
import org.europa.together.business.Logger;
import org.europa.together.business.acl.RolesDAO;
import org.europa.together.domain.JpaPagination;
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
    public void delete(final String roleName) throws EntityNotFoundException {
        RolesDO role = find(roleName);
        if (role != null) {
            if (role.isDeleteable()) {
                mainEntityManagerFactory.remove(role);
            }
        } else {
            throw new EntityNotFoundException("Role(" + role.getName()
                    + ") can't deleted, because it is protected.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolesDO> listProtectedRoles() {

        Map<String, Boolean> filter = new HashMap<>();
        filter.put("deleteable", Boolean.FALSE);

        JpaPagination pagination = new JpaPagination("name");
        pagination.setFilterBooleanCriteria(filter);

        return listAllElements(pagination);
    }
}
