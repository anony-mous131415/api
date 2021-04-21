package io.revx.api.audience.pojo;

public class AppsFlyerAudienceTokenDto {

    String api_key;

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    @Override
    public String toString() {
        return "AppsFlyerAudienceTokenDto{" +
                "api_key='" + api_key + '\'' +
                '}';
    }
}
