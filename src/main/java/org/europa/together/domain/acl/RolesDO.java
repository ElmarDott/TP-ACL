package org.europa.together.domain.acl;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * USER ROLES OBJECT. Default roles are:
 * <ul>
 * <li>Guest</li>
 * <li>User</li>
 * <li>Moderator</li>
 * <li>Administrator</li>
 * </ul>
 */
@Entity
@Table(name = "ROLES",
        //CHECKSTYLE:OFF
        indexes = {
            @Index(columnList = "NAME", name = "role_name")
        }
//CHECKSTYLE:ON
)
public class RolesDO implements Serializable {

    private static final long serialVersionUID = 50L;
    private static final int HASH = 11;

    /**
     * The name of the used database table for this domain object.
     */
    public static final String TABLE_NAME = "ROLES";

    @Id
    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "DELETEABLE")
    private boolean deleteable;

    /**
     * Default Constructor.
     */
    public RolesDO() {
        this.deleteable = true;
    }

    /**
     * Constructor.
     *
     * @param name as String
     */
    public RolesDO(final String name) {
        this.name = name;
        this.deleteable = true;
    }

    @Override
    public int hashCode() {
        int hash = HASH * 37;
        hash = hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        boolean success = false;
        if (obj != null && obj instanceof RolesDO) {
            if (this == obj) {
                success = true;
            } else {
                final RolesDO other = (RolesDO) obj;
                if (Objects.equals(this.name, other.name)) {
                    success = true;
                }
            }
        }
        return success;
    }

    @Override
    public String toString() {
        return "RolesDO{"
                + "name=" + name
                + ", description=" + description
                + ", deleteable=" + deleteable
                + "}";
    }

    //<editor-fold defaultstate="collapsed" desc="Getter / Setter">
    /**
     * Get the Role name. Primary Key.
     *
     * @return name as String
     */
    public String getName() {
        return name;
    }

    /**
     * Set the Role name. Primary Key.
     *
     * @param name as String
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get the description of a role.
     *
     * @return description as String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of a role.
     *
     * @param description as String
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Flag to protect that a role can not deleted by accident. The ACL contains
     * by default four protected roles: Guest, User, Moderator & Administrator.
     *
     * @return true on success
     */
    public boolean isDeleteable() {
        return deleteable;
    }

    /**
     * Set a flag to protect that a role can not deleted by accident. The ACL
     * contains by default four protected roles: Guest, User, Moderator &
     * Administrator.
     *
     * @param deleteable as boolean
     */
    public void setDeleteable(final boolean deleteable) {
        this.deleteable = deleteable;
    }
    //</editor-fold>
}
