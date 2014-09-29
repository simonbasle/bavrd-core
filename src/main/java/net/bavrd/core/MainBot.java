package net.bavrd.core;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public class MainBot extends Verticle {

  @Override
  public void start() {
    if (!container.config().containsField("modules"))
      throw new IllegalArgumentException("failed to load 'modules' configuration");

    JsonArray mods = container.config().getArray("modules");
    for (Object m : mods) {
      JsonObject mod = (JsonObject) m;
      String modName = mod.getString("moduleName");
      JsonObject modConfig = mod.getObject("moduleConf");
      String modRef = mod.getString("moduleRef");

      container.logger().info("Attempting to deploy "+ modName);
      container.deployVerticle(modRef, modConfig, new Handler<AsyncResult<String>>() {
        @Override
        public void handle(AsyncResult<String> event) {
          container.logger().info(event.result());
        }
      });
    }
  }
}
