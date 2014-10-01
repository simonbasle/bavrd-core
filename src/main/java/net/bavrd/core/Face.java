package net.bavrd.core;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Whitelist;

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

  public String sanitizeRichText(String htmlBody) {
    String cleanHtml = Jsoup.clean(htmlBody, FORMATTED_TEXT_WHITELIST);
    Document bodyFragment = Jsoup.parseBodyFragment(cleanHtml);
    StringBuffer output = new StringBuffer();
    for (Node n : bodyFragment.body().childNodes()) {
      output.append(sanitize(n));
    }
    return output.toString();
  }

  private String sanitize(Node n) {
    String output;
    if (n instanceof Element) {
      StringBuffer inner = new StringBuffer();
      for (Node child : n.childNodes()) {
        inner.append(sanitize(child));
      }
      String text = inner.toString();

      Element e = (Element) n;
      if (e.tagName().equals("b")) {
        output = formatBold(text);
      } else if (e.tagName().equals("br")) {
        output = formatNewLine();
      }  else if (e.tagName().equals("i")) {
        output = formatItalic(text);
      }  else if (e.tagName().equals("code")) {
        output = formatCode(text);
      }  else if (e.tagName().equals("img")) {
        output = formatImg(e.attr("abs:src"), e.attr("alt"));
      } else {
        output = text;
      }
    } else if (n instanceof TextNode) {
      output = ((TextNode) n).text();
    } else
      output = "";

    //jsoup tends to add some whitespaces before and after <br>, let's get rid of them
    if (n.nextSibling() instanceof Element && ((Element) n.nextSibling()).tagName().equals("br"))
      output = output.replaceFirst("\\s+$", "");

    if (n.previousSibling() instanceof  Element && ((Element) n.previousSibling()).tagName().equals("br"))
      output = output.replaceFirst("^\\s+", "");

    return output;
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
