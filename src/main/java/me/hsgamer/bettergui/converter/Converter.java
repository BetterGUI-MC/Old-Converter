package me.hsgamer.bettergui.converter;

import de.leonhard.storage.internal.FlatFile;

public interface Converter {

  void convert(FlatFile from, FlatFile to);
}
