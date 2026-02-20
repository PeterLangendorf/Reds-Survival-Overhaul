package net.redfox.survivaloverhaul.mixin.food;

import net.minecraft.world.entity.player.Player;
import net.redfox.survivaloverhaul.config.ModCommonConfigs;
import net.redfox.survivaloverhaul.util.config.ConfigUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Player.class)
public abstract class GlobalHungerMultiplierMixin {
  @Unique
  private Player survivaloverhaul$self() {
    return (Player) (Object) this;
  }

  @ModifyVariable(method = "causeFoodExhaustion", at = @At("HEAD"), argsOnly = true)
  private float modifyFoodExhaustion(float pExhaustion) {
    if (!ModCommonConfigs.GLOBAL_HUNGER_MULTIPLIER_ENABLED.get()) return pExhaustion;
    return pExhaustion * ConfigUtil.getGlobalHungerMultiplier(survivaloverhaul$self().level().getDifficulty());
  }
}
