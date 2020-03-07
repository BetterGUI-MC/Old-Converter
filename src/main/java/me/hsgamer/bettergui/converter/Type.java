package me.hsgamer.bettergui.converter;

import de.leonhard.storage.internal.FlatFile;
import me.hsgamer.bettergui.converter.impl.ChestCommands;

public enum Type {
  CC(new ChestCommands())
  ;
  private Converter converter;
  Type(Converter converter) {
    this.converter = converter;
  }

  public void convert(FlatFile from, FlatFile to) {
    converter.convert(from, to);
  }
}
