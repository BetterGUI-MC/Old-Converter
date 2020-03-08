package me.hsgamer.bettergui.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {

  private Utils() {

  }

  @SuppressWarnings("unchecked")
  public static List<String> createStringListFromObject(Object input, boolean trim, String split) {
    List<String> list = new ArrayList<>();
    if (input instanceof List) {
      list.addAll((List<String>) input);
    } else {
      String value = String.valueOf(input);
      if (split == null || split.isEmpty()) {
        list.add(value);
      } else {
        list.addAll(Arrays.asList(value.split(split)));
      }
    }
    if (trim) {
      list.replaceAll(String::trim);
    }
    return list;
  }

  public static boolean isValueNumber(String input) {
    try {
      Double.parseDouble(input);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
