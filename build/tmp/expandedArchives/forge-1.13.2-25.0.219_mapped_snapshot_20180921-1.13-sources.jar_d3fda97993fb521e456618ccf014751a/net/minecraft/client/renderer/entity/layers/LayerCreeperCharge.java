package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.client.renderer.entity.model.ModelCreeper;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerCreeperCharge implements LayerRenderer<EntityCreeper> {
   private static final ResourceLocation LIGHTNING_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
   private final RenderCreeper creeperRenderer;
   private final ModelCreeper creeperModel = new ModelCreeper(2.0F);

   public LayerCreeperCharge(RenderCreeper creeperRendererIn) {
      this.creeperRenderer = creeperRendererIn;
   }

   public void render(EntityCreeper entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (entitylivingbaseIn.getPowered()) {
         boolean flag = entitylivingbaseIn.isInvisible();
         GlStateManager.depthMask(!flag);
         this.creeperRenderer.bindTexture(LIGHTNING_TEXTURE);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         float f = (float)entitylivingbaseIn.ticksExisted + partialTicks;
         GlStateManager.translatef(f * 0.01F, f * 0.01F, 0.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.enableBlend();
         float f1 = 0.5F;
         GlStateManager.color4f(0.5F, 0.5F, 0.5F, 1.0F);
         GlStateManager.disableLighting();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
         this.creeperModel.setModelAttributes(this.creeperRenderer.getMainModel());
         Minecraft.getInstance().entityRenderer.setupFogColor(true);
         this.creeperModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
         Minecraft.getInstance().entityRenderer.setupFogColor(false);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         GlStateManager.matrixMode(5888);
         GlStateManager.enableLighting();
         GlStateManager.disableBlend();
         GlStateManager.depthMask(true);
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}