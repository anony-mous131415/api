package io.revx.api.mysql.repo.strategy;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.revx.api.mysql.entity.strategy.StrategyEntity;
import io.revx.core.model.strategy.StrategyQuickEditDTO;


@Repository
public interface StrategyRepository extends JpaRepository<StrategyEntity, Long> {


	List<StrategyEntity> findAllByIdIn(List<Long> ids);

	List<StrategyEntity> findAllByLicenseeIdAndIdIn(Long licenseeId, List<Long> ids);


	List<StrategyEntity> findByActive(boolean isActive);

	List<StrategyEntity> findByLicenseeId(Long licenseeId);

	StrategyEntity findByIdAndLicenseeId(Long id, Long licenseeId);

	List<StrategyEntity> findByNameAndCampianId(String name, Long campianId);

	StrategyEntity findByIdAndLicenseeIdAndAdvertiserIdIn(Long id, Long licenseeId,
			List<Long> advertiserId);

	List<StrategyEntity> findByLicenseeIdAndCampianIdIn(Long licenseeId,
			List<Long> campianIds);

	List<StrategyEntity> findByAdvertiserId(Long advertiserId, Pageable pageable);

	List<StrategyEntity> findByAdvertiserIdAndId(Long advertiserId, Long id, Pageable pageable);

	List<StrategyEntity> findByAdvertiserIdAndNameIgnoreCaseContaining(Long advertiserId, String name,
			Pageable pageable);


	@Transactional
	@Modifying
	@Query("update StrategyEntity set active = :status where id in (:idList)")
	int updateStatusInBulk(@Param("status") Boolean status, @Param("idList") List<Long> idList);

	@Transactional
	@Modifying
	@Query("update StrategyEntity set active = :status where id = :id")
	int updateStatus(@Param("status") Boolean status, @Param("id") Long id);

	//Quick-edit-get
	@Query("select new io.revx.core.model.strategy.StrategyQuickEditDTO  \n"+
			"(S.id , S.name ,\n" + 
			"S.campiagnFcap , S.userFcap , \n" + 
			"S.flowRate , S.pricingId , \n" + 
			"S.bidCapMinCpm , S.bidCapMaxCpm , \n" +
			"S.campianId , S.type , \n"+
			"S.userFcapDuration ) \n"+
			"from StrategyEntity S where id = :id")
	StrategyQuickEditDTO getQuickEditDetails(@Param("id") Long id);

	@Query(value="select distinct(al_id) from AdvertiserLineItem where al_advertiser_io_id=(?1)", nativeQuery = true)
	List<Long> findStrategyIdsFromCampaingId(long id);

}
