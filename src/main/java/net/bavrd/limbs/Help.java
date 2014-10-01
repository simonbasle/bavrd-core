package net.bavrd.limbs;

import java.util.Collections;
import java.util.Map;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
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
    EventBus eventBus = vertx.eventBus().registerHandler(EventEnum.INCOMING.vertxEndpoint, new Handler<Message<JsonObject>>() {

      @Override
      public void handle(Message<JsonObject> m) {
        FaceMessage fm = FaceMessage.decodeFrom(m.body());
        if ("help".equalsIgnoreCase(fm.message)) {
          ConcurrentSharedMap<String, String> helpMap = vertx.sharedData().getMap(SHARED_DATA_HELP);
          StringBuffer help = new StringBuffer("The following commands are available for " + container.config().getString("botName") + ":");
          for(Map.Entry<String, String> e : helpMap.entrySet()) {
            help.append("\n*").append(e.getKey()).append("* - ").append(e.getValue());
          }
          FaceMessage response = new FaceMessage(FaceMessage.SEND_TO_CHANNEL, fm.channel, help.toString());
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
    return Collections.singletonMap("help", "lists all registered commands in this bot");
  }
}
