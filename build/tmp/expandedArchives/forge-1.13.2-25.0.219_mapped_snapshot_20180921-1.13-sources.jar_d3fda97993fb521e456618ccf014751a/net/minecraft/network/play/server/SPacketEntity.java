package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketEntity implements Packet<INetHandlerPlayClient> {
   protected int entityId;
   protected int posX;
   protected int posY;
   protected int posZ;
   protected byte yaw;
   protected byte pitch;
   protected boolean onGround;
   protected boolean rotating;

   public SPacketEntity() {
   }

   public SPacketEntity(int entityIdIn) {
      this.entityId = entityIdIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarInt();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.entityId);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleEntityMovement(this);
   }

   public String toString() {
      return "Entity_" + super.toString();
   }

   @OnlyIn(Dist.CLIENT)
   public Entity getEntity(World worldIn) {
      return worldIn.getEntityByID(this.entityId);
   }

   @OnlyIn(Dist.CLIENT)
   public int getX() {
      return this.posX;
   }

   @OnlyIn(Dist.CLIENT)
   public int getY() {
      return this.posY;
   }

   @OnlyIn(Dist.CLIENT)
   public int getZ() {
      return this.posZ;
   }

   @OnlyIn(Dist.CLIENT)
   public byte getYaw() {
      return this.yaw;
   }

   @OnlyIn(Dist.CLIENT)
   public byte getPitch() {
      return this.pitch;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isRotating() {
      return this.rotating;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean getOnGround() {
      return this.onGround;
   }

   public static class Look extends SPacketEntity {
      public Look() {
         this.rotating = true;
      }

      public Look(int entityIdIn, byte yawIn, byte pitchIn, boolean onGroundIn) {
         super(entityIdIn);
         this.yaw = yawIn;
         this.pitch = pitchIn;
         this.rotating = true;
         this.onGround = onGroundIn;
      }

      /**
       * Reads the raw packet data from the data stream.
       */
      public void readPacketData(PacketBuffer buf) throws IOException {
         super.readPacketData(buf);
         this.yaw = buf.readByte();
         this.pitch = buf.readByte();
         this.onGround = buf.readBoolean();
      }

      /**
       * Writes the raw packet data to the data stream.
       */
      public void writePacketData(PacketBuffer buf) throws IOException {
         super.writePacketData(buf);
         buf.writeByte(this.yaw);
         buf.writeByte(this.pitch);
         buf.writeBoolean(this.onGround);
      }
   }

   public static class Move extends SPacketEntity {
      public Move() {
         this.rotating = true;
      }

      public Move(int entityIdIn, long xIn, long yIn, long zIn, byte yawIn, byte pitchIn, boolean onGroundIn) {
         super(entityIdIn);
         this.posX = (int)xIn;
         this.posY = (int)yIn;
         this.posZ = (int)zIn;
         this.yaw = yawIn;
         this.pitch = pitchIn;
         this.onGround = onGroundIn;
         this.rotating = true;
      }

      /**
       * Reads the raw packet data from the data stream.
       */
      public void readPacketData(PacketBuffer buf) throws IOException {
         super.readPacketData(buf);
         this.posX = buf.readShort();
         this.posY = buf.readShort();
         this.posZ = buf.readShort();
         this.yaw = buf.readByte();
         this.pitch = buf.readByte();
         this.onGround = buf.readBoolean();
      }

      /**
       * Writes the raw packet data to the data stream.
       */
      public void writePacketData(PacketBuffer buf) throws IOException {
         super.writePacketData(buf);
         buf.writeShort(this.posX);
         buf.writeShort(this.posY);
         buf.writeShort(this.posZ);
         buf.writeByte(this.yaw);
         buf.writeByte(this.pitch);
         buf.writeBoolean(this.onGround);
      }
   }

   public static class RelMove extends SPacketEntity {
      public RelMove() {
      }

      public RelMove(int entityIdIn, long xIn, long yIn, long zIn, boolean onGroundIn) {
         super(entityIdIn);
         this.posX = (int)xIn;
         this.posY = (int)yIn;
         this.posZ = (int)zIn;
         this.onGround = onGroundIn;
      }

      /**
       * Reads the raw packet data from the data stream.
       */
      public void readPacketData(PacketBuffer buf) throws IOException {
         super.readPacketData(buf);
         this.posX = buf.readShort();
         this.posY = buf.readShort();
         this.posZ = buf.readShort();
         this.onGround = buf.readBoolean();
      }

      /**
       * Writes the raw packet data to the data stream.
       */
      public void writePacketData(PacketBuffer buf) throws IOException {
         super.writePacketData(buf);
         buf.writeShort(this.posX);
         buf.writeShort(this.posY);
         buf.writeShort(this.posZ);
         buf.writeBoolean(this.onGround);
      }
   }
}