package net.minecraft.client.gui.toasts;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SystemToast implements IToast {
   private final SystemToast.Type type;
   private String title;
   private String subtitle;
   private long firstDrawTime;
   private boolean newDisplay;

   public SystemToast(SystemToast.Type typeIn, ITextComponent titleComponent, @Nullable ITextComponent subtitleComponent) {
      this.type = typeIn;
      this.title = titleComponent.getString();
      this.subtitle = subtitleComponent == null ? null : subtitleComponent.getString();
   }

   public IToast.Visibility draw(GuiToast toastGui, long delta) {
      if (this.newDisplay) {
         this.firstDrawTime = delta;
         this.newDisplay = false;
      }

      toastGui.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
      GlStateManager.color3f(1.0F, 1.0F, 1.0F);
      toastGui.drawTexturedModalRect(0, 0, 0, 64, 160, 32);
      if (this.subtitle == null) {
         toastGui.getMinecraft().fontRenderer.drawString(this.title, 18.0F, 12.0F, -256);
      } else {
         toastGui.getMinecraft().fontRenderer.drawString(this.title, 18.0F, 7.0F, -256);
         toastGui.getMinecraft().fontRenderer.drawString(this.subtitle, 18.0F, 18.0F, -1);
      }

      return delta - this.firstDrawTime < 5000L ? IToast.Visibility.SHOW : IToast.Visibility.HIDE;
   }

   public void setDisplayedText(ITextComponent titleComponent, @Nullable ITextComponent subtitleComponent) {
      this.title = titleComponent.getString();
      this.subtitle = subtitleComponent == null ? null : subtitleComponent.getString();
      this.newDisplay = true;
   }

   public SystemToast.Type getType() {
      return this.type;
   }

   public static void addOrUpdate(GuiToast p_193657_0_, SystemToast.Type p_193657_1_, ITextComponent p_193657_2_, @Nullable ITextComponent p_193657_3_) {
      SystemToast systemtoast = p_193657_0_.getToast(SystemToast.class, p_193657_1_);
      if (systemtoast == null) {
         p_193657_0_.add(new SystemToast(p_193657_1_, p_193657_2_, p_193657_3_));
      } else {
         systemtoast.setDisplayedText(p_193657_2_, p_193657_3_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      TUTORIAL_HINT,
      NARRATOR_TOGGLE,
      WORLD_BACKUP;
   }
}