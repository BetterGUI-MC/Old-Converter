package me.hsgamer.bettergui.converter;

import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
  private static Logger logger = Logger.getLogger(Main.class.getName());

  public static void main(String[] args) throws IOException {
    if (args.length > 1) {
      try {
        Type type = Type.valueOf(args[0].toUpperCase());
        String filename = args[1];
        File file = new File(filename);
        if (file.exists()) {
          File output = new File("output_" + file.getName());
          if (output.createNewFile()) {
            logger.info("Created output file");
          }
          Config fromConfig = LightningBuilder.fromFile(file).createConfig();
          Config toConfig = LightningBuilder.fromFile(output).createConfig();
          type.convert(fromConfig, toConfig);
        } else {
          logger.log(Level.WARNING, "{0} is not found", filename);
        }
      } catch (IllegalArgumentException e) {
        logger.warning("Cannot find the converter for " + args[0]);
      }
    } else {
      logger.info("Usage: java -jar Converter.jar <CC/DM> <filename>.yml");
    }
  }
}
