package net.redfox.survivaloverhaul.util.config;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.Difficulty;
import net.redfox.survivaloverhaul.config.ModCommonConfigs;

public class ConfigUtil {
  private static final Map<Difficulty, Double> EGG_MULTIPLIERS = new HashMap<>();
  private static final Map<Difficulty, Double> GLOBAL_HUNGER_MULTIPLIERS = new HashMap<>();
  private static final Map<Difficulty, Double> BREEDING_MULTIPLIERS = new HashMap<>();
  private static final Map<Difficulty, Double> BABY_AGE_MULTIPLIERS = new HashMap<>();
  private static final Map<Difficulty, Integer> BONEMEAL_LOWER_MULTIPLIERS = new HashMap<>();
  private static final Map<Difficulty, Integer> BONEMEAL_UPPER_MULTIPLIERS = new HashMap<>();
  private static double egg_cooldown;
  private static float passive_hunger;

  public static void init() {
    egg_cooldown = ModCommonConfigs.EGG_COOLDOWN.get();
    passive_hunger = ModCommonConfigs.PASSIVE_EXHAUSTION.get().floatValue() / 300f; //Convert to exhaustion per tick
    StringParsingUtil.fillMap(EGG_MULTIPLIERS, ModCommonConfigs.EGG_TIME_MODIFIERS.get(), Difficulty::byName, Double::parseDouble);
    StringParsingUtil.fillMap(GLOBAL_HUNGER_MULTIPLIERS, ModCommonConfigs.GLOBAL_HUNGER_MULTIPLIERS.get(), Difficulty::byName, Double::parseDouble);
    StringParsingUtil.fillMap(BREEDING_MULTIPLIERS, ModCommonConfigs.BREEDING_COOLDOWN_MULTIPLIERS.get(), Difficulty::byName, Double::parseDouble);
    StringParsingUtil.fillMap(BABY_AGE_MULTIPLIERS, ModCommonConfigs.BABY_GROWTH_MODIFIERS.get(), Difficulty::byName, Double::parseDouble);
    StringParsingUtil.fillMap(BONEMEAL_LOWER_MULTIPLIERS, ModCommonConfigs.BONE_MEAL_LOWER_LIMIT.get(), Difficulty::byName, Integer::parseInt);
    StringParsingUtil.fillMap(BONEMEAL_UPPER_MULTIPLIERS, ModCommonConfigs.BONE_MEAL_UPPER_LIMIT.get(), Difficulty::byName, Integer::parseInt);
  }

  public static int getEggCooldown(Difficulty difficulty) {
    return (int) (egg_cooldown * EGG_MULTIPLIERS.getOrDefault(difficulty, 1.0));
  }

  public static float getGlobalHungerMultiplier(Difficulty difficulty) {
    return GLOBAL_HUNGER_MULTIPLIERS.getOrDefault(difficulty, 1.0).floatValue();
  }

  public static int getBreedingCooldown(Difficulty difficulty) {
    return (int) (6000 * BREEDING_MULTIPLIERS.getOrDefault(difficulty, 1.0));
  }

  public static int getBabyAge(Difficulty difficulty) {
    return (int) (24000 * BABY_AGE_MULTIPLIERS.getOrDefault(difficulty, 1d));
  }

  public static int getBonemealLowerBound(Difficulty difficulty) {
    return BONEMEAL_LOWER_MULTIPLIERS.getOrDefault(difficulty, 2);
  }

  public static int getBonemealUpperBound(Difficulty difficulty) {
    return BONEMEAL_UPPER_MULTIPLIERS.getOrDefault(difficulty, 5);
  }

  public static float getPassiveHungerLoss() {
    return passive_hunger;
  }
}
