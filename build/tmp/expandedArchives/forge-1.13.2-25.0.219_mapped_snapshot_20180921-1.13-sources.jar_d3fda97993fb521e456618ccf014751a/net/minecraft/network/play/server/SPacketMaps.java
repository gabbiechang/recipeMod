package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.Collection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketMaps implements Packet<INetHandlerPlayClient> {
   private int mapId;
   private byte mapScale;
   private boolean trackingPosition;
   private MapDecoration[] icons;
   private int minX;
   private int minZ;
   private int columns;
   private int rows;
   private byte[] mapDataBytes;

   public SPacketMaps() {
   }

   public SPacketMaps(int mapIdIn, byte mapScaleIn, boolean trackingPositionIn, Collection<MapDecoration> iconsIn, byte[] p_i46937_5_, int minXIn, int minZIn, int columnsIn, int rowsIn) {
      this.mapId = mapIdIn;
      this.mapScale = mapScaleIn;
      this.trackingPosition = trackingPositionIn;
      this.icons = iconsIn.toArray(new MapDecoration[iconsIn.size()]);
      this.minX = minXIn;
      this.minZ = minZIn;
      this.columns = columnsIn;
      this.rows = rowsIn;
      this.mapDataBytes = new byte[columnsIn * rowsIn];

      for(int i = 0; i < columnsIn; ++i) {
         for(int j = 0; j < rowsIn; ++j) {
            this.mapDataBytes[i + j * columnsIn] = p_i46937_5_[minXIn + i + (minZIn + j) * 128];
         }
      }

   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.mapId = buf.readVarInt();
      this.mapScale = buf.readByte();
      this.trackingPosition = buf.readBoolean();
      this.icons = new MapDecoration[buf.readVarInt()];

      for(int i = 0; i < this.icons.length; ++i) {
         MapDecoration.Type mapdecoration$type = buf.readEnumValue(MapDecoration.Type.class);
         this.icons[i] = new MapDecoration(mapdecoration$type, buf.readByte(), buf.readByte(), (byte)(buf.readByte() & 15), buf.readBoolean() ? buf.readTextComponent() : null);
      }

      this.columns = buf.readUnsignedByte();
      if (this.columns > 0) {
         this.rows = buf.readUnsignedByte();
         this.minX = buf.readUnsignedByte();
         this.minZ = buf.readUnsignedByte();
         this.mapDataBytes = buf.readByteArray();
      }

   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.mapId);
      buf.writeByte(this.mapScale);
      buf.writeBoolean(this.trackingPosition);
      buf.writeVarInt(this.icons.length);

      for(MapDecoration mapdecoration : this.icons) {
         buf.writeEnumValue(mapdecoration.getType());
         buf.writeByte(mapdecoration.getX());
         buf.writeByte(mapdecoration.getY());
         buf.writeByte(mapdecoration.getRotation() & 15);
         if (mapdecoration.func_204309_g() != null) {
            buf.writeBoolean(true);
            buf.writeTextComponent(mapdecoration.func_204309_g());
         } else {
            buf.writeBoolean(false);
         }
      }

      buf.writeByte(this.columns);
      if (this.columns > 0) {
         buf.writeByte(this.rows);
         buf.writeByte(this.minX);
         buf.writeByte(this.minZ);
         buf.writeByteArray(this.mapDataBytes);
      }

   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleMaps(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getMapId() {
      return this.mapId;
   }

   /**
    * Sets new MapData from the packet to given MapData param
    */
   @OnlyIn(Dist.CLIENT)
   public void setMapdataTo(MapData mapdataIn) {
      mapdataIn.scale = this.mapScale;
      mapdataIn.trackingPosition = this.trackingPosition;
      mapdataIn.mapDecorations.clear();

      for(int i = 0; i < this.icons.length; ++i) {
         MapDecoration mapdecoration = this.icons[i];
         mapdataIn.mapDecorations.put("icon-" + i, mapdecoration);
      }

      for(int j = 0; j < this.columns; ++j) {
         for(int k = 0; k < this.rows; ++k) {
            mapdataIn.colors[this.minX + j + (this.minZ + k) * 128] = this.mapDataBytes[j + k * this.columns];
         }
      }

   }
}