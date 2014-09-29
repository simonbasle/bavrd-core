package net.bavrd.faces;

import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;

import net.bavrd.core.EventEnum;
import net.bavrd.core.Face;
import net.bavrd.utils.VertxHandlers;

public class SlackFace extends Face {

  private int port;
  private String route;
  private String botName;
  private String token;

  @Override
  public void startBavrd() {
    port = container.config().getInteger("port", 8080);
    route = container.config().getString("route", "/incoming/slack/");
    botName = container.config().getString("botName", "bavrd");
    token = container.config().getString("api_token", "");

    vertx.createHttpServer()
        .requestHandler(new Handler<HttpServerRequest>() {
          @Override
          public void handle(final HttpServerRequest req) {
            if (!req.path().equalsIgnoreCase(route)
                || !req.method().equals("POST")) {
              req.response().setStatusCode(404).end();
            } else {
              req.expectMultiPart(true);
              req.bodyHandler(new Handler<Buffer>() {
                @Override
                public void handle(Buffer buffer) {
                  String payload = buffer.toString();
                  container.logger().debug(payload);

                  MultiMap attributes = req.formAttributes();
                  String trigger = attributes.get("trigger_word");
                  String message = attributes.get("text");
                  message = message.replaceFirst(trigger, "").trim();

                  //TODO verifications
                  FaceMessage formatted = new FaceMessage(attributes.get("user_name"),
                      attributes.get("channel_id"),
                      message);

                  vertx.eventBus().publish(EventEnum.INCOMING.vertxEndpoint, formatted.asJson());
                  req.response().end();
                }
              });
            }
          }
        })
        .listen(port);

    final HttpClient slack = vertx.createHttpClient()
        .setSSL(true)
        .setTrustAll(true)
        .setPort(443)
        .setHost("slack.com");

    vertx.eventBus().registerHandler(EventEnum.OUTGOING.vertxEndpoint, new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> m) {
        FaceMessage body = FaceMessage.decodeFrom(m.body());

        Escaper esc = UrlEscapers.urlFormParameterEscaper();
        StringBuffer payload = new StringBuffer()
            .append("token=")
            .append(esc.escape(token))
            .append("&channel=")
            .append(esc.escape(body.channel))
            .append("&text=")
            .append(esc.escape(body.message.trim()))
            .append("&username=")
            .append(esc.escape(botName));

        Handler<HttpClientResponse> responseHandler;
        if (container.logger().isDebugEnabled())
          responseHandler = new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse response) {
              container.logger().debug(response.statusCode() + " : " + response.statusMessage());
            }
          };
        else
          responseHandler = VertxHandlers.noOpHandler();

        slack.post("/api/chat.postMessage", responseHandler)
            .putHeader("Content-Type", "application/x-www-form-urlencoded")
            .end(payload.toString());
      }
    });
  }

  @Override
  public String getName() {
    return "Slack";
  }

  @Override
  public String getHelp() {
    return "Slack Integration";
  }
}
