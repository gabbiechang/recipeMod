package net.minecraft.network.login.server;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketLoginSuccess implements Packet<INetHandlerLoginClient> {
   private GameProfile profile;

   public SPacketLoginSuccess() {
   }

   public SPacketLoginSuccess(GameProfile profileIn) {
      this.profile = profileIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      String s = buf.readString(36);
      String s1 = buf.readString(16);
      UUID uuid = s.length() > 0 ? UUID.fromString(s) : null; // Forge: prevent exception with bad data.
      this.profile = new GameProfile(uuid, s1);
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      UUID uuid = this.profile.getId();
      buf.writeString(uuid == null ? "" : uuid.toString());
      buf.writeString(this.profile.getName());
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(INetHandlerLoginClient handler) {
      handler.handleLoginSuccess(this);
   }

   @OnlyIn(Dist.CLIENT)
   public GameProfile getProfile() {
      return this.profile;
   }
}