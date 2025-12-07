package net.redfox.hardcorereimagined.food;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.redfox.hardcorereimagined.HardcoreReimagined;
import net.redfox.hardcorereimagined.config.FormattedConfigValues;

public class FoodNerf {
  public static final Map<Item, String> TOOLTIPS = new HashMap<>();

  public static void nerfFoods() {
    for (Item item : FormattedConfigValues.FoodNerf.FOOD_MODIFICATIONS.keySet()) {
      if (item.isEdible()) {
        FoodCategory category = FormattedConfigValues.FoodNerf.FOOD_MODIFICATIONS.get(item);
        TOOLTIPS.put(item, formatTooltip(category.name()));
        modifyFoodProps(
            item,
            new FoodProperties.Builder()
                .nutrition(category.hunger())
                .saturationMod((float) (category.saturationMultiplier() / 2f))
                .build());
        modifyMaxStackSize(item, category.maxStackSize());
      } else {
        HardcoreReimagined.LOGGER.error("{} is not an edible item and was therefore skipped", item);
      }
    }
  }

  private static void modifyFoodProps(Item item, FoodProperties props) {
    try {
      Field foodfield = ObfuscationReflectionHelper.findField(Item.class, "f_41380_");
      foodfield.setAccessible(true);
      foodfield.set(item, props);
    } catch (Exception e) {
      e.printStackTrace();
      HardcoreReimagined.LOGGER.error(e.getMessage());
    }
  }

  private static void modifyMaxStackSize(Item item, int maxStackSize) {
    try {
      Field maxStackSizeField = ObfuscationReflectionHelper.findField(Item.class, "f_41370_");
      maxStackSizeField.setAccessible(true);
      maxStackSizeField.set(item, maxStackSize);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static String formatTooltip(String input) {
    String lower = input.toLowerCase();
    String[] split = lower.split("_");
    StringBuilder output = new StringBuilder();
    for (String s : split) {
      output.append(s.substring(0, 1).toUpperCase()).append(s.substring(1)).append(" ");
    }
    return output.toString().trim();
  }

  public static void addTooltip(ItemTooltipEvent event) {
    if (!event.getItemStack().isEdible()) return;
    if (event.getEntity() == null) return;
    if (!TOOLTIPS.containsKey(event.getItemStack().getItem())) return;

    event
        .getToolTip()
        .add(
            1,
            Component.literal(TOOLTIPS.get(event.getItemStack().getItem()))
                .withStyle(ChatFormatting.GRAY));
    event.getToolTip().add(2, Component.empty());
  }
}
