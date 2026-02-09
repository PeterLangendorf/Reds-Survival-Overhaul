package net.redfox.hardcorereimagined.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.GameRules;
import net.redfox.hardcorereimagined.config.ModCommonConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tick(Player pPlayer, CallbackInfo ci) {
      pPlayer.getFoodData().addExhaustion(ModCommonConfigs.PASSIVE_EXHAUSTION.get());
      Difficulty difficulty = pPlayer.level().getDifficulty();
      this.lastFoodLevel = this.foodLevel;
      if (this.exhaustionLevel > 4.0F) {
        this.exhaustionLevel -= 4.0F;
        if (this.saturationLevel > 0.0F) {
          this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
        } else if (difficulty != Difficulty.PEACEFUL) {
          this.foodLevel = Math.max(this.foodLevel - 1, 0);
        }
      }

      boolean flag =
   pPlayer.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
      if (flag && this.saturationLevel > 0.0F && pPlayer.isHurt() && this.foodLevel >= 20) {
        ++this.tickTimer;
        if (this.tickTimer >= 100 *
   PublishedConfigValues.GLOBAL_HUNGER_MULTIPLIER.get(difficulty)) {
          float f = Math.min(this.saturationLevel, 6.0F);
          pPlayer.heal(f / 6.0F);
          this.addExhaustion(f);
          this.tickTimer = 0;
        }
      } else if (flag && this.foodLevel >= 11 && pPlayer.isHurt()) {
        ++this.tickTimer;
        if (this.tickTimer >= 100 *
   PublishedConfigValues.GLOBAL_HUNGER_MULTIPLIER.get(difficulty)) {
          pPlayer.heal(1.0F);
          this.addExhaustion(6.0F);
          this.tickTimer = 0;
        }
      } else if (this.foodLevel <= 0) {
        ++this.tickTimer;
        if (this.tickTimer >= 80) {
          pPlayer.hurt(pPlayer.damageSources().starve(), 10000.0F);
          this.tickTimer = 0;
        }
      } else {
        this.tickTimer = 0;
      }
      ci.cancel();
    }

    @Inject(method = "addExhaustion", at = @At("HEAD"), cancellable = true)
    public void addExhaustion(float pExhaustion, CallbackInfo ci) {
      Player
      if (Minecraft.getInstance().level != null)
        this.exhaustionLevel =
            Math.min(
                this.exhaustionLevel
                    + pExhaustion
                        * PublishedConfigValues.GLOBAL_HUNGER_MULTIPLIER.get(
                            Minecraft.getInstance().level.getDifficulty()),
                40.0F);
      ci.cancel();
    }
}
