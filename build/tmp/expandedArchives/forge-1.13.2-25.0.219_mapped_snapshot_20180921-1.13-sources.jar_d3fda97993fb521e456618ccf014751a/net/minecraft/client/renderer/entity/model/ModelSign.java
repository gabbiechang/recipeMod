package net.minecraft.client.renderer.entity.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelSign extends ModelBase {
   /** The board on a sign that has the writing on it. */
   private final ModelRenderer signBoard = new ModelRenderer(this, 0, 0);
   /** The stick a sign stands on. */
   private final ModelRenderer signStick;

   public ModelSign() {
      this.signBoard.addBox(-12.0F, -14.0F, -1.0F, 24, 12, 2, 0.0F);
      this.signStick = new ModelRenderer(this, 0, 14);
      this.signStick.addBox(-1.0F, -2.0F, -1.0F, 2, 14, 2, 0.0F);
   }

   /**
    * Renders the sign model through TileEntitySignRenderer
    */
   public void renderSign() {
      this.signBoard.render(0.0625F);
      this.signStick.render(0.0625F);
   }

   public ModelRenderer getSignStick() {
      return this.signStick;
   }
}