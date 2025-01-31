package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;

public class MegaJungleFeature extends HugeTreesFeature<NoFeatureConfig> {
   public MegaJungleFeature(boolean notify, int baseHeightIn, int extraRandomHeightIn, IBlockState woodMetadataIn, IBlockState p_i46448_5_) {
      super(notify, baseHeightIn, extraRandomHeightIn, woodMetadataIn, p_i46448_5_);
   }

   public boolean place(Set<BlockPos> changedBlocks, IWorld worldIn, Random rand, BlockPos position) {
      int i = this.getHeight(rand);
      if (!this.func_203427_a(worldIn, position, i)) {
         return false;
      } else {
         this.func_202408_c(worldIn, position.up(i), 2);

         for(int j = position.getY() + i - 2 - rand.nextInt(4); j > position.getY() + i / 2; j -= 2 + rand.nextInt(4)) {
            float f = rand.nextFloat() * ((float)Math.PI * 2F);
            int k = position.getX() + (int)(0.5F + MathHelper.cos(f) * 4.0F);
            int l = position.getZ() + (int)(0.5F + MathHelper.sin(f) * 4.0F);

            for(int i1 = 0; i1 < 5; ++i1) {
               k = position.getX() + (int)(1.5F + MathHelper.cos(f) * (float)i1);
               l = position.getZ() + (int)(1.5F + MathHelper.sin(f) * (float)i1);
               this.func_208520_a(changedBlocks, worldIn, new BlockPos(k, j - 3 + i1 / 2, l), this.woodMetadata);
            }

            int j2 = 1 + rand.nextInt(2);
            int j1 = j;

            for(int k1 = j - j2; k1 <= j1; ++k1) {
               int l1 = k1 - j1;
               this.growLeavesLayer(worldIn, new BlockPos(k, k1, l), 1 - l1);
            }
         }

         for(int i2 = 0; i2 < i; ++i2) {
            BlockPos blockpos = position.up(i2);
            if (this.canGrowInto(worldIn, blockpos)) {
               this.func_208520_a(changedBlocks, worldIn, blockpos, this.woodMetadata);
               if (i2 > 0) {
                  this.func_202407_a(worldIn, rand, blockpos.west(), BlockVine.EAST);
                  this.func_202407_a(worldIn, rand, blockpos.north(), BlockVine.SOUTH);
               }
            }

            if (i2 < i - 1) {
               BlockPos blockpos1 = blockpos.east();
               if (this.canGrowInto(worldIn, blockpos1)) {
                  this.func_208520_a(changedBlocks, worldIn, blockpos1, this.woodMetadata);
                  if (i2 > 0) {
                     this.func_202407_a(worldIn, rand, blockpos1.east(), BlockVine.WEST);
                     this.func_202407_a(worldIn, rand, blockpos1.north(), BlockVine.SOUTH);
                  }
               }

               BlockPos blockpos2 = blockpos.south().east();
               if (this.canGrowInto(worldIn, blockpos2)) {
                  this.func_208520_a(changedBlocks, worldIn, blockpos2, this.woodMetadata);
                  if (i2 > 0) {
                     this.func_202407_a(worldIn, rand, blockpos2.east(), BlockVine.WEST);
                     this.func_202407_a(worldIn, rand, blockpos2.south(), BlockVine.NORTH);
                  }
               }

               BlockPos blockpos3 = blockpos.south();
               if (this.canGrowInto(worldIn, blockpos3)) {
                  this.func_208520_a(changedBlocks, worldIn, blockpos3, this.woodMetadata);
                  if (i2 > 0) {
                     this.func_202407_a(worldIn, rand, blockpos3.west(), BlockVine.EAST);
                     this.func_202407_a(worldIn, rand, blockpos3.south(), BlockVine.NORTH);
                  }
               }
            }
         }

         return true;
      }
   }

   private void func_202407_a(IWorld p_202407_1_, Random p_202407_2_, BlockPos p_202407_3_, BooleanProperty p_202407_4_) {
      if (p_202407_2_.nextInt(3) > 0 && p_202407_1_.isAirBlock(p_202407_3_)) {
         this.setBlockState(p_202407_1_, p_202407_3_, Blocks.VINE.getDefaultState().with(p_202407_4_, Boolean.valueOf(true)));
      }

   }

   private void func_202408_c(IWorld p_202408_1_, BlockPos p_202408_2_, int p_202408_3_) {
      int i = 2;

      for(int j = -2; j <= 0; ++j) {
         this.growLeavesLayerStrict(p_202408_1_, p_202408_2_.up(j), p_202408_3_ + 1 - j);
      }

   }
}