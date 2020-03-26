package org.europa.together.domain.acl;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.TimeZone;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.europa.together.utils.StringUtils;
import org.europa.together.utils.acl.Constraints;
import org.hibernate.annotations.CreationTimestamp;

/**
 * USER ACCOUNT OBJECT. A user can have only one Role. The timestamp is stored
 * in UTC format and need transformed to the client timezone.
 */
@Entity
@Table(name = "ACCOUNT",
        //CHECKSTYLE:OFF
        indexes = {
            @Index(columnList = "EMAIL", name = "account_email")
        }
//CHECKSTYLE:ON
)
public class AccountDO implements Serializable {

    private static final long serialVersionUID = 10L;
    private static final int HASH = 3;

    /**
     * The name of the used database table for this domain object.
     */
    public static final String TABLE_NAME = "ACCOUNT";

    @Id
    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PASSWORD")
    private String password;

    @ManyToOne
    @JoinColumn(name = "ROLE_NAME")
    private RolesDO role;

    @CreationTimestamp
    @Column(name = "REGISTRATION_DATE", nullable = false)
    private Timestamp registrationDate;

    @Column(name = "VERIFIED", nullable = false)
    private boolean verified;

    @Column(name = "ACTIVATED", nullable = false)
    private boolean activated;

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
        this.registrationDate = new Timestamp(System.currentTimeMillis());
        this.activated = false;
        this.verified = false;
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
        this.registrationDate = new Timestamp(System.currentTimeMillis());
        this.verificationCode = StringUtils.generateUUID();
        this.activated = false;
        this.verified = false;
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
                + ", registrationDate=" + registrationDate
                + ", verificationCode=" + verificationCode
                + ", verified=" + verified
                + ", activated=" + activated
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
    public Timestamp getRegistrationDate() {
        Timestamp copy = registrationDate;
        return copy;
    }

    /**
     * Set the Registration Date of an Account, when it get created.
     *
     * @param registrationDate as Timestamp
     */
    public void setRegistrationDate(final Timestamp registrationDate) {
        this.registrationDate = registrationDate;
    }

    /**
     * Shows if an Account is verified.
     *
     * @return true on success
     */
    public boolean isVerified() {
        return verified;
    }

    /**
     * Set an Account to verified, if the verification process was success.
     * During the creation process is set to false.
     *
     * @param verified as boolean
     */
    public void setVerified(final boolean verified) {
        this.verified = verified;
    }

    /**
     * Shows if an Account is activated. Deactivated Accounts can not Login.
     *
     * @return true on success.
     */
    public boolean isActivated() {
        return activated;
    }

    /**
     * Allows to block the login of an account.
     *
     * @param activated as boolean
     */
    public void setActivated(final boolean activated) {
        this.activated = activated;
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
