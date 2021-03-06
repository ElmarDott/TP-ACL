package org.europa.together.domain.acl;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * RESOURCES OBJECT.
 */
@Entity
@Table(name = "RESOURCES",
        //CHECKSTYLE:OFF
        indexes = {
            @Index(columnList = "RESOURCE", name = "resource_name"),
            @Index(columnList = "TEMPLATE", name = "template_view")
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
    public static final String TABLE_NAME = "RESOURCES";

    @Id
    @Column(name = "RESOURCE")
    private String resourceName;

    @Id
    @Column(name = "TEMPLATE")
    private String view;

    @Column(name = "ACTIONS")
    private String actions;

    @Column(name = "DELETEABLE")
    private boolean deleteable;

    /**
     * Default Constructor.
     */
    public ResourcesDO() {
        this.deleteable = true;
        this.view = "default";
        this.actions = "ALL";
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
        this.actions = "ALL";
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
                + ", actions=" + actions
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
     * Fetch all possible actions, as comma separated String, for a resource.
     * Action could be: approval, publish, reject and so on.
     *
     * @return actions as String
     */
    public String getActions() {
        return actions;
    }

    /**
     * Ad actions like: approval, publish, reject and so on to a resource.
     *
     * @param actions as String
     */
    public void setActions(final String actions) {
        this.actions = actions;
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
