package org.europa.together.domain.acl;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * ID CLASS for a permission to map a resource to a role.
 */
@Embeddable
public class PermissionId implements Serializable {

    private static final long serialVersionUID = 31L;

    @ManyToOne
    @JoinColumn(name = "ROLE_NAME")
    private RolesDO role;

    @OneToOne
    @JoinColumns({
        @JoinColumn(name = "RESOURCE_RESOURCE"),
        @JoinColumn(name = "RESOURCE_TEMPLATE")
    })
    private ResourcesDO resource;

    /**
     * Default Constructor.
     */
    public PermissionId() {
        /* Not in use. */
    }

    /**
     * Constructor.
     *
     * @param resource as Resource
     * @param role as Role
     */
    public PermissionId(final ResourcesDO resource, final RolesDO role) {
        this.resource = resource;
        this.role = role;
    }

    //<editor-fold defaultstate="collapsed" desc="Getter / Setter">
    /**
     * Get the resources for a permission.
     *
     * @return a resource as Object.
     */
    public ResourcesDO getResource() {
        return resource;
    }

    /**
     * Set the resources for a permission.
     *
     * @param resource as Object
     */
    public void setResource(final ResourcesDO resource) {
        this.resource = resource;
    }

    /**
     * Get the role for a permission.
     *
     * @return role as object
     */
    public RolesDO getRole() {
        return role;
    }

    /**
     *
     * Set the role for a permission.
     *
     * @param role as Object
     */
    public void setRole(final RolesDO role) {
        this.role = role;
    }
    //</editor-fold>
}
