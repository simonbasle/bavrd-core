package net.bavrd.core;

import java.util.HashMap;
import java.util.Map;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public abstract class BavrdVerticle extends Verticle {

  /**
   * default init behavior for a BAVRD Verticle : register a handler for "bavrd-discover" events
   */
  @Override
  public void start() {
    vertx.eventBus().registerHandler(EventEnum.DISCOVER.vertxEndpoint, new Handler<Message<String>>() {
      @Override
      public void handle(Message<String> event) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("moduleType", getType());
        jsonMap.put("moduleHelp", getHelp());
        event.reply(new JsonObject());
      }
    });

    startBavrd();

    container.logger().info(getName() + " BAVRD module started");
  }

  /** init method for the concrete BAVRD Verticle, other than bavrd-discover */
  public abstract void startBavrd();

  /** @return the name of the BAVRD module */
  public abstract String getName();

  /** @return the type of this module */
  public abstract BavrdComponent getType();

  /** @return a help String (that could be shown to a user) to describe what commands this module reacts to  */
  public abstract String getHelp();
}