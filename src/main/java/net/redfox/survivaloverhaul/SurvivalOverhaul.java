package net.redfox.survivaloverhaul;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.redfox.survivaloverhaul.client.TemperatureHudOverlay;
import net.redfox.survivaloverhaul.config.ModClientConfigs;
import net.redfox.survivaloverhaul.config.ModCommonConfigs;
import net.redfox.survivaloverhaul.effect.ModEffects;
import net.redfox.survivaloverhaul.util.config.ConfigUtil;
import net.redfox.survivaloverhaul.environment.CropNerf;
import net.redfox.survivaloverhaul.event.AppleSkinEvents;
import net.redfox.survivaloverhaul.food.FoodNerf;
import net.redfox.survivaloverhaul.networking.ModPackets;
import org.slf4j.Logger;

@Mod(SurvivalOverhaul.MOD_ID)
public class SurvivalOverhaul {
  public static final String MOD_ID = "survivaloverhaul";
  public static final Logger LOGGER = LogUtils.getLogger();

  public SurvivalOverhaul(IEventBus modEventBus, ModContainer modContainer) {

    modContainer.registerConfig(
        ModConfig.Type.COMMON, ModCommonConfigs.SPEC, SurvivalOverhaul.MOD_ID + "/common.toml");
    modContainer.registerConfig(
        ModConfig.Type.CLIENT, ModClientConfigs.SPEC, SurvivalOverhaul.MOD_ID + "/client.toml");

    ModEffects.register(modEventBus);

    modEventBus.addListener(this::commonSetup);
    NeoForge.EVENT_BUS.addListener(CropNerf::onCropGrowth);
    NeoForge.EVENT_BUS.register(this);

    if (ModList.get().isLoaded("appleskin")) {
      NeoForge.EVENT_BUS.addListener(AppleSkinEvents::onAppleSkinFoodEvent);
    }
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
    ModPackets.init();
    TemperatureHudOverlay.init();
    if (ModCommonConfigs.FOOD_MODIFICATION_ENABLED.get()) FoodNerf.init();
    ConfigUtil.init();

    if (ModClientConfigs.FOOD_TYPE_TOOLTIP_DISPLAY.get())
      NeoForge.EVENT_BUS.addListener(FoodNerf::addTooltip);
  }
}
