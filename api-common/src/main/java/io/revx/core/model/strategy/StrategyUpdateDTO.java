package io.revx.core.model.strategy;

import java.util.HashSet;
import java.util.Set;

public class StrategyUpdateDTO {
  Set<Long> creativeIdsToInsert = new HashSet<>();
  Set<Long> creativeIdsToDelete = new HashSet<>();
  Set<Long> pixelIdsToInsert = new HashSet<>();
  Set<Long> pixelIdsToDelete = new HashSet<>();

  public Set<Long> getCreativeIdsToInsert() {
    return creativeIdsToInsert;
  }

  public void setCreativeIdsToInsert(Set<Long> creativeIdsToInsert) {
    this.creativeIdsToInsert = creativeIdsToInsert;
  }

  public void addCreativeIdToInsert(Long id) {
    if (this.creativeIdsToInsert == null)
      this.creativeIdsToInsert = new HashSet<>();
    this.creativeIdsToInsert.add(id);
  }

  public Set<Long> getCreativeIdsToDelete() {
    return creativeIdsToDelete;
  }

  public void setCreativeIdsToDelete(Set<Long> creativeIdsToDelete) {
    this.creativeIdsToDelete = creativeIdsToDelete;
  }

  public void addCreativeIdToDelete(Long id) {
    if (this.creativeIdsToDelete == null)
      this.creativeIdsToDelete = new HashSet<>();
    this.creativeIdsToDelete.add(id);
  }

  public Set<Long> getPixelIdsToInsert() {
    return pixelIdsToInsert;
  }

  public void setPixelIdsToInsert(Set<Long> pixelIdsToInsert) {
    this.pixelIdsToInsert = pixelIdsToInsert;
  }

  public void addPixelIdToInsert(Long id) {
    if (this.pixelIdsToInsert == null)
      this.pixelIdsToInsert = new HashSet<>();
    this.pixelIdsToInsert.add(id);
  }


  public Set<Long> getPixelIdsToDelete() {
    return pixelIdsToDelete;
  }

  public void setPixelIdsToDelete(Set<Long> pixelIdsToDelete) {
    this.pixelIdsToDelete = pixelIdsToDelete;
  }


  public void addPixelIdToDelete(Long id) {
    if (this.pixelIdsToDelete == null)
      this.pixelIdsToDelete = new HashSet<>();
    this.pixelIdsToDelete.add(id);
  }


}
