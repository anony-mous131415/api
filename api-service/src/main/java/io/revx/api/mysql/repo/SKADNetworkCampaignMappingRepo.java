package io.revx.api.mysql.repo;

import io.revx.api.mysql.entity.SKADNetworkCampaignMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SKADNetworkCampaignMappingRepo extends
        JpaRepository<SKADNetworkCampaignMappingEntity, Long> {

    @Query(value = "select count(sncm_skad_campaign_Id) from SKAdNetworkCampaignMapping s where s.sncm_campaign_id = ?1",
            nativeQuery = true)
    long getStrategyCountPerCampaign(long campaignId);

}
