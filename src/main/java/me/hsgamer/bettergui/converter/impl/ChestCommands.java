package me.hsgamer.bettergui.converter.impl;

import de.leonhard.storage.internal.FlatFile;
import de.leonhard.storage.sections.FlatFileSection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.hsgamer.bettergui.converter.Converter;
import me.hsgamer.bettergui.converter.Utils;

public class ChestCommands implements Converter {

  private static List<Integer> toSlots(String input) {
    List<Integer> slots = new ArrayList<>();
    for (String string : input.split(",")) {
      string = string.trim();
      if (Utils.isValueNumber(string)) {
        slots.add(Integer.parseInt(string));
      } else {
        String[] split = string.split("-", 2);
        split[0] = split[0].trim();
        split[1] = split[1].trim();
        if (Utils.isValueNumber(split[0]) && Utils.isValueNumber(split[1])) {
          int s1 = Integer.parseInt(split[0]);
          int s2 = Integer.parseInt(split[1]);
          int start = Math.min(s1, s2);
          int end = Math.max(s1, s2);
          for (int i = start; i <= end; i++) {
            slots.add(i);
          }
        }
      }
    }
    slots.replaceAll(slot -> slot - 1);
    return slots;
  }

  public void convert(Logger logger, FlatFile from, FlatFile to) {
    for (String key : from.singleLayerKeySet()) {
      FlatFileSection section = from.getSection(key);
      to.setPathPrefix(key);

      if (key.equalsIgnoreCase("menu-settings")) {
        for (String subkey : section.singleLayerKeySet()) {
          switch (subkey) {
            case MenuSettings.MENU_COMMAND:
            case MenuSettings.OPEN_ACTION:
            case MenuSettings.CLOSE_ACTION:
              List<String> commands = Utils
                  .createStringListFromObject(section.get(subkey, Object.class), true, ";");
              to.set(subkey, commands);
              break;
            case MenuSettings.OPEN_ITEM:
              // IGNORED
              break;
            case MenuSettings.AUTO_REFRESH:
            case MenuSettings.ROWS:
              to.set(subkey, section.getInt(subkey));
              break;
            default:
              to.set(subkey, section.get(subkey));
              break;
          }
        }
      } else {
        for (String subkey : section.singleLayerKeySet()) {
          switch (subkey) {
            case IconNodes.ID:
              to.set(subkey, section.getString(subkey).replace(" ", "_"));
              break;
            case IconNodes.SKULL_OWNER:
              to.set("head", section.getString(subkey));
              break;
            case IconNodes.DATA_VALUE:
            case IconNodes.DURABILITY:
              to.set("damage", section.getString(subkey));
              break;
            case IconNodes.KEEP_OPEN:
              to.set("close-on-click", !section.getBoolean(subkey));
              break;
            case IconNodes.COMMAND:
              Object value = section.get(IconNodes.COMMAND);
              if (value instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) value;
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                  List<String> commands = Utils
                      .createStringListFromObject(entry.getValue(), true, ";");
                  to.set(String.join(".", subkey, entry.getKey()), commands);
                }
              } else {
                List<String> commands = Utils.createStringListFromObject(value, true, ";");
                to.set(subkey, commands);
              }
              break;
            case IconNodes.ENCHANT:
              List<String> enchants = Utils
                  .createStringListFromObject(section.get(IconNodes.ENCHANT), true, ";");
              to.set(subkey, enchants);
              to.set("flag", Collections.singletonList("HIDE_ENCHANTS"));
              break;
            case IconNodes.POSITION_X:
            case IconNodes.POSITION_Y:
              to.set(subkey, section.getInt(subkey));
              break;
            case IconNodes.SLOT:
              List<String> strings = new ArrayList<>();
              toSlots(section.getString(subkey)).forEach(slot -> strings.add(String.valueOf(slot)));
              to.set(subkey, String.join(", ", strings));
              break;
            case IconNodes.PRICE:
              to.set(IconNodes.CLICK_REQUIREMENT + ".money", section.get(subkey));
              break;
            case IconNodes.POINTS:
              to.set(IconNodes.CLICK_REQUIREMENT + ".point", section.get(subkey));
              break;
            case IconNodes.TOKENS:
              to.set(IconNodes.CLICK_REQUIREMENT + ".token", section.get(subkey));
              break;
            case IconNodes.EXP_LEVELS:
              to.set(IconNodes.CLICK_REQUIREMENT + ".level", section.get(subkey));
              break;
            case IconNodes.PERMISSION:
              to.set(IconNodes.CLICK_REQUIREMENT + ".permission", section.get(subkey));
              break;
            case IconNodes.VIEW_PERMISSION:
              to.set(IconNodes.VIEW_REQUIREMENT + ".permission", section.get(subkey));
              break;
            case IconNodes.COOLDOWN:
            case IconNodes.REQUIRED_ITEM:
            case IconNodes.PERMISSION_MESSAGE:
            case IconNodes.CLICK_REQUIREMENT_MESSAGE:
            case IconNodes.COOLDOWN_MESSAGE:
              logger.log(Level.WARNING, "Ignored {0}.{1}",
                  new Object[]{section.getPathPrefix(), subkey});
              break;
            default:
              to.set(subkey, section.get(subkey));
              break;
          }
        }
      }
    }
  }

  private static class MenuSettings {

    static final String MENU_COMMAND = "command";
    static final String OPEN_ACTION = "open-action";
    static final String CLOSE_ACTION = "close-action";
    static final String OPEN_ITEM = "open-with-item";
    static final String ROWS = "rows";
    static final String AUTO_REFRESH = "auto-refresh";
  }

  private static class IconNodes {

    static final String ID = "ID";
    static final String DATA_VALUE = "DATA-VALUE";
    static final String DURABILITY = "DURABILITY";
    static final String ENCHANT = "ENCHANTMENT";
    static final String SKULL_OWNER = "SKULL-OWNER";
    static final String COMMAND = "COMMAND";
    static final String KEEP_OPEN = "KEEP-OPEN";
    static final String POSITION_X = "POSITION-X";
    static final String POSITION_Y = "POSITION-Y";
    static final String SLOT = "SLOT";
    static final String COOLDOWN = "COOLDOWN";
    static final String VIEW_REQUIREMENT = "VIEW-REQUIREMENT";
    static final String CLICK_REQUIREMENT = "CLICK-REQUIREMENT";
    static final String COOLDOWN_MESSAGE = "COOLDOWN-MESSAGE";

    static final String PRICE = "PRICE";
    static final String POINTS = "POINTS";
    static final String TOKENS = "TOKENS";
    static final String EXP_LEVELS = "LEVELS";
    static final String REQUIRED_ITEM = "REQUIRED-ITEM";
    static final String PERMISSION = "PERMISSION";
    static final String PERMISSION_MESSAGE = "PERMISSION-MESSAGE";
    static final String VIEW_PERMISSION = "VIEW-PERMISSION";
    static final String CLICK_REQUIREMENT_MESSAGE = "CLICK-REQUIREMENT-MESSAGE";
  }
}
