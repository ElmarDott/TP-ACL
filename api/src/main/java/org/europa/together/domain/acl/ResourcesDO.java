package org.europa.together.domain.acl;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * RESOURCES OBJECT.
 */
@Entity
@Table(name = "ACL_RESOURCES",
        //CHECKSTYLE:OFF
        indexes = {
            @Index(columnList = "RESOURCE", name = "acl_resource_name"),
            @Index(columnList = "TEMPLATE", name = "acl_template_view")
        }
//CHECKSTYLE:ON
)
@IdClass(ResourceId.class)
public class ResourcesDO implements Serializable {

    private static final long serialVersionUID = 40L;
    private static final int HASH = 9;

    /**
     * The name of the used database table for this domain object.
     */
    public static final String TABLE_NAME = "ACL_RESOURCES";

    @Id
    @Column(name = "RESOURCE")
    private String resourceName;

    @Id
    @Column(name = "TEMPLATE")
    private String view;

    @Column(name = "DELETEABLE")
    private boolean deleteable;

    /**
     * Default Constructor.
     */
    public ResourcesDO() {
        this.deleteable = true;
        this.view = "default";
    }

    /**
     * Constructor.
     *
     * @param resourceName as String
     */
    public ResourcesDO(final String resourceName) {
        this.resourceName = resourceName;
        this.view = "default";
        this.deleteable = true;
    }

    @Override
    public int hashCode() {
        int a = HASH + Objects.hashCode(this.resourceName);
        int b = HASH + Objects.hashCode(this.view);
        return a * b;
    }

    @Override
    public boolean equals(final Object obj) {
        boolean success = false;
        if (obj != null && obj instanceof ResourcesDO) {
            if (this == obj) {
                success = true;
            } else {
                final ResourcesDO other = (ResourcesDO) obj;
                if (Objects.equals(this.resourceName, other.resourceName)
                        && Objects.equals(this.view, other.view)) {
                    success = true;
                }
            }
        }
        return success;
    }

    @Override
    public String toString() {
        return "ResourcesDO{"
                + "name=" + resourceName
                + ", view=" + view
                + ", deleteable=" + deleteable
                + "}";
    }

    //<editor-fold defaultstate="collapsed" desc="Getter / Setter">
    /**
     * Get the name. Primary Key.
     *
     * @return name as String
     */
    public String getName() {
        return resourceName;
    }

    /**
     * Set the resource name. Primary Key. Good Resource names are: Article and
     * so on.
     *
     * @param name as String
     */
    public void setName(final String name) {
        this.resourceName = name;
    }

    /**
     * Define which template is to use for the given resource.
     *
     * @return view as String
     */
    public String getView() {
        return view;
    }

    /**
     * Define which template will be chosen to open a resource.
     *
     * @param view as String
     */
    public void setView(final String view) {
        this.view = view;
    }

    /**
     * Flag to protect that a resource can not deleted by accident. The ACL
     * contains by default some protected resources like Document and Article.
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
