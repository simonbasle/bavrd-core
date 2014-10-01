package net.bavrd.core;

import java.util.Map;

import org.vertx.java.platform.Verticle;

public abstract class BavrdVerticle extends Verticle {

  public static final String SHARED_DATA_HELP = "bavrd-help";

  /**
   * default init behavior for a BAVRD Verticle : register a handler for "bavrd-discover" events
   */
  @Override
  public void start() {
    //add help (commands and their helptext) to the shared map of BAVRD commands
    Map<String, String> help = getHelp();
    if (!help.isEmpty())
      vertx.sharedData().getMap(SHARED_DATA_HELP).putAll(help);

    startBavrd();

    String moduleName = container.config().getString("moduleName");
    container.logger().info("[module started] " + moduleName + " - " + this.getClass().getCanonicalName());
  }

  /** init method for the concrete BAVRD Verticle */
  public abstract void startBavrd();

  /** @return the type of this module */
  public abstract BavrdComponent getType();

  /** @return a map of commands this module reacts to (command representation - command description) */
  public abstract Map<String, String> getHelp();
}