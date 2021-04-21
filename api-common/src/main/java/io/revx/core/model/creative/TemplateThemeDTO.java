package io.revx.core.model.creative;

public class TemplateThemeDTO {

    private Long id;
    private String themeName;
    private Long advertiserId;
    private String styleJson;
    private Boolean isActive;
    private Long createdOn;
    private Long createdBy;
    private Long modifiedBy;
    private Long modifiedOn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public String getStyleJson() {
        return styleJson;
    }

    public void setStyleJson(String styleJson) {
        this.styleJson = styleJson;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
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
        return "TemplateThemeDTO{" +
                "id=" + id +
                ", themeName='" + themeName + '\'' +
                ", advertiserId=" + advertiserId +
                ", styleJson='" + styleJson + '\'' +
                ", isActive=" + isActive +
                ", createdOn=" + createdOn +
                ", createdBy=" + createdBy +
                ", modifiedBy=" + modifiedBy +
                ", modifiedOn=" + modifiedOn +
                '}';
    }
}
