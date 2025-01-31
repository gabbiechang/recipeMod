package net.minecraft.client.renderer;

import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StitcherException extends RuntimeException {
   private final Stitcher.Holder holder;

   public StitcherException(Stitcher.Holder holderIn, String message) {
      super(message);
      this.holder = holderIn;
   }
}