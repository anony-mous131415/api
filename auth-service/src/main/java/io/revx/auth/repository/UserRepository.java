package io.revx.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.auth.entity.UserInfoEntity;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;

@Repository
public interface UserRepository extends JpaRepository<UserInfoEntity, Integer> {

  @LogMetrics(
      name = GraphiteConstants.DB + GraphiteConstants.MYSQL + GraphiteConstants.FIND + ".user")
  UserInfoEntity findByUsername(String username);

  @LogMetrics(
      name = GraphiteConstants.DB + GraphiteConstants.MYSQL + GraphiteConstants.FIND + ".user")
  UserInfoEntity findByUsernameAndIsActive(String username, boolean isActive);
}
