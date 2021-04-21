package io.revx.auth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;


@Entity
@Table(name = "LifeTimeAuthentication")
public class LifeTimeAuthenticationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lta_id")
    private long id;

    @Column(name = "lta_user_id")
    private long userId;

    @Column(name ="lta_licensee_id")
    private long licenseeId;

    @Column(name ="lta_token", length = 1000)
    private String lifeTimeAuthToken;

    @Column(name = "lta_expiry_time")
    private long timeToExpire;

    @Column(name = "lta_created_on")
    private long createOn;

    @Column(name = "lta_is_active")
    private boolean isActive;

    @Column(name = "lta_modified_on")
    private long modifiedOn;

    public long getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(long modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getLicenseeId() {
        return licenseeId;
    }

    public void setLicenseeId(long licenseeId) {
        this.licenseeId = licenseeId;
    }

    public String getLifeTimeAuthToken() {
        return lifeTimeAuthToken;
    }

    public void setLifeTimeAuthToken(String lifeTimeAuthToken) {
        this.lifeTimeAuthToken = lifeTimeAuthToken;
    }

    public long getTimeToExpire() {
        return timeToExpire;
    }

    public void setTimeToExpire(long timeToExpire) {
        this.timeToExpire = timeToExpire;
    }

    public long getCreateOn() {
        return createOn;
    }

    public void setCreateOn(long createOn) {
        this.createOn = createOn;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "LifeTimeAuthenticationEntity{" +
                "id=" + id +
                ", userId=" + userId +
                ", licenseeId=" + licenseeId +
                ", lifeTimeAuthToken='" + lifeTimeAuthToken + '\'' +
                ", timeToExpire=" + timeToExpire +
                ", createOn=" + createOn +
                ", isActive=" + isActive +
                ", modifiedOn=" + modifiedOn +
                '}';
    }
}
