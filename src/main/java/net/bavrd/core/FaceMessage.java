package net.bavrd.core;

import org.vertx.java.core.json.JsonObject;

public final class FaceMessage {

  private static final String TOKEN_USER = "user";
  private static final String TOKEN_USERID = "userId";
  private static final String TOKEN_CHANNEL = "channel";
  private static final String TOKEN_CHANNELID = "channelId";
  private static final String TOKEN_MESSAGE = "message";
  private static final String TOKEN_ISREPLY = "isReply";
  private static final String TOKEN_ISPRIVATE = "isPrivate";

  /** a human-friendly identification of the user that issued the command */
  public final String userName;
  /** a machine-friendly identification of the user that issued the command, useful for example to reply in private to the user */
  public final String userId;
  /** a human-friendly identification of the channel in which the command was issued */
  public final String channelName;
  /** a machine-friendly identification of the channel in which the command was issued, useful to reply in it */
  public final String channelId;
  /** the (incoming) command or (outgoing) formatted response */
  public final String message;
  /** flag to mark a message as a response */
  public final boolean isReply;
  /** flag to mark a response as private (must be sent only to original user) */
  public final boolean isPrivate;

  private FaceMessage(String user, String userId, String channel, String channelId, String message, boolean isReply, boolean isPrivate) {
    this.userName = user;
    this.userId = userId;
    this.channelName = channel;
    this.channelId = channelId;
    this.message = message;
    this.isReply = isReply;
    this.isPrivate = isPrivate;
  }

  public FaceMessage reply(String replyContent) {
    return new FaceMessage(this.userName, this.userId, this.channelName, this.channelId, replyContent, true, false);
  }

  public FaceMessage replyPrivate(String replyContent) {
    return new FaceMessage(this.userName, this.userId, this.channelName, this.channelId, replyContent, true, true);
  }

  public static FaceMessage incoming(String fromId, String fromName, String inChannelId, String inChannelName, String content) {
    return new FaceMessage(fromName, fromId, inChannelName, inChannelId, content, false, false);
  }

  public static FaceMessage decodeFrom(JsonObject body) {
    return new FaceMessage(
        body.getString(TOKEN_USER),
        body.getString(TOKEN_USERID),
        body.getString(TOKEN_CHANNEL),
        body.getString(TOKEN_CHANNELID),
        body.getString(TOKEN_MESSAGE),
        body.getBoolean(TOKEN_ISREPLY, Boolean.FALSE),
        body.getBoolean(TOKEN_ISPRIVATE, Boolean.FALSE));
  }

  public JsonObject asJson() {
    JsonObject result = new JsonObject();
    result.putString(TOKEN_USER, userName);
    result.putString(TOKEN_USERID, userId);
    result.putString(TOKEN_CHANNEL, channelName);
    result.putString(TOKEN_CHANNELID, channelId);
    result.putString(TOKEN_MESSAGE, message);
    result.putBoolean(TOKEN_ISREPLY, isReply);
    result.putBoolean(TOKEN_ISPRIVATE, isPrivate);
    return result;
  }
}
