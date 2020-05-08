package me.hsgamer.bettergui.converter.converter.impl;

import de.leonhard.storage.internal.FlatFile;
import de.leonhard.storage.sections.FlatFileSection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import me.hsgamer.bettergui.converter.Utils;
import me.hsgamer.bettergui.converter.converter.Converter;
import me.hsgamer.bettergui.converter.menupart.impl.Icon;
import me.hsgamer.bettergui.converter.menupart.impl.Settings;

public class ChestCommands implements Converter {

  private static final List<String> CLICK_TYPE = Arrays.asList(
      "CONTROL_DROP",
      "CREATIVE",
      "DOUBLE_CLICK",
      "DROP",
      "LEFT",
      "MIDDLE",
      "NUMBER_KEY",
      "RIGHT",
      "SHIFT_LEFT",
      "SHIFT_RIGHT",
      "UNKNOWN",
      "WINDOW_BORDER_LEFT",
      "WINDOW_BORDER_RIGHT",
      "DEFAULT"
  );

  private static final List<String> REQUIREMENTS = Arrays.asList(
      "LEVEL",
      "MONEY",
      "PERMISSION",
      "POINT",
      "TOKEN",
      "CONDITION",
      "ITEM"
  );

  private static List<Integer> toSlots(String input) {
    List<Integer> slots = new ArrayList<>();
    for (String string : input.split(",")) {
      string = string.trim();
      if (Utils.isValidNumber(string)) {
        slots.add(Integer.parseInt(string));
      } else {
        String[] split = string.split("-", 2);
        split[0] = split[0].trim();
        split[1] = split[1].trim();
        if (Utils.isValidNumber(split[0]) && Utils.isValidNumber(split[1])) {
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

  private Settings getSettings(Logger logger, FlatFileSection section) {
    Settings settings = new Settings();

    for (String key : section.singleLayerKeySet()) {
      switch (key) {
        case MenuSettings.MENU_COMMAND:
          settings.setCommand(Utils
              .createStringListFromObject(section.get(key), true, ";"));
          break;
        case MenuSettings.OPEN_ACTION:
          settings.setOpenAction(Utils
              .createStringListFromObject(section.get(key), true, ";"));
          break;
        case MenuSettings.CLOSE_ACTION:
          settings.setCloseAction(Utils
              .createStringListFromObject(section.get(key), true, ";"));
          break;
        case MenuSettings.AUTO_REFRESH:
          settings.setAutoRefresh(section.getInt(key));
          break;
        case MenuSettings.ROWS:
          settings.setRows(section.getInt(key));
          break;
        case MenuSettings.INVENTORY_TYPE:
          settings.setInventoryType(section.getString(key));
          break;
        case MenuSettings.NAME:
          settings.setName(section.getString(key));
          break;
        default:
          logger.warning(() -> "Ignored " + section.getPathPrefix() + "." + key);
          break;
      }
    }
    return settings;
  }

  @SuppressWarnings("unchecked")
  private Icon getIcon(Logger logger, String name, FlatFileSection section, FlatFile from) {
    Icon icon = new Icon(name);

    for (String key : section.singleLayerKeySet()) {
      switch (key) {
        case IconNodes.ID:
          icon.setId(section.getString(key).replace(" ", "_"));
          break;
        case IconNodes.SKULL_OWNER:
          icon.addProperty("head", section.getString(key));
          break;
        case IconNodes.DATA_VALUE:
        case IconNodes.DURABILITY:
          icon.setDamage(String.valueOf(section.get(key)));
          break;
        case IconNodes.KEEP_OPEN:
          icon.addProperty("close-on-click", !section.getBoolean(key));
          break;
        case IconNodes.COMMAND:
          Object value = section.get(IconNodes.COMMAND);
          if (value instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) value;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
              List<String> commands = Utils
                  .createStringListFromObject(entry.getValue(), true, ";");
              icon.addProperty(String.join(".", key, entry.getKey()), commands);
            }
          } else {
            List<String> commands = Utils.createStringListFromObject(value, true, ";");
            icon.addProperty(key, commands);
          }
          break;
        case IconNodes.ENCHANT:
          List<String> enchants = Utils
              .createStringListFromObject(section.get(IconNodes.ENCHANT), true, ";");
          icon.setEnchant(enchants);
          icon.addFlag("HIDE_ENCHANTS");
          break;
        case IconNodes.POSITION_X:
        case IconNodes.POSITION_Y:
          icon.addProperty(key, section.getInt(key));
          break;
        case IconNodes.SLOT:
          List<String> strings = new ArrayList<>();
          toSlots(section.getString(key)).forEach(slot -> strings.add(String.valueOf(slot)));
          icon.setSlot(String.join(", ", strings));
          break;
        case IconNodes.PRICE:
          icon.addProperty(IconNodes.CLICK_REQUIREMENT + ".default.converted.money",
              section.get(key));
          break;
        case IconNodes.POINTS:
          icon.addProperty(IconNodes.CLICK_REQUIREMENT + ".default.converted.point",
              section.get(key));
          break;
        case IconNodes.TOKENS:
          icon.addProperty(IconNodes.CLICK_REQUIREMENT + ".default.converted.token",
              section.get(key));
          break;
        case IconNodes.EXP_LEVELS:
          icon.addProperty(IconNodes.CLICK_REQUIREMENT + ".default.converted.level",
              section.get(key));
          break;
        case IconNodes.PERMISSION:
          icon.addProperty(IconNodes.CLICK_REQUIREMENT + ".default.converted.permission.value",
              Utils.createStringListFromObject(section.get(key), true, ";"));
          break;
        case IconNodes.VIEW_PERMISSION:
          icon.addProperty(IconNodes.VIEW_REQUIREMENT + ".converted.permission",
              Utils.createStringListFromObject(section.get(key), true, ";"));
          break;
        case IconNodes.CLICK_REQUIREMENT:
          Set<String> set = section.singleLayerKeySet();
          if (Utils.isOneOf(set, CLICK_TYPE)) {
            set.forEach(clicktype -> setRequirement(logger, icon,
                IconNodes.CLICK_REQUIREMENT + "." + clicktype + ".converted",
                from.getSection(section.getPathPrefix() + "." + key + "." + clicktype)));
          } else {
            setRequirement(logger, icon, IconNodes.CLICK_REQUIREMENT + ".default.converted",
                from.getSection(section.getPathPrefix() + "." + key));
          }
          break;
        case IconNodes.VIEW_REQUIREMENT:
          setRequirement(logger, icon, IconNodes.VIEW_REQUIREMENT + ".converted",
              from.getSection(section.getPathPrefix() + "." + key));
          break;
        case IconNodes.AMOUNT:
          icon.setAmount(String.valueOf(section.get(key)));
          break;
        case IconNodes.NAME:
          icon.setDisplayName(section.getString(key));
          break;
        case IconNodes.LORE:
          icon.setLore(section.getStringList(key));
          break;
        case IconNodes.NBT_DATA:
          icon.addProperty("nbt-data", section.getString(key));
          break;
        case IconNodes.COOLDOWN:
          icon.addProperty(IconNodes.CLICK_REQUIREMENT + ".default.converted.cooldown.value",
              section.get(key));
          break;
        case IconNodes.PERMISSION_MESSAGE:
          icon.addProperty(
              IconNodes.CLICK_REQUIREMENT + ".default.converted.permission.fail-command",
              "tell: " + section.get(key));
          break;
        case IconNodes.CLICK_REQUIREMENT_MESSAGE:
          icon.addProperty(IconNodes.CLICK_REQUIREMENT + ".default.converted.fail-command",
              "tell: " + section.get(key));
          break;
        case IconNodes.COOLDOWN_MESSAGE:
          icon.addProperty(IconNodes.CLICK_REQUIREMENT + ".default.converted.cooldown.fail-command",
              "tell: " + section.get(key));
          break;
        default:
          logger.warning(() -> "Ignored Section: " + section.getPathPrefix() + "." + key);
          break;
      }
    }
    return icon;
  }

  public void convert(Logger logger, FlatFile from, FlatFile to) {
    Settings settings = null;
    List<Icon> icons = new ArrayList<>();

    for (String key : from.singleLayerKeySet()) {
      FlatFileSection section = from.getSection(key);
      if (key.equalsIgnoreCase("menu-settings")) {
        settings = getSettings(logger, section);
      } else {
        icons.add(getIcon(logger, key, section, from));
      }
    }

    if (settings != null) {
      settings.getPaths().forEach(to::set);
    }
    icons.forEach(icon -> icon.getPaths().forEach(to::set));
  }

  private void setRequirement(Logger logger, Icon icon, String path, FlatFileSection section) {
    Map<String, Object> requirement = convertRequirement(logger, section);
    if (!requirement.isEmpty()) {
      requirement.forEach((k, v) -> icon.addProperty(path + "." + k, v));
    }
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> convertRequirement(Logger logger, FlatFileSection section) {
    Map<String, Object> map = new HashMap<>();
    section.singleLayerKeySet().forEach(requirement -> {
      Map<String, Object> options = new HashMap<>();
      Object object = section.get(requirement);
      if (object instanceof Map) {
        Map<String, Object> fromOptions = (Map<String, Object>) object;
        if (fromOptions.containsKey(RequirementSettings.VALUE)) {
          setRequirementValue(logger, requirement, fromOptions.get(RequirementSettings.VALUE),
              options);
        }
        if (fromOptions.containsKey(RequirementSettings.TAKE)) {
          options.put(RequirementSettings.TAKE, fromOptions.get(RequirementSettings.TAKE));
        }
        if (fromOptions.containsKey(RequirementSettings.MESSAGE)) {
          options.put("fail-command", "tell: " + fromOptions.get(RequirementSettings.MESSAGE));
        }
      } else {
        setRequirementValue(logger, requirement, object, options);
      }
      if (options.containsKey(RequirementSettings.VALUE)) {
        map.put(requirement, options);
      }
    });
    return map;
  }

  private void setRequirementValue(Logger logger, String name, Object value,
      Map<String, Object> map) {
    if (!REQUIREMENTS.contains(name) || name.equalsIgnoreCase("ITEM")) {
      logger.warning(() -> "Ignored Requirement: " + name);
      return;
    }

    if (name.equalsIgnoreCase("PERMISSION")) {
      map.put(RequirementSettings.VALUE, Utils.createStringListFromObject(value, true, ";"));
    } else {
      map.put(RequirementSettings.VALUE, value);
    }
  }

  private static class RequirementSettings {

    static final String VALUE = "VALUE";
    static final String TAKE = "TAKE";
    static final String MESSAGE = "MESSAGE";
  }

  private static class MenuSettings {

    static final String MENU_COMMAND = "command";
    static final String OPEN_ACTION = "open-action";
    static final String CLOSE_ACTION = "close-action";
    static final String ROWS = "rows";
    static final String AUTO_REFRESH = "auto-refresh";
    static final String INVENTORY_TYPE = "inventory-type";
    static final String NAME = "name";
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
    static final String AMOUNT = "AMOUNT";
    static final String NAME = "NAME";
    static final String LORE = "LORE";
    static final String NBT_DATA = "NBT-DATA";

    // IGNORED
//    static final String COLOR = "COLOR";
//    static final String BANNER_COLOR = "BANNER-COLOUR";
//    static final String BANNER_PATTERNS = "BANNER-PATTERNS";
//    static final String FIREWORK = "FIREWORK";
//    static final String REQUIRED_ITEM = "REQUIRED-ITEM";

    static final String PRICE = "PRICE";
    static final String POINTS = "POINTS";
    static final String TOKENS = "TOKENS";
    static final String EXP_LEVELS = "LEVELS";
    static final String PERMISSION = "PERMISSION";
    static final String PERMISSION_MESSAGE = "PERMISSION-MESSAGE";
    static final String VIEW_PERMISSION = "VIEW-PERMISSION";
    static final String CLICK_REQUIREMENT_MESSAGE = "CLICK-REQUIREMENT-MESSAGE";
  }
}
