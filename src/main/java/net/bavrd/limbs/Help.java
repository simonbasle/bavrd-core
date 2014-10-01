package net.bavrd.limbs;

import java.util.Collections;
import java.util.Map;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.shareddata.ConcurrentSharedMap;

import net.bavrd.core.BavrdComponent;
import net.bavrd.core.BavrdVerticle;
import net.bavrd.core.EventEnum;
import net.bavrd.core.FaceMessage;
import net.bavrd.utils.HelpUtils;

public class Help extends BavrdVerticle {

  @Override
  public void startBavrd() {
    vertx.eventBus().registerHandler(EventEnum.INCOMING.vertxEndpoint, new Handler<Message<JsonObject>>() {

      @Override
      public void handle(Message<JsonObject> m) {
        FaceMessage fm = FaceMessage.decodeFrom(m.body());
        if ("help".equalsIgnoreCase(fm.message)) {
          StringBuffer help = new StringBuffer();
          help.append("<b>Hello ").append(fm.userName)
              .append(", I'm <i>").append(container.config().getString("botName")).append("</i>")
              .append(" and I know the following commands</b> :");

          //get the global help map, deserialize each entry and format it for display
          ConcurrentSharedMap<String, String> globalHelpMap = vertx.sharedData().getMap(HelpUtils.SHARED_DATA_HELP);
          for (String serializedHelpMap : globalHelpMap.values()) {
            Map<String, String> helpMap = HelpUtils.deserializeHelp(serializedHelpMap);
            for (Map.Entry<String, String> e : helpMap.entrySet()) {
              help.append("<br/><code>").append(e.getKey()).append("</code> - ").append(e.getValue());
            }
          }

          FaceMessage response = fm.reply(help.toString());
          vertx.eventBus().send(EventEnum.OUTGOING.vertxEndpoint, response.asJson());
        }
      }
    });
  }

  @Override
  public BavrdComponent getType() {
    return BavrdComponent.LIMB;
  }

  @Override
  public Map<String, String> getHelp() {
    return Collections.singletonMap("help", "lists all my registered commands");
  }
}
