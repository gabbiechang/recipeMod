package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IAnimal;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public abstract class EntityGolem extends EntityCreature implements IAnimal {
   protected EntityGolem(EntityType<?> p_i48569_1_, World p_i48569_2_) {
      super(p_i48569_1_, p_i48569_2_);
   }

   public void fall(float distance, float damageMultiplier) {
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return null;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return null;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return null;
   }

   /**
    * Get number of ticks, at least during which the living entity will be silent.
    */
   public int getTalkInterval() {
      return 120;
   }

   /**
    * Determines if an entity can be despawned, used on idle far away entities
    */
   public boolean canDespawn() {
      return false;
   }
}