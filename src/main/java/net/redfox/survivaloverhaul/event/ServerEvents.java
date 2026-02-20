package net.redfox.survivaloverhaul.event;

import java.util.*;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;
import net.redfox.survivaloverhaul.SurvivalOverhaul;
import net.redfox.survivaloverhaul.command.GetTemperature;
import net.redfox.survivaloverhaul.command.SetTemperature;
import net.redfox.survivaloverhaul.config.ModCommonConfigs;
import net.redfox.survivaloverhaul.food.foodHistory.PlayerFoodHistory;
import net.redfox.survivaloverhaul.food.foodHistory.PlayerFoodHistoryProvider;
import net.redfox.survivaloverhaul.networking.ModPackets;
import net.redfox.survivaloverhaul.networking.packet.EatFoodC2SPacket;
import net.redfox.survivaloverhaul.networking.packet.FoodHistorySyncS2CPacket;
import net.redfox.survivaloverhaul.networking.packet.TemperatureDataSyncS2CPacket;
import net.redfox.survivaloverhaul.player.SymptomNerf;
import net.redfox.survivaloverhaul.temperature.PlayerTemperature;
import net.redfox.survivaloverhaul.temperature.PlayerTemperatureProvider;
import net.redfox.survivaloverhaul.weight.Weight;

public class ServerEvents {
  @Mod.EventBusSubscriber(modid = SurvivalOverhaul.MOD_ID)
  public static class ServerFoodEvents {
    @SubscribeEvent
    public static void onEatFood(LivingEntityUseItemEvent.Finish event) {
      if (!(event.getEntity() instanceof Player p)) return;
      if (!p.level().isClientSide()) return;
      if (!event.getItem().isEdible()) return;

      ModPackets.sendToServer(
          new EatFoodC2SPacket(PlayerFoodHistory.getItemNameFromStack(event.getItem())));
    }
  }

  @Mod.EventBusSubscriber(modid = SurvivalOverhaul.MOD_ID)
  public static class ServerTemperatureEvents {
    @SubscribeEvent
    public static void onServerTickEvent(TickEvent.ServerTickEvent event) {
      if (event.phase == TickEvent.Phase.END) {
        if (event.getServer().getTickCount() % 20 == 0) {
          for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            PlayerTemperature.periodicUpdate(player);
            SymptomNerf.periodicUpdate(player);
            Weight.applyWeightModifier(player);
          }
        }
      }
    }

    @SubscribeEvent
    public static void onItemPickup(PlayerEvent.ItemPickupEvent event) {
      Weight.applyWeightModifier(event.getEntity());
    }

    @SubscribeEvent
    public static void onItemDrop(ItemTossEvent event) {
      Weight.applyWeightModifier(event.getPlayer());
    }



    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
      new GetTemperature(event.getDispatcher());
      new SetTemperature(event.getDispatcher());

      ConfigCommand.register(event.getDispatcher());
    }
  }

  @Mod.EventBusSubscriber(modid = SurvivalOverhaul.MOD_ID)
  public static class ServerCapablityEvents {
    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
      if (event.getObject() instanceof Player) {
        if (!event
            .getObject()
            .getCapability(PlayerFoodHistoryProvider.PLAYER_FOOD_HISTORY)
            .isPresent()) {
          event.addCapability(
              ResourceLocation.fromNamespaceAndPath(SurvivalOverhaul.MOD_ID, "hunger_properties"),
              new PlayerFoodHistoryProvider());
        }
        if (!event
            .getObject()
            .getCapability(PlayerTemperatureProvider.PLAYER_TEMPERATURE)
            .isPresent()) {
          event.addCapability(
              ResourceLocation.fromNamespaceAndPath(SurvivalOverhaul.MOD_ID, "temp_properties"),
              new PlayerTemperatureProvider());
        }
      }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
      if (event.isWasDeath()) {
        event.getOriginal().reviveCaps();
        event
            .getOriginal()
            .getCapability(PlayerFoodHistoryProvider.PLAYER_FOOD_HISTORY)
            .ifPresent(
                oldStore ->
                    event
                        .getEntity()
                        .getCapability(PlayerFoodHistoryProvider.PLAYER_FOOD_HISTORY)
                        .ifPresent(newStore -> newStore.copyFrom(oldStore)));
      }
      event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
      if (!event.getLevel().isClientSide()) {
        if (event.getEntity() instanceof ServerPlayer player) {
          player
              .getCapability(PlayerFoodHistoryProvider.PLAYER_FOOD_HISTORY)
              .ifPresent(
                  history ->
                      ModPackets.sendToClient(
                          new FoodHistorySyncS2CPacket(history.getFoodHistory()), player));
          player
              .getCapability(PlayerTemperatureProvider.PLAYER_TEMPERATURE)
              .ifPresent(
                  playerTemperature -> {
                    ModPackets.sendToClient(
                        new TemperatureDataSyncS2CPacket(playerTemperature.getTemperature()),
                        player);
                  });
        }
      }
    }
  }

  @Mod.EventBusSubscriber(modid = SurvivalOverhaul.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
  public static class ServerHealthEvents {
    private static final Set<Entity> CANCEL_KNOCKBACK_SET =
        Collections.newSetFromMap(new WeakHashMap<>());

    public static void onLivingHurt(LivingHurtEvent event) {
      if (event.getSource().getEntity() instanceof Player player) {
        if (player.level().isClientSide()) {
          return;
        }
        if (!(event.getEntity() instanceof Player)) {
          if (player.getHealth() <= 6.0F) {
            CANCEL_KNOCKBACK_SET.add(event.getEntity());
          }
        }
      }
    }

    @SubscribeEvent
    public static void onLivingKnockback(LivingKnockBackEvent event) {
      if (CANCEL_KNOCKBACK_SET.remove(event.getEntity())) {
        event.setCanceled(true);
      }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
      event.getEntity().setHealth((int) (event.getEntity().getMaxHealth() * ModCommonConfigs.SPAWN_HEALTH_MULTIPLIER.get()));
      event.getEntity().getFoodData().setFoodLevel((int) (event.getEntity().getFoodData().getFoodLevel() * ModCommonConfigs.SPAWN_HUNGER_MULTIPLIER.get()));
    }
  }
}
