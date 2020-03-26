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
import org.europa.together.utils.StringUtils;
import org.europa.together.utils.acl.Constraints;
import org.hibernate.annotations.CreationTimestamp;

/**
 * LOGIN OBJECT. Store the Logins of an user (Account) for statistics. The
 * timestamp is stored in UTC format and need transformed to the client
 * timezone.
 */
@Entity
@Table(name = "LOGIN",
        //CHECKSTYLE:OFF
        indexes = {
            @Index(columnList = "EMAIL", name = "login_account_email")
        }
//CHECKSTYLE:ON
)
public class LoginDO implements Serializable {

    private static final long serialVersionUID = 20L;
    private static final int HASH = 5;

    /**
     * The name of the used database table for this domain object.
     */
    public static final String TABLE_NAME = "LOGIN";

    @Id
    @Column(name = "IDX")
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "EMAIL", nullable = false)
    private AccountDO account;

    @CreationTimestamp
    @Column(name = "LOGIN_DATE")
    private Date loginDate;

    @Column(name = "IPADRESS")
    private String ipAddress;

    @Column(name = "BROWSER_ID")
    private String browserID;

    @Column(name = "OPERATION_SYSTEM")
    private String operationSystem;

    @Column(name = "LOGOUT")
    private boolean logout;

    /**
     * Default Constructor.
     */
    public LoginDO() {
        TimeZone.setDefault(Constraints.SYSTEM_DEFAULT_TIMEZONE);
        this.uuid = StringUtils.generateUUID();
        this.loginDate = new Date(System.currentTimeMillis());
        this.logout = false;
    }

    /**
     * Constructor.
     *
     * @param account as Account
     */
    public LoginDO(final AccountDO account) {
        TimeZone.setDefault(Constraints.SYSTEM_DEFAULT_TIMEZONE);
        this.uuid = StringUtils.generateUUID();
        this.account = account;
        this.loginDate = new Date(System.currentTimeMillis());
        this.logout = false;
    }

    @Override
    public int hashCode() {
        int hash = HASH * 89;
        hash = hash + Objects.hashCode(this.uuid);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        boolean success = false;
        if (obj != null && obj instanceof LoginDO) {
            if (this == obj) {
                success = true;
            } else {
                final LoginDO other = (LoginDO) obj;
                if (Objects.equals(this.uuid, other.uuid)) {
                    success = true;
                }
            }
        }
        return success;
    }

    @Override
    public String toString() {
        return "LoginDO{"
                + "uuid=" + uuid
                + ", account=" + account
                + ", loginDate=" + loginDate
                + ", ipAddress=" + ipAddress
                + ", browserID=" + browserID
                + ", operationSystem=" + operationSystem
                + ", logout=" + logout
                + "}";
    }

    //<editor-fold defaultstate="collapsed" desc="Getter / Setter">
    /**
     * Get the UUID of an Login Object. Primary Key.
     *
     * @return uuid as String
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Set the UUID of an Login Object. Primary Key.
     *
     * @param uuid as String
     */
    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    /**
     * Get the Login of an Account.
     *
     * @return Account as Object
     */
    public AccountDO getAccount() {
        return account;
    }

    /**
     * Create a login for an Account.
     *
     * @param account as Object
     */
    public void setAccount(final AccountDO account) {
        this.account = account;
    }

    /**
     * EGet the Date of this specific Login.
     *
     * @return loginDate as Timestamp
     */
    public Date getLoginDate() {
        Date login = loginDate;
        return login;
    }

    /**
     * Set the Date for the login.
     *
     * @param loginDate as Timestamp
     */
    public void setLoginDate(final Date loginDate) {
        this.loginDate = loginDate;
    }

    /**
     * Get the IP Address of the Account for this login.
     *
     * @return ipAddress as String
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Set the IP Address of the Account for this login.
     *
     * @param ipAddress as String
     */
    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Get the Browser ID of the Account for this login.
     *
     * @return browserId as String
     */
    public String getBrowserID() {
        return browserID;
    }

    /**
     * Set the Browser ID of the Account for this login.
     *
     * @param browserID as String
     */
    public void setBrowserID(final String browserID) {
        this.browserID = browserID;
    }

    /**
     * Get the Operation System of the Account for this login.
     *
     * @return operationSystem as String
     */
    public String getOperationSystem() {
        return operationSystem;
    }

    /**
     *
     * Set the Operation System of the Account for this login.
     *
     * @param operationSystem as String
     */
    public void setOperationSystem(final String operationSystem) {
        this.operationSystem = operationSystem;
    }

    public boolean isLogout() {
        return logout;
    }

    public void setLogout(boolean logout) {
        this.logout = logout;
    }
    //</editor-fold>

}
