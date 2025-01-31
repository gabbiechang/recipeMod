package net.minecraft.network.login.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketDisconnectLogin implements Packet<INetHandlerLoginClient> {
   private ITextComponent reason;

   public SPacketDisconnectLogin() {
   }

   public SPacketDisconnectLogin(ITextComponent p_i46853_1_) {
      this.reason = p_i46853_1_;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.reason = ITextComponent.Serializer.fromJsonLenient(buf.readString(32767));
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeTextComponent(this.reason);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(INetHandlerLoginClient handler) {
      handler.handleDisconnect(this);
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getReason() {
      return this.reason;
   }
}