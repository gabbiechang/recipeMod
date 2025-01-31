package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;

public class EntityAIWatchClosest extends EntityAIBase {
   protected EntityLiving entity;
   /** The closest entity which is being watched by this one. */
   protected Entity closestEntity;
   /** This is the Maximum distance that the AI will look for the Entity */
   protected float maxDistance;
   private int lookTime;
   private final float chance;
   protected Class<? extends Entity> watchedClass;

   public EntityAIWatchClosest(EntityLiving entityIn, Class<? extends Entity> watchTargetClass, float maxDistance) {
      this(entityIn, watchTargetClass, maxDistance, 0.02F);
   }

   public EntityAIWatchClosest(EntityLiving entityIn, Class<? extends Entity> watchTargetClass, float maxDistance, float chanceIn) {
      this.entity = entityIn;
      this.watchedClass = watchTargetClass;
      this.maxDistance = maxDistance;
      this.chance = chanceIn;
      this.setMutexBits(2);
   }

   /**
    * Returns whether the EntityAIBase should begin execution.
    */
   public boolean shouldExecute() {
      if (this.entity.getRNG().nextFloat() >= this.chance) {
         return false;
      } else {
         if (this.entity.getAttackTarget() != null) {
            this.closestEntity = this.entity.getAttackTarget();
         }

         if (this.watchedClass == EntityPlayer.class) {
            this.closestEntity = this.entity.world.getClosestPlayer(this.entity.posX, this.entity.posY, this.entity.posZ, (double)this.maxDistance, EntitySelectors.NOT_SPECTATING.and(EntitySelectors.notRiding(this.entity)));
         } else {
            this.closestEntity = this.entity.world.findNearestEntityWithinAABB(this.watchedClass, this.entity.getBoundingBox().grow((double)this.maxDistance, 3.0D, (double)this.maxDistance), this.entity);
         }

         return this.closestEntity != null;
      }
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      if (!this.closestEntity.isAlive()) {
         return false;
      } else if (this.entity.getDistanceSq(this.closestEntity) > (double)(this.maxDistance * this.maxDistance)) {
         return false;
      } else {
         return this.lookTime > 0;
      }
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.lookTime = 40 + this.entity.getRNG().nextInt(40);
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      this.closestEntity = null;
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      this.entity.getLookHelper().setLookPosition(this.closestEntity.posX, this.closestEntity.posY + (double)this.closestEntity.getEyeHeight(), this.closestEntity.posZ, (float)this.entity.getHorizontalFaceSpeed(), (float)this.entity.getVerticalFaceSpeed());
      --this.lookTime;
   }
}