package org.europa.together.exceptions.acl;

/**
 * Exception for authentication errors.
 */
public class SessionException extends Exception {

    private static final long serialVersionUID = 9L;

    /**
     * Creates a new instance of <code>AuthenticationException</code> without
     * detail message.
     */
    public SessionException() {
        /* NOT IN USE. */
    }

    /**
     * Constructs an instance of <code>AuthenticationException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public SessionException(final String msg) {
        super(msg);
    }
}
