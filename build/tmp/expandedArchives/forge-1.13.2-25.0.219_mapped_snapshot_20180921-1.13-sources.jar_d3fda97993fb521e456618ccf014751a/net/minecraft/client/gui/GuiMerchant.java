package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketSelectTrade;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GuiMerchant extends GuiContainer {
   private static final Logger LOGGER = LogManager.getLogger();
   /** The GUI texture for the villager merchant GUI. */
   private static final ResourceLocation MERCHANT_GUI_TEXTURE = new ResourceLocation("textures/gui/container/villager.png");
   /** The current IMerchant instance in use for this specific merchant. */
   private final IMerchant merchant;
   /** The button which proceeds to the next available merchant recipe. */
   private GuiMerchant.MerchantButton nextButton;
   /** Returns to the previous Merchant recipe if one is applicable. */
   private GuiMerchant.MerchantButton previousButton;
   /** The integer value corresponding to the currently selected merchant recipe. */
   private int selectedMerchantRecipe;
   /** The chat component utilized by this GuiMerchant instance. */
   private final ITextComponent chatComponent;
   private final InventoryPlayer field_212355_D;

   public GuiMerchant(InventoryPlayer playerInventoryIn, IMerchant merchantIn, World worldIn) {
      super(new ContainerMerchant(playerInventoryIn, merchantIn, worldIn));
      this.merchant = merchantIn;
      this.chatComponent = merchantIn.getDisplayName();
      this.field_212355_D = playerInventoryIn;
   }

   private void func_195391_j() {
      ((ContainerMerchant)this.inventorySlots).setCurrentRecipeIndex(this.selectedMerchantRecipe);
      this.mc.getConnection().sendPacket(new CPacketSelectTrade(this.selectedMerchantRecipe));
   }

   /**
    * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
    * window resizes, the buttonList is cleared beforehand.
    */
   protected void initGui() {
      super.initGui();
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.nextButton = this.addButton(new GuiMerchant.MerchantButton(1, i + 120 + 27, j + 24 - 1, true) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiMerchant.this.selectedMerchantRecipe++;
            MerchantRecipeList merchantrecipelist = GuiMerchant.this.merchant.getRecipes(GuiMerchant.this.mc.player);
            if (merchantrecipelist != null && GuiMerchant.this.selectedMerchantRecipe >= merchantrecipelist.size()) {
               GuiMerchant.this.selectedMerchantRecipe = merchantrecipelist.size() - 1;
            }

            GuiMerchant.this.func_195391_j();
         }
      });
      this.previousButton = this.addButton(new GuiMerchant.MerchantButton(2, i + 36 - 19, j + 24 - 1, false) {
         /**
          * Called when the left mouse button is pressed over this button. This method is specific to GuiButton.
          */
         public void onClick(double mouseX, double mouseY) {
            GuiMerchant.this.selectedMerchantRecipe--;
            if (GuiMerchant.this.selectedMerchantRecipe < 0) {
               GuiMerchant.this.selectedMerchantRecipe = 0;
            }

            GuiMerchant.this.func_195391_j();
         }
      });
      this.nextButton.enabled = false;
      this.previousButton.enabled = false;
   }

   /**
    * Draw the foreground layer for the GuiContainer (everything in front of the items)
    */
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      String s = this.chatComponent.getFormattedText();
      this.fontRenderer.drawString(s, (float)(this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2), 6.0F, 4210752);
      this.fontRenderer.drawString(this.field_212355_D.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
   }

   /**
    * Called from the main game loop to update the screen.
    */
   public void tick() {
      super.tick();
      MerchantRecipeList merchantrecipelist = this.merchant.getRecipes(this.mc.player);
      if (merchantrecipelist != null) {
         this.nextButton.enabled = this.selectedMerchantRecipe < merchantrecipelist.size() - 1;
         this.previousButton.enabled = this.selectedMerchantRecipe > 0;
      }

   }

   /**
    * Draws the background layer of this container (behind the items).
    */
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
      MerchantRecipeList merchantrecipelist = this.merchant.getRecipes(this.mc.player);
      if (merchantrecipelist != null && !merchantrecipelist.isEmpty()) {
         int k = this.selectedMerchantRecipe;
         if (k < 0 || k >= merchantrecipelist.size()) {
            return;
         }

         MerchantRecipe merchantrecipe = merchantrecipelist.get(k);
         if (merchantrecipe.isRecipeDisabled()) {
            this.mc.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();
            this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 21, 212, 0, 28, 21);
            this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 51, 212, 0, 28, 21);
         }
      }

   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      super.render(mouseX, mouseY, partialTicks);
      MerchantRecipeList merchantrecipelist = this.merchant.getRecipes(this.mc.player);
      if (merchantrecipelist != null && !merchantrecipelist.isEmpty()) {
         int i = (this.width - this.xSize) / 2;
         int j = (this.height - this.ySize) / 2;
         int k = this.selectedMerchantRecipe;
         MerchantRecipe merchantrecipe = merchantrecipelist.get(k);
         ItemStack itemstack = merchantrecipe.getItemToBuy();
         ItemStack itemstack1 = merchantrecipe.getSecondItemToBuy();
         ItemStack itemstack2 = merchantrecipe.getItemToSell();
         GlStateManager.pushMatrix();
         RenderHelper.enableGUIStandardItemLighting();
         GlStateManager.disableLighting();
         GlStateManager.enableRescaleNormal();
         GlStateManager.enableColorMaterial();
         GlStateManager.enableLighting();
         this.itemRender.zLevel = 100.0F;
         this.itemRender.renderItemAndEffectIntoGUI(itemstack, i + 36, j + 24);
         this.itemRender.renderItemOverlays(this.fontRenderer, itemstack, i + 36, j + 24);
         if (!itemstack1.isEmpty()) {
            this.itemRender.renderItemAndEffectIntoGUI(itemstack1, i + 62, j + 24);
            this.itemRender.renderItemOverlays(this.fontRenderer, itemstack1, i + 62, j + 24);
         }

         this.itemRender.renderItemAndEffectIntoGUI(itemstack2, i + 120, j + 24);
         this.itemRender.renderItemOverlays(this.fontRenderer, itemstack2, i + 120, j + 24);
         this.itemRender.zLevel = 0.0F;
         GlStateManager.disableLighting();
         if (this.isPointInRegion(36, 24, 16, 16, (double)mouseX, (double)mouseY) && !itemstack.isEmpty()) {
            this.renderToolTip(itemstack, mouseX, mouseY);
         } else if (!itemstack1.isEmpty() && this.isPointInRegion(62, 24, 16, 16, (double)mouseX, (double)mouseY) && !itemstack1.isEmpty()) {
            this.renderToolTip(itemstack1, mouseX, mouseY);
         } else if (!itemstack2.isEmpty() && this.isPointInRegion(120, 24, 16, 16, (double)mouseX, (double)mouseY) && !itemstack2.isEmpty()) {
            this.renderToolTip(itemstack2, mouseX, mouseY);
         } else if (merchantrecipe.isRecipeDisabled() && (this.isPointInRegion(83, 21, 28, 21, (double)mouseX, (double)mouseY) || this.isPointInRegion(83, 51, 28, 21, (double)mouseX, (double)mouseY))) {
            this.drawHoveringText(I18n.format("merchant.deprecated"), mouseX, mouseY);
         }

         GlStateManager.popMatrix();
         GlStateManager.enableLighting();
         GlStateManager.enableDepthTest();
         RenderHelper.enableStandardItemLighting();
      }

      this.renderHoveredToolTip(mouseX, mouseY);
   }

   public IMerchant getMerchant() {
      return this.merchant;
   }

   @OnlyIn(Dist.CLIENT)
   abstract static class MerchantButton extends GuiButton {
      private final boolean forward;

      public MerchantButton(int buttonID, int x, int y, boolean p_i1095_4_) {
         super(buttonID, x, y, 12, 19, "");
         this.forward = p_i1095_4_;
      }

      public void render(int mouseX, int mouseY, float partialTicks) {
         if (this.visible) {
            Minecraft.getInstance().getTextureManager().bindTexture(GuiMerchant.MERCHANT_GUI_TEXTURE);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = 0;
            int j = 176;
            if (!this.enabled) {
               j += this.width * 2;
            } else if (flag) {
               j += this.width;
            }

            if (!this.forward) {
               i += this.height;
            }

            this.drawTexturedModalRect(this.x, this.y, j, i, this.width, this.height);
         }
      }
   }
}