package io.revx.api.pojo;

import java.util.Set;
import io.revx.api.enums.SlicexEntity;

public class SlicexFilter {
  SlicexEntity entity;
  Set<Long> ids;

  public SlicexEntity getEntity() {
    return entity;
  }

  public void setEntity(SlicexEntity entity) {
    this.entity = entity;
  }

  public Set<Long> getIds() {
    return ids;
  }

  public void setIds(Set<Long> ids) {
    this.ids = ids;
  }

  @Override
  public String toString() {
    return "Filter [entity=" + entity + ", ids=" + ids + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((entity == null) ? 0 : entity.hashCode());
    result = prime * result + ((ids == null) ? 0 : ids.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SlicexFilter other = (SlicexFilter) obj;
    if (entity != other.entity)
      return false;
    if (ids == null) {
      if (other.ids != null)
        return false;
    } else if (!ids.equals(other.ids))
      return false;
    return true;
  }
}
