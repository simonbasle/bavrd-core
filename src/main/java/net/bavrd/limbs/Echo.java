package net.bavrd.limbs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

import net.bavrd.core.BavrdComponent;
import net.bavrd.core.BavrdVerticle;
import net.bavrd.core.Face;

public class Echo extends BavrdVerticle {

  private static final Pattern SAY_PATTERN = Pattern.compile("say (.*)");

  private static final String DEFAULT_SAY_FORMAT = "%u told me to say '%m'";

  protected String sayFormat;

  @Override
  public void startBavrd() {
    sayFormat = container.config().getString("sayFormat", DEFAULT_SAY_FORMAT);

    vertx.eventBus().registerHandler("bavrd-incoming", new Handler<Message<JsonObject>>() {

      @Override
      public void handle(Message<JsonObject> m) {
        String message = m.body().getString(Face.TOKEN_MESSAGE);
        Matcher matcher = SAY_PATTERN.matcher(message);
        if (matcher.matches()) {
          say(
              m.body().getString(Face.TOKEN_AUTH),
              m.body().getString(Face.TOKEN_CHANNEL),
              m.body().getString(Face.TOKEN_USER),
              matcher.group(1)
          );
        }
      }
    });
  }

  protected void say(String token, String channel, String user, String params) {
    String text = sayFormat.replaceAll("%u", user).replaceAll("%m", params);

    JsonObject reply = new JsonObject()
        .putString(Face.TOKEN_AUTH, token)
        .putString(Face.TOKEN_CHANNEL, channel)
        .putString(Face.TOKEN_MESSAGE, text);
    vertx.eventBus().send("bavrd-outgoing", reply);
  }

  @Override
  public String getName() {
    return "echo";
  }

  @Override
  public BavrdComponent getType() {
    return BavrdComponent.LIMB;
  }

  @Override
  public String getHelp() {
    return "'say X': will echo back X to the user";
  }
}
