/*
 * @author: ranjan-pritesh
 * @date: 
 */
package io.revx.api.mysql.repo.advertiser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.entity.advertiser.CurrencyEntity;

/**
 * The Interface AdvertiserRepository.
 */
@Repository
public interface CurrencyRepository extends JpaRepository<CurrencyEntity, Long> {

 Long findIdByCurrencyCode(String code);
  
}
