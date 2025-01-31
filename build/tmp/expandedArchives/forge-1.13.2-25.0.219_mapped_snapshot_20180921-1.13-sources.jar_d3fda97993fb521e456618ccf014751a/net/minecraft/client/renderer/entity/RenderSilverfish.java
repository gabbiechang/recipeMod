package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.ModelSilverfish;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderSilverfish extends RenderLiving<EntitySilverfish> {
   private static final ResourceLocation SILVERFISH_TEXTURES = new ResourceLocation("textures/entity/silverfish.png");

   public RenderSilverfish(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelSilverfish(), 0.3F);
   }

   protected float getDeathMaxRotation(EntitySilverfish entityLivingBaseIn) {
      return 180.0F;
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(EntitySilverfish entity) {
      return SILVERFISH_TEXTURES;
   }
}