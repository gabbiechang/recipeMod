package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.MathHelper;

public class EntityFlyHelper extends EntityMoveHelper {
   public EntityFlyHelper(EntityLiving p_i47418_1_) {
      super(p_i47418_1_);
   }

   public void tick() {
      if (this.action == EntityMoveHelper.Action.MOVE_TO) {
         this.action = EntityMoveHelper.Action.WAIT;
         this.entity.setNoGravity(true);
         double d0 = this.posX - this.entity.posX;
         double d1 = this.posY - this.entity.posY;
         double d2 = this.posZ - this.entity.posZ;
         double d3 = d0 * d0 + d1 * d1 + d2 * d2;
         if (d3 < (double)2.5000003E-7F) {
            this.entity.setMoveVertical(0.0F);
            this.entity.setMoveForward(0.0F);
            return;
         }

         float f = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
         this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, f, 10.0F);
         float f1;
         if (this.entity.onGround) {
            f1 = (float)(this.speed * this.entity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
         } else {
            f1 = (float)(this.speed * this.entity.getAttribute(SharedMonsterAttributes.FLYING_SPEED).getValue());
         }

         this.entity.setAIMoveSpeed(f1);
         double d4 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
         float f2 = (float)(-(MathHelper.atan2(d1, d4) * (double)(180F / (float)Math.PI)));
         this.entity.rotationPitch = this.limitAngle(this.entity.rotationPitch, f2, 10.0F);
         this.entity.setMoveVertical(d1 > 0.0D ? f1 : -f1);
      } else {
         this.entity.setNoGravity(false);
         this.entity.setMoveVertical(0.0F);
         this.entity.setMoveForward(0.0F);
      }

   }
}