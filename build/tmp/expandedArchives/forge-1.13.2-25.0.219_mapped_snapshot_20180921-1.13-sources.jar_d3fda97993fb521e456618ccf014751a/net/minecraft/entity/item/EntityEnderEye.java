package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityEnderEye extends Entity {
   /** 'x' location the eye should float towards. */
   private double targetX;
   /** 'y' location the eye should float towards. */
   private double targetY;
   /** 'z' location the eye should float towards. */
   private double targetZ;
   private int despawnTimer;
   private boolean shatterOrDrop;

   public EntityEnderEye(World worldIn) {
      super(EntityType.EYE_OF_ENDER, worldIn);
      this.setSize(0.25F, 0.25F);
   }

   public EntityEnderEye(World worldIn, double x, double y, double z) {
      this(worldIn);
      this.despawnTimer = 0;
      this.setPosition(x, y, z);
   }

   protected void registerData() {
   }

   /**
    * Checks if the entity is in range to render.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double distance) {
      double d0 = this.getBoundingBox().getAverageEdgeLength() * 4.0D;
      if (Double.isNaN(d0)) {
         d0 = 4.0D;
      }

      d0 = d0 * 64.0D;
      return distance < d0 * d0;
   }

   public void moveTowards(BlockPos pos) {
      double d0 = (double)pos.getX();
      int i = pos.getY();
      double d1 = (double)pos.getZ();
      double d2 = d0 - this.posX;
      double d3 = d1 - this.posZ;
      float f = MathHelper.sqrt(d2 * d2 + d3 * d3);
      if (f > 12.0F) {
         this.targetX = this.posX + d2 / (double)f * 12.0D;
         this.targetZ = this.posZ + d3 / (double)f * 12.0D;
         this.targetY = this.posY + 8.0D;
      } else {
         this.targetX = d0;
         this.targetY = (double)i;
         this.targetZ = d1;
      }

      this.despawnTimer = 0;
      this.shatterOrDrop = this.rand.nextInt(5) > 0;
   }

   /**
    * Updates the entity motion clientside, called by packets from the server
    */
   @OnlyIn(Dist.CLIENT)
   public void setVelocity(double x, double y, double z) {
      this.motionX = x;
      this.motionY = y;
      this.motionZ = z;
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float f = MathHelper.sqrt(x * x + z * z);
         this.rotationYaw = (float)(MathHelper.atan2(x, z) * (double)(180F / (float)Math.PI));
         this.rotationPitch = (float)(MathHelper.atan2(y, (double)f) * (double)(180F / (float)Math.PI));
         this.prevRotationYaw = this.rotationYaw;
         this.prevRotationPitch = this.rotationPitch;
      }

   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      this.lastTickPosX = this.posX;
      this.lastTickPosY = this.posY;
      this.lastTickPosZ = this.posZ;
      super.tick();
      this.posX += this.motionX;
      this.posY += this.motionY;
      this.posZ += this.motionZ;
      float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
      this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * (double)(180F / (float)Math.PI));

      for(this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f) * (double)(180F / (float)Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
         ;
      }

      while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
         this.prevRotationPitch += 360.0F;
      }

      while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
         this.prevRotationYaw -= 360.0F;
      }

      while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
         this.prevRotationYaw += 360.0F;
      }

      this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
      this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
      if (!this.world.isRemote) {
         double d0 = this.targetX - this.posX;
         double d1 = this.targetZ - this.posZ;
         float f1 = (float)Math.sqrt(d0 * d0 + d1 * d1);
         float f2 = (float)MathHelper.atan2(d1, d0);
         double d2 = (double)f + (double)(f1 - f) * 0.0025D;
         if (f1 < 1.0F) {
            d2 *= 0.8D;
            this.motionY *= 0.8D;
         }

         this.motionX = Math.cos((double)f2) * d2;
         this.motionZ = Math.sin((double)f2) * d2;
         if (this.posY < this.targetY) {
            this.motionY += (1.0D - this.motionY) * (double)0.015F;
         } else {
            this.motionY += (-1.0D - this.motionY) * (double)0.015F;
         }
      }

      float f3 = 0.25F;
      if (this.isInWater()) {
         for(int i = 0; i < 4; ++i) {
            this.world.spawnParticle(Particles.BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ);
         }
      } else {
         this.world.spawnParticle(Particles.PORTAL, this.posX - this.motionX * 0.25D + this.rand.nextDouble() * 0.6D - 0.3D, this.posY - this.motionY * 0.25D - 0.5D, this.posZ - this.motionZ * 0.25D + this.rand.nextDouble() * 0.6D - 0.3D, this.motionX, this.motionY, this.motionZ);
      }

      if (!this.world.isRemote) {
         this.setPosition(this.posX, this.posY, this.posZ);
         ++this.despawnTimer;
         if (this.despawnTimer > 80 && !this.world.isRemote) {
            this.playSound(SoundEvents.ENTITY_ENDER_EYE_DEATH, 1.0F, 1.0F);
            this.remove();
            if (this.shatterOrDrop) {
               this.world.spawnEntity(new EntityItem(this.world, this.posX, this.posY, this.posZ, new ItemStack(Items.ENDER_EYE)));
            } else {
               this.world.playEvent(2003, new BlockPos(this), 0);
            }
         }
      }

   }

   /**
    * Writes the extra NBT data specific to this type of entity. Should <em>not</em> be called from outside this class;
    * use {@link #writeUnlessPassenger} or {@link #writeWithoutTypeId} instead.
    */
   public void writeAdditional(NBTTagCompound compound) {
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(NBTTagCompound compound) {
   }

   /**
    * Gets how bright this entity is.
    */
   public float getBrightness() {
      return 1.0F;
   }

   @OnlyIn(Dist.CLIENT)
   public int getBrightnessForRender() {
      return 15728880;
   }

   /**
    * Returns true if it's possible to attack this entity with an item.
    */
   public boolean canBeAttackedWithItem() {
      return false;
   }
}