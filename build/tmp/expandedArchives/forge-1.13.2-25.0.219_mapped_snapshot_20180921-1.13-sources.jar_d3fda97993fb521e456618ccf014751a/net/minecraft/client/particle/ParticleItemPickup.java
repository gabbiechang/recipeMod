package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleItemPickup extends Particle {
   private final Entity item;
   private final Entity target;
   private int age;
   private final int maxAge;
   private final float yOffset;
   private final RenderManager renderManager = Minecraft.getInstance().getRenderManager();

   public ParticleItemPickup(World worldIn, Entity entityIn, Entity targetEntityIn, float yOffsetIn) {
      super(worldIn, entityIn.posX, entityIn.posY, entityIn.posZ, entityIn.motionX, entityIn.motionY, entityIn.motionZ);
      this.item = entityIn;
      this.target = targetEntityIn;
      this.maxAge = 3;
      this.yOffset = yOffsetIn;
   }

   /**
    * Renders the particle
    */
   public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      float f = ((float)this.age + partialTicks) / (float)this.maxAge;
      f = f * f;
      double d0 = this.item.posX;
      double d1 = this.item.posY;
      double d2 = this.item.posZ;
      double d3 = this.target.lastTickPosX + (this.target.posX - this.target.lastTickPosX) * (double)partialTicks;
      double d4 = this.target.lastTickPosY + (this.target.posY - this.target.lastTickPosY) * (double)partialTicks + (double)this.yOffset;
      double d5 = this.target.lastTickPosZ + (this.target.posZ - this.target.lastTickPosZ) * (double)partialTicks;
      double d6 = d0 + (d3 - d0) * (double)f;
      double d7 = d1 + (d4 - d1) * (double)f;
      double d8 = d2 + (d5 - d2) * (double)f;
      int i = this.getBrightnessForRender(partialTicks);
      int j = i % 65536;
      int k = i / 65536;
      OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, (float)j, (float)k);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      d6 = d6 - interpPosX;
      d7 = d7 - interpPosY;
      d8 = d8 - interpPosZ;
      GlStateManager.enableLighting();
      this.renderManager.renderEntity(this.item, d6, d7, d8, this.item.rotationYaw, partialTicks, false);
   }

   public void tick() {
      ++this.age;
      if (this.age == this.maxAge) {
         this.setExpired();
      }

   }

   /**
    * Retrieve what effect layer (what texture) the particle should be rendered with. 0 for the particle sprite sheet, 1
    * for the main Texture atlas, and 3 for a custom texture
    */
   public int getFXLayer() {
      return 3;
   }
}