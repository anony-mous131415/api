package io.revx.core.exception;

public class UnsortableAttributeException extends ApiException {

  private static final long serialVersionUID = 1l;
  public String attibute;

  public UnsortableAttributeException(String attribute) {
    super();
    this.attibute = attribute;
  }

  public UnsortableAttributeException(String attibute, String msg) {
    super(msg);
    this.attibute = attibute;
  }

}
