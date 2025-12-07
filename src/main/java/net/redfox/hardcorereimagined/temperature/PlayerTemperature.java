package net.redfox.hardcorereimagined.temperature;

import static net.redfox.hardcorereimagined.config.FormattedConfigValues.Temperature.*;

import java.util.*;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.redfox.hardcorereimagined.config.FormattedConfigValues;
import net.redfox.hardcorereimagined.effect.HeatStrokeEffect;
import net.redfox.hardcorereimagined.effect.HypothermiaEffect;
import net.redfox.hardcorereimagined.networking.ModPackets;
import net.redfox.hardcorereimagined.networking.packet.TemperatureDataSyncS2CPacket;
import net.redfox.hardcorereimagined.util.MathHelper;
import net.redfox.hardcorereimagined.util.config.ConfigValue;
import oshi.util.tuples.Pair;

@AutoRegisterCapability
public class PlayerTemperature {
  private double temperature = 0;

  public double getTemperature() {
    return MathHelper.roundToOneDecimal(temperature);
  }

  public void setTemperature(double temperature) {
    this.temperature = MathHelper.roundToOneDecimal(temperature);
  }

  public void approachTemperature(double goal) {
    double difference =
        MathHelper.roundToOneDecimal(
            ((Math.max(0.1, Math.min(5, Math.abs(goal - temperature) / 10))) * 10) / 10);

    if (temperature > goal) {
      temperature -= difference;
    } else if (temperature < goal) {
      temperature += difference;
    }
  }

  public void saveNBTData(CompoundTag nbt) {
    nbt.putDouble("temperature", MathHelper.roundToOneDecimal(temperature));
  }

  public void loadNBTData(CompoundTag nbt) {
    temperature = MathHelper.roundToOneDecimal(nbt.getDouble("temperature"));
  }

  public static void periodicUpdate(ServerPlayer player) {
    player
        .getCapability(PlayerTemperatureProvider.PLAYER_TEMPERATURE)
        .ifPresent(
            playerTemperature -> {
              double approachingTemperature = PlayerTemperature.calculateTemperatureGoal(player);
              double temp = playerTemperature.getTemperature();
              if (temp >= 80) {
                HeatStrokeEffect.applyStandardEffect(player, temp);
              } else if (temp <= -80) {
                HypothermiaEffect.applyStandardEffect(player, temp);
              }
              playerTemperature.approachTemperature(approachingTemperature);
              ModPackets.sendToClient(
                  new TemperatureDataSyncS2CPacket(playerTemperature.getTemperature()), player);
            });
  }

  public static double calculateTemperatureGoal(ServerPlayer player) {
    Level level = player.level();
    Block currentBlock = level.getBlockState(player.blockPosition()).getBlock();
    double goalTemperature = 0;
    // Biome

    if (BIOME_TEMPERATURE_ENABLED.getAsBoolean()) {
      goalTemperature +=
          FormattedConfigValues.checkMapForValue(
              BIOME_TEMPERATURES, level.getBiome(player.blockPosition()).get());
    }

    // Insulators (torches, campfires, lava, fire, magma)
    if (INSULATORS_ENABLED.getAsBoolean()) {
      Stream<BlockState> stream =
          level.getBlockStates(player.getBoundingBox().inflate(INSULATORS_RANGE.get()));
      if (INSULATORS_DISTINCT.getAsBoolean()) {
        Set<ConfigValue<Block>> countedConfigs = new HashSet<>();
        List<BlockState> allStates = stream.toList();
        for (BlockState state : allStates) {
          for (Map.Entry<ConfigValue<Block>, Integer> entry : INSULATOR_TEMPERATURES.entrySet()) {
            ConfigValue<Block> configValue = entry.getKey();
            if (configValue.is(state.getBlock())) {
              if (countedConfigs.add(configValue)) goalTemperature += entry.getValue();
              break;
            }
          }
        }

      } else {
        for (BlockState state : stream.toList()) {
          goalTemperature +=
              FormattedConfigValues.checkMapForValue(INSULATOR_TEMPERATURES, state.getBlock());
        }
      }
    }

    // Fluids
    if (FLUID_TEMPERATURE_ENABLED.getAsBoolean())
      goalTemperature += FormattedConfigValues.checkMapForValue(FLUID_TEMPERATURES, currentBlock);

    // Block Temperatures
    if (BLOCK_TEMPERATURES_ENABLED.getAsBoolean()
        && ((BLOCK_TEMPERATURES_NEED_BOOTS.getAsBoolean()
                && player.getItemBySlot(EquipmentSlot.FEET).isEmpty())
            || !BLOCK_TEMPERATURES_NEED_BOOTS.getAsBoolean())) {
      goalTemperature +=
          FormattedConfigValues.checkMapForValue(
              BLOCK_TEMPERATURES, level.getBlockState(player.blockPosition().below()).getBlock());
      if (level
          .getBlockState(player.blockPosition())
          .isCollisionShapeFullBlock(level, player.blockPosition()))
        goalTemperature += FormattedConfigValues.checkMapForValue(BLOCK_TEMPERATURES, currentBlock);
    }

    // Rain / snow / thunder
    if (WEATHER_TEMPERATURES_ENABLED.getAsBoolean()) {
      if (level.isRainingAt(player.blockPosition())) {
        goalTemperature += RAIN_TEMPERATURE.get();
      }
      if (level.isRaining()
          && level.getBiome(player.blockPosition()).value().coldEnoughToSnow(player.blockPosition())
          && level.canSeeSky(player.blockPosition())) {
        goalTemperature += SNOW_TEMPERATURE.get();
      }
    }

    // Day or night
    if (TIME_TEMPERATURE_ENABLED.getAsBoolean()) {
      if (level.isDay() && level.dimension() == Level.OVERWORLD) {
        goalTemperature += DAY_TEMPERATURE.get();
      } else {
        goalTemperature += NIGHT_TEMPERATURE.get();
      }
    }
    // On fire

    if (FIRE_TEMPERATURE_ENABLED.getAsBoolean() && player.getRemainingFireTicks() > 0) {
      goalTemperature += FIRE_TEMPERATURE.get();
    }

    // Altitude
    if (ALTITUDE_TEMPERATURE_ENABLED.getAsBoolean()) {
      if (player.getY() < LOWER_ALTITUDE.get()) {
        goalTemperature -=
            MathHelper.roundToOneDecimal(
                Math.abs(((player.getY() - LOWER_ALTITUDE.get()) / LOWER_MULTIPLIER.get())));
      }
      if (player.getY() > UPPER_ALTITUDE.get()) {
        goalTemperature -=
            MathHelper.roundToOneDecimal(
                Math.abs(((UPPER_ALTITUDE.get() - player.getY()) / UPPER_MULTIPLIER.get())));
      }
    }
    if (ARMOR_INSULATIONS_ENABLED.getAsBoolean()) {
      int heatResistance = 0;
      int coldResistance = 0;

      for (ItemStack i : player.getArmorSlots()) {
        Pair<Integer, Integer> ints =
            FormattedConfigValues.checkMapForPair(ARMOR_INSULATIONS, i.getItem());
        heatResistance += ints.getA();
        coldResistance += ints.getB();

        if (goalTemperature < 0) {
          goalTemperature += coldResistance;
          if (goalTemperature > 0) goalTemperature = 0;
        } else if (goalTemperature > 0) {
          goalTemperature -= heatResistance;
          if (goalTemperature < 0) goalTemperature = 0;
        }
      }
    }

    return goalTemperature
        + (FLUCTUATE_TEMPERATURE.getAsBoolean()
            ? MathHelper.roundToOneDecimal(Math.random() * 2 - 0.5)
            : 0);
  }
}
