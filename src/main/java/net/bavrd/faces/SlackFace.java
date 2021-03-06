package net.bavrd.faces;

import java.util.Collections;

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
import net.bavrd.core.FaceMessage;
import net.bavrd.utils.VertxHandlers;

public class SlackFace extends Face {

  private int port;
  private String route;
  private String botName;
  private String token;
  private String icon;

  @Override
  public void startBavrd() {
    port = container.config().getInteger("port", 8080);
    route = container.config().getString("route", "/incoming/slack/");
    botName = container.config().getString("botName", "bavrd");
    token = container.config().getString("api_token", "");
    icon = container.config().getString("bot_icon", null);

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
                  String userId = attributes.get("user_id");
                  String userName = attributes.get("user_name");
                  String channelName = attributes.get("channel_name");
                  String channelId = attributes.get("channel_id");

                  //TODO verifications
                  FaceMessage incoming = FaceMessage.incoming(userId, userName, channelId, channelName, message);

                  vertx.eventBus().publish(EventEnum.INCOMING.vertxEndpoint, incoming.asJson());
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
        Escaper esc = UrlEscapers.urlFormParameterEscaper();
        FaceMessage body = FaceMessage.decodeFrom(m.body());

        //sanitize the text and convert to Slack formatting
        String formattedText = formatText(body.message.trim());

        StringBuffer payload = new StringBuffer()
            .append("token=")
            .append(esc.escape(token))
            .append("&text=")
            .append(esc.escape(formattedText))
            .append("&username=")
            .append(esc.escape(botName));

        if (icon != null) {
          if (icon.startsWith(":"))
            payload.append("&icon_emoji=");
          else
            payload.append("&icon_url=");
          payload.append(esc.escape(icon));
        }

        if (body.isReply && body.isPrivate)
          payload.append("&channel=").append(esc.escape("@"+body.userId));
        else
          payload.append("&channel=").append(esc.escape(body.channelId));

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
  protected String formatBold(String text) {
    return "*"+text+"*";
  }

  @Override
  protected String formatItalic(String text) {
    return "_"+text+"_";
  }

  @Override
  protected String formatCode(String text) {
    return "`"+text+"`";
  }

  @Override
  protected String formatImg(String imageSrc, String imageAlt) {
    //no image support in Slack, try to give a link to image with alt text as label if possible
    if (imageAlt.length() > 0)
      return "<"+imageSrc+"|"+imageAlt+">";
    return imageSrc;
  }

  @Override
  protected String formatNewLine() {
    return "\n";
  }

  @Override
  public java.util.Map<String, String> getHelp() {
    return Collections.emptyMap();
  }
}
