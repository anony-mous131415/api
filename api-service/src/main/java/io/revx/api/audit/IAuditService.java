package io.revx.api.audit;

public interface IAuditService<T> {

  public void audit(T oldObj, T newObj) throws Exception;
}
