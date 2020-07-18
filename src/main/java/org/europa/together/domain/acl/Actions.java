package org.europa.together.domain.acl;

/**
 * Enumeration for possible Actions of a resource.
 */
public enum Actions {

    SUBMIT("SUBMIT"),
    PUBLISH("PUBLISH"),
    REJECT("REJECT"),
    REVOKE("REVOKE"),
    APPROVAL("APPROVAL"),
    ALL("ALL");

    private final String value;

    //CONSTRUCTOR
    Actions(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
