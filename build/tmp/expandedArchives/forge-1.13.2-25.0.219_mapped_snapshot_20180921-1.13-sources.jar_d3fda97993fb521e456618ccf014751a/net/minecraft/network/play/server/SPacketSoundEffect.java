package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;

public class SPacketSoundEffect implements Packet<INetHandlerPlayClient> {
   private SoundEvent sound;
   private SoundCategory category;
   private int posX;
   private int posY;
   private int posZ;
   private float soundVolume;
   private float soundPitch;

   public SPacketSoundEffect() {
   }

   public SPacketSoundEffect(SoundEvent soundIn, SoundCategory categoryIn, double xIn, double yIn, double zIn, float volumeIn, float pitchIn) {
      Validate.notNull(soundIn, "sound");
      this.sound = soundIn;
      this.category = categoryIn;
      this.posX = (int)(xIn * 8.0D);
      this.posY = (int)(yIn * 8.0D);
      this.posZ = (int)(zIn * 8.0D);
      this.soundVolume = volumeIn;
      this.soundPitch = pitchIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.sound = IRegistry.field_212633_v.get(buf.readVarInt());
      this.category = buf.readEnumValue(SoundCategory.class);
      this.posX = buf.readInt();
      this.posY = buf.readInt();
      this.posZ = buf.readInt();
      this.soundVolume = buf.readFloat();
      this.soundPitch = buf.readFloat();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(IRegistry.field_212633_v.getId(this.sound));
      buf.writeEnumValue(this.category);
      buf.writeInt(this.posX);
      buf.writeInt(this.posY);
      buf.writeInt(this.posZ);
      buf.writeFloat(this.soundVolume);
      buf.writeFloat(this.soundPitch);
   }

   @OnlyIn(Dist.CLIENT)
   public SoundEvent getSound() {
      return this.sound;
   }

   @OnlyIn(Dist.CLIENT)
   public SoundCategory getCategory() {
      return this.category;
   }

   @OnlyIn(Dist.CLIENT)
   public double getX() {
      return (double)((float)this.posX / 8.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public double getY() {
      return (double)((float)this.posY / 8.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public double getZ() {
      return (double)((float)this.posZ / 8.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public float getVolume() {
      return this.soundVolume;
   }

   @OnlyIn(Dist.CLIENT)
   public float getPitch() {
      return this.soundPitch;
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleSoundEffect(this);
   }
}