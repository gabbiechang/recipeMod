package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemFood extends Item {
   /** The amount this food item heals the player. */
   private final int healAmount;
   private final float saturationModifier;
   /** Whether wolves like this food (true for raw and cooked porkchop). */
   private final boolean meat;
   /** If this field is true, the food can be consumed even if the player don't need to eat. */
   private boolean alwaysEdible;
   private boolean fastEating;
   /** represents the potion effect that will occurr upon eating this food. Set by setPotionEffect */
   private PotionEffect potionId;
   /** probably of the set potion effect occurring */
   private float potionEffectProbability;

   public ItemFood(int healAmountIn, float saturation, boolean meat, Item.Properties builder) {
      super(builder);
      this.healAmount = healAmountIn;
      this.meat = meat;
      this.saturationModifier = saturation;
   }

   /**
    * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
    * the Item before the action is complete.
    */
   public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
      if (entityLiving instanceof EntityPlayer) {
         EntityPlayer entityplayer = (EntityPlayer)entityLiving;
         entityplayer.getFoodStats().addStats(this, stack);
         worldIn.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
         this.onFoodEaten(stack, worldIn, entityplayer);
         entityplayer.addStat(StatList.ITEM_USED.get(this));
         if (entityplayer instanceof EntityPlayerMP) {
            CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP)entityplayer, stack);
         }
      }

      stack.shrink(1);
      return stack;
   }

   protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
      if (!worldIn.isRemote && this.potionId != null && worldIn.rand.nextFloat() < this.potionEffectProbability) {
         player.addPotionEffect(new PotionEffect(this.potionId));
      }

   }

   /**
    * How long it takes to use or consume an item
    */
   public int getUseDuration(ItemStack stack) {
      return this.fastEating ? 16 : 32;
   }

   /**
    * returns the action that specifies what animation to play when the items is being used
    */
   public EnumAction getUseAction(ItemStack stack) {
      return EnumAction.EAT;
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      if (playerIn.canEat(this.alwaysEdible)) {
         playerIn.setActiveHand(handIn);
         return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
      } else {
         return new ActionResult<>(EnumActionResult.FAIL, itemstack);
      }
   }

   public int getHealAmount(ItemStack stack) {
      return this.healAmount;
   }

   public float getSaturationModifier(ItemStack stack) {
      return this.saturationModifier;
   }

   /**
    * Whether wolves like this food (true for raw and cooked porkchop).
    */
   public boolean isMeat() {
      return this.meat;
   }

   public ItemFood setPotionEffect(PotionEffect effect, float probability) {
      this.potionId = effect;
      this.potionEffectProbability = probability;
      return this;
   }

   /**
    * Set the field 'alwaysEdible' to true, and make the food edible even if the player don't need to eat.
    */
   public ItemFood setAlwaysEdible() {
      this.alwaysEdible = true;
      return this;
   }

   public ItemFood setFastEating() {
      this.fastEating = true;
      return this;
   }
}