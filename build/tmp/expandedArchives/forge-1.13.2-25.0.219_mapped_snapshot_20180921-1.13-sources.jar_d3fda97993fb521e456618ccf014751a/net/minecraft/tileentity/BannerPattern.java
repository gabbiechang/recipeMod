package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum BannerPattern implements net.minecraftforge.common.IExtensibleEnum {
   BASE("base", "b"),
   SQUARE_BOTTOM_LEFT("square_bottom_left", "bl", "   ", "   ", "#  "),
   SQUARE_BOTTOM_RIGHT("square_bottom_right", "br", "   ", "   ", "  #"),
   SQUARE_TOP_LEFT("square_top_left", "tl", "#  ", "   ", "   "),
   SQUARE_TOP_RIGHT("square_top_right", "tr", "  #", "   ", "   "),
   STRIPE_BOTTOM("stripe_bottom", "bs", "   ", "   ", "###"),
   STRIPE_TOP("stripe_top", "ts", "###", "   ", "   "),
   STRIPE_LEFT("stripe_left", "ls", "#  ", "#  ", "#  "),
   STRIPE_RIGHT("stripe_right", "rs", "  #", "  #", "  #"),
   STRIPE_CENTER("stripe_center", "cs", " # ", " # ", " # "),
   STRIPE_MIDDLE("stripe_middle", "ms", "   ", "###", "   "),
   STRIPE_DOWNRIGHT("stripe_downright", "drs", "#  ", " # ", "  #"),
   STRIPE_DOWNLEFT("stripe_downleft", "dls", "  #", " # ", "#  "),
   STRIPE_SMALL("small_stripes", "ss", "# #", "# #", "   "),
   CROSS("cross", "cr", "# #", " # ", "# #"),
   STRAIGHT_CROSS("straight_cross", "sc", " # ", "###", " # "),
   TRIANGLE_BOTTOM("triangle_bottom", "bt", "   ", " # ", "# #"),
   TRIANGLE_TOP("triangle_top", "tt", "# #", " # ", "   "),
   TRIANGLES_BOTTOM("triangles_bottom", "bts", "   ", "# #", " # "),
   TRIANGLES_TOP("triangles_top", "tts", " # ", "# #", "   "),
   DIAGONAL_LEFT("diagonal_left", "ld", "## ", "#  ", "   "),
   DIAGONAL_RIGHT("diagonal_up_right", "rd", "   ", "  #", " ##"),
   DIAGONAL_LEFT_MIRROR("diagonal_up_left", "lud", "   ", "#  ", "## "),
   DIAGONAL_RIGHT_MIRROR("diagonal_right", "rud", " ##", "  #", "   "),
   CIRCLE_MIDDLE("circle", "mc", "   ", " # ", "   "),
   RHOMBUS_MIDDLE("rhombus", "mr", " # ", "# #", " # "),
   HALF_VERTICAL("half_vertical", "vh", "## ", "## ", "## "),
   HALF_HORIZONTAL("half_horizontal", "hh", "###", "###", "   "),
   HALF_VERTICAL_MIRROR("half_vertical_right", "vhr", " ##", " ##", " ##"),
   HALF_HORIZONTAL_MIRROR("half_horizontal_bottom", "hhb", "   ", "###", "###"),
   BORDER("border", "bo", "###", "# #", "###"),
   CURLY_BORDER("curly_border", "cbo", new ItemStack(Blocks.VINE)),
   CREEPER("creeper", "cre", new ItemStack(Items.CREEPER_HEAD)),
   GRADIENT("gradient", "gra", "# #", " # ", " # "),
   GRADIENT_UP("gradient_up", "gru", " # ", " # ", "# #"),
   BRICKS("bricks", "bri", new ItemStack(Blocks.BRICKS)),
   SKULL("skull", "sku", new ItemStack(Items.WITHER_SKELETON_SKULL)),
   FLOWER("flower", "flo", new ItemStack(Blocks.OXEYE_DAISY)),
   MOJANG("mojang", "moj", new ItemStack(Items.ENCHANTED_GOLDEN_APPLE));

   private final String fileName;
   private final String hashname;
   private final String[] patterns = new String[3];
   private ItemStack patternItem = ItemStack.EMPTY;

   private BannerPattern(String p_i47245_3_, String p_i47245_4_) {
      this.fileName = p_i47245_3_;
      this.hashname = p_i47245_4_;
   }

   private BannerPattern(String p_i47246_3_, String p_i47246_4_, ItemStack p_i47246_5_) {
      this(p_i47246_3_, p_i47246_4_);
      this.patternItem = p_i47246_5_;
   }

   private BannerPattern(String p_i47247_3_, String p_i47247_4_, String p_i47247_5_, String p_i47247_6_, String p_i47247_7_) {
      this(p_i47247_3_, p_i47247_4_);
      this.patterns[0] = p_i47247_5_;
      this.patterns[1] = p_i47247_6_;
      this.patterns[2] = p_i47247_7_;
   }

   @OnlyIn(Dist.CLIENT)
   public String getFileName() {
      return this.fileName;
   }

   public String getHashname() {
      return this.hashname;
   }

   public String[] getPatterns() {
      return this.patterns;
   }

   public boolean hasPattern() {
      return !this.patternItem.isEmpty() || this.patterns[0] != null;
   }

   public boolean hasPatternItem() {
      return !this.patternItem.isEmpty();
   }

   public ItemStack getPatternItem() {
      return this.patternItem;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static BannerPattern byHash(String hash) {
      for(BannerPattern bannerpattern : values()) {
         if (bannerpattern.hashname.equals(hash)) {
            return bannerpattern;
         }
      }

      return null;
   }
   
   public static BannerPattern create(String enumName, String p_i47246_3_, String p_i47246_4_, ItemStack p_i47246_5_) {
	   throw new IllegalStateException("Enum not extended");
   }
   
   public static BannerPattern create(String enumName, String p_i47247_3_, String p_i47247_4_, String p_i47247_5_, String p_i47247_6_, String p_i47247_7_) {
	   throw new IllegalStateException("Enum not extended");
   }
}