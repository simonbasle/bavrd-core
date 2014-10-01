package net.bavrd.core;

import java.util.HashSet;
import java.util.Set;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public class MainBot extends Verticle {

  @Override
  public void start() {
    JsonObject globalConfig = container.config();
    if (!globalConfig.containsField("modules"))
      throw new IllegalArgumentException("failed to load 'modules' configuration");

    //this part of the config is global, will be appended to the config of each module
    Set<String> commonFieldNames = new HashSet<>(globalConfig.getFieldNames());
    commonFieldNames.remove("modules");

    //the "modules" part of the config lists all the required bavrd modules
    JsonArray mods = globalConfig.getArray("modules");
    for (Object m : mods) {
      JsonObject mod = (JsonObject) m;
      final String modName = mod.getString("moduleName");
      String modRef = mod.getString("moduleRef");

      //reconstruct a proper module config with common config elements and the module declared name
      JsonObject modConfig = mod.getObject("moduleConf").copy();
      for (String f : commonFieldNames) {
        modConfig.putValue(f, globalConfig.getField(f));
      }
      modConfig.putString("moduleName", modName);

      //deploy module
      container.logger().info("Attempting to deploy "+ modName);
      container.deployVerticle(modRef, modConfig, new Handler<AsyncResult<String>>() {
        @Override
        public void handle(AsyncResult<String> event) {
          if (event.failed())
            container.logger().error("Error deploying " + modName, event.cause());
        }
      });
    }
  }
}
