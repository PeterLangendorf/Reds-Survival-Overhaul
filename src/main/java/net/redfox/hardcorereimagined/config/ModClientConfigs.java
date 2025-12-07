package net.redfox.hardcorereimagined.config;

import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;

public class ModClientConfigs {
  public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
  public static final ForgeConfigSpec SPEC;

  public static final ForgeConfigSpec.ConfigValue<String> TEMP_DISPLAY_MODE;
  public static final ForgeConfigSpec.ConfigValue<Boolean> FOOD_TYPE_TOOLTIP_DISPLAY;

  static {
    BUILDER.push("client");
    {
      BUILDER.push("temperature");
      {
        TEMP_DISPLAY_MODE =
            BUILDER
                .comment(
                    "The method in which the mod displays your temperature.",
                    "Acceptable values: GAUGE, NUMBER, NONE, BOTH")
                .defineInList("displayMode", "GAUGE", List.of("GAUGE", "NUMBER", "NONE", "BOTH"));
      }
      BUILDER.pop();
      BUILDER.push("foodNerf");
      {
        FOOD_TYPE_TOOLTIP_DISPLAY =
            BUILDER
                .comment("If true, displays the type of food at the top of the tooltip.")
                .define("displayTooltip", true);
      }
      BUILDER.pop();
    }

    BUILDER.pop();
    SPEC = BUILDER.build();
  }
}
