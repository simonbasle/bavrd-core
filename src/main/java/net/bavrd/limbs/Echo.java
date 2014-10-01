package net.bavrd.limbs;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

import net.bavrd.core.BavrdComponent;
import net.bavrd.core.BavrdVerticle;
import net.bavrd.core.EventEnum;
import net.bavrd.core.FaceMessage;

public class Echo extends BavrdVerticle {

  private static final Pattern SAY_PATTERN = Pattern.compile("say (.*)");
  private static final Pattern ECHO_PATTERN = Pattern.compile("echo (.*)");

  private static final String DEFAULT_SAY_FORMAT = "%u told me to say '%m'";

  protected String sayFormat;

  @Override
  public void startBavrd() {
    sayFormat = container.config().getString("sayFormat", DEFAULT_SAY_FORMAT);

    vertx.eventBus().registerHandler(EventEnum.INCOMING.vertxEndpoint, new Handler<Message<JsonObject>>() {

      @Override
      public void handle(Message<JsonObject> m) {
        FaceMessage fm = FaceMessage.decodeFrom(m.body());
        Matcher sayMatcher = SAY_PATTERN.matcher(fm.message);
        if (sayMatcher.matches()) {
          say(fm.channel, fm.user, sayMatcher.group(1), true);
          return;
        }

        Matcher echoMatcher = ECHO_PATTERN.matcher(fm.message);
        if (echoMatcher.matches()) {
          say(fm.channel, fm.user, echoMatcher.group(1), false);
        }
      }
    });
  }

  protected void say(String channel, String user, String message, boolean wrapMessage) {
    String text = message;
    if (wrapMessage)
      text = sayFormat.replaceAll("%u", user).replaceAll("%m", message);
    FaceMessage reply = new FaceMessage(FaceMessage.SEND_TO_CHANNEL, channel, text);
    vertx.eventBus().send(EventEnum.OUTGOING.vertxEndpoint, reply.asJson());
  }

  @Override
  public BavrdComponent getType() {
    return BavrdComponent.LIMB;
  }

  @Override
  public java.util.Map<String, String> getHelp() {
    Map<String, String> help = new HashMap<>();
    help.put("echo X", "will echo back X to the user");
    help.put("say X", "will echo back X to the user, inside a phrase like \"user told me to say 'X'\"");
    return help;
  }
}
