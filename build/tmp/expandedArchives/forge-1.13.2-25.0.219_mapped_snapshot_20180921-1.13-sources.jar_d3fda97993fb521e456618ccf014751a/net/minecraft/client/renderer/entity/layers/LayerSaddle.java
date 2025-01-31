package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.entity.RenderPig;
import net.minecraft.client.renderer.entity.model.ModelPig;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerSaddle implements LayerRenderer<EntityPig> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/pig/pig_saddle.png");
   private final RenderPig pigRenderer;
   private final ModelPig pigModel = new ModelPig(0.5F);

   public LayerSaddle(RenderPig pigRendererIn) {
      this.pigRenderer = pigRendererIn;
   }

   public void render(EntityPig entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (entitylivingbaseIn.getSaddled()) {
         this.pigRenderer.bindTexture(TEXTURE);
         this.pigModel.setModelAttributes(this.pigRenderer.getMainModel());
         this.pigModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}