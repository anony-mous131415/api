package io.revx.api.audience.pojo;

public class AppsFlyerAudienceCreateResponseDto {

    Long container_id;

    public Long getContainer_id() {
        return container_id;
    }

    public void setContainer_id(Long container_id) {
        this.container_id = container_id;
    }

    @Override
    public String toString() {
        return "AppsFlyerAudienceSyncResponseDto{" +
                "container_id=" + container_id +
                '}';
    }
}
