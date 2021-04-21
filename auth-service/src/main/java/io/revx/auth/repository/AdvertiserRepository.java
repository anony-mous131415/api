package io.revx.auth.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import io.revx.auth.entity.AdvertiserEntity;

@Repository
public interface AdvertiserRepository extends JpaRepository<AdvertiserEntity, Integer> {

  List<AdvertiserEntity> findByIsActive(boolean isActive);

  @Query(value = "SELECT * FROM Advertiser WHERE av_is_active = ?1 and av_licensee_id=?2",
      nativeQuery = true)
  public List<AdvertiserEntity> findByLicenseeId(boolean isActive, long licenseeId);

}
