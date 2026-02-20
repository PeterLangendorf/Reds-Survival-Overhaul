package net.redfox.survivaloverhaul.mixin.food;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.redfox.survivaloverhaul.config.ModCommonConfigs;
import net.redfox.survivaloverhaul.util.config.ConfigUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class PassiveHungerLossMixin {
  @Inject(method = "tick", at = @At("HEAD"))
  private void tick(Player pPlayer, CallbackInfo ci) {
    pPlayer.causeFoodExhaustion(ConfigUtil.getPassiveHungerLoss());
  }
}
