package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelEnderCrystal extends ModelBase {
   /** The cube model for the Ender Crystal. */
   private final ModelRenderer cube;
   /** The glass model for the Ender Crystal. */
   private final ModelRenderer glass = new ModelRenderer(this, "glass");
   /** The base model for the Ender Crystal. */
   private final ModelRenderer base;

   public ModelEnderCrystal(float p_i1170_1_, boolean renderBase) {
      this.glass.setTextureOffset(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
      this.cube = new ModelRenderer(this, "cube");
      this.cube.setTextureOffset(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
      if (renderBase) {
         this.base = new ModelRenderer(this, "base");
         this.base.setTextureOffset(0, 16).addBox(-6.0F, 0.0F, -6.0F, 12, 4, 12);
      } else {
         this.base = null;
      }

   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      GlStateManager.pushMatrix();
      GlStateManager.scalef(2.0F, 2.0F, 2.0F);
      GlStateManager.translatef(0.0F, -0.5F, 0.0F);
      if (this.base != null) {
         this.base.render(scale);
      }

      GlStateManager.rotatef(limbSwingAmount, 0.0F, 1.0F, 0.0F);
      GlStateManager.translatef(0.0F, 0.8F + ageInTicks, 0.0F);
      GlStateManager.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
      this.glass.render(scale);
      float f = 0.875F;
      GlStateManager.scalef(0.875F, 0.875F, 0.875F);
      GlStateManager.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
      GlStateManager.rotatef(limbSwingAmount, 0.0F, 1.0F, 0.0F);
      this.glass.render(scale);
      GlStateManager.scalef(0.875F, 0.875F, 0.875F);
      GlStateManager.rotatef(60.0F, 0.7071F, 0.0F, 0.7071F);
      GlStateManager.rotatef(limbSwingAmount, 0.0F, 1.0F, 0.0F);
      this.cube.render(scale);
      GlStateManager.popMatrix();
   }
}