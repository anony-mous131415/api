package io.revx.api.mysql.entity;

import io.revx.core.enums.AppSettingsPropertyKey;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;

@Entity
@Table(name = "AppSettingsProperty")
public class AppSettingsPropertyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asp_id")
    private Long id;

    @Column(name = "asp_key",columnDefinition = "ENUM", nullable = false)
    @Enumerated(EnumType.STRING)
    private AppSettingsPropertyKey propertyKey;

    @Column(name = "asp_value")
    private String propertyValue;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "as_id", nullable = false)
    private AppSettingsEntity appSettingsEntity;

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

    public AppSettingsEntity getAppSettingsEntity() {
        return appSettingsEntity;
    }

    public void setAppSettingsEntity(AppSettingsEntity appSettingsEntity) {
        this.appSettingsEntity = appSettingsEntity;
    }

    @Override
    public String toString() {
        return "AppSettingsPropertyEntity{" +
                "id=" + id +
                ", propertyKey='" + propertyKey + '\'' +
                ", propertyValue='" + propertyValue + '\'' +
                ", appSettingsEntity=" + appSettingsEntity.getValue() +
                '}';
    }
}
