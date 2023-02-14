package org.europa.together.domain.acl;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.europa.together.utils.StringUtils;

/**
 * PERMISSION OBJECT.
 */
@Entity
@Table(name = "ACL_PERMISSIONS",
        //CHECKSTYLE:OFF
        indexes = {
            @Index(columnList = "RESOURCE_RESOURCE", name = "acl_resource_resource"),
            @Index(columnList = "RESOURCE_TEMPLATE", name = "acl_resource_template"),
            @Index(columnList = "ROLE_NAME", name = "role_name")
        },
        //CHECKSTYLE:ON
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {
        "RESOURCE_RESOURCE", "RESOURCE_TEMPLATE", "ROLE_NAME"})
        }
)
public class PermissionDO implements Serializable {

    private static final long serialVersionUID = 30L;
    private static final int HASH = 7;

    /**
     * The name of the used database table for this domain object.
     */
    public static final String TABLE_NAME = "ACL_PERMISSIONS";

    @EmbeddedId
    private PermissionId permissionId;

    @Column(name = "IDX", unique = true, nullable = false)
    private String uuid;

    @Column(name = "DO_READ", nullable = false)
    private boolean read = false;

    @Column(name = "DO_CREATE", nullable = false)
    private boolean create = false;

    @Column(name = "DO_CHANGE", nullable = false)
    private boolean change = false;

    @Column(name = "DO_DELETE", nullable = false)
    private boolean delete = false;

    /**
     * Default Constructor.
     */
    public PermissionDO() {
        this.uuid = StringUtils.generateUUID();
    }

    /**
     * Constructor.
     *
     * @param permissionId as PermissionId
     */
    public PermissionDO(final PermissionId permissionId) {
        this.permissionId = permissionId;
        this.uuid = StringUtils.generateUUID();
    }

    @Override
    public int hashCode() {
        int hash = HASH * 17;
        hash = hash + Objects.hashCode(this.uuid);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        boolean success = false;
        if (obj != null && obj instanceof PermissionDO) {
            if (this == obj) {
                success = true;
            } else {
                final PermissionDO other = (PermissionDO) obj;
                if (Objects.equals(this.permissionId, other.permissionId)
                        && Objects.equals(this.uuid, other.uuid)) {
                    success = true;
                }
            }
        }
        return success;
    }

    @Override
    public String toString() {
        return "PermissionDO{"
                + "permissionId=" + permissionId
                + ", uuid=" + uuid
                + ", read=" + read
                + ", create=" + create
                + ", change=" + change
                + ", delete=" + delete
                + "}";
    }

    //<editor-fold defaultstate="collapsed" desc="Getter / Setter">
    /**
     * get the Identifier.
     *
     * @return uuid as String
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the identifier.
     *
     * @param uuid as String
     */
    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the PremissionID. Primary Key.
     *
     * @return permissionId as Object
     */
    public PermissionId getPermissionId() {
        return permissionId;
    }

    /**
     * Set the PremissionID. Primary Key.
     *
     * @param permissionId as Object
     */
    public void setPermissionId(final PermissionId permissionId) {
        this.permissionId = permissionId;
    }

    /**
     * Indicate the READ permission for a resource.
     *
     * @return true on success
     */
    public boolean isRead() {
        return read;
    }

    /**
     * Define the READ permission for a resource.
     *
     * @param read as boolean
     */
    public void setRead(final boolean read) {
        this.read = read;
    }

    /**
     * Indicate the CREATE permission for a resource.
     *
     * @return true on success
     */
    public boolean isCreate() {
        return create;
    }

    /**
     * Define the CREATE permission for a resource.
     *
     * @param create as boolean
     */
    public void setCreate(final boolean create) {
        this.create = create;
    }

    /**
     * Indicate the CHANGE (update) permission for a resource.
     *
     * @return true on success
     */
    public boolean isChange() {
        return change;
    }

    /**
     * Define the CHANGE (update) permission for a resource.
     *
     * @param change as boolean
     */
    public void setChange(final boolean change) {
        this.change = change;
    }

    /**
     * Indicate the DELETE permission for a resource.
     *
     * @return true on success
     */
    public boolean isDelete() {
        return delete;
    }

    /**
     * Define the DELETE permission for a resource.
     *
     * @param delete as boolean
     */
    public void setDelete(final boolean delete) {
        this.delete = delete;
    }
    //</editor-fold>
}
