package net.redfox.hardcorereimagined.symptom;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public class ModSymptoms {
  public static void periodicUpdate(ServerPlayer player) {
    if (player.isCreative() || player.isDeadOrDying()) return;
    float health = player.getHealth();
    int hunger = player.getFoodData().getFoodLevel();

    int slownessAmplifier = -1;
    int miningFatigueAmplifier = -1;
    int weaknessAmplifier = -1;
    int nauseaAmplifier = -1;

    if (health <= 10) {
      slownessAmplifier++;
    }
    if (health <= 6) {
      miningFatigueAmplifier++;
      slownessAmplifier++;
    }
    if (health <= 3) {
      weaknessAmplifier++;
      slownessAmplifier++;
    }
    if (health <= 2) {
      nauseaAmplifier++;
      slownessAmplifier++;
      weaknessAmplifier++;
    }
    if (hunger <= 8) {
      slownessAmplifier++;
    }
    if (hunger <= 4) {
      weaknessAmplifier++;
    }
    if (hunger <= 2) {
      slownessAmplifier++;
      miningFatigueAmplifier++;
    }
    if (slownessAmplifier >= 0)
      player.addEffect(create(MobEffects.MOVEMENT_SLOWDOWN, 120, slownessAmplifier));
    if (miningFatigueAmplifier >= 0)
      player.addEffect(create(MobEffects.DIG_SLOWDOWN, 120, miningFatigueAmplifier));
    if (weaknessAmplifier >= 0)
      player.addEffect(create(MobEffects.WEAKNESS, 120, weaknessAmplifier));
    if (nauseaAmplifier >= 0) player.addEffect(create(MobEffects.CONFUSION, 120, nauseaAmplifier));
  }

  public static MobEffectInstance create(MobEffect effect, int durationInTicks, int amplifier) {
    return new MobEffectInstance(effect, durationInTicks, amplifier, false, false, true);
  }
}
