package org.europa.together.exceptions.acl;

/**
 * Exception for authentication errors.
 */
public class AuthenticationException extends Exception {

    private static final long serialVersionUID = 9L;

    /**
     * Creates a new instance of <code>AuthenticationException</code> without
     * detail message.
     */
    public AuthenticationException() {
        /* NOT IN USE. */
    }

    /**
     * Constructs an instance of <code>AuthenticationException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public AuthenticationException(final String msg) {
        super(msg);
    }
}
