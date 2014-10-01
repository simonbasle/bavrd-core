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
          String text = sayFormat.replaceAll("%u", fm.userName).replaceAll("%m", sayMatcher.group(1));
          FaceMessage reply = fm.reply(text);
          vertx.eventBus().send(EventEnum.OUTGOING.vertxEndpoint, reply.asJson());
          return;
        }

        Matcher echoMatcher = ECHO_PATTERN.matcher(fm.message);
        if (echoMatcher.matches()) {
          FaceMessage reply = fm.reply(echoMatcher.group(1));
          vertx.eventBus().send(EventEnum.OUTGOING.vertxEndpoint, reply.asJson());
        }
      }
    });
  }

  @Override
  public BavrdComponent getType() {
    return BavrdComponent.LIMB;
  }

  @Override
  public java.util.Map<String, String> getHelp() {
    Map<String, String> help = new HashMap<>();
    help.put("echo X", "will echo back <code>X</code> to you");
    help.put("say X", "will echo back <code>X</code> to you, inside a phrase like <i>user told me to say 'X'</i>");
    return help;
  }
}
