package net.minecraft.client.particle;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticlePortal extends Particle {
   private final float portalParticleScale;
   private final double portalPosX;
   private final double portalPosY;
   private final double portalPosZ;

   protected ParticlePortal(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      this.motionX = xSpeedIn;
      this.motionY = ySpeedIn;
      this.motionZ = zSpeedIn;
      this.posX = xCoordIn;
      this.posY = yCoordIn;
      this.posZ = zCoordIn;
      this.portalPosX = this.posX;
      this.portalPosY = this.posY;
      this.portalPosZ = this.posZ;
      float f = this.rand.nextFloat() * 0.6F + 0.4F;
      this.particleScale = this.rand.nextFloat() * 0.2F + 0.5F;
      this.portalParticleScale = this.particleScale;
      this.particleRed = f * 0.9F;
      this.particleGreen = f * 0.3F;
      this.particleBlue = f;
      this.maxAge = (int)(Math.random() * 10.0D) + 40;
      this.setParticleTextureIndex((int)(Math.random() * 8.0D));
   }

   public void move(double x, double y, double z) {
      this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
      this.resetPositionToBB();
   }

   /**
    * Renders the particle
    */
   public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      float f = ((float)this.age + partialTicks) / (float)this.maxAge;
      f = 1.0F - f;
      f = f * f;
      f = 1.0F - f;
      this.particleScale = this.portalParticleScale * f;
      super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
   }

   public int getBrightnessForRender(float partialTick) {
      int i = super.getBrightnessForRender(partialTick);
      float f = (float)this.age / (float)this.maxAge;
      f = f * f;
      f = f * f;
      int j = i & 255;
      int k = i >> 16 & 255;
      k = k + (int)(f * 15.0F * 16.0F);
      if (k > 240) {
         k = 240;
      }

      return j | k << 16;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      float f = (float)this.age / (float)this.maxAge;
      float f1 = -f + f * f * 2.0F;
      float f2 = 1.0F - f1;
      this.posX = this.portalPosX + this.motionX * (double)f2;
      this.posY = this.portalPosY + this.motionY * (double)f2 + (double)(1.0F - f);
      this.posZ = this.portalPosZ + this.motionZ * (double)f2;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new ParticlePortal(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
      }
   }
}