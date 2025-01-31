package net.minecraft.client.renderer;

import com.google.common.collect.Ordering;
import java.util.Collection;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class InventoryEffectRenderer extends GuiContainer {
   /** True if there is some potion effect to display */
   protected boolean hasActivePotionEffects;

   public InventoryEffectRenderer(Container inventorySlotsIn) {
      super(inventorySlotsIn);
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      super.initGui();
      this.updateActivePotionEffects();
   }

   protected void updateActivePotionEffects() {
      boolean hasVisibleEffect = false;
      for(PotionEffect potioneffect : this.mc.player.getActivePotionEffects()) {
         Potion potion = potioneffect.getPotion();
         if(potion.shouldRender(potioneffect)) { hasVisibleEffect = true; break; }
      }
      if (this.mc.player.getActivePotionEffects().isEmpty() || !hasVisibleEffect) {
         this.guiLeft = (this.width - this.xSize) / 2;
         this.hasActivePotionEffects = false;
      } else {
         if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.PotionShiftEvent(this))) this.guiLeft = (this.width - this.xSize) / 2; else
         this.guiLeft = 160 + (this.width - this.xSize - 200) / 2;
         this.hasActivePotionEffects = true;
      }

   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      super.render(mouseX, mouseY, partialTicks);
      if (this.hasActivePotionEffects) {
         this.drawActivePotionEffects();
      }

   }

   /**
    * Display the potion effects list
    */
   private void drawActivePotionEffects() {
      int i = this.guiLeft - 124;
      int j = this.guiTop;
      int k = 166;
      Collection<PotionEffect> collection = this.mc.player.getActivePotionEffects();
      if (!collection.isEmpty()) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableLighting();
         int l = 33;
         if (collection.size() > 5) {
            l = 132 / (collection.size() - 1);
         }

         for(PotionEffect potioneffect : Ordering.natural().sortedCopy(collection)) {
            Potion potion = potioneffect.getPotion();
            if(!potion.shouldRender(potioneffect)) continue;
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
            this.drawTexturedModalRect(i, j, 0, 166, 140, 32);
            if (potion.hasStatusIcon()) {
               int i1 = potion.getStatusIconIndex();
               this.drawTexturedModalRect(i + 6, j + 7, i1 % 12 * 18, 198 + i1 / 12 * 18, 18, 18);
            }

            potion.renderInventoryEffect(potioneffect, this, i, j, this.zLevel);
            if (!potion.shouldRenderInvText(potioneffect)) { j += l; continue; }
            String s1 = I18n.format(potion.getName());
            if (potioneffect.getAmplifier() == 1) {
               s1 = s1 + ' ' + I18n.format("enchantment.level.2");
            } else if (potioneffect.getAmplifier() == 2) {
               s1 = s1 + ' ' + I18n.format("enchantment.level.3");
            } else if (potioneffect.getAmplifier() == 3) {
               s1 = s1 + ' ' + I18n.format("enchantment.level.4");
            }

            this.fontRenderer.drawStringWithShadow(s1, (float)(i + 10 + 18), (float)(j + 6), 16777215);
            String s = PotionUtil.getPotionDurationString(potioneffect, 1.0F);
            this.fontRenderer.drawStringWithShadow(s, (float)(i + 10 + 18), (float)(j + 6 + 10), 8355711);
            j += l;
         }

      }
   }
}