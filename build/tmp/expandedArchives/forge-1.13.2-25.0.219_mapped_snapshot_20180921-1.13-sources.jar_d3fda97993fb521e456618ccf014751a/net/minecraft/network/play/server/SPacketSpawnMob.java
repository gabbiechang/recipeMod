package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketSpawnMob implements Packet<INetHandlerPlayClient> {
   private int entityId;
   private UUID uniqueId;
   private int type;
   private double x;
   private double y;
   private double z;
   private int velocityX;
   private int velocityY;
   private int velocityZ;
   private byte yaw;
   private byte pitch;
   private byte headPitch;
   private EntityDataManager dataManager;
   private List<EntityDataManager.DataEntry<?>> dataManagerEntries;

   public SPacketSpawnMob() {
   }

   public SPacketSpawnMob(EntityLivingBase entityIn) {
      this.entityId = entityIn.getEntityId();
      this.uniqueId = entityIn.getUniqueID();
      this.type = IRegistry.field_212629_r.getId(entityIn.getType());
      this.x = entityIn.posX;
      this.y = entityIn.posY;
      this.z = entityIn.posZ;
      this.yaw = (byte)((int)(entityIn.rotationYaw * 256.0F / 360.0F));
      this.pitch = (byte)((int)(entityIn.rotationPitch * 256.0F / 360.0F));
      this.headPitch = (byte)((int)(entityIn.rotationYawHead * 256.0F / 360.0F));
      double d0 = 3.9D;
      double d1 = entityIn.motionX;
      double d2 = entityIn.motionY;
      double d3 = entityIn.motionZ;
      if (d1 < -3.9D) {
         d1 = -3.9D;
      }

      if (d2 < -3.9D) {
         d2 = -3.9D;
      }

      if (d3 < -3.9D) {
         d3 = -3.9D;
      }

      if (d1 > 3.9D) {
         d1 = 3.9D;
      }

      if (d2 > 3.9D) {
         d2 = 3.9D;
      }

      if (d3 > 3.9D) {
         d3 = 3.9D;
      }

      this.velocityX = (int)(d1 * 8000.0D);
      this.velocityY = (int)(d2 * 8000.0D);
      this.velocityZ = (int)(d3 * 8000.0D);
      this.dataManager = entityIn.getDataManager();
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarInt();
      this.uniqueId = buf.readUniqueId();
      this.type = buf.readVarInt();
      this.x = buf.readDouble();
      this.y = buf.readDouble();
      this.z = buf.readDouble();
      this.yaw = buf.readByte();
      this.pitch = buf.readByte();
      this.headPitch = buf.readByte();
      this.velocityX = buf.readShort();
      this.velocityY = buf.readShort();
      this.velocityZ = buf.readShort();
      this.dataManagerEntries = EntityDataManager.readEntries(buf);
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.entityId);
      buf.writeUniqueId(this.uniqueId);
      buf.writeVarInt(this.type);
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
      buf.writeByte(this.yaw);
      buf.writeByte(this.pitch);
      buf.writeByte(this.headPitch);
      buf.writeShort(this.velocityX);
      buf.writeShort(this.velocityY);
      buf.writeShort(this.velocityZ);
      this.dataManager.writeEntries(buf);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleSpawnMob(this);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public List<EntityDataManager.DataEntry<?>> getDataManagerEntries() {
      return this.dataManagerEntries;
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityID() {
      return this.entityId;
   }

   @OnlyIn(Dist.CLIENT)
   public UUID getUniqueId() {
      return this.uniqueId;
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityType() {
      return this.type;
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
   public int getVelocityX() {
      return this.velocityX;
   }

   @OnlyIn(Dist.CLIENT)
   public int getVelocityY() {
      return this.velocityY;
   }

   @OnlyIn(Dist.CLIENT)
   public int getVelocityZ() {
      return this.velocityZ;
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
   public byte getHeadPitch() {
      return this.headPitch;
   }
}