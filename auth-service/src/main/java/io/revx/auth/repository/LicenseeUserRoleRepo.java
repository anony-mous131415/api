package io.revx.auth.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import io.revx.auth.entity.LicenseeUserRolesEntity;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;

@Repository
public interface LicenseeUserRoleRepo extends JpaRepository<LicenseeUserRolesEntity, Long> {

  @LogMetrics(
      name = GraphiteConstants.DB + GraphiteConstants.MYSQL + GraphiteConstants.FIND + ".ur")
  @Query(
      value = "SELECT * FROM UserRoles WHERE ur_user_id = ?1 and ur_licensee_id =?2 and ur_adv_id=?3",
      nativeQuery = true)
  public List<LicenseeUserRolesEntity> findByUserIdAndLicenseeIdAndAdvId(long userId,
      long licenseeId, long advId);

  @LogMetrics(
      name = GraphiteConstants.DB + GraphiteConstants.MYSQL + GraphiteConstants.FIND + ".ur")
  @Query(
      value = "SELECT * FROM UserRoles WHERE ur_user_id = ?1 and (ur_licensee_id =?2 or ur_licensee_id is null)",
      nativeQuery = true)
  public List<LicenseeUserRolesEntity> findByUserIdAndLicenseeId(long userId, long licenseeId);

  @LogMetrics(
      name = GraphiteConstants.DB + GraphiteConstants.MYSQL + GraphiteConstants.FIND + ".ur")
  List<LicenseeUserRolesEntity> findByUserId(long userId);
}
