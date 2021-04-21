package io.revx.api.mysql.amtdb.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.amtdb.entity.RuleFilter;

@Repository
public interface RuleFilterRepository extends JpaRepository<RuleFilter, Long>{


}
