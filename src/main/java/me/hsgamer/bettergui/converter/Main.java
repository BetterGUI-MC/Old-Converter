package me.hsgamer.bettergui.converter;

import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Main {

  private static Logger logger = Logger.getLogger(Main.class.getName());

  static {
    ConsoleHandler handler = new ConsoleHandler();
    handler.setLevel(Level.INFO);
    handler.setFormatter(new Formatter() {
      @Override
      public String format(LogRecord logRecord) {
        return "[" + logRecord.getLevel() + "] " + logRecord.getMessage() + "\n";
      }
    });
    logger.addHandler(handler);
    logger.setUseParentHandlers(false);
  }

  public static void main(String[] args) throws IOException {
    if (args.length > 0) {
      try {
        Type type = Type.valueOf(System.getProperty("type", "CC"));
        logger.warning("");
        logger.warning("This is not a perfect converter");
        logger.warning("You may need to double-check your file after the conversion is completed");
        logger.warning("");
        logger.info(() -> "CONVERTER: " + type.name());

        File outputDir = new File("output");
        if (outputDir.mkdirs()) {
          logger.info("Created output folder");
        }

        for (String filename : args) {
          File file = new File(filename);
          if (file.exists()) {
            logger.info(() -> "CONVERTING \t--> " + filename);

            File output = new File("output", file.getName());
            if (output.createNewFile()) {
              logger.info("Created output file");
            }

            // Init config
            Config fromConfig = LightningBuilder.fromFile(file).createConfig();
            Config toConfig = LightningBuilder.fromFile(output).createConfig();

            // Start the convert
            type.convert(logger, fromConfig, toConfig);

            // Finish
            logger.info(() -> "CONVERTED \t--> " + outputDir.getName() + File.separator + filename);
          } else {
            logger.log(Level.WARNING, "{0} is not found", filename);
          }
        }
      } catch (IllegalArgumentException e) {
        logger.warning("Cannot find the converter for " + args[0]);
        logger.warning("Available: " + Arrays.toString(Type.values()));
      }
    } else {
      logger.info("Usage: java -Dtype=<type> -jar Converter-<version>.jar <filename>.yml");
    }
  }
}
