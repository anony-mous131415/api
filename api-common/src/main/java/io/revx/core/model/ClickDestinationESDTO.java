package io.revx.core.model;

public class ClickDestinationESDTO extends StatusTimeModel{

    private Long advertiserId;
    private Long licenseeId;
    private Boolean dco;
    private Boolean refactor;
    private Boolean skadTarget;
    private String campaignType;

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public Long getLicenseeId() {
        return licenseeId;
    }

    public void setLicenseeId(Long licenseeId) {
        this.licenseeId = licenseeId;
    }

    public Boolean getDco() {
        return dco;
    }

    public void setDco(Boolean dco) {
        this.dco = dco;
    }

    public Boolean getRefactor() {
        return refactor;
    }

    public void setRefactor(Boolean refactor) {
        this.refactor = refactor;
    }

    public Boolean getSkadTarget() {
        return skadTarget;
    }

    public void setSkadTarget(Boolean skadTarget) {
        this.skadTarget = skadTarget;
    }

    public String getCampaignType() {
        return campaignType;
    }

    public void setCampaignType(String campaignType) {
        this.campaignType = campaignType;
    }

    @Override
    public String toString() {
        return "ClickDestinationESDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", modifiedTime=" + modifiedTime +
                ", modifiedBy=" + modifiedBy +
                ", advertiserId=" + advertiserId +
                ", licenseeId=" + licenseeId +
                ", dco=" + dco +
                ", refactor=" + refactor +
                ", skadTarget=" + skadTarget +
                ", campaignType='" + campaignType + '\'' +
                ", active=" + active +
                ", creationTime=" + creationTime +
                ", createdBy=" + createdBy +
                '}';
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
