package io.revx.api.mysql.repo.creative;

import io.revx.api.mysql.entity.creative.CreativeTemplateThemeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreativeTemplateThemeRepo extends JpaRepository<CreativeTemplateThemeEntity, Long> {

    List<CreativeTemplateThemeEntity> findByAdvertiserId(Long advertiserId);
}
