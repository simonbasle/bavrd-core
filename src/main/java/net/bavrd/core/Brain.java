package net.bavrd.core;

import java.util.Map;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;

public abstract class Brain extends BavrdVerticle {

  @Override
  public void startBavrd() {
    initBrain();

    vertx.eventBus().registerHandler("bavrd-brain-get", new Handler<Message<String>>() {
      @Override
      public void handle(Message<String> event) {
        Object value = get(event.body());
        event.reply(value);
      }
    });

    vertx.eventBus().registerHandler("bavrd-brain-put", new Handler<Message<Map.Entry<String, Object>>>() {
      @Override
      public void handle(Message<Map.Entry<String, Object>> event) {
        Object oldValue = put(event.body().getKey(), event.body().getValue());
        event.reply(oldValue);
      }
    });
  }

  @Override
  public BavrdComponent getType() {
    return BavrdComponent.BRAIN;
  }

  @Override
  public String getHelp() {
    return "bavrd-brain-get: get an object from memory using provided key"
        +"\nbavrd-brain-put: commit an object to memory on the given key";
  }

  /**
   * initialize the BAVRD Brain instance
   */
  public abstract void initBrain();

  /**
   * in reaction to a BAVRD get message, return the value stored by the brain
   * @param key the looked up key
   * @return the value stored by the brain or null if none
   */
  public abstract Object get(String key);

  /**
   * in reaction to a BAVRD put message, store a value in the brain
   * @param key the key to which the value will be stored
   * @param value the value to store
   * @return the old value for same key, or null if none
   */
  public abstract Object put(String key, Object value);
}
