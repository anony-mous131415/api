package io.revx.api.mysql.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.revx.api.mysql.entity.UserInfoEntity;

@Repository
public interface UserRepository extends JpaRepository<UserInfoEntity, Integer> {
	UserInfoEntity findByUsername(String username);

	@Query(value = "select * from UserInfo where ui_id IN (?1)", nativeQuery =true)
	List<UserInfoEntity> findAllById(List<Integer> ids);
}
