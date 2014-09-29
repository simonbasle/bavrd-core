package net.bavrd.core;

import org.vertx.java.core.json.JsonObject;

public abstract class Face extends BavrdVerticle {

  @Override
  public BavrdComponent getType() {
    return BavrdComponent.FACE;
  }

  public static final class FaceMessage {

    private static final String TOKEN_USER = "user";
    private static final String TOKEN_CHANNEL = "channel";
    private static final String TOKEN_MESSAGE = "message";

    public final String user;
    public final String channel;
    public final String message;

    public FaceMessage(String user, String channel, String message) {
      this.user = user;
      this.channel = channel;
      this.message = message;
    }

    public static FaceMessage decodeFrom(JsonObject body) {
      return new FaceMessage(body.getString(TOKEN_USER), body.getString(TOKEN_CHANNEL), body.getString(TOKEN_MESSAGE));
    }

    public JsonObject asJson() {
      JsonObject result = new JsonObject();
      result.putString(TOKEN_USER, user);
      result.putString(TOKEN_CHANNEL, channel);
      result.putString(TOKEN_MESSAGE, message);
      return null;
    }
  }
}
