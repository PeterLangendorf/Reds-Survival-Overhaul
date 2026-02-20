package net.redfox.survivaloverhaul.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;

public class ModCommonConfigs {
  private static final ArrayList<String> DEFAULT_DIFFICULTY_MULTIPLIER =
      new ArrayList<>(Arrays.asList("peaceful:1", "easy:2", "normal:4", "hard:8"));

  public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
  public static final ForgeConfigSpec SPEC;

  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BABY_GROWTH_MODIFIERS;
  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BONE_MEAL_UPPER_LIMIT;
  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BONE_MEAL_LOWER_LIMIT;
  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BREEDING_COOLDOWN_MULTIPLIERS;
  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> EGG_TIME_MODIFIERS;
  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> CROP_GROWTH_DIFFICULTY_MULTIPLIERS;
  public static final ForgeConfigSpec.ConfigValue<Boolean> GLOBAL_HUNGER_MULTIPLIER_ENABLED;
  public static final ForgeConfigSpec.ConfigValue<List<? extends String>> GLOBAL_HUNGER_MULTIPLIERS;
  public static final ForgeConfigSpec.ConfigValue<Boolean> PASSIVE_EXHAUSTION_ENABLED;
  public static final ForgeConfigSpec.ConfigValue<Double> PASSIVE_EXHAUSTION_RATE;
  public static final ForgeConfigSpec.ConfigValue<Double> SPAWN_HEALTH_MULTIPLIER;
  public static final ForgeConfigSpec.ConfigValue<Double> SPAWN_HUNGER_MULTIPLIER;
  public static final ForgeConfigSpec.ConfigValue<Integer> EGG_COOLDOWN;


  public static final ForgeConfigSpec.ConfigValue<Boolean> TEMPERATURE_FLUCTUATION;

  public static final ForgeConfigSpec.ConfigValue<Boolean> BIOME_TEMPERATURE_ENABLED;
  public static final ForgeConfigSpec.ConfigValue<Boolean> BLOCK_INSULATOR_TEMPERATURE_ENABLED;
  public static final ForgeConfigSpec.ConfigValue<Boolean> ON_TOP_BLOCK_TEMPERATURE_ENABLED;
  public static final ForgeConfigSpec.ConfigValue<Boolean> INSIDE_BLOCK_TEMPERATURE_ENABLED;
  public static final ForgeConfigSpec.ConfigValue<Boolean> ARMOR_INSULATOR_TEMPERATURE_ENABLED;

  public static final ForgeConfigSpec.ConfigValue<Boolean> WEATHER_TEMPERATURE_ENABLED;
  public static final ForgeConfigSpec.ConfigValue<Integer> RAIN_TEMPERATURE;
  public static final ForgeConfigSpec.ConfigValue<Integer> SNOW_TEMPERATURE;

  public static final ForgeConfigSpec.ConfigValue<Boolean> TIME_TEMPERATURE_ENABLED;
  public static final ForgeConfigSpec.ConfigValue<Integer> DAY_TEMPERATURE;
  public static final ForgeConfigSpec.ConfigValue<Integer> NIGHT_TEMPERATURE;

  public static final ForgeConfigSpec.ConfigValue<Boolean> FIRE_TEMPERATURE_ENABLED;
  public static final ForgeConfigSpec.ConfigValue<Integer> FIRE_TEMPERATURE;

  public static final ForgeConfigSpec.ConfigValue<Boolean> ALTITUDE_TEMPERATURE_ENABLED;
  public static final ForgeConfigSpec.ConfigValue<Integer> UPPER_ALTITUDE;
  public static final ForgeConfigSpec.ConfigValue<Integer> LOWER_ALTITUDE;
  public static final ForgeConfigSpec.ConfigValue<Double> UPPER_MULTIPLIER;
  public static final ForgeConfigSpec.ConfigValue<Double> LOWER_MULTIPLIER;

  public static final ForgeConfigSpec.ConfigValue<Boolean> FOOD_MODIFICATION_ENABLED;

  public static final ForgeConfigSpec.ConfigValue<Boolean> FOOD_HISTORY_ENABLED;
  public static final ForgeConfigSpec.ConfigValue<List<Double>> NUTRITION_DECAY;
  public static final ForgeConfigSpec.ConfigValue<Integer> MAX_HISTORY;



  static {
    BUILDER.push("common");
    {
      BUILDER.push("temperature");
      {
        TEMPERATURE_FLUCTUATION =
            BUILDER
                .comment("If true, enables a small constant temperature fluctuation.")
                .define("temperature_fluctuation", true);

        BUILDER.push("biomes");
        {
          BIOME_TEMPERATURE_ENABLED =
              BUILDER
                  .comment("If true, enables temperature fluctuation based on biomes.")
                  .comment("Biomes can be modified in the JSON file at /survivaloverhaul/temperature/biomes.json")
                  .define("biome_temperature_enabled", true);
        }
        BUILDER.pop();

        BUILDER.push("insulators");
        {
          BLOCK_INSULATOR_TEMPERATURE_ENABLED =
              BUILDER
                  .comment("If true, enables temperature fluctuation based on block insulators.")
                  .comment("Block insulators can be modified in the JSON file at /survivaloverhaul/temperature/insulators.json")
                  .define("block_insulator_temperature_enabled", true);
        }
        BUILDER.pop();

        BUILDER.push("inside_blocks");
        {
          INSIDE_BLOCK_TEMPERATURE_ENABLED =
              BUILDER
                  .comment("If true, enables temperature fluctuation based on inside blocks.")
                  .comment("Inside blocks can be modified in the JSON file at /survivaloverhaul/temperature/inside_blocks.json")
                  .define("inside_block_temperature_enabled", true);
        }
        BUILDER.pop();

        BUILDER.push("on_top_blocks");
        {
          ON_TOP_BLOCK_TEMPERATURE_ENABLED =
              BUILDER
                  .comment("If true, enables temperature fluctuation based on on-top blocks.")
                  .comment("On-top blocks can be modified in the JSON file at /survivaloverhaul/temperature/on_top_blocks.json")
                  .define("on_top_block_temperature_enabled", true);
        }
        BUILDER.pop();

        BUILDER.push("weather");
        {
          WEATHER_TEMPERATURE_ENABLED =
              BUILDER
                  .comment("If true, enables temperature fluctuation based on weather.")
                  .define("weather_temperature_enabled", true);

          RAIN_TEMPERATURE =
              BUILDER
                  .comment("The temperature that is applied when exposed to rain")
                  .define("rain_temperature", -30);

          SNOW_TEMPERATURE =
              BUILDER
                  .comment("The temperature that is applied when exposed to snow")
                  .define("snow_temperature", -60);
        }
        BUILDER.pop();

        BUILDER.push("time");
        {
          TIME_TEMPERATURE_ENABLED =
              BUILDER
                  .comment("If true, enables temperature fluctuation based on time.")
                  .define("time_temperature_enabled", true);

          DAY_TEMPERATURE =
              BUILDER
                  .comment("The temperature that is applied when it is daytime")
                  .define("day_temperature", 10);

          NIGHT_TEMPERATURE =
              BUILDER
                  .comment("The temperature that is applied when it is nighttime")
                  .define("night_temperature", -10);
        }
        BUILDER.pop();

        BUILDER.push("fire");
        {
          FIRE_TEMPERATURE_ENABLED =
              BUILDER
                  .comment("If true, enables temperature fluctuation based on fire.")
                  .define("fire_temperature_enabled", true);

          FIRE_TEMPERATURE =
              BUILDER
                  .comment("The temperature that is applied when the player is on fire")
                  .defineInRange("fire_temperature", 50, 0, Integer.MAX_VALUE);
        }
        BUILDER.pop();

        BUILDER.push("altitude");
        {
          ALTITUDE_TEMPERATURE_ENABLED =
              BUILDER
                  .comment("If true, enables temperature fluctuation based on altitude.")
                  .define("altitude_temperature_enabled", true);

          UPPER_ALTITUDE =
              BUILDER
                  .comment("The upwards y-value when temperature will start changing")
                  .define("upper_altitude", 80);

          UPPER_MULTIPLIER =
              BUILDER
                  .comment("The amount of blocks that result in a change of 1 temperature")
                  .comment(
                      "For example, a value of 5 means that every 5 blocks traveled after the start will change your temperature by 1")
                  .defineInRange("upper_multiplier", 5, 1, 128d);

          LOWER_ALTITUDE =
              BUILDER
                  .comment("The downwards y-value when temperature will start changing")
                  .define("lower_altitude", 40);

          LOWER_MULTIPLIER =
              BUILDER
                  .comment("The amount of blocks that result in a change of 1 temperature")
                  .comment(
                      "For example, a value of 5 means that every 5 blocks traveled after the start will change your temperature by 1")
                  .defineInRange("lower_multiplier", 5, 1, 128d);
        }
        BUILDER.pop();

        BUILDER.push("armor_insulators");
        {
          ARMOR_INSULATOR_TEMPERATURE_ENABLED =
              BUILDER
                  .comment("If true, enables temperature fluctuation based on armor insulators.")
                  .comment("Armor insulators can be modified in the JSON file at /survivaloverhaul/temperature/armor_insulators.json")
                  .define("armor_insulator_temperature_enabled", true);
        }
        BUILDER.pop();
      }
      BUILDER.pop();
      BUILDER.push("environment");
      {
        CROP_GROWTH_DIFFICULTY_MULTIPLIERS =
            BUILDER
                .comment("The modifier for the growth time of crops depending on difficulty.")
                .define("cropGrowthDifficultyMultiplier", DEFAULT_DIFFICULTY_MULTIPLIER);
        BUILDER.push("bone_meal");
        {
          BONE_MEAL_UPPER_LIMIT = BUILDER.comment("The upper limit for the amount of ticks a piece of bone meal can accelerate a plant growth, depending on difficulty.")
              .defineList("boneMealUpperLimit", new ArrayList<>(Arrays.asList("peaceful:5", "easy:4", "normal:2", "hard:0")), obj -> obj instanceof String);
          BONE_MEAL_LOWER_LIMIT = BUILDER.comment("The lower limit for the amount of ticks a piece of bone meal can accelerate a plant growth, depending on difficulty.")
              .defineList("boneMealLowerLimit", new ArrayList<>(Arrays.asList("peaceful:2", "easy:1", "normal:0", "hard:0")), obj -> obj instanceof String);
        }
        BUILDER.pop();
        BABY_GROWTH_MODIFIERS =
            BUILDER
                .comment("The modifier for the growth time of baby animals.")
                .defineList("babyGrowthMultiplier", DEFAULT_DIFFICULTY_MULTIPLIER, obj -> obj instanceof String);
        BREEDING_COOLDOWN_MULTIPLIERS =
            BUILDER
                .comment("The modifier for the breeding cooldown of animals.")
                .defineList("breedingCooldownMultiplier", DEFAULT_DIFFICULTY_MULTIPLIER, obj -> obj instanceof String);
        EGG_COOLDOWN =
            BUILDER.comment("The base cooldown for eggs being laid").define("eggCooldown", 6000);
        EGG_TIME_MODIFIERS =
            BUILDER
                .comment("The modifier for the egg laying cooldown of chickens.")
                .defineList("eggCooldownMultiplier", DEFAULT_DIFFICULTY_MULTIPLIER, obj -> obj instanceof String);
      }
      BUILDER.pop();
      BUILDER.push("food");
      {
        BUILDER.push("global");
        {
          BUILDER.comment("Passive exhaustion is exhaustion that is applied constantly to the player, no matter what they're doing.");
          PASSIVE_EXHAUSTION_ENABLED = BUILDER.comment("If true, enables passive exhaustion").define("passiveExhaustionEnabled", true);
          PASSIVE_EXHAUSTION_RATE =
              BUILDER
                  .comment("The rate, in hunger per minute, that exhaustion that is applied to the player.")
                  .comment("For example, a value of 1 means that every minute, the player will have lost 1 hunger.")
                  .define("passiveExhaustion", 1d);
          BUILDER.comment("The global hunger multiplier is a multiplier, scaled by difficulty, that will be applied to every single action that decreases the player's hunger,");
          GLOBAL_HUNGER_MULTIPLIER_ENABLED = BUILDER.comment("If true, enables the global hunger multiplier.").define("globalHungerMultiplierEnabled", true);
          GLOBAL_HUNGER_MULTIPLIERS =
              BUILDER
                  .comment(
                      "The multiplier, based on difficulty, that will be applied.")
                  .defineList(
                      "globalHungerMultiplier",
                      new ArrayList<>(Arrays.asList("peaceful:1", "easy:1.5", "normal:2", "hard:3")), obj -> obj instanceof String);

        }
        BUILDER.pop();
        BUILDER.push("overrides");
        {
          FOOD_MODIFICATION_ENABLED =
              BUILDER
                  .comment("If true, enables the modification of specified foods.")
                  .comment("Food overrides can be modified in the JSON file at /survivaloverhaul/food/food_overrides.json")
                  .define("foodModificationEnabled", true);
        }
        BUILDER.pop();
        BUILDER.push("foodHistory");
        {
          FOOD_HISTORY_ENABLED =
              BUILDER
                  .comment(
                      "If true, enables progressive decay from eating the same food repeatedly.")
                  .define("foodHistoryEnabled", true);

          NUTRITION_DECAY =
              BUILDER
                  .comment(
                      "The sequence for the decay on the nutrition values of food. Note that each value is a flat value, so they are not applied on top of each other.",
                      "1.0 means 100% of the nutritional value, while 0.0 means 0%.")
                  .define(
                      "nutritionalDecay",
                      new ArrayList<>(Arrays.asList(1.0, 1.0, 1.0, 0.75, 0.5, 0.25, 0.0)));

          MAX_HISTORY =
              BUILDER
                  .comment("The maximum food history that is stored.")
                  .defineInRange("Max History", 30, 3, 100);
        }
        BUILDER.pop();
      }
      BUILDER.pop();
      BUILDER.push("health");
      {
        SPAWN_HEALTH_MULTIPLIER =
            BUILDER
                .comment("The multiplier applied to health upon respawn")
                .define("spawnHealthMultiplier", 0.5);
        SPAWN_HUNGER_MULTIPLIER =
            BUILDER
                .comment("The multiplier applied to hunger upon respawn")
                .define("spawnHungerMultiplier", 0.5);
      }
      BUILDER.pop();
      BUILDER.pop();
      SPEC = BUILDER.build();
    }
  }
}
