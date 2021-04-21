package io.revx.core.model.creative;

import java.io.Serializable;

public class Size implements Serializable{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public Integer height;

  public Integer width;

  public Size() {
    
  }
  
  public Size(Integer height, Integer width) {
    super();
    this.height = height;
    this.width = width;
  }

  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }

  public Integer getWidth() {
    return width;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }

  @Override
  public String toString() {
    return "Size [height=" + height + ", width=" + width + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((height == null) ? 0 : height.hashCode());
    result = prime * result + ((width == null) ? 0 : width.hashCode());
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
    Size other = (Size) obj;
    if (height == null) {
      if (other.height != null)
        return false;
    } else if (!height.equals(other.height))
        return false;
    if (width == null) {
      if (other.width != null)
        return false;
    } else if (!width.equals(other.width))
        return false;
    return true;
  }
}
