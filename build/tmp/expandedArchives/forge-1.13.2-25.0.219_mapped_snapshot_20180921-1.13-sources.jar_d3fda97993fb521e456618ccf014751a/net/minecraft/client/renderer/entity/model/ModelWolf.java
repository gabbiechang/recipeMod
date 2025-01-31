package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelWolf extends ModelBase {
   /** main box for the wolf head */
   private final ModelRenderer wolfHeadMain;
   /** The wolf's body */
   private final ModelRenderer wolfBody;
   /** Wolf'se first leg */
   private final ModelRenderer wolfLeg1;
   /** Wolf's second leg */
   private final ModelRenderer wolfLeg2;
   /** Wolf's third leg */
   private final ModelRenderer wolfLeg3;
   /** Wolf's fourth leg */
   private final ModelRenderer wolfLeg4;
   /** The wolf's tail */
   private final ModelRenderer wolfTail;
   /** The wolf's mane */
   private final ModelRenderer wolfMane;

   public ModelWolf() {
      float f = 0.0F;
      float f1 = 13.5F;
      this.wolfHeadMain = new ModelRenderer(this, 0, 0);
      this.wolfHeadMain.addBox(-2.0F, -3.0F, -2.0F, 6, 6, 4, 0.0F);
      this.wolfHeadMain.setRotationPoint(-1.0F, 13.5F, -7.0F);
      this.wolfBody = new ModelRenderer(this, 18, 14);
      this.wolfBody.addBox(-3.0F, -2.0F, -3.0F, 6, 9, 6, 0.0F);
      this.wolfBody.setRotationPoint(0.0F, 14.0F, 2.0F);
      this.wolfMane = new ModelRenderer(this, 21, 0);
      this.wolfMane.addBox(-3.0F, -3.0F, -3.0F, 8, 6, 7, 0.0F);
      this.wolfMane.setRotationPoint(-1.0F, 14.0F, 2.0F);
      this.wolfLeg1 = new ModelRenderer(this, 0, 18);
      this.wolfLeg1.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
      this.wolfLeg1.setRotationPoint(-2.5F, 16.0F, 7.0F);
      this.wolfLeg2 = new ModelRenderer(this, 0, 18);
      this.wolfLeg2.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
      this.wolfLeg2.setRotationPoint(0.5F, 16.0F, 7.0F);
      this.wolfLeg3 = new ModelRenderer(this, 0, 18);
      this.wolfLeg3.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
      this.wolfLeg3.setRotationPoint(-2.5F, 16.0F, -4.0F);
      this.wolfLeg4 = new ModelRenderer(this, 0, 18);
      this.wolfLeg4.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
      this.wolfLeg4.setRotationPoint(0.5F, 16.0F, -4.0F);
      this.wolfTail = new ModelRenderer(this, 9, 18);
      this.wolfTail.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
      this.wolfTail.setRotationPoint(-1.0F, 12.0F, 8.0F);
      this.wolfHeadMain.setTextureOffset(16, 14).addBox(-2.0F, -5.0F, 0.0F, 2, 2, 1, 0.0F);
      this.wolfHeadMain.setTextureOffset(16, 14).addBox(2.0F, -5.0F, 0.0F, 2, 2, 1, 0.0F);
      this.wolfHeadMain.setTextureOffset(0, 10).addBox(-0.5F, 0.0F, -5.0F, 3, 3, 4, 0.0F);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
      if (this.isChild) {
         float f = 2.0F;
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, 5.0F * scale, 2.0F * scale);
         this.wolfHeadMain.renderWithRotation(scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.translatef(0.0F, 24.0F * scale, 0.0F);
         this.wolfBody.render(scale);
         this.wolfLeg1.render(scale);
         this.wolfLeg2.render(scale);
         this.wolfLeg3.render(scale);
         this.wolfLeg4.render(scale);
         this.wolfTail.renderWithRotation(scale);
         this.wolfMane.render(scale);
         GlStateManager.popMatrix();
      } else {
         this.wolfHeadMain.renderWithRotation(scale);
         this.wolfBody.render(scale);
         this.wolfLeg1.render(scale);
         this.wolfLeg2.render(scale);
         this.wolfLeg3.render(scale);
         this.wolfLeg4.render(scale);
         this.wolfTail.renderWithRotation(scale);
         this.wolfMane.render(scale);
      }

   }

   /**
    * Used for easily adding entity-dependent animations. The second and third float params here are the same second and
    * third as in the setRotationAngles method.
    */
   public void setLivingAnimations(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
      EntityWolf entitywolf = (EntityWolf)entitylivingbaseIn;
      if (entitywolf.isAngry()) {
         this.wolfTail.rotateAngleY = 0.0F;
      } else {
         this.wolfTail.rotateAngleY = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
      }

      if (entitywolf.isSitting()) {
         this.wolfMane.setRotationPoint(-1.0F, 16.0F, -3.0F);
         this.wolfMane.rotateAngleX = 1.2566371F;
         this.wolfMane.rotateAngleY = 0.0F;
         this.wolfBody.setRotationPoint(0.0F, 18.0F, 0.0F);
         this.wolfBody.rotateAngleX = ((float)Math.PI / 4F);
         this.wolfTail.setRotationPoint(-1.0F, 21.0F, 6.0F);
         this.wolfLeg1.setRotationPoint(-2.5F, 22.0F, 2.0F);
         this.wolfLeg1.rotateAngleX = ((float)Math.PI * 1.5F);
         this.wolfLeg2.setRotationPoint(0.5F, 22.0F, 2.0F);
         this.wolfLeg2.rotateAngleX = ((float)Math.PI * 1.5F);
         this.wolfLeg3.rotateAngleX = 5.811947F;
         this.wolfLeg3.setRotationPoint(-2.49F, 17.0F, -4.0F);
         this.wolfLeg4.rotateAngleX = 5.811947F;
         this.wolfLeg4.setRotationPoint(0.51F, 17.0F, -4.0F);
      } else {
         this.wolfBody.setRotationPoint(0.0F, 14.0F, 2.0F);
         this.wolfBody.rotateAngleX = ((float)Math.PI / 2F);
         this.wolfMane.setRotationPoint(-1.0F, 14.0F, -3.0F);
         this.wolfMane.rotateAngleX = this.wolfBody.rotateAngleX;
         this.wolfTail.setRotationPoint(-1.0F, 12.0F, 8.0F);
         this.wolfLeg1.setRotationPoint(-2.5F, 16.0F, 7.0F);
         this.wolfLeg2.setRotationPoint(0.5F, 16.0F, 7.0F);
         this.wolfLeg3.setRotationPoint(-2.5F, 16.0F, -4.0F);
         this.wolfLeg4.setRotationPoint(0.5F, 16.0F, -4.0F);
         this.wolfLeg1.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
         this.wolfLeg2.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
         this.wolfLeg3.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
         this.wolfLeg4.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
      }

      this.wolfHeadMain.rotateAngleZ = entitywolf.getInterestedAngle(partialTickTime) + entitywolf.getShakeAngle(partialTickTime, 0.0F);
      this.wolfMane.rotateAngleZ = entitywolf.getShakeAngle(partialTickTime, -0.08F);
      this.wolfBody.rotateAngleZ = entitywolf.getShakeAngle(partialTickTime, -0.16F);
      this.wolfTail.rotateAngleZ = entitywolf.getShakeAngle(partialTickTime, -0.2F);
   }

   /**
    * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
    * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how "far"
    * arms and legs can swing at most.
    */
   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
      this.wolfHeadMain.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      this.wolfHeadMain.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      this.wolfTail.rotateAngleX = ageInTicks;
   }
}