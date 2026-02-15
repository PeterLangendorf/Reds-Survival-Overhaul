package net.redfox.survivaloverhaul.util;

import net.minecraft.world.item.Item;
import net.neoforged.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.Map;

public class ItemModifier {
  public enum Modification {
    STACK_SIZE,
    FOODSAT
  }
  private static final Map<Modification, String> PROPERTY_PATHS = Map.of(Modification.STACK_SIZE, "f_41370_", Modification.FOODSAT, "f_41380_");

  public static void modifyItem(Modification path, Item item, Object set) {
    try {
      Field field = ObfuscationReflectionHelper.findField(Item.class, PROPERTY_PATHS.get(path));
      field.setAccessible(true);
      field.set(item, set);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}