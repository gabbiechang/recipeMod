package net.minecraft.client.gui;

import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BossInfoClient extends BossInfo {
   protected float rawPercent;
   protected long percentSetTime;

   public BossInfoClient(SPacketUpdateBossInfo packetIn) {
      super(packetIn.getUniqueId(), packetIn.getName(), packetIn.getColor(), packetIn.getOverlay());
      this.rawPercent = packetIn.getPercent();
      this.percent = packetIn.getPercent();
      this.percentSetTime = Util.milliTime();
      this.setDarkenSky(packetIn.shouldDarkenSky());
      this.setPlayEndBossMusic(packetIn.shouldPlayEndBossMusic());
      this.setCreateFog(packetIn.shouldCreateFog());
   }

   public void setPercent(float percentIn) {
      this.percent = this.getPercent();
      this.rawPercent = percentIn;
      this.percentSetTime = Util.milliTime();
   }

   public float getPercent() {
      long i = Util.milliTime() - this.percentSetTime;
      float f = MathHelper.clamp((float)i / 100.0F, 0.0F, 1.0F);
      return this.percent + (this.rawPercent - this.percent) * f;
   }

   public void updateFromPacket(SPacketUpdateBossInfo packetIn) {
      switch(packetIn.getOperation()) {
      case UPDATE_NAME:
         this.setName(packetIn.getName());
         break;
      case UPDATE_PCT:
         this.setPercent(packetIn.getPercent());
         break;
      case UPDATE_STYLE:
         this.setColor(packetIn.getColor());
         this.setOverlay(packetIn.getOverlay());
         break;
      case UPDATE_PROPERTIES:
         this.setDarkenSky(packetIn.shouldDarkenSky());
         this.setPlayEndBossMusic(packetIn.shouldPlayEndBossMusic());
      }

   }
}