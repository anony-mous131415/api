package io.revx.core.event;

import java.util.concurrent.Executors;
import com.google.common.eventbus.AsyncEventBus;

public class EventBusManager {
  public static AsyncEventBus eventBus = new AsyncEventBus(Executors.newCachedThreadPool());
}
