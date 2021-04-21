package io.revx.api.mysql.repo.campaign;

import java.math.BigDecimal;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.revx.api.mysql.entity.campaign.CampaignEntity;

@Repository
public interface CampaignRepository extends JpaRepository<CampaignEntity, Long> {

	List<CampaignEntity> findByActive(boolean isActive);

	List<CampaignEntity> findByLicenseeId(Long licenseeId);

	CampaignEntity findByIdAndLicenseeId(Long id, Long licenseeId);

	CampaignEntity findByIdAndLicenseeIdAndAdvertiserIdIn(Long id, Long licenseeId, List<Long> advertiserId);

	@Transactional
	@Modifying
	@Query("update CampaignEntity set active = :status where id in (:idList)")
	int updateStatusInBulk(@Param("status") Boolean status, @Param("idList") List<Long> idList);

	@Transactional
	@Modifying
	@Query("update CampaignEntity set active = :status where id = :idList")
	int updateStatus(@Param("status") Boolean status, @Param("idList") Long id);

	List<CampaignEntity> findByAdvertiserId(Long advertiserId, Pageable pageable);

	List<CampaignEntity> findByAdvertiserIdAndId(Long advertiserId, Long id, Pageable pageable);


	@Transactional
	@Modifying
	@Query("update CampaignEntity set cpaTarget = CASE WHEN cpaTarget IS NULL THEN :cpaTarget ELSE cpaTarget END where id = :campaignId")
	int updateCPATarget(@Param("campaignId") Long campaignId, @Param("cpaTarget") BigDecimal cpaTarget);

	
	List<CampaignEntity> findByAdvertiserIdAndNameIgnoreCaseContaining(Long advertiserId, String name, Pageable pageable);


}
