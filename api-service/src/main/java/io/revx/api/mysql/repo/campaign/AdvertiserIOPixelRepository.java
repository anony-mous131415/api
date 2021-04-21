package io.revx.api.mysql.repo.campaign;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.entity.campaign.AdvertiserIOPixel;

@Repository
public interface AdvertiserIOPixelRepository extends JpaRepository<AdvertiserIOPixel, Long> {


  List<AdvertiserIOPixel> findAllByCampaignId(Long campaignId);

}
