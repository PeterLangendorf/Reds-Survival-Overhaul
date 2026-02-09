package net.redfox.hardcorereimagined.environment;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.Difficulty;
import net.redfox.hardcorereimagined.util.config.json.JsonConfigReader;

public class ChickenNerf {
  private static final Map<Difficulty, Double> DIFFICULTY_MULTIPLIERS = new HashMap<>();
  private static final JsonArray JSON_DIFFICULTY_MULTIPLIERS = JsonConfigReader.getOrCreateJsonFile("environment/chickens", JsonConfigReader.CHICKEN_CONFIG).get("values").getAsJsonArray();
  private static double cooldown;

  public static void init() {
    cooldown = JSON_DIFFICULTY_MULTIPLIERS.get(0).getAsJsonObject().get("egg_cooldown").getAsDouble();
    for (JsonElement element : JSON_DIFFICULTY_MULTIPLIERS.get(1).getAsJsonArray()) {
      Difficulty difficulty = Difficulty.byName(element.getAsJsonObject().get("difficulty").getAsString());
      if (difficulty == null) continue;
      DIFFICULTY_MULTIPLIERS.put(difficulty, element.getAsJsonObject().get("multiplier").getAsDouble());
    }
  }

  public static int getCooldown(Difficulty difficulty) {
    return (int)( cooldown * DIFFICULTY_MULTIPLIERS.getOrDefault(difficulty, 1.0));
  }
}