package io.revx.core.model;

import io.revx.core.enums.AppSettingsPropertyKey;

import java.io.Serializable;

public class AppSettingsPropertyDTO implements Serializable {

    private Long id;
    private AppSettingsPropertyKey propertyKey;
    private String propertyValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AppSettingsPropertyKey getPropertyKey() {
        return propertyKey;
    }

    public void setPropertyKey(AppSettingsPropertyKey propertyKey) {
        this.propertyKey = propertyKey;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppSettingsPropertyDTO that = (AppSettingsPropertyDTO) o;

        if (!id.equals(that.id)) return false;
        if (propertyKey != that.propertyKey) return false;
        return propertyValue.equals(that.propertyValue);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + propertyKey.hashCode();
        result = 31 * result + propertyValue.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AppSettingsPropertyDTO{" +
                "id=" + id +
                ", propertyKey='" + propertyKey + '\'' +
                ", propertyValue='" + propertyValue + '\'' +
                '}';
    }
}
