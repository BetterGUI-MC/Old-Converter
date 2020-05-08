package me.hsgamer.bettergui.converter.converter.impl;

import de.leonhard.storage.internal.FlatFile;
import de.leonhard.storage.sections.FlatFileSection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import me.hsgamer.bettergui.converter.Utils;
import me.hsgamer.bettergui.converter.converter.Converter;
import me.hsgamer.bettergui.converter.menupart.impl.Icon;
import me.hsgamer.bettergui.converter.menupart.impl.Settings;

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

  private static Icon getIcon(Logger logger, String name, FlatFileSection section) {
    Icon icon = new Icon(name);

    section.singleLayerKeySet().forEach(s -> {
      switch (s) {
        case IconSettings.MATERIAL:
          icon.setId(String.valueOf(section.get(s)));
          break;
        case IconSettings.DATA:
          icon.setDamage(String.valueOf(section.get(s)));
          break;
        case IconSettings.AMOUNT:
        case IconSettings.AMOUNT_DYNAMIC:
          icon.setAmount(String.valueOf(section.get(s)));
          break;
        case IconSettings.NAME:
          icon.setDisplayName(String.valueOf(section.get(s)));
          break;
        case IconSettings.LORE:
          icon.setLore(section.getStringList(s));
          break;
        case IconSettings.SLOT:
          icon.setSlot(String.valueOf(section.get(s)));
          break;
        case IconSettings.SLOTS:
          List<String> strings = new ArrayList<>();
          section.getIntegerList(s).forEach(slot -> strings.add(String.valueOf(slot)));
          icon.setSlot(String.join(", ", strings));
          break;
        case IconSettings.ENCHANTMENT:
          icon.setEnchant(Utils.createStringListFromObject(section.get(s), true, ""));
          break;
        case IconSettings.HIDE_ENCHANTS:
          icon.addFlags("HIDE_ENCHANTS");
          break;
        case IconSettings.HIDE_EFFECTS:
          icon.addFlags("HIDE_POTION_EFFECTS");
          break;
        case IconSettings.HIDE_ATTRIBUTES:
          icon.addFlags("HIDE_ATTRIBUTES");
          break;
        case IconSettings.LEFT_COMMANDS:
          icon.addProperty("command.left", convertActions(section.getStringList(s)));
          break;
        case IconSettings.SHIFT_LEFT_COMMANDS:
          icon.addProperty("command.shift_left", convertActions(section.getStringList(s)));
          break;
        case IconSettings.RIGHT_COMMANDS:
          icon.addProperty("command.right", convertActions(section.getStringList(s)));
          break;
        case IconSettings.SHIFT_RIGHT_COMMANDS:
          icon.addProperty("command.shift_right", convertActions(section.getStringList(s)));
          break;
        case IconSettings.MIDDLE_COMMANDS:
          icon.addProperty("command.middle", convertActions(section.getStringList(s)));
          break;
        default:
          logger.info(() -> "Ignored icon setting " + name + "." + s);
          break;
      }
    });

    return icon;
  }

  @Override
  public void convert(Logger logger, FlatFile from, FlatFile to) {
    Settings settings = new Settings();
    List<Icon> icons = new ArrayList<>();
    for (String setting : from.singleLayerKeySet()) {
      switch (setting) {
        case MenuSettings.TITLE:
          settings.setName(from.getString(setting));
          break;
        case MenuSettings.OPEN_COMMAND:
          settings.setCommand(Utils.createStringListFromObject(from.get(setting), true, ""));
          break;
        case MenuSettings.OPEN_ACTION:
          settings.setOpenAction(convertActions(from.getStringList(setting)));
          break;
        case MenuSettings.CLOSE_ACTION:
          settings.setCloseAction(convertActions(from.getStringList(setting)));
          break;
        case MenuSettings.INVENTORY_TYPE:
          settings.setInventoryType(from.getString(setting));
          break;
        case MenuSettings.SIZE:
          settings.setRows(from.getInt(setting) / 9);
          break;
        case MenuSettings.UPDATE:
          settings.setAutoRefresh(from.getInt(setting) * 20);
          break;
        case MenuSettings.ITEMS:
          from.singleLayerKeySet(setting)
              .forEach(s -> icons.add(getIcon(logger, s, from.getSection(setting + "." + s))));
          break;
        default:
          logger.info(() -> "Ignored " + setting);
          break;
      }
    }
    settings.getPaths().forEach(to::set);
    icons.forEach(icon -> icon.getPaths().forEach(to::set));
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
