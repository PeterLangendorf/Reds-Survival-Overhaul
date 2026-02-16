package net.redfox.survivaloverhaul.weight;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.redfox.survivaloverhaul.util.config.JsonConfigReader;
import net.redfox.survivaloverhaul.util.config.StringParsingUtil;

import java.util.*;


public class Weight {
  private static final Map<Item, Float> WEIGHT_MAP = new IdentityHashMap<>();
  private static final JsonArray JSON_WEIGHTS = JsonConfigReader.getOrCreateJsonFile("weight/weights", JsonConfigReader.WEIGHTS).get("values").getAsJsonArray();

  private static final UUID MODIFIER_UUID = Mth.createInsecureUUID();

  public static float getTotalWeight(Player player) {
    float weight = 0;
    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
      ItemStack stack = player.getInventory().getItem(i);
        Float add = WEIGHT_MAP.getOrDefault(stack.getItem(), null);
        if (add != null) weight += add * stack.getCount();
        else {
          Optional<JsonElement> element = JSON_WEIGHTS.asList().stream().filter(str -> StringParsingUtil.matchesStringOrTag(str.getAsJsonObject().get("item").getAsString(), stack.getItem())).findFirst();
          if (element.isPresent()) {
            double value = element.get().getAsJsonObject().get("weight").getAsDouble();
            WEIGHT_MAP.put(stack.getItem(), (float) value);
            weight += (float) value * stack.getCount();
        }
          else  {
            WEIGHT_MAP.put(stack.getItem(), 0.01f);
          }
      }
    }
    return weight;
  }

  public static void applyWeightModifier(Player player) {
    double percentage = -(Math.min(getTotalWeight(player) / 100d, 1));
    AttributeInstance instance = player.getAttributes().getInstance(Attributes.MOVEMENT_SPEED);
    AttributeModifier modifier = instance.getModifier(MODIFIER_UUID);
    if (modifier != null) {
      if (!(Math.abs(percentage - modifier.getAmount()) < 0.0001d)) {
        instance.removeModifier(MODIFIER_UUID);
        instance.addTransientModifier(new AttributeModifier(MODIFIER_UUID, "weight", percentage*0.7, AttributeModifier.Operation.MULTIPLY_TOTAL));
      }
    } else {
      instance.addTransientModifier(new AttributeModifier(MODIFIER_UUID, "weight", percentage*0.7, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }
  }
}