package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderFallingBlock extends Render<EntityFallingBlock> {
   public RenderFallingBlock(RenderManager renderManagerIn) {
      super(renderManagerIn);
      this.shadowSize = 0.5F;
   }

   /**
    * Renders the desired {@code T} type Entity.
    */
   public void doRender(EntityFallingBlock entity, double x, double y, double z, float entityYaw, float partialTicks) {
      IBlockState iblockstate = entity.getBlockState();
      if (iblockstate.getRenderType() == EnumBlockRenderType.MODEL) {
         World world = entity.getWorldObj();
         if (iblockstate != world.getBlockState(new BlockPos(entity)) && iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE) {
            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            if (this.renderOutlines) {
               GlStateManager.enableColorMaterial();
               GlStateManager.enableOutlineMode(this.getTeamColor(entity));
            }

            bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);
            BlockPos blockpos = new BlockPos(entity.posX, entity.getBoundingBox().maxY, entity.posZ);
            GlStateManager.translatef((float)(x - (double)blockpos.getX() - 0.5D), (float)(y - (double)blockpos.getY()), (float)(z - (double)blockpos.getZ() - 0.5D));
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
            blockrendererdispatcher.getBlockModelRenderer().renderModel(world, blockrendererdispatcher.getModelForState(iblockstate), iblockstate, blockpos, bufferbuilder, false, new Random(), iblockstate.getPositionRandom(entity.getOrigin()));
            tessellator.draw();
            if (this.renderOutlines) {
               GlStateManager.disableOutlineMode();
               GlStateManager.disableColorMaterial();
            }

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
         }
      }
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(EntityFallingBlock entity) {
      return TextureMap.LOCATION_BLOCKS_TEXTURE;
   }
}