package net.redfox.survivaloverhaul.effect;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.redfox.survivaloverhaul.player.SymptomNerf;
import net.redfox.survivaloverhaul.util.ModDamageTypes;

public class HeatStrokeEffect extends MobEffect {
  public HeatStrokeEffect(MobEffectCategory pCategory, int pColor) {
    super(pCategory, pColor);
  }

  @Override
  public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
    pLivingEntity.hurt(
        new DamageSource(
            pLivingEntity
                .level()
                .registryAccess()
                .lookupOrThrow(Registries.DAMAGE_TYPE)
                .getOrThrow(ModDamageTypes.HEAT_STROKE_KEY)),
        1 + pAmplifier);
    super.applyEffectTick(pLivingEntity, pAmplifier);
  }

  @Override
  public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
    return pDuration % Math.max(10, 50 - 10 * pAmplifier) == 0;
  }

  public static void applyStandardEffect(ServerPlayer player, double temp) {
    player.addEffect(
        SymptomNerf.create(
            ModEffects.HEAT_STROKE.get(), 120, Math.abs((int) ((50 - temp) / 30)) - 1));
  }
}
