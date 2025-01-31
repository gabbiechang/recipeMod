package net.minecraft.world.chunk.storage;

import com.google.common.collect.Lists;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import javax.annotation.Nullable;
import net.minecraft.util.Util;

public class RegionFile {
   // Minecraft is limited to 256 sections per chunk. So 1MB. This can easily be override.
   // So we extend this to use the REAL size when the count is maxed by seeking to that section and reading the length.
   private static final boolean FORGE_ENABLE_EXTENDED_SAVE = Boolean.parseBoolean(System.getProperty("forge.enableExtendedSave", "true"));
   private static final org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger();
   private static final byte[] EMPTY_SECTOR = new byte[4096];
   private final File fileName;
   private RandomAccessFile dataFile;
   private final int[] offsets = new int[1024];
   private final int[] chunkTimestamps = new int[1024];
   private List<Boolean> sectorFree;
   /** McRegion sizeDelta */
   private int sizeDelta;
   private long lastModified;

   public RegionFile(File fileNameIn) {
      this.fileName = fileNameIn;
      this.sizeDelta = 0;

      try {
         if (fileNameIn.exists()) {
            this.lastModified = fileNameIn.lastModified();
         }

         this.dataFile = new RandomAccessFile(fileNameIn, "rw");
         if (this.dataFile.length() < 4096L) {
            this.dataFile.write(EMPTY_SECTOR);
            this.dataFile.write(EMPTY_SECTOR);
            this.sizeDelta += 8192;
         }

         if ((this.dataFile.length() & 4095L) != 0L) {
            for(int i = 0; (long)i < (this.dataFile.length() & 4095L); ++i) {
               this.dataFile.write(0);
            }
         }

         int i1 = (int)this.dataFile.length() / 4096;
         this.sectorFree = Lists.newArrayListWithCapacity(i1);

         for(int j = 0; j < i1; ++j) {
            this.sectorFree.add(true);
         }

         this.sectorFree.set(0, false);
         this.sectorFree.set(1, false);
         this.dataFile.seek(0L);

         for(int j1 = 0; j1 < 1024; ++j1) {
            int k = this.dataFile.readInt();
            this.offsets[j1] = k;
            int length = k & 255;
            if (length == 255) {
               if ((k >> 8) <= this.sectorFree.size()) { // We're maxed out, so we need to read the proper length from the section
                  this.dataFile.seek((k >> 8) * 4096);
                  length = (this.dataFile.readInt() + 4) / 4096 + 1;
                  this.dataFile.seek(j1 * 4 + 4); //Go back to where we were
               }
            }
            if (k != 0 && (k >> 8) + length <= this.sectorFree.size()) {
               for (int l = 0; l < length; ++l) {
                  this.sectorFree.set((k >> 8) + l, false);
               }
            }
            else if (length > 0)
               LOGGER.warn("Invalid chunk: ({}, {}) Offset: {} Length: {} runs off end file. {}", j1 % 32, (int)(j1 / 32), k >> 8, length, fileNameIn);
         }

         for(int k1 = 0; k1 < 1024; ++k1) {
            int l1 = this.dataFile.readInt();
            this.chunkTimestamps[k1] = l1;
         }
      } catch (IOException ioexception) {
         ioexception.printStackTrace();
      }

   }

   /**
    * Returns an uncompressed chunk stream from the region file.
    */
   @Nullable
   public synchronized DataInputStream getChunkDataInputStream(int x, int z) {
      if (this.outOfBounds(x, z)) {
         return null;
      } else {
         try {
            int i = this.getOffset(x, z);
            if (i == 0) {
               return null;
            } else {
               int j = i >> 8;
               int k = i & 255;
               if (k == 255) {
                  this.dataFile.seek(j * 4096);
                  k = (this.dataFile.readInt() + 4) / 4096 + 1;
               }
               if (j + k > this.sectorFree.size()) {
                  return null;
               } else {
                  this.dataFile.seek((long)(j * 4096));
                  int l = this.dataFile.readInt();
                  if (l > 4096 * k) {
                     LOGGER.warn("Invalid chunk: ({}, {}) Offset: {} Invalid Size: {}>{} {}", x, z, j, l, k * 4096, fileName);
                     return null;
                  } else if (l <= 0) {
                     LOGGER.warn("Invalid chunk: ({}, {}) Offset: {} Invalid Size: {} {}", x, z, j, l, fileName);
                     return null;
                  } else {
                     byte b0 = this.dataFile.readByte();
                     if (b0 == 1) {
                        byte[] abyte1 = new byte[l - 1];
                        this.dataFile.read(abyte1);
                        return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(abyte1))));
                     } else if (b0 == 2) {
                        byte[] abyte = new byte[l - 1];
                        this.dataFile.read(abyte);
                        return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(abyte))));
                     } else {
                        return null;
                     }
                  }
               }
            }
         } catch (IOException var9) {
            return null;
         }
      }
   }

   public boolean doesChunkExist(int p_212167_1_, int p_212167_2_) {
      if (this.outOfBounds(p_212167_1_, p_212167_2_)) {
         return false;
      } else {
         int i = this.getOffset(p_212167_1_, p_212167_2_);
         if (i == 0) {
            return false;
         } else {
            int j = i >> 8;
            int k = i & 255;
            if (j + k > this.sectorFree.size()) {
               return false;
            } else {
               try {
                  this.dataFile.seek((long)(j * 4096));
                  int l = this.dataFile.readInt();
                  if (l > 4096 * k) {
                     return false;
                  } else {
                     return l > 0;
                  }
               } catch (IOException var7) {
                  return false;
               }
            }
         }
      }
   }

   /**
    * Returns an output stream used to write chunk data. Data is on disk when the returned stream is closed.
    */
   @Nullable
   public DataOutputStream getChunkDataOutputStream(int x, int z) {
      return this.outOfBounds(x, z) ? null : new DataOutputStream(new BufferedOutputStream(new DeflaterOutputStream(new RegionFile.ChunkBuffer(x, z))));
   }

   /**
    * Writes the specified chunk to disk.
    */
   protected synchronized void write(int x, int z, byte[] data, int length) {
      try {
         int i = this.getOffset(x, z);
         int j = i >> 8;
         int k = i & 255;
         if (k == 255) {
             this.dataFile.seek(j * 4096);
             k = (this.dataFile.readInt() + 4) / 4096 + 1;
         }
         int l = (length + 5) / 4096 + 1;
         if (l >= 256) {
            if (!FORGE_ENABLE_EXTENDED_SAVE) return;
            LOGGER.warn("Large Chunk Detected: ({}, {}) Size: {} {}", x, z, l, fileName);
         }

         if (j != 0 && k == l) {
            this.write(j, data, length);
         } else {
            for(int i1 = 0; i1 < k; ++i1) {
               this.sectorFree.set(j + i1, true);
            }

            int l1 = this.sectorFree.indexOf(true);
            int j1 = 0;
            if (l1 != -1) {
               for(int k1 = l1; k1 < this.sectorFree.size(); ++k1) {
                  if (j1 != 0) {
                     if (this.sectorFree.get(k1)) {
                        ++j1;
                     } else {
                        j1 = 0;
                     }
                  } else if (this.sectorFree.get(k1)) {
                     l1 = k1;
                     j1 = 1;
                  }

                  if (j1 >= l) {
                     break;
                  }
               }
            }

            if (j1 >= l) {
               j = l1;
               this.setOffset(x, z, l1 << 8 | (l > 255 ? 255 : l));

               for(int j2 = 0; j2 < l; ++j2) {
                  this.sectorFree.set(j + j2, false);
               }

               this.write(j, data, length);
            } else {
               this.dataFile.seek(this.dataFile.length());
               j = this.sectorFree.size();

               for(int i2 = 0; i2 < l; ++i2) {
                  this.dataFile.write(EMPTY_SECTOR);
                  this.sectorFree.add(false);
               }

               this.sizeDelta += 4096 * l;
               this.write(j, data, length);
               this.setOffset(x, z, j << 8 | (l > 255 ? 255 : l));
            }
         }

         this.setChunkTimestamp(x, z, (int)(Util.millisecondsSinceEpoch() / 1000L));
      } catch (IOException ioexception) {
         ioexception.printStackTrace();
      }

   }

   /**
    * Writes the chunk data to this RegionFile.
    */
   private void write(int sectorNumber, byte[] data, int length) throws IOException {
      this.dataFile.seek((long)(sectorNumber * 4096));
      this.dataFile.writeInt(length + 1);
      this.dataFile.writeByte(2);
      this.dataFile.write(data, 0, length);
   }

   /**
    * Checks if region is out of bounds.
    */
   private boolean outOfBounds(int x, int z) {
      return x < 0 || x >= 32 || z < 0 || z >= 32;
   }

   /**
    * Gets a chunk's offset in region file.
    */
   private int getOffset(int x, int z) {
      return this.offsets[x + z * 32];
   }

   /**
    * Checks if a chunk has been saved.
    */
   public boolean isChunkSaved(int x, int z) {
      return this.getOffset(x, z) != 0;
   }

   /**
    * Sets the chunk's offset in the region file.
    */
   private void setOffset(int x, int z, int offset) throws IOException {
      this.offsets[x + z * 32] = offset;
      this.dataFile.seek((long)((x + z * 32) * 4));
      this.dataFile.writeInt(offset);
   }

   /**
    * Updates the specified chunk's write timestamp.
    */
   private void setChunkTimestamp(int x, int z, int timestamp) throws IOException {
      this.chunkTimestamps[x + z * 32] = timestamp;
      this.dataFile.seek((long)(4096 + (x + z * 32) * 4));
      this.dataFile.writeInt(timestamp);
   }

   /**
    * close this RegionFile and prevent further writes
    */
   public void close() throws IOException {
      if (this.dataFile != null) {
         this.dataFile.close();
      }

   }

   class ChunkBuffer extends ByteArrayOutputStream {
      private final int chunkX;
      private final int chunkZ;

      public ChunkBuffer(int x, int z) {
         super(8096);
         this.chunkX = x;
         this.chunkZ = z;
      }

      public void close() {
         RegionFile.this.write(this.chunkX, this.chunkZ, this.buf, this.count);
      }
   }
}