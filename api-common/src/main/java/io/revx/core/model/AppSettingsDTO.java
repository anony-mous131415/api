package io.revx.core.model;

import io.revx.core.enums.AppSettingsKey;
import io.revx.core.enums.AppSettingsType;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class AppSettingsDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    private Long id;
    private Long licenseeId;
    private Long advertiserId;
    private AppSettingsKey settingsKey;
    private String settingsValue;
    private AppSettingsType settingsType;
    private Boolean isActive;
    private List<AppSettingsPropertyDTO> appSettingsProperties;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLicenseeId() {
        return licenseeId;
    }

    public void setLicenseeId(Long licenseeId) {
        this.licenseeId = licenseeId;
    }

    public long getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public AppSettingsKey getSettingsKey() {
        return settingsKey;
    }

    public void setSettingsKey(AppSettingsKey settingsKey) {
        this.settingsKey = settingsKey;
    }

    public String getSettingsValue() {
        return settingsValue;
    }

    public void setSettingsValue(String settingsValue) {
        this.settingsValue = settingsValue;
    }

    public AppSettingsType getSettingsType() {
        return settingsType;
    }

    public void setSettingsType(AppSettingsType settingsType) {
        this.settingsType = settingsType;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public List<AppSettingsPropertyDTO> getAppSettingsProperties() {
        return appSettingsProperties;
    }

    public void setAppSettingsProperties(List<AppSettingsPropertyDTO> appSettingsProperties) {
        this.appSettingsProperties = appSettingsProperties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppSettingsDTO that = (AppSettingsDTO) o;

        if (!id.equals(that.id)) return false;
        if (!licenseeId.equals(that.licenseeId)) return false;
        if (!Objects.equals(advertiserId, that.advertiserId)) return false;
        if (settingsKey != that.settingsKey) return false;
        if (!settingsValue.equals(that.settingsValue)) return false;
        if (settingsType != that.settingsType) return false;
        if (!isActive.equals(that.isActive)) return false;
        return appSettingsProperties.equals(that.appSettingsProperties);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + licenseeId.hashCode();
        result = 31 * result + (advertiserId != null ? advertiserId.hashCode() : 0);
        result = 31 * result + settingsKey.hashCode();
        result = 31 * result + settingsValue.hashCode();
        result = 31 * result + settingsType.hashCode();
        result = 31 * result + isActive.hashCode();
        result = 31 * result + appSettingsProperties.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AppSettingsDTO{" +
                "id=" + id +
                ", licenseeId=" + licenseeId +
                ", advertiserId=" + advertiserId +
                ", settingsKey='" + settingsKey + '\'' +
                ", settingsValue='" + settingsValue + '\'' +
                ", settingsType=" + settingsType +
                ", isActive=" + isActive +
                ", appSettingsProperties=" + appSettingsProperties +
                '}';
    }
}
