package net.bavrd.core;

public enum EventEnum {

  INCOMING("bavrd-incoming"),
  OUTGOING("bavrd-outgoing"),
  DISCOVER("bavrd-discover"),
  BRAIN_GET("bavrd-brain-get"),
  BRAIN_PUT("bavrd-brain-put");

  public String vertxEndpoint;

  private EventEnum(String vertxEndpoint) {
    this.vertxEndpoint = vertxEndpoint;
  }

  @Override
  public String toString() {
    return this.vertxEndpoint;
  }
}
