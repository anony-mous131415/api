package io.revx.api.audience.pojo;

public class AppsFlyerAudienceSyncDto {

    Long container_id;
    String url_hashed;
    String url;
    String api_key;

    public Long getContainer_id() {
        return container_id;
    }

    public void setContainer_id(Long container_id) {
        this.container_id = container_id;
    }

    public String getUrl_hashed() {
        return url_hashed;
    }

    public void setUrl_hashed(String url_hashed) {
        this.url_hashed = url_hashed;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    @Override
    public String toString() {
        return "AppsFlyerAudienceSyncDto{" +
                "container_id=" + container_id +
                ", url_hashed='" + url_hashed + '\'' +
                ", url='" + url + '\'' +
                ", api_key='" + api_key + '\'' +
                '}';
    }
}
