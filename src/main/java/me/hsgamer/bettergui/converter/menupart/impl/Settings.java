package me.hsgamer.bettergui.converter.menupart.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import me.hsgamer.bettergui.converter.menupart.MenuPart;

public class Settings implements MenuPart {

  private final Map<String, Object> otherProperties = new HashMap<>();
  private String name;
  private String inventoryType;
  private Integer rows;
  private String permission;
  private List<String> command;
  private List<String> openAction;
  private List<String> closeAction;
  private Integer autoRefresh;

  @Override
  public Map<String, Object> getPaths() {
    Map<String, Object> path = new LinkedHashMap<>();
    if (name != null) {
      path.put("menu-settings.name", name);
    }
    if (inventoryType != null) {
      path.put("menu-settings.inventory-type", inventoryType);
    }
    if (rows != null) {
      path.put("menu-settings.rows", rows);
    }
    if (permission != null) {
      path.put("menu-settings.permission", permission);
    }
    if (command != null) {
      path.put("menu-settings.command", command);
    }
    if (openAction != null) {
      path.put("menu-settings.open-action", openAction);
    }
    if (closeAction != null) {
      path.put("menu-settings.close-action", closeAction);
    }
    if (autoRefresh != null) {
      path.put("menu-settings.auto-refresh", autoRefresh);
    }
    if (!otherProperties.isEmpty()) {
      otherProperties.forEach((k, v) -> path.put("menu-settings." + k, v));
    }
    return path;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getInventoryType() {
    return inventoryType;
  }

  public void setInventoryType(String inventoryType) {
    this.inventoryType = inventoryType;
  }

  public Integer getRows() {
    return rows;
  }

  public void setRows(int rows) {
    this.rows = rows;
  }

  public String getPermission() {
    return permission;
  }

  public void setPermission(String permission) {
    this.permission = permission;
  }

  public List<String> getCommand() {
    return command;
  }

  public void setCommand(List<String> command) {
    this.command = command;
  }

  public List<String> getOpenAction() {
    return openAction;
  }

  public void setOpenAction(List<String> openAction) {
    this.openAction = openAction;
  }

  public List<String> getCloseAction() {
    return closeAction;
  }

  public void setCloseAction(List<String> closeAction) {
    this.closeAction = closeAction;
  }

  public Integer getAutoRefresh() {
    return autoRefresh;
  }

  public void setAutoRefresh(int autoRefresh) {
    this.autoRefresh = autoRefresh;
  }

  public Map<String, Object> getOtherProperties() {
    return otherProperties;
  }

  public void addProperty(String property, Object value) {
    otherProperties.put(property, value);
  }
}
