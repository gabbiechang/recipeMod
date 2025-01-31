package net.minecraft.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class EntityFlying extends EntityLiving {
   protected EntityFlying(EntityType<?> type, World p_i48578_2_) {
      super(type, p_i48578_2_);
   }

   public void fall(float distance, float damageMultiplier) {
   }

   protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
   }

   public void travel(float strafe, float vertical, float forward) {
      if (this.isInWater()) {
         this.moveRelative(strafe, vertical, forward, 0.02F);
         this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
         this.motionX *= (double)0.8F;
         this.motionY *= (double)0.8F;
         this.motionZ *= (double)0.8F;
      } else if (this.isInLava()) {
         this.moveRelative(strafe, vertical, forward, 0.02F);
         this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
         this.motionX *= 0.5D;
         this.motionY *= 0.5D;
         this.motionZ *= 0.5D;
      } else {
         float f = 0.91F;
         if (this.onGround) {
            BlockPos underPos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.posZ));
            f = this.world.getBlockState(underPos).getSlipperiness(world, underPos, this) * 0.91F;
         }

         float f1 = 0.16277137F / (f * f * f);
         this.moveRelative(strafe, vertical, forward, this.onGround ? 0.1F * f1 : 0.02F);
         f = 0.91F;
         if (this.onGround) {
            BlockPos underPos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.posZ));
            f = this.world.getBlockState(underPos).getSlipperiness(world, underPos, this) * 0.91F;
         }

         this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
         this.motionX *= (double)f;
         this.motionY *= (double)f;
         this.motionZ *= (double)f;
      }

      this.prevLimbSwingAmount = this.limbSwingAmount;
      double d1 = this.posX - this.prevPosX;
      double d0 = this.posZ - this.prevPosZ;
      float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;
      if (f2 > 1.0F) {
         f2 = 1.0F;
      }

      this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F;
      this.limbSwing += this.limbSwingAmount;
   }

   /**
    * Returns true if this entity should move as if it were on a ladder (either because it's actually on a ladder, or
    * for AI reasons)
    */
   public boolean isOnLadder() {
      return false;
   }
}