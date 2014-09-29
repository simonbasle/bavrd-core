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

import net.bavrd.core.Face;
import net.bavrd.utils.VertxHandlers;

public class SlackFace extends Face {

  @Override
  public void startBavrd() {
    final int port = container.config().getInteger("port", 8080);
    final String route = container.config().getString("route", "/incoming/slack/");
    final String botName = container.config().getString("botName", "bavrd");
    final String token = container.config().getString("api_token", "");

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

                  JsonObject attributesJson = new JsonObject();
                  attributesJson.putString(TOKEN_USER, attributes.get("user_name"));
                  attributesJson.putString(TOKEN_CHANNEL, attributes.get("channel_id"));
                  attributesJson.putString(TOKEN_MESSAGE, message);
                  attributesJson.putString(TOKEN_AUTH, attributes.get("token"));

                  vertx.eventBus().publish("bavrd-incoming", attributesJson);
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

    vertx.eventBus().registerHandler("bavrd-outgoing", new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> m) {
        String message = m.body().getString(TOKEN_MESSAGE);
        String channelId = m.body().getString(TOKEN_CHANNEL);
        String notification_token = m.body().getString(TOKEN_AUTH);

        Escaper esc = UrlEscapers.urlFormParameterEscaper();
        StringBuffer payload = new StringBuffer()
            .append("token=")
            .append(esc.escape(token))
            .append("&channel=")
            .append(esc.escape(channelId))
            .append("&text=")
            .append(esc.escape(message.trim()))
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
