package net.redfox.survivaloverhaul.mixin.food;

import net.minecraft.world.food.FoodData;
import net.redfox.survivaloverhaul.config.ModCommonConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FoodData.class)
public abstract class InstantStarveMixin {

  @ModifyArg(
      method = "tick",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/entity/player/Player;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"),
      index = 1)
  private float modify(float x) {
    if (!ModCommonConfigs.INSTANT_STARVE_ENABLED.get()) return x;
    return 10000F;
  }
}
