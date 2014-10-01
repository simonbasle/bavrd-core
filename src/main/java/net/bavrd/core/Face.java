package net.bavrd.core;

import org.vertx.java.core.json.JsonObject;

public abstract class Face extends BavrdVerticle {

  @Override
  public BavrdComponent getType() {
    return BavrdComponent.FACE;
  }

}
