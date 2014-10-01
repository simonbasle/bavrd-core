package net.bavrd.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * An utility class to help correctly serializing / deserializing modules help maps, so as to store them into a shared vertx map, one entry per module
 */
public class HelpUtils {

  /** the id of the vertx shared map that contains serialized help entries for all active modules */
  public static final String SHARED_DATA_HELP = "bavrd-help";

  private static final String COMMAND_SEPARATOR = "\n";
  private static final String DESC_SEPARATOR = "~";

  /**
   * Serializes a module's help map, in order to be able to store it as a single entry in a vertx shared map
   * @param moduleHelp the module's help map (see {@link net.bavrd.core.BavrdVerticle#getHelp()})
   * @return the serialized help map
   */
  public static final String serializeHelp(Map<String, String> moduleHelp) {
    StringBuffer help = new StringBuffer();
    for(Map.Entry<String, String> e : moduleHelp.entrySet()) {
      help.append(COMMAND_SEPARATOR).append(e.getKey());
      help.append(DESC_SEPARATOR).append(e.getValue());
    }
    return help.toString();
  }

  /**
   * Deserializes a module's help map that was stored as a single entry in a vertx shared map
   * @param serialized the serialized help map
   * @return  the module's help map (see {@link net.bavrd.core.BavrdVerticle#getHelp()})
   */
  public static final Map<String, String> deserializeHelp(String serialized) {
    String[] commands = serialized.split(COMMAND_SEPARATOR);
    Map<String, String> result = new HashMap<>(commands.length);
    for (String command : commands) {
      String[] keyValuePair = command.split(DESC_SEPARATOR);
      if (keyValuePair.length == 2)
        result.put(keyValuePair[0], keyValuePair[1]);
    }
    return result;
  }
}
