package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.model.ModelCow;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderCow extends RenderLiving<EntityCow> {
   private static final ResourceLocation COW_TEXTURES = new ResourceLocation("textures/entity/cow/cow.png");

   public RenderCow(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelCow(), 0.7F);
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(EntityCow entity) {
      return COW_TEXTURES;
   }
}