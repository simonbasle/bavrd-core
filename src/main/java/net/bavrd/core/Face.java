package net.bavrd.core;

public abstract class Face extends BavrdVerticle {

  public static final String TOKEN_USER = "user";
  public static final String TOKEN_CHANNEL = "channel";
  public static final String TOKEN_MESSAGE = "message";
  public static final String TOKEN_AUTH = "auth";

  @Override
  public BavrdComponent getType() {
    return BavrdComponent.FACE;
  }
}
