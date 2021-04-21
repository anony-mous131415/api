package io.revx.api.audit;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;
import io.revx.core.constant.Constants;
import io.revx.core.event.MessageObject;
import io.revx.core.exception.NoAuditChangeException;

public class AuditServiceListener {

  private static Logger logger = LogManager.getLogger(AuditServiceListener.class);

  public static final Map<Class, IAuditService> services = new HashMap<Class, IAuditService>();


  @Subscribe
  public void audit(MessageObject mo) {
    try {
      logger.debug("Going On Audit Class ");
      setThreadContext(mo.userId);
      logger.info("recieved event.....");
      logger.info("previous object: " + mo.getPrevValue());
      logger.info("new object: " + mo.getNewValue());
      if (mo.getNewValue() == null) {
        logger.error("new value cannot be null");
        return;
      }
      IAuditService as = services.get(mo.getNewValue().getClass());
      if (as == null)
        audit(mo.getPrevValue(), mo.getNewValue());
      else
        as.audit(mo.getPrevValue(), mo.getNewValue());
    } catch (NoAuditChangeException e) {
      logger.info("changes are not auditable", e);
    } catch (Throwable th) {
      logger.error("failed to audit changes", th);
    } finally {
    }
  }

  private void audit(Serializable prevValue, Serializable newValue) {
    logger.warn("unsupported type in audit");
    if (newValue != null)
      logger.warn("type: " + newValue.getClass().getName());
    else
      logger.warn("type: null object passed");
  }

  @Subscribe
  public void deadEvent(DeadEvent de) {
    logger.warn("recieved a dead event: " + de.getEvent());
  }

  private void setThreadContext(Long userId) {
    ThreadContext.put(Constants.USER_ID, String.valueOf(userId));
  }
}
