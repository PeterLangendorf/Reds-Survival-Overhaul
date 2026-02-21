package net.redfox.survivaloverhaul.food;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.redfox.survivaloverhaul.util.ItemModifier;
import net.redfox.survivaloverhaul.config.JsonConfigReader;

public class FoodNerf {
  private static final JsonArray JSON_FOOD_CATEGORIES = JsonConfigReader.getOrCreateJsonFile("food/food_types", JsonConfigReader.FOOD_TYPES).get("values").getAsJsonArray();
  private static final JsonArray JSON_FOOD_OVERRIDES = JsonConfigReader.getOrCreateJsonFile("food/food_overrides", JsonConfigReader.FOOD_OVERRIDES).get("values").getAsJsonArray();
  private static final Map<Item, String> TOOLTIPS = new HashMap<>();


  public static void init() {
    JSON_FOOD_OVERRIDES.forEach(strItem -> {
      Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(strItem.getAsJsonObject().get("food").getAsString()));
      JsonObject category = JSON_FOOD_CATEGORIES.asList().stream().filter(
          element -> element.getAsJsonObject().get("name").getAsString().equals(strItem.getAsJsonObject().get("food_type").getAsString())).findFirst().get().getAsJsonObject();;
      ItemModifier.modifyItem(ItemModifier.Modification.STACK_SIZE, item, category.get("stack_size").getAsInt());
      ItemModifier.modifyItem(ItemModifier.Modification.FOODSAT, item, new FoodProperties.Builder().nutrition(category.get("food_value").getAsInt()).saturationMod(category.get("saturation_multiplier").getAsInt()).build());
      TOOLTIPS.put(item, formatTooltip(category.get("name").getAsString()));
    });
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
