 package net.redfox.hardcorereimagined.mixin;

 import net.minecraft.sounds.SoundEvents;
 import net.minecraft.util.Mth;
 import net.minecraft.world.Difficulty;
 import net.minecraft.world.DifficultyInstance;
 import net.minecraft.world.entity.EntityType;
 import net.minecraft.world.entity.animal.Animal;
 import net.minecraft.world.entity.animal.Chicken;
 import net.minecraft.world.item.Items;
 import net.minecraft.world.level.Level;
 import net.minecraft.world.level.gameevent.GameEvent;
 import net.minecraft.world.phys.Vec3;
 import net.redfox.hardcorereimagined.HardcoreReimagined;
 import net.redfox.hardcorereimagined.config.ModCommonConfigs;
 import net.redfox.hardcorereimagined.environment.ChickenNerf;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

 import java.util.HashMap;
 import java.util.Map;

 @Mixin(Chicken.class)
 public abstract class ChickenMixin extends Animal {
  protected ChickenMixin(EntityType<? extends Animal> pEntityType, Level pLevel) {
    super(pEntityType, pLevel);
  }

  @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
  public void aiStep(CallbackInfo ci) {
    Chicken thisObject = (Chicken) (Object) this;
    super.aiStep();
    thisObject.oFlap = thisObject.flap;
    thisObject.oFlapSpeed = thisObject.flapSpeed;
    thisObject.flapSpeed += (thisObject.onGround() ? -1.0F : 4.0F) * 0.3F;
    thisObject.flapSpeed = Mth.clamp(thisObject.flapSpeed, 0.0F, 1.0F);
    if (!thisObject.onGround() && thisObject.flapping < 1.0F) {
      thisObject.flapping = 1.0F;
    }

    thisObject.flapping *= 0.9F;
    Vec3 vec3 = thisObject.getDeltaMovement();
    if (!thisObject.onGround() && vec3.y < 0.0D) {
      thisObject.setDeltaMovement(vec3.multiply(1.0D, 0.6D, 1.0D));
    }

    thisObject.flap += thisObject.flapping * 2.0F;
    if (!thisObject.level().isClientSide
        && thisObject.isAlive()
        && !thisObject.isBaby()
        && !thisObject.isChickenJockey()
        && --thisObject.eggTime <= 0) {
      thisObject.playSound(
          SoundEvents.CHICKEN_EGG,
          1.0F,
          (thisObject.random.nextFloat() - thisObject.random.nextFloat()) * 0.2F + 1.0F);
      thisObject.spawnAtLocation(Items.EGG);
      thisObject.gameEvent(GameEvent.ENTITY_PLACE);
      int cooldown = ChickenNerf.getCooldown(thisObject.level().getDifficulty());
      thisObject.eggTime = thisObject.random.nextInt(cooldown) + cooldown;
    }
    ci.cancel();
  }
 }
