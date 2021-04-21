package io.revx.api.mysql.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SKAdNetworkCampaignMapping")
public class SKADNetworkCampaignMappingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sncm_id")
    private long id;

    @Column(name = "sncm_strategy_id", nullable = false)
    private long strategyId;

    @Column(name = "sncm_campaign_id", nullable = false)
    private long campaignId;

    @Column(name = "sncm_advertiser_id", nullable = false)
    private long advertiserId;

    @Column(name = "sncm_skad_campaign_Id", nullable = false)
    private long strategyCount;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(long strategyId) {
        this.strategyId = strategyId;
    }

    public long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(long campaignId) {
        this.campaignId = campaignId;
    }

    public long getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public long getStrategyCount() {
        return strategyCount;
    }

    public void setStrategyCount(long strategyCount) {
        this.strategyCount = strategyCount;
    }

    @Override
    public String toString() {
        return "SKAdNetworkCampaignMappingEntity{" +
                "id=" + id +
                ", strategyId=" + strategyId +
                ", campaignId=" + campaignId +
                ", advertiserId=" + advertiserId +
                ", strategyCount=" + strategyCount +
                '}';
    }
}
