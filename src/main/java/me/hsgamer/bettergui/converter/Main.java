package me.hsgamer.bettergui.converter;

import java.util.logging.Logger;

public class Main {
  private static Logger logger = Logger.getLogger(Main.class.getName());

  public static void main(String[] args) {
    if (args.length > 1) {

    } else {
      logger.info("Usage: java -jar Converter.jar <CC/DM> <filename>.yml");
    }
  }
}
