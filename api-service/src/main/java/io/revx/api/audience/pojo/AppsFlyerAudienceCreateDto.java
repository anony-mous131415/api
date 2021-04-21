package io.revx.api.audience.pojo;

public class AppsFlyerAudienceCreateDto {

    String api_key;
    String name;
    String platform;

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
    public String toString() {
        return "AppsFlyerAudienceCreateDto{" +
                "api_key='" + api_key + '\'' +
                ", name='" + name + '\'' +
                ", platform='" + platform + '\'' +
                '}';
    }
}
