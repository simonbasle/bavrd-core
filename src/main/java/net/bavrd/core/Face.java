package net.bavrd.core;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.vertx.java.core.json.JsonObject;

public abstract class Face extends BavrdVerticle {

  protected static final Whitelist FORMATTED_TEXT_WHITELIST = new Whitelist()
      .addTags("b", "br", "code", "i", "img")
        .addAttributes("img", "alt", "src")
        .addProtocols("img", "src", "http", "https")
        ;

  @Override
  public BavrdComponent getType() {
    return BavrdComponent.FACE;
  }

  protected String sanitizeRichText(String htmlBody) {
    String cleanHtml = Jsoup.clean(htmlBody, FORMATTED_TEXT_WHITELIST);
    Document bodyFragment = Jsoup.parseBodyFragment(cleanHtml);
    StringBuffer output = new StringBuffer();
    for (Element e : bodyFragment.getAllElements()) {
      if (e.tagName().equals("b")) {
        output.append(formatBold(e.text()));
      } else if (e.tagName().equals("br")) {
        output.append(formatNewLine());
      }  else if (e.tagName().equals("i")) {
        output.append(formatItalic(e.text()));
      }  else if (e.tagName().equals("code")) {
        output.append(formatCode(e.text()));
      }  else if (e.tagName().equals("img")) {
        output.append(formatImg(e.attr("abs:src"), e.attr("alt")));
      } else {
        output.append(e.text());
      }
    }
    return output.toString();
  }

  /**
   * Override this method to allow formatting of bold text with b html tag
   * @param text the text that was detected as bold
   * @return the text formatted accordingly to the distant chat rules for displaying bold text
   */
  protected String formatBold(String text) {
    return text;
  }

  /**
   * Override this method to allow formatting of italic text with i html tag
   * @param text the text that was detected as italic
   * @return the text formatted accordingly to the distant chat rules for displaying italic text
   */
  protected String formatItalic(String text) {
    return text;
  }

  /**
   * Override this method to allow formatting of code text with b html tag
   * @param text the text that was detected as code
   * @return the text formatted accordingly to the distant chat rules for displaying code preformatted text
   */
  protected String formatCode(String text) {
    return text;
  }

  /**
   * Override this method to allow formatting of images with img html tag
   * @param imageSrc the url that was detected as an image source
   * @param imageAlt the optionnal alt text detected in the image tag (or empty string if none)
   * @return a text formatted accordingly to the distant chat rules for displaying images in text
   */
  protected String formatImg(String imageSrc, String imageAlt) {
    return imageSrc;
  }

  /**
   * Override this method to allow introduction of new lines in text
   * @return a string that creates a new line accordingly to the distant chat rules for displaying formatted text
   */
  protected String formatNewLine() {
    return "\n";
  }

}
