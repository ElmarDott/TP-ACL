package org.europa.together.domain.acl;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite Primary Key for ResourecesDO.
 */
public class ResourceId implements Serializable {

    private static final long serialVersionUID = 41L;
    private static final int HASH = 11;

    private String resourceName;
    private String view;

    @Override
    public int hashCode() {
        int a = HASH + Objects.hashCode(this.resourceName);
        int b = HASH + Objects.hashCode(this.view);
        return a * b;
    }

    @Override
    public boolean equals(final Object obj) {
        boolean success = false;
        if (obj != null && obj instanceof ResourceId) {
            if (this == obj) {
                success = true;
            } else {
                final ResourceId other = (ResourceId) obj;
                if (Objects.equals(this.resourceName, other.resourceName)
                        && Objects.equals(this.view, other.view)) {
                    success = true;
                }
            }
        }
        return success;
    }

    //<editor-fold defaultstate="collapsed" desc="Getter / Setter">
    /**
     * Get the Resource Name.
     *
     * @return resoureceName as String
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Set the resourceName.
     *
     * @param resourceName as String
     */
    public void setResourceName(final String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * Get the view.
     *
     * @return view as String
     */
    public String getView() {
        return view;
    }

    /**
     * Set the view.
     *
     * @param view as String
     */
    public void setView(final String view) {
        this.view = view;
    }
    //</editor-fold>
}
