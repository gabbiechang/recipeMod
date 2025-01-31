package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.client.renderer.entity.model.ModelShulker;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderShulker extends RenderLiving<EntityShulker> {
   public static final ResourceLocation field_204402_a = new ResourceLocation("textures/entity/shulker/shulker.png");
   public static final ResourceLocation[] SHULKER_ENDERGOLEM_TEXTURE = new ResourceLocation[]{new ResourceLocation("textures/entity/shulker/shulker_white.png"), new ResourceLocation("textures/entity/shulker/shulker_orange.png"), new ResourceLocation("textures/entity/shulker/shulker_magenta.png"), new ResourceLocation("textures/entity/shulker/shulker_light_blue.png"), new ResourceLocation("textures/entity/shulker/shulker_yellow.png"), new ResourceLocation("textures/entity/shulker/shulker_lime.png"), new ResourceLocation("textures/entity/shulker/shulker_pink.png"), new ResourceLocation("textures/entity/shulker/shulker_gray.png"), new ResourceLocation("textures/entity/shulker/shulker_light_gray.png"), new ResourceLocation("textures/entity/shulker/shulker_cyan.png"), new ResourceLocation("textures/entity/shulker/shulker_purple.png"), new ResourceLocation("textures/entity/shulker/shulker_blue.png"), new ResourceLocation("textures/entity/shulker/shulker_brown.png"), new ResourceLocation("textures/entity/shulker/shulker_green.png"), new ResourceLocation("textures/entity/shulker/shulker_red.png"), new ResourceLocation("textures/entity/shulker/shulker_black.png")};

   public RenderShulker(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelShulker(), 0.0F);
      this.addLayer(new RenderShulker.HeadLayer());
   }

   public ModelShulker getMainModel() {
      return (ModelShulker)super.getMainModel();
   }

   /**
    * Renders the desired {@code T} type Entity.
    */
   public void doRender(EntityShulker entity, double x, double y, double z, float entityYaw, float partialTicks) {
      int i = entity.getClientTeleportInterp();
      if (i > 0 && entity.isAttachedToBlock()) {
         BlockPos blockpos = entity.getAttachmentPos();
         BlockPos blockpos1 = entity.getOldAttachPos();
         double d0 = (double)((float)i - partialTicks) / 6.0D;
         d0 = d0 * d0;
         double d1 = (double)(blockpos.getX() - blockpos1.getX()) * d0;
         double d2 = (double)(blockpos.getY() - blockpos1.getY()) * d0;
         double d3 = (double)(blockpos.getZ() - blockpos1.getZ()) * d0;
         super.doRender(entity, x - d1, y - d2, z - d3, entityYaw, partialTicks);
      } else {
         super.doRender(entity, x, y, z, entityYaw, partialTicks);
      }

   }

   public boolean shouldRender(EntityShulker livingEntity, ICamera camera, double camX, double camY, double camZ) {
      if (super.shouldRender(livingEntity, camera, camX, camY, camZ)) {
         return true;
      } else {
         if (livingEntity.getClientTeleportInterp() > 0 && livingEntity.isAttachedToBlock()) {
            BlockPos blockpos = livingEntity.getOldAttachPos();
            BlockPos blockpos1 = livingEntity.getAttachmentPos();
            Vec3d vec3d = new Vec3d((double)blockpos1.getX(), (double)blockpos1.getY(), (double)blockpos1.getZ());
            Vec3d vec3d1 = new Vec3d((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
            if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(vec3d1.x, vec3d1.y, vec3d1.z, vec3d.x, vec3d.y, vec3d.z))) {
               return true;
            }
         }

         return false;
      }
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(EntityShulker entity) {
      return entity.getColor() == null ? field_204402_a : SHULKER_ENDERGOLEM_TEXTURE[entity.getColor().getId()];
   }

   protected void applyRotations(EntityShulker entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
      switch(entityLiving.getAttachmentFacing()) {
      case DOWN:
      default:
         break;
      case EAST:
         GlStateManager.translatef(0.5F, 0.5F, 0.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
         break;
      case WEST:
         GlStateManager.translatef(-0.5F, 0.5F, 0.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
         break;
      case NORTH:
         GlStateManager.translatef(0.0F, 0.5F, -0.5F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         break;
      case SOUTH:
         GlStateManager.translatef(0.0F, 0.5F, 0.5F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
         break;
      case UP:
         GlStateManager.translatef(0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
      }

   }

   /**
    * Allows the render to do state modifications necessary before the model is rendered.
    */
   protected void preRenderCallback(EntityShulker entitylivingbaseIn, float partialTickTime) {
      float f = 0.999F;
      GlStateManager.scalef(0.999F, 0.999F, 0.999F);
   }

   @OnlyIn(Dist.CLIENT)
   class HeadLayer implements LayerRenderer<EntityShulker> {
      private HeadLayer() {
      }

      public void render(EntityShulker entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
         GlStateManager.pushMatrix();
         switch(entitylivingbaseIn.getAttachmentFacing()) {
         case DOWN:
         default:
            break;
         case EAST:
            GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translatef(1.0F, -1.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
            break;
         case WEST:
            GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translatef(-1.0F, -1.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
            break;
         case NORTH:
            GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translatef(0.0F, -1.0F, -1.0F);
            break;
         case SOUTH:
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translatef(0.0F, -1.0F, 1.0F);
            break;
         case UP:
            GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translatef(0.0F, -2.0F, 0.0F);
         }

         ModelRenderer modelrenderer = RenderShulker.this.getMainModel().getHead();
         modelrenderer.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
         modelrenderer.rotateAngleX = headPitch * ((float)Math.PI / 180F);
         EnumDyeColor enumdyecolor = entitylivingbaseIn.getColor();
         if (enumdyecolor == null) {
            RenderShulker.this.bindTexture(RenderShulker.field_204402_a);
         } else {
            RenderShulker.this.bindTexture(RenderShulker.SHULKER_ENDERGOLEM_TEXTURE[enumdyecolor.getId()]);
         }

         modelrenderer.render(scale);
         GlStateManager.popMatrix();
      }

      public boolean shouldCombineTextures() {
         return false;
      }
   }
}