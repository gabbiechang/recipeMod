package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.concurrent.Immutable;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Immutable
public class BlockPos extends Vec3i {
   private static final Logger LOGGER = LogManager.getLogger();
   /** An immutable block pos with zero as all coordinates. */
   public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);
   private static final int NUM_X_BITS = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
   private static final int NUM_Z_BITS = NUM_X_BITS;
   private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
   private static final int Y_SHIFT = 0 + NUM_Z_BITS;
   private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;
   private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
   private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
   private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;

   public BlockPos(int x, int y, int z) {
      super(x, y, z);
   }

   public BlockPos(double x, double y, double z) {
      super(x, y, z);
   }

   public BlockPos(Entity source) {
      this(source.posX, source.posY, source.posZ);
   }

   public BlockPos(Vec3d vec) {
      this(vec.x, vec.y, vec.z);
   }

   public BlockPos(Vec3i source) {
      this(source.getX(), source.getY(), source.getZ());
   }

   /**
    * Add the given coordinates to the coordinates of this BlockPos
    */
   public BlockPos add(double x, double y, double z) {
      return x == 0.0D && y == 0.0D && z == 0.0D ? this : new BlockPos((double)this.getX() + x, (double)this.getY() + y, (double)this.getZ() + z);
   }

   /**
    * Add the given coordinates to the coordinates of this BlockPos
    */
   public BlockPos add(int x, int y, int z) {
      return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
   }

   /**
    * Add the given Vector to this BlockPos
    */
   public BlockPos add(Vec3i vec) {
      return this.add(vec.getX(), vec.getY(), vec.getZ());
   }

   /**
    * Subtract the given Vector from this BlockPos
    */
   public BlockPos subtract(Vec3i vec) {
      return this.add(-vec.getX(), -vec.getY(), -vec.getZ());
   }

   /**
    * Offset this BlockPos 1 block up
    */
   public BlockPos up() {
      return this.up(1);
   }

   /**
    * Offset this BlockPos n blocks up
    */
   public BlockPos up(int n) {
      return this.offset(EnumFacing.UP, n);
   }

   /**
    * Offset this BlockPos 1 block down
    */
   public BlockPos down() {
      return this.down(1);
   }

   /**
    * Offset this BlockPos n blocks down
    */
   public BlockPos down(int n) {
      return this.offset(EnumFacing.DOWN, n);
   }

   /**
    * Offset this BlockPos 1 block in northern direction
    */
   public BlockPos north() {
      return this.north(1);
   }

   /**
    * Offset this BlockPos n blocks in northern direction
    */
   public BlockPos north(int n) {
      return this.offset(EnumFacing.NORTH, n);
   }

   /**
    * Offset this BlockPos 1 block in southern direction
    */
   public BlockPos south() {
      return this.south(1);
   }

   /**
    * Offset this BlockPos n blocks in southern direction
    */
   public BlockPos south(int n) {
      return this.offset(EnumFacing.SOUTH, n);
   }

   /**
    * Offset this BlockPos 1 block in western direction
    */
   public BlockPos west() {
      return this.west(1);
   }

   /**
    * Offset this BlockPos n blocks in western direction
    */
   public BlockPos west(int n) {
      return this.offset(EnumFacing.WEST, n);
   }

   /**
    * Offset this BlockPos 1 block in eastern direction
    */
   public BlockPos east() {
      return this.east(1);
   }

   /**
    * Offset this BlockPos n blocks in eastern direction
    */
   public BlockPos east(int n) {
      return this.offset(EnumFacing.EAST, n);
   }

   /**
    * Offset this BlockPos 1 block in the given direction
    */
   public BlockPos offset(EnumFacing facing) {
      return this.offset(facing, 1);
   }

   /**
    * Offsets this BlockPos n blocks in the given direction
    */
   public BlockPos offset(EnumFacing facing, int n) {
      return n == 0 ? this : new BlockPos(this.getX() + facing.getXOffset() * n, this.getY() + facing.getYOffset() * n, this.getZ() + facing.getZOffset() * n);
   }

   public BlockPos rotate(Rotation rotationIn) {
      switch(rotationIn) {
      case NONE:
      default:
         return this;
      case CLOCKWISE_90:
         return new BlockPos(-this.getZ(), this.getY(), this.getX());
      case CLOCKWISE_180:
         return new BlockPos(-this.getX(), this.getY(), -this.getZ());
      case COUNTERCLOCKWISE_90:
         return new BlockPos(this.getZ(), this.getY(), -this.getX());
      }
   }

   /**
    * Calculate the cross product of this and the given Vector
    */
   public BlockPos crossProduct(Vec3i vec) {
      return new BlockPos(this.getY() * vec.getZ() - this.getZ() * vec.getY(), this.getZ() * vec.getX() - this.getX() * vec.getZ(), this.getX() * vec.getY() - this.getY() * vec.getX());
   }

   /**
    * Serialize this BlockPos into a long value
    */
   public long toLong() {
      return ((long)this.getX() & X_MASK) << X_SHIFT | ((long)this.getY() & Y_MASK) << Y_SHIFT | ((long)this.getZ() & Z_MASK) << 0;
   }

   /**
    * Create a BlockPos from a serialized long value (created by toLong)
    */
   public static BlockPos fromLong(long serialized) {
      int i = (int)(serialized << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
      int j = (int)(serialized << 64 - Y_SHIFT - NUM_Y_BITS >> 64 - NUM_Y_BITS);
      int k = (int)(serialized << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
      return new BlockPos(i, j, k);
   }

   /**
    * Create an Iterable that returns all positions in the box specified by the given corners. There is no requirement
    * that one corner is greater than the other; individual coordinates will be swapped as needed.
    *  
    * In situations where it is usable, prefer {@link #getAllInBoxMutable(BlockPos, BlockPos}) instead as it has better
    * performance (fewer allocations)
    *  
    * @see #getAllInBox(int, int, int, int, int, int)
    * @see #getAllInBoxMutable(BlockPos, BlockPos)
    * @see #mutablesBetween(int, int, int, int, int, int)
    */
   public static Iterable<BlockPos> getAllInBox(BlockPos from, BlockPos to) {
      return getAllInBox(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()), Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
   }

   /**
    * Create an Iterable that returns all positions in the box specified by the coordinates. <strong>Coordinates must be
    * in order</strong>; e.g. x1 <= x2.
    *  
    * In situations where it is usable, prefer {@link #getAllInBoxMutable(BlockPos, BlockPos}) instead as it has better
    * performance (fewer allocations)
    *  
    * @see #getAllInBox(BlockPos, BlockPos)
    * @see #getAllInBoxMutable(BlockPos, BlockPos)
    * @see #mutablesBetween(int, int, int, int, int, int)
    */
   public static Iterable<BlockPos> getAllInBox(int x1, int y1, int z1, int x2, int y2, int z2) {
      return () -> {
         return new AbstractIterator<BlockPos>() {
            private boolean first = true;
            private int lastX;
            private int lastY;
            private int lastZ;

            protected BlockPos computeNext() {
               if (this.first) {
                  this.first = false;
                  this.lastX = x1;
                  this.lastY = y1;
                  this.lastZ = z1;
                  return new BlockPos(x1, y1, z1);
               } else if (this.lastX == x2 && this.lastY == y2 && this.lastZ == z2) {
                  return this.endOfData();
               } else {
                  if (this.lastX < x2) {
                     ++this.lastX;
                  } else if (this.lastY < y2) {
                     this.lastX = x1;
                     ++this.lastY;
                  } else if (this.lastZ < z2) {
                     this.lastX = x1;
                     this.lastY = y1;
                     ++this.lastZ;
                  }

                  return new BlockPos(this.lastX, this.lastY, this.lastZ);
               }
            }
         };
      };
   }

   /**
    * Returns a version of this BlockPos that is guaranteed to be immutable.
    *  
    * <p>When storing a BlockPos given to you for an extended period of time, make sure you
    * use this in case the value is changed internally.</p>
    */
   public BlockPos toImmutable() {
      return this;
   }

   /**
    * Creates an Iterable that returns all positions in the box specified by the given corners. There is no requirement
    * that one corner is greater than the other; individual coordinates will be swapped as needed.
    *  
    * This method uses {@link BlockPos.MutableBlockPos MutableBlockPos} instead of regular BlockPos, which grants better
    * performance. However, the resulting BlockPos instances can only be used inside the iteration loop (as otherwise
    * the value will change), unless {@link #toImmutable()} is called. This method is ideal for searching large areas
    * and only storing a few locations.
    *  
    * @see #getAllInBox(BlockPos, BlockPos)
    * @see #getAllInBox(int, int, int, int, int, int)
    * @see #getAllInBoxMutable(BlockPos, BlockPos)
    * @see #mutablesBetween(int, int, int, int, int, int)
    */
   public static Iterable<BlockPos.MutableBlockPos> getAllInBoxMutable(BlockPos from, BlockPos to) {
      return getAllInBoxMutable(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()), Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
   }

   /**
    * Creates an Iterable that returns all positions in the box specified by the given corners. <strong>Coordinates must
    * be in order</strong>; e.g. x1 <= x2.
    *  
    * This method uses {@link BlockPos.MutableBlockPos MutableBlockPos} instead of regular BlockPos, which grants better
    * performance. However, the resulting BlockPos instances can only be used inside the iteration loop (as otherwise
    * the value will change), unless {@link #toImmutable()} is called. This method is ideal for searching large areas
    * and only storing a few locations.
    *  
    * @see #getAllInBox(BlockPos, BlockPos)
    * @see #getAllInBox(int, int, int, int, int, int)
    * @see #getAllInBoxMutable(BlockPos, BlockPos)
    */
   public static Iterable<BlockPos.MutableBlockPos> getAllInBoxMutable(int x1, int y1, int z1, int x2, int y2, int z2) {
      return () -> {
         return new AbstractIterator<BlockPos.MutableBlockPos>() {
            private BlockPos.MutableBlockPos pos;

            protected BlockPos.MutableBlockPos computeNext() {
               if (this.pos == null) {
                  this.pos = new BlockPos.MutableBlockPos(x1, y1, z1);
                  return this.pos;
               } else if (this.pos.x == x2 && this.pos.y == y2 && this.pos.z == z2) {
                  return this.endOfData();
               } else {
                  if (this.pos.x < x2) {
                     ++this.pos.x;
                  } else if (this.pos.y < y2) {
                     this.pos.x = x1;
                     ++this.pos.y;
                  } else if (this.pos.z < z2) {
                     this.pos.x = x1;
                     this.pos.y = y1;
                     ++this.pos.z;
                  }

                  return this.pos;
               }
            }
         };
      };
   }

   public static class MutableBlockPos extends BlockPos {
      /** Mutable X Coordinate */
      protected int x;
      /** Mutable Y Coordinate */
      protected int y;
      /** Mutable Z Coordinate */
      protected int z;

      public MutableBlockPos() {
         this(0, 0, 0);
      }

      public MutableBlockPos(BlockPos pos) {
         this(pos.getX(), pos.getY(), pos.getZ());
      }

      public MutableBlockPos(int x_, int y_, int z_) {
         super(0, 0, 0);
         this.x = x_;
         this.y = y_;
         this.z = z_;
      }

      /**
       * Add the given coordinates to the coordinates of this BlockPos
       */
      public BlockPos add(double x, double y, double z) {
         return super.add(x, y, z).toImmutable();
      }

      /**
       * Add the given coordinates to the coordinates of this BlockPos
       */
      public BlockPos add(int x, int y, int z) {
         return super.add(x, y, z).toImmutable();
      }

      /**
       * Offsets this BlockPos n blocks in the given direction
       */
      public BlockPos offset(EnumFacing facing, int n) {
         return super.offset(facing, n).toImmutable();
      }

      public BlockPos rotate(Rotation rotationIn) {
         return super.rotate(rotationIn).toImmutable();
      }

      /**
       * Gets the X coordinate.
       */
      public int getX() {
         return this.x;
      }

      /**
       * Gets the Y coordinate.
       */
      public int getY() {
         return this.y;
      }

      /**
       * Gets the Z coordinate.
       */
      public int getZ() {
         return this.z;
      }

      /**
       * None
       */
      public BlockPos.MutableBlockPos setPos(int xIn, int yIn, int zIn) {
         this.x = xIn;
         this.y = yIn;
         this.z = zIn;
         return this;
      }

      @OnlyIn(Dist.CLIENT)
      public BlockPos.MutableBlockPos setPos(Entity entityIn) {
         return this.setPos(entityIn.posX, entityIn.posY, entityIn.posZ);
      }

      public BlockPos.MutableBlockPos setPos(double xIn, double yIn, double zIn) {
         return this.setPos(MathHelper.floor(xIn), MathHelper.floor(yIn), MathHelper.floor(zIn));
      }

      public BlockPos.MutableBlockPos setPos(Vec3i vec) {
         return this.setPos(vec.getX(), vec.getY(), vec.getZ());
      }

      public BlockPos.MutableBlockPos move(EnumFacing facing) {
         return this.move(facing, 1);
      }

      public BlockPos.MutableBlockPos move(EnumFacing facing, int n) {
         return this.setPos(this.x + facing.getXOffset() * n, this.y + facing.getYOffset() * n, this.z + facing.getZOffset() * n);
      }

      public BlockPos.MutableBlockPos move(int xIn, int yIn, int zIn) {
         return this.setPos(this.x + xIn, this.y + yIn, this.z + zIn);
      }

      public void setY(int yIn) {
         this.y = yIn;
      }

      /**
       * Returns a version of this BlockPos that is guaranteed to be immutable.
       *  
       * <p>When storing a BlockPos given to you for an extended period of time, make sure you
       * use this in case the value is changed internally.</p>
       */
      public BlockPos toImmutable() {
         return new BlockPos(this);
      }
   }

   public static final class PooledMutableBlockPos extends BlockPos.MutableBlockPos implements AutoCloseable {
      private boolean released;
      private static final List<BlockPos.PooledMutableBlockPos> POOL = Lists.newArrayList();

      private PooledMutableBlockPos(int xIn, int yIn, int zIn) {
         super(xIn, yIn, zIn);
      }

      public static BlockPos.PooledMutableBlockPos retain() {
         return retain(0, 0, 0);
      }

      public static BlockPos.PooledMutableBlockPos retain(Entity entityIn) {
         return retain(entityIn.posX, entityIn.posY, entityIn.posZ);
      }

      public static BlockPos.PooledMutableBlockPos retain(double xIn, double yIn, double zIn) {
         return retain(MathHelper.floor(xIn), MathHelper.floor(yIn), MathHelper.floor(zIn));
      }

      public static BlockPos.PooledMutableBlockPos retain(int xIn, int yIn, int zIn) {
         synchronized(POOL) {
            if (!POOL.isEmpty()) {
               BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = POOL.remove(POOL.size() - 1);
               if (blockpos$pooledmutableblockpos != null && blockpos$pooledmutableblockpos.released) {
                  blockpos$pooledmutableblockpos.released = false;
                  blockpos$pooledmutableblockpos.setPos(xIn, yIn, zIn);
                  return blockpos$pooledmutableblockpos;
               }
            }
         }

         return new BlockPos.PooledMutableBlockPos(xIn, yIn, zIn);
      }

      /**
       * None
       */
      public BlockPos.PooledMutableBlockPos setPos(int xIn, int yIn, int zIn) {
         return (BlockPos.PooledMutableBlockPos)super.setPos(xIn, yIn, zIn);
      }

      @OnlyIn(Dist.CLIENT)
      public BlockPos.PooledMutableBlockPos setPos(Entity entityIn) {
         return (BlockPos.PooledMutableBlockPos)super.setPos(entityIn);
      }

      public BlockPos.PooledMutableBlockPos setPos(double xIn, double yIn, double zIn) {
         return (BlockPos.PooledMutableBlockPos)super.setPos(xIn, yIn, zIn);
      }

      public BlockPos.PooledMutableBlockPos setPos(Vec3i vec) {
         return (BlockPos.PooledMutableBlockPos)super.setPos(vec);
      }

      public BlockPos.PooledMutableBlockPos move(EnumFacing facing) {
         return (BlockPos.PooledMutableBlockPos)super.move(facing);
      }

      public BlockPos.PooledMutableBlockPos move(EnumFacing facing, int n) {
         return (BlockPos.PooledMutableBlockPos)super.move(facing, n);
      }

      public BlockPos.PooledMutableBlockPos move(int xIn, int yIn, int zIn) {
         return (BlockPos.PooledMutableBlockPos)super.move(xIn, yIn, zIn);
      }

      public void close() {
         synchronized(POOL) {
            if (POOL.size() < 100) {
               POOL.add(this);
            }

            this.released = true;
         }
      }
   }
}