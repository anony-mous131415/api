package io.revx.core.model.creative;

public class CompanionAdDetails {

  public String imageLink;

  public Size size;

  public CompanionFormat imageFormat;

  public CompanionAdDetails() {

  }

  public CompanionAdDetails(String imageLink, Size size, CompanionFormat imageFormat) {
    this.imageLink = imageLink;
    this.size = size;
    this.imageFormat = imageFormat;
  }

  public String getImageLink() {
    return imageLink;
  }

  public void setImageLink(String imageLink) {
    this.imageLink = imageLink;
  }

  public Size getSize() {
    return size;
  }

  public void setSize(Size size) {
    this.size = size;
  }

  public CompanionFormat getImageFormat() {
    return imageFormat;
  }

  public void setImageFormat(CompanionFormat imageFormat) {
    this.imageFormat = imageFormat;
  }

  @Override
  public String toString() {
    return "CompanionAdDetails [imageLink=" + imageLink + ", size=" + size + ", imageFormat="
        + imageFormat + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((imageFormat == null) ? 0 : imageFormat.hashCode());
    result = prime * result + ((imageLink == null) ? 0 : imageLink.hashCode());
    result = prime * result + ((size == null) ? 0 : size.hashCode());
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
    CompanionAdDetails other = (CompanionAdDetails) obj;
    if (imageFormat != other.imageFormat)
      return false;
    if (imageLink == null) {
      if (other.imageLink != null)
        return false;
    } else if (!imageLink.equals(other.imageLink))
      return false;
    if (size == null) {
      if (other.size != null)
        return false;
    } else if (!size.equals(other.size))
      return false;
    return true;
  }

}
