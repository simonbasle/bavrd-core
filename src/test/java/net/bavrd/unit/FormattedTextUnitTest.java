package net.bavrd.unit;

import java.util.Map;

import org.jsoup.Jsoup;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.bavrd.core.Face;
import net.bavrd.faces.SlackFace;

public class FormattedTextUnitTest {

  @Test
  public void testSlackFormatter() {
    SlackFace sf = new SlackFace();

    String inputString = "<img src='http://upload.wikimedia.org/wikipedia/commons/7/72/Torchlight_help_icon.svg' alt='Help Icon'/><br/>"
        + "<b>The following commands are available for <i>bot</i></b>:"
        + "<br/><code>command1</code> - description of command 1";

    String expectedOutput = "<http://upload.wikimedia.org/wikipedia/commons/7/72/Torchlight_help_icon.svg|Help Icon>"
        + "\n*The following commands are available for _bot_*:"
        + "\n`command1` - description of command 1";

    Assert.assertEquals(expectedOutput, sf.sanitizeRichText(inputString));
  }
}
