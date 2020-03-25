package me.hsgamer.bettergui.converter.impl;

import de.leonhard.storage.internal.FlatFile;
import de.leonhard.storage.sections.FlatFileSection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import me.hsgamer.bettergui.converter.Converter;

public class DeluxeMenus implements Converter {

  private static List<String> convertActions(List<String> actions) {
    actions.replaceAll(DeluxeMenus::convertAction);
    return actions;
  }

  private static String convertAction(String iconCommand) {
    return iconCommand
        .replace("[player]", "player:")
        .replace("[commandevent]", "player:")
        .replace("[console]", "console:")
        .replace("[message]", "tell:")
        .replace("[openguimenu]", "open:")
        .replace("[connect]", "server:")
        .replace("[close]", "close-menu")
        .replace("[json]", "json:")
        .replace("[refresh]", "refresh")
        .replace("[broadcastsound]", "sound:")
        .replace("[sound]", "sound:")
        ;
  }

  private static void setIcon(Logger logger, String name, FlatFileSection section, FlatFile to) {
    to.setPathPrefix(name);
    section.singleLayerKeySet().forEach(s -> {
      List<String> flags = new ArrayList<>();
      Map<String, List<String>> commands = new HashMap<>();

      switch (s) {
        case IconSettings.MATERIAL:
          to.set("id", section.get(s));
          break;
        case IconSettings.DATA:
          to.set("damage", section.get(s));
          break;
        case IconSettings.AMOUNT:
        case IconSettings.AMOUNT_DYNAMIC:
          to.set("amount", section.get(s));
          break;
        case IconSettings.NAME:
          to.set("name", section.get(s));
          break;
        case IconSettings.LORE:
          to.set("lore", section.get(s));
          break;
        case IconSettings.SLOT:
          to.set("slot", section.get(s));
          break;
        case IconSettings.SLOTS:
          List<String> strings = new ArrayList<>();
          section.getIntegerList(s).forEach(slot -> strings.add(String.valueOf(slot)));
          to.set("slot", String.join(", ", strings));
          break;
        case IconSettings.ENCHANTMENT:
          to.set("enchant", section.get(s));
          break;
        case IconSettings.HIDE_ENCHANTS:
          flags.add("HIDE_ENCHANTS");
          break;
        case IconSettings.HIDE_EFFECTS:
          flags.add("HIDE_POTION_EFFECTS");
          break;
        case IconSettings.HIDE_ATTRIBUTES:
          flags.add("HIDE_ATTRIBUTES");
          break;
        case IconSettings.LEFT_COMMANDS:
          commands.put("left", convertActions(section.getStringList(s)));
          break;
        case IconSettings.SHIFT_LEFT_COMMANDS:
          commands.put("shift_left", convertActions(section.getStringList(s)));
          break;
        case IconSettings.RIGHT_COMMANDS:
          commands.put("right", convertActions(section.getStringList(s)));
          break;
        case IconSettings.SHIFT_RIGHT_COMMANDS:
          commands.put("shift_right", convertActions(section.getStringList(s)));
          break;
        case IconSettings.MIDDLE_COMMANDS:
          commands.put("middle", convertActions(section.getStringList(s)));
          break;
        default:
          logger.info(() -> "Ignored icon setting " + name + "." + s);
          break;
      }

      if (!flags.isEmpty()) {
        to.set("flag", flags);
      }
      if (!commands.isEmpty()) {
        to.set("command", commands);
      }
    });
  }

  @Override
  public void convert(Logger logger, FlatFile from, FlatFile to) {
    for (String setting : from.singleLayerKeySet()) {
      switch (setting) {
        case MenuSettings.TITLE:
          to.set("menu-settings.name", from.get(setting));
          break;
        case MenuSettings.OPEN_COMMAND:
          to.set("menu-settings.command", from.get(setting));
          break;
        case MenuSettings.OPEN_ACTION:
          to.set("menu-settings.open-action", convertActions(from.getStringList(setting)));
          break;
        case MenuSettings.CLOSE_ACTION:
          to.set("menu-settings.close-action", convertActions(from.getStringList(setting)));
          break;
        case MenuSettings.INVENTORY_TYPE:
          to.set("menu-settings.inventory-type", from.getString(setting));
          break;
        case MenuSettings.SIZE:
          to.set("menu-settings.row", from.getInt(setting) / 9);
          break;
        case MenuSettings.UPDATE:
          to.set("menu-settings.auto-refresh", from.getInt(setting) * 20);
          break;
        case MenuSettings.ITEMS:
          from.singleLayerKeySet(setting)
              .forEach(s -> setIcon(logger, s, from.getSection(setting + "." + s), to));
          to.setPathPrefix(null);
          break;
        default:
          logger.info(() -> "Ignored " + setting);
          break;
      }
    }
  }

  private static final class MenuSettings {

    static final String TITLE = "menu_title";
    static final String OPEN_COMMAND = "open_command";
    static final String OPEN_ACTION = "open_commands";
    static final String CLOSE_ACTION = "close_commands";
    static final String INVENTORY_TYPE = "inventory_type";
    static final String SIZE = "size";
    static final String ITEMS = "items";
    static final String UPDATE = "update_interval";
  }

  private static final class IconSettings {

    static final String MATERIAL = "material";
    static final String DATA = "data";
    static final String AMOUNT = "amount";
    static final String AMOUNT_DYNAMIC = "dynamic_amount";
    static final String NAME = "display_name";
    static final String LORE = "lore";
    static final String SLOT = "slot";
    static final String SLOTS = "slots";
    static final String ENCHANTMENT = "enchantments";
    static final String HIDE_ENCHANTS = "hide_enchantments";
    static final String HIDE_ATTRIBUTES = "hide_attributes";
    static final String HIDE_EFFECTS = "hide_effects";
    static final String LEFT_COMMANDS = "left_click_commands";
    static final String SHIFT_LEFT_COMMANDS = "shift_left_click_commands";
    static final String RIGHT_COMMANDS = "right_click_commands";
    static final String SHIFT_RIGHT_COMMANDS = "shift_right_click_commands";
    static final String MIDDLE_COMMANDS = "middle_click_commands";
  }
}
