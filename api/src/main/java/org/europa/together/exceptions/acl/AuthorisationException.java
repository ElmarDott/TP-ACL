package org.europa.together.exceptions.acl;

/**
 * Exception for authorization errors.
 */
public class AuthorisationException extends Exception {

    private static final long serialVersionUID = 8L;

    /**
     * Creates a new instance of <code>AuthorisationException</code> without
     * detail message.
     */
    public AuthorisationException() {
        /* NOT IN USE. */
    }

    /**
     * Constructs an instance of <code>AuthorisationException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public AuthorisationException(final String msg) {
        super(msg);
    }
}
