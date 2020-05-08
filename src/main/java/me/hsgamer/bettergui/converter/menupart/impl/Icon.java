package me.hsgamer.bettergui.converter.menupart.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import me.hsgamer.bettergui.converter.menupart.MenuPart;

public class Icon implements MenuPart {

  private final String name;
  private final List<String> flags = new ArrayList<>();
  private final Map<String, Object> otherProperties = new HashMap<>();
  private String slot;
  private String id;
  private String damage;
  private String amount;
  private String displayName;
  private List<String> lore;
  private List<String> enchant;

  public Icon(String name) {
    this.name = name;
  }

  @Override
  public Map<String, Object> getPaths() {
    Map<String, Object> paths = new LinkedHashMap<>();
    if (slot != null) {
      paths.put(name + ".slot", slot);
    }
    if (id != null) {
      paths.put(name + ".id", id);
    }
    if (damage != null) {
      paths.put(name + ".damage", damage);
    }
    if (amount != null) {
      paths.put(name + ".amount", amount);
    }
    if (displayName != null) {
      paths.put(name + ".name", displayName);
    }
    if (lore != null) {
      paths.put(name + ".lore", lore);
    }
    if (enchant != null) {
      paths.put(name + ".enchant", enchant);
    }
    if (!flags.isEmpty()) {
      paths.put(name + ".flag", flags);
    }
    if (!otherProperties.isEmpty()) {
      otherProperties.forEach((k, v) -> paths.put(name + "." + k, v));
    }
    return paths;
  }

  public String getSlot() {
    return slot;
  }

  public void setSlot(String slot) {
    this.slot = slot;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDamage() {
    return damage;
  }

  public void setDamage(String damage) {
    this.damage = damage;
  }

  public String getAmount() {
    return amount;
  }

  public void setAmount(String amount) {
    this.amount = amount;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public List<String> getLore() {
    return lore;
  }

  public void setLore(List<String> lore) {
    this.lore = lore;
  }

  public List<String> getEnchant() {
    return enchant;
  }

  public void setEnchant(List<String> enchant) {
    this.enchant = enchant;
  }

  public List<String> getFlags() {
    return flags;
  }

  public void addFlags(String flag) {
    flags.add(flag);
  }

  public Map<String, Object> getOtherProperties() {
    return otherProperties;
  }

  public void addProperty(String property, Object value) {
    otherProperties.put(property, value);
  }
}
