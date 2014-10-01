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

public class Help extends BavrdVerticle {

  @Override
  public void startBavrd() {
    vertx.eventBus().registerHandler(EventEnum.INCOMING.vertxEndpoint, new Handler<Message<JsonObject>>() {

      @Override
      public void handle(Message<JsonObject> m) {
        FaceMessage fm = FaceMessage.decodeFrom(m.body());
        if ("help".equalsIgnoreCase(fm.message)) {
          ConcurrentSharedMap<String, String> helpMap = vertx.sharedData().getMap(SHARED_DATA_HELP);
          StringBuffer help = new StringBuffer();
          help.append("<b>The following commands are available for <i>" + container.config().getString("botName") + "</i></b>:");
          for(Map.Entry<String, String> e : helpMap.entrySet()) {
            help.append("<br/><code>").append(e.getKey()).append("</code> - ").append(e.getValue());
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
    return Collections.singletonMap("help", "lists all registered commands of this bot");
  }
}
