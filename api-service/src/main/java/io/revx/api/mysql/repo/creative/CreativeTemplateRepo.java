package io.revx.api.mysql.repo.creative;

import io.revx.api.mysql.entity.creative.CreativeTemplateEntity;
import io.revx.api.mysql.entity.creative.TemplateMetaData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreativeTemplateRepo extends JpaRepository<CreativeTemplateEntity, Long> {

    Page<CreativeTemplateEntity> findAllByActiveTrueAndDynamic(boolean isDynamic, Pageable pageable);

    Page<CreativeTemplateEntity>
        findAllByActiveTrueAndSlotsLessThanEqualAndDynamic(int noOfSlots, boolean isDynamic, Pageable pageable);

    Page<CreativeTemplateEntity> findAllByActiveTrueAndDynamicAndSizeIn
            (boolean dynamic, List<String> sizes, Pageable pageable);

    Page<CreativeTemplateEntity> findAllByActiveTrueAndSlotsLessThanEqualAndDynamicAndSizeIn
            (int slots, boolean dynamic, List<String> sizes, Pageable pageable);

    List<TemplateMetaData> findAllByDynamicAndActive(boolean isDynamic, boolean isActive);
}
