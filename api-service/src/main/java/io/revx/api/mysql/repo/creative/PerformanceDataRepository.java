/*
 * @author: ranjan-pritesh
 * @date: 
 */
package io.revx.api.mysql.repo.creative;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.revx.api.mysql.entity.creative.PerformanceDataEntity;

/**
 * The Interface CreativeRepository.
 */
@Repository
public interface PerformanceDataRepository extends JpaRepository<PerformanceDataEntity, Long> {

	List<PerformanceDataEntity> findAllByCreativeId(Long id);

	@Query(value = "SELECT timestamp, licensee_id, adv_id, io_id, li_id, creative_id, entity_type, SUM(impressions) AS impressions, SUM(clicks) AS clicks, SUM(view_conversions) AS view_conversions, SUM(click_conversions) AS click_conversions, conversion_delivery, revenue_in_platform_currency, revenue_in_licensee_currency, revenue_in_advertiser_currency, cost_in_platform_currency, cost_in_licensee_currency, cost_in_advertiser_currency, imp_installs, click_installs, txn_amount_in_platform_currency, txn_amount_in_advertiser_currency, txn_amount_in_licensee_currency FROM PerformanceData WHERE creative_id IN (?1) GROUP BY creative_id", nativeQuery = true)
	List<PerformanceDataEntity> findAllByCreativeIdList(List<Long> ids);

	
	@Query(value = "SELECT timestamp, licensee_id, adv_id, io_id, li_id, creative_id, entity_type, SUM(impressions) AS impressions, SUM(clicks) AS clicks, SUM(view_conversions) AS view_conversions, SUM(click_conversions) AS click_conversions, conversion_delivery, revenue_in_platform_currency, revenue_in_licensee_currency, revenue_in_advertiser_currency, cost_in_platform_currency, cost_in_licensee_currency, cost_in_advertiser_currency, imp_installs, click_installs, txn_amount_in_platform_currency, txn_amount_in_advertiser_currency, txn_amount_in_licensee_currency FROM PerformanceData WHERE creative_id IN (?1) and timestamp>= (?2) and timestamp < (?3) GROUP BY creative_id", nativeQuery = true)
	List<PerformanceDataEntity> findAllByCreativeIdListAndTimeStamp(List<Long> ids, Long startTimeStamp , Long endTimestamp );
}