package net.bavrd.core;

import java.util.Map;

import org.vertx.java.platform.Verticle;

import net.bavrd.utils.HelpUtils;

public abstract class BavrdVerticle extends Verticle {

  /**
   * default init behavior for a BAVRD Verticle : save help, init bavrd module (see {{@link BavrdVerticle#startBavrd()}}), log loading
   */
  @Override
  public void start() {
    String moduleName = container.config().getString("moduleName");
    //add help (commands and their helptext) to the shared map of BAVRD commands
    Map<String, String> helpMap = getHelp();
    if (!helpMap.isEmpty()) {
      vertx.sharedData().getMap(HelpUtils.SHARED_DATA_HELP).put(moduleName, HelpUtils.serializeHelp(helpMap));
    }

    startBavrd();

    container.logger().info("[module started] " + moduleName + " - " + this.getClass().getCanonicalName());
  }

  /** init method for the concrete BAVRD Verticle */
  public abstract void startBavrd();

  /** @return the type of this module */
  public abstract BavrdComponent getType();

  /** @return a map of commands this module reacts to (command representation - command description) */
  public abstract Map<String, String> getHelp();
}