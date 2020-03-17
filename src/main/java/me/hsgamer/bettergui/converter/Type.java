package me.hsgamer.bettergui.converter;

import de.leonhard.storage.internal.FlatFile;
import java.util.logging.Logger;
import me.hsgamer.bettergui.converter.impl.ChestCommands;
import me.hsgamer.bettergui.converter.impl.DeluxeMenus;

public enum Type {
  CC(new ChestCommands()),
  DM(new DeluxeMenus());
  private Converter converter;

  Type(Converter converter) {
    this.converter = converter;
  }

  public void convert(Logger logger, FlatFile from, FlatFile to) {
    converter.convert(logger, from, to);
  }
}
