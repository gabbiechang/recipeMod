package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemPotion extends Item {
   public ItemPotion(Item.Properties builder) {
      super(builder);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getDefaultInstance() {
      return PotionUtils.addPotionToItemStack(super.getDefaultInstance(), PotionTypes.WATER);
   }

   /**
    * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
    * the Item before the action is complete.
    */
   public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
      EntityPlayer entityplayer = entityLiving instanceof EntityPlayer ? (EntityPlayer)entityLiving : null;
      if (entityplayer == null || !entityplayer.abilities.isCreativeMode) {
         stack.shrink(1);
      }

      if (entityplayer instanceof EntityPlayerMP) {
         CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP)entityplayer, stack);
      }

      if (!worldIn.isRemote) {
         for(PotionEffect potioneffect : PotionUtils.getEffectsFromStack(stack)) {
            if (potioneffect.getPotion().isInstant()) {
               potioneffect.getPotion().affectEntity(entityplayer, entityplayer, entityLiving, potioneffect.getAmplifier(), 1.0D);
            } else {
               entityLiving.addPotionEffect(new PotionEffect(potioneffect));
            }
         }
      }

      if (entityplayer != null) {
         entityplayer.addStat(StatList.ITEM_USED.get(this));
      }

      if (entityplayer == null || !entityplayer.abilities.isCreativeMode) {
         if (stack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
         }

         if (entityplayer != null) {
            entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
         }
      }

      return stack;
   }

   /**
    * How long it takes to use or consume an item
    */
   public int getUseDuration(ItemStack stack) {
      return 32;
   }

   /**
    * returns the action that specifies what animation to play when the items is being used
    */
   public EnumAction getUseAction(ItemStack stack) {
      return EnumAction.DRINK;
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      playerIn.setActiveHand(handIn);
      return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
   }

   /**
    * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
    * different names based on their damage or NBT.
    */
   public String getTranslationKey(ItemStack stack) {
      return PotionUtils.getPotionFromItem(stack).getNamePrefixed(this.getTranslationKey() + ".effect.");
   }

   /**
    * allows items to add custom lines of information to the mouseover description
    */
   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      PotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
   }

   /**
    * Returns true if this item has an enchantment glint. By default, this returns <code>stack.isItemEnchanted()</code>,
    * but other items can override it (for instance, written books always return true).
    *  
    * Note that if you override this method, you generally want to also call the super version (on {@link Item}) to get
    * the glint for enchanted items. Of course, that is unnecessary if the overwritten version always returns true.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean hasEffect(ItemStack stack) {
      return super.hasEffect(stack) || !PotionUtils.getEffectsFromStack(stack).isEmpty();
   }

   /**
    * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
    */
   public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
      if (this.isInGroup(group)) {
         for(PotionType potiontype : IRegistry.field_212621_j) {
            if (potiontype != PotionTypes.EMPTY) {
               items.add(PotionUtils.addPotionToItemStack(new ItemStack(this), potiontype));
            }
         }
      }

   }
}