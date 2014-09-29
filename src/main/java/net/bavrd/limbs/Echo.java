package net.bavrd.limbs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

import net.bavrd.core.BavrdComponent;
import net.bavrd.core.BavrdVerticle;
import net.bavrd.core.EventEnum;
import net.bavrd.core.Face;

public class Echo extends BavrdVerticle {

  private static final Pattern SAY_PATTERN = Pattern.compile("say (.*)");

  private static final String DEFAULT_SAY_FORMAT = "%u told me to say '%m'";

  protected String sayFormat;

  @Override
  public void startBavrd() {
    sayFormat = container.config().getString("sayFormat", DEFAULT_SAY_FORMAT);

    vertx.eventBus().registerHandler(EventEnum.INCOMING.vertxEndpoint, new Handler<Message<JsonObject>>() {

      @Override
      public void handle(Message<JsonObject> m) {
        Face.FaceMessage fm = Face.FaceMessage.decodeFrom(m.body());
        Matcher matcher = SAY_PATTERN.matcher(fm.message);
        if (matcher.matches()) {
          say(fm.channel, fm.user, matcher.group(1));
        }
      }
    });
  }

  protected void say(String channel, String user, String params) {
    String text = sayFormat.replaceAll("%u", user).replaceAll("%m", params);
    Face.FaceMessage reply = new Face.FaceMessage(Face.FaceMessage.SEND_TO_CHANNEL, channel, text);
    vertx.eventBus().send(EventEnum.OUTGOING.vertxEndpoint, reply.asJson());
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
