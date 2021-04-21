package io.revx.core.model;

public class LogoModel extends BaseModel{

    private String logoLink;
    private String logoKey;
    private Long advertiserId;
    private Long licenseeId;

    public String getLogoLink() {
        return logoLink;
    }

    public void setLogoLink(String logoLink) {
        this.logoLink = logoLink;
    }

    public String getLogoKey() {
        return logoKey;
    }

    public void setLogoKey(String logoKey) {
        this.logoKey = logoKey;
    }

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + (int) (advertiserId ^ (advertiserId >>> 32)) ;
        result = prime * result + (int) (licenseeId ^ (licenseeId >>> 32)) ;
        result = prime * result + ((logoLink == null) ? 0 : logoLink.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LogoModel other = (LogoModel) obj;
        if (id.longValue() != other.id.longValue())
            return false;
        if (advertiserId.longValue() != ((LogoModel) obj).advertiserId.longValue())
            return false;
        if (licenseeId.longValue() != ((LogoModel) obj).licenseeId.longValue())
            return false;
        if (logoLink == null) {
            return other.logoLink == null;
        } else return logoLink.equals(other.logoLink);
    }
}
