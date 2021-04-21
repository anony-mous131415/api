package io.revx.api.mysql.repo.creative;

import io.revx.api.mysql.entity.creative.CreativeTemplateVariablesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreativeTemplateVariablesRepo extends JpaRepository<CreativeTemplateVariablesEntity, Long> {

    List<CreativeTemplateVariablesEntity> findByActiveTrue();

}
