package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.pathfinding.PathNavigateGround;

public class EntityAITempt extends EntityAIBase {
   /** The entity using this AI that is tempted by the player. */
   private final EntityCreature temptedEntity;
   private final double speed;
   /** X position of player tempting this mob */
   private double targetX;
   /** Y position of player tempting this mob */
   private double targetY;
   /** Z position of player tempting this mob */
   private double targetZ;
   /** Tempting player's pitch */
   private double pitch;
   /** Tempting player's yaw */
   private double yaw;
   /** The player that is tempting the entity that is using this AI. */
   private EntityPlayer temptingPlayer;
   /**
    * A counter that is decremented each time the shouldExecute method is called. The shouldExecute method will always
    * return false if delayTemptCounter is greater than 0.
    */
   private int delayTemptCounter;
   /** True if this EntityAITempt task is running */
   private boolean isRunning;
   private final Ingredient temptItem;
   /** Whether the entity using this AI will be scared by the tempter's sudden movement. */
   private final boolean scaredByPlayerMovement;

   public EntityAITempt(EntityCreature p_i47822_1_, double p_i47822_2_, Ingredient p_i47822_4_, boolean p_i47822_5_) {
      this(p_i47822_1_, p_i47822_2_, p_i47822_5_, p_i47822_4_);
   }

   public EntityAITempt(EntityCreature p_i47823_1_, double p_i47823_2_, boolean p_i47823_4_, Ingredient p_i47823_5_) {
      this.temptedEntity = p_i47823_1_;
      this.speed = p_i47823_2_;
      this.temptItem = p_i47823_5_;
      this.scaredByPlayerMovement = p_i47823_4_;
      this.setMutexBits(3);
      if (!(p_i47823_1_.getNavigator() instanceof PathNavigateGround)) {
         throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
      }
   }

   /**
    * Returns whether the EntityAIBase should begin execution.
    */
   public boolean shouldExecute() {
      if (this.delayTemptCounter > 0) {
         --this.delayTemptCounter;
         return false;
      } else {
         this.temptingPlayer = this.temptedEntity.world.getClosestPlayerToEntity(this.temptedEntity, 10.0D);
         if (this.temptingPlayer == null) {
            return false;
         } else {
            return this.isTempting(this.temptingPlayer.getHeldItemMainhand()) || this.isTempting(this.temptingPlayer.getHeldItemOffhand());
         }
      }
   }

   protected boolean isTempting(ItemStack stack) {
      return this.temptItem.test(stack);
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      if (this.scaredByPlayerMovement) {
         if (this.temptedEntity.getDistanceSq(this.temptingPlayer) < 36.0D) {
            if (this.temptingPlayer.getDistanceSq(this.targetX, this.targetY, this.targetZ) > 0.010000000000000002D) {
               return false;
            }

            if (Math.abs((double)this.temptingPlayer.rotationPitch - this.pitch) > 5.0D || Math.abs((double)this.temptingPlayer.rotationYaw - this.yaw) > 5.0D) {
               return false;
            }
         } else {
            this.targetX = this.temptingPlayer.posX;
            this.targetY = this.temptingPlayer.posY;
            this.targetZ = this.temptingPlayer.posZ;
         }

         this.pitch = (double)this.temptingPlayer.rotationPitch;
         this.yaw = (double)this.temptingPlayer.rotationYaw;
      }

      return this.shouldExecute();
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.targetX = this.temptingPlayer.posX;
      this.targetY = this.temptingPlayer.posY;
      this.targetZ = this.temptingPlayer.posZ;
      this.isRunning = true;
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      this.temptingPlayer = null;
      this.temptedEntity.getNavigator().clearPath();
      this.delayTemptCounter = 100;
      this.isRunning = false;
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      this.temptedEntity.getLookHelper().setLookPositionWithEntity(this.temptingPlayer, (float)(this.temptedEntity.getHorizontalFaceSpeed() + 20), (float)this.temptedEntity.getVerticalFaceSpeed());
      if (this.temptedEntity.getDistanceSq(this.temptingPlayer) < 6.25D) {
         this.temptedEntity.getNavigator().clearPath();
      } else {
         this.temptedEntity.getNavigator().tryMoveToEntityLiving(this.temptingPlayer, this.speed);
      }

   }

   /**
    * @see #isRunning
    */
   public boolean isRunning() {
      return this.isRunning;
   }
}