package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.model.ModelSkeleton;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerStrayClothing implements LayerRenderer<EntityStray> {
   private static final ResourceLocation STRAY_CLOTHES_TEXTURES = new ResourceLocation("textures/entity/skeleton/stray_overlay.png");
   private final RenderLivingBase<?> renderer;
   private final ModelSkeleton layerModel = new ModelSkeleton(0.25F, true);

   public LayerStrayClothing(RenderLivingBase<?> p_i47183_1_) {
      this.renderer = p_i47183_1_;
   }

   public void render(EntityStray entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.layerModel.setModelAttributes(this.renderer.getMainModel());
      this.layerModel.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.renderer.bindTexture(STRAY_CLOTHES_TEXTURES);
      this.layerModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
   }

   public boolean shouldCombineTextures() {
      return true;
   }
}