package net.minecraft.pathfinding;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldEventListener;

public class PathWorldListener implements IWorldEventListener {
   private final List<PathNavigate> navigations = Lists.newArrayList();

   public void notifyBlockUpdate(IBlockReader worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
      if (this.didBlockChange(worldIn, pos, oldState, newState)) {
         int i = 0;

         for(int j = this.navigations.size(); i < j; ++i) {
            PathNavigate pathnavigate = this.navigations.get(i);
            if (pathnavigate != null && !pathnavigate.canUpdatePathOnTimeout()) {
               Path path = pathnavigate.getPath();
               if (path != null && !path.isFinished() && path.getCurrentPathLength() != 0) {
                  PathPoint pathpoint = pathnavigate.currentPath.getFinalPathPoint();
                  double d0 = pos.distanceSq(((double)pathpoint.x + pathnavigate.entity.posX) / 2.0D, ((double)pathpoint.y + pathnavigate.entity.posY) / 2.0D, ((double)pathpoint.z + pathnavigate.entity.posZ) / 2.0D);
                  int k = (path.getCurrentPathLength() - path.getCurrentPathIndex()) * (path.getCurrentPathLength() - path.getCurrentPathIndex());
                  if (d0 < (double)k) {
                     pathnavigate.updatePath();
                  }
               }
            }
         }

      }
   }

   protected boolean didBlockChange(IBlockReader worldIn, BlockPos pos, IBlockState oldState, IBlockState newState) {
      VoxelShape voxelshape = oldState.getCollisionShape(worldIn, pos);
      VoxelShape voxelshape1 = newState.getCollisionShape(worldIn, pos);
      return VoxelShapes.compare(voxelshape, voxelshape1, IBooleanFunction.NOT_SAME);
   }

   public void notifyLightSet(BlockPos pos) {
   }

   /**
    * On the client, re-renders all blocks in this range, inclusive. On the server, does nothing.
    */
   public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
   }

   public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {
   }

   public void addParticle(IParticleData particleData, boolean alwaysRender, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
   }

   public void addParticle(IParticleData particleData, boolean ignoreRange, boolean minimizeLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
   }

   /**
    * Called on all IWorldAccesses when an entity is created or loaded. On client worlds, starts downloading any
    * necessary textures. On server worlds, adds the entity to the entity tracker.
    */
   public void onEntityAdded(Entity entityIn) {
      if (entityIn instanceof EntityLiving) {
         this.navigations.add(((EntityLiving)entityIn).getNavigator());
      }

   }

   /**
    * Called on all IWorldAccesses when an entity is unloaded or destroyed. On client worlds, releases any downloaded
    * textures. On server worlds, removes the entity from the entity tracker.
    */
   public void onEntityRemoved(Entity entityIn) {
      if (entityIn instanceof EntityLiving) {
         this.navigations.remove(((EntityLiving)entityIn).getNavigator());
      }

   }

   public void playRecord(SoundEvent soundIn, BlockPos pos) {
   }

   public void broadcastSound(int soundID, BlockPos pos, int data) {
   }

   public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {
   }

   public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
   }
}