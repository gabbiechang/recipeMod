package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketPlayerPosLook implements Packet<INetHandlerPlayClient> {
   private double x;
   private double y;
   private double z;
   private float yaw;
   private float pitch;
   private Set<SPacketPlayerPosLook.EnumFlags> flags;
   private int teleportId;

   public SPacketPlayerPosLook() {
   }

   public SPacketPlayerPosLook(double xIn, double yIn, double zIn, float yawIn, float pitchIn, Set<SPacketPlayerPosLook.EnumFlags> flagsIn, int teleportIdIn) {
      this.x = xIn;
      this.y = yIn;
      this.z = zIn;
      this.yaw = yawIn;
      this.pitch = pitchIn;
      this.flags = flagsIn;
      this.teleportId = teleportIdIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.x = buf.readDouble();
      this.y = buf.readDouble();
      this.z = buf.readDouble();
      this.yaw = buf.readFloat();
      this.pitch = buf.readFloat();
      this.flags = SPacketPlayerPosLook.EnumFlags.unpack(buf.readUnsignedByte());
      this.teleportId = buf.readVarInt();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
      buf.writeFloat(this.yaw);
      buf.writeFloat(this.pitch);
      buf.writeByte(SPacketPlayerPosLook.EnumFlags.pack(this.flags));
      buf.writeVarInt(this.teleportId);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(INetHandlerPlayClient handler) {
      handler.handlePlayerPosLook(this);
   }

   @OnlyIn(Dist.CLIENT)
   public double getX() {
      return this.x;
   }

   @OnlyIn(Dist.CLIENT)
   public double getY() {
      return this.y;
   }

   @OnlyIn(Dist.CLIENT)
   public double getZ() {
      return this.z;
   }

   @OnlyIn(Dist.CLIENT)
   public float getYaw() {
      return this.yaw;
   }

   @OnlyIn(Dist.CLIENT)
   public float getPitch() {
      return this.pitch;
   }

   @OnlyIn(Dist.CLIENT)
   public int getTeleportId() {
      return this.teleportId;
   }

   /**
    * Returns a set of which fields are relative. Items in this set indicate that the value is a relative change applied
    * to the player's position, rather than an exact value.
    */
   @OnlyIn(Dist.CLIENT)
   public Set<SPacketPlayerPosLook.EnumFlags> getFlags() {
      return this.flags;
   }

   public static enum EnumFlags {
      X(0),
      Y(1),
      Z(2),
      Y_ROT(3),
      X_ROT(4);

      private final int bit;

      private EnumFlags(int bitIn) {
         this.bit = bitIn;
      }

      private int getMask() {
         return 1 << this.bit;
      }

      /**
       * Checks if this flag is set within the given set of flags.
       */
      private boolean isSet(int flags) {
         return (flags & this.getMask()) == this.getMask();
      }

      public static Set<SPacketPlayerPosLook.EnumFlags> unpack(int flags) {
         Set<SPacketPlayerPosLook.EnumFlags> set = EnumSet.noneOf(SPacketPlayerPosLook.EnumFlags.class);

         for(SPacketPlayerPosLook.EnumFlags spacketplayerposlook$enumflags : values()) {
            if (spacketplayerposlook$enumflags.isSet(flags)) {
               set.add(spacketplayerposlook$enumflags);
            }
         }

         return set;
      }

      public static int pack(Set<SPacketPlayerPosLook.EnumFlags> flags) {
         int i = 0;

         for(SPacketPlayerPosLook.EnumFlags spacketplayerposlook$enumflags : flags) {
            i |= spacketplayerposlook$enumflags.getMask();
         }

         return i;
      }
   }
}