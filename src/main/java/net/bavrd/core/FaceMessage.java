package net.bavrd.core;

import org.vertx.java.core.json.JsonObject;

public final class FaceMessage {

  private static final String TOKEN_USER = "user";
  private static final String TOKEN_CHANNEL = "channel";
  private static final String TOKEN_MESSAGE = "message";
  private static final String TOKEN_ISREPLY = "isReply";
  private static final String TOKEN_ISPRIVATE = "isPrivate";

  /** the user that issued the command, or if isReply and isPrivate, the user to whom to reply to */
  public final String user;
  public final String channel;
  public final String message;
  public final boolean isReply;
  public final boolean isPrivate;

  private FaceMessage(String user, String channel, String message, boolean isReply, boolean isPrivate) {
    this.user = user;
    this.channel = channel;
    this.message = message;
    this.isReply = isReply;
    this.isPrivate = isPrivate;
  }

  public FaceMessage reply(String replyContent) {
    return new FaceMessage(this.user, this.channel, replyContent, true, false);
  }

  public FaceMessage replyPrivate(String replyContent) {
    return new FaceMessage(this.user, this.channel, replyContent, true, true);
  }

  public static FaceMessage incoming(String from, String inChannel, String content) {
    return new FaceMessage(from, inChannel, content, false, false);
  }

  public static FaceMessage decodeFrom(JsonObject body) {
    return new FaceMessage(
        body.getString(TOKEN_USER),
        body.getString(TOKEN_CHANNEL),
        body.getString(TOKEN_MESSAGE),
        body.getBoolean(TOKEN_ISREPLY, Boolean.FALSE),
        body.getBoolean(TOKEN_ISPRIVATE, Boolean.FALSE));
  }

  public JsonObject asJson() {
    JsonObject result = new JsonObject();
    result.putString(TOKEN_USER, user);
    result.putString(TOKEN_CHANNEL, channel);
    result.putString(TOKEN_MESSAGE, message);
    result.putBoolean(TOKEN_ISREPLY, isReply);
    result.putBoolean(TOKEN_ISPRIVATE, isPrivate);
    return result;
  }
}
