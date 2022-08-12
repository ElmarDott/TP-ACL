package org.europa.together.domain.acl;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.europa.together.utils.StringUtils;
import org.europa.together.utils.Constraints;
import org.hibernate.annotations.CreationTimestamp;

/**
 * USER ACCOUNT OBJECT. A user can have only one Role. The timestamp is stored
 * in UTC format and need transformed to the client timezone.
 */
@Entity
@Table(name = "ACL_ACCOUNT",
        //CHECKSTYLE:OFF
        indexes = {
            @Index(columnList = "EMAIL", name = "account_email"),
            @Index(columnList = "ROLE_NAME", name = "account_role"),
            @Index(columnList = "VERIFICATION_CODE", name = "account_verification")
        }
//CHECKSTYLE:ON
)
public class AccountDO implements Serializable {

    private static final long serialVersionUID = 10L;
    private static final int HASH = 3;

    /**
     * The name of the used database table for this domain object.
     */
    public static final String TABLE_NAME = "ACL_ACCOUNT";

    @Id
    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PASSWORD")
    private String password;

    @ManyToOne
    @JoinColumn(name = "ROLE_NAME")
    private RolesDO role;

    @CreationTimestamp
    @Column(name = "REGISTERED", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date registered;

    @Column(name = "VERIFIED")
    @Temporal(TemporalType.DATE)
    private Date verified;

    @Column(name = "DEACTIVATED")
    @Temporal(TemporalType.DATE)
    private Date deactivated;

    @Column(name = "VERIFICATION_CODE", unique = true, nullable = false)
    private String verificationCode;

    @Column(name = "DEFAULT_LOCALE")
    private String defaultLocale;

    @Column(name = "DEFAULT_TIMEZONE")
    private String defaultTimezone;

    /**
     * Default Constructor.
     */
    public AccountDO() {
        TimeZone.setDefault(Constraints.SYSTEM_DEFAULT_TIMEZONE);
        this.registered = new Date(System.currentTimeMillis());
        this.verificationCode = StringUtils.generateUUID();
        this.defaultLocale = "EN_en";
        this.defaultTimezone = "UTC+00:00";
    }

    /**
     * Constructor.
     *
     * @param email as String
     */
    public AccountDO(final String email) {
        TimeZone.setDefault(Constraints.SYSTEM_DEFAULT_TIMEZONE);
        this.email = email;
        this.registered = new Date(System.currentTimeMillis());
        this.verificationCode = StringUtils.generateUUID();
        this.defaultLocale = "EN_en";
        this.defaultTimezone = "UTC+00:00";
    }

    @Override
    public int hashCode() {
        int hash = HASH * 97;
        hash = hash + Objects.hashCode(this.email);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        boolean success = false;
        if (obj != null && obj instanceof AccountDO) {
            if (this == obj) {
                success = true;
            } else {
                final AccountDO other = (AccountDO) obj;
                if (Objects.equals(this.email, other.email)) {
                    success = true;
                }
            }
        }
        return success;
    }

    @Override
    public String toString() {
        return "AccountDO{"
                + "email=" + email
                + ", password=" + password
                + ", role=" + role
                + ", registrationDate=" + registered
                + ", verificationCode=" + verificationCode
                + ", verified=" + verified
                + ", activated=" + deactivated
                + ", defaultLocale=" + defaultLocale
                + ", defaultTimezone=" + defaultTimezone
                + "}";
    }

    //<editor-fold defaultstate="collapsed" desc="Getter / Setter">
    /**
     * Get the E-Mail of an Account. Primary Key.
     *
     * @return email as String
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the E-Mail of an Account. Primary Key.
     *
     * @param email as String
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * Get the password (SHA-512) of an Account.
     *
     * @return password as String
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password (SHA-512) of an Account.
     *
     * @param password as String
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * Get the Role of an Account.
     *
     * @return role as Object
     */
    public RolesDO getRole() {
        return role;
    }

    /**
     * Set the Role of an Account.
     *
     * @param role as Object
     */
    public void setRole(final RolesDO role) {
        this.role = role;
    }

    /**
     * Get the Registration Date of an Account, when it was created.
     *
     * @return registrationDate as Timestamp
     */
    public Date isRegistered() {
        return registered;
    }

    /**
     * Set the Registration Date of an Account, when it get created.
     */
    public void setRegistered() {
        this.registered = new Date(System.currentTimeMillis());
    }

    /**
     * Shows if an Account is verified.
     *
     * @return true on success
     */
    public Date isVerified() {
        return verified;
    }

    /**
     * Set an Account to verified, if the verification process was
     * success.During the creation process is set to false.
     */
    public void setVerified() {
        this.verified = new Date(System.currentTimeMillis());
    }

    /**
     * Shows if an Account is activated. Deactivated Accounts can not Login.
     *
     * @return true on success.
     */
    public Date isDeactivated() {
        return deactivated;
    }

    /**
     * Allows to block the login of an account.
     */
    public void setDeactivated() {
        this.deactivated = new Date(System.currentTimeMillis());
    }

    /**
     * Reset the deactivation of an Account, by erasing the deactivation date.
     */
    public void reactivateAccount() {
        this.deactivated = null;
    }

    /**
     * Get the verification code (UUID) for the DoublOptIn procedure.
     *
     * @return verificationCode as String
     */
    public String getVerificationCode() {
        return verificationCode;
    }

    /**
     * Allows the auto generated verification code (UUID) during the Account
     * creation to change.
     *
     * @param verificationCode as String
     */
    public void setVerificationCode(final String verificationCode) {
        this.verificationCode = verificationCode;
    }

    /**
     * Get the default localisation.
     *
     * @return locale as String
     */
    public String getDefaultLocale() {
        return defaultLocale;
    }

    /**
     * Set the default localisation.
     *
     * @param defaultLocale as String
     */
    public void setDefaultLocale(final String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    /**
     * Get the default timezone as String in the format: +/-02:00.
     *
     * @return timezone as String
     */
    public String getDefaultTimezone() {
        return defaultTimezone;
    }

    /**
     * Set the default timezone in the format: +/-02:00.
     *
     * @param defaultTimezone as String
     */
    public void setDefaultTimezone(final String defaultTimezone) {
        this.defaultTimezone = defaultTimezone;
    }
    //</editor-fold>
}
