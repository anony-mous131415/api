package io.revx.api.mysql.dco.repo.catalog;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.dco.entity.catalog.AtomCatalogVariablesEntity;

@Repository
public interface AtomCatalogVariableRepository extends JpaRepository<AtomCatalogVariablesEntity, Long> {
  
  List<AtomCatalogVariablesEntity> findAllByIdIn(List<Long> ids);
  
}
