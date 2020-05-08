package me.hsgamer.bettergui.converter.converter;

import de.leonhard.storage.internal.FlatFile;
import java.util.logging.Logger;

public interface Converter {

  void convert(Logger logger, FlatFile from, FlatFile to);
}
