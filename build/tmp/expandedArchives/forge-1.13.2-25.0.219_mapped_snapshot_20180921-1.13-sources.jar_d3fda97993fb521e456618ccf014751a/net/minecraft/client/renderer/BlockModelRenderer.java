package net.minecraft.client.renderer;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockModelRenderer {
   private final BlockColors blockColors;
   private static final ThreadLocal<Object2IntLinkedOpenHashMap<BlockPos>> CACHE_COMBINED_LIGHT = ThreadLocal.withInitial(() -> {
      Object2IntLinkedOpenHashMap<BlockPos> object2intlinkedopenhashmap = new Object2IntLinkedOpenHashMap<BlockPos>(50) {
         protected void rehash(int p_rehash_1_) {
         }
      };
      object2intlinkedopenhashmap.defaultReturnValue(Integer.MAX_VALUE);
      return object2intlinkedopenhashmap;
   });
   private static final ThreadLocal<Boolean> CACHE_ENABLED = ThreadLocal.withInitial(() -> {
      return false;
   });

   public BlockModelRenderer(BlockColors blockColorsIn) {
      this.blockColors = blockColorsIn;
   }

   @Deprecated
   public boolean renderModel(IWorldReader worldIn, IBakedModel modelIn, IBlockState stateIn, BlockPos posIn, BufferBuilder buffer, boolean checkSides, Random randomIn, long rand) {
      return renderModel(worldIn, modelIn, stateIn, posIn, buffer, checkSides, randomIn, rand, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }

   public boolean renderModel(IWorldReader worldIn, IBakedModel modelIn, IBlockState stateIn, BlockPos posIn, BufferBuilder buffer, boolean checkSides, Random randomIn, long rand, net.minecraftforge.client.model.data.IModelData modelData) {
      boolean flag = Minecraft.isAmbientOcclusionEnabled() && stateIn.getLightValue(worldIn, posIn) == 0 && modelIn.isAmbientOcclusion(stateIn);
      modelData = modelIn.getModelData(worldIn, posIn, stateIn, modelData);

      try {
         return flag ? this.renderModelSmooth(worldIn, modelIn, stateIn, posIn, buffer, checkSides, randomIn, rand, modelData) : this.renderModelFlat(worldIn, modelIn, stateIn, posIn, buffer, checkSides, randomIn, rand, modelData);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block model");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Block model being tesselated");
         CrashReportCategory.addBlockInfo(crashreportcategory, posIn, stateIn);
         crashreportcategory.addDetail("Using AO", flag);
         throw new ReportedException(crashreport);
      }
   }

   @Deprecated
   public boolean renderModelSmooth(IWorldReader worldIn, IBakedModel modelIn, IBlockState stateIn, BlockPos posIn, BufferBuilder buffer, boolean checkSides, Random randomIn, long rand) {
      return renderModelSmooth(worldIn, modelIn, stateIn, posIn, buffer, checkSides, randomIn, rand, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }

   public boolean renderModelSmooth(IWorldReader worldIn, IBakedModel modelIn, IBlockState stateIn, BlockPos posIn, BufferBuilder buffer, boolean checkSides, Random randomIn, long rand, net.minecraftforge.client.model.data.IModelData modelData) {
      boolean flag = false;
      float[] afloat = new float[EnumFacing.values().length * 2];
      BitSet bitset = new BitSet(3);
      BlockModelRenderer.AmbientOcclusionFace blockmodelrenderer$ambientocclusionface = new BlockModelRenderer.AmbientOcclusionFace();

      for(EnumFacing enumfacing : EnumFacing.values()) {
         randomIn.setSeed(rand);
         List<BakedQuad> list = modelIn.getQuads(stateIn, enumfacing, randomIn, modelData);
         if (!list.isEmpty() && (!checkSides || Block.shouldSideBeRendered(stateIn, worldIn, posIn, enumfacing))) {
            this.renderQuadsSmooth(worldIn, stateIn, posIn, buffer, list, afloat, bitset, blockmodelrenderer$ambientocclusionface);
            flag = true;
         }
      }

      randomIn.setSeed(rand);
      List<BakedQuad> list1 = modelIn.getQuads(stateIn, (EnumFacing)null, randomIn, modelData);
      if (!list1.isEmpty()) {
         this.renderQuadsSmooth(worldIn, stateIn, posIn, buffer, list1, afloat, bitset, blockmodelrenderer$ambientocclusionface);
         flag = true;
      }

      return flag;
   }

   @Deprecated
   public boolean renderModelFlat(IWorldReader worldIn, IBakedModel modelIn, IBlockState stateIn, BlockPos posIn, BufferBuilder buffer, boolean checkSides, Random randomIn, long rand) {
      return renderModelFlat(worldIn, modelIn, stateIn, posIn, buffer, checkSides, randomIn, rand, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }

   public boolean renderModelFlat(IWorldReader worldIn, IBakedModel modelIn, IBlockState stateIn, BlockPos posIn, BufferBuilder buffer, boolean checkSides, Random randomIn, long rand, net.minecraftforge.client.model.data.IModelData modelData) {
      boolean flag = false;
      BitSet bitset = new BitSet(3);

      for(EnumFacing enumfacing : EnumFacing.values()) {
         randomIn.setSeed(rand);
         List<BakedQuad> list = modelIn.getQuads(stateIn, enumfacing, randomIn, modelData);
         if (!list.isEmpty() && (!checkSides || Block.shouldSideBeRendered(stateIn, worldIn, posIn, enumfacing))) {
            int i = stateIn.getPackedLightmapCoords(worldIn, posIn.offset(enumfacing));
            this.renderQuadsFlat(worldIn, stateIn, posIn, i, false, buffer, list, bitset);
            flag = true;
         }
      }

      randomIn.setSeed(rand);
      List<BakedQuad> list1 = modelIn.getQuads(stateIn, (EnumFacing)null, randomIn, modelData);
      if (!list1.isEmpty()) {
         this.renderQuadsFlat(worldIn, stateIn, posIn, -1, true, buffer, list1, bitset);
         flag = true;
      }

      return flag;
   }

   private void renderQuadsSmooth(IWorldReader blockAccessIn, IBlockState stateIn, BlockPos posIn, BufferBuilder buffer, List<BakedQuad> list, float[] quadBounds, BitSet bitSet, BlockModelRenderer.AmbientOcclusionFace aoFace) {
      Vec3d vec3d = stateIn.getOffset(blockAccessIn, posIn);
      double d0 = (double)posIn.getX() + vec3d.x;
      double d1 = (double)posIn.getY() + vec3d.y;
      double d2 = (double)posIn.getZ() + vec3d.z;
      int i = 0;

      for(int j = list.size(); i < j; ++i) {
         BakedQuad bakedquad = list.get(i);
         this.fillQuadBounds(stateIn, bakedquad.getVertexData(), bakedquad.getFace(), quadBounds, bitSet);
         aoFace.updateVertexBrightness(blockAccessIn, stateIn, posIn, bakedquad.getFace(), quadBounds, bitSet);
         buffer.addVertexData(bakedquad.getVertexData());
         buffer.putBrightness4(aoFace.vertexBrightness[0], aoFace.vertexBrightness[1], aoFace.vertexBrightness[2], aoFace.vertexBrightness[3]);
         if(bakedquad.shouldApplyDiffuseLighting()) {
            float diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(bakedquad.getFace());
            aoFace.vertexColorMultiplier[0] *= diffuse;
            aoFace.vertexColorMultiplier[1] *= diffuse;
            aoFace.vertexColorMultiplier[2] *= diffuse;
            aoFace.vertexColorMultiplier[3] *= diffuse;
         }
         if (bakedquad.hasTintIndex()) {
            int k = this.blockColors.getColor(stateIn, blockAccessIn, posIn, bakedquad.getTintIndex());
            float f = (float)(k >> 16 & 255) / 255.0F;
            float f1 = (float)(k >> 8 & 255) / 255.0F;
            float f2 = (float)(k & 255) / 255.0F;
            buffer.putColorMultiplier(aoFace.vertexColorMultiplier[0] * f, aoFace.vertexColorMultiplier[0] * f1, aoFace.vertexColorMultiplier[0] * f2, 4);
            buffer.putColorMultiplier(aoFace.vertexColorMultiplier[1] * f, aoFace.vertexColorMultiplier[1] * f1, aoFace.vertexColorMultiplier[1] * f2, 3);
            buffer.putColorMultiplier(aoFace.vertexColorMultiplier[2] * f, aoFace.vertexColorMultiplier[2] * f1, aoFace.vertexColorMultiplier[2] * f2, 2);
            buffer.putColorMultiplier(aoFace.vertexColorMultiplier[3] * f, aoFace.vertexColorMultiplier[3] * f1, aoFace.vertexColorMultiplier[3] * f2, 1);
         } else {
            buffer.putColorMultiplier(aoFace.vertexColorMultiplier[0], aoFace.vertexColorMultiplier[0], aoFace.vertexColorMultiplier[0], 4);
            buffer.putColorMultiplier(aoFace.vertexColorMultiplier[1], aoFace.vertexColorMultiplier[1], aoFace.vertexColorMultiplier[1], 3);
            buffer.putColorMultiplier(aoFace.vertexColorMultiplier[2], aoFace.vertexColorMultiplier[2], aoFace.vertexColorMultiplier[2], 2);
            buffer.putColorMultiplier(aoFace.vertexColorMultiplier[3], aoFace.vertexColorMultiplier[3], aoFace.vertexColorMultiplier[3], 1);
         }

         buffer.putPosition(d0, d1, d2);
      }

   }

   private void fillQuadBounds(IBlockState stateIn, int[] vertexData, EnumFacing face, @Nullable float[] quadBounds, BitSet boundsFlags) {
      float f = 32.0F;
      float f1 = 32.0F;
      float f2 = 32.0F;
      float f3 = -32.0F;
      float f4 = -32.0F;
      float f5 = -32.0F;

      for(int i = 0; i < 4; ++i) {
         float f6 = Float.intBitsToFloat(vertexData[i * 7]);
         float f7 = Float.intBitsToFloat(vertexData[i * 7 + 1]);
         float f8 = Float.intBitsToFloat(vertexData[i * 7 + 2]);
         f = Math.min(f, f6);
         f1 = Math.min(f1, f7);
         f2 = Math.min(f2, f8);
         f3 = Math.max(f3, f6);
         f4 = Math.max(f4, f7);
         f5 = Math.max(f5, f8);
      }

      if (quadBounds != null) {
         quadBounds[EnumFacing.WEST.getIndex()] = f;
         quadBounds[EnumFacing.EAST.getIndex()] = f3;
         quadBounds[EnumFacing.DOWN.getIndex()] = f1;
         quadBounds[EnumFacing.UP.getIndex()] = f4;
         quadBounds[EnumFacing.NORTH.getIndex()] = f2;
         quadBounds[EnumFacing.SOUTH.getIndex()] = f5;
         int j = EnumFacing.values().length;
         quadBounds[EnumFacing.WEST.getIndex() + j] = 1.0F - f;
         quadBounds[EnumFacing.EAST.getIndex() + j] = 1.0F - f3;
         quadBounds[EnumFacing.DOWN.getIndex() + j] = 1.0F - f1;
         quadBounds[EnumFacing.UP.getIndex() + j] = 1.0F - f4;
         quadBounds[EnumFacing.NORTH.getIndex() + j] = 1.0F - f2;
         quadBounds[EnumFacing.SOUTH.getIndex() + j] = 1.0F - f5;
      }

      float f9 = 1.0E-4F;
      float f10 = 0.9999F;
      switch(face) {
      case DOWN:
         boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
         boundsFlags.set(0, (f1 < 1.0E-4F || stateIn.isFullCube()) && f1 == f4);
         break;
      case UP:
         boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
         boundsFlags.set(0, (f4 > 0.9999F || stateIn.isFullCube()) && f1 == f4);
         break;
      case NORTH:
         boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
         boundsFlags.set(0, (f2 < 1.0E-4F || stateIn.isFullCube()) && f2 == f5);
         break;
      case SOUTH:
         boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
         boundsFlags.set(0, (f5 > 0.9999F || stateIn.isFullCube()) && f2 == f5);
         break;
      case WEST:
         boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
         boundsFlags.set(0, (f < 1.0E-4F || stateIn.isFullCube()) && f == f3);
         break;
      case EAST:
         boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
         boundsFlags.set(0, (f3 > 0.9999F || stateIn.isFullCube()) && f == f3);
      }

   }

   private void renderQuadsFlat(IWorldReader blockAccessIn, IBlockState stateIn, BlockPos posIn, int brightnessIn, boolean ownBrightness, BufferBuilder buffer, List<BakedQuad> list, BitSet bitSet) {
      Vec3d vec3d = stateIn.getOffset(blockAccessIn, posIn);
      double d0 = (double)posIn.getX() + vec3d.x;
      double d1 = (double)posIn.getY() + vec3d.y;
      double d2 = (double)posIn.getZ() + vec3d.z;
      int i = 0;

      for(int j = list.size(); i < j; ++i) {
         BakedQuad bakedquad = list.get(i);
         if (ownBrightness) {
            this.fillQuadBounds(stateIn, bakedquad.getVertexData(), bakedquad.getFace(), (float[])null, bitSet);
            BlockPos blockpos = bitSet.get(0) ? posIn.offset(bakedquad.getFace()) : posIn;
            brightnessIn = stateIn.getPackedLightmapCoords(blockAccessIn, blockpos);
         }

         buffer.addVertexData(bakedquad.getVertexData());
         buffer.putBrightness4(brightnessIn, brightnessIn, brightnessIn, brightnessIn);
         if (bakedquad.hasTintIndex()) {
            int k = this.blockColors.getColor(stateIn, blockAccessIn, posIn, bakedquad.getTintIndex());
            float f = (float)(k >> 16 & 255) / 255.0F;
            float f1 = (float)(k >> 8 & 255) / 255.0F;
            float f2 = (float)(k & 255) / 255.0F;
            if(bakedquad.shouldApplyDiffuseLighting()) {
               float diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(bakedquad.getFace());
               f *= diffuse;
               f1 *= diffuse;
               f2 *= diffuse;
            }
            buffer.putColorMultiplier(f, f1, f2, 4);
            buffer.putColorMultiplier(f, f1, f2, 3);
            buffer.putColorMultiplier(f, f1, f2, 2);
            buffer.putColorMultiplier(f, f1, f2, 1);
         } else if(bakedquad.shouldApplyDiffuseLighting()) {
            float diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(bakedquad.getFace());
            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 4);
            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 3);
            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 2);
            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 1);
         }

         buffer.putPosition(d0, d1, d2);
      }

   }

   public void renderModelBrightnessColor(IBakedModel bakedModel, float brightness, float red, float green, float blue) {
      this.renderModelBrightnessColor((IBlockState)null, bakedModel, brightness, red, green, blue);
   }

   public void renderModelBrightnessColor(@Nullable IBlockState state, IBakedModel modelIn, float brightness, float red, float green, float blue) {
      Random random = new Random();
      long i = 42L;

      for(EnumFacing enumfacing : EnumFacing.values()) {
         random.setSeed(42L);
         this.renderModelBrightnessColorQuads(brightness, red, green, blue, modelIn.getQuads(state, enumfacing, random));
      }

      random.setSeed(42L);
      this.renderModelBrightnessColorQuads(brightness, red, green, blue, modelIn.getQuads(state, (EnumFacing)null, random));
   }

   public void renderModelBrightness(IBakedModel model, IBlockState state, float brightness, boolean glDisabled) {
      GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
      int i = this.blockColors.getColor(state, (IWorldReaderBase)null, (BlockPos)null, 0);
      float f = (float)(i >> 16 & 255) / 255.0F;
      float f1 = (float)(i >> 8 & 255) / 255.0F;
      float f2 = (float)(i & 255) / 255.0F;
      if (!glDisabled) {
         GlStateManager.color4f(brightness, brightness, brightness, 1.0F);
      }

      this.renderModelBrightnessColor(state, model, brightness, f, f1, f2);
   }

   private void renderModelBrightnessColorQuads(float brightness, float red, float green, float blue, List<BakedQuad> listQuads) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      int i = 0;

      for(int j = listQuads.size(); i < j; ++i) {
         BakedQuad bakedquad = listQuads.get(i);
         bufferbuilder.begin(7, DefaultVertexFormats.ITEM);
         bufferbuilder.addVertexData(bakedquad.getVertexData());
         if (bakedquad.hasTintIndex()) {
            bufferbuilder.putColorRGB_F4(red * brightness, green * brightness, blue * brightness);
         } else {
            bufferbuilder.putColorRGB_F4(brightness, brightness, brightness);
         }

         Vec3i vec3i = bakedquad.getFace().getDirectionVec();
         bufferbuilder.putNormal((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
         tessellator.draw();
      }

   }

   public static void enableCache() {
      CACHE_ENABLED.set(true);
   }

   public static void disableCache() {
      CACHE_COMBINED_LIGHT.get().clear();
      CACHE_ENABLED.set(false);
   }

   private static int getPackedLightmapCoords(IBlockState blockStateIn, IWorldReader worldIn, BlockPos posIn) {
      Boolean obool = CACHE_ENABLED.get();
      Object2IntLinkedOpenHashMap<BlockPos> object2intlinkedopenhashmap = null;
      if (obool) {
         object2intlinkedopenhashmap = CACHE_COMBINED_LIGHT.get();
         int i = object2intlinkedopenhashmap.getInt(posIn);
         if (i != Integer.MAX_VALUE) {
            return i;
         }
      }

      int j = blockStateIn.getPackedLightmapCoords(worldIn, posIn);
      if (object2intlinkedopenhashmap != null) {
         if (object2intlinkedopenhashmap.size() == 50) {
            object2intlinkedopenhashmap.removeFirstInt();
         }

         object2intlinkedopenhashmap.put(posIn.toImmutable(), j);
      }

      return j;
   }

   @OnlyIn(Dist.CLIENT)
   class AmbientOcclusionFace {
      private final float[] vertexColorMultiplier = new float[4];
      private final int[] vertexBrightness = new int[4];

      public void updateVertexBrightness(IWorldReader worldIn, IBlockState state, BlockPos centerPos, EnumFacing direction, float[] faceShape, BitSet shapeState) {
         BlockPos blockpos = shapeState.get(0) ? centerPos.offset(direction) : centerPos;
         BlockModelRenderer.EnumNeighborInfo blockmodelrenderer$enumneighborinfo = BlockModelRenderer.EnumNeighborInfo.getNeighbourInfo(direction);
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
         blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[0]);
         int i = BlockModelRenderer.getPackedLightmapCoords(state, worldIn, blockpos$mutableblockpos);
         float f = worldIn.getBlockState(blockpos$mutableblockpos).getAmbientOcclusionLightValue();
         blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[1]);
         int j = BlockModelRenderer.getPackedLightmapCoords(state, worldIn, blockpos$mutableblockpos);
         float f1 = worldIn.getBlockState(blockpos$mutableblockpos).getAmbientOcclusionLightValue();
         blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[2]);
         int k = BlockModelRenderer.getPackedLightmapCoords(state, worldIn, blockpos$mutableblockpos);
         float f2 = worldIn.getBlockState(blockpos$mutableblockpos).getAmbientOcclusionLightValue();
         blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[3]);
         int l = BlockModelRenderer.getPackedLightmapCoords(state, worldIn, blockpos$mutableblockpos);
         float f3 = worldIn.getBlockState(blockpos$mutableblockpos).getAmbientOcclusionLightValue();
         blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[0]).move(direction);
         boolean flag = worldIn.getBlockState(blockpos$mutableblockpos).getOpacity(worldIn, blockpos$mutableblockpos) == 0;
         blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[1]).move(direction);
         boolean flag1 = worldIn.getBlockState(blockpos$mutableblockpos).getOpacity(worldIn, blockpos$mutableblockpos) == 0;
         blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[2]).move(direction);
         boolean flag2 = worldIn.getBlockState(blockpos$mutableblockpos).getOpacity(worldIn, blockpos$mutableblockpos) == 0;
         blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[3]).move(direction);
         boolean flag3 = worldIn.getBlockState(blockpos$mutableblockpos).getOpacity(worldIn, blockpos$mutableblockpos) == 0;
         float f4;
         int i1;
         if (!flag2 && !flag) {
            f4 = f;
            i1 = i;
         } else {
            blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[0]).move(blockmodelrenderer$enumneighborinfo.corners[2]);
            f4 = worldIn.getBlockState(blockpos$mutableblockpos).getAmbientOcclusionLightValue();
            i1 = BlockModelRenderer.getPackedLightmapCoords(state, worldIn, blockpos$mutableblockpos);
         }

         float f5;
         int j1;
         if (!flag3 && !flag) {
            f5 = f;
            j1 = i;
         } else {
            blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[0]).move(blockmodelrenderer$enumneighborinfo.corners[3]);
            f5 = worldIn.getBlockState(blockpos$mutableblockpos).getAmbientOcclusionLightValue();
            j1 = BlockModelRenderer.getPackedLightmapCoords(state, worldIn, blockpos$mutableblockpos);
         }

         float f6;
         int k1;
         if (!flag2 && !flag1) {
            f6 = f1;
            k1 = j;
         } else {
            blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[1]).move(blockmodelrenderer$enumneighborinfo.corners[2]);
            f6 = worldIn.getBlockState(blockpos$mutableblockpos).getAmbientOcclusionLightValue();
            k1 = BlockModelRenderer.getPackedLightmapCoords(state, worldIn, blockpos$mutableblockpos);
         }

         float f7;
         int l1;
         if (!flag3 && !flag1) {
            f7 = f1;
            l1 = j;
         } else {
            blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$enumneighborinfo.corners[1]).move(blockmodelrenderer$enumneighborinfo.corners[3]);
            f7 = worldIn.getBlockState(blockpos$mutableblockpos).getAmbientOcclusionLightValue();
            l1 = BlockModelRenderer.getPackedLightmapCoords(state, worldIn, blockpos$mutableblockpos);
         }

         int i2 = BlockModelRenderer.getPackedLightmapCoords(state, worldIn, centerPos);
         blockpos$mutableblockpos.setPos(centerPos).move(direction);
         if (shapeState.get(0) || !worldIn.getBlockState(blockpos$mutableblockpos).isOpaqueCube(worldIn, blockpos$mutableblockpos)) {
            i2 = BlockModelRenderer.getPackedLightmapCoords(state, worldIn, blockpos$mutableblockpos);
         }

         float f8 = shapeState.get(0) ? worldIn.getBlockState(blockpos).getAmbientOcclusionLightValue() : worldIn.getBlockState(centerPos).getAmbientOcclusionLightValue();
         BlockModelRenderer.VertexTranslations blockmodelrenderer$vertextranslations = BlockModelRenderer.VertexTranslations.getVertexTranslations(direction);
         if (shapeState.get(1) && blockmodelrenderer$enumneighborinfo.doNonCubicWeight) {
            float f29 = (f3 + f + f5 + f8) * 0.25F;
            float f30 = (f2 + f + f4 + f8) * 0.25F;
            float f31 = (f2 + f1 + f6 + f8) * 0.25F;
            float f32 = (f3 + f1 + f7 + f8) * 0.25F;
            float f13 = faceShape[blockmodelrenderer$enumneighborinfo.vert0Weights[0].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert0Weights[1].shape];
            float f14 = faceShape[blockmodelrenderer$enumneighborinfo.vert0Weights[2].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert0Weights[3].shape];
            float f15 = faceShape[blockmodelrenderer$enumneighborinfo.vert0Weights[4].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert0Weights[5].shape];
            float f16 = faceShape[blockmodelrenderer$enumneighborinfo.vert0Weights[6].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert0Weights[7].shape];
            float f17 = faceShape[blockmodelrenderer$enumneighborinfo.vert1Weights[0].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert1Weights[1].shape];
            float f18 = faceShape[blockmodelrenderer$enumneighborinfo.vert1Weights[2].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert1Weights[3].shape];
            float f19 = faceShape[blockmodelrenderer$enumneighborinfo.vert1Weights[4].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert1Weights[5].shape];
            float f20 = faceShape[blockmodelrenderer$enumneighborinfo.vert1Weights[6].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert1Weights[7].shape];
            float f21 = faceShape[blockmodelrenderer$enumneighborinfo.vert2Weights[0].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert2Weights[1].shape];
            float f22 = faceShape[blockmodelrenderer$enumneighborinfo.vert2Weights[2].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert2Weights[3].shape];
            float f23 = faceShape[blockmodelrenderer$enumneighborinfo.vert2Weights[4].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert2Weights[5].shape];
            float f24 = faceShape[blockmodelrenderer$enumneighborinfo.vert2Weights[6].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert2Weights[7].shape];
            float f25 = faceShape[blockmodelrenderer$enumneighborinfo.vert3Weights[0].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert3Weights[1].shape];
            float f26 = faceShape[blockmodelrenderer$enumneighborinfo.vert3Weights[2].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert3Weights[3].shape];
            float f27 = faceShape[blockmodelrenderer$enumneighborinfo.vert3Weights[4].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert3Weights[5].shape];
            float f28 = faceShape[blockmodelrenderer$enumneighborinfo.vert3Weights[6].shape] * faceShape[blockmodelrenderer$enumneighborinfo.vert3Weights[7].shape];
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert0] = f29 * f13 + f30 * f14 + f31 * f15 + f32 * f16;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert1] = f29 * f17 + f30 * f18 + f31 * f19 + f32 * f20;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert2] = f29 * f21 + f30 * f22 + f31 * f23 + f32 * f24;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert3] = f29 * f25 + f30 * f26 + f31 * f27 + f32 * f28;
            int j2 = this.getAoBrightness(l, i, j1, i2);
            int k2 = this.getAoBrightness(k, i, i1, i2);
            int l2 = this.getAoBrightness(k, j, k1, i2);
            int i3 = this.getAoBrightness(l, j, l1, i2);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert0] = this.getVertexBrightness(j2, k2, l2, i3, f13, f14, f15, f16);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert1] = this.getVertexBrightness(j2, k2, l2, i3, f17, f18, f19, f20);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert2] = this.getVertexBrightness(j2, k2, l2, i3, f21, f22, f23, f24);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert3] = this.getVertexBrightness(j2, k2, l2, i3, f25, f26, f27, f28);
         } else {
            float f9 = (f3 + f + f5 + f8) * 0.25F;
            float f10 = (f2 + f + f4 + f8) * 0.25F;
            float f11 = (f2 + f1 + f6 + f8) * 0.25F;
            float f12 = (f3 + f1 + f7 + f8) * 0.25F;
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert0] = this.getAoBrightness(l, i, j1, i2);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert1] = this.getAoBrightness(k, i, i1, i2);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert2] = this.getAoBrightness(k, j, k1, i2);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert3] = this.getAoBrightness(l, j, l1, i2);
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert0] = f9;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert1] = f10;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert2] = f11;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert3] = f12;
         }

      }

      /**
       * Get ambient occlusion brightness
       */
      private int getAoBrightness(int br1, int br2, int br3, int br4) {
         if (br1 == 0) {
            br1 = br4;
         }

         if (br2 == 0) {
            br2 = br4;
         }

         if (br3 == 0) {
            br3 = br4;
         }

         return br1 + br2 + br3 + br4 >> 2 & 16711935;
      }

      private int getVertexBrightness(int b1, int b2, int b3, int b4, float w1, float w2, float w3, float w4) {
         int i = (int)((float)(b1 >> 16 & 255) * w1 + (float)(b2 >> 16 & 255) * w2 + (float)(b3 >> 16 & 255) * w3 + (float)(b4 >> 16 & 255) * w4) & 255;
         int j = (int)((float)(b1 & 255) * w1 + (float)(b2 & 255) * w2 + (float)(b3 & 255) * w3 + (float)(b4 & 255) * w4) & 255;
         return i << 16 | j;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum EnumNeighborInfo {
      DOWN(new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.5F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.SOUTH}),
      UP(new EnumFacing[]{EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH}, 1.0F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.SOUTH}),
      NORTH(new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST}),
      SOUTH(new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.UP}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST}),
      WEST(new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH}),
      EAST(new EnumFacing[]{EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH});

      private final EnumFacing[] corners;
      private final boolean doNonCubicWeight;
      private final BlockModelRenderer.Orientation[] vert0Weights;
      private final BlockModelRenderer.Orientation[] vert1Weights;
      private final BlockModelRenderer.Orientation[] vert2Weights;
      private final BlockModelRenderer.Orientation[] vert3Weights;
      private static final BlockModelRenderer.EnumNeighborInfo[] VALUES = Util.make(new BlockModelRenderer.EnumNeighborInfo[6], (p_209260_0_) -> {
         p_209260_0_[EnumFacing.DOWN.getIndex()] = DOWN;
         p_209260_0_[EnumFacing.UP.getIndex()] = UP;
         p_209260_0_[EnumFacing.NORTH.getIndex()] = NORTH;
         p_209260_0_[EnumFacing.SOUTH.getIndex()] = SOUTH;
         p_209260_0_[EnumFacing.WEST.getIndex()] = WEST;
         p_209260_0_[EnumFacing.EAST.getIndex()] = EAST;
      });

      private EnumNeighborInfo(EnumFacing[] cornersIn, float brightness, boolean doNonCubicWeightIn, BlockModelRenderer.Orientation[] vert0WeightsIn, BlockModelRenderer.Orientation[] vert1WeightsIn, BlockModelRenderer.Orientation[] vert2WeightsIn, BlockModelRenderer.Orientation[] vert3WeightsIn) {
         this.corners = cornersIn;
         this.doNonCubicWeight = doNonCubicWeightIn;
         this.vert0Weights = vert0WeightsIn;
         this.vert1Weights = vert1WeightsIn;
         this.vert2Weights = vert2WeightsIn;
         this.vert3Weights = vert3WeightsIn;
      }

      public static BlockModelRenderer.EnumNeighborInfo getNeighbourInfo(EnumFacing facing) {
         return VALUES[facing.getIndex()];
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Orientation {
      DOWN(EnumFacing.DOWN, false),
      UP(EnumFacing.UP, false),
      NORTH(EnumFacing.NORTH, false),
      SOUTH(EnumFacing.SOUTH, false),
      WEST(EnumFacing.WEST, false),
      EAST(EnumFacing.EAST, false),
      FLIP_DOWN(EnumFacing.DOWN, true),
      FLIP_UP(EnumFacing.UP, true),
      FLIP_NORTH(EnumFacing.NORTH, true),
      FLIP_SOUTH(EnumFacing.SOUTH, true),
      FLIP_WEST(EnumFacing.WEST, true),
      FLIP_EAST(EnumFacing.EAST, true);

      private final int shape;

      private Orientation(EnumFacing facingIn, boolean flip) {
         this.shape = facingIn.getIndex() + (flip ? EnumFacing.values().length : 0);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static enum VertexTranslations {
      DOWN(0, 1, 2, 3),
      UP(2, 3, 0, 1),
      NORTH(3, 0, 1, 2),
      SOUTH(0, 1, 2, 3),
      WEST(3, 0, 1, 2),
      EAST(1, 2, 3, 0);

      private final int vert0;
      private final int vert1;
      private final int vert2;
      private final int vert3;
      private static final BlockModelRenderer.VertexTranslations[] VALUES = Util.make(new BlockModelRenderer.VertexTranslations[6], (p_209261_0_) -> {
         p_209261_0_[EnumFacing.DOWN.getIndex()] = DOWN;
         p_209261_0_[EnumFacing.UP.getIndex()] = UP;
         p_209261_0_[EnumFacing.NORTH.getIndex()] = NORTH;
         p_209261_0_[EnumFacing.SOUTH.getIndex()] = SOUTH;
         p_209261_0_[EnumFacing.WEST.getIndex()] = WEST;
         p_209261_0_[EnumFacing.EAST.getIndex()] = EAST;
      });

      private VertexTranslations(int vert0In, int vert1In, int vert2In, int vert3In) {
         this.vert0 = vert0In;
         this.vert1 = vert1In;
         this.vert2 = vert2In;
         this.vert3 = vert3In;
      }

      public static BlockModelRenderer.VertexTranslations getVertexTranslations(EnumFacing facingIn) {
         return VALUES[facingIn.getIndex()];
      }
   }
}