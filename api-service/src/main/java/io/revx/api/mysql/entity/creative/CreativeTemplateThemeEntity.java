package io.revx.api.mysql.entity.creative;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TemplateTheme")
public class CreativeTemplateThemeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tt_id")
    private long id;

    @Column(name = "tt_adv_id", nullable = false)
    private long advertiserId;

    @Column(name = "tt_name", nullable = false)
    private String themeName;

    @Column(name = "tt_style_json", nullable = false)
    private String styleJson;

    @Column(name = "tt_is_active", nullable = false, columnDefinition = "tinyint(1) default 1")
    private boolean active;

    @Column(name = "tt_created_by")
    private Long createdBy;

    @Column(name = "tt_created_on")
    private Long createdOn;

    @Column(name = "tt_modified_by")
    private Long modifiedBy;

    @Column(name = "tt_modified_on")
    private Long modifiedOn;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public String getStyleJson() {
        return styleJson;
    }

    public void setStyleJson(String styleJson) {
        this.styleJson = styleJson;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }

    public Long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Long getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Long modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    @Override
    public String toString() {
        return "CreativeTemplateThemeEntity{" +
                "id=" + id +
                ", advertiserId=" + advertiserId +
                ", themeName='" + themeName + '\'' +
                ", styleJson='" + styleJson + '\'' +
                ", isActive=" + active +
                ", createdBy=" + createdBy +
                ", createdOn=" + createdOn +
                ", modifiedBy=" + modifiedBy +
                ", modifiedOn=" + modifiedOn +
                '}';
    }

}
