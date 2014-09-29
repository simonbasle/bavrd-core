package net.bavrd.utils;

import org.vertx.java.core.Handler;

public class VertxHandlers {

  public static <T> Handler<T> noOpHandler() {
    return new Handler<T>() {
      @Override
      public void handle(T o) {
        //NO-OP
      }
    };
  }
}
