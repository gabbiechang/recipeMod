package net.minecraft.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerShulkerBox;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiShulkerBox extends GuiContainer {
   private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("textures/gui/container/shulker_box.png");
   private final IInventory inventory;
   private final InventoryPlayer playerInventory;

   public GuiShulkerBox(InventoryPlayer playerInventoryIn, IInventory inventoryIn) {
      super(new ContainerShulkerBox(playerInventoryIn, inventoryIn, Minecraft.getInstance().player));
      this.playerInventory = playerInventoryIn;
      this.inventory = inventoryIn;
      ++this.ySize;
   }

   /**
    * Draws the screen and all the components in it.
    */
   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      super.render(mouseX, mouseY, partialTicks);
      this.renderHoveredToolTip(mouseX, mouseY);
   }

   /**
    * Draw the foreground layer for the GuiContainer (everything in front of the items)
    */
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.fontRenderer.drawString(this.inventory.getDisplayName().getFormattedText(), 8.0F, 6.0F, 4210752);
      this.fontRenderer.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
   }

   /**
    * Draws the background layer of this container (behind the items).
    */
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
   }
}